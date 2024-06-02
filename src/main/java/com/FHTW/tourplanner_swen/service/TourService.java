package com.FHTW.tourplanner_swen.service;

import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TourService {
    void saveNewTour(TourDto tourDto);

    List<TourDto> getAllTours();
    List<TourDto> getTourByName(String name);

    ResponseEntity<TourDto> getTourById(Long tourId);

    ResponseEntity<byte[]> getMapById(Long tourId);

    void createTourMapImage(TourDto tourDto);

    void copyImageIntoPermanentFolder(TourDto tourDto);

    void updateTour(TourDto tourDto);

    void deleteTour(Long id);
}
