package com.example.tourplanner_swen;

import com.example.tourplanner_swen.api.TourApi;
import com.example.tourplanner_swen.persistence.entities.TourEntity;
import com.example.tourplanner_swen.persistence.entities.TourLogEntity;
import com.example.tourplanner_swen.persistence.repositories.TourLogRepository;
import com.example.tourplanner_swen.persistence.repositories.TourRepository;
import com.example.tourplanner_swen.service.TourService;
import com.example.tourplanner_swen.service.dtos.TourDto;
import com.example.tourplanner_swen.service.dtos.TourLogDto;
import com.example.tourplanner_swen.service.impl.TourServiceImpl;
import com.example.tourplanner_swen.service.mapper.TourLogMapper;
import com.example.tourplanner_swen.service.mapper.TourMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
}
