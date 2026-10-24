package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.rt.v1_20_2.CbLibAuthlib;

/** authlib 4.0.43's com.mojang.authlib.BaseUserAuthentication (gone in 5.0.47), its bytecode's logic over 5.0.47's types */
public abstract class BaseUserAuthentication implements UserAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUserAuthentication.class);
    protected static final String STORAGE_KEY_PROFILE_NAME = "displayName";
    protected static final String STORAGE_KEY_PROFILE_ID = "uuid";
    protected static final String STORAGE_KEY_PROFILE_PROPERTIES = "profileProperties";
    protected static final String STORAGE_KEY_USER_NAME = "username";
    protected static final String STORAGE_KEY_USER_ID = "userid";
    protected static final String STORAGE_KEY_USER_PROPERTIES = "userProperties";
    private final AuthenticationService authenticationService;
    private final PropertyMap userProperties = new PropertyMap();
    private String userid;
    private String username;
    private String password;
    private GameProfile selectedProfile;
    private UserType userType;

    protected BaseUserAuthentication(AuthenticationService authenticationService) {
        Validate.notNull(authenticationService);
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername()) && StringUtils.isNotBlank(getPassword());
    }

    @Override
    public void logOut() {
        password = null;
        userid = null;
        setSelectedProfile(null);
        getModifiableUserProperties().clear();
        setUserType(null);
    }

    @Override
    public boolean isLoggedIn() {
        return getSelectedProfile() != null;
    }

    @Override
    public void setUsername(String username) {
        if (isLoggedIn() && canPlayOnline()) {
            throw new IllegalStateException("Cannot change username whilst logged in & online");
        }
        this.username = username;
    }

    @Override
    public void setPassword(String password) {
        if (isLoggedIn() && canPlayOnline() && StringUtils.isNotBlank(password)) {
            throw new IllegalStateException("Cannot set password whilst logged in & online");
        }
        this.password = password;
    }

    protected String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadFromStorage(Map<String, Object> credentials) {
        logOut();
        setUsername(String.valueOf(credentials.get(STORAGE_KEY_USER_NAME)));
        if (credentials.containsKey(STORAGE_KEY_USER_ID)) {
            userid = String.valueOf(credentials.get(STORAGE_KEY_USER_ID));
        } else {
            userid = username;
        }
        if (credentials.containsKey(STORAGE_KEY_USER_PROPERTIES)) {
            try {
                for (Map<String, String> property : (List<Map<String, String>>) credentials.get(STORAGE_KEY_USER_PROPERTIES)) {
                    String name = property.get("name");
                    String value = property.get("value");
                    String signature = property.get("signature");
                    if (signature == null) {
                        getModifiableUserProperties().put(name, new Property(name, value));
                    } else {
                        getModifiableUserProperties().put(name, new Property(name, value, signature));
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Couldn't deserialize user properties", t);
            }
        }
        if (credentials.containsKey(STORAGE_KEY_PROFILE_NAME) && credentials.containsKey(STORAGE_KEY_PROFILE_ID)) {
            GameProfile profile = CbLibAuthlib.gameProfile(
                CbLibAuthlib.fromString(String.valueOf(credentials.get(STORAGE_KEY_PROFILE_ID))),
                String.valueOf(credentials.get(STORAGE_KEY_PROFILE_NAME)));
            if (credentials.containsKey(STORAGE_KEY_PROFILE_PROPERTIES)) {
                try {
                    for (Map<String, String> property : (List<Map<String, String>>) credentials.get(STORAGE_KEY_PROFILE_PROPERTIES)) {
                        String name = property.get("name");
                        String value = property.get("value");
                        String signature = property.get("signature");
                        if (signature == null) {
                            profile.getProperties().put(name, new Property(name, value));
                        } else {
                            profile.getProperties().put(name, new Property(name, value, signature));
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.warn("Couldn't deserialize profile properties", t);
                }
            }
            setSelectedProfile(profile);
        }
    }

    @Override
    public Map<String, Object> saveForStorage() {
        Map<String, Object> result = new HashMap<>();
        if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }
        if (getUserID() != null) {
            result.put(STORAGE_KEY_USER_ID, getUserID());
        } else if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }
        if (!getUserProperties().isEmpty()) {
            List<Map<String, String>> properties = new ArrayList<>();
            for (Property userProperty : getUserProperties().values()) {
                Map<String, String> property = new HashMap<>();
                property.put("name", userProperty.name());
                property.put("value", userProperty.value());
                property.put("signature", userProperty.signature());
                properties.add(property);
            }
            result.put(STORAGE_KEY_USER_PROPERTIES, properties);
        }
        GameProfile selected = getSelectedProfile();
        if (selected != null) {
            result.put(STORAGE_KEY_PROFILE_NAME, selected.getName());
            result.put(STORAGE_KEY_PROFILE_ID, CbLibAuthlib.getId(selected));
            List<Map<String, String>> properties = new ArrayList<>();
            for (Property profileProperty : selected.getProperties().values()) {
                Map<String, String> property = new HashMap<>();
                property.put("name", profileProperty.name());
                property.put("value", profileProperty.value());
                property.put("signature", profileProperty.signature());
                properties.add(property);
            }
            if (!properties.isEmpty()) {
                result.put(STORAGE_KEY_PROFILE_PROPERTIES, properties);
            }
        }
        return result;
    }

    protected void setSelectedProfile(GameProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    @Override
    public GameProfile getSelectedProfile() {
        return selectedProfile;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().getSimpleName());
        result.append("{");
        if (isLoggedIn()) {
            result.append("Logged in as ");
            result.append(getUsername());
            if (getSelectedProfile() != null) {
                result.append(" / ");
                result.append(getSelectedProfile());
                result.append(" - ");
                if (canPlayOnline()) {
                    result.append("Online");
                } else {
                    result.append("Offline");
                }
            }
        } else {
            result.append("Not logged in");
        }
        result.append("}");
        return result.toString();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Override
    public String getUserID() {
        return userid;
    }

    @Override
    public PropertyMap getUserProperties() {
        if (isLoggedIn()) {
            PropertyMap result = new PropertyMap();
            result.putAll(getModifiableUserProperties());
            return result;
        }
        return new PropertyMap();
    }

    protected PropertyMap getModifiableUserProperties() {
        return userProperties;
    }

    @Override
    public UserType getUserType() {
        if (isLoggedIn()) {
            return userType == null ? UserType.LEGACY : userType;
        }
        return null;
    }

    protected void setUserType(UserType userType) {
        this.userType = userType;
    }

    protected void setUserid(String userid) {
        this.userid = userid;
    }
}
