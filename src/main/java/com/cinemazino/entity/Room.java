package com.cinemazino.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "Rooms", uniqueConstraints = @UniqueConstraint(columnNames = { "theater_id", "name" }))
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private String name;

    private Integer capacity;
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    // ...getter, setter...
}