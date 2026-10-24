package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.properties.PropertyMap;

/** authlib 4.0.43's com.mojang.authlib.yggdrasil.response.User (gone in 5.0.47): the account in a login answer */
public class User {
    private String id;
    private PropertyMap properties;

    public User() {
    }

    public String getId() {
        return id;
    }

    public PropertyMap getProperties() {
        return properties;
    }
}
