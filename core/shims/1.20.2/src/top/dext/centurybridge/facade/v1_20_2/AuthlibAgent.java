package top.dext.centurybridge.facade.v1_20_2;

/**
 * authlib 4.0.43's com.mojang.authlib.Agent (1.20.1), gone from 5.0.47 (1.20.2) with the legacy login it served: a game
 * name and version, MINECRAFT ("Minecraft", 1) and SCROLLS ("Scrolls", 1). 5.0.47's profile repository no longer takes an
 * agent; a 4.0.43 lookup always named MINECRAFT (YggdrasilGameProfileRepository looked up Minecraft profiles for it).
 */
public class AuthlibAgent {
    public static final AuthlibAgent MINECRAFT = new AuthlibAgent("Minecraft", 1);
    public static final AuthlibAgent SCROLLS = new AuthlibAgent("Scrolls", 1);
    private final String name;
    private final int version;

    public AuthlibAgent(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Agent{name='" + name + "', version=" + version + "}";
    }
}
