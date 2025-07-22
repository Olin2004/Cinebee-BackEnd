package com.cinebee.repository;

import com.cinebee.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    // Sắp xếp theo priority giảm dần (priority cao lên đầu), sau đó theo ID giảm dần
    List<Banner> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescIdDesc(LocalDate start, LocalDate end);
    
    // ✨ Tìm banner theo movieId (để validate)
    List<Banner> findByMovieId(Long movieId);
}
