package zoomapi.components.Entity;

import org.json.JSONArray;
import org.json.JSONObject;

public class Channel {
    private String id;
    private String name;
    private int type;

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static JSONArray getChannel(JSONObject jsonObject){
        return jsonObject.getJSONArray("channels");
    }

    public static Channel JsonToChannel(JSONObject jsonObject){
        Channel channel=new Channel();
        channel.setId(jsonObject.getString("id"));
        channel.setName(jsonObject.getString("name"));
        channel.setType(jsonObject.getInt("type"));
        return channel;
    }
}
