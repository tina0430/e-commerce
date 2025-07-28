## E-Commerce Backend 설계 개요
- 실용적인 수준에서 DDD(Domain-Driven Design) 도입하였습니다.
- 헥사고날 아키텍처 미적용 사유
   - 본 프로젝트는 소규모(단일 서비스, 단일 팀, 1인 개발)환경을 기반으로 하며, 헥사고날 아키텍처(Port & Adapter 구조)는 복잡도 대비 실효성이 낮다고 판단하여 도입하지 않았습니다.

---
### 설계 기준
1. DDD & 클린 아키텍처의 도입<br>
   - 도메인 로직을 최대한 도메인 내부로 끌어들이고, 도메인이 스스로의 룰을 갖도록 유도<br>
   - Entity, Value Object, Domain Service를 명확히 구분<br>
   - 계층 간 책임과 의존 방향을 분리<br>
2. 작은 규모에 맞는 실용적 구조<br>
   - 작은 프로젝트 규모에 맞게 ApplicationService는 사용하지 않고 DomainService만 사용<br>
   - 제한적인 상황에서만 Facade도입: 3개 이상의 도메인을 오케스트레이션할 때에만 사용<br>
   - 무리한 추상화보다 가독성과 유지보수성을 우선<br>
   - 기능별로 패키징
      
   ```
   kr.hhplus.be.ecommerce
   ├── common
   ├── order
   │   ├── domain
   │   ├── infrastructure
   │   ├── presentation
   │   └── application
   ├── coupon
   ├── ...
   ```
4. 트랜잭션 및 도메인 변경 반영 전략<br>
   - @Transactional 부여 기준
      - 비즈니스 로직의 원자성 단위를 기준으로 부여<br>
      - DB 상태 변경이 일어나는 애플리케니션, 서비스 계층 메서드에만 부여<br>
      - 읽기 전용 작업에는 부여하지 않음 (불필요한 트랜잭션 회피)<br>
      - 상위 메서드에서 @Transactional이 부여되어 있고, 컨트롤러의 호출이 따로 없는 경우에는 중복 선언을 하지 않음<br>
   - 영속성 컨텍스트 유지 및 반영 전략
      - 도메인 객체의 상태 변경을 entity에 반영하는 책임은 PersistenceMapper의 applyToEntity 메서드가 담당<br>
      - 트랜잭션 범위 내 JPA의 dirty checking 을 활용하여 DB에 자동 반영되도록 구성
      - 한계점: applyToEntity()는 영속 상태(EntityManager에 관리되는)의 엔티티에만 호출되어야 하며, detached 상태의 엔티티에서는 반영이 누락될 수 있음
         - 작은 규모 + 1인 개발 환경에서는 applyToEntity() 호출 원칙을 안정적으로 유지할 수 있다고 판단
         - 향후 확장을 고려하여 PersistenceUpdater 등의 서브 컴포넌트를 통해 책임을 더 명확히 분리하는 방안도 검토 중
      ```java
      default void applyToEntity(Order domain, OrderEntity entity) {
              if (domain == null || entity == null) {
                  return;
              }
              entity.setStatus(domain.getStatus());
              entity.setTotalAmount(domain.getTotalAmount());
              entity.setUpdatedAt(domain.getUpdatedAt());
          }
      ```
5. 예외 처리 전략<br>
   - BusinessException, SystemException 구분<br>
      - 비즈니스 조건 위반 → BusinessException<br>
      - 그외의 예외적 상황 → SystemException<br>
   - Controller 레이어의 예외 처리 전략<br>
      - 예외 발생 지점<br>
         - DTO 바인딩 실패<br>
         - PathVariable/RequestParam 변환 실패<br>
         - 비즈니스 로직에서 전달된 예외<br>
      - 처리 방식<br>
         - @ControllerAdvice + @ExceptionHandler<br>
         - SystemException 던짐<br>
         - 클라이언트에게 400, 403, 404, 500 등으로 명확하게 응답<br>
   - Application 레이어의 예외 처리 전략<br>
       - 예외 발생 지점<br>
         - 외부 시스템 연동 실패 (결제 API, 메시지 브로커, DB 등)<br>
         - 흐름 제어상 명확하게 나눠야 하는 예외<br>
         - 보상 트랜잭션 처리 중 실패<br>
      - 처리 방식<br>
         - BusinessException은 그대로 던짐 (컨트롤러로 전파)<br>
         - RuntimeException 등은 catch 후 → SystemException으로 래핑<br>
         - 흐름이 중요한 경우 로깅<br>
   - Domain 레이어의 예외 처리 전략<br>
      - 예외 발생 지점<br>
         - 정책 위반, 유효성 불충족, 상태 변경 불가능 등의 비즈니스 규칙 위반<br>
         - 도메인 엔티티 내부의 불변조건 위반<br>
      - 처리 방식<br>
        - BusinessException 던짐<br>
        - Exception 메시지는 구체적인 비즈니스 상황을 담도록 함<br>
        - BusinessError enum 을 활용해 일관성 유지<br>
   - Infrastructure 레이어의 예외 처리 전략<br>
     - 예외 발생 지점<br>
        - QueryDSL 등 외부 의존 처리 실패<br>
     - 처리 전략<br>
        - Facade 또는 Application 레이어에서 try-catch<br>
6. 계층간 모델 맵핑
   - Dto <-> Domain Object
      - DtoMapper 활용
   - Entity <-> Domain Object 
      - PersistenceMapper 활용

---

### 그 외 고민 포인트<br>
1. OrderCalculator는 도메인 객체인가 서비스인가?<br>
   - 결론: 서비스<br>
   - 근거: 도메인의 일부로 간주되나, 도메인 지식(계산 규칙)을 구현한 독립적인 로직임<br>
2. 결제 상태를 상세하게 관리할 필요가 있을까?<br>
   - 결론: 결제 성공 시 외부 데이터 전송 등 후속 작업이 있으므로, 상태값(PENDING, SUCCESS, FAILED)을 명확히 유지<br>
   - 근거: 처리를 위해 명시적 상태 전이는 필요함<br>
3. Entity 생성 시 new ArrayList<>(orderItems)처럼 깊은 복사를 해야 할까?<br>
   - 결론: 해야함<br>
   - 근거
      - 외부에서 전달한 List를 그대로 참조하면 사이드 이펙트 발생 가능<br>
      - 불변성 유지 또는 추후 도메인 로직에서 조작을 위한 캡슐화 가능(중요)<br>
4. 쿠폰 vs 쿠폰 정책을 나눠야 할까?<br>
   - 결론: 나누지 않음<br>
   - 근거: 쿠폰 정책이 큰 도메인이 아님 + 작은 규모의 프로젝트라서 굳이 나누지 않음<br>
5. 보상 트랜잭션 처리는 Facade의 역할일까, Service의 역할일까?<br>
   - 결론: Facade<br>
   - 근거
      - 보상 로직은 여러 도메인(coupon, order, product 등)에 걸쳐있어 단일 도메인 서비스에 책임을 두기에는 부적절함<br>
      - 도메인 간 조율 및 트랜잭션 흐름을 관리하는 Application 레이어에서 처리하는 것이 책임 분리 측면에서 더 적합<br>
6. 결제 상태 설계가 필요할까?<br>
   - 결론: 필요함<br>
   - 근거: 상태 기반 흐름 제어가 가능하고 장애 분석도 수월함<br>

---

#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행

```bash
docker-compose up -d
```
