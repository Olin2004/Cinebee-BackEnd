package com.cinebee.service.impl;

import com.cinebee.dto.request.BookingRequest;
import com.cinebee.dto.response.BookingResponse;
import com.cinebee.entity.*;
import com.cinebee.exception.ApiException;
import com.cinebee.exception.ErrorCode;
import com.cinebee.repository.*;
import com.cinebee.repository.SeatRepository;
import com.cinebee.repository.ShowtimeRepository;
import com.cinebee.repository.TicketRepository;
import com.cinebee.repository.UserRepository;
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
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    // PaymentRepository is not used in this logic, can be removed if not needed elsewhere

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Get current user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUsername));

        // 2. Get Showtime
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ApiException(ErrorCode.SHOWTIME_NOT_FOUND));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.SHOWTIME_HAS_PASSED);
        }

        List<Ticket> createdTickets = new ArrayList<>();
        List<Seat> seatsToUpdate = new ArrayList<>();
        double totalPrice = 0.0;

        // 3. Process each selected seat
        for (String seatNumber : request.getSeatNumbers()) {
            Seat seat = seatRepository.findByShowtimeIdAndSeatNumber(showtime.getId(), seatNumber)
                    .orElseThrow(() -> new ApiException(ErrorCode.SEAT_NOT_FOUND, "Seat " + seatNumber + " not found for this showtime."));

            if (!seat.getIsAvailable()) {
                throw new ApiException(ErrorCode.SEAT_NOT_AVAILABLE, "Seat " + seatNumber + " is already booked.");
            }

            // 4. Mark seat as unavailable and create a ticket
            seat.setIsAvailable(false);
            seatsToUpdate.add(seat);

            Ticket ticket = new Ticket();
            ticket.setUser(currentUser);
            ticket.setShowtime(showtime);
            ticket.setSeat(seat);
            
            // Calculate price for this specific ticket
            Double ticketPrice = showtime.getMovie().getBasePrice() * seat.getPriceModifier();
            ticket.setPrice(ticketPrice);
            totalPrice += ticketPrice;
            
            createdTickets.add(ticket);
        }

        // 5. Save all changes to the database
        seatRepository.saveAll(seatsToUpdate);
        List<Ticket> savedTickets = ticketRepository.saveAll(createdTickets);

        log.info("Successfully created {} tickets for user {}", savedTickets.size(), currentUsername);

        // 6. Create and return the response from the first saved ticket
        // (Assuming a booking can be represented by its first ticket)
        return mapToBookingResponse(savedTickets.get(0), savedTickets, totalPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse.SeatInfo> getAvailableSeats(Long showtimeId) {
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new ApiException(ErrorCode.SHOWTIME_NOT_FOUND);
        }
        
        List<Seat> allSeats = seatRepository.findByShowtimeId(showtimeId);
        
        return allSeats.stream()
                .filter(Seat::getIsAvailable)
                .map(seat -> {
                    Double finalPrice = seat.getShowtime().getMovie().getBasePrice() * seat.getPriceModifier();
                    return new BookingResponse.SeatInfo(seat.getSeatNumber(), seat.getSeatType().name(), finalPrice);
                })
                .collect(Collectors.toList());
    }

    // Helper method to map to response
    private BookingResponse mapToBookingResponse(Ticket representativeTicket, List<Ticket> allTickets, double totalPrice) {
        BookingResponse response = new BookingResponse();
        response.setTicketId(representativeTicket.getId()); // Use the ID of the first ticket as the representative booking ID
        response.setShowtimeId(representativeTicket.getShowtime().getId());
        response.setMovieTitle(representativeTicket.getShowtime().getMovie().getTitle());
        response.setTheaterName(representativeTicket.getShowtime().getTheater().getName());
        response.setRoomName(representativeTicket.getShowtime().getRoom().getName());
        response.setShowtime(representativeTicket.getShowtime().getStartTime());
        response.setBookedAt(representativeTicket.getBookedAt());
        response.setStatus("PENDING_PAYMENT"); // Default status after booking
        response.setTotalPrice(totalPrice);

        List<BookingResponse.SeatInfo> seatInfos = allTickets.stream()
                .map(ticket -> new BookingResponse.SeatInfo(
                        ticket.getSeat().getSeatNumber(),
                        ticket.getSeat().getSeatType().name(),
                        ticket.getPrice()))
                .collect(Collectors.toList());
        response.setSeats(seatInfos);

        return response;
    }

    // --- Other methods need to be implemented properly as well ---

    @Override
    public List<BookingResponse> getUserBookings() {
        // This needs a proper implementation
        log.warn("getUserBookings is not fully implemented yet.");
        return new ArrayList<>();
    }

    @Override
    public BookingResponse getBookingDetails(Long ticketId) {
        // This needs a proper implementation
        log.warn("getBookingDetails is not fully implemented yet.");
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException(ErrorCode.TICKET_NOT_FOUND));
        // This mapping is simplified, might need to fetch all tickets of the same booking
        return mapToBookingResponse(ticket, List.of(ticket), ticket.getPrice());

    }

    @Override
    @Transactional
    public void cancelBooking(Long ticketId) {
        // This needs a proper implementation
        log.warn("cancelBooking is not fully implemented yet.");
    }
}
