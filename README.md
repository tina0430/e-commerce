## E-Commerce Backend 설계 개요
실용적인 수준에서 DDD(Domain-Driven Design)를 도입하였습니다.

---
### 설계 기준
1. DDD & 클린 아키텍처의 도입<br>
   도메인 로직을 최대한 도메인 내부로 끌어들이고, 도메인이 스스로의 룰을 갖도록 유도
   Entity, Value Object, Domain Service를 명확히 구분
   계층 간 책임과 의존 방향을 분리
2. 작은 규모에 맞는 실용적 구조<br>
   작은 프로젝트 규모에 맞게 ApplicationService는 사용하지 않고 DomainService만 사용
   Facade는 3개 이상의 도메인을 오케스트레이션할 때에만 사용
   무리한 추상화보다 가독성과 유지보수성을 우선
3. 트랜잭션 관리<br>
   DB 상태 변경이 일어나는 서비스 계층 메서드에만 @Transactional 부여
   비즈니스 로직의 원자성 단위를 기준으로 붙임
   읽기 전용 작업에는 부여하지 않음 (불필요한 트랜잭션 회피)

   | 위치                              | 사용 | 이유            |
   | ------------------------------- |----| ------------- |
   | `OrderService.createOrder()`    | ✅  | 주문 생성 및 저장 포함 |
   | `CouponService.useUserCoupon()` | ✅  | 상태 변경 포함      |
   | `getOrder(userId, orderId)`     | ❌  | 단순 조회만 수행     |

4. 예외 처리 전략<br>
   BusinessException과 SystemException을 구분
   - 비즈니스 조건 위반 → BusinessException
   - 그외의 예외적 상황 → SystemException
   1. Controller 레이어의 예외 처리 전략
      - 예외 발생 지점
         - DTO 바인딩 실패
         - PathVariable/RequestParam 변환 실패
         - 비즈니스 로직에서 전달된 예외
      - 처리 방식
         - @ControllerAdvice + @ExceptionHandler
         - 클라이언트에게 400, 403, 404, 500 등으로 명확하게 응답
   2. Application 레이어의 예외 처리 전략
       - 예외 발생 지점
         - 외부 시스템 연동 실패 (결제 API, 메시지 브로커, DB 등)
         - 흐름 제어상 명확하게 나눠야 하는 예외
         - 보상 트랜잭션 처리 중 실패
      - 처리 방식
         - BusinessException은 그대로 던짐 (컨트롤러로 전파)
         - RuntimeException 등은 catch 후 → SystemException으로 래핑
         - 흐름이 중요한 경우 로깅
   3. Domain 레이어의 예외 처리 전략
      - 예외 발생 지점
         - 정책 위반, 유효성 불충족, 상태 변경 불가능 등의 비즈니스 규칙 위반
         - 도메인 엔티티 내부의 불변조건 위반
      - 처리 방식
        - 반드시 BusinessException을 사용
        - Exception 메시지는 구체적인 비즈니스 상황을 담도록 함
        - BusinessError enum 을 활용해 일관성 유지
   4. Infrastructure 레이어의 예외 처리 전략
     - 예외 발생 지점
        - QueryDSL 등 외부 의존 처리 실패
     - 처리 전략
        - Facade 또는 Application 레이어에서 try-catch
5. 보상 트랜잭션 처리
   handlePaymentFailure() 내부에서 주문 취소, 재고 복구, 쿠폰 복원 등을 처리
   보상 로직 자체의 실패는 전파하지 않음 → 로그만 남김 + 알림 예정
6. 결제 상태 설계
   외부 결제 시스템과 연동 또는 로그 집계 등을 고려해 상태 이력을 명시적으로 관리

---

### 그 외 고민 포인트
1. OrderCalculator는 도메인 객체인가 서비스인가?<br>
   - 결정: 도메인 내부의 핵심 계산 로직이지만, 복잡도를 낮추기 위해 별도 클래스로 추출
   - 결정: 도메인의 일부로 간주되나, 
2. 결제 상태를 상세하게 관리할 필요가 있을까?<br>
   - 결정: 결제 성공 시 외부 데이터 전송 등 후속 작업이 있으므로, 상태값(PENDING, SUCCESS, FAILED)을 명확히 유지
   - 근거: 후속 처리를 위해 명시적 상태 전이는 필요함
3. Entity 생성 시 new ArrayList<>(orderItems)처럼 깊은 복사를 해야 할까?<br>
   - 결정: 인자로 받은 리스트를 그대로 들고 있지 않도록 안전하게 복사
   - 근거: 외부에서 전달한 List를 그대로 참조하면 사이드 이펙트 발생 가능
    불변성 유지 또는 추후 도메인 로직에서 조작을 위한 캡슐화 목적
---
#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```
