package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * The conversion-time signal "a converted mod mixed this restored member" (engine MixinRepair.MIXED_DIR): every
 * converted jar whose kept injectors select members this layer restores lists them in
 * META-INF/centurybridge/mixed/1.21.6.txt, "owner.name(desc)", the mixin class and its config section, tab-separated.
 * A route asks it before consulting a restored member, so with no such mixin loaded 1.21.6's own path runs exactly as
 * it is (a client-section mixin does not apply on a dedicated server and is not counted there).
 */
public final class CbMixedMembers {
    private static final String FILE = "META-INF/centurybridge/mixed/1.21.6.txt";
    private static volatile java.util.Set<String> mixed;

    private CbMixedMembers() {
    }

    /** whether a loaded mod's converted mixin injects into this restored member ("owner.name(desc)", intermediary) */
    public static boolean mixed(String key) {
        java.util.Set<String> s = mixed;
        return (s != null ? s : load()).contains(key);
    }

    private static synchronized java.util.Set<String> load() {
        if (mixed != null) {
            return mixed;
        }
        java.util.Set<String> out = new java.util.HashSet<>();
        boolean server = net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType()
            == net.fabricmc.api.EnvType.SERVER;
        try {
            java.util.Enumeration<java.net.URL> e = CbMixedMembers.class.getClassLoader().getResources(FILE);
            while (e.hasMoreElements()) {
                try (java.io.InputStream in = e.nextElement().openStream()) {
                    for (String l : new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).split("\n")) {
                        String[] f = l.split("\t");
                        if (f.length >= 3 && !(server && f[2].trim().equals("client"))) {
                            out.add(f[0]);
                        }
                    }
                }
            }
        } catch (java.io.IOException ex) {
            throw new java.io.UncheckedIOException(ex);
        }
        mixed = out;
        return out;
    }
}
