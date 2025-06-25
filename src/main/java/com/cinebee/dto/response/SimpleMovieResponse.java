package com.cinebee.dto.response;

public class SimpleMovieResponse {
    private Long id;
    private String title;
    private Integer likes;
    private Integer views;
    private String img;

    public SimpleMovieResponse(Long id, String title, Integer likes, Integer views, String img) {
        this.id = id;
        this.title = title;
        this.likes = likes;
        this.views = views;
        this.img = img;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}
