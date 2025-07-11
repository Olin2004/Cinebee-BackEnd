package com.cinebee.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String othernames;
    private Double rating;
    private String img;
    private Integer rank;
    private Integer duration;
    private String genre;
    private String actors;
    private String director;
    private String country;
    private LocalDate releaseDate;

}
