-- payment 테이블 데이터 (20개)
INSERT IGNORE INTO payment (reg_time, update_time, created_by, modified_by, amount, buyer_addr, buyer_email, buyer_name, buyer_postcode, buyer_tel, imp_uid, item_nm, order_status, paid_at, pay_status, payment_method, order_id) VALUES
('2025-02-20 12:28:16.492779', '2025-02-20 12:28:16.492779', 'test@test.com', 'test@test.com', 51800.00, '서울시 종로1가 1-1', 'test@portone.io', '홍길동', '123-123', NULL, 'imp_020075914011', '로얄 캐닌 미니 어덜트', 'PAYMENT_COMPLETED', '2025-02-20 12:28:16', 'PAYMENT', 'CREDIT_CARD', 3),
('2025-02-20 12:32:10.294512', '2025-02-20 12:32:10.294512', 'test@test.com', 'test@test.com', 23900.00, '서울시 강남구 2-2', 'test2@portone.io', '이순신', '123-456', NULL, 'imp_020075914012', '퓨리나 원 스몰브리드', 'PAYMENT_COMPLETED', '2025-02-20 12:32:10', 'PAYMENT', 'CREDIT_CARD', 4),
('2025-02-20 12:35:54.514367', '2025-02-20 12:35:54.514367', 'test@test.com', 'test@test.com', 31900.00, '서울시 마포구 3-3', 'test3@portone.io', '김유신', '123-789', NULL, 'imp_020075914013', '네추럴 밸런스 라지브리드', 'PAYMENT_COMPLETED', '2025-02-20 12:35:54', 'PAYMENT', 'CREDIT_CARD', 5),
('2025-02-20 12:39:48.324902', '2025-02-20 12:39:48.324902', 'test@test.com', 'test@test.com', 22900.00, '서울시 송파구 4-4', 'test4@portone.io', '박지원', '123-012', NULL, 'imp_020075914014', '오리젠 오리지널 독', 'PAYMENT_COMPLETED', '2025-02-20 12:39:48', 'PAYMENT', 'CREDIT_CARD', 6),
('2025-02-20 12:42:30.482349', '2025-02-20 12:42:30.482349', 'test@test.com', 'test@test.com', 37900.00, '서울시 중구 5-5', 'test5@portone.io', '최강훈', '123-345', NULL, 'imp_020075914015', '힐스 사이언스 다이어트', 'PAYMENT_COMPLETED', '2025-02-20 12:42:30', 'PAYMENT', 'CREDIT_CARD', 7),
('2025-02-20 12:46:16.751928', '2025-02-20 12:46:16.751928', 'test@test.com', 'test@test.com', 45900.00, '서울시 관악구 6-6', 'test6@portone.io', '강호동', '123-567', NULL, 'imp_020075914016', '웰니스 코어 리듀스드 패트', 'PAYMENT_COMPLETED', '2025-02-20 12:46:16', 'PAYMENT', 'CREDIT_CARD', 8),
('2025-02-20 12:50:45.037529', '2025-02-20 12:50:45.037529', 'test@test.com', 'test@test.com', 51900.00, '서울시 서초구 7-7', 'test7@portone.io', '홍석천', '123-678', NULL, 'imp_020075914017', '블루 버팔로 라이프 프로텍션', 'PAYMENT_COMPLETED', '2025-02-20 12:50:45', 'PAYMENT', 'CREDIT_CARD', 9),
('2025-02-20 12:54:31.492357', '2025-02-20 12:54:31.492357', 'test@test.com', 'test@test.com', 28900.00, '서울시 성북구 8-8', 'test8@portone.io', '이영희', '123-890', NULL, 'imp_020075914018', '나우 프레쉬 스몰브리드', 'PAYMENT_COMPLETED', '2025-02-20 12:54:31', 'PAYMENT', 'CREDIT_CARD', 10),
('2025-02-20 12:58:16.239034', '2025-02-20 12:58:16.239034', 'test@test.com', 'test@test.com', 30900.00, '서울시 동대문구 9-9', 'test9@portone.io', '박상현', '123-234', NULL, 'imp_020075914019', '홀리스틱 셀렉트', 'PAYMENT_COMPLETED', '2025-02-20 12:58:16', 'PAYMENT', 'CREDIT_CARD', 11),
('2025-02-20 13:02:23.154654', '2025-02-20 13:02:23.154654', 'test@test.com', 'test@test.com', 38900.00, '서울시 노원구 10-10', 'test10@portone.io', '이재훈', '123-456', NULL, 'imp_020075914020', '로얄 캐닌 라지브리드', 'PAYMENT_COMPLETED', '2025-02-20 13:02:23', 'PAYMENT', 'CREDIT_CARD', 12),
('2025-02-20 13:06:19.302748', '2025-02-20 13:06:19.302748', 'test@test.com', 'test@test.com', 29900.00, '서울시 용산구 11-11', 'test11@portone.io', '정민호', '123-567', NULL, 'imp_020075914021', '펫가든 내추럴', 'PAYMENT_COMPLETED', '2025-02-20 13:06:19', 'PAYMENT', 'CREDIT_CARD', 13),
('2025-02-20 13:10:05.792410', '2025-02-20 13:10:05.792410', 'test@test.com', 'test@test.com', 44900.00, '서울시 양천구 12-12', 'test12@portone.io', '김지은', '123-678', NULL, 'imp_020075914022', '아보덤 내추럴', 'PAYMENT_COMPLETED', '2025-02-20 13:10:05', 'PAYMENT', 'CREDIT_CARD', 14),
('2025-02-20 13:14:11.684526', '2025-02-20 13:14:11.684526', 'test@test.com', 'test@test.com', 37900.00, '서울시 강서구 13-13', 'test13@portone.io', '박준영', '123-789', NULL, 'imp_020075914023', '아카나 헤리티지 독', 'PAYMENT_COMPLETED', '2025-02-20 13:14:11', 'PAYMENT', 'CREDIT_CARD', 15),
('2025-02-20 13:18:17.214761', '2025-02-20 13:18:17.214761', 'test@test.com', 'test@test.com', 24900.00, '서울시 마포구 14-14', 'test14@portone.io', '조윤서', '123-890', NULL, 'imp_020075914024', '네이쳐스 로직', 'PAYMENT_COMPLETED', '2025-02-20 13:18:17', 'PAYMENT', 'CREDIT_CARD', 16),
('2025-02-20 13:22:29.084753', '2025-02-20 13:22:29.084753', 'test@test.com', 'test@test.com', 45900.00, '서울시 광진구 15-15', 'test15@portone.io', '한상훈', '123-234', NULL, 'imp_020075914025', '로얄 캐닌 미니 어덜트', 'PAYMENT_COMPLETED', '2025-02-20 13:22:29', 'PAYMENT', 'CREDIT_CARD', 17),
('2025-02-20 13:26:30.093680', '2025-02-20 13:26:30.093680', 'test@test.com', 'test@test.com', 37900.00, '서울시 서대문구 16-16', 'test16@portone.io', '최성진', '123-012', NULL, 'imp_020075914026', '오리젠 오리지널 독', 'PAYMENT_COMPLETED', '2025-02-20 13:26:30', 'PAYMENT', 'CREDIT_CARD', 18),
('2025-02-20 13:30:12.508961', '2025-02-20 13:30:12.508961', 'test@test.com', 'test@test.com', 42900.00, '서울시 중랑구 17-17', 'test17@portone.io', '이태훈', '123-345', NULL, 'imp_020075914027', '펫가든 내추럴', 'PAYMENT_COMPLETED', '2025-02-20 13:30:12', 'PAYMENT', 'CREDIT_CARD', 19),
('2025-02-20 13:34:41.724352', '2025-02-20 13:34:41.724352', 'test@test.com', 'test@test.com', 38900.00, '서울시 성동구 18-18', 'test18@portone.io', '서정희', '123-456', NULL, 'imp_020075914028', '로얄 캐닌 라지브리드', 'PAYMENT_COMPLETED', '2025-02-20 13:34:41', 'PAYMENT', 'CREDIT_CARD', 20);
