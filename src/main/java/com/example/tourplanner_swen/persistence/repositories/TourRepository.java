package com.example.tourplanner_swen.persistence.repositories;

import com.example.tourplanner_swen.persistence.entities.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourRepository extends JpaRepository <TourEntity, Long> {

}
