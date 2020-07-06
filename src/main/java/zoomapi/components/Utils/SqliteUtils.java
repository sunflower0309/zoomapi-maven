package zoomapi.components.Utils;

import javax.naming.Context;
import java.beans.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteUtils<T>{
    private static final String TYPE_INT = "int";
    private static final String TYPE_STRING = "String";
    private static final String TABLE_ID = "id";
    private static final String DB_FILE="zoomapi.db";


    public static Connection getConn() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:"+DB_FILE);
    }

    public void newTable(String tableName,Class cls) throws SQLException, ClassNotFoundException {
        Field[] fs=cls.getDeclaredFields();
        String sql = "create table if not exists " + tableName + "(";
        if (!contain(fs, TABLE_ID)) {
            sql += TABLE_ID + " integer primary key autoincrement";
        }
        for (Field f : fs) {
            if (f.getName().equals(TABLE_ID)) {
                sql += TABLE_ID + " text primary key";
                continue;
            }
            switch (f.getType().getSimpleName()) {
                case TYPE_INT:
                    sql += "," + f.getName() + " integer";
                    break;
                case TYPE_STRING:
                    sql += "," + f.getName() + " text";
                    break;
            }
        }
        sql += ")";
        executeSQL(sql);
    }

    public void insert(String table, T obj) throws SQLException, IllegalAccessException, ClassNotFoundException {
        Class clas=obj.getClass();
        Field[] fs=clas.getDeclaredFields();
        String sql="insert or replace into "+table+" (";
        String vals="(";
        for(Field field:fs){
            switch (field.getType().getSimpleName()){
                case TYPE_INT:
                    field.setAccessible(true);
                    int val=(int)field.get(obj);
                    sql=sql + field.getName()+",";
                    vals=vals+ val +",";
                    break;
                case TYPE_STRING:
                    field.setAccessible(true);
                    String string=(String) field.get(obj);
                    sql=sql + field.getName()+",";
                    vals=vals+"\""+string+"\",";
                    break;
            }
        }
        sql=sql.substring(0,sql.length()-1)+") values ";
        vals=vals.substring(0,vals.length()-1)+")";
        executeSQL(sql+vals);
    }

    public void delete(String table, T obj) throws SQLException, IllegalAccessException, ClassNotFoundException {
        Class clas=obj.getClass();
        Field[] fs=clas.getDeclaredFields();
        String sql="delete from "+table+" where id=";
        for(Field field:fs){
            if (field.getName().equals(TABLE_ID)) {
                field.setAccessible(true);
                sql += (int)field.get(obj);
                break;
            }
        }
        executeSQL(sql);
    }

    public List<T> query(String sql,List<Object> params,Class cls) throws SQLException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchFieldException {
        List<T> list=new ArrayList<T>();
        Connection conn=getConn();
        PreparedStatement stmt = conn.prepareStatement(sql);
        if(params!=null){
            for(int i=0;i<params.size();i++){
                stmt.setObject(i+1,params.get(i));
            }
        }
        ResultSet rs=null;
        ResultSetMetaData rm=null;
        try{
            rs=stmt.executeQuery();
            rm=rs.getMetaData();
            while(rs.next()){
                Object m= cls.newInstance();
                for(int i=0;i<rm.getColumnCount();i++){
                    String columnName=rm.getColumnName(i+1);
                    Object value=rs.getObject(i+1);
                    Field field=cls.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(m,value);
                }
                list.add((T) m);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            rs.close();
            conn.close();
        }
        return list;
    }

    public void executeSQL(String sql) throws SQLException, ClassNotFoundException {
        Connection connection=getConn();
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
        stmt.close();
        connection.close();
    }
    private boolean contain(Field[] tableInfo, String tableId) {
        for (Field field : tableInfo) {
            if (field.getName().equals(tableId)) return true;
        }
        return false;
    }
}
