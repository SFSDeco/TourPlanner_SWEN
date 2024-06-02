package com.FHTW.tourplanner_swen.service.impl;

import com.FHTW.tourplanner_swen.api.MapApi;
import com.FHTW.tourplanner_swen.service.mapper.TourMapper;
import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.persistence.repositories.TourRepository;
import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

import java.util.List;
import java.util.Optional;

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
                .fromAddress(tourDto.getFromAddress())
                .toAddress(tourDto.getToAddress())
                .transportation_type(tourDto.getTransportation_type())
                .build();
        TourDto createTourDto = tourMapper.mapToDto(tourRepository.save(entity));

        log.info("Tour created with id: " + createTourDto.getId() + ". Now creating TourMap image.");

        createTourMapImage(createTourDto);

        log.info("TourMap image created, tour creation process finished.");
    }

    @Override
    public List<TourDto> getAllTours() {return tourMapper.mapToDto(tourRepository.findAll());}

    @Override
    public List<TourDto> getTourByName(String name){
        return tourMapper.mapToDto(tourRepository.findByNameIgnoreCase(name));
    }

    @Override
    public ResponseEntity<TourDto> getTourById(Long tourId){
        Optional<TourEntity> tourEntityOptional = tourRepository.findById(tourId);
        if(tourEntityOptional.isPresent()){
            TourDto tourDto = tourMapper.mapToDto(tourEntityOptional.get());
            return ResponseEntity.ok(tourDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<byte[]> getMapById(Long tourId){
        Optional<TourEntity> tourEntityOptional = tourRepository.findById(tourId);
        if(tourEntityOptional.isPresent()) {
            byte[] mapImageData;
            Path imagePath = Paths.get("./img/maps/"+tourEntityOptional.get().getName() + "_" + tourId + ".png");
            try{
                mapImageData = Files.readAllBytes(imagePath);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                return new ResponseEntity<>(mapImageData, headers, HttpStatus.OK);
            } catch(IOException e) {
                System.err.println("Error during image to Bye: " + e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.error("Tour with ID " + tourId + " not found!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void updateTour(TourDto tourDto){
        Optional<TourEntity> optionalTourEntity = tourRepository.findById(tourDto.getId());
        if(optionalTourEntity.isPresent()){
            TourEntity currentTour = optionalTourEntity.get();
            currentTour.setName(tourDto.getName());
            currentTour.setFromAddress(tourDto.getFromAddress());
            currentTour.setToAddress(tourDto.getToAddress());
            currentTour.setTransportation_type(tourDto.getTransportation_type());

            tourRepository.save(currentTour);

            log.info("Tour Entity updated, updating map...");

            createTourMapImage(tourDto);

            log.info("Tour Map created...");
        } else {
            log.error("Tour Id not found: " + tourDto.getId());
        }

    }

    @Override
    public void deleteTour(Long id){
        if(tourRepository.existsById(id)) {
            tourRepository.deleteById(id);
            log.info("Tour successfully deleted");
        } else {
            log.error("Tour with id not found: " + id);
        }
    }

    @Override
    public void createTourMapImage(TourDto tourDto){

        String startCoordinates = mapApi.searchAddress(tourDto.getFromAddress());
        String endCoordinates = mapApi.searchAddress(tourDto.getToAddress());

        mapApi.getMap(startCoordinates, endCoordinates);

        log.debug("Map Creation done...");

        copyImageIntoPermanentFolder(tourDto);

        log.debug("Map imaged moved into permanent folder.");
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
