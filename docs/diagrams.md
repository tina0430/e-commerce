# 1. 포인트 관리

---
## 1.1. 포인트 조회
### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Controller as 포인트<br>컨트롤러
    participant Service as 포인트<br>서비스

    User ->> Controller: 포인트 조회 요청
    activate Controller
    Controller ->> Service: 포인트 정보 조회
    activate Service
    Service -->> Controller: 포인트 정보
    deactivate Service
    Controller ->> User: 포인트 정보 응답
    deactivate Controller
```

## 1.2. 포인트 충전
### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Controller as 포인트<br>컨트롤러
    participant Service as 포인트<br>서비스

    User ->> Controller: 포인트 충전 요청
    activate Controller
    Controller ->> Service: 포인트 충전 요청
    Service ->> Service: 포인트 잔액 확인
    activate Service
    alt 최대 충전 가능 금액 초과
        Service -->> Controller: 충전 실패 (최대 충전 가능 금액 초과)
        Controller ->> User: 실패(최대 충전 가능 금액 초과) 응답
    else 충전 가능
        Service ->> Service: 잔액 증가
        Service ->> Service: 충전 내역 저장
        Service -->> Controller: 충전 후 포인트 정보
        Controller ->> User: 포인트 정보 응답
    end
    deactivate Service
    deactivate Controller
```

# 2. 상품 주문/결제

---
## 2.1. 상품 주문
### 플로우 차트
```mermaid
stateDiagram-v2
direction LR
[*] --> 재고확인 : 주문 요청 도착

    재고확인 --> 재고부족 : 재고 부
    재고부족 --> [*] : 주문 실패 응답

    재고확인 --> 쿠폰검증 : 전체 품목 재고 충분
    쿠폰검증 --> 쿠폰무효 : 쿠폰 유효성 검사 실패
    쿠폰무효 --> 재고복원 : 보상 트랜잭션
    재고복원 --> [*] : 주문 실패 응답

    쿠폰검증 --> 주문완료 : 쿠폰 유효성 통과
    주문완료 --> [*] : 주문 성공 응답
```
### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Controller as 주문 컨트롤러
    participant OrderService as 주문 서비스
    participant CouponService as 쿠폰 서비스
    participant ProductService as 상품 서비스

    User ->> Controller: 1. 주문 요청
    Controller ->> OrderService: 2. 주문 처리 요청

    %% 쿠폰 유효성 확인만 수행
    OrderService ->> CouponService: 3. 쿠폰 유효성 확인 요청
    activate CouponService
    alt 쿠폰 유효하지 않음
        CouponService -->> OrderService: 4. 쿠폰 사용 불가
        OrderService -->> Controller: 5. 주문 실패 (쿠폰 오류)
        Controller ->> User: 6. 주문 실패 (쿠폰 오류) 응답
    else 쿠폰 유효함
        CouponService -->> OrderService: 4. 쿠폰 사용 가능
        deactivate CouponService

        %% 재고 확인
        OrderService ->> ProductService: 5. 재고 차감 요청
        activate ProductService
        alt 재고 부족
            ProductService -->> OrderService: 6. 재고 부족
            OrderService -->> Controller: 7. 주문 실패 (재고 부족)
            Controller ->> User: 8. 주문 실패 (재고 부족) 응답
        else 재고 충분
            ProductService -->> OrderService: 6. 재고 차감 성공
            deactivate ProductService

            %% 쿠폰 상태 변경
            OrderService ->> CouponService: 7. 쿠폰 상태 변경 (사용 완료)
            activate CouponService
            CouponService -->> OrderService: 8. 쿠폰 상태 변경 완료
            deactivate CouponService

            %% 주문 정보 저장
            OrderService ->> OrderService: 9. 주문 정보 저장
            OrderService -->> Controller: 10. 주문 성공
            Controller ->> User: 11. 주문 성공 응답
        end
    end
```

## 2.2. 상품 주문 결제
### 플로우 차트
```mermaid
stateDiagram-v2
    direction LR
    [*] --> 주문확인 : 결제 요청 도착

    주문확인 --> 잔액확인 : 포인트 잔액 확인
    잔액확인 --> 잔액부족 : 포인트 부족
    잔액부족 --> 쿠폰복원 : 보상 트랜잭션
    쿠폰복원 --> 재고복원 : 보상 트랜잭션
    재고복원 --> [*] : 주문 실패 응답

    잔액확인 --> 잔액충분 : 포인트 충분
    잔액충분 --> 포인트차감 : 포인트 차감 성공
    포인트차감 --> 결제완료 : 결제 성공 처리
    결제완료 --> [*] : 주문 성공 응답
```
### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Controller as 결제<br>컨트롤러
    participant PaymentService as 결제<br>서비스
    participant PointService as 포인트<br>서비스
    participant OrderService as 주문<br>서비스
    participant CouponService as 쿠폰<br>서비스
    participant ProductService as 상품<br>서비스
    User ->> Controller: 1. 결제 요청
    Controller ->> PaymentService: 2. 결제 처리 요청
    PaymentService ->> PointService: 3. 포인트 차감 요청
    PointService ->> PointService: 4. 포인트 잔액 확인
    alt 포인트 부족
        PointService -->> PaymentService: 5. 잔액 부족 오류
        PaymentService ->> OrderService: 6. 주문 실패 처리 요청
        OrderService ->> OrderService: 7. 주문 상태 변경<br>(결제 대기 -> 주문 취소)
        OrderService ->> CouponService: 8. 쿠폰 상태 복원 요청
        OrderService ->> ProductService: 9. 재고 복원 요청
        PaymentService -->> Controller: 10. 결제 실패(잔액 부족)
        Controller ->> User: 11. 결제 실패(잔액 부족) 응답
    else 포인트 충분
        PointService -->> PaymentService: 5. 포인트 차감 완료
        PaymentService ->> PaymentService: 6. 결제 내역 저장
        PaymentService ->> OrderService: 7. 주문 상태 변경 요청
        OrderService ->> OrderService: 8. 주문 상태 변경<br>(결제 대기 -> 결제 완료)
        OrderService -->> PaymentService: 9. 상태 변경 완료
        PaymentService -->> Controller: 10. 결제 완료
        Controller ->> User: 11. 결제 완료 응답
    end
```
### 주문 상태 다이어그램
```mermaid
stateDiagram-v2
    direction LR
    [*] --> 결제대기 : 주문 완료
    결제대기 --> 결제완료 : 결제 성공
    결제대기 --> 주문취소 : 결제 실패
    결제대기 --> 주문취소 : 사용자 취소
    결제완료 --> 주문취소 : 관리자 취소
    결제완료 --> 주문완료 : 상품 수령 확인
```

## 2.3. 주문 데이터 전송
```mermaid
sequenceDiagram
    participant PaymentService as 결제 서비스
    participant ExternalAdapter as 외부 전송 어댑터
    participant DataPlatform as 외부 데이터 플랫폼

    PaymentService ->> ExternalAdapter: 결제 정보 전달
    activate ExternalAdapter
    ExternalAdapter ->> DataPlatform: 결제 정보 전송
    alt 전송 성공(2xx)
        DataPlatform -->> ExternalAdapter: 성공 응답
        ExternalAdapter ->> ExternalAdapter: 성공 로그 기록
    else 전송 실패(4xx/5xx)
        DataPlatform -->> ExternalAdapter: 오류 응답
        ExternalAdapter ->> ExternalAdapter: 실패 로그 기록
        ExternalAdapter ->> ExternalAdapter: 재시도 큐 적재
		    deactivate ExternalAdapter
    end
```

# 3. 쿠폰 관리

---
## 3.1. 쿠폰 정책 발행
### 상태 다이어그램
```mermaid
stateDiagram-v2
    direction LR
    [*] --> 발행대기 : 쿠폰 정책 등록
    발행대기 --> 발행중 : 발행 시작 시간 도달
    발행중 --> 발행종료 : 발행 종료 시간 도달
    발행대기 --> 발행종료 : 관리자가 수동 종료
    발행중 --> 발행종료 : 발급 수량 소진
    발행중 --> 발행종료 : 관리자가 수동 종료
    발행종료 --> [*]
```
## 3.2. 사용자 쿠폰 발급
### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Controller as 쿠폰 컨트롤러
    participant Service as 쿠폰 서비스
    
    User ->> Controller: 1. 쿠폰 조회 요청
    activate Controller
    Controller ->> Service: 1.2. 발급 가능 쿠폰 조회 요청
    activate Service
    Service -->> Controller: 1.3. 발급 가능 쿠폰 
    deactivate Service
    Controller ->> User: 1.4. 발급 가능 쿠폰 응답
    deactivate Controller
    User ->> Controller: 2.1. 쿠폰 발급 요청
    activate Controller
    Controller ->> Service: 2.2. 쿠폰 발급 요청
    activate Service
    alt 잔여 수량이 없는 경우
        Service -->> Controller: 2.3. 잔여 수량 없음
        Controller ->> User: 2.4. 발급 실패(잔여 수량 없음) 응답
    else 잔여 수량이 있는 경우
        Service -->> Controller: 2.3. 잔여 수량 있음
        Service ->> Service: 2.4. 쿠폰 발급 기록 생성
        Service ->> Service: 2.5. 쿠폰 잔여 수량 차감
        deactivate Service
        Controller ->> User: 2.6. 발급 완료 응답
        deactivate Controller
    end
```
### 사용자 쿠폰 상태 다이어그램
```mermaid
stateDiagram-v2
    direction LR
    [*] --> 사용가능 : 쿠폰 발급원
    사용완료 --> 사용가능 : 상태 복구
    사용가능 --> 사용완료 : 결제 시 쿠폰 사용
    사용가능 --> 기간만료 : 유효기간 경과
    사용완료 --> [*]
    기간만료 --> [*]
    사용가능 --> [*]
```
# 4. 상품 조회

---
## 4.1. 판매 우수 상품 목록 조회
### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    actor User as 사용자
    participant Controller as 상품<br>컨트롤러
    participant ProductService as 상품<br>서비스

    User ->> Controller: 1. 판매 우수 상품 리스트 조회 요청
    Controller ->> ProductService: 2. 판매 우수 상품 리스트 조회 요청
    ProductService ->> ProductService: 3. 판매 우수 상품<br>리스트 확인
    ProductService -->> Controller: 4. 판매 우수 상품 리스트 응답
    Controller -->> User: 5. 판매 우수 상품 리스트 응답
```