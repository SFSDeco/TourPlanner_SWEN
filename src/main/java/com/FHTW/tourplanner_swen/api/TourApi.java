package com.FHTW.tourplanner_swen.api;


import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path ="tour")
public class TourApi {
    @Autowired
    private TourService tourService;

    @GetMapping
    public List<TourDto> getAllTours() { return tourService.getAllTours(); }

    @GetMapping("/name/{name}")
    public List<TourDto> getTourByName(@PathVariable String name) { return tourService.getTourByName(name); }

    @PostMapping
    public void insertNewTour(@RequestBody TourDto tour) { tourService.saveNewTour(tour); }
}