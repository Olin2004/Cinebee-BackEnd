package com.cinebee.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerRespone {
    Long id;
    private String title;
    private String description;
    private String bannerUrl;
    private String isActive;
}
