package Request;

/**
 * Created by saggezza on 8/6/14.
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.*;


public class ApacheAsync implements Runnable{
    private String host;
    private String path;
    private String query;

    public ApacheAsync(String host, String path, String query){
        this.host=host;
        this.path=path;
        this.query=query;
    }

    @Override
    public void run(){
        try { Thread.sleep((new Random().nextInt(10)+1)*100); } catch (InterruptedException e) { e.printStackTrace(); }
        String responseStr = Thread.currentThread().getName();
        try {
            URI uri = buildURI(this.host, this.path, this.query);
            HttpGet httpGet = makeHttpGet(uri);
            responseStr += " " + makeRequest(httpGet);

        }
        catch (URISyntaxException e) { e.printStackTrace(); }
//        while(!Thread.currentThread().isInterrupted()){
//
//        }
        System.out.println(responseStr);
        Thread.currentThread().interrupt();
    }

    public URI buildURI(String host, String path, String query) throws URISyntaxException {
        URIBuilder builder = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPath(path);
        String[] qs = query.split("&");
        for (int i=0; i<qs.length; i++){
            String[] param = qs[i].split("=");
            builder.addParameter(param[0],param[1]);
        }
        URI uri = builder.build();
        System.out.println(uri.toString());
        return uri;
    }

    public HttpGet makeHttpGet(URI uri){ return new HttpGet(uri); }

    public String makeRequest(HttpGet httpGet){
        String responseString = "ERROR";
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        try {
            httpClient.start();
            Future<HttpResponse> future = httpClient.execute(httpGet, null);
            HttpResponse response = future.get();
            responseString = response.getStatusLine().toString();
            httpClient.close();
        } catch (ExecutionException | InterruptedException | IOException e) { e.printStackTrace(); }
        return responseString;
    }

    public static void main(String[] args) {
        String[] requests = new String[] {"uid=Kevin&time=NOW", "uid=Kevin2&time=THen","uid=Kevin3&time=Future"};
        ExecutorService executorService = Executors.newCachedThreadPool();
        long start = System.currentTimeMillis();
        System.out.println("Part 1");
        for (int i=0; i<requests.length;i++){
            executorService.execute(new ApacheAsync("localhost:80","/JavaPlow/urlinfo", requests[i]));
        }
        executorService.shutdown();
        System.out.println("Part2");
        System.out.println(System.currentTimeMillis()-start);


        while (!executorService.isTerminated()){}
        System.out.println("\nFinished all threads");
//        System.exit(1);
    }
}
