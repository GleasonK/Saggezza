package com.saggezza.jtracker.enrich;

import java.util.HashMap;
import java.util.Map;

/**
 * FatFormat class is used to find the index where a value should be placed in O(1) lookup time.
 * <p>Do not allow data tracked to contain commas, will throw off the CSV</p>
 * @author Kevin Gleason
 * @version 0.0.2
 */
public class FatFormat {
    private static final Map<String,Integer> fatIndexMap = new HashMap<String, Integer>();

    //Add all index pairs from file
    static {
        fatIndexMap.put("aid",0);
        fatIndexMap.put("p",1);
        fatIndexMap.put("collector_tstamp",2);
        fatIndexMap.put("dtm",3);
        fatIndexMap.put("e",4);
        fatIndexMap.put("evn",5);
        fatIndexMap.put("eid",6);
        fatIndexMap.put("tid",7);
        fatIndexMap.put("tna",8);
        fatIndexMap.put("tv",9);
        fatIndexMap.put("v_collector",10);
        fatIndexMap.put("v_etl",11);
        fatIndexMap.put("uid",12);
        fatIndexMap.put("uip",13);
        fatIndexMap.put("fp",14);
        fatIndexMap.put("domain_userid",15);
        fatIndexMap.put("domain_sessionidx",16);
        fatIndexMap.put("network_userid",17);
        fatIndexMap.put("geo_country",18);
        fatIndexMap.put("geo_region",19);
        fatIndexMap.put("geo_city",20);
        fatIndexMap.put("geo_zipcode",21);
        fatIndexMap.put("geo_latitude",22);
        fatIndexMap.put("geo_longitude",23);
        fatIndexMap.put("url",24);
        fatIndexMap.put("page",25);
        fatIndexMap.put("refr",26);
        fatIndexMap.put("page_urlscheme",27);
        fatIndexMap.put("page_urlhost",28);
        fatIndexMap.put("page_urlport",29);
        fatIndexMap.put("page_urlpath",30);
        fatIndexMap.put("page_urlquery",31);
        fatIndexMap.put("page_urlfragment",32);
        fatIndexMap.put("refr_urlscheme",33);
        fatIndexMap.put("refr_urlhost",34);
        fatIndexMap.put("refr_urlport",35);
        fatIndexMap.put("refr_urlpath",36);
        fatIndexMap.put("refr_urlquery",37);
        fatIndexMap.put("refr_urlfragment",38);
        fatIndexMap.put("refr_medium",39);
        fatIndexMap.put("refr_source",40);
        fatIndexMap.put("refr_term",41);
        fatIndexMap.put("mkt_medium",42);
        fatIndexMap.put("mkt_source",43);
        fatIndexMap.put("mkt_term",44);
        fatIndexMap.put("mkt_content",45);
        fatIndexMap.put("mkt_campaign",46);
        fatIndexMap.put("cx",47);
        fatIndexMap.put("se_ca",48);
        fatIndexMap.put("se_ac",49);
        fatIndexMap.put("se_la",50);
        fatIndexMap.put("se_pr",51);
        fatIndexMap.put("se_va",52);
        fatIndexMap.put("ue_na",53);
        fatIndexMap.put("ue_px",54);
        fatIndexMap.put("tr_id",55);
        fatIndexMap.put("tr_af",56);
        fatIndexMap.put("tr_tt",57);
        fatIndexMap.put("tr_tx",58);
        fatIndexMap.put("tr_sh",59);
        fatIndexMap.put("tr_ci",60);
        fatIndexMap.put("tr_st",61);
        fatIndexMap.put("tr_co",62);
        fatIndexMap.put("ti_id",63);
        fatIndexMap.put("ti_sk",64);
        fatIndexMap.put("ti_nm",65);
        fatIndexMap.put("ti_ca",66);
        fatIndexMap.put("ti_pr",67);
        fatIndexMap.put("ti_qu",68);
        fatIndexMap.put("pp_xoffset_min",69);
        fatIndexMap.put("pp_xoffset_max",70);
        fatIndexMap.put("pp_yoffset_min",71);
        fatIndexMap.put("pp_yoffset_max",72);
        fatIndexMap.put("useragent",73);
        fatIndexMap.put("br_name",74);
        fatIndexMap.put("br_family",75);
        fatIndexMap.put("br_version",76);
        fatIndexMap.put("br_type",77);
        fatIndexMap.put("br_renderengine",78);
        fatIndexMap.put("br_lang",79);
        fatIndexMap.put("f_pdf",80);
        fatIndexMap.put("f_fla",81);
        fatIndexMap.put("f_java",82);
        fatIndexMap.put("f_dir",83);
        fatIndexMap.put("f_qt",84);
        fatIndexMap.put("br_features_realplayer",85);
        fatIndexMap.put("f_wma",86);
        fatIndexMap.put("f_gears",87);
        fatIndexMap.put("f_ag",88);
        fatIndexMap.put("cookie",89);
        fatIndexMap.put("cd",90);
        fatIndexMap.put("br_viewwidth",91);
        fatIndexMap.put("br_viewheight",92);
        fatIndexMap.put("os_nm",93);
        fatIndexMap.put("os_fam",94);
        fatIndexMap.put("os_man",95);
        fatIndexMap.put("tz",96);
        fatIndexMap.put("dvce_type",97);
        fatIndexMap.put("dvce_ismobile",98);
        fatIndexMap.put("dvce_screenwidth",99);
        fatIndexMap.put("dvce_screenheight",100);
        fatIndexMap.put("doc_charset",101);
        fatIndexMap.put("doc_width",102);
        fatIndexMap.put("doc_height",103);
    }

    //Private constructor
    private FatFormat(){}

    public static int getIndex(String columnHead){
        return FatFormat.fatIndexMap.get(columnHead);
    }

    public static Map<String,Integer> getFatIndexMap(){
        return fatIndexMap;
    }

    public static void main(String[] args) {
        try {
            System.out.println(FatFormat.getIndex("p"));
        } catch (NullPointerException e) {
            System.out.println("Error, key did not exist. Check the key naming system.");
            e.printStackTrace();
        }

    }
}
