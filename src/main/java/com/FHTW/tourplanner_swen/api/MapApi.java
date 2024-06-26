package com.FHTW.tourplanner_swen.api;

import java.util.List;

public interface MapApi {

    String searchAddress(String text);
    List<double[]> searchDirection(String start, String end);
    void getMap(String startCoordinate, String endCoordinate);

}
