package com.saggezza.litetracker.emit;

import com.saggezza.litetracker.track.PayloadMap;
import com.saggezza.litetracker.track.TrackerC;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * An asynchronous emitter class to store requests and send them to the
 *  collector with minimal effect on code.
 */
public class Emitter {
    static {
        try {URI uriLoad = new URI("");} catch (URISyntaxException e) { e.printStackTrace(); }
        HttpGet getLoad = new HttpGet();
        URIBuilder uri = new URIBuilder();
        StringBuilder sb = new StringBuilder();
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
    }

    public static boolean emitAll=true;

    //Static
    private boolean debug = false,
            emit = true,
            singleVar=false;

    private String collectorURI,
            path;

    public Emitter(){
        this.collectorURI=null;
        this.path=null;
        this.emit=false;
    }

    public Emitter(String collectorURI, String path){
        this.collectorURI=collectorURI;
        this.path=path;
        this.emit=true;
    }

    public void emit(PayloadMap payload){
        if (this.emit) {
            if (this.path.charAt(0) != '/')
                this.path = "/" + this.path;
            try {
                URI uri = buildURI("http", this.collectorURI, this.path, payload);
                HttpGet httpGet = makeHttpGet(uri);
                makeRequest(httpGet);
                if (TrackerC.debug) System.out.println("URI: " + uri.toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Web functions
    *   Functions used to configure the Get request
    *   Split into several functions to configure HTTP errors catches in the future
    *   buildURI sets all the parameters up for the HttpGet
    *   HttpGet makes an HttpGet object.
    */
    private URI buildURI(String scheme, String host, String path, PayloadMap payload) throws URISyntaxException {
        URIBuilder uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path);
        Set<Map.Entry<String,String>> entrySet = payload.getParamEntrySet();
        if (singleVar){
            Iterator<Map.Entry<String,String>> entrySetIter = entrySet.iterator();
            String singleVarPayload = getSingleVarPayload(entrySet.iterator());
            uri.setParameter("params", singleVarPayload);
        } else {
            for (Map.Entry<String,String> i : entrySet) {
                uri.setParameter(i.getKey(), i.getValue());
            }
        }
        return uri.build();
    }

    // Return all variables in a single string form.
    // Need to leave out first key for sake of parameter.
    private String getSingleVarPayload(Iterator<Map.Entry<String,String>> entrySetIter){
        StringBuilder sb = new StringBuilder();
        while(entrySetIter.hasNext()){
            Map.Entry<String,String> tmp = entrySetIter.next();
            sb.append(tmp.getKey()+"="+tmp.getValue());
            if (entrySetIter.hasNext())
                sb.append(",");
        }
        return sb.toString();
    }

    // Make a HttpGet class based on the URI
    private HttpGet makeHttpGet(URI uri){
        return new HttpGet(uri);
    }

    // Make the request, do the work you need to, then close the response.
    // All acceptable status codes are in the 200s
    private void makeRequest(HttpGet httpGet)
            throws IOException {

        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        try{
            httpClient.start();
            Future<HttpResponse> future = httpClient.execute(httpGet, null);
            HttpResponse response = future.get();
            System.out.println("Response: " + response.getStatusLine());
        }
        catch (ExecutionException e){ e.printStackTrace(); }
        catch (InterruptedException e){ e.printStackTrace(); }
        finally {
            httpClient.close();
        }
    }

}