package com.cinemazino.dto.response;

public class TrendingMovieResponse {
    private Long id;
    private String title;
    private String othernames;
    private Double rating;
    private Integer votes;
    private String img;
    private Integer likes;
    private Long ticketSales;
    private Integer rank;

    public TrendingMovieResponse() {
    }

    public TrendingMovieResponse(Long id, String title, String othernames, Double rating, Integer votes, String img,
            Integer likes, Long ticketSales, Integer rank) {
        this.id = id;
        this.title = title;
        this.othernames = othernames;
        this.rating = rating;
        this.votes = votes;
        this.img = img;
        this.likes = likes;
        this.ticketSales = ticketSales;
        this.rank = rank;
    }

    public TrendingMovieResponse(Long id, String title, String othernames, Double rating, Integer votes, String posterUrl, Integer likes, long ticketSales, int rank) {
        this.id = id;
        this.title = title;
        this.othernames = othernames;
        this.rating = rating;
        this.votes = votes;
        this.img = posterUrl;
        this.likes = likes;
        this.ticketSales = ticketSales;
        this.rank = rank;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOthernames() {
        return othernames;
    }

    public void setOthernames(String othernames) {
        this.othernames = othernames;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Long getTicketSales() {
        return ticketSales;
    }

    public void setTicketSales(Long ticketSales) {
        this.ticketSales = ticketSales;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
