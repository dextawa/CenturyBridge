package cb.s205;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * A 1.20.4-era mod (intermediary names) with recipes guarded by Fabric API 1.20.4's item / block / fluid_tags_populated
 * resource conditions, which 1.20.5's Fabric API dropped and the 1.20.5 step layer gives back with 1.20.4's test: every
 * listed tag loaded in this reload with at least one entry. Once the server started each recipe's presence is checked:
 * "CB-S205 ok|FAIL <case> <detail>", ending "CB-S205 summary <start|reload> ok=N fail=M"; again after each datapack reload.
 * <ul>
 * <li>control: no condition (the recipe format loads at all);</li>
 * <li>items_logs, blocks_logs, fluids_water: populated vanilla tags, loaded;</li>
 * <li>items_missing, items_logs_and_missing: a tag no pack defines, not loaded;</li>
 * <li>items_empty: cb_s205:empty, a tag this mod defines with no entries -- loaded by 1.20.4's test only if the
 *     reload's tags were captured with their entries (1.20.5's own tags_populated passes it).</li>
 * </ul>
 */
public class S205TestMod implements ModInitializer {
    private static int ok;
    private static int fail;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> checkAll(server, "start"));
        // a datapack reload (/reload) goes through the server's reload path, not the world loader's
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> checkAll(server, "reload"));
    }

    private static void checkAll(net.minecraft.server.MinecraftServer server, String when) {
        ok = 0;
        fail = 0;
        net.minecraft.class_1863 recipes = server.method_3772();
        expect(recipes, when, "control", true);
        expect(recipes, when, "items_logs", true);
        expect(recipes, when, "blocks_logs", true);
        expect(recipes, when, "fluids_water", true);
        expect(recipes, when, "items_missing", false);
        expect(recipes, when, "items_logs_and_missing", false);
        expect(recipes, when, "items_empty", false);
        System.out.println("CB-S205 summary " + when + " ok=" + ok + " fail=" + fail);
    }

    private static void expect(net.minecraft.class_1863 recipes, String when, String name, boolean loaded) {
        try {
            boolean present = recipes.method_8130(new net.minecraft.class_2960("cb_s205", name)).isPresent();
            boolean pass = present == loaded;
            if (pass) ok++; else fail++;
            System.out.println("CB-S205 " + (pass ? "ok" : "FAIL") + " " + when + " " + name + " loaded=" + present + " expected=" + loaded);
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-S205 FAIL " + when + " " + name + " threw " + t);
        }
    }
}
