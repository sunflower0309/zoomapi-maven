package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils;

import java.io.IOException;
import java.util.HashMap;

public class user {
    public static JSONObject list(HashMap<String,String> data, String token) throws IOException {
        String url=String.format("/users");
        String jsonstr= utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static JSONObject create(HashMap<String,String> data, String token){
        String url=String.format("/users");
        String jsonstr=utils.post_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static String update(HashMap<String,String> data,String token,String userId){
        String url=String.format("/users/%s",userId);
        return utils.patch_request(url,data,null,null,token);
    }
    public static String delete(HashMap<String,String> data,String token,String userId){
        String url=String.format("/users/%s",userId);
        return utils.delete_request(url,data,null,null,token);
    }
    public static JSONObject get(HashMap<String,String> data,String token,String userId){
        String url=String.format("/users/%s",userId);
        String jsonstr=utils.get_request(url,data,null,null,token);
        if(jsonstr==null){
            return null;
        }
        return new JSONObject(jsonstr);
    }
}
