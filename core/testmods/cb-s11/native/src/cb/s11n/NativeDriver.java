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
    public static volatile int serializeHeads;
    public static volatile int serializeReturns;
    public static volatile String lastSerialized;
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

        // 1.21.11's GameRule.serialize consults the 1.21.10 BooleanValue.serialize cb-s11 mixes (only while it answers
        // otherwise than its body)
        int zh = serializeHeads;
        int zr = serializeReturns;
        String plain = class_1928.field_19389.method_76146(true);
        boolean sawPlain = "true".equals(lastSerialized);
        driver("loudSerialize", true);
        String loud = class_1928.field_19389.method_76146(true);
        driver("loudSerialize", false);
        // cb-s11's 1.21.10 biome: its BiomeSpecialEffects.Builder settings are the biome's environment attributes
        net.minecraft.class_1959 biome = (net.minecraft.class_1959) driver("builtBiome");
        net.minecraft.class_12199 attrs = biome.method_75734();
        Integer fog = attrs.method_75663(net.minecraft.class_12206.field_63742, 0);
        Integer waterFog = attrs.method_75663(net.minecraft.class_12206.field_63744, 0);
        Integer sky = attrs.method_75663(net.minecraft.class_12206.field_63746, 0);
        Float volume = attrs.method_75663(net.minecraft.class_12206.field_63752, 1.0F);
        check("biome-attributes", fog == 0x123456 && waterFog == 0x222222 && sky == 0x654321 && volume == 0.5F
            && biome.method_24377().comp_5169() == 0x111111,
            "fog " + Integer.toHexString(fog) + " waterFog " + Integer.toHexString(waterFog) + " sky " + Integer.toHexString(sky) + " volume " + volume);
        // cb-s11's 1.21.10 Fabric API biome modification of plains: particle, loop and mood sounds, additions cleared,
        // music -> plains' 1.21.11 AMBIENT_PARTICLES / AMBIENT_SOUNDS / BACKGROUND_MUSIC
        net.minecraft.class_12199 plains = server.method_30611().method_30530(net.minecraft.class_7924.field_41236)
            .method_31140(net.minecraft.class_1972.field_9451).method_75734();
        java.util.List<net.minecraft.class_4761> particles = plains.method_75663(net.minecraft.class_12206.field_63750, java.util.List.of());
        net.minecraft.class_12190 sounds = plains.method_75663(net.minecraft.class_12206.field_63753, net.minecraft.class_12190.field_63684);
        net.minecraft.class_12194 music = plains.method_75663(net.minecraft.class_12206.field_63751, net.minecraft.class_12194.field_63700);
        boolean particleOk = particles.size() == 1 && particles.get(0).comp_4993() == net.minecraft.class_2398.field_11248
            && particles.get(0).comp_4994() == 0.0125F;
        boolean soundsOk = sounds.comp_5125().map(snd -> snd.method_40230().equals(net.minecraft.class_3417.field_14564.method_40230())).orElse(false)
            && sounds.comp_5126().map(m -> m == net.minecraft.class_4968.field_23146).orElse(false) && sounds.comp_5127().isEmpty();
        boolean musicOk = music.comp_5133().map(m -> m.comp_5124().method_40230().equals(net.minecraft.class_3417.field_23790.method_40230())
            && m.comp_4359() == 100 && m.comp_4360() == 200 && m.comp_4361()).orElse(false);
        check("fapi-biome-effects", particleOk && soundsOk && musicOk,
            "particles " + particles + " sounds " + sounds + " music " + music);
        check("native-serialize-route", "true".equals(plain) && "yes".equals(loud) && sawPlain
            && serializeHeads == zh + 2 && serializeReturns == zr + 2,
            "plain " + plain + " loud " + loud + " native heads " + (serializeHeads - zh) + " returns " + (serializeReturns - zr));
    }
}
