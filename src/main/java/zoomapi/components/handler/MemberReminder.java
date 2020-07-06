package zoomapi.components.handler;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.components.Entity.Channel;
import zoomapi.components.Entity.ChannelMembers;
import zoomapi.components.Utils.ChannelMembersSQLUtils;
import zoomapi.components.Utils.ChannelSQLUtils;
import zoomapi.components.chat_channels;
import zoomapi.components.reminder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MemberReminder extends Thread{
    private chat_channels chat_channels;
    private HashMap<String,HashMap<String,String>> curr_mems;
    private HashMap<String,HashMap<String,String>> new_mems;
    private boolean flag;
    private boolean changed;
    private ChannelSQLUtils channelSQLUtils;
    private ChannelMembersSQLUtils channelMembersSQLUtils;
    public MemberReminder(chat_channels chat_channels) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        this.chat_channels=chat_channels;
        this.flag=true;
        this.changed=false;
        this.channelMembersSQLUtils=new ChannelMembersSQLUtils();
        this.channelSQLUtils=new ChannelSQLUtils();

    }
    public void execute_reminder(reminder reminder){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag){
                    try {
                        Thread.sleep(30000);
                        initialize();
                        List<JSONArray> refreshList=getnew();
                        compare_channel(curr_mems,new_mems,reminder);
                        if(changed){
                            System.out.println("Changed!");
                            channelSQLUtils.refresh(refreshList.get(0));
                            channelMembersSQLUtils.refreshAll(refreshList);
                        }
                        else System.out.println("Not Changed!");
                        changed=false;
                    } catch (InterruptedException | IOException | IllegalAccessException | SQLException | ClassNotFoundException | NoSuchFieldException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void initialize() throws IOException, ClassNotFoundException, SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        curr_mems=new HashMap<>();
        List<Channel> channelList=channelSQLUtils.query("select * from Channel",null,Channel.class);
        for (Channel channel : channelList) {
            HashMap<String, String> temp = new HashMap<>();
            List<ChannelMembers> membersList = channelMembersSQLUtils.query("select * from ChannelMembers where channel_id='"
                    + channel.getId() + "'", null, ChannelMembers.class);
            for (ChannelMembers channelMembers : membersList) {
                temp.put(channelMembers.getId(), channelMembers.getEmail());
            }
            //System.out.println(temp);
            curr_mems.put(channel.getId(), temp);
        }
        System.out.println("Member Reminder Initialized");
    }

    private List<JSONArray> getnew() throws IOException {
        new_mems=new HashMap<>();
        List<JSONArray> refreshArray=new ArrayList<>();
        JSONArray cha=chat_channels.list_channels(null).getJSONArray("channels");
        refreshArray.add(cha);
        for(int i=0;i<cha.length();i++){
            HashMap<String,String> temp=new HashMap<>();
            JSONArray members=chat_channels.list_members(null,cha.getJSONObject(i).getString("id")).getJSONArray("members");
            refreshArray.add(members);
            for(int k=0;k<members.length();k++){
                temp.put(members.getJSONObject(k).getString("id"),
                       members.getJSONObject(k).getString("email"));
            }
            new_mems.put(cha.getJSONObject(i).getString("id"),temp);
        }
        return refreshArray;
    }

    private void compare_channel(HashMap<String,HashMap<String,String>> curr_mems,
                                 HashMap<String,HashMap<String,String>> new_mems,reminder reminder){
        for(String key:curr_mems.keySet()){//channel in now but not in new list was deleted
            if(!new_mems.containsKey(key)){
                System.out.println("deleted!");
                reminder.addChannelChange(key,"deleted");//callback
                changed=true;
            }
        }
        for(String key:new_mems.keySet()){//channel in new but not in now list was created
            if(!curr_mems.containsKey(key)){
                System.out.println("added!");
                reminder.addChannelChange(key,"added");//callback
                changed=true;
            }
            else compare_members(key,curr_mems.get(key),new_mems.get(key),reminder);//both in old and new
        }
    }

    private void compare_members(String channel_id,HashMap<String,String> cha_now,
                                 HashMap<String,String> cha_new,reminder reminder){
        HashMap<String,String> mem_change=new HashMap<>();
        for(String id:cha_now.keySet()){//member in now but not in new list leaved the channel
            if(!cha_new.containsKey(id)){
                mem_change.put(cha_now.get(id),"leave");//callback
                changed=true;
            }
        }
        for(String id:cha_new.keySet()){//member in new but not in now list joined the channel
            if(!cha_now.containsKey(id)){
                mem_change.put(cha_new.get(id),"join");//callback
                changed=true;
            }
        }
        if(mem_change.size()>0) reminder.addMemChange(channel_id,mem_change);
    }
    public void end(){
        this.flag=false;
    }
    public void reset(){
        this.flag=true;
    }
}
