package com.FHTW.tourplanner_swen.service.mapper;

public interface Mapper<S, T> {
    T mapToDto(S source);
}
