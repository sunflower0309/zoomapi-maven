package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils;

import java.io.IOException;
import java.util.HashMap;

public class webinar {
    public static JSONObject list(HashMap<String,String> data, String token,String userId) throws IOException {
        String url=String.format("/users/%s/webinars",userId);
        String jsonstr= utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static JSONObject create(HashMap<String,String> data, String token,String userId){
        String url=String.format("/users/%s/webinars",userId);
        String jsonstr=utils.post_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static String update(HashMap<String,String> data,String token,String Id){
        String url=String.format("/webinars/%s",Id);
        return utils.patch_request(url,data,null,null,token);
    }
    public static String delete(HashMap<String,String> data,String token,String Id){
        String url=String.format("/webinars/%s",Id);
        return utils.delete_request(url,data,null,null,token);
    }
    public static String end(HashMap<String,String> data,String token,String Id){
        String url=String.format("/webinars/%s/status",Id);
        return utils.put_request(url,data,null,null,token);
    }
    public static JSONObject get(HashMap<String,String> data,String token,String Id){
        String url=String.format("/webinars/%s",Id);
        String jsonstr=utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static JSONObject register(HashMap<String,String> data, String token,String Id){
        String url=String.format("/webinars/%s/registrants",Id);
        String jsonstr=utils.post_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
}
