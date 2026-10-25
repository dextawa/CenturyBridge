package cb.smx4n;

import cb.smx4n.mixin.TextureManagerAccess;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_1059;
import net.minecraft.class_310;
import net.minecraft.class_380;

/**
 * smx4's native 1.21.4 driver (see ../build.sh): the registered texture map dump (4a), TextureManager.register
 * HEAD / RETURN / Map.put redirect counts (4c), the climbed mod's load counts against the registers by class (4b, also
 * across one resource reload), isMouseOver inside mouseClicked (7b); stops the client.
 */
public class Driver implements ClientModInitializer {
    public static final AtomicInteger REG_HEAD = new AtomicInteger();
    public static final AtomicInteger REG_RETURN = new AtomicInteger();
    public static final AtomicInteger REG_PUT = new AtomicInteger();
    public static final AtomicInteger MC_CALLS = new AtomicInteger();
    public static final AtomicInteger OVER_IN_CLICK = new AtomicInteger();
    public static final ThreadLocal<Integer> IN_CLICK = ThreadLocal.withInitial(() -> 0);
    /** registers of DynamicTexture / TextureAtlas / FontTexture by runtime class */
    public static final Map<String, Integer> REGISTERS = new ConcurrentHashMap<>();
    static final Map<String, Boolean> OK = new LinkedHashMap<>();

    public static void registered(class_1044 t) {
        if (t instanceof class_1043 || t instanceof class_1059 || t instanceof class_380) {
            REGISTERS.merge(t.getClass().getName(), 1, Integer::sum);
        }
    }

    static void check(String name, boolean pass, String detail) {
        OK.put(name, pass);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    static void log(String s) {
        System.out.println("CB-SMX4 " + s);
    }

    @Override
    public void onInitializeClient() {
        Thread t = new Thread(Driver::drive, "cb-smx4n");
        t.setDaemon(true);
        t.start();
    }

    @SuppressWarnings("unchecked")
    static Map<String, Integer> loads(Class<?> counters) throws ReflectiveOperationException {
        return counters == null ? Map.of() : new TreeMap<>((Map<String, Integer>) counters.getField("LOADS").get(null));
    }

    static void drive() {
        class_310 mc = class_310.method_1551();
        try {
            long until = System.currentTimeMillis() + 240_000;
            while ((mc.field_1724 == null || mc.field_1687 == null) && System.currentTimeMillis() < until) {
                Thread.sleep(500);
            }
            if (mc.field_1724 == null) {
                check("join", false, "no world joined within 240 s");
                return;
            }
            Thread.sleep(3000);
            Class<?> counters = null;
            try {
                counters = Class.forName("cb.smx4c.Counters");
            } catch (ClassNotFoundException absent) {
                log("cb-smx4c not loaded: climbed checks not run (dump run)");
            }

            // 4a: the registered texture map (compare with the bare 1.21.4 run)
            Map<String, String> tex = CompletableFuture.supplyAsync(() -> {
                Map<String, String> out = new TreeMap<>();
                for (Map.Entry<?, ?> e : ((TextureManagerAccess) mc.method_1531()).cb$byPath().entrySet()) {
                    out.put(String.valueOf(e.getKey()), e.getValue().getClass().getName());
                }
                return out;
            }, mc).get(20, TimeUnit.SECONDS);
            for (Map.Entry<String, String> e : tex.entrySet()) {
                log("tex " + e.getKey() + " " + e.getValue());
            }
            log("tex-total " + tex.size() + " hash " + Integer.toHexString(tex.hashCode()));

            // 4c: native register HEAD / RETURN / redirect
            int h = REG_HEAD.get(), r = REG_RETURN.get(), p = REG_PUT.get();
            check("4c.registerInjects", h > 0 && h == r && h == p, "register HEAD " + h + ", RETURN " + r + ", Map.put redirect " + p);

            // 4b: one load per register of such a texture, and again across a resource reload
            Map<String, Integer> regs0 = new TreeMap<>(REGISTERS), loads0 = loads(counters);
            if (counters != null) {
                check("4b.loadsPerRegister", regs0.equals(loads0), "registers by class " + regs0 + ", loads by class " + loads0);
            }
            CompletableFuture<?> reload = CompletableFuture.supplyAsync(mc::method_1521, mc).get(20, TimeUnit.SECONDS);
            reload.get(120, TimeUnit.SECONDS);
            Thread.sleep(2000);
            Map<String, Integer> regs1 = new TreeMap<>(REGISTERS), loads1 = loads(counters);
            if (counters != null) {
                check("4b.afterReload", regs1.equals(loads1), "after one reload: registers " + regs1 + ", loads " + loads1);
            }
            int h1 = REG_HEAD.get(), r1 = REG_RETURN.get(), p1 = REG_PUT.get();
            check("4c.afterReload", h1 == r1 && h1 == p1, "register HEAD " + h1 + ", RETURN " + r1 + ", Map.put redirect " + p1);

            // wait for the climbed driver
            if (counters != null) {
                long u2 = System.currentTimeMillis() + 180_000;
                while (!(Boolean) counters.getField("done").get(null) && System.currentTimeMillis() < u2) {
                    Thread.sleep(500);
                }
                int mcCalls = MC_CALLS.get(), over = OVER_IN_CLICK.get();
                check("7b.isMouseOverPerClick", mcCalls > 0 && over == mcCalls, "AbstractWidget.mouseClicked calls " + mcCalls
                    + ", isMouseOver RETURNs inside them " + over);
            }
        } catch (Throwable t) {
            log("FAIL driver " + t);
            t.printStackTrace();
        }
        int ok = 0;
        for (boolean b : OK.values()) {
            ok += b ? 1 : 0;
        }
        log("summary ok=" + ok + " fail=" + (OK.size() - ok));
        log(ok + "/" + OK.size());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        mc.execute(mc::method_1592);
    }
}
