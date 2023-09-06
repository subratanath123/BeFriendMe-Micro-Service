package net.befriendme.entity.idp;

import net.befriendme.entity.idp.IdpProvider;
import net.befriendme.entity.token.TokenResponse;

import java.io.Serializable;

public class IdpUser implements Serializable {

    private String email;

    private IdpProvider idpProvider;

    private TokenResponse tokenInfo;

    private String profilePictureUrl;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public IdpProvider getIdpProvider() {
        return idpProvider;
    }

    public void setIdpProvider(IdpProvider idpProvider) {
        this.idpProvider = idpProvider;
    }

    public TokenResponse getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenResponse tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
