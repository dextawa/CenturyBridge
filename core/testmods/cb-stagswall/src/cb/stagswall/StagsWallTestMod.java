package cb.stagswall;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3481;
import net.minecraft.class_6862;
import net.minecraft.class_7924;

/**
 * Real-server check of the tag-drift bridge at the wall, 1.21.11 -> 26.1 (written against 1.21.11, converted one
 * step). 26.1 renamed big_dripleaf_placeable to supports_big_dripleaf (same members, the same blocks read it): this
 * mod's own file adding stone to minecraft:big_dripleaf_placeable must let big dripleaf stand on stone, as it did in
 * 1.21.11 (the file is mirrored into the renamed tag). A tag file of this mod including #big_dripleaf_placeable is
 * rewritten to the renamed tag; one including #minecraft:dirt reaches 26.1 substrate_overworld (1.21.11's dirt
 * members). Its file adding a stick to minecraft:dyeable also reaches cauldron_can_remove_dye, the part of dyeable
 * 26.1's cauldron reads. Prints "CBTEST PASS|FAIL name" lines and a summary.
 */
public class StagsWallTestMod implements ModInitializer {

    private static final List<String> FAILS = new ArrayList<>();
    private static int passed;

    private static void check(String name, boolean ok, Object detail) {
        if (ok) {
            passed++;
            System.out.println("CBTEST PASS " + name + " : " + detail);
        } else {
            FAILS.add(name);
            System.out.println("CBTEST FAIL " + name + " : " + detail);
        }
    }

    private static class_6862<class_2248> block(String ns, String path) {
        return class_6862.method_40092(class_7924.field_41254, class_2960.method_60655(ns, path));
    }

    private static class_6862<class_1792> item(String ns, String path) {
        return class_6862.method_40092(class_7924.field_41197, class_2960.method_60655(ns, path));
    }

    private static boolean in(class_2248 b, class_6862<class_2248> tag) {
        return b.method_9564().method_26164(tag);
    }

    /** whether big dripleaf can stand on ground placed at a fresh spot */
    private static boolean dripleafOn(class_3218 level, class_2248 ground, int x) {
        class_2338 pos = new class_2338(x, 120, 0);
        level.method_8501(pos, ground.method_9564());
        level.method_8501(pos.method_10084(), class_2246.field_10124.method_9564());
        return class_2246.field_28682.method_9564().method_26184(level, pos.method_10084());
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                class_3218 level = server.method_30002();
                // behaviour: big dripleaf on this mod's stone (the mod's big_dripleaf_placeable addition)
                check("big dripleaf stands on dirt (vanilla member)", dripleafOn(level, class_2246.field_10566, 0), "dirt");
                check("big dripleaf stands on stone (this mod's big_dripleaf_placeable addition)",
                    dripleafOn(level, class_2246.field_10340, 4), "stone");
                // the field: BlockTags.BIG_DRIPLEAF_PLACEABLE as 1.21.11 code reads it
                class_6862<class_2248> field = class_3481.field_35443;
                check("BIG_DRIPLEAF_PLACEABLE key", true, field.comp_327());
                check("BIG_DRIPLEAF_PLACEABLE holds this mod's stone", in(class_2246.field_10340, field), "stone");
                check("BIG_DRIPLEAF_PLACEABLE holds clay (vanilla member)", in(class_2246.field_10460, field), "clay");
                // this mod's tag files naming drifted vanilla tags
                class_6862<class_2248> ground = block("cb_stagswall", "dripleaf_ground");
                check("own tag including #big_dripleaf_placeable holds clay and stone",
                    in(class_2246.field_10460, ground) && in(class_2246.field_10340, ground), "cb_stagswall:dripleaf_ground");
                class_6862<class_2248> dirt = block("cb_stagswall", "my_dirt");
                check("own tag including #dirt holds grass_block (1.21.11 dirt member, not 26.1 dirt)",
                    in(class_2246.field_10219, dirt), "cb_stagswall:my_dirt");
                // dyeable: restored id, and the cauldron's part of it
                check("dyeable holds this mod's stick", new class_1799(class_1802.field_8600).method_31573(item("minecraft", "dyeable")), "stick");
                check("cauldron_can_remove_dye holds this mod's stick",
                    new class_1799(class_1802.field_8600).method_31573(item("minecraft", "cauldron_can_remove_dye")), "stick");
            } catch (RuntimeException e) {
                check("checks ran", false, e);
            }
            System.out.println("CBTEST SUMMARY passed=" + passed + " failed=" + FAILS.size() + " " + FAILS);
        });
    }
}
