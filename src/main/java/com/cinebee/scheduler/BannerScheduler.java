package com.cinebee.scheduler;

import com.cinebee.entity.Banner;
import com.cinebee.repository.BannerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class BannerScheduler {
    @Autowired
    private BannerRepository bannerRepository;
    // fixedRate = 5000
    // Chạy mỗi ngày lúc 0h05 sáng
    @Scheduled(fixedRate = 5000)
    public void deactivateExpiredBanners() {
        LocalDate today = LocalDate.now();
        List<Banner> expiredBanners = bannerRepository.findAll().stream()
                .filter(b -> b.isActive() && b.getEndDate() != null && b.getEndDate().isBefore(today))
                .toList();
        for (Banner banner : expiredBanners) {
            banner.setActive(false);
        }
        if (!expiredBanners.isEmpty()) {
            bannerRepository.saveAll(expiredBanners);
           log.info("[BannerScheduler] Đã cập nhật " + expiredBanners.size() + " banner hết hạn thành inactive.");
        }
    }
}
