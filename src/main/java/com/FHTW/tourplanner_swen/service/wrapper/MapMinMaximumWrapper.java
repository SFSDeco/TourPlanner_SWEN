package com.FHTW.tourplanner_swen.service.wrapper;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapMinMaximumWrapper {
    double minimumLongitude;
    double minimumLatitude;
    double maximumLongitude;
    double maximumLatitude;
}
