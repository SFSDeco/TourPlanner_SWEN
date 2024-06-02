package com.FHTW.tourplanner_swen.service.impl;

import com.FHTW.tourplanner_swen.api.MapApi;
import com.FHTW.tourplanner_swen.persistence.repositories.TourLogRepository;
import com.FHTW.tourplanner_swen.service.PDFGenerator;
import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import com.FHTW.tourplanner_swen.service.mapper.TourLogMapper;
import com.FHTW.tourplanner_swen.service.mapper.TourMapper;
import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.persistence.repositories.TourRepository;
import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
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
    private TourLogRepository tourLogRepository;
    @Autowired
    private TourMapper tourMapper;
    @Autowired
    private TourLogMapper tourLogMapper;
    @Autowired
    private MapApi mapApi;
    @Autowired
    private PDFGenerator pdfGenerator;

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
    public List<TourDto> getAllTours() {
        log.info("Creating response with all Tours...");
        return tourMapper.mapToDto(tourRepository.findAll());
    }

    @Override
    public List<TourDto> getTourByName(String name){
        log.info("Creating list of all Tours with the name " + name);
        return tourMapper.mapToDto(tourRepository.findByNameIgnoreCase(name));
    }

    @Override
    public ResponseEntity<TourDto> getTourById(Long tourId){
        Optional<TourEntity> tourEntityOptional = tourRepository.findById(tourId);
        if(tourEntityOptional.isPresent()){
            TourDto tourDto = tourMapper.mapToDto(tourEntityOptional.get());
            log.info("Found Tour with id: " + tourId);
            return ResponseEntity.ok(tourDto);
        } else {
            log.error("Could not find Tour with id: " + tourId);
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

                log.info("Successfully found Map corresponding to tourId: " + tourId);
                return new ResponseEntity<>(mapImageData, headers, HttpStatus.OK);
            } catch(IOException e) {

                log.error("Error during image to Byte: " + e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.error("Tour with ID " + tourId + " not found!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Resource> getReportPDFbyId(Long tourId){
        try{
            Optional<TourEntity> optionalTourEntity = tourRepository.findById(tourId);
            if(optionalTourEntity.isPresent()) {

                TourDto tourDto = tourMapper.mapToDto(optionalTourEntity.get());
                List<TourLogDto> tourLogDtos = tourLogMapper.mapToDto(tourLogRepository.findByTour(optionalTourEntity.get()));

                pdfGenerator.generatePdfFromTourDto(tourDto, tourLogDtos);
                File pdfFile = new File("./tour_report.pdf");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("filename", "tour_report.pdf");

                log.info("Successfully generated pdf file, sending to Client...");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(pdfFile.length())
                        .body(new FileSystemResource(pdfFile));
            } else {

                log.error("Tour with tourId: " + tourId + " could not be found.");
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e){
            log.error("Error during PDF generation: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
