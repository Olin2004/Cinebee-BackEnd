package com.cinebee.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Seats", uniqueConstraints = @UniqueConstraint(columnNames = { "showtime_id", "seat_number" }))
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomSeat.SeatType seatType = RoomSeat.SeatType.STANDARD;

    private Boolean isAvailable = true;
    private Double priceModifier;
    // ...getter, setter...

    public Double getPriceModifier() {
        return priceModifier;
    }

    public void setPriceModifier(Double priceModifier) {
        this.priceModifier = priceModifier;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}