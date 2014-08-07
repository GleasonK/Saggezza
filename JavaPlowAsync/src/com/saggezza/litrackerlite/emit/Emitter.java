package com.saggezza.litrackerlite.emit;

import com.saggezza.litrackerlite.track.PayloadMap;
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
 *  collector with minimal effect on code. Implements {@link java.lang.Runnable} so that is can
 *  be called using a {@link java.util.concurrent.ExecutorService}.
 *  TODO: Implement a batch request system depending on the collector interface.
 *  @author Kevin Gleason
 *  @version 0.5.0
 */
public class Emitter implements Runnable{
    public static boolean emitAll=true,
                          debug=false,
                          singleVar=false;
    private boolean emit = true;
    private String collectorURI,
                   path;
    private PayloadMap payload;

    /**
     * Empty constructor turns emitting off, will not call and Http Request functions.
     */
    public Emitter(){
        this.collectorURI=null;
        this.path=null;
        this.emit=false;
    }

    /**
     * Constructor to specify the collector URI. Changes emit to true, allowing for tracking to happen unless the
     *  static Emitter.emitAll boolean is specified as false,
     * Currently code assumes http:// and not https://.
     *  To change scheme see buildURI called in the run() implementation.
     * @param collectorURI The base path of the collector URI.
     * @param path
     */
    public Emitter(String collectorURI, String path){
        this.collectorURI=collectorURI;
        this.path=path;
        this.emit=true;
    }

    /**
     * Should only be used by the TrackerC class to call track. Constructor required to allow Payload.
     *  Added here instead of static runnable class to keep application as lightweight as possible.
     *  Makes a copy of the Emitter used in TrackerC so that changes to the emitter do not effect
     *  the tracking instance.
     * @param e Emitter specified in TrackerC
     * @param p Payload from TrackerC
     */
    public Emitter(Emitter e, PayloadMap p){
        this.collectorURI = e.collectorURI;
        this.path = e.path;
        this.payload = p;
        this.emit = e.emit;
    }

    /**
     * Implementation of the {@link java.lang.Runnable} interface. Allows the costly request to be made
     *  in the background.
     */
    @Override
    public void run(){
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
