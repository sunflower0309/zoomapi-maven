package zoomapi;

import zoomapi.components.chat_channels;
import zoomapi.components.chat_messages;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class oauth_client {
    private String client_id;
    private String client_secret;
    private int port;
    private String redirect_url;
    private String access_token;
    private zoomapi.components.chat_channels chat_channels;
    private zoomapi.components.chat_messages chat_messages;
    public oauth_client(String client_id,String client_secret,int port,
                        String redirect_url) throws InterruptedException, IOException, ExecutionException, URISyntaxException {

        this.client_id=client_id;
        this.client_secret=client_secret;
        this.port=port;
        this.redirect_url=redirect_url;
        this.access_token=zoomapi.utils.get_oauth_token(zoomapi.utils.getProperties().getProperty("client_id"),
                zoomapi.utils.getProperties().getProperty("client_secret"),this.redirect_url,4100);
        this.chat_channels=new chat_channels(this.access_token);
        this.chat_messages=new chat_messages(this.access_token);
    }

    public oauth_client(String client_id,String client_secret,int port,String access_token,
                        String redirect_url) throws InterruptedException, IOException, ExecutionException, URISyntaxException {

        this.client_id=client_id;
        this.client_secret=client_secret;
        this.port=port;
        this.redirect_url=redirect_url;
        this.access_token=access_token;
        this.chat_channels=new chat_channels(this.access_token);
        this.chat_messages=new chat_messages(this.access_token);
    }

    public int getPort() {
        return port;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
