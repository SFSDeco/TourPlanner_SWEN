package com.FHTW.tourplanner_swen;


import com.FHTW.tourplanner_swen.api.MapApi;
import com.FHTW.tourplanner_swen.api.TourApi;
import com.FHTW.tourplanner_swen.api.TourLogApi;
import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.persistence.entities.TourLogEntity;
import com.FHTW.tourplanner_swen.persistence.repositories.TourLogRepository;
import com.FHTW.tourplanner_swen.persistence.repositories.TourRepository;
import com.FHTW.tourplanner_swen.service.PDFGenerator;
import com.FHTW.tourplanner_swen.service.PixelCalculator;
import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import com.FHTW.tourplanner_swen.service.mapper.TourLogMapper;
import com.FHTW.tourplanner_swen.service.mapper.TourMapper;
import com.FHTW.tourplanner_swen.service.wrapper.TileWrapper;
import com.FHTW.tourplanner_swen.service.wrapper.TourLogRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    @Autowired
    private TourApi tourApi;
    @Autowired
    private TourLogApi tourLogApi;


    @Test
    void contextLoads() {
    }

    //TEST ENTITIES

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

    //TEST TOUR LOGS

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

    //TEST MAPPER

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

    //TEST TOUR SERVICE

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

    //TEST MAP API

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

    //TEST MAP CREATION

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

    //TEST TOUR API

    @Test
    public void test_insertNewTour(){
        TourDto tourDto = TourDto.builder()
                .name("Test")
                .fromAddress("Höchstädtplatz, Wien 1200, Austria")
                .toAddress("Schottenring, Wien 1010, Austria")
                .transportation_type("foot-walking")
                .build();

        long countBeforeInsert = tourRepository.count();

        tourApi.insertNewTour(tourDto);

        long countAfterInsert = tourRepository.count();

        assertEquals(countBeforeInsert+1, countAfterInsert);
    }

    @Test
    public void test_getAllTours(){
        List<TourDto> listOfAllTours = tourMapper.mapToDto(tourRepository.findAll());

        List<TourDto> listThroughTourApi = tourApi.getAllTours();

        assertEquals(listOfAllTours, listThroughTourApi);
    }

    @Test
    public void test_getTourByName(){
        String findString = "Test";
        List<TourDto> listOfTestToursDirect = tourMapper.mapToDto(tourRepository.findByNameIgnoreCase(findString));
        List<TourDto> listOfTestToursApi = tourApi.getTourByName(findString);

        assertEquals(listOfTestToursApi, listOfTestToursDirect);
    }

    @Test
    public void test_updateTour(){
        TourDto tourDto = TourDto.builder()
                .id(1L)
                .name("Test")
                .fromAddress("Höchstädtplatz, Wien 1200, Austria")
                .toAddress("Mariahilfer Straße, Wien 1070, Austria")
                .transportation_type("foot-walking")
                .build();

        tourApi.updateTour(tourDto.getId(), tourDto);

        Optional<TourEntity> updatedTour = tourRepository.findById(1L);
        if(updatedTour.isPresent()){
            TourDto updatedTourDto = tourMapper.mapToDto(updatedTour.get());

            assertEquals(tourDto, updatedTourDto);
        } else {
            Assertions.fail("Updated tour not found.");
        }

    }

    @Test
    public void test_deleteTour(){
        long countBeforeDelete = tourRepository.count();

        tourApi.deleteTour(1L);

        long countAfterDelete = tourRepository.count();

        assertEquals(countBeforeDelete, countAfterDelete+1);
    }

    //TEST TOUR_LOG API
    @Test
    public void test_insertNewTourLog(){
        TourLogDto tourLogDto = TourLogDto.builder()
                .rating(5)
                .log_date(LocalDate.parse("2024-06-02"))
                .difficulty("low")
                .comment("TestComment")
                .build();

        TourLogRequest tourLogRequest = TourLogRequest.builder()
                .tourId(1L)
                .tourLog(tourLogDto)
                .build();

        long countBeforeInsert = tourLogRepository.count();

        tourLogApi.insertNewTourLog(tourLogRequest);

        long countAfterInsert = tourLogRepository.count();

        assertEquals(countBeforeInsert+1, countAfterInsert);
    }

    @Test
    public void test_getAllLogs(){
        List<TourLogDto> listOfAllTourLogs = tourLogMapper.mapToDto(tourLogRepository.findAll());

        List<TourLogDto> listThroughTourLogApi = tourLogApi.getAllLogs();

        assertEquals(listOfAllTourLogs, listThroughTourLogApi);
    }

    @Test
    public void test_getTourLogs(){

        TourEntity tourEntity = TourEntity.builder()
                .id(1L)
                .build();

        List<TourLogDto> listOfToursTourLogs = tourLogMapper.mapToDto((tourLogRepository.findByTour(tourEntity)));

        List<TourLogDto> listOfTourTourLogsApi = tourLogApi.getTourLogs(1L);

        assertEquals(listOfToursTourLogs, listOfTourTourLogsApi);
    }

    @Test
    public void test_updateTourLog(){
        Optional<TourEntity> tour = tourRepository.findById(1L);
        if(tour.isPresent()) {
            TourLogDto tourLogDto = TourLogDto.builder()
                    .log_date(LocalDate.parse("2024-06-02"))
                    .comment("A new comment")
                    .difficulty("extreme")
                    .rating(7)
                    .id(2L)
                    .tour(tourMapper.mapToDto(tour.get()))
                    .build();

            tourLogApi.updateTourLog(tour.get().getId(), tourLogDto.getId(), tourLogDto);

            Optional<TourLogEntity> updatedTourLog = tourLogRepository.findById(tourLogDto.getId());
            if (updatedTourLog.isPresent()) {
                TourLogDto updatedTourLogDto = tourLogMapper.mapToDto(updatedTourLog.get());

                assertEquals(tourLogDto, updatedTourLogDto);
            } else {
                Assertions.fail("Updated tour not found.");
            }
        } else {
            Assertions.fail("Test Tour with id 1 not found.");
        }

    }

    @Test
    public void test_deleteTourLog(){
        long countBeforeDelete = tourLogRepository.count();

        tourLogApi.deleteTourLog(2L);

        long countAfterDelete = tourLogRepository.count();

        assertEquals(countBeforeDelete, countAfterDelete+1);
    }

    //Test Tile Wrapper
    @Test
    public void test_latlon2Tile(){
        double latitude = 48.23932;
        double longitude = 16.37710;

        TileWrapper.Tile tile = TileWrapper.latlon2Tile(latitude, longitude,  13);
        assertEquals(tile.x(), 4468);
        assertEquals(tile.y(), 2839);
    }

    //Test Pixel Calculator
    @Test
    public void test_latlon2Pixel(){
        double latitude = 48.2392;
        double longitude = 16.37710;

        PixelCalculator.Point point = PixelCalculator.latLonToPixel(latitude, longitude, 13);

        assertEquals(point.x(), 1143979);
        assertEquals(point.y(), 726912);
    }

    //Test Repository Find
    @Test
    public void test_TourRepositoryFindByName(){
        TourEntity tour = TourEntity.builder()
                .name("FindThis")
                .build();

        tourRepository.save(tour);

        List<TourEntity> foundEntities = tourRepository.findByNameIgnoreCase("findthis");

        assertFalse(foundEntities.isEmpty());
    }

    @Test
    public void test_TourLogRepositoryFindByTour(){
        Optional<TourEntity> tour = tourRepository.findById(1L);
        if(tour.isPresent()){
            List<TourLogEntity> tourLogs = tourLogRepository.findByTour(tour.get());

            assertFalse(tourLogs.isEmpty(), "Returned list of Logs is empty, verify that the Test Tour has associated Logs.");

        } else {
            Assertions.fail("Could not find Test entity id(1)");
        }
    }

    //TEST REPORT GENERATION
    @Test
    public void test_GeneratePdfFromTourDto(){
        TourDto tourDto = TourDto.builder()
                .id(1L)
                .name("Test")
                .fromAddress("Höchstädtplatz, 1200 Wien, Austria")
                .toAddress("Ziegelhofstraße, Wien 1220, Austria")
                .transportation_type("foot-walking")
                .build();

        Optional<TourEntity> optionalTourEntity = tourRepository.findById(tourDto.getId());

        if(optionalTourEntity.isPresent()) {

            PDFGenerator pdfGenerator = new PDFGenerator();
            List<TourLogDto> tourLogDtos = tourLogMapper.mapToDto(tourLogRepository.findByTour(optionalTourEntity.get()));

            try {
                pdfGenerator.generatePdfFromTourDto(tourDto, tourLogDtos);
            } catch (Exception e) {
                Assertions.fail("Exception occurred while generating PDF: " + e.getMessage());
            }
        } else {
            Assertions.fail("Test Entity does not exist");
        }
    }

}


