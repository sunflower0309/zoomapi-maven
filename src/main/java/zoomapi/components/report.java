package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils;

import java.io.IOException;
import java.util.HashMap;

public class report {
    public static JSONObject get_user_report(HashMap<String,String> data, String token, String userId) throws IOException {
        String url=String.format("/report/users/%s/meetings",userId);
        String jsonstr= utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
    public static JSONObject get_account_report(HashMap<String,String> data, String token) throws IOException {
        String url=String.format("/report/users");
        String jsonstr=utils.get_request(url,data,null,null,token);
        return new JSONObject(jsonstr);
    }
}
