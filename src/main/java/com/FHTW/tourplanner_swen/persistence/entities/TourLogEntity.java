package com.FHTW.tourplanner_swen.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="TOUR_LOG")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tour_log_seq_generator")
    @SequenceGenerator(name="tour_log_seq_generator", sequenceName ="tour_log_seq", allocationSize = 1)
    private Long id;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "tourid", referencedColumnName = "id")
    private TourEntity tour;

}
