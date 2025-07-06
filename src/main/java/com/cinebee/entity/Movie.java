package com.cinebee.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "poster_url", length = 255)
    private String posterUrl;

    @Column(name = "poster_public_id", length = 255)
    private String posterPublicId;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(length = 50)
    private String genre;

    @Column(name = "base_price", nullable = false)
    private Double basePrice = 0.0;

    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 255)
    private String othernames;

    @Column
    private Double rating = 0.0;

    @Column
    private Integer votes = 0;


    @Column
    private Integer views = 0;

    // Thêm các trường mới
    @Column(length = 255)
    private String actors; // Diễn viên

    @Column(length = 255)
    private String director; // Đạo diễn

    @Column(length = 100)
    private String country; // Quốc gia
}