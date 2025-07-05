package com.cinebee.controller;

import com.cinebee.dto.request.BannerRequest;
import com.cinebee.entity.Banner;
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

    @PostMapping("/add-banner")
    public ResponseEntity<?> addBanner(@RequestBody BannerRequest request) {
        Banner banner = bannerService.createBanner(request);
        return ResponseEntity.ok(banner);
    }

    @PostMapping("/update-banner/{id}")
    public ResponseEntity<?> updateBanner(@PathVariable Long id, @RequestBody BannerRequest request) {
        Banner updatedBanner = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(updatedBanner);
    }
    @DeleteMapping("/delete-banner/{id}")
    public ResponseEntity<?> deleteBanner(@PathVariable Long id) {
        Banner deletedBanner = bannerService.deleteBanner(id);
        return ResponseEntity.ok(deletedBanner);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Banner>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return ResponseEntity.ok(banners);
    }
}
