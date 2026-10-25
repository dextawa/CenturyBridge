package cb.clienttest;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1921;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_490;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * The GUI phase: one screen that draws through the 1.20.1 GuiGraphics (DrawContext), Font and RenderType
 * API every frame, each piece a Frames step ("gui.*", "font.*", "rt.*"), on an opaque backdrop so the
 * screenshots of two runs compare.
 */
public class GuiScreen extends class_437 {
    static final int FULL_BRIGHT = 15728880;
    static final class_2960 STONE = new class_2960("textures/block/stone.png");
    static final class_2960 DIAMOND = new class_2960("textures/block/diamond_block.png");
    static final String TEXT = "CenturyBridge";

    public GuiScreen() {
        super(class_2561.method_43470("CB client test: GUI"));
    }

    @Override
    public boolean method_25421() {
        return false;
    }

    @Override
    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
        class_310 mc = class_310.method_1551();
        class_327 font = this.field_22793;
        int w = this.field_22789, h = this.field_22790;
        Frames.step("gui.backdrop", () -> {
            ctx.method_25294(0, 0, w, h, 0xFF202830);
            return null;
        });
        Frames.step("gui.fill", () -> {
            ctx.method_25294(10, 10, 110, 40, 0xFFCC3333);
            return null;
        });
        Frames.step("gui.fillGradient", () -> {
            ctx.method_25296(120, 10, 220, 40, 0xFF3355FF, 0xFF55FF33);
            return null;
        });
        Frames.step("gui.blit", () -> {
            ctx.method_25302(DIAMOND, 230, 10, 0, 0, 30, 30);
            return null;
        });
        Frames.step("gui.drawString", () -> {
            int end = ctx.method_51433(font, TEXT, 10, 50, 0xFFFFFF00, true);
            int expect = 10 + font.method_1727(TEXT) + 1; // 1.20.1: the x after the text, +1 with a shadow
            if (end != expect) {
                throw new AssertionError("drawString returned " + end + ", 1.20.1 answers " + expect);
            }
            return "end=" + end;
        });
        Frames.step("gui.drawCentered", () -> {
            ctx.method_27534(font, class_2561.method_43470("centered " + TEXT), w / 2, h - 40, 0xFFFFFFFF);
            return null;
        });
        Frames.step("gui.pose", () -> {
            ctx.method_51448().method_22903();
            ctx.method_51448().method_46416(10, 62, 0);
            ctx.method_51448().method_22905(2.0f, 2.0f, 1.0f);
            ctx.method_51433(font, "x2 " + TEXT, 0, 0, 0xFF55FFFF, true);
            ctx.method_51448().method_22909();
            return null;
        });
        Frames.step("gui.scissor", () -> {
            ctx.method_44379(10, 85, 60, 100);
            ctx.method_25294(0, 80, 200, 110, 0xFFFF00FF); // only 50x15 of it may show
            ctx.method_44380();
            return null;
        });
        Frames.step("gui.outline", () -> {
            ctx.method_49601(70, 85, 50, 15, 0xFFFFFFFF);
            ctx.method_25292(130, 200, 92, 0xFFFFAA00);
            return null;
        });
        Frames.step("gui.item", () -> {
            ctx.method_51427(new class_1799(class_1802.field_8802), 10, 115);
            ctx.method_51427(new class_1799(class_1802.field_8367), 30, 115); // foil
            class_1799 stack = new class_1799(class_1802.field_8270, 64);
            ctx.method_51427(stack, 50, 115);
            ctx.method_51431(font, stack, 50, 115);
            return null;
        });
        Frames.step("gui.entity", () -> {
            if (mc.field_1724 == null) {
                throw new IllegalStateException("no player");
            }
            class_490.method_2486(ctx, 100, 170, 25, 100 - mouseX, 140 - mouseY, mc.field_1724);
            return null;
        });
        Frames.step("font.drawInBatch", () -> {
            Matrix4f m = ctx.method_51448().method_23760().method_23761();
            int end = font.method_27521(TEXT, 130f, 115f, 0xFFFFFFFF, false, m, ctx.method_51450(),
                class_327.class_6415.field_33993, 0, FULL_BRIGHT);
            ctx.method_51452();
            int expect = 130 + font.method_1727(TEXT);
            if (end != expect) {
                throw new AssertionError("drawInBatch returned " + end + ", 1.20.1 answers " + expect);
            }
            return "end=" + end;
        });
        Frames.step("font.outline", () -> {
            Matrix4f m = ctx.method_51448().method_23760().method_23761();
            font.method_37296(class_2561.method_43470("outlined").method_30937(), 130f, 130f, 0xFFFFFF00, 0xFF000000,
                m, ctx.method_51450(), FULL_BRIGHT);
            ctx.method_51452();
            return null;
        });
        // render types: a 20x20 quad each through the GUI's buffer source, in its own vertex format
        int x = 230;
        x = quad(ctx, "rt.gui", class_1921.method_51784(), x, Fmt.POS_COLOR);
        x = quad(ctx, "rt.entitySolid", class_1921.method_23572(STONE), x, Fmt.ENTITY);
        x = quad(ctx, "rt.entityCutoutNoCull", class_1921.method_23578(STONE), x, Fmt.ENTITY);
        x = quad(ctx, "rt.entityTranslucent", class_1921.method_23580(STONE), x, Fmt.ENTITY);
        x = quad(ctx, "rt.entitySmoothCutout", class_1921.method_23584(STONE), x, Fmt.ENTITY);
        x = 230;
        x = quad2(ctx, "rt.entityDecal", class_1921.method_23586(STONE), x, Fmt.ENTITY);
        x = quad2(ctx, "rt.itemEntityTranslucentCull", class_1921.method_29379(STONE), x, Fmt.ENTITY);
        x = quad2(ctx, "rt.eyes", class_1921.method_23026(STONE), x, Fmt.ENTITY);
        x = quad2(ctx, "rt.text", class_1921.method_23028(STONE), x, Fmt.TEXT);
        Frames.step("gui.tooltip", () -> {
            ctx.method_51438(font, class_2561.method_43470("tooltip " + TEXT), 10, h - 20);
            return null;
        });
        super.method_25394(ctx, mouseX, mouseY, delta);
    }

    enum Fmt { POS_COLOR, ENTITY, TEXT }

    private static int quad(class_332 ctx, String name, class_1921 type, int x, Fmt fmt) {
        draw(ctx, name, type, x, 50, fmt);
        return x + 24;
    }

    private static int quad2(class_332 ctx, String name, class_1921 type, int x, Fmt fmt) {
        draw(ctx, name, type, x, 74, fmt);
        return x + 24;
    }

    private static void draw(class_332 ctx, String name, class_1921 type, int x, int y, Fmt fmt) {
        Frames.step(name, () -> {
            Matrix4f m = ctx.method_51448().method_23760().method_23761();
            Matrix3f n = ctx.method_51448().method_23760().method_23762();
            class_4597.class_4598 src = ctx.method_51450();
            class_4588 vc = src.getBuffer(type);
            float[][] corners = {{x, y, 0, 0}, {x, y + 20, 0, 1}, {x + 20, y + 20, 1, 1}, {x + 20, y, 1, 0}};
            for (float[] c : corners) {
                vc.method_22918(m, c[0], c[1], 0f);
                vc.method_1336(255, 255, 255, 255);
                if (fmt == Fmt.ENTITY) {
                    vc.method_22913(c[2], c[3]).method_22922(0).method_22916(FULL_BRIGHT).method_23763(n, 0f, 0f, 1f);
                } else if (fmt == Fmt.TEXT) {
                    vc.method_22913(c[2], c[3]).method_22916(FULL_BRIGHT);
                }
                vc.method_1344();
            }
            ctx.method_51452();
            return null;
        });
    }
}
