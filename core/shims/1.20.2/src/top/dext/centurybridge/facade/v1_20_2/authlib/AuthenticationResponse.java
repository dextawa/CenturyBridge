package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.GameProfile;

/** authlib 4.0.43's com.mojang.authlib.yggdrasil.response.AuthenticationResponse (gone in 5.0.47): the /authenticate answer */
public class AuthenticationResponse extends Response {
    private String accessToken;
    private String clientToken;
    private GameProfile selectedProfile;
    private GameProfile[] availableProfiles;
    private User user;

    public AuthenticationResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public GameProfile[] getAvailableProfiles() {
        return availableProfiles;
    }

    public GameProfile getSelectedProfile() {
        return selectedProfile;
    }

    public User getUser() {
        return user;
    }
}
