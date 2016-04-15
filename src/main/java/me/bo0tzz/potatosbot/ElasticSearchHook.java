package me.bo0tzz.potatosbot;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by bo0tzz on 15-4-2016.
 */
public class ElasticSearchHook {
    private static String ELASTICSEARCH_URL = "http://vps269588.ovh.net:9200/audio/";
    private static String RANDOM_QUERY = "{\n" +
            "  \"query\": {\n" +
            "    \"function_score\" : {\n" +
            "      \"query\" : { \"match_all\": {} },\n" +
            "      \"random_score\" : {}\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private ElasticSearchHook() {
    }

    public static JSONArray getResults(String query) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(ELASTICSEARCH_URL + "_search").queryString("q", query).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JSONArray array = response.getBody().getObject().getJSONObject("hits").getJSONArray("hits");
        return array;
    }

    public static JSONObject getRandom() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post(ELASTICSEARCH_URL + "_search").body(new JsonNode(RANDOM_QUERY)).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JSONObject object = response.getBody().getObject().getJSONObject("hits").getJSONArray("hits").getJSONObject(0);
        return object;
    }
}
