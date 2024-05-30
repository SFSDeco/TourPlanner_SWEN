package com.FHTW.tourplanner_swen.service.impl;

import com.FHTW.tourplanner_swen.api.MapApi;
import com.FHTW.tourplanner_swen.service.PixelCalculator;
import com.FHTW.tourplanner_swen.service.wrapper.MapMinMaximumWrapper;
import com.FHTW.tourplanner_swen.service.wrapper.TileWrapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class MapApiImpl implements MapApi {

    private static final String API_KEY = "5b3ce3597851110001cf62487526ba5559864cadbaabc18943f1d29c";

    private static final int ZOOM_LEVEL = 14;


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
    public void getMap(String startCoordinates, String endCoordinates) {
        List<double[]> routeCoordinates = searchDirection(startCoordinates, endCoordinates);

        double[] firstCoordinates = routeCoordinates.get(0);

        MapMinMaximumWrapper mapMinMax = MapMinMaximumWrapper.builder()
                .maximumLatitude(firstCoordinates[0])
                .minimumLatitude(firstCoordinates[0])
                .maximumLongitude(firstCoordinates[1])
                .minimumLongitude(firstCoordinates[1])
                .build();

        if(!routeCoordinates.isEmpty()){
            for(double[] coords : routeCoordinates){
                mapMinMax.setMaximumLatitude(Math.max(coords[0], mapMinMax.getMaximumLatitude()));
                mapMinMax.setMaximumLongitude(Math.max(coords[1], mapMinMax.getMaximumLongitude()));
                mapMinMax.setMinimumLatitude(Math.min(coords[0], mapMinMax.getMinimumLatitude()));
                mapMinMax.setMinimumLongitude(Math.min(coords[1], mapMinMax.getMinimumLongitude()));

            }
        }

        try{
            generateMapImage(routeCoordinates, mapMinMax);
        }catch(IOException e){
            System.err.println("Error occurred during Map Image generation: " + e);
        }
    }

    private void generateMapImage(List<double[]> routeCoordinates, MapMinMaximumWrapper mapMinMax) throws IOException {
        var topLeftTile = TileWrapper.latlon2Tile(mapMinMax.getMaximumLatitude(), mapMinMax.getMinimumLongitude(), ZOOM_LEVEL);
        var bottomRightTile = TileWrapper.latlon2Tile(mapMinMax.getMinimumLatitude(), mapMinMax.getMaximumLongitude(), ZOOM_LEVEL);

        int tilesX = bottomRightTile.x() - topLeftTile.x() + 1;
        int tilesY = bottomRightTile.y() - topLeftTile.y() + 1;

        BufferedImage finalImage = new BufferedImage(tilesX*256, tilesY*256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();

        for (int x = topLeftTile.x(); x <= bottomRightTile.x(); x++) {
            for (int y = topLeftTile.y(); y <= bottomRightTile.y(); y++) {
                BufferedImage tileImage = fetchTile(x, y);
                int xPos = (x - topLeftTile.x()) * 256;
                int yPos = (y - topLeftTile.y()) * 256;
                g.drawImage(tileImage, xPos, yPos, null);
            }
        }

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));

        PixelCalculator.Point topLeftTilePixel = new PixelCalculator.Point(topLeftTile.x() * 256, topLeftTile.y() * 256);

        for (int i = 0; i < routeCoordinates.size() - 1; i++) {
            double[] startWayPoint = routeCoordinates.get(i);
            double[] endWayPoint = routeCoordinates.get(i + 1);

            PixelCalculator.Point startGlobalPos = PixelCalculator.latLonToPixel(startWayPoint[0], startWayPoint[1], ZOOM_LEVEL);
            PixelCalculator.Point startRelativePos = new PixelCalculator.Point(startGlobalPos.x() - topLeftTilePixel.x(), startGlobalPos.y() - topLeftTilePixel.y());

            PixelCalculator.Point endGlobalPos = PixelCalculator.latLonToPixel(endWayPoint[0], endWayPoint[1], ZOOM_LEVEL);
            PixelCalculator.Point endRelativePos = new PixelCalculator.Point(endGlobalPos.x() - topLeftTilePixel.x(), endGlobalPos.y() - topLeftTilePixel.y());

            g.drawLine(startRelativePos.x(), startRelativePos.y(), endRelativePos.x(), endRelativePos.y());
        }

        g.dispose();

        ImageIO.write(finalImage, "png", new File("MapImage.png"));
    }

    private static BufferedImage fetchTile(int x, int y) throws IOException {
        String tileUrl = "https://tile.openstreetmap.org/" + ZOOM_LEVEL + "/" + x + "/" + y + ".png";
        URL url = new URL(tileUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.addRequestProperty("User-Agent", "TourPlanner/SWEN course exercise application");

        try (InputStream inputStream = httpConn.getInputStream()) {
            return ImageIO.read(inputStream);
        } finally {
            httpConn.disconnect();
        }
    }

}
