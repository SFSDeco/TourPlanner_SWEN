package com.FHTW.tourplanner_swen.service.impl;

import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.persistence.entities.TourLogEntity;
import com.FHTW.tourplanner_swen.persistence.repositories.TourLogRepository;
import com.FHTW.tourplanner_swen.persistence.repositories.TourRepository;
import com.FHTW.tourplanner_swen.service.TourLogService;
import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import com.FHTW.tourplanner_swen.service.mapper.TourLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TourLogServiceImpl implements TourLogService {
    @Autowired
    private TourLogRepository tourLogRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private TourLogMapper tourLogMapper;

    @Override
    public void addNewTourLog(TourLogDto tourLogDto, Long tourId){
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour not Found"));

        TourLogEntity tourLog = TourLogEntity.builder()
                .id(tourLogDto.getId())
                .comment(tourLogDto.getComment())
                .tour(tour)
                .log_date(tourLogDto.getLog_date())
                .difficulty(tourLogDto.getDifficulty())
                .rating(tourLogDto.getRating())
                .build();

        tourLogRepository.save(tourLog);

        log.info("Successfully created new Tour Log");
    }

    @Override
    public List<TourLogDto> getAllLogs() {
        return tourLogMapper.mapToDto(tourLogRepository.findAll());
    }

    @Override
    public void updateTourLog(Long tourId, TourLogDto tourLogDto){
        Optional<TourLogEntity> optionalTourLogEntity = tourLogRepository.findById(tourLogDto.getId());
        Optional<TourEntity> optionalTourEntity = tourRepository.findById(tourId);
        if((optionalTourEntity.isPresent()
                                && optionalTourLogEntity.isPresent()
                                && (optionalTourEntity.get().getId().equals(optionalTourLogEntity.get().getTour().getId())))){

            TourLogEntity currentTourLog = optionalTourLogEntity.get();
            currentTourLog.setComment(tourLogDto.getComment());

            tourLogRepository.save(currentTourLog);

            log.info("TourLog Entity updated.");
        } else {
            log.error("TourLog Id not found: " + tourLogDto.getId());
        }

    }

    @Override
    public void deleteTourLog(Long logId){
        if(tourLogRepository.existsById(logId)) {
            tourLogRepository.deleteById(logId);
            log.info("Tour successfully deleted");
        } else {
            log.error("Tour with id not found: " + logId);
        }
    }
}
