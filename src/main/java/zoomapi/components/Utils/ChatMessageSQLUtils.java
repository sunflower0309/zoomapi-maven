package zoomapi.components.Utils;

import zoomapi.components.Entity.ChatMessageEntity;

import java.sql.SQLException;
import java.util.List;

public class ChatMessageSQLUtils extends SqliteUtils<ChatMessageEntity> {

    public List<ChatMessageEntity> selectAll() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        return super.query("select * from Message", null, ChatMessageEntity.class);
    }
    public boolean exist(ChatMessageEntity chatMessageEntity) throws ClassNotFoundException, SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        return query("select * from Message where channel_id='"+chatMessageEntity.getChannel_id()+"' and id='"+
                chatMessageEntity.getId()+"'",null,ChatMessageEntity.class).size()!=0;
    }
}
