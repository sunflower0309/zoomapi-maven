package zoomapi.components;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class chat_messages {
    private String token;
    public chat_messages(String token){
        this.token=token;
    }
    public JSONObject post(HashMap<String,String> data){
        String url="/chat/users/me/messages";
        String jsonstr=utils.post_request(url,data,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public JSONObject list(HashMap<String,String> data,String userId) throws IOException {
        String url=String.format("/chat/users/%s/messages",userId);
        String jsonstr=utils.get_request(url,data,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public String update(HashMap<String,String> data,String messageId){
        String url=String.format("/chat/users/me/messages/%s",messageId);
        return utils.put_request(url,data,null,null,this.token);
    }
    public String delete(HashMap<String,String> data,String messageId){
        String url=String.format("/chat/users/me/messages/%s",messageId);
        return utils.delete_request(url,data,null,null,this.token);
    }
}
