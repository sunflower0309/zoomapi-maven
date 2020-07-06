package zoomapi.components.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.components.Entity.Channel;
import zoomapi.components.Entity.ChannelMembers;
import zoomapi.components.chat_channels;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChannelMembersSQLUtils extends SqliteUtils<ChannelMembers> {
    private static final String TYPE_INT = "int";
    private static final String TYPE_STRING = "String";
    private static final String TABLE_ID = "id";
    private static final String DB_FILE="zoomapi.db";
    @Override
    public void newTable(String tableName, Class cls) throws SQLException, ClassNotFoundException {
        Field[] fs=cls.getDeclaredFields();
        String sql = "create table if not exists " + tableName + "(";
        for (Field f : fs) {
            switch (f.getType().getSimpleName()) {
                case TYPE_INT:
                    sql +=  f.getName() + " integer,";
                    break;
                case TYPE_STRING:
                    sql +=  f.getName() + " text,";

                    break;
            }
        }
        sql+="primary key(id,channel_id)";
        sql += ")";
        System.out.println(sql);
        executeSQL(sql);
    }

    public void refreshAll(String token) throws IOException, ClassNotFoundException, SQLException,
            NoSuchFieldException, InstantiationException, IllegalAccessException {
        ChannelSQLUtils channelSQLUtils=new ChannelSQLUtils();
        List<Channel> channelList=channelSQLUtils.query("select * from Channel",null,Channel.class);
        chat_channels chat_channels=new chat_channels(token);
        executeSQL("delete from ChannelMembers");
        for (Channel channel:channelList){
            JSONObject jsonObject=chat_channels.list_members(null,channel.getId());
            JSONArray newList=ChannelMembers.getMembers(jsonObject);
            for(Object obj:newList){
                ChannelMembers channelMembers=ChannelMembers.jsonToChannelMembers((JSONObject)obj,channel.getId());
                insert("ChannelMembers",channelMembers);
            }
        }

    }

    public void refreshAll(List<JSONArray> refreshList) throws IllegalAccessException, SQLException, ClassNotFoundException {
        executeSQL("delete from ChannelMembers");
        for(int i=1;i<refreshList.size();i++){//第一个jsonarray是channels
            for(int k=0;k<refreshList.get(i).length();k++){
                ChannelMembers channelMembers=ChannelMembers.jsonToChannelMembers((JSONObject)refreshList.get(i).getJSONObject(k),
                        Channel.JsonToChannel(refreshList.get(0).getJSONObject(i-1)).getId());
                insert("ChannelMembers",channelMembers);
            }
        }
    }

    public void delete(ChannelMembers channelMembers) throws SQLException, ClassNotFoundException {
        executeSQL("delete from ChannelMembers where id='"+channelMembers.getId()+"' and channelId='"+
                channelMembers.getChannel_id()+"'");
    }


}
