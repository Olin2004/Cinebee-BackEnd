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
    public void updateBannerStatus() {
        LocalDate today = LocalDate.now();
        List<Banner> allBanners = bannerRepository.findAll();
        boolean hasChanges = false;

        for (Banner banner : allBanners) {
            boolean shouldBeActive = banner.getStartDate() != null 
                && banner.getEndDate() != null
                && !today.isBefore(banner.getStartDate())  // today >= startDate
                && !today.isAfter(banner.getEndDate());    // today <= endDate

            // Chỉ cập nhật nếu trạng thái thay đổi
            if (banner.isActive() != shouldBeActive) {
                banner.setActive(shouldBeActive);
                hasChanges = true;
                
                if (shouldBeActive) {
                    log.info("[BannerScheduler] ✅ Banner ID {} '{}' đã được KÍCH HOẠT (trong thời gian hiệu lực)", 
                        banner.getId(), banner.getTitle());
                } else {
                    log.info("[BannerScheduler] ❌ Banner ID {} '{}' đã bị VÔ HIỆU HÓA (hết hạn hoặc chưa đến ngày)", 
                        banner.getId(), banner.getTitle());
                }
            }
        }

        if (hasChanges) {
            bannerRepository.saveAll(allBanners);
            log.info("[BannerScheduler] 🔄 Đã cập nhật trạng thái banner.");
        }
    }
}
