package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11735;
import net.minecraft.class_12225;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4264;
import net.minecraft.class_5481;
import net.minecraft.class_5489;
import net.minecraft.class_9848;
import org.joml.Matrix3x2f;

/**
 * 1.21.10's text drawing into a GuiGraphics, given back on 1.21.11, which draws widget and label text through an
 * ActiveTextCollector (class_12225) instead (javap -c -p of both code jars):
 * <ul>
 * <li>MultiLineLabel.render(graphics, align, x, y, lineHeight, shadow, color) and getStyle(align, x, y, lineHeight,
 *     mouseX, mouseY): 1.21.11 keeps only visitLines(align, x, y, lineHeight, collector) (method_75816), which hands the
 *     collector each line at the x 1.21.10 computed (align.method_73214(x, width)) and answers the y below the last line.
 *     The lines are collected ({@link Lines}) and 1.21.10's bodies run over them: drawString(font, line, x, y, color)
 *     per line (1.21.10 never read {@code shadow}); the line under the mouse and the font splitter's style at the
 *     offset. The font is the label's own (MultiLineLabel$2's font field, as 1.21.10's bodies read it).</li>
 * <li>AbstractWidget.renderScrollingString (method_49604) and its statics (method_52718, method_49605): 1.21.10's
 *     bodies over GuiGraphics' scissor and drawString / drawCenteredString, which 1.21.11 still has.</li>
 * <li>AbstractButton.renderString(graphics, font, color) (method_48589): 1.21.10's body (renderScrollingString with an
 *     inset of 2), restored on AbstractButton. 1.21.11's final renderWidget draws the text through
 *     renderContents -> renderDefaultLabel(collector) (method_75793); a 1.21.10 button overriding renderString is
 *     reached from there ({@link #renderStringBridge}) with 1.21.10's arguments: the collector's GuiGraphics, the
 *     client font and ARGB.color(alpha, active ? white : 0xFFA0A0A0).</li>
 * <li>renderContents (method_75752) is abstract in 1.21.11; a 1.21.10 button class had renderWidget's body from
 *     AbstractButton: the sprite, then the text. {@link #renderContents} is that, as 1.21.11's Button.Plain draws it.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbTextDrawing {
    private CbTextDrawing() {
    }

    /** implemented by MultiLineLabel$2 (mixin/CbMultiLineLabelFontAccess): the label's font */
    public interface LabelFont {
        class_327 cb$v1_21_11$font();
    }

    /** implemented by StringSplitter (mixin/CbStringSplitterRestoredMixin): 1.21.10's componentStyleAtWidth */
    public interface SplitterStyle {
        class_2583 cb$v1_21_11$styleAtWidth(class_5481 seq, int width);
    }

    /** implemented by GuiGraphics' collector (mixin/CbGuiCollectorAccess): the GuiGraphics it draws into */
    public interface CollectorGraphics {
        class_332 cb$v1_21_11$graphics();
    }

    /** implemented by AbstractWidget (mixin/CbAbstractWidgetRestoredMixin) */
    public interface WidgetText {
        /** this.renderScrollingString(graphics, font, inset, color), virtually */
        void cb$v1_21_11$scrollingString(class_332 graphics, class_327 font, int inset, int color);
    }

    /** implemented by AbstractButton (mixin/CbAbstractButtonRestoredMixin) */
    public interface ButtonText {
        /** this.renderDefaultSprite(graphics) (method_75794) */
        void cb$v1_21_11$sprite(class_332 graphics);

        /** this.renderDefaultLabel(collector) (method_75793), virtually */
        void cb$v1_21_11$label(class_12225 collector);
    }

    /** implemented by SpriteIconButton (mixin/CbSpriteIconButtonAccess) */
    public interface SpriteIcon {
        int cb$v1_21_11$spriteWidth();

        int cb$v1_21_11$spriteHeight();

        /** this.renderSprite(graphics, x, y) (method_75817) */
        void cb$v1_21_11$icon(class_332 graphics, int x, int y);
    }

    /**
     * 1.21.10 MultiLineLabel.render / getStyle, restored as defaults of MultiLineLabel
     * (mixin/CbMultiLineLabelRestoredMixin): 1.21.11's labels take the defaults ({@link #method_73212} /
     * {@link #method_72103} over visitLines); a 1.21.10 implementation overrides them.
     */
    public interface LegacyLabel {
        int method_73212(class_332 graphics, class_11735 align, int x, int y, int lineHeight, boolean shadow, int color);

        class_2583 method_72103(class_11735 align, int x, int y, int lineHeight, double mouseX, double mouseY);
    }

    /** implemented by GuiGraphics (mixin/CbGuiGraphicsMouseAccess): the mouse position its collectors hit-test with */
    public interface GraphicsMouse {
        int cb$v1_21_11$mouseX();

        int cb$v1_21_11$mouseY();
    }

    /** implemented by ActiveTextCollector.ClickableStyleFinder (mixin/CbStyleFinderAccess) */
    public interface StyleFinder {
        int cb$v1_21_11$x();

        int cb$v1_21_11$y();

        /** the finder's own style filter (method_75776): kept when it has a click event (or an insertion, if asked) */
        void cb$v1_21_11$offer(class_2583 style);
    }

    /**
     * A 1.21.10 button's renderWidget override, renamed (overrideRenames): 1.21.11's AbstractButton.renderWidget is
     * final; its head hands the call to this (mixin/CbAbstractButtonRenderWidgetMixinHooks).
     */
    public interface LegacyRenderWidget {
        void cb$renderWidget(class_332 graphics, int mouseX, int mouseY, float partialTick);
    }

    private static final ThreadLocal<java.util.Set<Object>> RENDER_WIDGET =
        ThreadLocal.withInitial(() -> java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));

    /**
     * First entry into a 1.21.10 button's renderWidget on this thread: its override runs; its super.renderWidget(..)
     * comes back as a re-entry and runs vanilla's final body. Its own set, not {@link Guard}'s: the renderString /
     * renderScrollingString hooks that body reaches guard the same object.
     */
    public static boolean renderWidgetEnter(Object button) {
        return RENDER_WIDGET.get().add(button);
    }

    public static void renderWidgetExit(Object button) {
        RENDER_WIDGET.get().remove(button);
    }

    /**
     * visitLines(align, x, y, lineHeight, collector) (method_75816, abstract in 1.21.11) of a 1.21.10 MultiLineLabel
     * implementation, which has 1.21.10's render(graphics, ..) and getStyle(..) only (abstractFills). The collectors
     * 1.21.11 hands a label (javap of every implementor and caller):
     * <ul>
     * <li>a GuiGraphics' collector (class_332$class_12229, every screen drawing a label): the label's render into
     *     that GuiGraphics, with the colour that collector draws lines in (ARGB.white(params alpha)) and the shadow
     *     1.21.10's screens passed (true; 1.21.11's collector draws with one too); then the style getStyle answers at
     *     the GuiGraphics' mouse goes to the collector, which keeps it as hovered / clicked per its mode, as it does
     *     for the styles of the lines it draws itself;</li>
     * <li>a ClickableStyleFinder (class_12225$class_12226): getStyle at the finder's position, through the finder's
     *     own filter;</li>
     * <li>any other collector would need the lines, which a 1.21.10 implementation never exposes: nothing is handed
     *     to it.</li>
     * </ul>
     * Answers the y below the last line, as visitLines does.
     */
    public static int visitLines(class_5489 label, class_11735 align, int x, int y, int lineHeight, class_12225 collector) {
        if (collector instanceof CollectorGraphics g) {
            class_332 graphics = g.cb$v1_21_11$graphics();
            LegacyLabel old = (LegacyLabel) label;
            int end = old.method_73212(graphics, align, x, y, lineHeight, true,
                class_9848.method_61317(collector.method_75760().comp_5152()));
            GraphicsMouse m = (GraphicsMouse) graphics;
            class_2583 style = old.method_72103(align, x, y, lineHeight, m.cb$v1_21_11$mouseX(), m.cb$v1_21_11$mouseY());
            if (style != null) {
                @SuppressWarnings("unchecked")
                java.util.function.Consumer<class_2583> styles = (java.util.function.Consumer<class_2583>) (Object) collector;
                styles.accept(style); // GuiGraphics' collector is its own style consumer (method_75791)
            }
            return end;
        }
        if (collector instanceof StyleFinder f) {
            class_2583 style = ((LegacyLabel) label).method_72103(align, x, y, lineHeight, f.cb$v1_21_11$x(), f.cb$v1_21_11$y());
            if (style != null) {
                f.cb$v1_21_11$offer(style);
            }
        }
        return y + lineHeight * label.method_30887();
    }

    /** the old renderString a 1.21.10 button declares (an override hook's adapter) */
    public interface LegacyRenderString {
        void method_48589(class_332 graphics, class_327 font, int color);
    }

    /** the lines a label hands its collector, at the x and y it hands them */
    static final class Lines implements class_12225 {
        final class_327 font;
        final List<class_5481> seqs = new ArrayList<>();
        final List<int[]> at = new ArrayList<>();
        class_12225.class_12227 params = new class_12225.class_12227(new Matrix3x2f());

        Lines(class_327 font) {
            this.font = font;
        }

        @Override
        public class_12225.class_12227 method_75760() {
            return this.params;
        }

        @Override
        public void method_75764(class_12225.class_12227 p) {
            this.params = p;
        }

        @Override
        public void method_75766(class_11735 align, int x, int y, class_12225.class_12227 p, class_5481 seq) {
            this.seqs.add(seq);
            this.at.add(new int[] {align.method_75792(x, this.font, seq), y});
        }

        @Override
        public void method_75773(class_2561 text, int x, int left, int right, int top, int bottom, class_12225.class_12227 p) {
            // scrolling text, as GuiGraphics' collector lays it out (labels never hand one)
            this.method_75772(text, x, left, right, top, bottom, this.font.method_27525(text), 9, p);
        }
    }

    static class_327 font(class_5489 label) {
        return label instanceof LabelFont f ? f.cb$v1_21_11$font() : class_310.method_1551().field_1772;
    }

    /** 1.21.10 MultiLineLabel.render(graphics, align, x, y, lineHeight, shadow, color): the y below the last line */
    public static int method_73212(class_5489 label, class_332 graphics, class_11735 align, int x, int y, int lineHeight,
                             boolean shadow, int color) {
        class_327 font = font(label);
        Lines lines = new Lines(font);
        int end = label.method_75816(align, x, y, lineHeight, lines);
        for (int i = 0; i < lines.seqs.size(); i++) {
            int[] p = lines.at.get(i);
            graphics.method_35720(font, lines.seqs.get(i), p[0], p[1], color);
        }
        return end;
    }

    /** 1.21.10 MultiLineLabel.getStyle(align, x, y, lineHeight, mouseX, mouseY) */
    public static class_2583 method_72103(class_5489 label, class_11735 align, int x, int y, int lineHeight, double mouseX,
                                   double mouseY) {
        class_327 font = font(label);
        Lines lines = new Lines(font);
        label.method_75816(align, x, y, lineHeight, lines);
        int i = class_3532.method_15357((mouseY - y) / lineHeight);
        if (i < 0 || i >= lines.seqs.size()) {
            return null;
        }
        int lx = lines.at.get(i)[0];
        if (mouseX < lx) {
            return null;
        }
        return ((SplitterStyle) font.method_27527()).cb$v1_21_11$styleAtWidth(lines.seqs.get(i), class_3532.method_15357(mouseX - lx));
    }

    /** 1.21.10 AbstractWidget.renderScrollingString(graphics, font, message, minX, minY, maxX, maxY, color) */
    public static void method_52718(class_332 graphics, class_327 font, class_2561 message, int minX, int minY, int maxX,
                                    int maxY, int color) {
        method_49605(graphics, font, message, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    /** 1.21.10 AbstractWidget.renderScrollingString(graphics, font, message, centerX, minX, minY, maxX, maxY, color) */
    public static void method_49605(class_332 graphics, class_327 font, class_2561 message, int centerX, int minX, int minY,
                                    int maxX, int maxY, int color) {
        int width = font.method_27525(message);
        int y = (minY + maxY - 9) / 2 + 1;
        int room = maxX - minX;
        if (width > room) {
            int over = width - room;
            double seconds = class_156.method_658() / 1000.0;
            double period = Math.max(over * 0.5, 3.0);
            double t = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * seconds / period)) / 2.0 + 0.5;
            double offset = class_3532.method_16436(t, 0.0, over);
            graphics.method_44379(minX, minY, maxX, maxY);
            graphics.method_27535(font, message, minX - (int) offset, y, color);
            graphics.method_44380();
        } else {
            int x = class_3532.method_15340(centerX, minX + width / 2, maxX - width / 2);
            graphics.method_27534(font, message, x, y, color);
        }
    }

    /**
     * renderContents of a 1.21.10 button class (abstract in 1.21.11): 1.21.10's AbstractButton.renderWidget drew the
     * sprite and then renderString; 1.21.11's final renderWidget draws the focus outline after renderContents, as
     * 1.21.10's did after renderString. The text goes through renderDefaultLabel over the GuiGraphics' collector, as
     * 1.21.11's Button.Plain does, so a 1.21.10 renderString override is reached there ({@link #renderStringBridge}).
     */
    public static void renderContents(class_4264 button, class_332 graphics, int mouseX, int mouseY, float partialTick) {
        ButtonText b = (ButtonText) button;
        b.cb$v1_21_11$sprite(graphics);
        b.cb$v1_21_11$label(graphics.method_75787(button, class_332.class_12228.field_63850));
    }

    /** the colour 1.21.10's AbstractButton.renderWidget passed renderString */
    public static int legacyColor(class_4264 b) {
        return class_9848.method_71346(b.method_75798(), b.field_22763 ? -1 : -6250336);
    }

    /**
     * renderDefaultLabel(collector) of a 1.21.10 button declaring renderString: its renderString with 1.21.10's
     * arguments (AbstractButton.renderWidget passed the client font and ARGB.color(alpha, active ? -1 : 0xFFA0A0A0)).
     * A collector that is not a GuiGraphics' (none in 1.21.11 reaches here) goes to the label again, which the guard
     * hands to the superclass.
     */
    public static void renderStringBridge(LegacyRenderString self, class_12225 collector) {
        if (collector instanceof CollectorGraphics g) {
            self.method_48589(g.cb$v1_21_11$graphics(), class_310.method_1551().field_1772, legacyColor((class_4264) self));
        } else {
            ((ButtonText) self).cb$v1_21_11$label(collector);
        }
    }

    /**
     * renderContents of a 1.21.10 SpriteIconButton subclass declaring renderString. 1.21.11's CenteredIcon / TextAndIcon
     * draw their text themselves (no renderDefaultLabel); 1.21.10's renderWidget was AbstractButton's (sprite, then
     * this.renderString) and then the icon: centred (CenteredIcon) or at the right edge less 2 (TextAndIcon), both
     * vertically centred -- the positions 1.21.11's renderContents uses.
     */
    public static void spriteIconBridge(LegacyRenderString self, class_332 graphics, int mouseX, int mouseY,
                                        float partialTick) {
        class_4264 b = (class_4264) self;
        SpriteIcon icon = (SpriteIcon) self;
        ((ButtonText) self).cb$v1_21_11$sprite(graphics);
        self.method_48589(graphics, class_310.method_1551().field_1772, legacyColor(b));
        int x = b instanceof net.minecraft.class_8662.class_8665
            ? b.method_46426() + b.method_25368() - icon.cb$v1_21_11$spriteWidth() - 2
            : b.method_46426() + b.method_25368() / 2 - icon.cb$v1_21_11$spriteWidth() / 2;
        int y = b.method_46427() + b.method_25364() / 2 - icon.cb$v1_21_11$spriteHeight() / 2;
        icon.cb$v1_21_11$icon(graphics, x, y);
    }

    /** the old renderScrollingString a 1.21.10 button declares (an override hook's adapter) */
    public interface LegacyScrollingString {
        void method_49604(class_332 graphics, class_327 font, int inset, int color);
    }

    /**
     * renderScrollingLabel(collector, message, inset) (method_75799) of a 1.21.10 button declaring renderScrollingString:
     * 1.21.10's AbstractButton.renderString called this.renderScrollingString(graphics, font, 2, color), the message
     * being getMessage() as 1.21.11's renderDefaultLabel passes it. Another collector gets 1.21.11's body.
     */
    public static void scrollingBridge(LegacyScrollingString self, class_12225 collector, class_2561 message, int inset) {
        class_4264 b = (class_4264) self;
        if (collector instanceof CollectorGraphics g) {
            self.method_49604(g.cb$v1_21_11$graphics(), class_310.method_1551().field_1772, inset, legacyColor(b));
        } else {
            collector.method_75770(message, b.method_46426() + inset, b.method_46426() + b.method_25368() - inset,
                b.method_46427(), b.method_46427() + b.method_25364());
        }
    }

    /** 1.21.10 SpriteIconButton.TextAndIcon.renderString: the label scrolls between x + 2 and the icon */
    public static void textAndIconString(net.minecraft.class_8662.class_8665 b, class_332 graphics, class_327 font, int color) {
        int left = b.method_46426() + 2;
        int right = b.method_46426() + b.method_25368() - ((SpriteIcon) b).cb$v1_21_11$spriteWidth() - 4;
        int center = b.method_46426() + b.method_25368() / 2;
        method_49605(graphics, font, b.method_25369(), center, left, b.method_46427(), right, b.method_46427() + b.method_25364(),
            color);
    }
}
