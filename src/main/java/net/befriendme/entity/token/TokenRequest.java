package net.befriendme.entity.token;

import java.io.Serializable;

public class TokenRequest implements Serializable {

    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String clientSecret;

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public static class Builder {
        private TokenRequest tokenRequest = new TokenRequest();

        public Builder grantType(String grantType) {
            tokenRequest.grantType = grantType;
            return this;
        }

        public Builder code(String code) {
            tokenRequest.code = code;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            tokenRequest.redirectUri = redirectUri;
            return this;
        }

        public Builder clientId(String clientId) {
            tokenRequest.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            tokenRequest.clientSecret = clientSecret;
            return this;
        }

        // Other builder methods for additional fields

        public TokenRequest build() {
            return tokenRequest;
        }
    }

}
