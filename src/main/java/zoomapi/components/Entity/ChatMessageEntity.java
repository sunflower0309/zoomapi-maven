package zoomapi.components.Entity;

import org.json.JSONObject;

public class ChatMessageEntity {
    private String id;
    public String message;
    public String sender;
    private String dateTime;
    private int timeStamp;
    private String channel_id;

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public static ChatMessageEntity JsonToMessage(JSONObject jsonObject,String channel_id){
        ChatMessageEntity chatMessageEntity =new ChatMessageEntity();
        chatMessageEntity.setDateTime(jsonObject.getString("date_time"));
        chatMessageEntity.setId(jsonObject.getString("id"));
        chatMessageEntity.setMessage(jsonObject.getString("message"));
        if(jsonObject.has("sender")) chatMessageEntity.setSender(jsonObject.getString("sender"));
        else chatMessageEntity.setSender("xdx19970130@gmail.com");
        chatMessageEntity.setTimeStamp(jsonObject.getInt("timestamp"));
        chatMessageEntity.setChannel_id(channel_id);
        return chatMessageEntity;
    }
}
