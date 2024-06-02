package com.FHTW.tourplanner_swen.api;

import com.FHTW.tourplanner_swen.service.TourLogService;
import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import com.FHTW.tourplanner_swen.service.wrapper.TourLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "tour_log")
public class TourLogApi {
    @Autowired
    private TourLogService tourLogService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<TourLogDto> getAllLogs() {return tourLogService.getAllLogs();}

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{tourId}")
    public List<TourLogDto> getTourLogs(@PathVariable Long tourId) { return tourLogService.getTourLogs(tourId);}

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public void insertNewTourLog(@RequestBody TourLogRequest request) {
        tourLogService.addNewTourLog(request.getTourLog(), request.getTourId());
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/{tourId}/{logId}")
    public void updateTourLog(@PathVariable Long tourId, @PathVariable Long logId, @RequestBody TourLogDto tourLogDto){
        tourLogDto.setId(logId);
        tourLogService.updateTourLog(tourId, tourLogDto);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{logId}")
    public void deleteTourLog(@PathVariable Long logId){ tourLogService.deleteTourLog(logId);}
}
