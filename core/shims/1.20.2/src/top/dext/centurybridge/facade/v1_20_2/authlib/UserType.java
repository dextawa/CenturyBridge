package top.dext.centurybridge.facade.v1_20_2.authlib;

import java.util.HashMap;
import java.util.Map;

/** authlib 4.0.43's com.mojang.authlib.UserType (gone in 5.0.47) */
public enum UserType {
    LEGACY("legacy"),
    MOJANG("mojang");

    private static final Map<String, UserType> BY_NAME = new HashMap<>();
    private final String name;

    static {
        for (UserType type : values()) {
            BY_NAME.put(type.name, type);
        }
    }

    UserType(String name) {
        this.name = name;
    }

    public static UserType byName(String name) {
        return BY_NAME.get(name);
    }

    public String getName() {
        return name;
    }
}
