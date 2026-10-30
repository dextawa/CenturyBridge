package cb.sk212;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A 1.21.1-era server mod (intermediary names) reading AbstractFurnaceBlockEntity.recipesUsed (class_2609.field_11986,
 * Object2IntOpenHashMap<ResourceLocation> -> Reference2IntOpenHashMap<ResourceKey<Recipe>> at 1.21.2) through the vanilla
 * class and as this.field of a subclass of its own. Prints "CB-SK212 ok|FAIL <what>" and "CB-SK212 DONE passed/checks".
 */
public class Sk212TestMod implements ModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("cb_sk212");
    private int checks;
    private int passed;

    /** a mod subclass of FurnaceBlockEntity reading the inherited private map (the mod's access widener opens it) */
    static class MyFurnace extends net.minecraft.class_3866 {
        MyFurnace() {
            super(net.minecraft.class_2338.field_10980, net.minecraft.class_2246.field_10181.method_9564());
        }

        int used(net.minecraft.class_2960 id) {
            return this.field_11986.getInt(id);
        }

        void use(net.minecraft.class_2960 id, int n) {
            this.field_11986.addTo(id, n);
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
            LOG.info("CB-SK212 ok " + what);
        } else {
            LOG.info("CB-SK212 FAIL " + what);
        }
    }

    private void run(MinecraftServer server) {
        try {
            net.minecraft.class_2960 id = net.minecraft.class_2960.method_60655("cbsk212", "probe");
            net.minecraft.class_3866 f = new net.minecraft.class_3866(net.minecraft.class_2338.field_10980,
                net.minecraft.class_2246.field_10181.method_9564());
            Object2IntOpenHashMap<net.minecraft.class_2960> used = f.field_11986;
            used.addTo(id, 2);
            check(used.getInt(id) == 2 && used.size() == 1, "recipesUsed.addTo reads back through the vanilla owner");
            check(used.keySet().contains(id), "recipesUsed keys answer the ResourceLocation");
            net.minecraft.class_2487 tag = f.method_38244(server.method_30611());
            check(tag.method_10562("RecipesUsed").method_10550("cbsk212:probe") == 2,
                "the furnace saves the count written through recipesUsed (" + tag.method_10562("RecipesUsed") + ")");
            used.clear();
            check(f.method_38244(server.method_30611()).method_10562("RecipesUsed").method_10545("cbsk212:probe") == false,
                "clear() through recipesUsed empties what the furnace saves");

            MyFurnace mine = new MyFurnace();
            mine.use(id, 3);
            check(mine.used(id) == 3, "subclass this.recipesUsed reads its own write");
            check(mine.method_38244(server.method_30611()).method_10562("RecipesUsed").method_10550("cbsk212:probe") == 3,
                "the subclass furnace saves the count written through this.recipesUsed");
        } catch (Throwable t) {
            LOG.error("CB-SK212 FAIL exception", t);
            checks++;
        }
        LOG.info("CB-SK212 DONE " + passed + "/" + checks);
    }
}
