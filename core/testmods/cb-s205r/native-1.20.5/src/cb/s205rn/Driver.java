package cb.s205rn;

/**
 * cb-s205rn: a mod native to W (not converted), with mixins on the call sites the recipe id routes wrap
 * (W 1.20.5: RecipeManager.apply's entry key and Recipe.CODEC.parse; W 1.21.4: scanDirectory's fileToId and parse;
 * both: RecipeHolder.STREAM_CODEC). Its own wrappers must keep running and recipes load as W loads them, with the
 * routes active (cb-s205r loaded) and idle. "CB-S205RN ok|FAIL <case> <detail>", "CB-S205RN summary ok=N fail=M".
 */
public final class Driver {
    public static volatile int keys;
    public static volatile int parses;
    public static volatile int codecDecodes;
    private static int ok;
    private static int fail;

    private Driver() {
    }

    static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-S205RN " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void run(net.minecraft.server.MinecraftServer server) {
        String mode;
        try {
            Class.forName("cb.s205r.S205RTestMod");
            mode = "(cb_s205r loaded: routes active)";
        } catch (ClassNotFoundException e) {
            mode = "(no converted mod: routes idle)";
        }
        try {
            int recipes = server.method_3772().method_8126().size();
            check("load-wrappers", keys > 0 && keys == parses, "keys=" + keys + " parses=" + parses + " " + mode);
            check("recipes", recipes > 0 && recipes <= parses, "loaded=" + recipes + " " + mode);
            net.minecraft.class_8786<?> holder = server.method_3772().method_8126().iterator().next();
            net.minecraft.class_9129 buf = new net.minecraft.class_9129(io.netty.buffer.Unpooled.buffer(), server.method_30611());
            net.minecraft.class_9139<net.minecraft.class_9129, Object> codec = (net.minecraft.class_9139) net.minecraft.class_8786.field_48357;
            codec.encode(buf, holder);
            int before = codecDecodes;
            net.minecraft.class_8786<?> back = (net.minecraft.class_8786<?>) codec.decode(buf);
            check("holder-codec", codecDecodes == before + 1 && back.comp_1932().equals(holder.comp_1932()),
                "decodes=" + (codecDecodes - before) + " id=" + back.comp_1932() + " " + mode);
        } catch (Throwable t) {
            check("threw", false, t.toString());
            t.printStackTrace(System.out);
        }
        System.out.println("CB-S205RN summary ok=" + ok + " fail=" + fail);
    }
}
