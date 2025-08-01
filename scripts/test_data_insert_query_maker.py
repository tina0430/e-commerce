import random
from datetime import datetime, timedelta
import pandas as pd

def now():
    return datetime.now().strftime('%Y-%m-%d %H:%M:%S')

def date_range(start, end):
    delta = end - start
    return start + timedelta(days=random.randint(0, delta.days))

def random_datetime(start: datetime, end: datetime) -> datetime:
    """start ~ end 사이의 랜덤한 datetime 반환"""
    delta_seconds = int((end - start).total_seconds())
    random_offset = random.randint(0, delta_seconds)
    return start + timedelta(seconds=random_offset)

def bulk_insert_sql(table_name, columns, rows):
    values = []
    for row in rows:
        val = []
        for item in row:
            if isinstance(item, str):
                val.append(f"'{item}'")
            elif item is None:
                val.append("NULL")
            else:
                val.append(str(item))
        values.append(f"({', '.join(val)})")
    col_str = ", ".join(columns)
    values_str = ",\n".join(values)
    return f"INSERT INTO {table_name} ({col_str}) VALUES\n{values_str};\n"

def save_sql_to_file(sql_content, filename):
    """SQL 내용을 파일로 저장"""
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(sql_content)
    print(f"SQL 파일이 생성되었습니다: {filename}")

def generate_all_sql_files(user_count=1000, product_count=100, 
                          max_option_count=5, policy_count=50, max_coupon_count_per_user=10, 
                          order_count=500):
    """모든 테이블의 INSERT SQL을 생성하고 파일로 저장"""
    
    # 파일명에 타임스탬프 추가
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    
    print("=== 데이터 생성 시작 ===")
    
    # 1. USER
    print("USER 데이터 생성 중...")
    users = generate_users(user_count)
    user_sql = generate_users_insert_sql(users)
    save_sql_to_file(user_sql, f"V2__insert_user.sql")
    
    # 2. PRODUCT
    print("PRODUCT 데이터 생성 중...")
    products = generate_products(product_count)
    product_sql = generate_products_insert_sql(products)
    save_sql_to_file(product_sql, f"V3_insert_product.sql")
    
    # 3. PRODUCT_OPTION
    print("PRODUCT_OPTION 데이터 생성 중...")
    product_options = generate_product_options(products, max_option_count)
    product_option_sql = generate_product_options_insert_sql(product_options)
    save_sql_to_file(product_option_sql, f"V4__insert_product_option.sql")
    
    # 4. COUPON_POLICY
    print("COUPON_POLICY 데이터 생성 중...")
    coupon_policies = generate_coupon_policy(policy_count)
    coupon_policy_sql = generate_coupon_policy_insert_sql(coupon_policies)
    save_sql_to_file(coupon_policy_sql, f"V5__insert_coupon_policy.sql")
    
    # 5. USER_COUPON
    print("USER_COUPON 데이터 생성 중...")
    user_coupons = generate_user_coupons(users, coupon_policies, max_coupon_count_per_user)
    user_coupon_sql = generate_user_coupons_insert_sql(user_coupons)
    save_sql_to_file(user_coupon_sql, f"V6__insert_user_coupon.sql")
    
    # 6. ORDER (최적화된 버전)
    print("ORDER 데이터 생성 중...")
    orders, order_items = generate_orders_optimized(users, user_coupons, product_options, order_count)
    order_sql = generate_orders_insert_sql(orders)
    save_sql_to_file(order_sql, f"V7__insert_order.sql")
    
    # 7. ORDER_ITEM
    print("ORDER_ITEM 데이터 생성 중...")
    order_item_sql = generate_order_items_insert_sql(order_items)
    save_sql_to_file(order_item_sql, f"V8__insert_order_item.sql")
    
    # 8. PAYMENT
    print("PAYMENT 데이터 생성 중...")
    payments = generate_payments(orders)
    payment_sql = generate_payments_insert_sql(payments)
    save_sql_to_file(payment_sql, f"V9__insert_payment.sql")

    # 9. POINT_TRANSACTION
    print("POINT_TRANSACTION 데이터 생성 중...")
    point_transactions = generate_point_transactions(users, orders)
    point_transaction_sql = generate_point_transactions_insert_sql(point_transactions)
    save_sql_to_file(point_transaction_sql, f"V10__insert_point_transaction.sql")
    
    print("\n=== 데이터 생성 완료 ===")
    print(f"생성된 파일들:")
    print(f"- V2__insert_user.sql")
    print(f"- V3__insert_product.sql")
    print(f"- V4__insert_product_option.sql")
    print(f"- V5__insert_coupon_policy.sql")
    print(f"- V6__insert_user_coupon.sql")
    print(f"- V7__insert_order.sql")
    print(f"- V8__insert_order_item.sql")
    print(f"- V9__insert_payment.sql")
    print(f"- V10__insert_point_transaction.sql")

# -------------------------------
# 1. USER
# -------------------------------
def generate_dates(count, start_year=2020, end_year=2025):
    """시간이 지날수록 수가 늘어나는 일시 리스트 생성"""
    start_date = datetime(start_year, 1, 1)
    total_months = (end_year - start_year + 1) * 12

    # 증가하는 월별 가중치
    weights = [i + 1 for i in range(total_months)]
    total_weight = sum(weights)

    # 각 월별 가입자 수 결정
    monthly_counts = [round(count * (w / total_weight)) for w in weights]

    # 총합 보정
    diff = count - sum(monthly_counts)
    for i in range(abs(diff)):
        monthly_counts[i % total_months] += (1 if diff > 0 else -1)

    # 날짜 생성
    dates = []
    for idx, count in enumerate(monthly_counts):
        month_start = (start_date + pd.DateOffset(months=idx)).to_pydatetime()
        month_end = (month_start + pd.DateOffset(months=1) - pd.DateOffset(days=1)).to_pydatetime()
        for _ in range(count):
            dates.append(random_datetime(month_start, month_end))
    return sorted(dates) # 오름차순 정렬

def generate_users(user_count):
    users = {}
    join_dates = generate_dates(user_count)
    for i, join_date in enumerate(join_dates):
        name = f"user_{i+1}"
        users[i+1] = {
            "user_id": i+1,
            "user_name": name,
            "current_balance": 0,
            "created_at": join_date
        }
    return users

def generate_users_insert_sql(users):
    rows = []
    for user in users.values():
        i = user["user_id"]
        name = f"user_{i}"
        created_at = date_range(datetime(2023, 1, 1), datetime(2024, 12, 31))
        rows.append((
            i, name, 0, created_at.strftime('%Y-%m-%d %H:%M:%S'), created_at.strftime('%Y-%m-%d %H:%M:%S')
        ))
    return bulk_insert_sql(
        "tb_user",
        ["user_id", "user_name", "current_balance", "created_at", "updated_at"],
        rows
    )

# -------------------------------
# 2. PRODUCT
# -------------------------------
def generate_products(product_count):
    products = {}
    reg_dates = generate_dates(product_count)
    for i, reg_date in enumerate(reg_dates):
        name = f"product_{i+1}"
        products[i+1] = {
            "product_id": i+1,
            "product_name": name,
            "created_at": reg_date
        }
    return products

def generate_products_insert_sql(products):
    rows = []
    for product in products.values():
        rows.append((product["product_id"], product["product_name"], product["created_at"].strftime('%Y-%m-%d %H:%M:%S')))
    return bulk_insert_sql(
        "tb_product",
        ["product_id", "product_name", "created_at"],
        rows
    )

# -------------------------------
# 3. PRODUCT_OPTION
# -------------------------------
def generate_product_options(products, max_option_count):
    product_options = {}
    option_id = 1
    for product_id in products.keys():
        option_count = random.randint(2, max_option_count)
        for i in range(1, option_count + 1):
            option_name = f"option_{product_id}_{i+1}"
            qty = random.randint(0, 500)
            price = random.randint(1000, 100000)
            product_options[option_id] = {
                "product_option_id": option_id,
                "product_id": product_id,
                "product_option_name": option_name,
                "quantity": qty,
                "price": price,
                "updated_at": now()
            }
            option_id += 1
    return product_options

def generate_product_options_insert_sql(product_options):
    rows = []
    for product_option in product_options.values():
        rows.append((product_option["product_option_id"], product_option["product_id"], product_option["product_option_name"], product_option["quantity"], product_option["price"], now()))
    return bulk_insert_sql(
        "tb_product_option",
        ["product_option_id", "product_id", "product_option_name", "quantity", "price", "updated_at"],
        rows
    )

# -------------------------------
# 4. COUPON_POLICY
# -------------------------------
def generate_coupon_policy(policy_count):
    coupon_policies = {}
    status_choices = ["PENDING"] * 10 + ["ACTIVE"] * 70 + ["ENDED"] * 20
    discount_type_choices = ["RATE", "AMOUNT"]
    create_dates = generate_dates(policy_count)

    for i, create_date in enumerate(create_dates):
        discount_type = random.choice(discount_type_choices)
        if discount_type == "RATE":
            discount_value = random.choice([5, 10, 15, 30, 50])
            max_discount = random.choice([5000, 10000, 20000])
        else:
            discount_value = random.choice([1000, 2000, 5000])
            max_discount = discount_value

        min_order = random.choice([5000, 10000, 20000])
        valid_days = random.choice([3, 7, 14, 30])

        issue_start = create_date
        issue_end = issue_start + timedelta(days=random.choice([3, 7, 14, 30]))
        coupon_status = random.choice(status_choices)

        coupon_policies[i] = {
            "coupon_policy_id": i+1,
            "coupon_name": f"coupon_{i+1}",
            "discount_type": discount_type,
            "discount_value": discount_value,
            "max_discount_amount": max_discount,
            "min_order_amount": min_order,
            "issue_start_at": issue_start,
            "issue_end_at": issue_end,
            "total_quantity": 1000000,
            "remaining_quantity": 1000000,
            "valid_duration_days": valid_days,
            "coupon_status": coupon_status,
            "created_at": create_date,
            "updated_at": create_date
        }
    return coupon_policies

def generate_coupon_policy_insert_sql(coupon_policies):
    rows = []
    for coupon_policy in coupon_policies.values():
        rows.append((coupon_policy["coupon_policy_id"], coupon_policy["coupon_name"], coupon_policy["discount_type"], coupon_policy["discount_value"], coupon_policy["max_discount_amount"], coupon_policy["min_order_amount"], coupon_policy["issue_start_at"].strftime('%Y-%m-%d %H:%M:%S'), coupon_policy["issue_end_at"].strftime('%Y-%m-%d %H:%M:%S'), coupon_policy["total_quantity"], coupon_policy["remaining_quantity"], coupon_policy["valid_duration_days"], coupon_policy["coupon_status"], now(), now()))
    return bulk_insert_sql(
        "tb_coupon_policy",
        ["coupon_policy_id", "coupon_name", "discount_type", "discount_value", "max_discount_amount", "min_order_amount", "issue_start_at", "issue_end_at", "total_quantity", "remaining_quantity", "valid_duration_days", "coupon_status", "created_at", "updated_at"],
        rows
    )

# -------------------------------
# 5. USER_COUPON
# -------------------------------
def generate_user_coupons(users, coupon_policies, max_per_user):
    statuses = ["AVAILABLE"] * 7 + ["USED"] * 2 + ["EXPIRED"] * 1
    available_policies = {i: coupon_policy for i, coupon_policy in coupon_policies.items() if coupon_policy["coupon_status"] == "ACTIVE"}
    user_coupons = {}
    coupon_id = 1
    
    for user_id in users.keys():
        coupon_count = random.randint(0, max_per_user)
        for _ in range(coupon_count):
            if available_policies:
                policy_id = random.choice(list(available_policies.keys()))
                policy = available_policies[policy_id]
                status = random.choice(statuses)
                start_at = datetime.now() - timedelta(days=random.randint(0, 10))
                end_at = start_at + timedelta(days=random.randint(5, 10))
                
                user_coupons[coupon_id] = {
                    "user_coupon_id": coupon_id,
                    "coupon_policy_id": policy_id,
                    "user_id": user_id,
                    "coupon_name": f"user_coupon_{coupon_id}",
                    "discount_type": policy.get('discount_type'),
                    "discount_value": policy.get('discount_value'),
                    "max_discount_amount": policy.get('max_discount_amount'),
                    "min_order_amount": policy.get('min_order_amount'),
                    "usage_status": status,
                    "start_at": start_at,
                    "end_at": end_at,
                    "created_at": now(),
                }
                coupon_id += 1
    
    return user_coupons

def generate_user_coupons_insert_sql(user_coupons):
    rows = []
    for user_coupon in user_coupons.values():
        rows.append((user_coupon["user_coupon_id"], user_coupon["coupon_policy_id"], user_coupon["user_id"], user_coupon["coupon_name"], user_coupon["discount_type"], user_coupon["discount_value"], user_coupon["max_discount_amount"], user_coupon["min_order_amount"], user_coupon["usage_status"], user_coupon["start_at"].strftime('%Y-%m-%d %H:%M:%S'), user_coupon["end_at"].strftime('%Y-%m-%d %H:%M:%S'), now(), now()))
    return bulk_insert_sql(
        "tb_user_coupon",
        ["user_coupon_id", "coupon_policy_id", "user_id", "coupon_name", "discount_type", "discount_value", "max_discount_amount", "min_order_amount", "usage_status", "start_at", "end_at", "created_at", "updated_at"],
        rows
    )

# -------------------------------
# 6. ORDER (최적화된 버전)
# -------------------------------
def generate_orders_optimized(users, user_coupons, product_options, order_count):
    """성능 최적화된 주문 생성 함수"""
    orders = {}
    order_items = {}
    statuses = ["PENDING"] * 1 + ["CONFIRMED"] * 8 + ["CANCELLED"] * 1
    order_dates = generate_dates(order_count)
    
    # 사용 가능한 쿠폰 미리 필터링
    available_user_coupons = {
        user_coupon_id: user_coupon 
        for user_coupon_id, user_coupon in user_coupons.items() 
        if user_coupon['usage_status'] == 'AVAILABLE'
    }
    
    # 상품 옵션 ID 리스트 미리 생성
    product_option_ids = list(product_options.keys())
    
    for order_id, order_date in enumerate(order_dates):
        order_id = order_id + 1
        user_id = random.choice(list(users.keys()))
        status = random.choice(statuses)
        
        # 쿠폰 사용 여부 결정 (간소화)
        user_coupon_id = None
        user_coupon = None
        if available_user_coupons and random.random() < 0.3:  # 30% 확률
            user_coupon_id = random.choice(list(available_user_coupons.keys()))
            user_coupon = available_user_coupons[user_coupon_id]
        
        # 주문 아이템 생성 (간소화)
        order_item_count = random.randint(1, 3)
        total_amount = 0
        order_item_id = order_id * 1000 + 1
        
        # 주문 아이템 생성
        for _ in range(order_item_count):
            product_option_id = random.choice(product_option_ids)
            quantity = random.randint(1, 5)
            unit_price = product_options[product_option_id]['price']
            item_total = unit_price * quantity
            
            order_items[order_item_id] = {
                "order_item_id": order_item_id,
                "order_id": order_id,
                "product_id": product_options[product_option_id]['product_id'],
                "product_option_id": product_option_id,
                "quantity": quantity,
                "unit_price": unit_price,
                "discount_amount": 0,
                "total_amount": item_total,
                "final_amount": item_total,
                "created_at": order_date,
                "updated_at": order_date
            }
            order_item_id += 1
            total_amount += item_total
        
        # 할인 계산 (간소화)
        discount_amount = 0
        if user_coupon and total_amount >= user_coupon['min_order_amount']:
            if user_coupon['discount_type'] == 'RATE':
                discount_calc = total_amount * user_coupon['discount_value'] / 100
                max_discount = user_coupon['max_discount_amount'] or float('inf')
                discount_amount = min(discount_calc, max_discount)
            else:  # AMOUNT
                discount_amount = min(user_coupon['discount_value'], total_amount)
            
            # 쿠폰 사용 처리
            if status != "CANCELLED":
                user_coupon['usage_status'] = "USED"
                del available_user_coupons[user_coupon_id]
        
        final_amount = total_amount - discount_amount
        
        orders[order_id] = {
            "order_id": order_id,
            "user_id": user_id,
            "user_coupon_id": user_coupon_id,
            "total_amount": total_amount,
            "discount_amount": discount_amount,
            "final_amount": final_amount,
            "order_status": status,
            "created_at": order_date,
            "updated_at": order_date
        }
    
    return orders, order_items

def generate_orders_insert_sql(orders):
    rows = []
    for order in orders.values():
        rows.append((order["order_id"], order["user_id"], order["user_coupon_id"], order["total_amount"], order["discount_amount"], order["final_amount"], order["order_status"], order["created_at"].strftime('%Y-%m-%d %H:%M:%S'), order["updated_at"].strftime('%Y-%m-%d %H:%M:%S')))
    return bulk_insert_sql(
        "tb_order",
        ["order_id", "user_id", "user_coupon_id", "total_amount", "discount_amount",
         "final_amount", "order_status", "created_at", "updated_at"],
        rows
    )

# -------------------------------
# 7. ORDER_ITEM
# -------------------------------
def generate_order_items_insert_sql(order_items):
    rows = []
    for order_item in order_items.values():
        rows.append((order_item["order_item_id"], order_item["order_id"], order_item["product_id"], order_item["product_option_id"], order_item["quantity"], order_item["unit_price"], order_item["discount_amount"], order_item["final_amount"]))
    return bulk_insert_sql(
        "tb_order_item",
        ["order_item_id", "order_id", "product_id", "product_option_id", "quantity", "unit_price", "discount_amount", "final_amount"],
        rows
    )

# -------------------------------
# 8. PAYMENT
# -------------------------------
def generate_payments(orders):
    payments = {}
    
    for order_id, order in orders.items():
        payment_id = order_id
        total_amount = order.get('total_amount')
        discount_amount = order.get('discount_amount')
        final_amount = order.get('final_amount')
        created_at = order.get('created_at')
        payment_status = 'FAILED' if order['order_status'] == 'CANCELLED' else 'SUCCESS'            
        payments[payment_id] = {
            "payment_id": payment_id,
            "order_id": order_id,
            "total_amount": total_amount,
            "discount_amount": discount_amount,
            "final_amount": final_amount,
            "payment_status": payment_status,
            "created_at": created_at,
            "updated_at": created_at
        }
    
    return payments

def generate_payments_insert_sql(payments):
    rows = []
    for payment in payments.values():
        rows.append((payment["payment_id"], payment["order_id"], payment["total_amount"], payment["discount_amount"], payment["final_amount"], payment["payment_status"], payment["created_at"].strftime('%Y-%m-%d %H:%M:%S'), payment["updated_at"].strftime('%Y-%m-%d %H:%M:%S')))
    return bulk_insert_sql(
        "tb_payment",
        ["payment_id", "order_id", "total_amount", "discount_amount", "final_amount", "payment_status", "created_at", "updated_at"],    
        rows
    )

# -------------------------------
# 9. POINT_TRANSACTION
# -------------------------------
def generate_point_transactions(users, orders):
    transaction_id = 1
    point_transactions = {}
    
    for order in orders.values():
        user_id = order.get('user_id')
        user = users.get(user_id)
        if not user:
            continue
            
        balance = user.get('current_balance', 0)
        final_amount = order.get('final_amount', 0)
        
        # 잔액이 부족하면 충전
        if balance < final_amount:
            charge_amount = max(final_amount * 2, 1000000)  # 충분한 금액 충전
            point_transactions[transaction_id] = {
                "transaction_id": transaction_id,
                "user_id": user_id,
                "transaction_type": "CHARGE",
                "amount": charge_amount,
                "balance": balance + charge_amount,
                "created_at": order.get('created_at') - timedelta(minutes=1)
            }
            transaction_id += 1
            balance += charge_amount
        
        # 포인트 사용
        point_transactions[transaction_id] = {
            "transaction_id": transaction_id,
            "user_id": user_id,
            "transaction_type": "USE",
            "amount": final_amount,
            "balance": balance - final_amount,
            "created_at": order.get('created_at')
        }
        transaction_id += 1
        
        # 사용자 잔액 업데이트
        user['current_balance'] = balance - final_amount
    
    return point_transactions

def generate_point_transactions_insert_sql(point_transactions):
    rows = []
    for point_transaction in point_transactions.values():
        rows.append((point_transaction["transaction_id"], point_transaction["user_id"], point_transaction["transaction_type"], point_transaction["amount"], point_transaction["balance"], point_transaction["created_at"].strftime('%Y-%m-%d %H:%M:%S')))
    return bulk_insert_sql(
        "tb_point_transaction",
        ["transaction_id", "user_id", "transaction_type", "amount", "balance", "created_at"],
        rows
    )

if __name__ == "__main__":
    # 기본 설정값 (테스트용으로 크기 축소)
    user_count = 10000
    product_count = 10000
    max_option_count = 5
    policy_count = 5000
    max_coupon_count_per_user = 10
    order_count = 100000

    # 모든 SQL 파일 생성
    generate_all_sql_files(
        user_count=user_count,
        product_count=product_count,
        max_option_count=max_option_count,
        policy_count=policy_count,
        max_coupon_count_per_user=max_coupon_count_per_user,
        order_count=order_count
    )