package com.cinebee.service.impl;

import com.cinebee.dto.request.BannerRequest;
import com.cinebee.entity.Banner;
import com.cinebee.entity.Movie;
import com.cinebee.exception.ErrorCode;
import com.cinebee.repository.BannerRepository;
import com.cinebee.repository.MovieRepository;
import com.cinebee.service.BannerService;
import com.cinebee.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {
    private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);
    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Tạo mới banner
    @Override
    @Transactional
    public Banner createBanner(BannerRequest request) {
        Banner banner = new Banner();
        banner.setTitle(request.getTitle());
        banner.setDescription(request.getDescription());
        banner.setBannerUrl(request.getBannerUrl());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setActive(true);
        if (request.getMovieId() != null) {
            Movie movie = ServiceUtils.findObjectOrThrow(() -> movieRepository.findById(request.getMovieId()), ErrorCode.MOVIE_NOT_FOUND);
            banner.setMovie(movie);
        }
        return bannerRepository.save(banner);
    }


    // Cập nhật thông tin banner
    @Override
    @Transactional
    public Banner updateBanner(Long id, BannerRequest request) {
        Banner banner = ServiceUtils.findObjectOrThrow(() -> bannerRepository.findById(id), ErrorCode.BANNER_NOT_FOUND);
        banner.setTitle(request.getTitle());
        banner.setDescription(request.getDescription());
        banner.setBannerUrl(request.getBannerUrl());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setActive(true);
        return bannerRepository.save(banner);
    }

    // Lấy danh sách banner còn hiệu lực
    @Transactional(readOnly = true)
    public List<Banner> getActiveBanners() {
        LocalDate today = LocalDate.now();
        return bannerRepository.findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(today, today);
    }
    // Xóa banner bằng cách đánh dấu là không hoạt động
    @Override
    @Transactional
    public Banner deleteBanner(Long id) {
        Banner banner = ServiceUtils.findObjectOrThrow(() -> bannerRepository.findById(id), ErrorCode.BANNER_NOT_FOUND);
        banner.setActive(false);
        return bannerRepository.save(banner);
    }
}