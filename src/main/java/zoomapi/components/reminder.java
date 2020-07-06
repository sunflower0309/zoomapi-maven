package zoomapi.components;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.components.handler.ChatReminder;
import zoomapi.components.handler.UpdateReminder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class reminder {
    private JSONArray newmsg;
    private JSONArray updatemsg;
    private HashMap<String,String> channelChange;
    private HashMap<String,HashMap<String,String>> memChange;
    public reminder(){
        this.newmsg=new JSONArray();
        this.updatemsg=new JSONArray();
        this.memChange=new HashMap<>();
        this.channelChange=new HashMap<>();
    }

    public JSONArray getNewmsg() {
        return newmsg;
    }

    public JSONArray getUpdatemsg() {
        return updatemsg;
    }

    public HashMap<String, String> getChannelChange() {
        return channelChange;
    }

    public HashMap<String, HashMap<String, String>> getMemChange() {
        return memChange;
    }

    public void addNewmsg(JSONObject msg) {
        this.newmsg.put(msg);
        System.out.println("new msg:"+msg.toString());
    }

    public void addUpdatemsg(JSONObject msg) {
        this.updatemsg.put(msg);
        System.out.println("update msg:"+msg.toString());
    }

    public void addMemChange(String channel_id,HashMap<String,String> channel) {
        this.memChange.put(channel_id,channel);
        System.out.println("channel member change"+channel);
    }

    public void addChannelChange(String channel_id,String channel_status){
        this.channelChange.put(channel_id, channel_status);
        System.out.println("channel change:"+channel_id+" "+channel_status);
    }
}
