package mx.iteso.desi.cloud.hw2;

import mx.iteso.desi.cloud.*;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

import java.io.IOException;
import java.util.StringTokenizer;

public class GeocodeMapper extends Mapper<LongWritable, Text, Text, GeocodeWritable> {

    private final static GeocodeWritable one = new GeocodeWritable();
    private Text word = new Text();

    public static String textValue = "Point";

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        Triple tr;

        StringTokenizer itr = new StringTokenizer(value.toString(),"\n");

        while(itr.hasMoreTokens()) {

            String token = itr.nextToken();

            if(token.charAt(0) == '#' || token.equals("started"))
                continue;

            tr = ParseTriple.parseTriple(token);

            if (tr.get(1).equals("http://www.georss.org/georss/point")) {
                Double [] latLon = ParserCoordinates.parseCoordinates(tr.get(2));

                Text text = new Text(textValue);
                DoubleWritable lat = new DoubleWritable(latLon[0]);
                DoubleWritable lon = new DoubleWritable(latLon[1]);

                one.set(text, lat, lon);
                word.set(tr.get(0));
                context.write(word, one);
            }
            else if (tr.get(1).equals("http://xmlns.com/foaf/0.1/depiction")) {

                Text text = new Text(tr.get(2));

                one.set(text, new DoubleWritable(), new DoubleWritable());
                word.set(tr.get(0));
                context.write(word, one);
            }

        }
    }
}
