package com.example.tourplanner_swen.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TOUR")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
}
