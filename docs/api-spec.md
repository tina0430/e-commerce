# API Specification

## 1. Point API

### 1.1. 잔액 조회 (Get Point Balance)
- **Endpoint**: `GET /api/users/{userId}/point`
- **Description**: 특정 사용자의 현재 포인트 잔액을 조회합니다.
- **Parameters**:
    - `userId` (Path Variable, Long): 사용자 ID
- **Responses**:
    - `200 OK`:
        ```json
        {
            "userId": 1,
            "balance": 10000
        }
        ```

### 1.2. 잔액 충전 (Charge Point)
- **Endpoint**: `POST /api/users/{userId}/point/charge`
- **Description**: 특정 사용자의 포인트 잔액을 충전합니다.
- **Parameters**:
    - `userId` (Path Variable, Long): 사용자 ID
- **Request Body**:
    ```json
    {
        "userId": 1,
        "amount": 5000
    }
    ```
- **Responses**:
    - `200 OK`:
        ```json
        {
            "userId": 1,
            "balance": 15000
        }
        ```
    - `400 Bad Request`:
        ```json
        {
            "message": "최대 충전 가능 금액을 초과했습니다."
        }
        ```

### 1.3. 포인트 사용 (Use Point)
- **Endpoint**: `PATCH /api/users/{userId}/point/use`
- **Description**: 특정 사용자의 포인트를 사용합니다.
- **Parameters**:
    - `userId` (Path Variable, Long): 사용자 ID
- **Request Body**:
    ```json
    {
        "userId": 1,
        "amount": 2000
    }
    ```
- **Responses**:
    - `200 OK`:
        ```json
        {
            "userId": 1,
            "balance": 13000
        }
        ```

### 1.4. 포인트 내역 조회 (Get Point History)
- **Endpoint**: `GET /api/users/{userId}/point/history`
- **Description**: 특정 사용자의 포인트 충전 및 사용 내역을 조회합니다.
- **Parameters**:
    - `userId` (Path Variable, Long): 사용자 ID
- **Responses**:
    - `200 OK`:
        ```json
        [
            {
                "transactionId": 1,
                "userId": 1,
                "transactionType": "CHARGE",
                "amount": 5000,
                "balance": 15000,
                "createdAt": "2023-07-18T10:00:00"
            },
            {
                "transactionId": 2,
                "userId": 1,
                "transactionType": "USE",
                "amount": 2000,
                "balance": 13000,
                "createdAt": "2023-07-18T10:30:00"
            }
        ]
        ```

## 2. Coupon API

### 2.1. 쿠폰 정책 발행 (Create Coupon Policy)
- **Endpoint**: `POST /api/coupon-policies`
- **Description**: 관리자가 새로운 쿠폰 정책을 발행합니다.
- **Request Body**:
    ```json
    {
        "couponName": "신규 가입 축하 쿠폰",
        "discountType": "AMOUNT",
        "discountValue": 5000,
        "minOrderAmount": 10000,
        "minOrderPrice": 0,
        "issueStartAt": "2023-07-01T00:00:00",
        "issueEndAt": "2023-07-31T23:59:59",
        "totalQuantity": 1000,
        "validDurationDays": 30
    }
    ```
- **Responses**:
    - `200 OK`:
        ```json
        {
            "couponPolicyId": 1
        }
        ```

### 2.2. 발급 가능 쿠폰 조회 (Get Available Coupons)
- **Endpoint**: `GET /api/coupons/available`
- **Description**: 현재 발급 가능한 쿠폰 목록을 조회합니다.
- **Responses**:
    - `200 OK`:
        ```json
        [
            {
                "couponPolicyId": 1,
                "couponName": "신규 가입 축하 쿠폰",
                "discountType": "AMOUNT",
                "discountValue": 5000,
                "minOrderAmount": 10000,
                "minOrderPrice": 0,
                "issueEndAt": "2023-07-31T23:59:59"
            }
        ]
        ```

### 2.3. 쿠폰 발급 (Issue Coupon)
- **Endpoint**: `POST /api/coupons/issue`
- **Description**: 사용자에게 특정 쿠폰을 발급합니다.
- **Request Body**:
    ```json
    {
        "userId": 1,
        "couponPolicyId": 1
    }
    ```
- **Responses**:
    - `200 OK`:
        ```json
        {
            "userCouponId": 101
        }
        ```
    - `400 Bad Request`:
        ```json
        {
            "message": "잔여 수량이 부족합니다."
        }
        ```

### 2.4. 사용자 쿠폰 목록 조회 (Get User Coupons)
- **Endpoint**: `GET /api/users/{userId}/coupons`
- **Description**: 특정 사용자가 보유한 쿠폰 목록을 조회합니다.
- **Parameters**:
    - `userId` (Path Variable, Long): 사용자 ID
- **Responses**:
    - `200 OK`:
        ```json
        [
            {
                "couponId": 101,
                "userId": 1,
                "couponName": "신규 가입 축하 쿠폰",
                "discountType": "AMOUNT",
                "discountValue": 5000,
                "minOrderAmount": 10000,
                "minOrderPrice": 0,
                "issueEndAt": "2023-08-30T23:59:59"
            }
        ]
        ```

### 2.5. 쿠폰 사용 (Use Coupon)
- **Endpoint**: `PATCH /api/coupons/use`
- **Description**: 사용자가 보유한 쿠폰을 사용합니다.
- **Request Body**:
    ```json
    {
        "userId": 1,
        "couponId": 101
    }
    ```
- **Responses**:
    - `200 OK`:
        ```json
        {
            "userCouponId": 101
        }
        ```

## 3. Product API

### 3.1. 상품 목록 조회 (Get Products)
- **Endpoint**: `GET /api/products`
- **Description**: 모든 상품 목록을 조회합니다.
- **Responses**:
    - `200 OK`:
        ```json
        [
            {
                "productId": 1,
                "name": "상품 A",
                "options": [
                    {
                        "productOptionId": 101,
                        "productId": 1,
                        "name": "옵션 A-1",
                        "quantity": 100,
                        "price": 10000
                    }
                ]
            }
        ]
        ```

### 3.2. 단일 상품 조회 (Get Product)
- **Endpoint**: `GET /api/products/{productId}`
- **Description**: 특정 상품의 상세 정보를 조회합니다.
- **Parameters**:
    - `productId` (Path Variable, Long): 상품 ID
- **Responses**:
    - `200 OK`:
        ```json
        {
            "productId": 1,
            "name": "상품 A",
            "options": [
                {
                    "productOptionId": 101,
                    "productId": 1,
                    "name": "옵션 A-1",
                    "quantity": 100,
                    "price": 10000
                }
            ]
        }
        ```

### 3.3. 상위 판매 상품 조회 (Get Top Selling Products)
- **Endpoint**: `GET /api/products/top-selling`
- **Description**: 판매량이 가장 높은 상위 상품 목록을 조회합니다.
- **Responses**:
    - `200 OK`:
        ```json
        [
            {
                "productId": 1,
                "name": "상품 A",
                "options": [
                    {
                        "productOptionId": 101,
                        "productId": 1,
                        "name": "옵션 A-1",
                        "quantity": 100,
                        "price": 10000
                    }
                ]
            }
        ]
        ```

## 4. Order API

### 4.1. 상품 주문 (Order Products)
- **Endpoint**: `POST /api/users/{userId}/orders`
- **Description**: 사용자가 상품을 주문합니다.
- **Parameters**:
    - `userId` (Path Variable, Long): 사용자 ID
- **Request Body**:
    ```json
    {
        "userId": 1,
        "items": [
            {
                "productId": 1,
                "productOptionId": 101,
                "quantity": 1
            }
        ]
    }
    ```
- **Responses**:
    - `200 OK`:
        ```json
        {
            "orderId": 1,
            "totalAmount": 10000,
            "status": "PENDING",
            "createdAt": "2023-07-18T11:00:00"
        }
        ```
    - `400 Bad Request`:
        ```json
        {
            "message": "쿠폰이 유효하지 않습니다."
        }
        ```
    - `400 Bad Request`:
        ```json
        {
            "message": "재고가 부족합니다."
        }
        ```

### 4.2. 상품 주문 내역 조회 (Get Order History)
- **Endpoint**: `GET /api/users/{userId}/orders`
- **Description**: 특정 사용자의 주문 내역을 조회합니다.
- **Parameters**:
    - `userId` (Path Variable, Long): 사용자 ID
- **Responses**:
    - `200 OK`:
        ```json
        [
            {
                "orderId": 1,
                "totalAmount": 10000,
                "status": "COMPLETED",
                "items": [
                    {
                        "productId": 1,
                        "productName": "상품 A",
                        "productOptionId": 101,
                        "productOptionName": "옵션 A-1",
                        "quantity": 1,
                        "unitPrice": 10000,
                        "discountAmount": 0,
                        "finalPrice": 10000
                    }
                ],
                "createdAt": "2023-07-18T11:00:00"
            }
        ]
        ```
