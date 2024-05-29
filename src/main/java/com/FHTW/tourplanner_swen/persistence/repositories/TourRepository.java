package com.FHTW.tourplanner_swen.persistence.repositories;

import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository <TourEntity, Long> {
    List<TourEntity> findByNameIgnoreCase(String name);
}

// Weiter so Herr Prof. Dec.