package zoomapi.components;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.oauth_client;
import zoomapi.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class chat {
    private String token;
    private chat_channels chat_channels;
    private chat_messages chat_messages;
    public chat(String token){
        this.token=token;
        this.chat_channels=new chat_channels(this.token);
        this.chat_messages=new chat_messages(this.token);
    }

    public String getToken() {
        return token;
    }

    public void send_msg(String name, String message) throws IOException {
        HashMap<String,String> map_cha_list=new HashMap<String,String>();
        JSONObject channel_json=chat_channels.list_channels(map_cha_list);
        JSONArray channel_list=new JSONArray(channel_json.get("channels").toString());
        while (!channel_json.get("next_page_token").toString().equals("")){
            map_cha_list.put("next_page_token",channel_json.get("next_page_token").toString());
            channel_json=chat_messages.list(map_cha_list,"me");
            JSONArray jsonArray=new JSONArray(channel_json.get("channels").toString());
            channel_list= utils.joinJSONArray(channel_list,jsonArray);
        }
        String channelid=null;
        for(int i=0;i<channel_list.length();i++){
            JSONObject jsonObject= (JSONObject) channel_list.get(i);
            if(jsonObject.get("name").toString().equals(name)){
                channelid=jsonObject.get("id").toString();
            }
        }
        if(channelid==null){
            System.out.println("can't find this channel");
            return;
        }
        HashMap<String,String> msg=new HashMap<String,String>();
        msg.put("to_channel","\""+channelid+"\"");
        if(!utils.isAlp(message)){//make it like String in json
            message="\""+message+"\"";
        }
        msg.put("message",message);
        chat_messages.post(msg);
    }
    
    public JSONArray chat_history(String start, String end,String name) throws IOException, ParseException {
        String real_end=change_day(end,1);
        HashMap<String,String> map_cha_list=new HashMap<String,String>();
        JSONObject channel_json=chat_channels.list_channels(map_cha_list);
        JSONArray channel_list=new JSONArray(channel_json.get("channels").toString());
        while (!channel_json.get("next_page_token").toString().equals("")){
            map_cha_list.put("next_page_token",channel_json.get("next_page_token").toString());
            channel_json=chat_messages.list(map_cha_list,"me");
            JSONArray jsonArray=new JSONArray(channel_json.get("channels").toString());
            channel_list= utils.joinJSONArray(channel_list,jsonArray);
        }
        String channelid=null;
        for(int i=0;i<channel_list.length();i++){
            JSONObject jsonObject= (JSONObject) channel_list.get(i);
            if(jsonObject.get("name").toString().equals(name)){//get channel id
                channelid=jsonObject.get("id").toString();
            }
        }
        if(channelid==null){
            System.out.println("can't find this channel");
            return null;
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        int between_days=daysBetween(sdf.parse(start),sdf.parse(real_end))+1;
        if(between_days>5) between_days=5;//limit max days
        HashMap<String,String> map_msg_list=new HashMap<String,String>();
        map_msg_list.put("to_channel",channelid);
        map_msg_list.put("page_size","50");
        JSONArray msglist=new JSONArray();
        for(int i=0;i<between_days;i++){
            if(i==0){
                //System.out.println("msg_list_size_first:"+msglist.length());
                map_msg_list.put("date",start);
                JSONObject jsonObject=chat_messages.list(map_msg_list,"me");
                JSONArray msglistfirstday=reverse(new JSONArray(jsonObject.get("messages").toString()));
                while (!jsonObject.get("next_page_token").toString().equals("")){
                    map_msg_list.put("next_page_token",jsonObject.get("next_page_token").toString());
                    jsonObject=chat_messages.list(map_msg_list,"me");
                    JSONArray jsonArray=reverse(new JSONArray(jsonObject.get("messages").toString()));
                    msglistfirstday=utils.joinJSONArray(msglistfirstday,jsonArray);
                }
                for(int k=0;k<msglistfirstday.length();k++){//because of the timezone difference,need a filter
                    if(Integer.parseInt((msglistfirstday.getJSONObject(k).get("date_time"))
                            .toString().substring(11,13))>=7&&((msglistfirstday.getJSONObject(k).get("date_time"))
                            .toString().substring(0,10).equals(start))){
                        msglist.put(msglistfirstday.get(k));
                    }
                }
                map_msg_list.remove("next_page_token");
            }
            else if(i==(between_days-1)){//the last day
                map_msg_list.put("date",change_day(start,i));
                JSONObject jsonObject=chat_messages.list(map_msg_list,"me");
                JSONArray msglistlastday=reverse(new JSONArray(jsonObject.get("messages").toString()));
                while (!jsonObject.get("next_page_token").toString().equals("")){
                    map_msg_list.put("next_page_token",jsonObject.get("next_page_token").toString());
                    jsonObject=chat_messages.list(map_msg_list,"me");
                    JSONArray jsonArray=reverse(new JSONArray(jsonObject.get("messages").toString()));
                    msglistlastday=utils.joinJSONArray(msglistlastday,jsonArray);
                }
                for(int k=0;k<msglistlastday.length();k++){//filter
                    if(Integer.parseInt((msglistlastday.getJSONObject(k).get("date_time"))
                            .toString().substring(11,13))<7&&((msglistlastday.getJSONObject(k).get("date_time"))
                            .toString().substring(0,10).equals(real_end))){
                        msglist.put(msglistlastday.get(k));
                    }
                }
                map_msg_list.remove("next_page_token");
            }
            else {
                map_msg_list.put("date",change_day(start,i));
                JSONObject jsonObject=chat_messages.list(map_msg_list,"me");
                JSONArray msglistmidday=reverse(new JSONArray(jsonObject.get("messages").toString()));
                while (!jsonObject.get("next_page_token").toString().equals("")){
                    map_msg_list.put("next_page_token",jsonObject.get("next_page_token").toString());
                    jsonObject=chat_messages.list(map_msg_list,"me");
                    JSONArray jsonArray=reverse(new JSONArray(jsonObject.get("messages").toString()));
                    msglistmidday=utils.joinJSONArray(msglistmidday,jsonArray);
                }
                msglist=utils.joinJSONArray(msglist,msglistmidday);
                map_msg_list.remove("next_page_token");
            }
        }
        return msglist;
    }

    public interface IPredicate{
        boolean match(JSONObject jsonObject);
    }

    public JSONArray search(String start, String end, String name,IPredicate iPredicate) throws IOException, ParseException {
        JSONArray msglist=chat_history(start, end, name);
        JSONArray result=new JSONArray();
        for(int i=0;i<msglist.length();i++){
            JSONObject msg=msglist.getJSONObject(i);
            if(iPredicate.match(msg)) result.put(msg);
        }
        return result;
    }

    public String change_day(String date,int num) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date d =sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DATE,num);
        //System.out.println("date:"+sdf.format(calendar.getTime()));
        return sdf.format(calendar.getTime());
    }

    public int daysBetween(Date smdate,Date bdate) throws ParseException
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        smdate=sdf.parse(sdf.format(smdate));
        bdate=sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    public JSONArray reverse(JSONArray jsonArray){
        JSONArray jsonArray1=new JSONArray();
        for(int i=jsonArray.length()-1;i>=0;i--){
            jsonArray1.put(jsonArray.get(i));
        }
        return jsonArray1;
    }
}
