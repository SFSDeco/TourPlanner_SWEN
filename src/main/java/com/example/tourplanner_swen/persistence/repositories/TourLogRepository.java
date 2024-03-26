package com.example.tourplanner_swen.persistence.repositories;

import com.example.tourplanner_swen.persistence.entities.TourEntity;
import com.example.tourplanner_swen.persistence.entities.TourLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourLogRepository extends JpaRepository<TourLogEntity, Long> {
    List<TourLogEntity> findByTour(TourEntity tour);
}
