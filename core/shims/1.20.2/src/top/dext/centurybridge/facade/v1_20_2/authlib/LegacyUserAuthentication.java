package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import top.dext.centurybridge.rt.v1_20_2.CbLibAuthlib;

/** authlib 4.0.43's com.mojang.authlib.legacy.LegacyUserAuthentication (gone in 5.0.47): login.minecraft.net's form login */
public class LegacyUserAuthentication extends HttpUserAuthentication {
    private static final URL AUTHENTICATION_URL = HttpAuthenticationService.constantURL("https://login.minecraft.net");
    private static final int AUTHENTICATION_VERSION = 14;
    private static final int RESPONSE_PART_PROFILE_NAME = 2;
    private static final int RESPONSE_PART_SESSION_TOKEN = 3;
    private static final int RESPONSE_PART_PROFILE_ID = 4;
    private String sessionToken;

    protected LegacyUserAuthentication(LegacyAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }
        Map<String, Object> args = new HashMap<>();
        args.put("user", getUsername());
        args.put("password", getPassword());
        args.put("version", AUTHENTICATION_VERSION);
        String response;
        try {
            response = getAuthenticationService().performPostRequest(AUTHENTICATION_URL, HttpAuthenticationService.buildQuery(args),
                "application/x-www-form-urlencoded").trim();
        } catch (IOException e) {
            throw new AuthenticationException("Authentication server is not responding", e);
        }
        String[] split = response.split(":");
        if (split.length == 5) {
            String profileId = split[RESPONSE_PART_PROFILE_ID];
            String profileName = split[RESPONSE_PART_PROFILE_NAME];
            String sessionToken = split[RESPONSE_PART_SESSION_TOKEN];
            if (StringUtils.isBlank(profileId) || StringUtils.isBlank(profileName) || StringUtils.isBlank(sessionToken)) {
                throw new AuthenticationException("Unknown response from authentication server: " + response);
            }
            setSelectedProfile(new GameProfile(CbLibAuthlib.fromString(profileId), profileName));
            this.sessionToken = sessionToken;
            setUserType(UserType.LEGACY);
        } else {
            throw new InvalidCredentialsException(response);
        }
    }

    @Override
    public void logOut() {
        super.logOut();
        sessionToken = null;
    }

    @Override
    public boolean canPlayOnline() {
        return isLoggedIn() && getSelectedProfile() != null && getAuthenticatedToken() != null;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        if (getSelectedProfile() != null) {
            return new GameProfile[] {getSelectedProfile()};
        }
        return new GameProfile[0];
    }

    @Override
    public void selectGameProfile(GameProfile profile) throws AuthenticationException {
        throw new UnsupportedOperationException("Game profiles cannot be changed in the legacy authentication service");
    }

    @Override
    public String getAuthenticatedToken() {
        return sessionToken;
    }

    @Override
    public String getUserID() {
        return getUsername();
    }

    @Override
    public LegacyAuthenticationService getAuthenticationService() {
        return (LegacyAuthenticationService) super.getAuthenticationService();
    }
}
