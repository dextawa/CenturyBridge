package cb.swallc;

import java.util.Map;
import net.minecraft.class_310;

/** client check 20 a), c), d) and f) (camera) in code (b: the renderers SwallClient registers; e, the cloud renderer call: not covered) */
final class Check20 {
    private Check20() {
    }

    static int setupAnimCalls;

    /** a mod's TurtleModel subclass, as 1.21.11 mods wrote it (super(root), setupAnim calling super) */
    static final class ModTurtleModel extends net.minecraft.class_614 {
        ModTurtleModel(net.minecraft.class_630 root) {
            super(root);
        }

        @Override
        public void method_17125(net.minecraft.class_10077 state) {
            setupAnimCalls++;
            super.method_17125(state);
        }
    }

    /** a mod scroll area: 200 px of content in a 50 px box, 9 px a wheel notch */
    static final class ModScrollArea extends net.minecraft.class_7528 {
        ModScrollArea() {
            super(0, 0, 100, 50, net.minecraft.class_2561.method_43470("cb-swallc scroll"));
        }

        @Override
        protected int method_44395() {
            return 200;
        }

        @Override
        protected double method_44393() {
            return 9.0;
        }

        @Override
        protected void method_48579(net.minecraft.class_332 graphics, int x, int y, float pt) {
        }

        @Override
        protected void method_47399(net.minecraft.class_6382 narration) {
        }

        boolean bar() {
            return method_44392();
        }
    }

    static void run(class_310 mc, Map<String, String> out) {
        try {
            net.minecraft.class_614 plain = new net.minecraft.class_614(mc.method_31974().method_32072(net.minecraft.class_5602.field_27673));
            ModTurtleModel mod = new ModTurtleModel(mc.method_31974().method_32072(net.minecraft.class_5602.field_27673));
            net.minecraft.class_10077 state = new net.minecraft.class_10077();
            state.field_53601 = true;
            plain.method_17125(state);
            mod.method_17125(state);
            out.put("check20.a.turtleModel", (setupAnimCalls == 1 ? "ok" : "FAIL") + " subclassSetupAnim=" + setupAnimCalls);
        } catch (Throwable t) {
            out.put("check20.a.turtleModel", "FAIL " + t);
        }
        try {
            ModScrollArea area = new ModScrollArea();
            boolean bar = area.bar();
            area.method_25401(10, 10, 0, -1);
            double scrolled = area.method_44387();
            out.put("check20.c.scrollbar", (bar ? "ok" : "FAIL") + " visible=" + bar);
            out.put("check20.c.scrollRate", (scrolled == 9.0 ? "ok" : "FAIL") + " scrolled=" + scrolled);
        } catch (Throwable t) {
            out.put("check20.c.scrollbar", "FAIL " + t);
        }
        try {
            net.minecraft.class_2561 text = net.minecraft.class_2561.method_43470("cb-swallc gui message");
            net.minecraft.class_7591 tag = net.minecraft.class_7591.method_44751();
            int now = mc.field_1705.method_1738();
            net.minecraft.class_303 msg = new net.minecraft.class_303(now, text, null, tag);
            net.minecraft.class_303.class_7590 line = new net.minecraft.class_303.class_7590(now, text.method_30937(), tag, true);
            mc.field_1705.method_1743().method_1812(net.minecraft.class_2561.method_43470("cb-swallc client line"));
            out.put("check20.d.guiMessage", (msg.comp_893() == text && msg.comp_894() == tag && msg.comp_892() == now ? "ok" : "FAIL")
                + " " + msg.comp_893().getString());
            out.put("check20.d.guiMessageLine", (line.comp_897() == tag && line.comp_898() && line.comp_895() == now ? "ok" : "FAIL")
                + " end=" + line.comp_898());
        } catch (Throwable t) {
            out.put("check20.d.guiMessage", "FAIL " + t);
        }
    }

    /**
     * f) a mod Camera subclass's setup(level, player, detached, false, pt) against native 26.1's main camera in third
     * person (the game set to THIRD_PERSON_BACK a few ticks before): the same distance from the eye, collision included
     */
    static void camera(class_310 mc, Map<String, String> out) {
        try {
            double nativeDist = mc.field_1773.method_19418().method_71156().method_1022(mc.field_1724.method_33571());
            net.minecraft.class_4184 cam = new net.minecraft.class_4184() {
            };
            cam.method_19321(mc.field_1687, mc.field_1724, true, false, 0.37F);
            double dist = cam.method_71156().method_1022(mc.field_1724.method_33571());
            float pt = cam.method_55437();
            float mainPt = mc.field_1773.method_19418().method_55437();
            out.put("check20.f.cameraSetup", (Math.abs(dist - nativeDist) < 0.25 ? "ok" : "FAIL") + " behind=" + String.format("%.2f", dist)
                + " native26.1=" + String.format("%.2f", nativeDist));
            out.put("check20.f.cameraPartialTick", (Math.abs(pt - 0.37F) < 1e-6 ? "ok" : "FAIL") + " own=" + pt + " main=" + mainPt);
        } catch (Throwable t) {
            out.put("check20.f.cameraSetup", "FAIL " + t);
        }
    }
}
