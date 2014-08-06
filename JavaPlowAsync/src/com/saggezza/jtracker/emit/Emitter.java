package com.saggezza.jtracker.emit;

import com.saggezza.jtracker.track.PayloadMap;
import com.saggezza.jtracker.track.TrackerC;
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
public class Emitter implements Runnable{
    public static boolean emitAll=true,
            debug=false;

    private boolean
            emit = true,
            singleVar=false;
    private String collectorURI,
            path;

    private PayloadMap payload;


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

    public Emitter(Emitter e, PayloadMap p){
        this.collectorURI = e.collectorURI;
        this.path = e.path;
        this.payload = p;
        this.emit = e.emit;
    }


    @Override
    public void run(){
//        try { Thread.sleep(5000);} catch (InterruptedException e) { e.printStackTrace(); }
        if (this.emit) {
            if (this.path.charAt(0) != '/')
                this.path = "/" + this.path;
            try {
                URI uri = buildURI("http", this.collectorURI, this.path, payload);
                HttpGet httpGet = makeHttpGet(uri);
                String responseStr = makeRequest(httpGet);
                if (Emitter.debug) System.out.println(responseStr + "\n  " + "URI: " + uri.toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//
//    public void emit(PayloadMap payload){
//
//    }

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
    private String makeRequest(HttpGet httpGet)
            throws IOException {
        String responseStr = Thread.currentThread().getName();
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        try{
            httpClient.start();
            Future<HttpResponse> future = httpClient.execute(httpGet, null);
            HttpResponse response = future.get();
            responseStr += " response: " + response.getStatusLine();
        }
        catch (ExecutionException e){ e.printStackTrace(); }
        catch (InterruptedException e){ e.printStackTrace(); }
        finally {
            httpClient.close();
        }
        return responseStr;
    }

}
