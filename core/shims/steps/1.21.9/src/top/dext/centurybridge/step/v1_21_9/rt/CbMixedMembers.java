package top.dext.centurybridge.step.v1_21_9.rt;

/**
 * Which members this layer restores a loaded mod's converted mixin injects into: the conversion writes them to
 * META-INF/centurybridge/mixed/1.21.9.txt in each converted jar ("owner.name(desc)", mixin class, config section; smixin3
 * (l), cases_steps/mixed-members.md). A route consults a restored 1.21.8 member inside 1.21.9's own path only when that
 * member is mixed (or overridden, which the route tests itself); unmixed, 1.21.9's path runs untouched. Server-side
 * runs skip the client section's entries.
 */
public final class CbMixedMembers {
    private static final String FILE = "META-INF/centurybridge/mixed/1.21.9.txt";
    private static volatile java.util.Set<String> mixed;

    private CbMixedMembers() {
    }

    /** whether a loaded mod's converted mixin injects into this restored member ("owner.name(desc)") */
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
                        String[] f = l.trim().split("\t");
                        if (f.length >= 3 && !(server && f[2].equals("client"))) {
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
