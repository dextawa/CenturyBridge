package cb.sk203;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A 1.20.2-era server mod (intermediary names) reading the id maps 1.20.3 retyped Object2IntMap -> Reference2IntMap
 * (IdMapper.tToId class_2361.field_11100, MappedRegistry.toId class_2370.field_26683), the way Kilt's
 * GameData$ClearableObjectIntIdentityMap does (Kilt E3): through the vanilla class, and as this.field of a subclass of its
 * own one and two levels down. Prints "CB-SK203 ok|FAIL <what>" and "CB-SK203 DONE passed/checks" once the server started.
 */
public class Sk203TestMod implements ModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("cb_sk203");
    private int checks;
    private int passed;

    /** a mod subclass of IdMapper reading the inherited private map (the mod's access widener opens it) */
    static class Sub<T> extends net.minecraft.class_2361<T> {
        int raw(T t) {
            return this.field_11100.getInt(t);
        }

        void forget(T t) {
            this.field_11100.removeInt(t);
        }

        void wipe() {
            this.field_11100.clear();
        }
    }

    /** two levels down, as Kilt's GameData$BlockCallbacks$1 under ClearableObjectIntIdentityMap */
    static class Sub2<T> extends Sub<T> {
        boolean has(T t) {
            return this.field_11100.containsKey(t);
        }
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::run);
    }

    private void check(boolean ok, String what) {
        checks++;
        if (ok) {
            passed++;
            LOG.info("CB-SK203 ok " + what);
        } else {
            LOG.info("CB-SK203 FAIL " + what);
        }
    }

    private void run(MinecraftServer server) {
        try {
            net.minecraft.class_2680 stone = net.minecraft.class_2246.field_10340.method_9564();
            Object2IntMap<net.minecraft.class_2680> states = net.minecraft.class_2248.field_10651.field_11100;
            check(states.getInt(stone) == net.minecraft.class_2248.method_9507(stone),
                "Block.BLOCK_STATE_REGISTRY.tToId reads the state's id (" + states.getInt(stone) + ")");
            check(states.size() > 1000, "tToId has every block state (" + states.size() + ")");

            net.minecraft.class_2370<net.minecraft.class_2248> blocks = (net.minecraft.class_2370<net.minecraft.class_2248>) net.minecraft.class_7923.field_41175;
            Object2IntMap<net.minecraft.class_2248> toId = blocks.field_26683;
            check(toId.getInt(net.minecraft.class_2246.field_10340) == blocks.method_10206(net.minecraft.class_2246.field_10340),
                "BuiltInRegistries.BLOCK toId reads STONE's id");

            Sub<String> sub = new Sub<>();
            sub.method_10203("a", 5);
            check(sub.raw("a") == 5, "subclass this.tToId reads a mapping made through vanilla");
            sub.forget("a");
            check(sub.method_10206("a") == -1, "a removal through subclass this.tToId reaches vanilla getId");
            Sub2<String> sub2 = new Sub2<>();
            sub2.method_10203("b", 7);
            check(sub2.has("b") && sub2.raw("b") == 7, "two levels down, this.tToId reads the mapping");
            sub2.wipe();
            check(sub2.method_10206("b") == -1, "clear() through subclass this.tToId reaches vanilla getId");

            // WorldUpgrader.progressMap (class_1257.field_5762, Object2FloatMap -> Reference2FloatMap at 1.20.3), on an
            // upgrader of an empty scratch world, cancelled right after
            java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("cbsk203");
            net.minecraft.class_32.class_5143 access = net.minecraft.class_32.method_26999(dir).method_27002("w");
            net.minecraft.class_1257 up = new net.minecraft.class_1257(access, server.method_3855(),
                server.method_30611().method_30530(net.minecraft.class_7924.field_41224), false);
            try {
                net.minecraft.class_5321<net.minecraft.class_1937> probe = net.minecraft.class_5321.method_29179(
                    net.minecraft.class_7924.field_41223, new net.minecraft.class_2960("cbsk203", "probe"));
                it.unimi.dsi.fastutil.objects.Object2FloatMap<net.minecraft.class_5321<net.minecraft.class_1937>> pm = up.field_5762;
                pm.put(probe, 0.25f);
                check(up.method_5393(probe) == 0.25f, "a progress written through WorldUpgrader.progressMap reads back via dimensionProgress");
                check(pm.containsKey(probe) && pm.getFloat(probe) == 0.25f, "progressMap reads its own write");
            } finally {
                up.method_5402();
                access.close();
            }
        } catch (Throwable t) {
            LOG.error("CB-SK203 FAIL exception", t);
            checks++;
        }
        LOG.info("CB-SK203 DONE " + passed + "/" + checks);
    }
}
