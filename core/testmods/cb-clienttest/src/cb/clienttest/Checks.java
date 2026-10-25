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

    /** s9 check 4b as 1.20.1 has it: the offline player's skin is a default skin texture */
    static void skin(class_310 mc) {
        class_2960 loc = mc.method_1582().method_44705(mc.field_1724.method_7334());
        Tally.check("skin.default", loc != null && loc.method_12832().startsWith("textures/entity/player/"),
            "getInsecureSkinLocation: " + loc);
    }

    /** s9 check 1 as 1.20.1 has it: what InputScreen's overrides recorded from the real X input */
    static void input(InputScreen in) {
        if (!in.done) {
            Tally.fail("input.keyPressed", "the input sequence never arrived (no F9): " + in.pressed.size() + " presses");
            return;
        }
        StringBuilder mods = new StringBuilder(), rel = new StringBuilder();
        java.util.Set<Integer> scancodes = new java.util.HashSet<>();
        for (int[] p : in.pressed) {
            if (p[0] == 65) {
                mods.append(p[2]).append(' ');
                scancodes.add(p[1]);
            }
        }
        for (int[] p : in.released) {
            if (p[0] == 65) {
                rel.append(p[2]).append(' ');
            }
        }
        Tally.check("input.keyPressed", mods.toString().equals("0 1 2 ") && scancodes.size() == 1 && !scancodes.contains(0),
            "key 65 mods " + mods + "scancodes " + scancodes + " (1.20.1: 0 1 2, one non-zero scancode)");
        Tally.check("input.keyReleased", rel.toString().equals("0 1 2 "), "key 65 released with mods " + rel + "(1.20.1: 0 1 2)");
        StringBuilder h = new StringBuilder();
        for (boolean[] b : in.helpers) {
            h.append(b[0] ? 'S' : '-').append(b[1] ? 'C' : '-').append(b[2] ? 'A' : '-').append(' ');
        }
        Tally.check("input.modifierHelpers", h.toString().equals("--- S-- -CA "),
            "hasShiftDown/hasControlDown/isSelectAll per A press: " + h + "(1.20.1: --- S-- -CA)");
        Tally.check("input.selectAll", "aA".equals(in.highlightedAfterSelectAll),
            "EditBox highlighted after Ctrl+A: \"" + in.highlightedAfterSelectAll + "\" (1.20.1: \"aA\")");
        StringBuilder ch = new StringBuilder();
        for (int[] c : in.chars) {
            ch.append(String.format("%04x/%d ", c[0], c[1]));
        }
        Tally.check("input.charTyped", ch.toString().equals("0061/0 0041/1 00e9/0 d83d/0 de00/0 "),
            "charTyped " + ch + "(1.20.1: 0061/0 0041/1 00e9/0 d83d/0 de00/0)");
        String value = in.edit.method_1882();
        Tally.check("input.editBox", new String(Character.toChars(0xe9)).concat(new String(Character.toChars(0x1f600))).equals(value),
            "EditBox value " + value.codePoints().mapToObj(Integer::toHexString).collect(java.util.stream.Collectors.toList())
                + " (1.20.1: [e9, 1f600])");
        double[][] want = {{InputScreen.WIDGET_X + InputScreen.WIDGET_W / 2.0, InputScreen.WIDGET_Y + InputScreen.WIDGET_H / 2.0, 0, 0},
            {InputScreen.RIGHT_X, InputScreen.RIGHT_Y, 1, 0}, {InputScreen.DRAG_X, InputScreen.DRAG_Y, 0, 0},
            {InputScreen.SHIFT_X, InputScreen.SHIFT_Y, 0, 1}};
        Tally.check("input.mouseClicked", matches(in.clicks, want, true), "clicks (x,y,button,shift) " + show(in.clicks));
        Tally.check("input.widgetOnClick", in.widget.onClicks.size() == 1, in.widget.onClicks.size() + " onClick calls "
            + show(in.widget.onClicks));
        Tally.check("input.mouseDragged", in.drags > 0 && Math.abs(in.dragDx - InputScreen.DRAG_DX) <= 2
            && Math.abs(in.dragDy - InputScreen.DRAG_DY) <= 2, in.drags + " drags, dx " + in.dragDx + ", dy " + in.dragDy
            + " (moved " + InputScreen.DRAG_DX + ", " + InputScreen.DRAG_DY + ")");
        Tally.check("input.mouseReleased", matches(in.releases, want, false), "releases (x,y,button) " + show(in.releases));
    }

    /** the recorded clicks (withShift) or releases against the sequence; the drag releases where it ended */
    private static boolean matches(List<double[]> got, double[][] want, boolean withShift) {
        if (got.size() != want.length) {
            return false;
        }
        for (int i = 0; i < want.length; i++) {
            double[] g = got.get(i);
            double wx = want[i][0] + (!withShift && i == 2 ? InputScreen.DRAG_DX : 0);
            double wy = want[i][1] + (!withShift && i == 2 ? InputScreen.DRAG_DY : 0);
            if (Math.abs(g[0] - wx) > 1.5 || Math.abs(g[1] - wy) > 1.5 || g[2] != want[i][2]
                || (withShift && g[3] != want[i][3])) {
                return false;
            }
        }
        return true;
    }

    private static String show(List<double[]> l) {
        StringBuilder b = new StringBuilder("[");
        for (double[] d : l) {
            b.append(Arrays.toString(d));
        }
        return b.append(']').toString();
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
