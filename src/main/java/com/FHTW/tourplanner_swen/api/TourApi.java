package com.FHTW.tourplanner_swen.api;


import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "tour")
public class TourApi {
    @Autowired
    private TourService tourService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<TourDto> getAllTours() { return tourService.getAllTours(); }

    @GetMapping("/name/{name}")
    public List<TourDto> getTourByName(@PathVariable String name) { return tourService.getTourByName(name); }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public void insertNewTour(@RequestBody TourDto tour) { tourService.saveNewTour(tour); }

    @PutMapping("/{id}")
    public void updateTour(@PathVariable Long id, @RequestBody TourDto tour){
        tour.setId(id);
        tourService.updateTour(tour);
    }

    @DeleteMapping("/{id}")
    public void deleteTour(@PathVariable Long id){ tourService.deleteTour(id); }
}
