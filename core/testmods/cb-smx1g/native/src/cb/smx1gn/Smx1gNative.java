package cb.smx1gn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

/**
 * Driver of the smx1g check (native 26.2). Waits until the climbed 1.21.5 mod cb_smx1g made its 64 legacy uniforms (or 90 s),
 * lets two more seconds of frames close their passes, then judges on the render thread (between frames, no pass open):
 * every legacy buffer was freed, each inside RenderPass.close(), none elsewhere, none still open; then replays the 1.21.6
 * layer's close hook on every backend that freed buffers and expects no further frees (idempotent). Without cb_smx1g it is
 * the native control: passes close and no legacy buffer exists. Prints "CB-SMX1G ok|FAIL ..", "CB-SMX1G n/t", stops the client.
 */
public class Smx1gNative implements ClientModInitializer {
    private static final List<Boolean> OK = new ArrayList<>();

    static void log(String s) {
        System.out.println("CB-SMX1G " + s);
    }

    static void check(String name, boolean pass, String detail) {
        OK.add(pass);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onInitializeClient() {
        boolean old = FabricLoader.getInstance().isModLoaded("cb_smx1g");
        Thread t = new Thread(() -> drive(old), "cb-smx1g-driver");
        t.setDaemon(true);
        t.start();
    }

    static <T> T onRender(Minecraft mc, Supplier<T> s) {
        CompletableFuture<T> f = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                f.complete(s.get());
            } catch (Throwable e) {
                f.completeExceptionally(e);
            }
        });
        return f.join();
    }

    static void drive(boolean old) {
        Minecraft mc = Minecraft.getInstance();
        while (mc == null) {
            sleep(500);
            mc = Minecraft.getInstance();
        }
        log("old mod loaded: " + old);
        long until = System.currentTimeMillis() + 90000;
        while (System.currentTimeMillis() < until
            && (old ? !"64".equals(System.getProperty("cbsmx1g.puts")) : Probe.passCloses < 200)) {
            sleep(500);
        }
        sleep(2000);
        final Minecraft m = mc;
        int[] s = onRender(m, () -> new int[] {Probe.created, Probe.freedInPassClose, Probe.freedElsewhere, Probe.open(),
            Probe.passCloses});
        String counts = "created=" + s[0] + " freedInPassClose=" + s[1] + " freedElsewhere=" + s[2] + " open=" + s[3]
            + " passCloses=" + s[4];
        log("counters " + counts + " puts=" + System.getProperty("cbsmx1g.puts") + " identityFails="
            + System.getProperty("cbsmx1g.identityFails"));
        check("native.passesClose", s[4] > 0, "passCloses=" + s[4]);
        Object hook = FabricLoader.getInstance().getObjectShare().get("centurybridge:gl_uniforms_closed");
        if (old) {
            String puts = System.getProperty("cbsmx1g.puts");
            check("old.puts", "64".equals(puts), "puts=" + puts);
            check("old.sameArray", "0".equals(System.getProperty("cbsmx1g.identityFails")),
                "identityFails=" + System.getProperty("cbsmx1g.identityFails"));
            check("layer.buffersMade", s[0] > 0 && puts != null && s[0] == Integer.parseInt(puts), counts);
            check("close.allFreedAtPassClose", s[0] > 0 && s[1] == s[0] && s[3] == 0, counts);
            check("close.noneFreedElsewhere", s[2] == 0, counts);
            check("close.hookPublished", hook instanceof Consumer, String.valueOf(hook));
            int backends = Probe.FREEING_BACKENDS.size();
            int[] after = hook instanceof Consumer ? onRender(m, () -> {
                @SuppressWarnings("unchecked")
                Consumer<Object> h = (Consumer<Object>) hook;
                Object[] bs;
                synchronized (Probe.FREEING_BACKENDS) {
                    bs = Probe.FREEING_BACKENDS.toArray();
                }
                for (Object b : bs) {
                    h.accept(b);
                }
                return new int[] {Probe.freedInPassClose, Probe.freedElsewhere};
            }) : new int[] {-1, -1};
            check("close.idempotent", backends > 0 && after[0] == s[1] && after[1] == s[2],
                "replayed on " + backends + " backends: freedInPassClose " + s[1] + "->" + after[0] + ", freedElsewhere "
                    + s[2] + "->" + after[1]);
        } else {
            check("native.noLegacyBuffers", s[0] == 0, counts);
            check("native.noHook", hook == null, String.valueOf(hook));
        }
        log(OK.stream().filter(b -> b).count() + "/" + OK.size());
        sleep(1000);
        m.execute(m::stop);
    }
}
