package zoomapi.bot;
import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.components.Entity.Channel;
import zoomapi.components.Entity.ChannelMembers;
import zoomapi.components.Entity.ChatMessageEntity;
import zoomapi.components.Entity.Credential;
import zoomapi.components.Utils.ChannelMembersSQLUtils;
import zoomapi.components.Utils.ChannelSQLUtils;
import zoomapi.components.Utils.ChatMessageSQLUtils;
import zoomapi.components.Utils.SqliteUtils;
import zoomapi.components.chat;
import zoomapi.components.chat_channels;
import zoomapi.components.handler.ChatReminder;
import zoomapi.components.handler.MemberReminder;
import zoomapi.components.handler.UpdateReminder;
import zoomapi.components.reminder;
import zoomapi.oauth_client;
import zoomapi.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class botm2{
    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException, URISyntaxException,
            ParseException, SQLException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException, NoSuchFieldException, InstantiationException {


        //------------------------------------------------------------
        SqliteUtils sqliteUtils=new SqliteUtils();
        //get exist token or insert
        String token=utils.existToken(sqliteUtils,zoomapi.utils.getProperties().getProperty("client_id"));
        if(token==null){
            Credential credential=new Credential();
            credential.setAccess_token("initial");
            credential.setClient_id(zoomapi.utils.getProperties().getProperty("client_id"));
            sqliteUtils.insert("Credential",credential);
        }
        oauth_client oauth_client=new oauth_client(zoomapi.utils.getProperties().getProperty("client_id"),
                zoomapi.utils.getProperties().getProperty("client_secret"),4100,
                token, "http://dae70cd0f934.ngrok.io");
        //if old token unavailable, refresh token
        if(!utils.verifyToken(oauth_client.getAccess_token())){
            oauth_client.setAccess_token(utils.get_oauth_token(oauth_client.getClient_id(),oauth_client.getClient_secret(),
                    oauth_client.getRedirect_url(),oauth_client.getPort()));
            Credential credential=new Credential();
            credential.setClient_id(oauth_client.getClient_id());
            credential.setAccess_token(oauth_client.getAccess_token());
            sqliteUtils.insert("Credential",credential);
        }
        //----------------------------------------------------------------------------------------
        //channel 开局更新 出错更新 reminder更新
        ChannelSQLUtils channelSQLUtils=new ChannelSQLUtils();
        channelSQLUtils.refresh(oauth_client.getAccess_token());
        //----------------------------------------------------------------------------------------
        //channelMember 开局更新 出错更新 reminder更新
        ChannelMembersSQLUtils channelMembersSQLUtils=new ChannelMembersSQLUtils();
        channelMembersSQLUtils.refreshAll(oauth_client.getAccess_token());
        //----------------------------------------------------------------------------------------
        //message timestamp 在某个时间段内不更新 //update 在库内则更新 //delete 在库内则更新
        ChatMessageSQLUtils chatMessageSQLUtils=new ChatMessageSQLUtils();
        //----------------------------------------------------------------------------------------
        //initialize all the components
        reminder reminder=new reminder();
        chat_channels chat_channels=new chat_channels(oauth_client.getAccess_token());
        chat chat=new chat(oauth_client.getAccess_token());
//        MemberReminder memberReminder=new MemberReminder(chat_channels);
//        UpdateReminder updateReminder=new UpdateReminder("test",chat);
//        ChatReminder chatReminder=new ChatReminder(chat,"test");
        //----------------------------------------------------------------------------------------
        //reminders
        //memberReminder.execute_reminder(reminder);
        //updateReminder.execute_reminder(reminder);
        //chatReminder.execute_reminder(reminder);
        //----------------------------------------------------------------------------------------
        boolean loop=true;
        while (loop){//set the default channel as test
            Scanner scanner = new Scanner(System.in);
            System.out.println("-----------------------");
            System.out.println("This Bot is based on the test channel.");
            System.out.println("Enter your choice:");
            System.out.println("1: Send a message");
            System.out.println("2: Get message history");
            System.out.println("3: Find message contains keyword");
            System.out.println("4: Exit");
            System.out.println("-----------------------");
            String flag=scanner.nextLine();
            switch (flag){
                case "1":
                    System.out.println("Please enter the message:");
                    String msg=scanner.nextLine();
                    chat.send_msg("test",msg);
                    break;
                case "2":
                    System.out.println("Please enter the start-date, format like YYYY-MM-DD:");
                    String startdate=scanner.nextLine();
                    System.out.println("Please enter the end-date, format like YYYY-MM-DD:");
                    String enddate=scanner.nextLine();
                    JSONArray history=chat.chat_history(startdate,enddate,"test");
                    for(int i=0;i<history.length();i++){
                        System.out.println(history.getJSONObject(i));
                    }
                    break;
                case "3":
                    System.out.println("Please enter the start-date, format like YYYY-MM-DD:");
                    String searchstartdate=scanner.nextLine();
                    System.out.println("Please enter the end-date, format like YYYY-MM-DD:");
                    String searchenddate=scanner.nextLine();
                    System.out.println("Please enter the keyword");
                    String keyword=scanner.nextLine();
                    JSONArray searcharray=chat.search(searchstartdate,searchenddate,"test",
                            (JSONObject j)->j.get("message").toString().contains(keyword));
                    for(int i=0;i<searcharray.length();i++){
                        System.out.println(searcharray.getJSONObject(i));
                    }
                    break;
                case "4":
                    loop=false;
                    break;
            }
        }

//        chat chat=new chat(oauth_client.getAccess_token());
//        //JSONArray jsonArray=chat.chat_history("2020-05-19","2020-05-20","test");
//        chat_channels chat_channels=new chat_channels(oauth_client.getAccess_token());
//        JSONArray channels= Channel.getChannel(chat_channels.list_channels(null));
//        ChannelSQLUtils channelSQLUtils=new ChannelSQLUtils();
//        ChannelMembersSQLUtils channelMembersSQLUtils=new ChannelMembersSQLUtils();
//        List<Channel> channels1=channelSQLUtils.query("select * from Channel",null,Channel.class);
//        for(Channel channel:channels1){
//            JSONArray members=ChannelMembers.getMembers(chat_channels.list_members(null,channel.getId()));
//            for(Object k:members){
//                ChannelMembers channelMembers=ChannelMembers.jsonToChannelMembers((JSONObject)k,channel.getId());
//                channelMembersSQLUtils.insert("ChannelMembers",channelMembers);
//            }
//        }

    }

}
