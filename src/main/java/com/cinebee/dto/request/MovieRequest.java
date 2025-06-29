package com.cinebee.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRequest {
    private String title;
    private String othernames;
    private Double basePrice;
    private Integer duration;
    private String genre;
    private String posterUrl;
    private String description;
}
