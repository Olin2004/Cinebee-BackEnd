package com.cinebee.service.impl;

import com.cinebee.dto.request.BannerRequest;
import com.cinebee.dto.response.BannerResponse;
import com.cinebee.entity.Banner;
import com.cinebee.entity.Movie;
import com.cinebee.mapper.BannerMapper;
import com.cinebee.exception.ErrorCode;
import com.cinebee.repository.BannerRepository;
import com.cinebee.repository.MovieRepository;
import com.cinebee.service.BannerService;
import com.cinebee.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    // Tạo mới banner (với validation)
    @Override
    @CacheEvict(value = "activeBanners", allEntries = true) // Clear cache khi tạo banner mới
    @Transactional
    public Banner createBanner(BannerRequest request) {
        // ✨ VALIDATION: Kiểm tra xem phim đã có banner chưa
        if (request.getMovieId() != null) {
            List<Banner> existingBanners = bannerRepository.findByMovieId(request.getMovieId());
            if (!existingBanners.isEmpty()) {
                logger.warn("⚠️ Movie {} đã có {} banner(s) tồn tại!", request.getMovieId(), existingBanners.size());
                
                // TỰ ĐỘNG UPDATE BANNER ĐẦU TIÊN THAY VÌ TẠO MỚI
                Banner existingBanner = existingBanners.get(0);
                logger.info("🔄 Tự động cập nhật banner {} thay vì tạo mới", existingBanner.getId());
                return updateBanner(existingBanner.getId(), request);
            }
        }
        
        Banner banner = new Banner();
        mapRequestToBanner(banner, request);
        
  // Tự động set isActive dựa trên ngày
        LocalDate today = LocalDate.now();
        boolean shouldBeActive = request.getStartDate() != null 
            && request.getEndDate() != null
            && !today.isBefore(request.getStartDate())  // today >= startDate
            && !today.isAfter(request.getEndDate());    // today <= endDate
            
        banner.setActive(shouldBeActive);
        
        if (request.getMovieId() != null) {
            Movie movie = ServiceUtils.findObjectOrThrow(() -> movieRepository.findById(request.getMovieId()), ErrorCode.MOVIE_NOT_FOUND);
            banner.setMovie(movie);
        }
        
        if (shouldBeActive) {
            logger.info("✅ Banner '{}' được tạo và KÍCH HOẠT (trong thời gian hiệu lực)", banner.getTitle());
        } else {
            logger.info("❌ Banner '{}' được tạo nhưng VÔ HIỆU HÓA (ngoài thời gian hiệu lực)", banner.getTitle());
        }
        
        return bannerRepository.save(banner);
    }


    // Cập nhật thông tin banner
    @Override
    @CacheEvict(value = "activeBanners", allEntries = true) // Clear cache khi update banner
    @Transactional
    public Banner updateBanner(Long id, BannerRequest request) {
        Banner banner = ServiceUtils.findObjectOrThrow(() -> bannerRepository.findById(id), ErrorCode.BANNER_NOT_FOUND);
        mapRequestToBanner(banner, request);
        
        // ✨ LOGIC MỚI: Tự động set isActive dựa trên ngày
        LocalDate today = LocalDate.now();
        boolean shouldBeActive = request.getStartDate() != null 
            && request.getEndDate() != null
            && !today.isBefore(request.getStartDate())  // today >= startDate
            && !today.isAfter(request.getEndDate());    // today <= endDate
            
        banner.setActive(shouldBeActive);
        
        if (shouldBeActive) {
            logger.info("✅ Banner '{}' được cập nhật và KÍCH HOẠT (trong thời gian hiệu lực)", banner.getTitle());
        } else {
            logger.info("❌ Banner '{}' được cập nhật nhưng VÔ HIỆU HÓA (ngoài thời gian hiệu lực)", banner.getTitle());
        }
        
        return bannerRepository.save(banner);
    }

    private void mapRequestToBanner(Banner banner, BannerRequest request) {
        banner.setTitle(request.getTitle());
        banner.setDescription(request.getDescription());
        banner.setBannerUrl(request.getBannerUrl());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        
        // Set priority - nếu không có thì dùng giá trị cao (banner mới ưu tiên)
        if (request.getPriority() != null) {
            banner.setPriority(request.getPriority());
        } else {
            // Banner mới không có priority thì set = timestamp để luôn lên đầu
            banner.setPriority((int) (System.currentTimeMillis() / 1000));
        }
    }

    // Lấy danh sách banner còn hiệu lực
    @Override
    // @Cacheable(value = "activeBanners", key = "'active-banners-' + #root.methodName") // Tắt cache vì serialization issue
    @Transactional(readOnly = true)
    public List<Banner> getActiveBanners() {
        LocalDate today = LocalDate.now();
        return bannerRepository.findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescIdDesc(today, today);
    }
    
    // ✨ Method mới cache BannerResponse (có thể serialize)
    @Override
    @Cacheable(value = "activeBanners", key = "'active-banner-responses'")
    @Transactional(readOnly = true)
    public List<BannerResponse> getActiveBannerResponses() {
        List<Banner> banners = getActiveBanners();
        return banners.stream()
                .map(BannerMapper::toBannerResponse)
                .toList();
    }
    
    // ✨ Method để lấy tất cả banner (cho admin)
    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getAllBannerResponses() {
        List<Banner> allBanners = bannerRepository.findAll();
        return allBanners.stream()
                .map(BannerMapper::toBannerResponse)
                .toList();
    }
    // Xóa banner bằng cách đánh dấu là không hoạt động
    @Override
    @CacheEvict(value = "activeBanners", allEntries = true) // Clear cache khi delete banner
    @Transactional
    public Banner deleteBanner(Long id) {
        Banner banner = ServiceUtils.findObjectOrThrow(() -> bannerRepository.findById(id), ErrorCode.BANNER_NOT_FOUND);
        banner.setActive(false);
        return bannerRepository.save(banner);
    }
    
    // ✨ Tìm banner theo movieId
    @Override
    public List<Banner> getBannersByMovieId(Long movieId) {
        return bannerRepository.findByMovieId(movieId);
    }
    
    // ✨ Method để fix priority cho banner cũ
    @Override
    @CacheEvict(value = "activeBanners", allEntries = true) // Clear cache sau khi fix
    @Transactional
    public void fixNullPriorities() {
        List<Banner> allBanners = bannerRepository.findAll();
        boolean hasChanges = false;
        
        for (Banner banner : allBanners) {
            if (banner.getPriority() == null) {
                // Set priority = ID để banner cũ có thứ tự theo ID
                banner.setPriority(banner.getId().intValue());
                hasChanges = true;
                logger.info("🔧 Fixed priority for banner ID {} to {}", banner.getId(), banner.getPriority());
            }
        }
        
        if (hasChanges) {
            bannerRepository.saveAll(allBanners);
            logger.info("✅ Đã cập nhật priority cho {} banner", allBanners.size());
        }
    }
}