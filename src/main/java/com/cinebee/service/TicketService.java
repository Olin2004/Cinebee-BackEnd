package com.cinebee.service;

import com.cinebee.dto.request.BookingRequest;
import com.cinebee.dto.response.BookingResponse;

import java.util.List;

public interface TicketService {
    
    /**
     * Tạo booking cho ghế đã chọn
     * @param request BookingRequest chứa showtimeId và danh sách ghế
     * @return BookingResponse với thông tin vé đã tạo
     */
    BookingResponse createBooking(BookingRequest request);
    
    /**
     * Lấy danh sách vé của user hiện tại
     * @return Danh sách BookingResponse
     */
    List<BookingResponse> getUserBookings();
    
    /**
     * Lấy thông tin chi tiết 1 vé
     * @param ticketId ID của vé
     * @return BookingResponse
     */
    BookingResponse getBookingDetails(Long ticketId);
    
    /**
     * Hủy vé (chỉ khi chưa thanh toán)
     * @param ticketId ID của vé cần hủy
     */
    void cancelBooking(Long ticketId);
    
    /**
     * Lấy danh sách ghế có sẵn cho 1 suất chiếu
     * @param showtimeId ID của suất chiếu
     * @return Danh sách thông tin ghế
     */
    List<BookingResponse.SeatInfo> getAvailableSeats(Long showtimeId);
}
