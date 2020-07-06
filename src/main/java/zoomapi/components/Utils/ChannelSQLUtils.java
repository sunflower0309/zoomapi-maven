package zoomapi.components.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.components.Entity.Channel;
import zoomapi.components.chat_channels;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ChannelSQLUtils extends SqliteUtils<Channel> {

    public void refresh(String token) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException {
        executeSQL("delete from Channel");
        chat_channels chat_channels=new chat_channels(token);
        JSONArray newList=Channel.getChannel(chat_channels.list_channels(null));
        for (Object o:newList){
            Channel channel=Channel.JsonToChannel((JSONObject) o);
            insert("Channel",channel);
        }
    }

    @Override
    public List<Channel> query(String sql, List<Object> params, Class cls) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        return super.query(sql, params, cls);
    }
    public void refresh(JSONArray refreshList) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException {
        executeSQL("delete from Channel");
        for (Object o:refreshList){
            Channel channel=Channel.JsonToChannel((JSONObject) o);
            insert("Channel",channel);
        }
    }
}
