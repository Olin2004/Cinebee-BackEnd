package com.cinebee.controller;

import com.cinebee.dto.request.BookingRequest;
import com.cinebee.dto.response.BookingResponse;
import com.cinebee.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    /**
     * API để đặt vé (chọn ghế và tạo booking)
     * POST /api/v1/tickets/book
     */
    @PostMapping("/book")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        log.info("Booking request received: {}", request);
        BookingResponse response = ticketService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API để lấy danh sách vé của user hiện tại
     * GET /api/v1/tickets/my-bookings
     */
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getUserBookings() {
        List<BookingResponse> bookings = ticketService.getUserBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * API để lấy chi tiết 1 vé
     * GET /api/v1/tickets/{ticketId}
     */
    @GetMapping("/{ticketId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> getBookingDetails(@PathVariable Long ticketId) {
        BookingResponse booking = ticketService.getBookingDetails(ticketId);
        return ResponseEntity.ok(booking);
    }

    /**
     * API để hủy vé (chỉ khi chưa thanh toán)
     * DELETE /api/v1/tickets/{ticketId}
     */
    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long ticketId) {
        ticketService.cancelBooking(ticketId);
        return ResponseEntity.noContent().build();
    }

    /**
     * API để lấy danh sách ghế available cho 1 suất chiếu
     * GET /api/v1/tickets/showtimes/{showtimeId}/seats
     */
    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<BookingResponse.SeatInfo>> getAvailableSeats(@PathVariable Long showtimeId) {
        List<BookingResponse.SeatInfo> seats = ticketService.getAvailableSeats(showtimeId);
        return ResponseEntity.ok(seats);
    }
}
