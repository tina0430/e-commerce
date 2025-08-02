CREATE TABLE tb_coupon_policy (
           coupon_policy_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '쿠폰 정책 ID',
           coupon_name VARCHAR(100) NOT NULL COMMENT '쿠폰명',
           discount_type VARCHAR(20) NOT NULL COMMENT '할인율/할인금액',
           discount_value INT NOT NULL COMMENT '할인 값',
           max_discount_amount INT COMMENT '최대 할인 금액',
           min_order_amount INT COMMENT '최소 주문 금액',
           issue_start_at DATETIME NOT NULL COMMENT '발급 시작일시',
           issue_end_at DATETIME NOT NULL COMMENT '발급 종료일시',
           total_quantity INT NOT NULL COMMENT '발급 가능 수량',
           remaining_quantity INT NOT NULL COMMENT '발급 잔여 수량',
           valid_duration_days INT NOT NULL COMMENT '유효일수',
           coupon_status VARCHAR(20) NOT NULL COMMENT '쿠폰 정책 상태',
           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '발행 일시',
           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
);

CREATE TABLE tb_user (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '사용자 ID',
  user_name VARCHAR(50) NOT NULL COMMENT '이름',
  current_balance INT NOT NULL DEFAULT 0 COMMENT '잔액',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입 일시',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
);

CREATE TABLE tb_user_coupon (
         user_coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '사용자 쿠폰 ID',
         coupon_policy_id BIGINT NOT NULL COMMENT '쿠폰 정책 ID',
         user_id BIGINT NOT NULL COMMENT '사용자 ID',
         coupon_name VARCHAR(100) NOT NULL COMMENT '쿠폰명',
         discount_type VARCHAR(20) NOT NULL COMMENT '할인 타입(비율/금액)',
         discount_value INT NOT NULL COMMENT '할인 값',
         max_discount_amount INT COMMENT '최대 할인 금액',
         min_order_amount INT COMMENT '최소 주문 금액',
         usage_status VARCHAR(20) NOT NULL COMMENT '쿠폰 상태',
         start_at DATETIME NOT NULL COMMENT '사용 시작 일시',
         end_at DATETIME NOT NULL COMMENT '사용 만료 일시',
         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '발급 일시',
         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
);

CREATE TABLE tb_point_transaction (
               transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '포인트 ID',
               user_id BIGINT NOT NULL COMMENT '사용자 ID',
               transaction_type VARCHAR(20) NOT NULL COMMENT '거래 종류(충전/사용)',
               amount INT NOT NULL COMMENT '금액',
               balance INT NOT NULL COMMENT '잔액',
               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시'
);

CREATE TABLE tb_product (
     product_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '상품 ID',
     product_name VARCHAR(100) NOT NULL COMMENT '상품명',
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록 일시'
);

CREATE TABLE tb_product_option (
            product_option_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '상품 옵션 ID',
            product_id BIGINT NOT NULL COMMENT '상품 ID',
            product_option_name VARCHAR(100) NOT NULL COMMENT '옵션명',
            quantity INT NOT NULL COMMENT '수량',
            price INT NOT NULL COMMENT '판매 금액',
            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
);

CREATE TABLE tb_order (
     order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '주문 ID',
     user_id BIGINT NOT NULL COMMENT '사용자 ID',
     user_coupon_id BIGINT COMMENT '쿠폰 ID',
     total_amount INT NOT NULL COMMENT '주문 금액',
     discount_amount INT NOT NULL COMMENT '할인 금액',
     final_amount INT NOT NULL COMMENT '결제 금액',
     order_status VARCHAR(20) NOT NULL COMMENT '주문 상태',
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '주문 일시',
     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
);

CREATE TABLE tb_order_item (
        order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '주문 상품 옵션 ID',
        order_id BIGINT NOT NULL COMMENT '주문 ID',
        product_id BIGINT NOT NULL COMMENT '상품 ID',
        product_option_id BIGINT NOT NULL COMMENT '상품 옵션 ID',
        quantity INT NOT NULL COMMENT '수량',
        unit_price INT NOT NULL COMMENT '판매 금액',
        discount_amount INT NOT NULL COMMENT '할인 금액',
        final_amount INT NOT NULL COMMENT '결제 금액'
);

CREATE TABLE tb_payment (
     payment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '결제 ID',
     order_id BIGINT NOT NULL COMMENT '주문 ID',
     total_amount INT NOT NULL COMMENT '판매 금액',
     discount_amount INT NOT NULL COMMENT '할인 금액',
     final_amount INT NOT NULL COMMENT '결제 금액',
     payment_status VARCHAR(20) NOT NULL COMMENT '결제 상태(결제대기/성공/실패)',
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '결제 일시',
     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정 일시'
);
