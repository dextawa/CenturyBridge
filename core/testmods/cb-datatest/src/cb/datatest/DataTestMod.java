package cb.datatest;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1295;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_2378;
import net.minecraft.class_2540;
import net.minecraft.class_2945;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_7923;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.4 mod with tracked entity data, written the ways 1.20.4 allowed: defined in
 * defineSynchedData(), defined in the constructor after super(), a mod subclass chaining to its
 * mod parent's defineSynchedData(), and a subclass of a vanilla entity chaining to vanilla's.
 * When the server has started it constructs each, reads and changes the data, and sends it through
 * the wire format (DataValue write / read) into a second instance, printing CBDATATEST lines.
 */
public class DataTestMod implements ModInitializer {

    static class_1299<Probe> PROBE;
    static class_1299<ProbeChild> PROBE_CHILD;
    static class_1299<ProbeCloud> PROBE_CLOUD;

    @Override
    public void onInitialize() {
        PROBE = class_2378.method_10230(class_7923.field_41177, new class_2960("cb_datatest", "probe"),
            class_1299.class_1300.<Probe>method_5903(Probe::new, class_1311.field_17715).method_17687(0.5f, 0.5f).method_5905("probe"));
        PROBE_CHILD = class_2378.method_10230(class_7923.field_41177, new class_2960("cb_datatest", "probe_child"),
            class_1299.class_1300.<ProbeChild>method_5903(ProbeChild::new, class_1311.field_17715).method_17687(0.5f, 0.5f).method_5905("probe_child"));
        PROBE_CLOUD = class_2378.method_10230(class_7923.field_41177, new class_2960("cb_datatest", "probe_cloud"),
            class_1299.class_1300.<ProbeCloud>method_5903(ProbeCloud::new, class_1311.field_17715).method_17687(0.5f, 0.5f).method_5905("probe_cloud"));
        ServerLifecycleEvents.SERVER_STARTED.register(DataTestMod::run);
    }

    static void check(String what, Object got, Object want) {
        boolean ok = want == null ? got == null : want.equals(got);
        System.out.println("CBDATATEST " + (ok ? "OK   " : "FAIL ") + what + " = " + got + (ok ? "" : " (want " + want + ")"));
    }

    /** the 1.20.4 wire format of a data list (ClientboundSetEntityDataPacket.pack / unpack) */
    static List<class_2945.class_7834<?>> roundTrip(List<class_2945.class_7834<?>> values) {
        class_2540 buf = new class_2540(Unpooled.buffer());
        for (class_2945.class_7834<?> v : values) {
            v.method_46361(buf); // the id byte, the serializer id, the value
        }
        buf.writeByte(255);
        List<class_2945.class_7834<?>> back = new ArrayList<>();
        int id;
        while ((id = buf.readUnsignedByte()) != 255) {
            back.add(class_2945.class_7834.method_46362(buf, id));
        }
        return back;
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();
        try {
            Probe a = new Probe(PROBE, level);
            class_2945 d = a.method_5841();
            check("probe COUNTER (defineSynchedData)", d.method_12789(Probe.COUNTER), 41);
            check("probe LATE (constructor body)", d.method_12789(Probe.LATE), 7);
            check("probe hasItem(LATE)", d.method_51696(Probe.LATE), true);
            check("probe isEmpty", d.method_12790(), false);
            d.method_12778(Probe.COUNTER, 42);
            d.method_12778(Probe.LATE, 8);
            check("probe dirty after set", d.method_12786(), true);
            List<class_2945.class_7834<?>> dirty = d.method_12781();
            check("probe packDirty size", dirty == null ? 0 : dirty.size(), 2);
            Probe b = new Probe(PROBE, level);
            b.method_5841().method_12779(roundTrip(d.method_46357()));
            check("synced COUNTER", b.method_5841().method_12789(Probe.COUNTER), 42);
            check("synced LATE", b.method_5841().method_12789(Probe.LATE), 8);
            check("probe flags (vanilla data)", b.method_5841().method_12789(Probe.SHARED_FLAGS_FOR_TEST), (byte) 0);
            boolean dup = false;
            try {
                d.method_12784(Probe.LATE, 9);
            } catch (IllegalArgumentException e) {
                dup = e.getMessage().startsWith("Duplicate id value for");
            }
            check("duplicate define refused", dup, true);

            ProbeChild c = new ProbeChild(PROBE_CHILD, level);
            check("child COUNTER (parent's define)", c.method_5841().method_12789(Probe.COUNTER), 41);
            check("child EXTRA (own define after super)", c.method_5841().method_12789(ProbeChild.EXTRA), "child");

            ProbeCloud cloud = new ProbeCloud(PROBE_CLOUD, level);
            check("cloud radius (vanilla defineSynchedData via super)", cloud.method_5599(), 3.0f);
            check("cloud MARK (mod define after vanilla's)", cloud.method_5841().method_12789(ProbeCloud.MARK), 5);

            a.method_5814(0.5, 100, 0.5);
            check("probe spawned", level.method_8649(a), true);
            System.out.println("CBDATATEST DONE");
        } catch (Throwable t) {
            System.out.println("CBDATATEST FAIL exception " + t);
            t.printStackTrace(System.out);
        }
    }
}
