package com.FHTW.tourplanner_swen.service.mapper;

import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import org.springframework.stereotype.Component;

@Component
public class TourMapper extends AbstractMapper<TourEntity, TourDto>{
    @Override
    public TourDto mapToDto(TourEntity source){

        return TourDto.builder()
                .id(source.getId())
                .name(source.getName())
                .fromAddress(source.getFromAddress())
                .toAddress(source.getToAddress())
                .transportation_type(source.getTransportation_type())
                .build();
    }
}
