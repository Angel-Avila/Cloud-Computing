package mx.iteso.desi.cloud.hw2;

import mx.iteso.desi.cloud.Geocode;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

import mx.iteso.desi.cloud.GeocodeWritable;

import java.io.IOException;

public class GeocodeReducer extends Reducer<Text, GeocodeWritable, Text, Text> {

    private String place;
    private double lat, lon;
    private Geocode geocode;

    private Geocode Guadalajara = new Geocode("Guadalajara", 20.66, -103.35);
    private Geocode Monterrey = new Geocode("Monterrey", 25.69, -100.32);
    private Geocode Philadelphia = new Geocode("Philadelphia", 39.95, -75.17);
    private Geocode Houston = new Geocode("Houston", 29.77, -95.35);
    private Geocode Seattle = new Geocode("Seattle", 47.61, -122.34);

    public void reduce(Text key, Iterable<GeocodeWritable> values, Context context) throws IOException, InterruptedException {

        for (GeocodeWritable g : values){
            if(g.getName().toString().equals(GeocodeMapper.textValue)){
                lat = g.getLatitude();
                lon = g.getLongitude();
            }
            else {
                place = g.getName().toString();
            }
        }

        if (isInRange(lat, lon)) {
            geocode = new Geocode(place, lat, lon);
            Text text = new Text(geocode.toString());
            context.write(key, text);
        }
    }

    private Boolean isInRange(Double lat, Double lon) {
        Geocode geocode = new Geocode("", lat, lon);

        if (geocode.getHaversineDistance(Guadalajara.getLatitude(), Guadalajara.getLongitude()) <= 5000.00) {
            return true;
        }  else if (geocode.getHaversineDistance(Monterrey.getLatitude(), Monterrey.getLongitude()) <= 5000.00) {
            return true;
        }  else if (geocode.getHaversineDistance(Philadelphia.getLatitude(), Philadelphia.getLongitude()) <= 5000.00) {
            return true;
        }  else if (geocode.getHaversineDistance(Houston.getLatitude(), Houston.getLongitude()) <= 5000.00) {
            return true;
        }  else if (geocode.getHaversineDistance(Seattle.getLatitude(), Seattle.getLongitude()) <= 5000.00) {
            return true;
        }

        return false;
    }
  
}
