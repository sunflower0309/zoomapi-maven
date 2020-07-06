package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils;

import java.io.IOException;
import java.util.HashMap;

public class recording {
    public static JSONObject list_recording(HashMap<String,String> data, String token) throws IOException {
        String url=String.format("/users/%s/recordings","me");
        String jsonstr= utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static String delete_recordings(HashMap<String,String> data,String token,String meetingId){
        String url=String.format("/meetings/%s/recordings",meetingId);
        return utils.delete_request(url,data,null,null,token);
    }
    public static JSONObject get_recordings(HashMap<String,String> data, String token, String meetingId) throws IOException {
        String url=String.format("/meetings/%s/recordings",meetingId);
        String jsonstr=utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
}
