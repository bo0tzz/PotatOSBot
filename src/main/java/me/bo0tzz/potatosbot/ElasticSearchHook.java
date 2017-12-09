package me.bo0tzz.potatosbot;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bo0tzz on 15-4-2016.
 */
public class ElasticSearchHook {
    private static String ELASTICSEARCH_URL;
    private static String RANDOM_QUERY = "{\"size\":1,\"query\":{\"function_score\":{\"functions\":[{\"random_score\":{}}]}}}";

    static {
        ELASTICSEARCH_URL = System.getenv("ELASTIC_IP");
        if (ELASTICSEARCH_URL == null || ELASTICSEARCH_URL.equals("")) {
            ELASTICSEARCH_URL = "http://elasticsearch:9200/";
        }
    }

    private ElasticSearchHook() {
    }

    public static JSONArray getResults(Character character, String query) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(ELASTICSEARCH_URL + character.getEndpoint() + "_search")
                    .queryString("q", query)
                    .header("Content-Type", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        if (response.getBody().getObject().getJSONObject("hits").getInt("total") == 0) {
            return null;
        }
        return response.getBody().getObject().getJSONObject("hits").getJSONArray("hits");
    }

    public static JSONArray getResults(String query) {
        return getResults(Character.ALL, query);
    }

    public static JSONObject getRandom(Character character) {
        HttpResponse<JsonNode> response = null;
        String url = ELASTICSEARCH_URL + character.getEndpoint() + "_search";
        HttpRequest request = Unirest.post(url)
                .header("Content-Type", "application/json")
                .body(new JsonNode(RANDOM_QUERY)).getHttpRequest();
        try {
            response = request.asJson();
        } catch (UnirestException e) {
            System.out.println("Ran into unirestexception! Url was " + url + "\n With request " + request.getBody().toString());
            e.printStackTrace();
        }
        try {
            return response.getBody().getObject().getJSONObject("hits").getJSONArray("hits").getJSONObject(0);
        } catch (JSONException e) {
            System.out.println("Received invalid json from url " + url + "\n With request " + request.getBody().toString());
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getRandom() {
        return getRandom(Character.ALL);
    }
}
