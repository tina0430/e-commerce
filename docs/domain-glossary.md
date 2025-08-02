# Glossary - Table & Column Naming Rules

## 테이블 명명 규칙

- 모든 테이블은 `tb_` 접두어를 붙인다.
- 예시:
    - `COUPON_POLICY` → `tb_coupon_policy`
    - `USER_COUPON` → `tb_user_coupon`

## 컬럼 명명 규칙

- snake_case를 따른다.
- 약어 대신 명확한 전체 단어를 사용한다.
- 정수형 컬럼은 `int`, `bigint` 등으로 명확히 타입 분리한다.
- 상태 값은 명확하게 구분되는 접미어를 붙인다.  
  예: `coupon_status`, `usage_status`, `payment_status`, `order_status`


## 도메인 모델에서 사용하는 핵심 개념 용
### 쿠폰 정책 (CouponPolicy)
- 유저에게 발급 가능한 쿠폰의 조건을 정의하는 정책
- 할인 방식(비율/정액), 사용 조건(최소 주문 금액), 유효기간 등을 포함

---

### 사용자 쿠폰 (UserCoupon)
- 특정 유저에게 발급된 쿠폰 인스턴스
- 쿠폰 정책 기반으로 생성되며, 사용 여부와 유효기간을 포함함

---

### 주문 (Order)
- 사용자가 상품을 구매하려는 행위
- 쿠폰 적용 가능, 상태(PENDING/CONFIRMED/CANCELLED) 보유

---

### 주문 항목 (OrderItem)
- 주문에 포함된 각 상품/옵션 단위
- 수량, 개별 가격, 할인 적용 금액 등 포함

---

### 결제 (Payment)
- 주문에 대한 실제 결제 처리 내역
- 상태와 결제 금액 정보를 포함

---

### 포인트 거래내역 (PointTransaction)
- 유저 포인트의 증감 내역
- 종류: 충전(CHARGE), 사용(USE)

---

### 포인트 잔액 (CurrentBalance)
- 유저가 현재 보유 중인 포인트 금액
- 포인트 거래에 따라 실시간으로 변동됨

---

### 상품/옵션 (Product/ProductOption)
- 판매 대상이 되는 아이템과 그 세부 선택지
- 예: 티셔츠(상품) - 블랙/XL(옵션)

### 금액 관련 용어

| 용어                | 설명 |
|-------------------|------|
| `total_amount`    | 주문 상품의 총 금액 (할인 전) |
| `discount_amount` | 쿠폰 등으로 인한 할인된 금액 |
| `final_amount`    | 실제 결제되는 최종 금액 = total_amount - discount_amount |
| `unit_price`      | 각 상품 옵션의 단가 |
| `final_amount`    | 각 주문 항목의 실 결제 금액 = unit_price × quantity - discount_amount |
| `amount` (포인트)    | 포인트 거래 단위 금액 |
| `balance` (포인트)   | 포인트 거래 후 잔액 |
| `current_balance` | 유저의 현재 보유 포인트 총액 (실시간 계산) |