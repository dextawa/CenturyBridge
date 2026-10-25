package cb.s11n;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1928;
import net.minecraft.server.MinecraftServer;

/**
 * A native 1.21.11 mod next to the converted cb-s11: with the step layer's game rule routes on (and cb-s11's 1.21.10
 * mixins on getBoolean / BooleanValue.set loaded), its own mixins on 1.21.11's GameRules.get / set still fire, once per
 * call, and a value it forces is what vanilla reads. Runs when the server has started (after cb-s11's driver):
 * "CB-S11N ok|FAIL case detail", then "CB-S11N summary ok=N fail=M".
 */
public final class NativeDriver implements ModInitializer {
    public static volatile int getHeads;
    public static volatile int getReturns;
    public static volatile Object lastReturn;
    public static volatile int setHeads;
    public static volatile int setTails;
    public static volatile Object lastSet;
    public static volatile boolean forceKeep;
    private static int ok;
    private static int fail;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(NativeDriver::run);
    }

    private static void check(String c, boolean cond, Object detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-S11N " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    /** cb-s11's (converted) Driver statics: its 1.21.10 mixins read / bump them */
    private static Object driver(String field) throws ReflectiveOperationException {
        return Class.forName("cb.s11.Driver").getField(field).get(null);
    }

    private static void driver(String field, Object value) throws ReflectiveOperationException {
        Class.forName("cb.s11.Driver").getField(field).set(null, value);
    }

    private static void run(MinecraftServer server) {
        try {
            run0(server);
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-S11N FAIL exception " + t);
            t.printStackTrace(System.out);
        }
        System.out.println("CB-S11N summary ok=" + ok + " fail=" + fail);
    }

    private static void run0(MinecraftServer server) throws Exception {
        class_1928 rules = server.method_30002().method_64395();
        rules.method_76186(class_1928.field_19389, false, server);

        // get: HEAD and RETURN mixins fire once per call; unmixed the value is 1.21.11's own
        int h = getHeads;
        int r = getReturns;
        Boolean v = rules.method_76185(class_1928.field_19389);
        check("native-get-fires", getHeads == h + 1 && getReturns == r + 1 && Boolean.FALSE.equals(v)
            && Boolean.FALSE.equals(lastReturn), "heads " + (getHeads - h) + " returns " + (getReturns - r) + " value " + v);

        // set: HEAD and TAIL mixins fire once per call; the value is stored; cb-s11's 1.21.10 mixin on
        // BooleanValue.set sees this 1.21.11 write once
        int sh = setHeads;
        int st = setTails;
        int vs = (Integer) driver("boolSets");
        rules.method_76186(class_1928.field_19389, true, server);
        int vsAfter = (Integer) driver("boolSets");
        check("native-set-fires", setHeads == sh + 1 && setTails == st + 1 && Boolean.TRUE.equals(lastSet)
            && Boolean.TRUE.equals(rules.method_76185(class_1928.field_19389)) && vsAfter == vs + 1,
            "heads " + (setHeads - sh) + " tails " + (setTails - st) + " 1.21.10 sets " + (vsAfter - vs));
        rules.method_76186(class_1928.field_19389, false, server);

        // a native HEAD mixin that answers get: vanilla reads its value (the game rule command's query)
        forceKeep = true;
        int forced = server.method_3734().method_9235().execute("gamerule keep_inventory", server.method_3739());
        Boolean fv = rules.method_76185(class_1928.field_19389);
        forceKeep = false;
        check("native-get-override", forced == 1 && Boolean.TRUE.equals(fv), "query " + forced + " get " + fv);

        // cb-s11's 1.21.10 mixin on getBoolean forcing keepInventory: vanilla's get reads it, and the native mixins
        // around 1.21.11's get still fire, once, the RETURN one seeing the 1.21.10 mixin's value
        h = getHeads;
        r = getReturns;
        driver("forceKeep", true);
        Boolean mv = rules.method_76185(class_1928.field_19389);
        driver("forceKeep", false);
        check("native-with-legacy-mixin", Boolean.TRUE.equals(mv) && getHeads == h + 1 && getReturns == r + 1
            && Boolean.TRUE.equals(lastReturn), "value " + mv + " heads " + (getHeads - h) + " returns " + (getReturns - r));
        check("native-unmixed-again", Boolean.FALSE.equals(rules.method_76185(class_1928.field_19389)), "keepInventory off");
    }
}
