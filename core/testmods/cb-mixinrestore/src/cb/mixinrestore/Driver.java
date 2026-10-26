package cb.mixinrestore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runs once the server's world is loaded: triggers the vanilla methods the test injectors hook and
 * checks that each handler ran with what V's code gave it. Logs "CB-MIXINRESTORE ok|FAIL <case>
 * <detail>" per case and "CB-MIXINRESTORE summary ok=N fail=M" at the end.
 */
public final class Driver {
    private static final Map<String, String> RAN = new ConcurrentHashMap<>();
    private static int ok;
    private static int fail;

    private Driver() {
    }

    private static final Map<String, Integer> COUNT = new ConcurrentHashMap<>();

    /** a handler run during class initialisation, counted (it runs before the driver) */
    public static void count(String c) {
        COUNT.merge(c, 1, Integer::sum);
    }

    public static void ran(String c, String values) {
        RAN.put(c, values);
        System.out.println("CB-MIXINRESTORE ran " + c + " " + values);
    }

    private static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-MIXINRESTORE " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        System.out.println("CB-MIXINRESTORE start");
        // ---- a HEAD injector on an override W dropped: the furnace's stillValid(null)
        try {
            net.minecraft.class_3866 furnace = new net.minecraft.class_3866(net.minecraft.class_2338.field_10980,
                net.minecraft.class_2246.field_10181.method_9564());
            RAN.remove("furnace-stillValid-head");
            boolean valid = ((net.minecraft.class_1263) furnace).method_5443(null);
            check("restored-override-head", valid && RAN.containsKey("furnace-stillValid-head"),
                "stillValid(null) -> " + valid + ", handler ran: " + RAN.containsKey("furnace-stillValid-head"));
        } catch (Throwable t) {
            check("restored-override-head", false, "threw " + t);
        }
        // ---- an unordinaled NEW injector where W added a second NEW: it fires at V's site only
        // (forces ParticleArgument's initialisation if nothing else did)
        new net.minecraft.class_2223(null);
        int n = COUNT.getOrDefault("particle-argument-new-exception", 0);
        check("sites-split-new", n == 1, "NEW DynamicCommandExceptionType handler ran " + n + " time(s) (V's code has 1 site)");
        // ---- a HEAD injector on a private method W reshaped with no wiring (body follow)
        int r = COUNT.getOrDefault("particle-types-register", 0);
        check("body-follow-head", r == 11, "ParticleTypes.register handler ran " + r + " time(s) (1.20.5 registers 11 particle types with options through it)");
        // ---- an INVOKE injector in a member a layer restores (1.20.5: FriendlyByteBuf.writeGameProfile)
        try {
            net.minecraft.class_2540 buf = new net.minecraft.class_2540(io.netty.buffer.Unpooled.buffer());
            RAN.remove("buf-writeGameProfile-invoke");
            buf.method_43616(new com.mojang.authlib.GameProfile(new java.util.UUID(1L, 2L), "cbrestore"));
            check("restored-member-invoke", "name=cbrestore".equals(RAN.get("buf-writeGameProfile-invoke")),
                "writeGameProfile wrote " + buf.readableBytes() + " bytes, writeUtf INVOKE handler: " + RAN.get("buf-writeGameProfile-invoke"));
        } catch (Throwable t) {
            check("restored-member-invoke", false, "threw " + t);
        }
        System.out.println("CB-MIXINRESTORE summary ok=" + ok + " fail=" + fail);
    }
}
