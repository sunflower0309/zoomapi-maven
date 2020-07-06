package zoomapi.components;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.utils;

import java.io.IOException;
import java.util.HashMap;

public class meeting {
    public static JSONObject list_meetings(HashMap<String,String> data, String token,String userId) throws IOException {
        String url=String.format("/users/%s/meetings",userId);
        String jsonstr= utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static JSONObject create_a_meeting(HashMap<String,String> data, String token){
        String url=String.format("/users/%s/meetings","me");
        String jsonstr=utils.post_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static String update(HashMap<String,String> data,String token,String meetingId){
        String url=String.format("/meetings/%s",meetingId);
        return utils.patch_request(url,data,null,null,token);
    }
    public static String delete(HashMap<String,String> data,String token,String meetingId){
        String url=String.format("/meetings/%s",meetingId);
        return utils.delete_request(url,data,null,null,token);
    }
    public static JSONObject get(HashMap<String,String> data,String token,String meetingId){
        String url=String.format("/meetings/%s",meetingId);
        String jsonstr=utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
}
