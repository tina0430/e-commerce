## ERD

---
```mermaid
erDiagram
   tb_coupon_policy {
      bigint coupon_policy_id PK "쿠폰 정책 ID"
      string coupon_name "쿠폰명"
      string discount_type "할인율/할인금액"
      int discount_value "할인 값"
      int max_discount_amount "최대 할인 금액"
      int min_order_amount "최소 주문 금액"
      datetime issue_start_at "발급 시작일시"
      datetime issue_end_at "발급 종료일시"
      int total_quantity "발급 가능 수량"
      int remaining_quantity "발급 잔여 수량"
      int valid_duration_days "유효일수"
      string coupon_status "쿠폰 정책 상태(발급 가능/발급 완료)"
      datetime created_at "발행 일시"
      datetime updated_at "수정 일시"
   }

    tb_user_coupon {
        bigint user_coupon_id PK "사용자 쿠폰 ID"
        bigint coupon_policy_id FK "쿠폰 정책 ID"
        bigint user_id FK "사용자 ID"
        string coupon_name "쿠폰명"
        string discount_type "할인 타입(비율/금액)"
        int discount_value "할인 값"
        int max_discount_amount "최대 할인 금액"
        int min_order_amount "최소 주문 금액"
        string usage_status "쿠폰 상태"
        datetime start_at "사용 시작 일시"
        datetime end_at "사용 만료 일시"
        datetime created_at "발급 일시"
        datetime updated_at "수정 일시"
    }

    tb_user {
        bigint user_id PK "사용자 ID"
        string user_name "이름"
        int current_balance "잔액"
        datetime created_at "가입 일시"
        datetime updated_at "수정 일시"
    }

    tb_point_transaction {
        bigint transaction_id PK "포인트 ID"
        bigint user_id FK "사용자 ID"
        string transaction_type "거래 종류(충전/사용)"
        int amount "금액"
        int balance "잔액"
        datetime created_at "생성 일시"
    }

    tb_product {
        bigint product_id PK "상품 ID"
        string product_name "상품명"
        datetime created_at "등록 일시"
    }

    tb_product_option {
        bigint product_option_id PK "상품 옵션 ID"
        bigint product_id FK "상품 ID"
        string product_option_name "옵션명"
        int quantity "수량"
        int price "판매 금액"
        datetime updated_at "수정 일시"
    }

    tb_order {
        bigint order_id PK "주문 ID"
        bigint user_id FK "사용자 ID"
        bigint user_coupon_id FK "쿠폰 ID"
        int total_amount "주문 금액"
        int discount_amount "할인 금액"
        int final_amount "결제 금액"
        string order_status "주문 상태"
        datetime created_at "주문 일시"
        datetime updated_at "수정 일시"
    }

    tb_order_item {
        bigint order_item_id PK "주문 상품 옵션 ID"
        bigint order_id FK "주문 ID"
        bigint product_id FK "상품 ID"
        bigint product_option_id FK "상품 옵션 ID"
        int quantity "수량"
        int unit_price "판매 금액"
        int discount_amount "할인 금액"
        int final_amount "결제 금액"
    }

    tb_payment {
        bigint payment_id PK "결제 ID"
        bigint order_id FK "주문 ID"
        int total_amount "판매 금액"
        int discount_amount "할인 금액"
        int final_amount "결제 금액"
        string payment_status "결제 상태(성공/실패)"
        datetime created_at "결제 일시"
    }

    USER ||--o{ POINT_TRANSACTION : "포인트 보유"
    USER ||--o{ ORDER : "주문"
    USER ||--o{ USER_COUPON : "쿠폰 보유"
    PRODUCT ||--o{ PRODUCT_OPTION : "옵션 포함"
    ORDER ||--o{ ORDER_ITEM : "주문 상품 포함"
    PRODUCT ||--o{ ORDER_ITEM : "상품 포함"
    PRODUCT_OPTION ||--o{ ORDER_ITEM : "옵션 선택"
    COUPON_POLICY ||--o{ USER_COUPON : "발급"
    ORDER ||--|| USER_COUPON : "쿠폰 포힘"
    ORDER ||--o{ PAYMENT : "결제 포함"
```