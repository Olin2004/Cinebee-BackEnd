-- Insert test data for CineBee database

-- Insert Movies
INSERT INTO movie (id, title, description, genre, duration, director, cast, rating, age_rating, poster_url, trailer_url, is_hot, is_new, base_price, status, created_at, updated_at) VALUES
(1, 'Avatar: The Way of Water', 'Phần tiếp theo của bộ phim Avatar huyền thoại', 'Sci-Fi', 192, 'James Cameron', 'Sam Worthington, Zoe Saldana', 8.5, 'T13', 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=500&h=750&q=80', '', true, false, 150000, 'ACTIVE', NOW(), NOW()),
(2, 'Top Gun: Maverick', 'Phi công huyền thoại trở lại', 'Action', 131, 'Joseph Kosinski', 'Tom Cruise, Miles Teller', 8.8, 'T13', 'https://images.unsplash.com/photo-1489599649716-11d8b2c1d11d?auto=format&fit=crop&w=500&h=750&q=80', '', true, true, 160000, 'ACTIVE', NOW(), NOW()),
(3, 'Black Panther: Wakanda Forever', 'Cuộc phiêu lưu mới ở Wakanda', 'Action', 161, 'Ryan Coogler', 'Letitia Wright, Angela Bassett', 7.8, 'T13', 'https://images.unsplash.com/photo-1489599649716-11d8b2c1d11d?auto=format&fit=crop&w=500&h=750&q=80', '', false, true, 155000, 'ACTIVE', NOW(), NOW()),
(4, 'Doctor Strange 2', 'Phù thủy tối thượng trong đa vũ trụ', 'Action', 126, 'Sam Raimi', 'Benedict Cumberbatch, Elizabeth Olsen', 7.5, 'T16', 'https://images.unsplash.com/photo-1489599649716-11d8b2c1d11d?auto=format&fit=crop&w=500&h=750&q=80', '', false, false, 145000, 'ACTIVE', NOW(), NOW());

-- Insert Theaters
INSERT INTO theater (id, name, address, phone, email, created_at, updated_at) VALUES
(1, 'CineBee Times City', '458 Minh Khai, Hai Bà Trưng, Hà Nội', '0243-974-3333', 'timescity@cinebee.vn', NOW(), NOW()),
(2, 'CineBee Aeon Mall', 'AEON Mall Long Biên, 27 Cổ Linh, Long Biên, Hà Nội', '0243-974-4444', 'aeonmall@cinebee.vn', NOW(), NOW()),
(3, 'CineBee Vincom Center', '191 Bà Triệu, Hai Bà Trưng, Hà Nội', '0243-974-5555', 'vincom@cinebee.vn', NOW(), NOW()),
(4, 'CineBee Lotte Center', '54 Liễu Giai, Ba Đình, Hà Nội', '0243-974-6666', 'lotte@cinebee.vn', NOW(), NOW());

-- Insert Rooms for each theater
INSERT INTO room (id, theater_id, name, total_seats, room_type, created_at, updated_at) VALUES
-- Theater 1 (Times City)
(1, 1, 'Room 1', 100, 'STANDARD', NOW(), NOW()),
(2, 1, 'Room 2', 120, 'VIP', NOW(), NOW()),
(3, 1, 'Room 3', 150, 'IMAX', NOW(), NOW()),
-- Theater 2 (Aeon Mall)
(4, 2, 'Room A', 100, 'STANDARD', NOW(), NOW()),
(5, 2, 'Room B', 120, 'VIP', NOW(), NOW()),
(6, 2, 'Room C', 80, '4DX', NOW(), NOW()),
-- Theater 3 (Vincom)
(7, 3, 'Room 1', 100, 'STANDARD', NOW(), NOW()),
(8, 3, 'Room 2', 120, 'PREMIUM', NOW(), NOW()),
-- Theater 4 (Lotte)
(9, 4, 'Room 1', 100, 'STANDARD', NOW(), NOW()),
(10, 4, 'Room 2', 150, 'IMAX', NOW(), NOW());

-- Insert Showtimes (for next 7 days)
INSERT INTO showtime (id, movie_id, theater_id, room_id, start_time, end_time, base_price, final_price, available_seats, created_at, updated_at) VALUES
-- Today
(1, 1, 1, 1, CONCAT(CURDATE(), ' 09:00:00'), CONCAT(CURDATE(), ' 12:12:00'), 150000, 150000, 95, NOW(), NOW()),
(2, 1, 1, 2, CONCAT(CURDATE(), ' 14:00:00'), CONCAT(CURDATE(), ' 17:12:00'), 200000, 200000, 115, NOW(), NOW()),
(3, 2, 1, 3, CONCAT(CURDATE(), ' 19:00:00'), CONCAT(CURDATE(), ' 21:11:00'), 250000, 250000, 145, NOW(), NOW()),
(4, 2, 2, 4, CONCAT(CURDATE(), ' 10:30:00'), CONCAT(CURDATE(), ' 12:41:00'), 160000, 160000, 90, NOW(), NOW()),
(5, 3, 2, 5, CONCAT(CURDATE(), ' 15:30:00'), CONCAT(CURDATE(), ' 18:11:00'), 210000, 210000, 110, NOW(), NOW()),
(6, 4, 3, 7, CONCAT(CURDATE(), ' 20:00:00'), CONCAT(CURDATE(), ' 22:06:00'), 145000, 145000, 85, NOW(), NOW()),
-- Tomorrow
(7, 1, 1, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 09:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 12:12:00'), 150000, 150000, 98, NOW(), NOW()),
(8, 1, 2, 4, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 13:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 16:12:00'), 160000, 160000, 95, NOW(), NOW()),
(9, 2, 3, 8, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 18:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 20:11:00'), 180000, 180000, 115, NOW(), NOW()),
(10, 3, 4, 10, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 21:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 23:41:00'), 250000, 250000, 140, NOW(), NOW()),
-- Day after tomorrow
(11, 1, 2, 6, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 11:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 14:12:00'), 300000, 300000, 75, NOW(), NOW()),
(12, 2, 1, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 16:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 18:11:00'), 200000, 200000, 118, NOW(), NOW()),
(13, 3, 3, 7, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 19:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 22:11:00'), 145000, 145000, 92, NOW(), NOW()),
(14, 4, 4, 9, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 22:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 00:06:00'), 145000, 145000, 88, NOW(), NOW()),
-- 3 days from now
(15, 1, 1, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 10:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 13:12:00'), 250000, 250000, 148, NOW(), NOW()),
(16, 2, 2, 5, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 14:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 16:41:00'), 210000, 210000, 112, NOW(), NOW()),
(17, 3, 4, 10, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 17:00:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 19:41:00'), 250000, 250000, 135, NOW(), NOW()),
(18, 4, 3, 8, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 20:30:00'), CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 22:36:00'), 180000, 180000, 105, NOW(), NOW());

-- Create some test users
INSERT INTO user (id, username, email, password, full_name, phone, role, status, created_at, updated_at) VALUES
(1, 'admin', 'admin@cinebee.vn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin User', '0901234567', 'ADMIN', 'ACTIVE', NOW(), NOW()),
(2, 'user1', 'user1@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test User 1', '0901234568', 'USER', 'ACTIVE', NOW(), NOW()),
(3, 'user2', 'user2@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test User 2', '0901234569', 'USER', 'ACTIVE', NOW(), NOW());

-- Note: The password hash above is for 'password123' - you should change this in production
-- To generate new password hash, use: echo -n 'your_password' | bcrypt-cli
