package com.FHTW.tourplanner_swen.service.impl;

import com.FHTW.tourplanner_swen.api.MapApi;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class MapApiImpl implements MapApi {

    private static final String API_KEY = "xxx";

    @Override
    public String searchAddress(String text) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(
                "https://api.openrouteservice.org/geocode/search?boundary.country=AT&api_key=" + API_KEY + "&text=" + text,
                String.class);

        String coordinate = Objects.requireNonNull(response.getBody()).substring(response.getBody().indexOf("coordinates") + 14, response.getBody().indexOf("properties") - 4);
        System.out.println(response);
        return coordinate;
    }

    @Override
    public List<double[]> searchDirection(String start, String end) {
        String[] profiles = {"driving-car", "cycling-regular", "foot-walking"};
        String profile = profiles[2];
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(
                "https://api.openrouteservice.org/v2/directions/" + profile + "?api_key=" + API_KEY + "&start=" + start + "&end=" + end,
                String.class);

        List<double[]> routeCoordinates = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            System.out.println(jsonResponse);

            JSONArray features = jsonResponse.getJSONArray("features");
            if (features.isEmpty()) {
                return null;
            }
            JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            // Swapping longitude and latitude for each coordinate
            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray coord = coordinates.getJSONArray(i);
                double longitude = coord.getDouble(0);
                double latitude = coord.getDouble(1);
                routeCoordinates.add(new double[]{latitude, longitude});
            }
        }
        return routeCoordinates;
    }

    @Override
    public void getMap() {
        // TODO implement me
    }
}
