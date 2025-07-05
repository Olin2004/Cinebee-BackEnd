package com.cinebee.service.impl;

import com.cinebee.dto.request.BannerRequest;
import com.cinebee.entity.Banner;
import com.cinebee.entity.Movie;
import com.cinebee.service.BannerService;
import com.cinebee.repository.BannerRepository;
import com.cinebee.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Tạo mới banner
    @Override
    public Banner createBanner(BannerRequest request) {
        Banner banner = new Banner();
        if(request == null) return  null;
        banner.setTitle(request.getTitle());
        banner.setDescription(request.getDescription());
        banner.setBannerUrl(request.getBannerUrl());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setActive(true);
        if (request.getMovieId() != null) {
            Movie movie = movieRepository.findById(request.getMovieId()).orElse(null);
            banner.setMovie(movie);
        }
        return bannerRepository.save(banner);
    }


    // Cập nhật thông tin banner
    @Override
    public Banner updateBanner(Long id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new RuntimeException("Banner not found"));
        banner.setTitle(request.getTitle());
        banner.setDescription(request.getDescription());
        banner.setBannerUrl(request.getBannerUrl());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setActive(true);
        return bannerRepository.save(banner);
    }

    // Lấy danh sách banner còn hiệu lực
    public List<Banner> getActiveBanners() {
        LocalDate today = LocalDate.now();
        return bannerRepository.findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(today, today);
    }
    // Xóa banner bằng cách đánh dấu là không hoạt động
    @Override
    public Banner deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new RuntimeException("Banner not found"));
        banner.setActive(false);
        return bannerRepository.save(banner);
    }
}