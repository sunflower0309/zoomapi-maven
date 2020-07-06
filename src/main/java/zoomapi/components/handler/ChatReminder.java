package zoomapi.components.handler;

import org.json.JSONArray;
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
import java.util.List;

public class ChatReminder extends Thread {
    private int message_count;
    private chat chat;
    private String name;
    private boolean flag;
    private ChatMessageSQLUtils chatMessageSQLUtils;
    private ChannelSQLUtils channelSQLUtils;
    public ChatReminder(chat chat,String name) throws IOException, ParseException {
        this.chat=chat;
        this.name=name;
        this.flag=true;
        this.chatMessageSQLUtils=new ChatMessageSQLUtils();
        this.channelSQLUtils=new ChannelSQLUtils();
        initialize();
    }

    public void execute_reminder(reminder reminder){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray new_msg_list=new JSONArray();
                while (flag){
                    Date date=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    String day=sdf.format(date);
                    try {
                        System.out.println("current: "+message_count);
                        Thread.sleep(5000);
                        new_msg_list=chat.chat_history(day,day,name);
                        int current_num=new_msg_list.length();
                        System.out.println("New :"+current_num);
                        List<Channel> channelList=channelSQLUtils.query("select * from Channel where name='"+name+"'",null,
                                Channel.class);
                        for(int i=message_count;i<current_num;i++){
                            reminder.addNewmsg(new_msg_list.getJSONObject(i));//callback
                            ChatMessageEntity chatMessageEntity=ChatMessageEntity.JsonToMessage(new_msg_list.getJSONObject(i)
                                    ,channelList.get(0).getId());
                            chatMessageSQLUtils.insert("Message",chatMessageEntity);
                        }
                        message_count=current_num;

                    } catch (IOException | ParseException | InterruptedException | SQLException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public int getMessage_count() {
        return message_count;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
    }

    private void initialize() throws IOException, ParseException {
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String day=sdf.format(date);
        message_count=chat.chat_history(day,day,name).length();
        System.out.println("New Message Reminder Initialized");
    }
    public void end(){
        this.flag=false;
    }
    public void reset(){
        this.flag=true;
    }
}
