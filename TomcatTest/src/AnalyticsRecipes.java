/**
 * Created by Kevin Gleason on 6/16/2014.
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.maxmind.geoip.Location;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map;

public class AnalyticsRecipes {
    public static void showLocationInfo(Location l){
        System.out.println("Country Info: " + l.countryCode + " " + l.countryName);
        System.out.println("City Info: " + l.area_code + " " + l.city);
        System.out.println("Postal Info: " + l.postalCode + " " + l.metro_code + " " + l.dma_code);
        System.out.println("GPS Info: " + l.latitude + " " + l.longitude);
        System.out.println();
        System.out.println();
    }

    public static void showLocationInfo(Map<String,String[]> m){
        if (!m.isEmpty())
            for (Map.Entry<String,String[]> entry : m.entrySet())
                System.out.println(entry.getKey() + " : " + entry.getValue()[0]);
    }

    public static JsonNode getDepthInfo(JsonNode j, String d1, String d2, String d3){
        return d3==null? j.get(d1).get(d2) : j.get(d1).get(d2).get(d3);
    }

    /**
     * Read the response from the request
     * @param ip
     * @param format
     */
    public static void requestGeoIP(String ip, String format){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            URIBuilder uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("freegeoip.net")
                    .setPath("/"+format+"/"+ip);
            HttpGet httpGet = new HttpGet(uri.build());
            CloseableHttpResponse response = httpClient.execute(httpGet);
            Header[] heads = response.getAllHeaders();
            for (Header h : heads)
                System.out.println(h);
            InputStream stream = response.getEntity().getContent();
            InputStreamReader reader = new InputStreamReader(stream);
            String read = EntityUtils.toString(response.getEntity());
            System.out.println(read);
            System.out.println(response.getEntity().getContent());
        } catch (IOException | URISyntaxException e) { e.printStackTrace(); }
    }


    public static void main(String[] args){
        AnalyticsRecipes.requestGeoIP("38.106.245.180","json");
    }
}
