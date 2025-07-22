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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banner")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    @PostMapping("/add-banner")
    public ResponseEntity<BannerResponse> addBanner(@RequestBody BannerRequest request) {
        Banner banner = bannerService.createBanner(request);
        BannerResponse response = BannerMapper.toBannerResponse(banner);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-banner/{id}")
    public ResponseEntity<BannerResponse> updateBanner(@PathVariable Long id, @RequestBody BannerRequest request) {
        Banner updatedBanner = bannerService.updateBanner(id, request);
        BannerResponse response = BannerMapper.toBannerResponse(updatedBanner);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete-banner/{id}")
    public ResponseEntity<BannerResponse> deleteBanner(@PathVariable Long id) {
        Banner deletedBanner = bannerService.deleteBanner(id);
        BannerResponse response = BannerMapper.toBannerResponse(deletedBanner);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BannerResponse>> getActiveBanners() {
        // ✨ Sử dụng method cache BannerResponse thay vì Banner entity
        List<BannerResponse> responses = bannerService.getActiveBannerResponses();
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/fix-priorities")
    public ResponseEntity<String> fixNullPriorities() {
        bannerService.fixNullPriorities();
        return ResponseEntity.ok("✅ Đã fix priority cho các banner cũ");
    }
}
