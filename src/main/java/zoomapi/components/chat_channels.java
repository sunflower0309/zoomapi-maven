package zoomapi.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import zoomapi.utils;
public class chat_channels {
    private String token;
    public chat_channels(String token){
        this.token=token;
    }
    public JSONObject get_a_channel(HashMap<String,String> data, String channelId) throws IOException {
        String url=String.format("/chat/channels/%s",channelId);
        String jsonstr=utils.get_request(url,data,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public JSONObject list_channels(HashMap<String,String> data) throws IOException {
        String url=String.format("/chat/users/%s/channels","me");
        String jsonstr=utils.get_request(url,data,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public JSONObject create_a_channel(HashMap<String,String> data){
        String url=String.format("/chat/users/%s/channels","me");
        String jsonstr=utils.post_request(url,data,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public String update_a_channel(HashMap<String,String> data,String channelId){
        String url=String.format("/chat/channels/%s",channelId);
        return utils.patch_request(url,data,null,null,this.token);
    }
    public String delete_a_channel(HashMap<String,String> data,String channelId){
        String url=String.format("/chat/channels/%s",channelId);
        return utils.delete_request(url,data,null,null,this.token);
    }
    public JSONObject list_members(HashMap<String,String> data,String channelId) throws IOException {
        HashMap<String,String> newdata=new HashMap<>();
        if(data!=null) data.put("page_size","100");
        else newdata.put("page_size","100");
        String url=String.format("/chat/channels/%s/members",channelId);
        String jsonstr=utils.get_request(url,newdata,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public JSONObject invite_members(HashMap<String,String> data,String channelId){
        String url=String.format("/chat/channels/%s/members",channelId);
        String jsonstr=utils.post_request(url,data,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public JSONObject join(HashMap<String,String> data,String channelId){
        String url=String.format("/chat/channels/%s/members/me",channelId);
        String jsonstr=utils.post_request(url,data,null,null,this.token);
        return new JSONObject(jsonstr);
    }
    public String leave(HashMap<String,String> data,String channelId){
        String url=String.format("/chat/channels/%s/members/me",channelId);
        return utils.delete_request(url,data,null,null,this.token);
    }
    public String remove(HashMap<String,String> data,String channelId,String memberId){
        String url=String.format("/chat/channels/%s/members/%s",channelId,memberId);
        return utils.delete_request(url,data,null,null,this.token);
    }
}
