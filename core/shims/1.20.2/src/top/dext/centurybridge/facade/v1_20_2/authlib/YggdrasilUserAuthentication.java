package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.facade.v1_20_2.AuthlibAgent;
import top.dext.centurybridge.rt.v1_20_2.CbLibAuthlib;
import top.dext.centurybridge.rt.v1_20_2.CbLibAuthlibLogin;

/**
 * authlib 4.0.43's com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication (gone in 5.0.47): the Yggdrasil username/password
 * and access-token login against the environment's auth host (/authenticate, /refresh, /validate), its bytecode's logic over
 * 5.0.47's types. The requests go through CbLibAuthlibLogin.makeRequest, 4.0.43's YggdrasilAuthenticationService.makeRequest.
 */
public class YggdrasilUserAuthentication extends HttpUserAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(YggdrasilUserAuthentication.class);
    private final URL routeAuthenticate;
    private final URL routeRefresh;
    private final URL routeValidate;
    private final URL routeInvalidate;
    private final URL routeSignout;
    private static final String STORAGE_KEY_ACCESS_TOKEN = "accessToken";
    private final AuthlibAgent agent;
    private GameProfile[] profiles;
    private final String clientToken;
    private String accessToken;
    private boolean isOnline;

    public YggdrasilUserAuthentication(YggdrasilAuthenticationService authenticationService, String clientToken, AuthlibAgent agent) {
        this(authenticationService, clientToken, agent, YggdrasilEnvironment.PROD.getEnvironment());
    }

    public YggdrasilUserAuthentication(YggdrasilAuthenticationService authenticationService, String clientToken, AuthlibAgent agent,
            Environment env) {
        super(authenticationService);
        this.clientToken = clientToken;
        this.agent = agent;
        LOGGER.info("Environment: " + CbLibAuthlib.getName(env), ". AuthHost: " + CbLibAuthlib.getAuthHost(env));
        String authHost = CbLibAuthlib.getAuthHost(env);
        routeAuthenticate = HttpAuthenticationService.constantURL(authHost + "/authenticate");
        routeRefresh = HttpAuthenticationService.constantURL(authHost + "/refresh");
        routeValidate = HttpAuthenticationService.constantURL(authHost + "/validate");
        routeInvalidate = HttpAuthenticationService.constantURL(authHost + "/invalidate");
        routeSignout = HttpAuthenticationService.constantURL(authHost + "/signout");
    }

    @Override
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername())
            && (StringUtils.isNotBlank(getPassword()) || StringUtils.isNotBlank(getAuthenticatedToken()));
    }

    @Override
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            logInWithToken();
        } else if (StringUtils.isNotBlank(getPassword())) {
            logInWithPassword();
        } else {
            throw new InvalidCredentialsException("Invalid password");
        }
    }

    protected void logInWithPassword() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }
        LOGGER.info("Logging in with username & password");
        AuthenticationRequest request = new AuthenticationRequest(getAgent(), getUsername(), getPassword(), clientToken);
        AuthenticationResponse response = CbLibAuthlibLogin.makeRequest(getAuthenticationService(), routeAuthenticate, request,
            AuthenticationResponse.class);
        if (!response.getClientToken().equals(clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }
        if (response.getSelectedProfile() != null) {
            setUserType(CbLibAuthlib.isLegacy(response.getSelectedProfile()) ? UserType.LEGACY : UserType.MOJANG);
        } else if (ArrayUtils.isNotEmpty(response.getAvailableProfiles())) {
            setUserType(CbLibAuthlib.isLegacy(response.getAvailableProfiles()[0]) ? UserType.LEGACY : UserType.MOJANG);
        }
        User user = response.getUser();
        if (user != null && user.getId() != null) {
            setUserid(user.getId());
        } else {
            setUserid(getUsername());
        }
        isOnline = true;
        accessToken = response.getAccessToken();
        profiles = response.getAvailableProfiles();
        setSelectedProfile(response.getSelectedProfile());
        getModifiableUserProperties().clear();
        updateUserProperties(user);
    }

    protected void updateUserProperties(User user) {
        if (user == null) {
            return;
        }
        if (user.getProperties() != null) {
            getModifiableUserProperties().putAll(user.getProperties());
        }
    }

    protected void logInWithToken() throws AuthenticationException {
        if (StringUtils.isBlank(getUserID())) {
            if (StringUtils.isBlank(getUsername())) {
                throw new InvalidCredentialsException("Invalid uuid & username");
            }
            setUserid(getUsername());
        }
        if (StringUtils.isBlank(getAuthenticatedToken())) {
            throw new InvalidCredentialsException("Invalid access token");
        }
        LOGGER.info("Logging in with access token");
        if (checkTokenValidity()) {
            LOGGER.debug("Skipping refresh call as we're safely logged in.");
            isOnline = true;
            return;
        }
        RefreshRequest request = new RefreshRequest(getAuthenticatedToken(), clientToken);
        RefreshResponse response = CbLibAuthlibLogin.makeRequest(getAuthenticationService(), routeRefresh, request, RefreshResponse.class);
        if (!response.getClientToken().equals(clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }
        if (response.getSelectedProfile() != null) {
            setUserType(CbLibAuthlib.isLegacy(response.getSelectedProfile()) ? UserType.LEGACY : UserType.MOJANG);
        } else if (ArrayUtils.isNotEmpty(response.getAvailableProfiles())) {
            setUserType(CbLibAuthlib.isLegacy(response.getAvailableProfiles()[0]) ? UserType.LEGACY : UserType.MOJANG);
        }
        if (response.getUser() != null && response.getUser().getId() != null) {
            setUserid(response.getUser().getId());
        } else {
            setUserid(getUsername());
        }
        isOnline = true;
        accessToken = response.getAccessToken();
        profiles = response.getAvailableProfiles();
        setSelectedProfile(response.getSelectedProfile());
        getModifiableUserProperties().clear();
        updateUserProperties(response.getUser());
    }

    protected boolean checkTokenValidity() throws AuthenticationException {
        ValidateRequest request = new ValidateRequest(getAuthenticatedToken(), clientToken);
        try {
            CbLibAuthlibLogin.makeRequest(getAuthenticationService(), routeValidate, request, Response.class);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    @Override
    public void logOut() {
        super.logOut();
        accessToken = null;
        profiles = null;
        isOnline = false;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        return profiles;
    }

    @Override
    public boolean isLoggedIn() {
        return StringUtils.isNotBlank(accessToken);
    }

    @Override
    public boolean canPlayOnline() {
        return isLoggedIn() && getSelectedProfile() != null && isOnline;
    }

    @Override
    public void selectGameProfile(GameProfile profile) throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("Cannot change game profile whilst not logged in");
        }
        if (getSelectedProfile() != null) {
            throw new AuthenticationException("Cannot change game profile. You must log out and back in.");
        }
        if (profile == null || !ArrayUtils.contains(profiles, profile)) {
            throw new IllegalArgumentException("Invalid profile '" + profile + "'");
        }
        RefreshRequest request = new RefreshRequest(getAuthenticatedToken(), clientToken, profile);
        RefreshResponse response = CbLibAuthlibLogin.makeRequest(getAuthenticationService(), routeRefresh, request, RefreshResponse.class);
        if (!response.getClientToken().equals(clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }
        isOnline = true;
        accessToken = response.getAccessToken();
        setSelectedProfile(response.getSelectedProfile());
    }

    @Override
    public void loadFromStorage(Map<String, Object> credentials) {
        super.loadFromStorage(credentials);
        accessToken = String.valueOf(credentials.get(STORAGE_KEY_ACCESS_TOKEN));
    }

    @Override
    public Map<String, Object> saveForStorage() {
        Map<String, Object> result = super.saveForStorage();
        if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            result.put(STORAGE_KEY_ACCESS_TOKEN, getAuthenticatedToken());
        }
        return result;
    }

    @Deprecated
    public String getSessionToken() {
        if (isLoggedIn() && getSelectedProfile() != null && canPlayOnline()) {
            return String.format("token:%s:%s", getAuthenticatedToken(), CbLibAuthlib.getId(getSelectedProfile()));
        }
        return null;
    }

    @Override
    public String getAuthenticatedToken() {
        return accessToken;
    }

    public AuthlibAgent getAgent() {
        return agent;
    }

    @Override
    public String toString() {
        return "YggdrasilAuthenticationService{agent=" + agent + ", profiles=" + Arrays.toString(profiles) + ", selectedProfile="
            + getSelectedProfile() + ", username='" + getUsername() + "', isLoggedIn=" + isLoggedIn() + ", userType=" + getUserType()
            + ", canPlayOnline=" + canPlayOnline() + ", accessToken='" + accessToken + "', clientToken='" + clientToken + "'}";
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }
}
