package com.cinebee.service;

import com.cinebee.dto.request.BannerRequest;
import com.cinebee.entity.Banner;

import java.util.List;

import com.cinebee.dto.response.BannerResponse;

public interface BannerService {

    Banner createBanner(BannerRequest request);

    List<Banner> getActiveBanners();
    
    // ✨ Method mới cache BannerResponse thay vì Banner entity
    List<BannerResponse> getActiveBannerResponses();
    
    // ✨ Method để lấy tất cả banner (cho admin)
    List<BannerResponse> getAllBannerResponses();

    Banner updateBanner(Long id, BannerRequest request);

    Banner deleteBanner(Long id);
    
    // ✨ Tìm banner theo movieId
    List<Banner> getBannersByMovieId(Long movieId);
    
    // ✨ Method để fix priority cho banner cũ
    void fixNullPriorities();
}
