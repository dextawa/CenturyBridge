package cb.clienttest;

import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.List;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1087;
import net.minecraft.class_1109;
import net.minecraft.class_1144;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_5819;
import net.minecraft.class_777;
import net.minecraft.class_243;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_338;
import net.minecraft.class_3417;
import net.minecraft.class_4184;
import net.minecraft.class_5602;
import net.minecraft.class_591;
import net.minecraft.class_630;
import net.minecraft.class_742;

/** One-shot checks (models, the mod's item resources and tools, audio, camera, chat) with their verdicts. */
final class Checks {
    private Checks() {
    }

    /** swall plan item 12: HumanoidModel.setAllVisible on the player model reaches the outer layers too */
    static void playerModel(class_310 mc) {
        class_591<class_742> pm = new class_591<>(mc.method_31974().method_32072(class_5602.field_27577), false);
        class_630[] parts = {pm.field_3398, pm.field_3394, pm.field_3483, pm.field_3484, pm.field_3486, pm.field_3482,
            pm.field_3479};
        pm.method_2805(false);
        int shownAfterFalse = 0;
        for (class_630 p : parts) {
            shownAfterFalse += p.field_3665 ? 1 : 0;
        }
        pm.method_2805(true);
        int shownAfterTrue = 0;
        for (class_630 p : parts) {
            shownAfterTrue += p.field_3665 ? 1 : 0;
        }
        Tally.check("model.playerSetAllVisible", shownAfterFalse == 0 && shownAfterTrue == parts.length,
            "head/hat/jacket/sleeves/pants shown: after false " + shownAfterFalse + ", after true " + shownAfterTrue
                + " of " + parts.length);
    }

    /** the mod's item model and name come from its own assets */
    static void itemResources(class_310 mc) {
        if (ItemsInit.pickaxe == null) {
            throw new IllegalStateException("the mod's tools are not registered", ItemsInit.failure);
        }
        class_1799 pick = new class_1799(ItemsInit.pickaxe);
        class_1087 model = mc.method_1480().method_4019(pick, null, null, 0);
        class_1087 missing = mc.method_1554().method_4744();
        Tally.check("item.model", model != null && model != missing,
            model == null ? "no model" : model == missing ? "the missing model" : "baked from the mod's model json");
        String name = pick.method_7964().getString();
        Tally.check("item.name", "CB Pickaxe".equals(name), "\"" + name + "\" (the mod's en_us.json: \"CB Pickaxe\")");
    }

    /** smx4 checks 1b/1c as 1.20.1 has them: the mod's BlockModel.bake and getMaterial mixins */
    static void blockModelMixins(class_310 mc) {
        Tally.check("model.bakeMixin", ModelProbe.bakes > 100, ModelProbe.bakes + " BlockModel.bake returns seen by the mod's injector");
        class_2680 stone = class_2246.field_10340.method_9564();
        class_1087 model = mc.method_1541().method_3349(stone);
        List<?> quads = model.method_4707(stone, class_2350.field_11036, class_5819.method_43047());
        class_1058 diamond = mc.method_1554().method_24153(class_1059.field_5275).method_4608(new class_2960("block/diamond_block"));
        boolean swapped = !quads.isEmpty() && ((class_777) quads.get(0)).method_35788() == diamond;
        Tally.check("model.materialMixin", ModelProbe.stoneMaterials > 0 && swapped, ModelProbe.stoneMaterials
            + " getMaterial answers replaced on the stone models; stone's top face " + (swapped ? "uses" : "does not use")
            + " the diamond block sprite (" + quads.size() + " quads)");
    }

    /** swall plan item 7: the sound device list, the engine's state, a sound played */
    static void audio(class_310 mc) {
        class_1144 sm = mc.method_1483();
        List<?> devices = sm.method_38565();
        Tally.check("audio.devices", devices != null, devices == null ? "null" : devices.size() + " devices " + devices);
        String debug = sm.method_20305();
        Tally.check("audio.debugString", debug != null && !debug.isEmpty(), "\"" + debug + "\"");
        class_1109 click = class_1109.method_47978(class_3417.field_15015, 1.0f);
        sm.method_4873(click);
        boolean active = sm.method_4877(click);
        Tally.check("audio.play", active, "UI click active right after play: " + active);
    }

    /** the mod's pickaxe and sword on its own Tier (needs the world's block tags) */
    static void tools() {
        if (ItemsInit.pickaxe == null || ItemsInit.sword == null) {
            Tally.fail("tools.register", ItemsInit.failure == null ? "not registered"
                : ItemsInit.failure.getClass().getName() + ": " + ItemsInit.failure.getMessage());
            return;
        }
        Tally.ok("tools.register", "cb_clienttest:cb_pickaxe, cb_clienttest:cb_sword");
        class_1799 pick = new class_1799(ItemsInit.pickaxe);
        class_1799 sword = new class_1799(ItemsInit.sword);
        boolean iron = pick.method_7951(class_2246.field_10212.method_9564());
        boolean obsidian = pick.method_7951(class_2246.field_10540.method_9564());
        Tally.check("tools.pickaxeMines", iron && !obsidian,
            "correct tool for iron ore " + iron + ", obsidian " + obsidian + " (1.20.1, tier level 2: true, false)");
        float speed = pick.method_7924(class_2246.field_10340.method_9564());
        float dirt = pick.method_7924(class_2246.field_10566.method_9564());
        Tally.check("tools.pickaxeSpeed", speed == ItemsInit.SPEED && dirt == 1.0f,
            "stone " + speed + ", dirt " + dirt + " (1.20.1: " + ItemsInit.SPEED + ", 1.0)");
        Tally.check("tools.durability", pick.method_7936() == ItemsInit.USES && sword.method_7936() == ItemsInit.USES,
            "max damage " + pick.method_7936() + "/" + sword.method_7936() + " (tier uses " + ItemsInit.USES + ")");
        float damage = ((class_1829) ItemsInit.sword).method_8020();
        Tally.check("tools.swordDamage", damage == ItemsInit.SWORD_DAMAGE + ItemsInit.DAMAGE_BONUS,
            "SwordItem.getDamage " + damage + " (1.20.1: " + (ItemsInit.SWORD_DAMAGE + ItemsInit.DAMAGE_BONUS) + ")");
        Multimap<?, ?> mods = sword.method_7926(class_1304.field_6173);
        Tally.check("tools.attributes", mods != null && mods.size() == 2,
            (mods == null ? "null" : mods.size()) + " main-hand attribute modifiers (1.20.1: damage and speed)");
    }

    /** swall plan item 11 as 1.20.1 has it: the partial tick, the camera at the player's eyes, its near plane */
    static void camera(class_310 mc) throws Exception {
        float[] partial = new float[12];
        boolean inRange = true;
        int distinct = 0;
        for (int i = 0; i < partial.length; i++) {
            partial[i] = ClientTestMod.onRender(mc::method_1488);
            inRange &= partial[i] >= 0f && partial[i] <= 1f;
            boolean seen = false;
            for (int j = 0; j < i; j++) {
                seen |= partial[j] == partial[i];
            }
            distinct += seen ? 0 : 1;
            Thread.sleep(37);
        }
        Tally.check("camera.partialTick", inRange && distinct >= 3,
            "Minecraft.getFrameTime " + Arrays.toString(partial) + " (in [0,1], " + distinct + " distinct)");
        ClientTestMod.attempt("camera.position", () -> {
            class_4184 cam = mc.field_1773.method_19418();
            class_243 pos = cam.method_19326();
            double d = pos.method_1022(mc.field_1724.method_33571());
            Tally.check("camera.position", d < 1.0 && !cam.method_19333() && cam.method_19331() == mc.field_1724,
                String.format("%.3f from the eyes, detached %s, entity is the player %s", d, cam.method_19333(),
                    cam.method_19331() == mc.field_1724));
            class_243 tl = cam.method_36425().method_36426();
            double len = Math.sqrt(tl.field_1352 * tl.field_1352 + tl.field_1351 * tl.field_1351 + tl.field_1350 * tl.field_1350);
            Tally.check("camera.nearPlane", len > 0.0 && len < 2.0, String.format("top-left corner %.3f away", len));
        });
    }

    /** swall plan item 15: the chat line the mod added answers its click event under the chat screen */
    static void chatClickable(class_310 mc) {
        class_338 chat = mc.field_1705.method_1743();
        int h = mc.method_22683().method_4502();
        String found = null;
        for (int y = h - 1; y > h - 200 && found == null; y--) {
            for (int x = 2; x < 80 && found == null; x += 3) {
                class_2583 s = chat.method_1816(x, y);
                if (s != null && s.method_10970() != null && ClientTestMod.CHAT_CMD.equals(s.method_10970().method_10844())) {
                    found = x + "," + y;
                }
            }
        }
        Tally.check("chat.clickable", found != null,
            found == null ? "no style with the mod's click event under the chat lines" : "click event at " + found);
    }
}
