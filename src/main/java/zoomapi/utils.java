package zoomapi;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuth20Service;


import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import okhttp3.*;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zoomapi.components.Entity.Credential;
import zoomapi.components.Utils.SqliteUtils;
import zoomapi.components.user;

public class utils{

    static String baseUrl="https://api.zoom.us/v2";

    public static Properties getProperties() throws FileNotFoundException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(new File(".\\src\\main\\java\\zoomapi\\resources.properties"));
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String get_request(String url, HashMap<String, String> data,
                              HashMap<String, String> headers, String cookie, String token){
        Throttle.getInstance().ThrottleOperation();
        final String[] json = new String[1];
        try{
            String urlString=baseUrl+url;
            if(data!=null){
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("?");
                for (String key:data.keySet()) {
                    stringBuilder.append(key);
                    stringBuilder.append("=");
                    stringBuilder.append(data.get(key));
                    stringBuilder.append("&");
                }
                String body = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
                //System.out.println(body);
                urlString+=body;
            }
            //System.out.println(urlString);
            URL link = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            if(headers==null){
                conn.setRequestProperty("Authorization","Bearer "+token);
            }
            conn.setRequestProperty("Content-Type","application/json");
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                //System.out.println("rsp:" + sb.toString());
                is.close();
                json[0] =sb.toString();
            } else {
                System.out.println("rsp code:" + conn.getResponseCode()+conn.getResponseMessage());
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json[0];
    }

    public static String post_request(String url, HashMap<String,String> data,
                                   HashMap<String,String> headers, String cookie, String token){
        Throttle.getInstance().ThrottleOperation();
        String urlString=baseUrl+url;
        final String[] json = new String[1];
        try {
            //System.out.println("data:"+data.keySet());
            //System.out.println(urlString);
            URL link = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if(headers==null){
                conn.setRequestProperty("Authorization","Bearer "+token);
            }
            conn.setRequestProperty("Content-Type","application/json");
            StringBuilder stringBuilder=new StringBuilder();
            //System.out.println("data:"+data.keySet());
            if(data!=null){
                stringBuilder.append("{");
                //System.out.println("data:"+data.keySet());
                for (String key:data.keySet()) {
                    if(isAlp(data.get(key))){//sometimes need to put integer or double in the hashmap
                        stringBuilder.append("\"");
                        stringBuilder.append(key);
                        stringBuilder.append("\": \"");
                        stringBuilder.append(data.get(key));
                        stringBuilder.append("\"");
                        stringBuilder.append(",");
                    }
                    else {
                        stringBuilder.append("\"");
                        stringBuilder.append(key);
                        stringBuilder.append("\": ");
                        stringBuilder.append(data.get(key));
                        stringBuilder.append(",");
                    }
                }
                String body = stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("}").toString();
                //System.out.println("body: "+body);
                DataOutputStream dataOutputStream=new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(body);
                dataOutputStream.flush();
                dataOutputStream.close();
            }
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_CREATED||conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                //System.out.println("rsp:" + sb.toString());
                //System.out.println(sb.toString());
                json[0] =sb.toString();
                is.close();

            } else {
                System.out.println("rsp code:" + conn.getResponseCode()+conn.getResponseMessage());
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json[0];
    }
    public static String patch_request(String url, HashMap<String,String> data,
                                   HashMap<String,String> headers, String cookie,String token){
        Throttle.getInstance().ThrottleOperation();
        String urlString=baseUrl+url;
        final String[] json = new String[1];
        OkHttpClient okHttpClient = new OkHttpClient();

        StringBuilder stringBuilder=new StringBuilder();
        if(data!=null){
                stringBuilder.append("{");
                for (String key:data.keySet()) {
                    if(isAlp(data.get(key))){
                        stringBuilder.append("\"");
                        stringBuilder.append(key);
                        stringBuilder.append("\": \"");
                        stringBuilder.append(data.get(key));
                        stringBuilder.append("\"");
                        stringBuilder.append(",");
                    }
                    else {
                        stringBuilder.append("\"");
                        stringBuilder.append(key);
                        stringBuilder.append("\": ");
                        stringBuilder.append(data.get(key));
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("}");
        }
        String requestMsg=stringBuilder.toString();
        RequestBody body = FormBody.create(MediaType.parse("application/json"), requestMsg);
        //RequestBody formBody = builder.build();

        Request request = new Request.Builder().addHeader("Authorization","Bearer "+token)
                .addHeader("Content-Type","application/json")
                .url(urlString)
                .patch(body)
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            //System.out.println(response.code());
            json[0] = String.valueOf(response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json[0];
    }
    public static String delete_request(String url, HashMap<String,String> data,
                                        HashMap<String,String> headers, String cookie,String token){
        Throttle.getInstance().ThrottleOperation();

        final String[] json = new String[1];
        try {
            String urlString=baseUrl+url;
            if(data!=null){
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("?");
                for (String key:data.keySet()) {
                    stringBuilder.append(key);
                    stringBuilder.append("=");
                    stringBuilder.append(data.get(key));
                    stringBuilder.append("&");

                }
                String body = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
                //System.out.println(body);
                urlString+=body;
            }
            URL link = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            if(headers==null){
                conn.setRequestProperty("Authorization","Bearer "+token);
            }
            conn.setRequestProperty("Content-Type","application/json");
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                //System.out.println("rsp code:" + conn.getResponseCode());
                is.close();
                json[0] =String.valueOf(conn.getResponseCode());
            } else {
                json[0] = conn.getResponseCode() +conn.getResponseMessage();
                System.out.println("rsp code:" + conn.getResponseCode()+conn.getResponseMessage());
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json[0];
    }
    public static String put_request(String url, HashMap<String,String> data,
                                   HashMap<String,String> headers, String cookie, String token){
        Throttle.getInstance().ThrottleOperation();
        String urlString=baseUrl+url;
        final String[] json = new String[1];
        try {
            //System.out.println(urlString);
            URL link = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            if(headers==null){
                conn.setRequestProperty("Authorization","Bearer "+token);
            }
            conn.setRequestProperty("Content-Type","application/json");
            if(data!=null){
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("{");
                for (String key:data.keySet()) {
                    if(isAlp(data.get(key))){
                        stringBuilder.append("\"");
                        stringBuilder.append(key);
                        stringBuilder.append("\": \"");
                        stringBuilder.append(data.get(key));
                        stringBuilder.append("\"");
                        stringBuilder.append(",");
                    }
                    else {
                        stringBuilder.append("\"");
                        stringBuilder.append(key);
                        stringBuilder.append("\": ");
                        stringBuilder.append(data.get(key));
                        stringBuilder.append(",");
                    }
                }
                String body = stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("}").toString();
                DataOutputStream dataOutputStream=new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(body);
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                //System.out.println("rsp code:" + conn.getResponseCode());
                is.close();
                json[0] =sb.toString();
            } else {
                System.out.println("rsp code:" + conn.getResponseCode()+conn.getResponseMessage());
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json[0];
    }

    public static String get_oauth_token(String client_id,String client_secret,
                                         String redirect_url, int port) throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        final String NETWORK_NAME = "Zoom";
        final OAuth20Service service = new ServiceBuilder(client_id)
                .apiSecret(client_secret)
                .callback(redirect_url)
                .build(ZoomApi20.instance());
        final Scanner in = new Scanner(System.in, "UTF-8");

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();
        System.out.println("Fetching the Authorization URL...");
        final Map<String, String> additionalParams = new HashMap<>();

        final String authorizationUrl = service.createAuthorizationUrlBuilder()
                .additionalParams(additionalParams)
                .build();
        Desktop desktop=Desktop.getDesktop();
        desktop.browse(new URI(authorizationUrl));
        System.out.println("before accept");
        ServerSocket server = new ServerSocket(port);
        Socket socket = server.accept();

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line = null;
        //System.out.println(line);
        // 进行输出
        while ((line = br.readLine()) != null) {
            if(line.contains("code")){
                System.out.println("-----real url is-----");
                System.out.println(line);
                break;
            }
        }

        server.close();

        String first=line.split("=")[1];
        String second=first.split(" ")[0];
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        System.out.println();

        System.out.println("Trading the Authorization Code for an Access Token...");
        OAuth2AccessToken accessToken = service.getAccessToken(second);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");

        return accessToken.getAccessToken();
    }

    public static String existToken(SqliteUtils sqliteUtils, String client_id) throws SQLException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        List<Credential> list =sqliteUtils.query("select * from Credential where client_id='"+client_id+"'",null,
                Credential.class);
        if(list.size()!=0){
            String token=list.get(0).getAccess_token();
            System.out.println(token);
            return token;
        }
        return null;
    }
    public static boolean verifyToken(String access_token){//available return true
        JSONObject jsonObject=user.get(null,access_token,"me");
        return jsonObject != null;
    }
    public static boolean isAlp(String str){
        for(int i=str.length();--i>=0;){
            if (!Character.isAlphabetic(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
    public static JSONArray joinJSONArray(JSONArray mData, JSONArray array) {
        StringBuffer buffer = new StringBuffer();
        try {
            int len = mData.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) mData.get(i);
                if (i == len - 1)
                    buffer.append(obj1.toString());
                else
                    buffer.append(obj1.toString()).append(",");
            }
            len = array.length();
            if (len > 0)
                buffer.append(",");
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) array.get(i);
                if (i == len - 1)
                    buffer.append(obj1.toString());
                else
                    buffer.append(obj1.toString()).append(",");
            }
            buffer.insert(0, "[").append("]");
            return new JSONArray(buffer.toString());
        } catch (Exception e) {
        }
        return null;
    }
}
