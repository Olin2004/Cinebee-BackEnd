package com.cinebee.controller;

import com.cinebee.dto.request.BannerRequest;
import com.cinebee.dto.response.BannerResponse;
import com.cinebee.entity.Banner;
import com.cinebee.mapper.BannerMapper;
import com.cinebee.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/banner")
public class BannerController {
    
    @Autowired
    private BannerService bannerService;

    /**
     * Tạo banner mới
     */
    @PostMapping("/add-banner")
    public ResponseEntity<BannerResponse> addBanner(@RequestBody BannerRequest request) {
        Banner banner = bannerService.createBanner(request);
        BannerResponse response = BannerMapper.toBannerResponse(banner);
        return ResponseEntity.ok(response);
    }

    /**
     * Update banner theo Movie ID - Tự động tạo mới nếu chưa có
     * Logic: 1 movie = 1 banner
     */
    @PostMapping("/update-banner/{movieId}")
    public ResponseEntity<BannerResponse> updateBannerByMovie(
            @PathVariable Long movieId, 
            @RequestBody BannerRequest request) {
        
        request.setMovieId(movieId);
        
        List<Banner> banners = bannerService.getBannersByMovieId(movieId);
        Banner result;
        
        if (banners.isEmpty()) {
            // Tạo banner mới nếu chưa có
            result = bannerService.createBanner(request);
        } else {
            // Update banner đầu tiên (should be only one)
            result = bannerService.updateBanner(banners.get(0).getId(), request);
        }
        
        BannerResponse response = BannerMapper.toBannerResponse(result);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Xóa banner (soft delete)
     */
    @DeleteMapping("/delete-banner/{id}")
    public ResponseEntity<BannerResponse> deleteBanner(@PathVariable Long id) {
        Banner deletedBanner = bannerService.deleteBanner(id);
        BannerResponse response = BannerMapper.toBannerResponse(deletedBanner);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy tất cả banner đang active
     */
    @GetMapping("/active")
    public ResponseEntity<List<BannerResponse>> getActiveBanners() {
        List<BannerResponse> responses = bannerService.getActiveBannerResponses();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Lấy tất cả banner (cho admin quản lý)
     */
    @GetMapping("/all")
    public ResponseEntity<List<BannerResponse>> getAllBanners() {
        List<BannerResponse> responses = bannerService.getAllBannerResponses();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Fix priority cho các banner cũ (maintenance)
     */
    @PostMapping("/fix-priorities")
    public ResponseEntity<String> fixNullPriorities() {
        bannerService.fixNullPriorities();
        return ResponseEntity.ok("✅ Đã fix priority cho các banner cũ");
    }
}
