package com.FHTW.tourplanner_swen;


import com.FHTW.tourplanner_swen.api.MapApi;
import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.persistence.entities.TourLogEntity;
import com.FHTW.tourplanner_swen.persistence.repositories.TourLogRepository;
import com.FHTW.tourplanner_swen.persistence.repositories.TourRepository;
import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import com.FHTW.tourplanner_swen.service.mapper.TourLogMapper;
import com.FHTW.tourplanner_swen.service.mapper.TourMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TourPlannerSwenApplicationTests {

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private TourLogRepository tourLogRepository;
    @Autowired
    private TourMapper tourMapper;
    @Autowired
    private TourLogMapper tourLogMapper;
    @Autowired
    private TourService tourService;
    @Autowired
    private MapApi mapApi;


    @Test
    void contextLoads() {
    }

    @Test
    void test_TourEntity(){
        TourEntity t = TourEntity.builder()
                .name("Tour A")
                .build();

        System.out.println(tourRepository.count() + " rows");
        tourRepository.save(t);
        System.out.println(tourRepository.count() + " rows");
    }

    @Test
    void test_TourLogEntity(){
        TourLogEntity t = TourLogEntity.builder()
                .comment("Tour A log entry")
                .build();

        System.out.println(tourLogRepository.count() + " rows");
        tourLogRepository.save(t);
        System.out.println(tourLogRepository.count() + " rows");
    }

    @Test
    void test_TourLogTour() {
        TourEntity a = TourEntity.builder()
                .name("Tour B")
                .build();

        tourRepository.save(a);

        System.out.println(tourLogRepository.count() + " rows");
        tourLogRepository.findAll().forEach(System.out::println);

        TourLogEntity b = TourLogEntity.builder()
                .comment("This is a comment")
                .tour(a)
                .build();

        tourLogRepository.save(b);

        tourLogRepository.save(TourLogEntity.builder()
                .comment("This is a comment")
                .tour(a)
                .build());

        System.out.println(tourLogRepository.count() + " rows");
        tourLogRepository.findAll().forEach(System.out::println);
    }

    @Test
    public void testMapper(){
        TourEntity tour = TourEntity.builder()
                .name("Amazing Tour")
                .build();

        TourDto tourDto = tourMapper.mapToDto(tour);

        TourLogEntity tourLog = TourLogEntity.builder()
                .comment("I hated it though")
                .tour(tour)
                .build();

        TourLogDto tourLogDto = tourLogMapper.mapToDto(tourLog);

        assertEquals(tour.getId(), tourDto.getId());
        assertEquals(tour.getName(), tourDto.getName());
        assertEquals(tourLog.getId(), tourLogDto.getId());
        assertEquals(tourLog.getComment(), tourLogDto.getComment());
        assertEquals(tourLog.getTour().getId(), tourLogDto.getTour().getId());
        assertEquals(tourLog.getTour().getName(), tourLogDto.getTour().getName());
    }


    @Test
    public void testTourService(){
        TourEntity tour = TourEntity.builder()
                .name("Tour A")
                .build();

        //See current amount of tours
        System.out.println(tourRepository.count() + " rows");
        //test getAllTours without addition
        System.out.println(tourService.getAllTours());

        //Save New Tour
        tourService.saveNewTour(tourMapper.mapToDto(tour));
        //See update amount of tours
        System.out.println(tourRepository.count() + " rows");
        //test getAllTours with addition
        System.out.println(tourService.getAllTours());

        assertEquals(tourService.getTourByName("Tour A").get(0).getName(), tourMapper.mapToDto(tour).getName());

    }

    @Test
    public void test_searchAddress(){
        String coordinates = mapApi.searchAddress("Austria, 1200 Wien, Höchstädtplatz");
        assertEquals(coordinates, "16.377966,48.240169");
    }

    @Test
    public void test_searchDirection(){
        String coordinates1 = mapApi.searchAddress("Austria, 1200 Wien, Höchstädtplatz");
        String coordinates2 = mapApi.searchAddress("Austria, 1020 Wien, Praterstern");
        System.out.println(coordinates1);
        // start: 16.381029,48.235378
        // end: 16.392599,48.22038
        List<double[]> routes = mapApi.searchDirection(coordinates1, coordinates2);

        AtomicInteger i = new AtomicInteger();
        StringBuffer sb = new StringBuffer();
        routes.forEach(r -> {
            if (i.get() > 0) {
                sb.append(";");
            }
            if (i.getAndIncrement() % 5 == 0) {
                sb.append("\n");
            }
            sb.append(String.format("[%f; %f]", r[0], r[1]));
        });
        System.out.println();
        String routesAsString = sb.toString();
        routesAsString = routesAsString.replace(",",".").replace(";",",");
        System.out.println(routesAsString);
        System.out.println();
        System.out.printf("start: %s\n", coordinates1);
        System.out.printf("end: %s\n", coordinates2);
    }

    @Test
    public void test_getMap(){
        String coordinates1 = mapApi.searchAddress("Austria, 1200 Wien, Höchstädtplatz");
        String coordinates2 = mapApi.searchAddress("Austria, 1180 Wien, Anastasius-Grün Gasse");

        mapApi.getMap(coordinates1, coordinates2);
    }

    @Test
    public void test_copyImageIntoPermanentFolder(){
        TourDto tourDto = TourDto.builder()
                .id(999L)
                .name("testImage")
                .build();

        tourService.copyImageIntoPermanentFolder(tourDto);
    }

    @Test
    public void test_createTourMapImage(){
        TourDto tourDto = TourDto.builder()
                .id(1000L)
                .name("MundoGoesWhereHePleases")
                .build();

        tourService.createTourMapImage(tourDto);
    }
}


