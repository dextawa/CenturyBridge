package cb.smx1d;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_11;
import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_259;
import net.minecraft.class_287;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_320;
import net.minecraft.class_4337;
import net.minecraft.class_4351;
import net.minecraft.class_4429;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4888;
import net.minecraft.class_5365;
import net.minecraft.class_5676;
import net.minecraft.class_638;
import net.minecraft.class_7172;
import net.minecraft.class_863;
import net.minecraft.class_868;
import net.minecraft.class_9;
import net.minecraft.class_9799;
import net.minecraft.class_9974;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A 1.21.10-era client mod (intermediary names) climbed 1.21.10 -> 1.21.11, exercising the seven landings of the
 * 1.21.11 step layer: VertexConsumer.addVertex(Matrix4f, ..) (call, own override, own super call) and the abstracts
 * 1.21.11 demands, ShapeRenderer's removed writers, the CycleButton builder, Options.graphicsMode(), Realms FileUpload,
 * ClientLevel sky / cloud colours, DebugRenderer / PathfindingRenderer statics. Started by the native 1.21.11 driver
 * cb_smx1dn ("cbsmx1d.go" set once the world is joined); writes each result to "cbsmx1d.r.<check>" ("ok .." / "FAIL ..")
 * and "cbsmx1d.old.done" at the end. Native counters ("cbsmx1d.n.<key>", bumped by the driver's mixins) and same-tick
 * reference values (JDK Function / Runnable objects the driver puts into the system properties) connect the two mods
 * without a class link.
 */
public class Smx1dOld implements ClientModInitializer {

    /** a VertexConsumer written against 1.21.10: the six abstracts and an override of addVertex(Matrix4f, ..) */
    static final class Counting implements class_4588 {
        int starts, overrides, colors;
        int lastR, lastG, lastB, lastA;
        Matrix4f lastMatrix;

        @Override
        public class_4588 method_22912(float x, float y, float z) {
            starts++;
            return this;
        }

        @Override
        public class_4588 method_1336(int r, int g, int b, int a) {
            colors++;
            lastR = r;
            lastG = g;
            lastB = b;
            lastA = a;
            return this;
        }

        @Override
        public class_4588 method_22913(float u, float v) {
            return this;
        }

        @Override
        public class_4588 method_60796(int u, int v) {
            return this;
        }

        @Override
        public class_4588 method_22921(int u, int v) {
            return this;
        }

        @Override
        public class_4588 method_22914(float x, float y, float z) {
            return this;
        }

        @Override
        public class_4588 method_22918(Matrix4f matrix, float x, float y, float z) {
            overrides++;
            lastMatrix = matrix;
            return class_4588.super.method_22918(matrix, x, y, z);
        }
    }

    /** a 1.21.10 MultiBufferSource handing one counting consumer out for every render type */
    static final class Buffers implements class_4597 {
        final Counting out = new Counting();
        final StringBuilder types = new StringBuilder();

        @Override
        public class_4588 method_73477(class_1921 type) {
            types.append(type).append(' ');
            return out;
        }
    }

    static void r(String name, boolean pass, String detail) {
        System.setProperty("cbsmx1d.r." + name, (pass ? "ok " : "FAIL ") + detail);
    }

    static long n(String key) {
        return Long.parseLong(System.getProperty("cbsmx1d.n." + key, "0"));
    }

    interface Check {
        void run() throws Exception;
    }

    static void guarded(String name, Check c) {
        try {
            c.run();
        } catch (Throwable t) {
            t.printStackTrace();
            r(name, false, "threw " + t);
        }
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
        Thread t = new Thread(Smx1dOld::drive, "cb-smx1d-old");
        t.setDaemon(true);
        t.start();
    }

    static void drive() {
        while (System.getProperty("cbsmx1d.go") == null) {
            sleep(500);
        }
        class_310 mc = class_310.method_1551();
        Object lock = new Object();
        boolean[] done = {false};
        mc.execute(() -> {
            try {
                onRenderThread(mc);
            } finally {
                synchronized (lock) {
                    done[0] = true;
                    lock.notifyAll();
                }
            }
        });
        synchronized (lock) {
            long until = System.currentTimeMillis() + 60000;
            while (!done[0] && System.currentTimeMillis() < until) {
                try {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        guarded("fileUpload", Smx1dOld::fileUpload);
        guarded("text", () -> text(mc));
        System.setProperty("cbsmx1d.old.done", "1");
    }

    static void onRenderThread(class_310 mc) {
        guarded("vertex", Smx1dOld::vertex);
        guarded("shapes", Smx1dOld::shapes);
        guarded("cycleButton", Smx1dOld::cycleButton);
        guarded("graphicsMode", () -> graphicsMode(mc));
        guarded("levelColors", () -> levelColors(mc));
        guarded("debugRender", Smx1dOld::debugRender);
        guarded("renderTypes", Smx1dOld::renderTypes);
        guarded("samplers", () -> samplers(mc));
        guarded("bakeQuad", () -> bakeQuad(mc));
    }

    // 1. VertexConsumer.addVertex(Matrix4f, ..): 1.21.11's takes a Matrix4fc
    static void vertex() {
        Counting c = new Counting();
        long nat = n("addVertex");
        Matrix4f m = new Matrix4f();
        c.method_22918(m, 1, 2, 3);
        r("vertex.callOwn", c.overrides == 1 && c.starts == 1 && c.lastMatrix == m,
            "overrides=" + c.overrides + " starts=" + c.starts + " sameMatrix=" + (c.lastMatrix == m));
        r("vertex.ownSuperReachesNative", n("addVertex") - nat == 1, "native=" + (n("addVertex") - nat));

        // vanilla's Pose form calls 1.21.11's Matrix4fc form; the 1.21.10 override must answer it with the same matrix
        class_4587.class_4665 pose = new class_4587().method_23760();
        c.method_56824(pose, 0, 0, 0);
        r("vertex.overrideFromVanilla", c.overrides == 2 && c.lastMatrix == pose.method_23761(),
            "overrides=" + c.overrides + " sameMatrix=" + (c.lastMatrix == pose.method_23761()));

        // setColor(int): a 1.21.10 default, abstract in 1.21.11
        c.method_39415(0xFF102030);
        r("vertex.setColorFill", c.lastR == 0x10 && c.lastG == 0x20 && c.lastB == 0x30 && c.lastA == 0xFF,
            "argb=" + c.lastA + "," + c.lastR + "," + c.lastG + "," + c.lastB);

        // a vanilla BufferBuilder through the converted call; the native default (and its mixin) runs
        try (class_9799 bytes = new class_9799(1536)) {
            class_287 bb = new class_287(bytes, com.mojang.blaze3d.vertex.VertexFormat.class_5596.field_27382, class_290.field_1592);
            nat = n("addVertex");
            for (int i = 0; i < 4; i++) {
                bb.method_22918(m, i, 0, 0);
            }
            r("vertex.bufferBuilderNative", n("addVertex") - nat == 4, "native=" + (n("addVertex") - nat));
        }
    }

    // 2. ShapeRenderer writers gone from 1.21.11, given back with 1.21.10's bodies
    static void shapes() {
        class_4587 stack = new class_4587();
        class_4587.class_4665 pose = stack.method_23760();
        int[] got = new int[7];
        Counting c = new Counting();
        class_9974.method_62293(pose, c, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0);
        got[0] = c.starts;
        c = new Counting();
        class_9974.method_62292(pose, c, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1);
        got[1] = c.starts;
        c = new Counting();
        class_9974.method_62295(pose, c, new class_238(0, 0, 0, 1, 1, 1), 1, 0, 0, 1);
        got[2] = c.starts;
        c = new Counting();
        class_9974.method_62294(stack, c, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1);
        got[3] = c.starts;
        c = new Counting();
        class_9974.method_62297(pose.method_23761(), c, class_2350.field_11036, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1);
        got[4] = c.starts;
        c = new Counting();
        class_9974.method_62298(stack, c, new Vector3f(), new class_243(0, 1, 0), 0xFFFF0000);
        got[5] = c.starts;
        c = new Counting();
        class_9974.method_62296(stack, c, class_259.method_1077(), 0, 0, 0, 0xFFFFFFFF);
        got[6] = c.starts;
        int[] want = {24, 24, 24, 30, 4, 2, 24};
        String[] names = {"renderLineBox62293", "renderLineBox62292", "renderLineBox62295", "filledBox62294", "renderFaceUp62297",
            "renderVector62298", "renderShapeCube62296"};
        for (int i = 0; i < want.length; i++) {
            r("shapes." + names[i], got[i] == want[i], "vertices=" + got[i] + " want=" + want[i]);
        }
    }

    // 3. CycleButton builder: withInitialValue / displayOnlyValue(boolean) / onOffBuilder(boolean) / builder(Function)
    static void cycleButton() {
        long builds = n("cycleBuild");
        class_5676<String> b = class_5676.<String>method_32606(v -> class_2561.method_43470(v))
            .method_32624("a", "b", "c").method_32619("b").method_35723(0, 0, 100, 20, class_2561.method_43470("cap"));
        r("cycle.initialValue", "b".equals(b.method_32603()), "value=" + b.method_32603());
        class_5676<String> d = class_5676.<String>method_32606(v -> class_2561.method_43470(v))
            .method_32624("a", "b", "c").method_35723(0, 0, 100, 20, class_2561.method_43470("cap"));
        r("cycle.defaultFirst", "a".equals(d.method_32603()), "value=" + d.method_32603());
        class_5676<Boolean> o = class_5676.method_32613(false).method_35723(0, 0, 100, 20, class_2561.method_43470("cap"));
        r("cycle.onOffFalse", Boolean.FALSE.equals(o.method_32603()), "value=" + o.method_32603());
        class_5676<String> only = class_5676.<String>method_32606(v -> class_2561.method_43470(v))
            .method_32624("x", "y").method_71805(true).method_35723(0, 0, 100, 20, class_2561.method_43470("cap"));
        String msg = only.method_25369().getString();
        r("cycle.displayOnlyValue", "x".equals(msg), "message=" + msg);
        r("cycle.nativeBuild", n("cycleBuild") - builds == 4, "native builds=" + (n("cycleBuild") - builds));
    }

    // 4. Options.graphicsMode(): a view over 1.21.11's improvedTransparency / graphicsPreset
    static void graphicsMode(class_310 mc) {
        class_315 options = mc.field_1690;
        Runnable snap = (Runnable) System.getProperties().get("cbsmx1d.snapGraphics");
        int rd = options.method_42503().method_41753();
        long sets = n("optionSet"), gets = n("optionGet"), presets = n("applyPreset");
        class_7172<class_5365> gm = options.method_42534();
        gm.method_41748(class_5365.field_25429);
        System.setProperty("cbsmx1d.g.phase", "fabulous");
        snap.run();
        class_5365 after = gm.method_41753();
        r("graphics.getFabulous", after == class_5365.field_25429, "get=" + after);
        gm.method_41748(class_5365.field_25428);
        System.setProperty("cbsmx1d.g.phase", "fancy");
        snap.run();
        class_5365 fancy = gm.method_41753();
        r("graphics.getNotFabulous", fancy != class_5365.field_25429, "get=" + fancy);
        r("graphics.renderDistanceKept", options.method_42503().method_41753() == rd, "rd " + rd + " -> " + options.method_42503().method_41753());
        r("graphics.noPresetApplied", n("applyPreset") - presets == 0, "applyGraphicsPreset=" + (n("applyPreset") - presets));
        r("graphics.nativeSetGet", n("optionSet") - sets >= 2 && n("optionGet") - gets >= 2,
            "sets=" + (n("optionSet") - sets) + " gets=" + (n("optionGet") - gets));
        r("graphics.statusIds", class_5365.field_25429.method_7362() == 2 && "options.graphics.fabulous".equals(class_5365.field_25429.method_7359())
            && class_5365.method_29592(1) == class_5365.field_25428 && class_5365.method_29592(3) == class_5365.field_25427,
            "id=" + class_5365.field_25429.method_7362() + " key=" + class_5365.field_25429.method_7359() + " byId(1)=" + class_5365.method_29592(1));
    }

    // 6. ClientLevel.getSkyColor / getCloudColor, compared with 1.21.11's attributes in the same tick
    @SuppressWarnings("unchecked")
    static void levelColors(class_310 mc) {
        class_638 level = mc.field_1687;
        class_243 pos = mc.field_1724.method_73189();
        int sky = level.method_23777(pos, 0.0F);
        int cloud = level.method_23785(0.0F);
        int[] want = ((Function<Object, int[]>) System.getProperties().get("cbsmx1d.expectColors")).apply(pos);
        r("level.skyColor", sky == want[0], String.format("old=%08x attr=%08x", sky, want[0]));
        r("level.cloudColor", cloud == want[1], String.format("old=%08x attr=%08x", cloud, want[1]));
    }

    // 7. DebugRenderer / PathfindingRenderer statics into a MultiBufferSource
    static void debugRender() {
        class_4587 stack = new class_4587();
        Buffers box = new Buffers();
        class_863.method_23102(stack, box, new class_238(0, 0, 0, 1, 1, 1), 1, 0, 0, 1);
        r("debug.filledBox", box.out.starts > 0, "vertices=" + box.out.starts + " types=" + box.types);
        Buffers text = new Buffers();
        class_863.method_23107(stack, text, "cb", 0, 64, 0, -1, 0.02F, true, 0.0F, true);
        r("debug.floatingText", true, "vertices=" + text.out.starts + " types=" + text.types);
        Counting shape = new Counting();
        class_863.method_62350(stack, shape, class_259.method_1077(), 0, 0, 0, 1, 1, 1, 1, true);
        r("debug.voxelShape", shape.starts == 24, "vertices=" + shape.starts + " want=24");
        Buffers path = new Buffers();
        class_11 p = new class_11(List.of(new class_9(0, 64, 0), new class_9(1, 64, 0), new class_9(2, 64, 0)), new class_2338(2, 64, 0), true);
        class_868.method_23118(stack, path, p, 0.5F, true, true, 0, 64, 0);
        r("debug.path", path.out.starts > 0, "vertices=" + path.out.starts + " types=" + path.types);
    }

    // 8. text drawing: MultiLineLabel.render / getStyle, AbstractButton.renderString override + super, scrolling string
    /** a 1.21.10 button overriding renderString and calling its super */
    static final class OldButton extends net.minecraft.class_4185 {
        int strings, lastColor;

        OldButton(int x, int y) {
            super(x, y, 120, 20, class_2561.method_43470("cb old button"), b -> { }, net.minecraft.class_4185.field_40754);
        }

        @Override
        public void method_48589(net.minecraft.class_332 g, net.minecraft.class_327 font, int color) {
            strings++;
            lastColor = color;
            super.method_48589(g, font, color);
        }

        /** AbstractWidget.renderScrollingString(graphics, font, message, minX, minY, maxX, maxY, color): protected static */
        static void scrolling(net.minecraft.class_332 g, net.minecraft.class_327 font) {
            method_52718(g, font, class_2561.method_43470("cb scrolling"), 10, 30, 60, 40, -1);
        }
    }

    /** a 1.21.10 MultiLineLabel implementation: render / getStyle / getLineCount / getWidth, nothing else */
    static final class OldLabel implements net.minecraft.class_5489 {
        int renders, lastColor;
        boolean lastShadow;

        @Override
        public int method_73212(net.minecraft.class_332 g, net.minecraft.class_5489.class_11735 align, int x, int y,
                                int lineHeight, boolean shadow, int color) {
            renders++;
            lastColor = color;
            lastShadow = shadow;
            g.method_51439(class_310.method_1551().field_1772, class_2561.method_43470("cb old label"), x, y, color, shadow);
            return y + lineHeight;
        }

        @Override
        public net.minecraft.class_2583 method_72103(net.minecraft.class_5489.class_11735 align, int x, int y, int lineHeight,
                                                    double mouseX, double mouseY) {
            return mouseY >= y && mouseY < y + lineHeight && mouseX >= x
                ? net.minecraft.class_2583.field_24360.method_10975("cbold") : null;
        }

        @Override
        public int method_30887() {
            return 1;
        }

        @Override
        public int method_44048() {
            return 60;
        }
    }

    /** a 1.21.10 ImageButton subclass overriding renderWidget (public there; final on AbstractButton in 1.21.11) */
    static final class OldImage extends net.minecraft.class_344 {
        int renders;

        OldImage(int x, int y) {
            super(x, y, 20, 20, new net.minecraft.class_8666(net.minecraft.class_2960.method_60656("widget/button")), b -> { });
        }

        @Override
        public void method_48579(net.minecraft.class_332 g, int mouseX, int mouseY, float delta) {
            renders++;
            super.method_48579(g, mouseX, mouseY, delta);
        }
    }

    /** a 1.21.10 SpriteIconButton.CenteredIcon subclass overriding renderString */
    static final class OldIcon extends net.minecraft.class_8662.class_8664 {
        int strings;

        OldIcon() {
            super(20, 20, class_2561.method_43470("cb icon"), 16, 16,
                new net.minecraft.class_8666(net.minecraft.class_2960.method_60656("icon/accessibility")), b -> { },
                class_2561.method_43470("cb icon tip"), net.minecraft.class_4185.field_40754);
        }

        @Override
        public void method_48589(net.minecraft.class_332 g, net.minecraft.class_327 font, int color) {
            strings++;
            super.method_48589(g, font, color);
        }
    }

    /** a 1.21.10 button overriding renderScrollingString(graphics, font, inset, color) only */
    static final class OldScroll extends net.minecraft.class_4185 {
        int scrolls, lastInset = -1;

        OldScroll(int x, int y) {
            super(x, y, 120, 20, class_2561.method_43470("cb scroll button"), b -> { }, net.minecraft.class_4185.field_40754);
        }

        @Override
        protected void method_49604(net.minecraft.class_332 g, net.minecraft.class_327 font, int inset, int color) {
            scrolls++;
            lastInset = inset;
            super.method_49604(g, font, inset, color);
        }
    }

    /** a 1.21.10 screen drawing a label through render(graphics, align, ..) and asking getStyle */
    static final class OldScreen extends net.minecraft.class_437 {
        net.minecraft.class_5489 label;
        OldButton old;
        OldImage img;
        OldIcon icon;
        OldScroll scroll;
        int frames, lastEnd = -1, lines, directRenders;
        String style = "none";
        String scrolled = "not run";
        Throwable failed;

        OldScreen() {
            super(class_2561.method_43470("cb smx1d text"));
        }

        @Override
        protected void method_25426() {
            label = net.minecraft.class_5489.method_30890(this.field_22793,
                class_2561.method_43470("cb label line").method_27696(net.minecraft.class_2583.field_24360.method_10975("cbins")), 200);
            lines = label.method_30887();
            old = this.method_37063(new OldButton(10, 60));
            this.method_37063(net.minecraft.class_4185.method_46430(class_2561.method_43470("cb vanilla button"), b -> { })
                .method_46434(10, 90, 120, 20).method_46431());
            img = this.method_37063(new OldImage(140, 60));
            icon = this.method_37063(new OldIcon());
            icon.method_46421(170);
            icon.method_46419(60);
            scroll = this.method_37063(new OldScroll(10, 120));
        }

        @Override
        public void method_25394(net.minecraft.class_332 g, int mouseX, int mouseY, float delta) {
            super.method_25394(g, mouseX, mouseY, delta);
            try {
                lastEnd = label.method_73212(g, net.minecraft.class_5489.class_11735.field_62009, 10, 10, 9, true, -1);
                net.minecraft.class_2583 s = label.method_72103(net.minecraft.class_5489.class_11735.field_62009, 10, 10, 9, 13.0, 12.0);
                style = s == null ? "null" : String.valueOf(s.method_10955());
                OldButton.scrolling(g, this.field_22793);
                scrolled = "ran";
                net.minecraft.class_344 asImage = img; // a call naming ImageButton from outside its hierarchy
                int before = img.renders;
                asImage.method_48579(g, mouseX, mouseY, delta);
                directRenders += img.renders - before;
            } catch (Throwable t) {
                failed = t;
            }
            frames++;
        }
    }

    static void text(class_310 mc) throws Exception {
        long labels0 = n("labelVisit"), oldLabel0 = n("defaultLabel.old"), vanLabel0 = n("defaultLabel.vanilla");
        long image0 = n("imageContents.old"), sprite0 = n("defaultSprite.old");
        OldScreen[] s = {null};
        mc.execute(() -> {
            s[0] = new OldScreen();
            mc.method_1507(s[0]);
        });
        sleep(4000);
        OldScreen sc = s[0];
        mc.execute(() -> mc.method_1507(null));
        sleep(500);
        if (sc.failed != null) {
            sc.failed.printStackTrace();
        }
        r("text.frames", sc.frames > 0 && sc.failed == null, "frames=" + sc.frames + " failed=" + sc.failed);
        r("text.labelRender", sc.lastEnd == 10 + 9 * sc.lines, "end=" + sc.lastEnd + " lines=" + sc.lines);
        r("text.labelNative", n("labelVisit") - labels0 >= 2L * sc.frames, "visitLines=" + (n("labelVisit") - labels0));
        r("text.labelStyle", "cbins".equals(sc.style), "insertion=" + sc.style);
        r("text.scrollingStatic", "ran".equals(sc.scrolled), sc.scrolled);
        r("text.oldRenderString", sc.old.strings > 0 && sc.old.lastColor == -1, "renderString=" + sc.old.strings
            + String.format(" color=%08x", sc.old.lastColor));
        r("text.oldDefaultLabelNative", n("defaultLabel.old") - oldLabel0 > 0, "renderDefaultLabel=" + (n("defaultLabel.old") - oldLabel0));
        r("text.vanillaDefaultLabelNative", n("defaultLabel.vanilla") - vanLabel0 > 0,
            "renderDefaultLabel=" + (n("defaultLabel.vanilla") - vanLabel0));
        r("text.oldRenderWidget", sc.img.renders >= sc.frames && sc.frames > 0, "renderWidget=" + sc.img.renders + " frames=" + sc.frames);
        r("text.oldRenderWidgetSuperNative", n("imageContents.old") - image0 > 0, "renderContents=" + (n("imageContents.old") - image0));
        r("text.renderWidgetCall", sc.directRenders > 0, "direct=" + sc.directRenders);
        r("text.oldIconString", sc.icon.strings > 0, "renderString=" + sc.icon.strings);
        r("text.oldIconSpriteNative", n("defaultSprite.old") - sprite0 > 0, "renderDefaultSprite=" + (n("defaultSprite.old") - sprite0));
        r("text.oldScrollingHook", sc.scroll.scrolls > 0 && sc.scroll.lastInset == 2, "renderScrollingString=" + sc.scroll.scrolls
            + " inset=" + sc.scroll.lastInset);
        oldLabelInVanillaScreen(mc);
    }

    /** a 1.21.10 MultiLineLabel implementation in a vanilla 1.21.11 screen (ConfirmScreen draws its label through visitLines) */
    static void oldLabelInVanillaScreen(class_310 mc) throws Exception {
        OldLabel label = new OldLabel();
        net.minecraft.class_405[] s = {null};
        mc.execute(() -> {
            s[0] = new net.minecraft.class_405(() -> { }, (a, b) -> { }, class_2561.method_43470("cb confirm"),
                class_2561.method_43470("cb confirm body"), false);
            mc.method_1507(s[0]);
        });
        sleep(1000);
        Throwable[] failed = {null};
        mc.execute(() -> {
            try {
                java.lang.reflect.Field f = net.minecraft.class_405.class.getDeclaredField("field_2365");
                f.setAccessible(true);
                f.set(s[0], label);
            } catch (Throwable t) {
                failed[0] = t;
            }
        });
        sleep(2000);
        mc.execute(() -> mc.method_1507(null));
        sleep(500);
        r("text.oldLabelVanillaScreen", failed[0] == null && label.renders > 0 && label.lastColor == -1 && label.lastShadow,
            "render=" + label.renders + String.format(" color=%08x", label.lastColor) + " shadow=" + label.lastShadow
            + " failed=" + failed[0]);
    }

    // 5. Realms FileUpload.upload() / cancel() / isFinished()
    static void fileUpload() throws Exception {
        File f = File.createTempFile("cbsmx1d", ".tar.gz");
        f.deleteOnExit();
        java.nio.file.Files.write(f.toPath(), new byte[64]);
        class_4888 info = class_4888.method_25088("{\"uploadEndpoint\":\"127.0.0.1\",\"port\":9,\"worldClosed\":true,\"token\":\"t\"}");
        class_320 session = new class_320("cbtest", UUID.randomUUID(), "0", Optional.empty(), Optional.empty());

        class_4337 cancelled = new class_4337(f, 1L, 1, info, session, "1.21.10", "release", new class_4351());
        cancelled.method_20970();
        class_4429 a = cancelled.method_20973();
        r("upload.cancelledFirst", a.field_20205 == -1, "status=" + a.field_20205);

        class_4337 up = new class_4337(f, 1L, 1, info, session, "1.21.10", "release", new class_4351());
        long t0 = System.currentTimeMillis();
        class_4429 b = up.method_20973();
        r("upload.unreachableEmpty", b.field_20205 == -1, "status=" + b.field_20205 + " in " + (System.currentTimeMillis() - t0) + " ms");
        r("upload.finished", up.method_20978(), "isFinished=" + up.method_20978());
        class_4429 again = up.method_20973();
        r("upload.secondCallEmpty", again.field_20205 == -1, "status=" + again.field_20205);
    }

    // ---- smx1k: 1.21.10 RenderType subclass, RenderPass.bindSampler / GpuTexture sampler state, FaceBakery.bakeQuad

    /** a 1.21.10 RenderType subclass: the public constructor with runnables and the four abstracts */
    static class LegacyType extends class_1921 {
        static int setups, clears;
        final class_1921 base = class_1921.method_23577();

        LegacyType(String name) {
            super(name, 4096, true, false, () -> setups++, () -> clears++);
        }

        @Override
        public void method_60895(net.minecraft.class_9801 mesh) {
            base.method_60895(mesh);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexFormat method_23031() {
            return base.method_23031();
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexFormat.class_5596 method_23033() {
            return base.method_23033();
        }

        @Override
        public com.mojang.blaze3d.pipeline.RenderPipeline method_73243() {
            return base.method_73243();
        }
    }

    /** its own setupRenderState override with a super call */
    static final class OwnSetupType extends LegacyType {
        static int own;

        OwnSetupType() {
            super("cb_smx1d_own");
        }

        @Override
        public void method_23516() {
            own++;
            super.method_23516();
        }
    }

    static void renderTypes() {
        LegacyType t = new LegacyType("cb_smx1d_legacy");
        class_1921 asType = t;
        r("rt.ctorValues", asType.method_22722() == 4096 && asType.method_23037() && !asType.method_60894(),
            "bufferSize=" + asType.method_22722() + " crumbling=" + asType.method_23037() + " sort=" + asType.method_60894());
        int s0 = LegacyType.setups, c0 = LegacyType.clears;
        asType.method_23516();
        asType.method_23518();
        r("rt.setupClearRunnables", LegacyType.setups == s0 + 1 && LegacyType.clears == c0 + 1,
            "setups=" + (LegacyType.setups - s0) + " clears=" + (LegacyType.clears - c0));
        r("rt.getName", "cb_smx1d_legacy".equals(asType.method_68484()), "name=" + asType.method_68484());
        class_1921 own = new OwnSetupType();
        s0 = LegacyType.setups;
        own.method_23516();
        r("rt.ownOverrideAndSuper", OwnSetupType.own == 1 && LegacyType.setups == s0 + 1,
            "own=" + OwnSetupType.own + " base=" + (LegacyType.setups - s0));
        class_1921 solid = class_1921.method_23577();
        solid.method_23516();
        solid.method_23518();
        r("rt.vanillaSetupClear", true, "no throw on " + solid.method_68484());
    }

    static void samplers(class_310 mc) {
        com.mojang.blaze3d.systems.GpuDevice dev = com.mojang.blaze3d.systems.RenderSystem.getDevice();
        com.mojang.blaze3d.textures.GpuTexture tex = dev.createTexture("cb_smx1d", com.mojang.blaze3d.textures.GpuTexture.USAGE_TEXTURE_BINDING
            | com.mojang.blaze3d.textures.GpuTexture.USAGE_COPY_DST, com.mojang.blaze3d.textures.TextureFormat.RGBA8, 4, 4, 1, 1);
        tex.setAddressMode(com.mojang.blaze3d.textures.AddressMode.CLAMP_TO_EDGE);
        tex.setTextureFilter(com.mojang.blaze3d.textures.FilterMode.LINEAR, false);
        com.mojang.blaze3d.textures.GpuTextureView view = dev.createTextureView(tex);
        net.minecraft.class_1044 atlas = mc.method_1531().method_4619(net.minecraft.class_1059.field_5275);
        long n0 = n("bindTexture");
        com.mojang.blaze3d.systems.RenderPass pass = dev.createCommandEncoder().createRenderPass(() -> "cb_smx1d",
            mc.method_1522().method_71639(), java.util.OptionalInt.empty());
        try {
            pass.bindSampler("Sampler0", view);
            Object s0 = System.getProperties().get("cbsmx1d.lastSampler");
            r("sampler.textureState", "CLAMP_TO_EDGE/CLAMP_TO_EDGE/LINEAR/LINEAR/0".equals(System.getProperty("cbsmx1d.lastSamplerState")),
                "state=" + System.getProperty("cbsmx1d.lastSamplerState") + " sampler=" + s0);
            pass.bindSampler("Sampler1", atlas.method_71659());
            Object expect = ((Function<Object, Object>) System.getProperties().get("cbsmx1d.atlasSampler")).apply(atlas);
            Object s1 = System.getProperties().get("cbsmx1d.lastSampler");
            r("sampler.ownerTexture", s1 != null && s1 == expect, "bound=" + s1 + " atlas's=" + expect);
            pass.bindSampler("Sampler1", null);
            r("sampler.nullUnbinds", "null".equals(System.getProperty("cbsmx1d.lastSamplerState")),
                "state=" + System.getProperty("cbsmx1d.lastSamplerState"));
        } finally {
            pass.close();
            view.close();
            tex.close();
        }
        r("sampler.nativeBindTexture", n("bindTexture") - n0 == 3, "native bindTexture HEAD x" + (n("bindTexture") - n0));
    }

    static void bakeQuad(class_310 mc) {
        net.minecraft.class_1059 atlas = (net.minecraft.class_1059) mc.method_1531().method_4619(net.minecraft.class_1059.field_5275);
        net.minecraft.class_1058 sprite = atlas.method_4608(net.minecraft.class_2960.method_60656("block/stone"));
        net.minecraft.class_783 face = new net.minecraft.class_783(class_2350.field_11036, -1, "#all",
            new net.minecraft.class_783.class_10800(0, 0, 16, 16), net.minecraft.class_10821.field_57029);
        long n0 = n("bakeQuad");
        // the identity ModelState as the mod's own (ModelState is unchanged at 1.21.11, all defaults); ModelRotation's
        // enum constants (class_1086.field_5350 ...) are a separate open area at 1.21.11 (class_1086 is no longer an enum)
        net.minecraft.class_3665 identity = new net.minecraft.class_3665() {
        };
        net.minecraft.class_777 q = net.minecraft.class_796.method_3468(new Vector3f(0, 0, 0), new Vector3f(16, 16, 16), face, sprite,
            class_2350.field_11036, identity, null, true, 0);
        r("bake.quad", q != null && q.comp_3723() == class_2350.field_11036 && q.comp_3724() == sprite,
            "direction=" + (q == null ? null : q.comp_3723()) + " sprite=" + (q == null ? null : q.comp_3724()));
        r("bake.nativeHook", n("bakeQuad") - n0 == 1, "native bakeQuad HEAD x" + (n("bakeQuad") - n0));
        modelRotation();
    }

    // ModelRotation: 1.21.10's enum constants on 1.21.11's per-group instances
    static void modelRotation() {
        net.minecraft.class_1086 x0y0 = net.minecraft.class_1086.field_5350;
        net.minecraft.class_1086 x90y180 = net.minecraft.class_1086.field_5367;
        r("rot.identity", x0y0.method_35808() == net.minecraft.class_4990.field_23292 && x0y0.method_3509().method_22936().equals(new org.joml.Matrix4f()),
            "group=" + x0y0.method_35808());
        r("rot.byQuadrants", net.minecraft.class_1086.method_68008(net.minecraft.class_10821.field_57030, net.minecraft.class_10821.field_57031) == x90y180,
            "by(R90,R180)=" + net.minecraft.class_1086.method_68008(net.minecraft.class_10821.field_57030, net.minecraft.class_10821.field_57031));
        net.minecraft.class_1086[] all = net.minecraft.class_1086.values();
        java.util.Set<net.minecraft.class_1086> distinct = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        distinct.addAll(java.util.Arrays.asList(all));
        r("rot.values", all.length == 16 && all[0] == x0y0 && all[6] == x90y180 && distinct.size() == 16,
            "n=" + all.length + " distinct=" + distinct.size());
        r("rot.valueOf", net.minecraft.class_1086.valueOf("X90_Y180") == x90y180 && x90y180.name().equals("X90_Y180") && x90y180.ordinal() == 6,
            "name=" + x90y180.name() + " ordinal=" + x90y180.ordinal());
        // what 1.21.11's own blockstate variants use for x=90, y=180 is the same object
        Object nativeRot = ((Function<Object[], Object>) System.getProperties().get("cbsmx1d.nativeRotation"))
            .apply(new Object[] {net.minecraft.class_10821.field_57030, net.minecraft.class_10821.field_57031});
        r("rot.sameAsNative", nativeRot == x90y180, "native=" + nativeRot + " old=" + x90y180);
    }
}
