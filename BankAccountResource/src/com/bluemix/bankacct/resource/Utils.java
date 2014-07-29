
package com.bluemix.bankacct.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * The utility class for the fat test cases.
 */
@SuppressWarnings("deprecation")
public class Utils {
    private static String KEY_ACCESS_TOKEN = "access_token";
    private static String KEY_REFRESH_TOKEN = "refresh_token";
    public static String KEY_SCOPES = "scope";
    public static String KEY_USERNAME = "user_name";

   
    
    public static ArrayList<?> readScopesFromResponse(HttpResponse resp) throws IllegalStateException, IOException {
        String jsonStr = getContentFromResponse(resp);
    	System.out.println("jsonStr is " + jsonStr);
        if (!checkIsJSON(jsonStr)) {
            throw new IllegalArgumentException("The " + jsonStr + " is not a valid JSON.");
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, ArrayList> respMap = getObjectFromJSONResponse(jsonStr, Map.class);
        ArrayList<?> tokenStr = respMap.get(KEY_SCOPES);
//        Log.info(Utils.class, "readTokenFromResponse", tokenStr);
        return tokenStr;
    }
    
    
    public static ArrayList<?> readScopesFromResponseJSON(String resp_json_str) throws IllegalStateException, IOException {
        String jsonStr = resp_json_str;
    	System.out.println("jsonStr is " + jsonStr);
        if (!checkIsJSON(jsonStr)) {
            throw new IllegalArgumentException("The " + jsonStr + " is not a valid JSON.");
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, ArrayList> respMap = getObjectFromJSONResponse(jsonStr, Map.class);
        ArrayList<?> tokenStr = respMap.get(KEY_SCOPES);
//        Log.info(Utils.class, "readTokenFromResponse", tokenStr);
        return tokenStr;
    }
 

    /**
     * Read the token from HTTP response
     * 
     * @param resp
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    public static String readTokenFromResponse(HttpResponse resp) throws IllegalStateException, IOException {
        String jsonStr = getContentFromResponse(resp);
    	System.out.println("jsonStr is " + jsonStr);
        if (!checkIsJSON(jsonStr)) {
            throw new IllegalArgumentException("The " + jsonStr + " is not a valid JSON.");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> respMap = getObjectFromJSONResponse(jsonStr, Map.class);
        String tokenStr = respMap.get(KEY_ACCESS_TOKEN);
//        Log.info(Utils.class, "readTokenFromResponse", tokenStr);
        return tokenStr;
    }
    
    /**
     * Read the token from HTTP response
     * 
     * @param resp
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    public static String[] readTokensFromResponse(HttpResponse resp) throws IllegalStateException, IOException {
        String jsonStr = getContentFromResponse(resp);
        String tokens[] = new String[2];
    	System.out.println("jsonStr is " + jsonStr);
        if (!checkIsJSON(jsonStr)) {
            throw new IllegalArgumentException("The " + jsonStr + " is not a valid JSON.");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> respMap = getObjectFromJSONResponse(jsonStr, Map.class);
        tokens[0] = respMap.get(KEY_ACCESS_TOKEN);
        tokens[1] = respMap.get(KEY_REFRESH_TOKEN);
//        Log.info(Utils.class, "readTokenFromResponse", tokenStr);
        return tokens;
    }

    /**
     * Get content from response in string format.
     * 
     * @param resp
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    public static String getContentFromResponse(HttpResponse resp) throws IllegalStateException, IOException {
        if (null == resp) {
            return "";
        }

        HttpEntity entity = resp.getEntity();
        InputStream cis = entity.getContent();
        BufferedReader cbReader = new BufferedReader(new InputStreamReader(cis));

        StringBuilder sBuilder = new StringBuilder();
        String lineStr = null;
        while (null != (lineStr = cbReader.readLine())) {
//            Log.info(Utils.class, "getContentFromResponse", lineStr);
            sBuilder.append(lineStr);
        }
        EntityUtils.consume(entity);

        return sBuilder.toString();
    }

    /**
     * Check if the string is a JSON string.
     * 
     * @param jsonStr
     * @return
     */
    public static boolean checkIsJSON(String jsonStr) {
        if (null == jsonStr || jsonStr.isEmpty()) {
            return false;
        }

        jsonStr = jsonStr.trim();
        return (jsonStr.startsWith("{") && jsonStr.endsWith("}")) || (jsonStr.startsWith("[") && jsonStr.endsWith("]"));

    }

    /**
     * Get the object from JSONResponse. Just support the List and Map type.
     * 
     * @param jsonStr
     * @param type
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T getObjectFromJSONResponse(String jsonStr, Class<T> type) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objMapper = new ObjectMapper();

        if (type.isAssignableFrom(List.class)) {
            List retObj = (List) objMapper.readValue(jsonStr, type);
            return (T) retObj;
        } else if (type.isAssignableFrom(Map.class)) {
            Map retObj = (Map) objMapper.readValue(jsonStr, type);
            return (T) retObj;
        } else {
            throw new IllegalArgumentException("The type: " + type.getCanonicalName() + " is not supported.");
        }
    }

    /**
     * Create the httpClient
     * 
     * @return
     */
    public static DefaultHttpClient createHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setRedirectStrategy(new LaxRedirectStrategy());

        return httpClient;
    }
}
