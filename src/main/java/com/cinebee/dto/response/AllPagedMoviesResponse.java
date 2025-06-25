package com.cinebee.dto.response;

import java.util.List;

public class AllPagedMoviesResponse<T> {
    private List<List<T>> pages;
    private int totalPages;
    private long totalElements;

    public AllPagedMoviesResponse(List<List<T>> pages, int totalPages, long totalElements) {
        this.pages = pages;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<List<T>> getPages() {
        return pages;
    }

    public void setPages(List<List<T>> pages) {
        this.pages = pages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
