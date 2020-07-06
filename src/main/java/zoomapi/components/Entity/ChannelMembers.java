package zoomapi.components.Entity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChannelMembers {
    private String id;
    private String channel_id;
    private String email;
    private String first_name;
    private String last_name;
    private String role;

    public String getId() {
        return id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getRole() {
        return role;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public static JSONArray getMembers(JSONObject jsonObject){
        return jsonObject.getJSONArray("members");
    }
    public static ChannelMembers jsonToChannelMembers(JSONObject jsonObject,String channel_id){
        ChannelMembers channelMembers=new ChannelMembers();
        channelMembers.setId(jsonObject.getString("id"));
        channelMembers.setEmail(jsonObject.getString("email"));
        channelMembers.setFirst_name(jsonObject.getString("first_name"));
        channelMembers.setLast_name(jsonObject.getString("last_name"));
        channelMembers.setRole(jsonObject.getString("role"));
        channelMembers.setChannel_id(channel_id);
        return channelMembers;
    }
}
