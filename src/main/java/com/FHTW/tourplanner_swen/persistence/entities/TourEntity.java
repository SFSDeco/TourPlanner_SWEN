package com.FHTW.tourplanner_swen.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tour_seq_generator")
    @SequenceGenerator(name="tour_seq_generator", sequenceName ="tour_seq", allocationSize = 1)
    private Long id;

    private String name;

    @Column(name = "fromaddress")
    private String fromAddress;

    @Column(name = "toaddress")
    private String toAddress;

    @Column(name = "transport_type")
    private String transportation_type;
}
