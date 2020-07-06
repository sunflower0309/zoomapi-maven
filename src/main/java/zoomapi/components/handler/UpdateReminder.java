package zoomapi.components.handler;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.components.Entity.Channel;
import zoomapi.components.Entity.ChatMessageEntity;
import zoomapi.components.Utils.ChannelSQLUtils;
import zoomapi.components.Utils.ChatMessageSQLUtils;
import zoomapi.components.chat;
import zoomapi.components.reminder;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateReminder extends Thread{
    //取两个jsonarray 比较不同
    //按时间顺序排好
    private JSONArray currentList;
    private JSONArray newList;
    private chat chat;
    private String name;
    private boolean flag;
    private ChatMessageSQLUtils chatMessageSQLUtils;
    private ChannelSQLUtils channelSQLUtils;
    public UpdateReminder(String name,chat chat) throws IOException, ParseException {//only check current date
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String day=sdf.format(date);
        this.currentList=chat.chat_history(day,day,name);
        this.chat=chat;
        this.name=name;
        this.flag=true;
        this.chatMessageSQLUtils=new ChatMessageSQLUtils();
        this.channelSQLUtils=new ChannelSQLUtils();
    }

    public void execute_reminder(reminder reminder){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag){
                    System.out.println("Update Message Reminder Initialized");
                    Date date=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    String day=sdf.format(date);
                    try {
                        Thread.sleep(5000);
                        newList=chat.chat_history(day,day,name);
                        int current_num=currentList.length();
                        String channelid=channelSQLUtils.query("select * from Channel where name='"+name+"'",
                                null, Channel.class).get(0).getId();
                        for(int i=0;i<current_num;i++){
                            if(!MsgJSONEquals(currentList.getJSONObject(i),newList.getJSONObject(i))){
                                reminder.addUpdatemsg(newList.getJSONObject(i));//callback
                                ChatMessageEntity chatMessageEntity=ChatMessageEntity.JsonToMessage(
                                        newList.getJSONObject(i),channelid);
                                if(chatMessageSQLUtils.exist(chatMessageEntity)){
                                    chatMessageSQLUtils.insert("Message",chatMessageEntity);
                                }
                            }
                        }
                        currentList=newList;

                    } catch (IOException | ParseException | InterruptedException | InstantiationException | NoSuchFieldException | SQLException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public boolean MsgJSONEquals(JSONObject jsonObject1,JSONObject jsonObject2){
        return jsonObject1.get("id").equals(jsonObject2.get("id"))&&
                jsonObject1.get("message").equals(jsonObject2.get("message"));
    }
    public void end(){
        this.flag=false;
    }
    public void reset(){
        this.flag=true;
    }
}
