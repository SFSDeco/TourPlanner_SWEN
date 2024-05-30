package com.FHTW.tourplanner_swen.service.impl;

import com.FHTW.tourplanner_swen.api.MapApi;
import com.FHTW.tourplanner_swen.service.mapper.TourMapper;
import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.persistence.repositories.TourRepository;
import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

import java.util.List;

@Component
@Slf4j
public class TourServiceImpl implements TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourMapper tourMapper;

    @Autowired
    private MapApi mapApi;

    @Override
    public void saveNewTour(TourDto tourDto){
        TourEntity entity = TourEntity.builder()
                .id(tourDto.getId())
                .name(tourDto.getName())
                .build();
        tourRepository.save(entity);
    }

    @Override
    public List<TourDto> getAllTours() {return tourMapper.mapToDto(tourRepository.findAll());}

    @Override
    public List<TourDto> getTourByName(String name){
        return tourMapper.mapToDto(tourRepository.findByNameIgnoreCase(name));
    }

    @Override
    public void createTourMapImage(TourDto tourDto){

        String placeholderAdd1 = "Austria, 1200 Wien, Höchstädtplatz";
        String placeholderAdd2 = "Austria, 1180 Wien, Gersthoferstraße";

        String startCoordinates = mapApi.searchAddress(placeholderAdd1);
        String endCoordinates = mapApi.searchAddress(placeholderAdd2);

        mapApi.getMap(startCoordinates, endCoordinates);

        copyImageIntoPermanentFolder(tourDto);
    }

    @Override
    public void copyImageIntoPermanentFolder(TourDto tourDto){
        Path sourcePath = Paths.get("./MapImage.png");
        Path destinationDirectory = Paths.get("./img/maps");
        Path destinationPath = destinationDirectory.resolve(tourDto.getName() + "_" + tourDto.getId() + ".png");

        try{
            if(!Files.exists(destinationDirectory)){
                Files.createDirectories(destinationDirectory);
            }
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            System.err.println("Failed to copy the file: " + e.getMessage());
        }

    }

}
