package com.cinebee.repository;

import com.cinebee.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Find booked seats for a specific showtime
    @Query("SELECT s.seatNumber FROM Ticket t JOIN t.seat s WHERE t.showtime.id = :showtimeId AND t.isCancelled = false")
    List<String> findBookedSeatsByShowtime(@Param("showtimeId") Long showtimeId);
    
    // Find tickets by user
    List<Ticket> findByUserId(Long userId);
    
    // Find tickets by showtime
    List<Ticket> findByShowtimeId(Long showtimeId);
    
    // Find tickets by user and showtime
    List<Ticket> findByUserIdAndShowtimeId(Long userId, Long showtimeId);
    
    // Count tickets for a showtime
    long countByShowtimeId(Long showtimeId);
}
