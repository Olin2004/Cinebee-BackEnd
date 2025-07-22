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

    Banner updateBanner(Long id, BannerRequest request);

    Banner deleteBanner(Long id);
    
    // ✨ Method để fix priority cho banner cũ
    void fixNullPriorities();
}
