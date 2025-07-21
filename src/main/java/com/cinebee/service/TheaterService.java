package com.cinebee.service;

import com.cinebee.dto.request.TheaterRequest;
import com.cinebee.dto.response.TheaterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TheaterService {
    TheaterResponse createTheater(TheaterRequest request);
    TheaterResponse updateTheater(Long id, TheaterRequest request);
    void deleteTheater(Long id); // This will be a soft delete
    TheaterResponse getTheaterById(Long id);
    Page<TheaterResponse> getAllTheaters(Pageable pageable);
}
