package top.dext.centurybridge.rt.v1_20_2;

/**
 * Which members this layer restores a loaded mod's converted mixin injects into. The conversion writes them to
 * META-INF/centurybridge/mixed/1.20.2.txt in each converted jar: "owner.name(desc)", the mixin class and its config
 * section (smixin3 (l), cases_steps/mixed-members.md). A route consults a restored 1.20.1 member inside 1.20.2's own
 * path only when such a member is mixed; unmixed, 1.20.2's path runs untouched. Server-side runs skip the entries of
 * the client section. The keys are this step's intermediary names, in the layer and in the mods' files alike, at
 * every release the layer climbs to (ldc strings and resources are not renamed).
 */
public final class CbMixedMembers {
    private static final String FILE = "META-INF/centurybridge/mixed/1.20.2.txt";
    private static volatile java.util.Set<String> mixed;
    private static volatile java.util.Set<String> owners;

    private CbMixedMembers() {
    }

    /** whether a loaded mod's converted mixin injects into this restored member ("owner.name(desc)") */
    public static boolean mixed(String key) {
        java.util.Set<String> s = mixed;
        return (s != null ? s : load()).contains(key);
    }

    /** whether a loaded mod's converted mixin injects into a member this layer restores on one of these classes */
    public static boolean mixedOn(java.util.Set<String> classes) {
        java.util.Set<String> o = owners;
        if (o == null) {
            load();
            o = owners;
        }
        for (String c : o) {
            if (classes.contains(c)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized java.util.Set<String> load() {
        if (mixed != null) {
            return mixed;
        }
        java.util.Set<String> out = new java.util.HashSet<>();
        java.util.Set<String> own = new java.util.HashSet<>();
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
                            int dot = f[0].indexOf('.');
                            if (dot > 0) {
                                own.add(f[0].substring(0, dot));
                            }
                        }
                    }
                }
            }
        } catch (java.io.IOException ex) {
            throw new java.io.UncheckedIOException(ex);
        }
        owners = own;
        mixed = out;
        return out;
    }
}
