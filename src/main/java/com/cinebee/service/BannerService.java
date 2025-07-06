package com.cinebee.service;

import com.cinebee.dto.request.BannerRequest;
import com.cinebee.entity.Banner;

import java.util.List;

public interface BannerService {
    Banner createBanner(BannerRequest request);
    List<Banner> getActiveBanners();
    Banner updateBanner(Long id, BannerRequest request);
    Banner deleteBanner(Long id);
}
