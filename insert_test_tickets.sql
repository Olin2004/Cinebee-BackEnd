-- =================================================
-- 🎬 CINEBEE - SCRIPT INSERT 30 VÉ TEST
-- =================================================
-- Script này sẽ tạo 30 vé test để thử nghiệm email và thanh toán
-- Đảm bảo database đã có data cho: users, movies, theaters, rooms, showtimes

-- =================================================
-- 📊 KIỂM TRA DATA CÓ SẴN TRƯỚC KHI INSERT
-- =================================================
SELECT 'Checking existing data...' as info;

-- Kiểm tra users
SELECT COUNT(*) as total_users FROM users;

-- Kiểm tra movies
SELECT COUNT(*) as total_movies FROM movies;

-- Kiểm tra theaters
SELECT COUNT(*) as total_theaters FROM theaters;

-- Kiểm tra rooms 
SELECT COUNT(*) as total_rooms FROM rooms;

-- Kiểm tra showtimes
SELECT COUNT(*) as total_showtimes FROM showtimes;

-- Kiểm tra seats
SELECT COUNT(*) as total_seats FROM seats;

-- =================================================
-- 🏗️ TẠO DATA CƠ BẢN NẾU CHƯA CÓ
-- =================================================

-- Tạo movie test nếu chưa có
INSERT IGNORE INTO movies (id, title, description, duration, base_price, genre, actors, director, country) VALUES
(1, 'Avengers: Endgame', 'Siêu phẩm Marvel', 180, 120000, 'Action', 'Robert Downey Jr., Chris Evans', 'Russo Brothers', 'USA'),
(2, 'Spider-Man: No Way Home', 'Người nhện đa vũ trụ', 150, 150000, 'Action', 'Tom Holland, Tobey Maguire', 'Jon Watts', 'USA'),
(3, 'Titanic', 'Câu chuyện tình yêu bất diệt', 195, 100000, 'Romance', 'Leonardo DiCaprio, Kate Winslet', 'James Cameron', 'USA');

-- Tạo theater test nếu chưa có
INSERT IGNORE INTO theaters (id, name, address, contact_info, opening_hours, status) VALUES
(1, 'CGV Vincom', '123 Nguyễn Huệ, Q1, HCM', '1900-6017', '08:00-23:00', 'ACTIVE');

-- Tạo room test nếu chưa có
INSERT IGNORE INTO rooms (id, theater_id, name, capacity) VALUES
(1, 1, 'Room 1', 100),
(2, 1, 'Room 2', 80);

-- Tạo showtimes test nếu chưa có
INSERT IGNORE INTO showtimes (id, movie_id, theater_id, room_id, start_time, end_time, price_modifier) VALUES
(1, 1, 1, 1, '2025-07-23 19:30:00', '2025-07-23 22:30:00', 1.0),
(2, 2, 1, 1, '2025-07-24 20:00:00', '2025-07-24 22:30:00', 1.2),
(3, 3, 1, 2, '2025-07-25 18:00:00', '2025-07-25 21:15:00', 0.8);

-- Tạo seats test cho mỗi showtime (30 ghế mỗi showtime)
INSERT IGNORE INTO seats (showtime_id, seat_number, seat_type, is_available, price_modifier) VALUES
-- Seats cho showtime 1 (A1-A10, B1-B10, C1-C10)
(1, 'A1', 'STANDARD', true, 1.0), (1, 'A2', 'STANDARD', true, 1.0), (1, 'A3', 'STANDARD', true, 1.0), (1, 'A4', 'STANDARD', true, 1.0), (1, 'A5', 'STANDARD', true, 1.0),
(1, 'A6', 'STANDARD', true, 1.0), (1, 'A7', 'STANDARD', true, 1.0), (1, 'A8', 'STANDARD', true, 1.0), (1, 'A9', 'STANDARD', true, 1.0), (1, 'A10', 'STANDARD', true, 1.0),
(1, 'B1', 'STANDARD', true, 1.0), (1, 'B2', 'STANDARD', true, 1.0), (1, 'B3', 'STANDARD', true, 1.0), (1, 'B4', 'STANDARD', true, 1.0), (1, 'B5', 'STANDARD', true, 1.0),
(1, 'B6', 'STANDARD', true, 1.0), (1, 'B7', 'STANDARD', true, 1.0), (1, 'B8', 'STANDARD', true, 1.0), (1, 'B9', 'STANDARD', true, 1.0), (1, 'B10', 'STANDARD', true, 1.0),
(1, 'C1', 'VIP', true, 1.5), (1, 'C2', 'VIP', true, 1.5), (1, 'C3', 'VIP', true, 1.5), (1, 'C4', 'VIP', true, 1.5), (1, 'C5', 'VIP', true, 1.5),
(1, 'C6', 'VIP', true, 1.5), (1, 'C7', 'VIP', true, 1.5), (1, 'C8', 'VIP', true, 1.5), (1, 'C9', 'VIP', true, 1.5), (1, 'C10', 'VIP', true, 1.5),

-- Seats cho showtime 2 (A1-A10, B1-B10, C1-C10)
(2, 'A1', 'STANDARD', true, 1.0), (2, 'A2', 'STANDARD', true, 1.0), (2, 'A3', 'STANDARD', true, 1.0), (2, 'A4', 'STANDARD', true, 1.0), (2, 'A5', 'STANDARD', true, 1.0),
(2, 'A6', 'STANDARD', true, 1.0), (2, 'A7', 'STANDARD', true, 1.0), (2, 'A8', 'STANDARD', true, 1.0), (2, 'A9', 'STANDARD', true, 1.0), (2, 'A10', 'STANDARD', true, 1.0),
(2, 'B1', 'STANDARD', true, 1.0), (2, 'B2', 'STANDARD', true, 1.0), (2, 'B3', 'STANDARD', true, 1.0), (2, 'B4', 'STANDARD', true, 1.0), (2, 'B5', 'STANDARD', true, 1.0),
(2, 'B6', 'STANDARD', true, 1.0), (2, 'B7', 'STANDARD', true, 1.0), (2, 'B8', 'STANDARD', true, 1.0), (2, 'B9', 'STANDARD', true, 1.0), (2, 'B10', 'STANDARD', true, 1.0),
(2, 'C1', 'VIP', true, 1.5), (2, 'C2', 'VIP', true, 1.5), (2, 'C3', 'VIP', true, 1.5), (2, 'C4', 'VIP', true, 1.5), (2, 'C5', 'VIP', true, 1.5),
(2, 'C6', 'VIP', true, 1.5), (2, 'C7', 'VIP', true, 1.5), (2, 'C8', 'VIP', true, 1.5), (2, 'C9', 'VIP', true, 1.5), (2, 'C10', 'VIP', true, 1.5),

-- Seats cho showtime 3 (A1-A10, B1-B10, C1-C10)
(3, 'A1', 'STANDARD', true, 1.0), (3, 'A2', 'STANDARD', true, 1.0), (3, 'A3', 'STANDARD', true, 1.0), (3, 'A4', 'STANDARD', true, 1.0), (3, 'A5', 'STANDARD', true, 1.0),
(3, 'A6', 'STANDARD', true, 1.0), (3, 'A7', 'STANDARD', true, 1.0), (3, 'A8', 'STANDARD', true, 1.0), (3, 'A9', 'STANDARD', true, 1.0), (3, 'A10', 'STANDARD', true, 1.0),
(3, 'B1', 'STANDARD', true, 1.0), (3, 'B2', 'STANDARD', true, 1.0), (3, 'B3', 'STANDARD', true, 1.0), (3, 'B4', 'STANDARD', true, 1.0), (3, 'B5', 'STANDARD', true, 1.0),
(3, 'B6', 'STANDARD', true, 1.0), (3, 'B7', 'STANDARD', true, 1.0), (3, 'B8', 'STANDARD', true, 1.0), (3, 'B9', 'STANDARD', true, 1.0), (3, 'B10', 'STANDARD', true, 1.0),
(3, 'C1', 'VIP', true, 1.5), (3, 'C2', 'VIP', true, 1.5), (3, 'C3', 'VIP', true, 1.5), (3, 'C4', 'VIP', true, 1.5), (3, 'C5', 'VIP', true, 1.5),
(3, 'C6', 'VIP', true, 1.5), (3, 'C7', 'VIP', true, 1.5), (3, 'C8', 'VIP', true, 1.5), (3, 'C9', 'VIP', true, 1.5), (3, 'C10', 'VIP', true, 1.5);

-- =================================================
-- 🎫 INSERT 30 VÉ TEST
-- =================================================
-- Sử dụng seat_id từ bảng seats vừa tạo

INSERT INTO tickets (user_id, showtime_id, seat_id, price, is_cancelled, ticket_sales) VALUES
-- VÉ CHO USER 1 (10 vé) - Sử dụng seat_id từ 1-30
(1, 1, 1, 120000, false, 0),   -- A1 showtime 1
(1, 1, 2, 120000, false, 0),   -- A2 showtime 1
(1, 2, 31, 150000, false, 0),  -- A1 showtime 2
(1, 2, 32, 150000, false, 0),  -- A2 showtime 2
(1, 3, 61, 100000, false, 0),  -- A1 showtime 3
(1, 3, 62, 100000, false, 0),  -- A2 showtime 3
(1, 1, 21, 180000, false, 0),  -- C1 VIP showtime 1
(1, 2, 51, 225000, false, 0),  -- C1 VIP showtime 2
(1, 3, 81, 120000, false, 0),  -- C1 VIP showtime 3
(1, 1, 22, 180000, false, 0),  -- C2 VIP showtime 1

-- VÉ CHO USER 2 (10 vé)
(2, 1, 3, 120000, false, 0),   -- A3 showtime 1
(2, 1, 4, 120000, false, 0),   -- A4 showtime 1
(2, 2, 33, 150000, false, 0),  -- A3 showtime 2
(2, 2, 34, 150000, false, 0),  -- A4 showtime 2
(2, 3, 63, 100000, false, 0),  -- A3 showtime 3
(2, 3, 64, 100000, false, 0),  -- A4 showtime 3
(2, 1, 23, 180000, false, 0),  -- C3 VIP showtime 1
(2, 2, 52, 225000, false, 0),  -- C2 VIP showtime 2
(2, 3, 82, 120000, false, 0),  -- C2 VIP showtime 3
(2, 1, 24, 180000, false, 0),  -- C4 VIP showtime 1

-- VÉ CHO USER 3 (5 vé)
(3, 1, 5, 120000, false, 0),   -- A5 showtime 1
(3, 2, 35, 150000, false, 0),  -- A5 showtime 2
(3, 3, 65, 100000, false, 0),  -- A5 showtime 3
(3, 1, 25, 180000, false, 0),  -- C5 VIP showtime 1
(3, 2, 53, 225000, false, 0),  -- C3 VIP showtime 2

-- VÉ CHO USER 4 (3 vé)
(4, 1, 6, 120000, false, 0),   -- A6 showtime 1
(4, 2, 36, 150000, false, 0),  -- A6 showtime 2
(4, 3, 66, 100000, false, 0),  -- A6 showtime 3

-- VÉ CHO USER 5 (2 vé)
(5, 1, 7, 120000, false, 0),   -- A7 showtime 1
(5, 2, 37, 150000, false, 0);  -- A7 showtime 2

-- =================================================
-- 📊 KIỂM TRA KỂT QUẢ
-- =================================================
SELECT 'Tickets inserted successfully!' as result;

-- Kiểm tra tổng số vé
SELECT COUNT(*) as total_tickets_after_insert FROM tickets;

-- Xem vé theo user
SELECT 
    u.username,
    u.email,
    COUNT(t.id) as total_tickets,
    SUM(t.price) as total_amount
FROM users u
LEFT JOIN tickets t ON u.id = t.user_id
GROUP BY u.id, u.username, u.email
ORDER BY total_tickets DESC;

-- Xem chi tiết 5 vé đầu tiên
SELECT 
    t.id as ticket_id,
    u.username,
    u.email,
    m.title as movie,
    th.name as theater,
    r.name as room,
    s.seat_number,
    st.start_time as showtime,
    t.price,
    t.booked_at
FROM tickets t
JOIN users u ON t.user_id = u.id
JOIN seats s ON t.seat_id = s.id
JOIN showtimes st ON t.showtime_id = st.id
JOIN movies m ON st.movie_id = m.id
JOIN rooms r ON st.room_id = r.id
JOIN theaters th ON st.theater_id = th.id
ORDER BY t.id
LIMIT 5;

-- =================================================
-- 🧪 TICKET IDs ĐỂ TEST EMAIL
-- =================================================
SELECT 
    CONCAT('POST http://localhost:8080/api/test/send-ticket-email/', t.id) as test_email_urls
FROM tickets t
ORDER BY t.id
LIMIT 10;

SELECT '=== DONE! CÓ THỂ TEST EMAIL VỚI CÁC TICKET ID TRÊN ===' as final_message;
