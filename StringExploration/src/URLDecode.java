import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by saggezza on 7/15/14.
 */
public class URLDecode {

    public static void decodeURL(String url){
        try {
            System.out.println(URLDecoder.decode(url,"UTF-8")); }
        catch (UnsupportedEncodingException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        String url = "params=dtm%3D2014-07-15+16%3A31%3A48.175%2Cp%3Dpc%2Ctv%3D0.1.0%2Ctna%3DGenericTracker+Test%2Caid" +
                "%3DJavaPlow%2Clang%3DEnglish%2Cres%3D1200x1080%2Cuid%3DKevin%2Ce%3Dge%2Cge_na%3DData+Loop%2Cevn" +
                "%3DLube+Insights%2CUsername%3Dsaggezza%2COperatingSystem%3DLinux%2COS_Version%3D3.13.0-30-generic%2CJRE_Version" +
                "%3D1.7.0_60%2CIteration%3D0%2Ccx%3DeyJQaG9uZSI6IkRyb2lkIiwiVGltZSI6IjJwbSIsIlpvbmUiOiJVU0EifQ%3D%3D,name=kevin";
        URLDecode.decodeURL(url);
    }
}
