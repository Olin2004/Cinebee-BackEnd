package com.cinebee.service.impl;

import com.cinebee.dto.request.BookingRequest;
import com.cinebee.dto.response.BookingResponse;
import com.cinebee.entity.*;
import com.cinebee.exception.ApiException;
import com.cinebee.exception.ErrorCode;
import com.cinebee.repository.*;
import com.cinebee.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    // TODO: Cần thêm ShowtimeRepository và SeatRepository
    // private final ShowtimeRepository showtimeRepository;
    // private final SeatRepository seatRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // Lấy user hiện tại
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUsername));

        log.info("Creating booking for user: {}, showtime: {}, seats: {}", 
                currentUsername, request.getShowtimeId(), request.getSeatNumbers());

        // TODO: Thực hiện logic tạo booking
        // 1. Kiểm tra showtime có tồn tại không
        // 2. Kiểm tra ghế có available không
        // 3. Tạo ticket cho từng ghế
        // 4. Trả về BookingResponse

        // TEMPORARY: Tạo response giả để test
        BookingResponse response = new BookingResponse();
        response.setTicketId(1L); // Temporary
        response.setShowtimeId(request.getShowtimeId());
        response.setMovieTitle("Test Movie");
        response.setTheaterName("CGV Test");
        response.setRoomName("Room 1");
        response.setShowtime(LocalDateTime.now().plusDays(1));
        response.setBookedAt(LocalDateTime.now());
        response.setStatus("PENDING_PAYMENT");
        
        // Tạo thông tin ghế
        List<BookingResponse.SeatInfo> seatInfos = request.getSeatNumbers().stream()
                .map(seatNumber -> new BookingResponse.SeatInfo(seatNumber, "STANDARD", 100000.0))
                .collect(Collectors.toList());
        response.setSeats(seatInfos);
        
        double totalPrice = seatInfos.size() * 100000.0;
        response.setTotalPrice(totalPrice);

        return response;
    }

    @Override
    public List<BookingResponse> getUserBookings() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Getting bookings for user: {}", currentUsername);
        
        // TODO: Implement logic lấy danh sách booking của user
        return new ArrayList<>();
    }

    @Override
    public BookingResponse getBookingDetails(Long ticketId) {
        // TODO: Implement logic lấy chi tiết booking
        throw new ApiException(ErrorCode.TICKET_NOT_FOUND);
    }

    @Override
    @Transactional
    public void cancelBooking(Long ticketId) {
        // TODO: Implement logic hủy booking
        log.info("Cancelling booking: {}", ticketId);
    }

    @Override
    public List<BookingResponse.SeatInfo> getAvailableSeats(Long showtimeId) {
        log.info("Getting available seats for showtime: {}", showtimeId);
        
        // TODO: Implement logic lấy ghế available
        // TEMPORARY: Trả về danh sách ghế giả
        List<BookingResponse.SeatInfo> availableSeats = new ArrayList<>();
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 10; col++) {
                String seatNumber = (char)('A' + row - 1) + String.valueOf(col);
                availableSeats.add(new BookingResponse.SeatInfo(seatNumber, "STANDARD", 100000.0));
            }
        }
        
        return availableSeats;
    }
}
