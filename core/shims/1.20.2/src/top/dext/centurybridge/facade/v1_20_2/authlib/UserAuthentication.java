package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Map;

/** authlib 4.0.43's com.mojang.authlib.UserAuthentication (gone in 5.0.47 with the username/password login it served) */
public interface UserAuthentication {
    boolean canLogIn();

    void logIn() throws AuthenticationException;

    void logOut();

    boolean isLoggedIn();

    boolean canPlayOnline();

    GameProfile[] getAvailableProfiles();

    GameProfile getSelectedProfile();

    void selectGameProfile(GameProfile profile) throws AuthenticationException;

    void loadFromStorage(Map<String, Object> credentials);

    Map<String, Object> saveForStorage();

    void setUsername(String username);

    void setPassword(String password);

    String getAuthenticatedToken();

    String getUserID();

    PropertyMap getUserProperties();

    UserType getUserType();
}
