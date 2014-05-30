/**
 * Request testing with Apache Commons files
 * Created by Kevin Gleason on 5/28/2014.
 */

import org.apache.http.*;
import org.apache.commons.net.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class RequestTest {
    //Instance variables
    private CloseableHttpClient httpClient;

    public RequestTest(String uriStr, String uriPath, Map<String, String> params)
            throws URISyntaxException, ClientProtocolException, IOException{
        //Make HttpClient and generate the get request
        this.httpClient = HttpClients.createDefault();
        URI uri = buildURI("http", uriStr, uriPath, params);
        HttpGet httpGet = makeHttpGet(uri);

        //Print URI
        System.out.println("URI: " + httpGet.getURI());  //DEBUG
        CloseableHttpResponse response = this.httpClient.execute(httpGet);
        System.out.println(response.toString());

        //Print all headers
        for (Object i : viewHeaders(response, "Set-Cookie"))
            System.out.println(i.toString());

        //Release the connection at the end. Put in try-catch-final
        httpGet.releaseConnection();

        //Even more finally close the response
        response.close();
    }

    //Build the URI for the get request
    public URI buildURI(String scheme, String host, String path, Map<String, String> paramMap) throws URISyntaxException{
        URIBuilder uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path);
        Set<String> params = paramMap.keySet();
        for (String i : params){
            uri.setParameter(i, paramMap.get(i));
        }
        return uri.build();
    }

    public HttpGet makeHttpGet(URI uri){
        return new HttpGet(uri);
    }

    public HttpResponse getResponse(HttpGet httpGet){
        HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                HttpStatus.SC_OK, "OK");
        return httpResponse;
    }

    //View all headers
    public List<Object> viewHeaders(HttpResponse response) {
        HeaderIterator hi = response.headerIterator();
        List<Object> headers = new ArrayList<Object>();
        while (hi.hasNext())
            headers.add(hi.next());
        return headers;
    }

    //View headers corresponding to certain string
    public List<Object> viewHeaders(HttpResponse response, String s) {
        HeaderIterator hi = response.headerIterator(s);
        List<Object> headers = new ArrayList<Object>();
        while (hi.hasNext())
            headers.add(hi.next());
        return headers;
    }

    public static void main(String args[]) throws URISyntaxException,
            ClientProtocolException, IOException{
        // Build the Parameter map
        Map<String,String> kvps = new LinkedHashMap<String, String>();
        kvps.put("q", "httpclient");
        kvps.put("btnG", "Google Search");
        kvps.put("aq", "f");
        kvps.put("oq", "");

        RequestTest test = new RequestTest("www.google.com", "/search", kvps);
    }
}
