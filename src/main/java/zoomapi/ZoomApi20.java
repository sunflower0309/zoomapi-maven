package zoomapi;

import com.github.scribejava.apis.openid.OpenIdJsonTokenExtractor;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

public class ZoomApi20 extends DefaultApi20 {
    protected ZoomApi20() {
    }

    public static zoomapi.ZoomApi20 instance() {
        return zoomapi.ZoomApi20.InstanceHolder.INSTANCE;
    }

    public String getAccessTokenEndpoint() {
        return "https://zoom.us/oauth/token";
    }

    protected String getAuthorizationBaseUrl() {
        return "https://zoom.us/oauth/authorize";
    }

    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OpenIdJsonTokenExtractor.instance();
    }

    public String getRevokeTokenEndpoint() {
        return "https://zoom.us/oauth/revoke";
    }

    private static class InstanceHolder {
        private static final zoomapi.ZoomApi20 INSTANCE = new zoomapi.ZoomApi20();

        private InstanceHolder() {
        }
    }
}
