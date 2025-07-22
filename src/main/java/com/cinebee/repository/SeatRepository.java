package com.cinebee.repository;

import com.cinebee.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats for a given showtime.
     * @param showtimeId The ID of the showtime.
     * @return A list of seats.
     */
    List<Seat> findByShowtimeId(Long showtimeId);

    /**
     * Find a specific seat by its number within a given showtime.
     * @param showtimeId The ID of the showtime.
     * @param seatNumber The number of the seat (e.g., "A1").
     * @return An Optional containing the seat if found.
     */
    Optional<Seat> findByShowtimeIdAndSeatNumber(Long showtimeId, String seatNumber);
}
