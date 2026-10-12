package top.dext.centurybridge.step.v1_21.rt;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6 GUI API on 1.21: MultiLineLabel, the options screens (OptionsSubScreen, SimpleOptionsSubScreen,
 * OptionsList, VideoSettingsScreen, OnlineOptionsScreen), the report screen, NoticeWithLinkScreen, the creative
 * and horse inventory screens, Screen's narration and panorama hooks, the menu background blurriness option,
 * subtitles, Camera.move, RealmsScreen's info color.
 */
@Environment(EnvType.CLIENT)
public final class CbClientGui {

    private CbClientGui() {
    }

    // ------------------------------------------------------------------ MultiLineLabel (class_5489)

    /*
     * 1.20.6's MultiLineLabel answered the y below the last line from renderCentered / renderLeftAligned
     * (y + lines * lineHeight); 1.21's answer nothing. 1.20.6's renderBackgroundCentered is gone. A 1.20.6
     * label class keeps its own methods (the adapter interfaces below, given it by the hooks); any other label
     * renders through 1.21 and the y is the one 1.20.6 computed.
     */

    public interface CbLegacyLabelCentered {
        int method_30888(net.minecraft.class_332 graphics, int x, int y);
    }

    public interface CbLegacyLabelCenteredFull {
        int method_30889(net.minecraft.class_332 graphics, int x, int y, int lineHeight, int color);
    }

    public interface CbLegacyLabelLeft {
        int method_30893(net.minecraft.class_332 graphics, int x, int y, int lineHeight, int color);
    }

    public interface CbLegacyLabelBackground {
        void method_41154(net.minecraft.class_332 graphics, int x, int y, int lineHeight, int padding, int color);
    }

    public static int cbRenderCentered(net.minecraft.class_5489 label, net.minecraft.class_332 graphics, int x, int y) {
        if (label instanceof CbLegacyLabelCentered legacy) {
            return legacy.method_30888(graphics, x, y);
        }
        label.method_30888(graphics, x, y);
        return y + label.method_30887() * 9;
    }

    public static int cbRenderCentered(net.minecraft.class_5489 label, net.minecraft.class_332 graphics, int x, int y,
                                       int lineHeight, int color) {
        if (label instanceof CbLegacyLabelCenteredFull legacy) {
            return legacy.method_30889(graphics, x, y, lineHeight, color);
        }
        label.method_30889(graphics, x, y, lineHeight, color);
        return y + label.method_30887() * lineHeight;
    }

    public static int cbRenderLeftAligned(net.minecraft.class_5489 label, net.minecraft.class_332 graphics, int x, int y,
                                          int lineHeight, int color) {
        if (label instanceof CbLegacyLabelLeft legacy) {
            return legacy.method_30893(graphics, x, y, lineHeight, color);
        }
        label.method_30893(graphics, x, y, lineHeight, color);
        return y + label.method_30887() * lineHeight;
    }

    /**
     * renderBackgroundCentered(graphics, x, y, lineHeight, padding, color) of 1.20.6: one fill behind the lines,
     * as wide as the widest line plus the padding each side (nothing for an empty label).
     */
    public static void cbRenderBackgroundCentered(net.minecraft.class_5489 label, net.minecraft.class_332 graphics, int x,
                                                  int y, int lineHeight, int padding, int color) {
        if (label instanceof CbLegacyLabelBackground legacy) {
            legacy.method_41154(graphics, x, y, lineHeight, padding, color);
            return;
        }
        int width = label.method_44048();
        if (width > 0) {
            graphics.method_25294(x - width / 2 - padding, y - padding, x + width / 2 + padding,
                y + label.method_30887() * lineHeight + padding, color);
        }
    }

    /** 1.21's void render methods, reaching a 1.20.6 label class's int-answering ones (override hooks) */
    public static void cbLabelCentered(CbLegacyLabelCentered label, net.minecraft.class_332 graphics, int x, int y) {
        label.method_30888(graphics, x, y);
    }

    public static void cbLabelCenteredFull(CbLegacyLabelCenteredFull label, net.minecraft.class_332 graphics, int x, int y,
                                           int lineHeight, int color) {
        label.method_30889(graphics, x, y, lineHeight, color);
    }

    public static void cbLabelLeft(CbLegacyLabelLeft label, net.minecraft.class_332 graphics, int x, int y, int lineHeight,
                                   int color) {
        label.method_30893(graphics, x, y, lineHeight, color);
    }

    /** MultiLineLabel.create(font, text, maxWidth) of 1.20.6: the text split to the width, fixed */
    public static net.minecraft.class_5489 method_30890(net.minecraft.class_327 font, net.minecraft.class_5348 text, int maxWidth) {
        return fixed(font, font.method_1728(text, maxWidth));
    }

    /** create(font, text, maxWidth, maxLines) of 1.20.6: the first maxLines lines of that split */
    public static net.minecraft.class_5489 method_30891(net.minecraft.class_327 font, net.minecraft.class_5348 text, int maxWidth,
                                                       int maxLines) {
        List<net.minecraft.class_5481> lines = font.method_1728(text, maxWidth);
        return fixed(font, lines.stream().limit(maxLines).toList());
    }

    /** create(font, components) of 1.20.6: one line per component, unsplit */
    public static net.minecraft.class_5489 method_35726(net.minecraft.class_327 font, List<net.minecraft.class_2561> components) {
        return fixed(font, components.stream().map(net.minecraft.class_2561::method_30937).toList());
    }

    /** createFixed(font, lines) of 1.20.6 */
    public static net.minecraft.class_5489 method_30895(net.minecraft.class_327 font, List<net.minecraft.class_5489.class_5490> lines) {
        if (lines.isEmpty()) {
            return net.minecraft.class_5489.field_26528;
        }
        return new FixedLabel(font, lines.stream().map(l -> new Line(l.comp_2877(), l.comp_2878())).collect(ImmutableList.toImmutableList()));
    }

    private static net.minecraft.class_5489 fixed(net.minecraft.class_327 font, List<net.minecraft.class_5481> lines) {
        if (lines.isEmpty()) {
            return net.minecraft.class_5489.field_26528;
        }
        return new FixedLabel(font, lines.stream().map(l -> new Line(l, font.method_30880(l))).collect(ImmutableList.toImmutableList()));
    }

    private record Line(net.minecraft.class_5481 text, int width) {
    }

    /** 1.20.6's fixed MultiLineLabel (its MultiLineLabel$2): the lines as made, drawn as 1.20.6 drew them */
    private static final class FixedLabel implements net.minecraft.class_5489, CbLegacyLabelBackground {
        private final net.minecraft.class_327 font;
        private final List<Line> lines;
        private final int width;

        FixedLabel(net.minecraft.class_327 font, List<Line> lines) {
            this.font = font;
            this.lines = lines;
            this.width = lines.stream().mapToInt(Line::width).max().orElse(0);
        }

        @Override
        public void method_30888(net.minecraft.class_332 graphics, int x, int y) {
            method_30889(graphics, x, y, 9, 0xFFFFFF);
        }

        @Override
        public void method_30889(net.minecraft.class_332 graphics, int x, int y, int lineHeight, int color) {
            int at = y;
            for (Line l : lines) {
                graphics.method_35720(font, l.text(), x - l.width() / 2, at, color);
                at += lineHeight;
            }
        }

        @Override
        public void method_30893(net.minecraft.class_332 graphics, int x, int y, int lineHeight, int color) {
            int at = y;
            for (Line l : lines) {
                graphics.method_35720(font, l.text(), x, at, color);
                at += lineHeight;
            }
        }

        @Override
        public int method_30896(net.minecraft.class_332 graphics, int x, int y, int lineHeight, int color) {
            int at = y;
            for (Line l : lines) {
                graphics.method_51430(font, l.text(), x, at, color, false);
                at += lineHeight;
            }
            return at;
        }

        @Override
        public void method_41154(net.minecraft.class_332 graphics, int x, int y, int lineHeight, int padding, int color) {
            if (width > 0) {
                graphics.method_25294(x - width / 2 - padding, y - padding, x + width / 2 + padding,
                    y + lines.size() * lineHeight + padding, color);
            }
        }

        @Override
        public int method_30887() {
            return lines.size();
        }

        @Override
        public int method_44048() {
            return width;
        }
    }

    // ------------------------------------------------------------------ the options and report screens

    /**
     * A 1.20.6 screen class (of a mod) marks itself with this (an adapter hook gives it to every 1.20.6 class
     * under Screen). OptionsSubScreen, AbstractReportScreen and Screen's panorama ask for it: 1.21 gave them new
     * behaviour (OptionsSubScreen.init builds an options list of its own and asks the abstract addOptions;
     * AbstractReportScreen.init builds header, content and footer; renderPanorama spins by the partial tick) that a
     * class written for 1.20.6 does not expect, so for such a class they keep 1.20.6's.
     */
    public interface CbLegacyScreen {
        /** Screen.advancePanoramaTime() of 1.20.6 (protected there): the screen's own when it overrides it */
        default float method_59870() {
            return cbAdvancePanoramaTime((net.minecraft.class_437) this);
        }
    }

    /** a 1.20.6 screen class's own advancePanoramaTime() override (the adapter hook's required shape) */
    public interface CbLegacyPanorama {
        float method_59870();
    }

    /**
     * Whether a screen runs 1.20.6's version of the vanilla class it extends: a 1.20.6 class whose nearest vanilla
     * superclass is that one (a 1.20.6 class under a 1.21 subclass, say of VideoSettingsScreen, relies on 1.21's).
     */
    public static boolean cbLegacyUnder(Object screen, Class<?> vanilla) {
        if (!(screen instanceof CbLegacyScreen)) {
            return false;
        }
        Class<?> c = screen.getClass().getSuperclass();
        while (c != null && !c.getName().startsWith("net.minecraft.")) {
            c = c.getSuperclass();
        }
        return c == vanilla;
    }

    /** OptionsList(minecraft, width, height, screen) of 1.20.6: its height was never read (the screen's layout sizes it) */

    /**
     * OptionsSubScreen.init of 1.20.6 (title, footer, the layout's widgets, positions) -- 1.21's also builds an
     * options list of its own and calls addOptions -- for a 1.20.6 subclass (mixin/CbClientOptionsSubScreenMixin).
     * addOptions is then never reached for it; the abstract fill answers that no 1.20.6 class has options to add
     * there.
     */
    public static void cbLegacyAddOptions(net.minecraft.class_4667 screen) {
    }

    /**
     * AbstractReportScreen.addContent is abstract since 1.21 and called only from 1.21's AbstractReportScreen.init,
     * which a 1.20.6 subclass does not run (its super.init() is Screen's, as in 1.20.6:
     * mixin/CbClientReportScreenMixin).
     */
    public static void cbLegacyAddContent(net.minecraft.class_8768<?> screen) {
    }

    /** VideoSettingsScreen(parent, options) of 1.20.6: 1.21's takes the game too */
    public static net.minecraft.class_310 cbMinecraft() {
        return net.minecraft.class_310.method_1551();
    }

    /** OnlineOptionsScreen.createOnlineOptionsScreen(minecraft, parent, options) of 1.20.6 is 1.21's constructor */
    public static net.minecraft.class_6777 method_47621(net.minecraft.class_310 minecraft, net.minecraft.class_437 parent,
                                                        net.minecraft.class_315 options) {
        return new net.minecraft.class_6777(parent, options);
    }

    // ------------------------------------------------------------------ NoticeWithLinkScreen (class_8669)

    /**
     * NoticeWithLinkScreen(title, message, url, onClose) took the link as a String in 1.20.6 and used it as given:
     * copied to the clipboard, and opened with Util.OS.openUri(String), which logs and does nothing for a string
     * that is no URI. 1.21 takes a URI. The 1.20.6 string is kept for the screen and both buttons use it
     * (mixin/CbClientNoticeWithLinkScreenMixin), so an unparsable link behaves as in 1.20.6; the URI 1.21 holds is
     * the string's own when it parses.
     */
    private static final Map<net.minecraft.class_8669, String> NOTICE_LINKS = new MapMaker().weakKeys().makeMap();

    public static URI cbUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            return URI.create("about:blank");
        }
    }

    public static void cbNoticeBuilt(net.minecraft.class_8669 screen, String url) {
        NOTICE_LINKS.put(screen, url);
    }

    /** the 1.20.6 string a screen was made with, or null for a 1.21-made one */
    public static String cbNoticeLink(net.minecraft.class_8669 screen) {
        return NOTICE_LINKS.get(screen);
    }

    // ------------------------------------------------------------------ inventory screens

    /**
     * CreativeModeInventoryScreen(player, features, op) took any Player in 1.20.6; 1.21's is the local player's
     * (it reads its connection's search trees). The creative screen only ever opens for the local player.
     */
    public static net.minecraft.class_746 cbLocalPlayer(net.minecraft.class_1657 player) {
        if (player instanceof net.minecraft.class_746 local) {
            return local;
        }
        throw new IllegalArgumentException("1.21's CreativeModeInventoryScreen needs the local player (it reads the "
            + "connection's search trees); a 1.20.6 caller passed " + player);
    }

    /**
     * HorseInventoryScreen(menu, inventory, horse) of 1.20.6 drew the chest grid when the horse was a chested
     * horse with a chest, getInventoryColumns() wide; 1.21's takes that column count (0 for none).
     */
    public static int cbHorseColumns(net.minecraft.class_1496 horse) {
        if (horse instanceof net.minecraft.class_1492 chested && chested.method_6703()) {
            return chested.method_6702();
        }
        return 0;
    }

    // ------------------------------------------------------------------ Screen narration and panorama (class_437)

    /** Screen.narrationEnabled() of 1.20.6: schedule a narration after the narrator-enabled delay */
    public static void cbNarrationEnabled(net.minecraft.class_437 screen) {
        if (screen instanceof CbLegacyNarration legacy) {
            legacy.method_37072();
        } else {
            cbSuperNarrationEnabled(screen);
        }
    }

    public static void cbSuperNarrationEnabled(net.minecraft.class_437 screen) {
        top.dext.centurybridge.step.v1_21.mixin.CbClientScreenAccess a = (top.dext.centurybridge.step.v1_21.mixin.CbClientScreenAccess) screen;
        a.cb$scheduleNarration(top.dext.centurybridge.step.v1_21.mixin.CbClientScreenAccess.cb$narratorEnabledDelay(), false);
    }

    /** a 1.20.6 screen's narrationEnabled(), which 1.21 replaced by updateNarratorStatus(enabled) */
    public interface CbLegacyNarration {
        void method_37072();
    }

    /**
     * 1.21's updateNarratorStatus(enabled): 1.20.6 called narrationEnabled() when the narrator came on; the rest is
     * 1.21's own (the narrator button shows the option's value).
     */
    public static void cbUpdateNarratorStatus(CbLegacyNarration screen, boolean enabled) {
        if (enabled) {
            screen.method_37072();
        }
        net.minecraft.class_5676<?> button = ((top.dext.centurybridge.step.v1_21.mixin.CbClientScreenAccess) screen).cb$narratorButton();
        if (button != null) {
            @SuppressWarnings("unchecked")
            net.minecraft.class_5676<Object> b = (net.minecraft.class_5676<Object>) button;
            b.method_32605(net.minecraft.class_310.method_1551().field_1690.method_42476().method_41753());
        }
    }

    /**
     * Screen.advancePanoramaTime() of 1.20.6: real time since the screen last asked, in ticks (half a tick after a
     * long gap); 1.20.6's renderPanorama spun the panorama by it. Kept per screen (mixin/CbClientScreenPanoramaMixin).
     */
    public static float cbAdvancePanoramaTime(net.minecraft.class_437 screen) {
        CbPanoramaTime t = (CbPanoramaTime) screen;
        long now = net.minecraft.class_156.method_658();
        float f = (float) (now - t.cb$lastPanoramaTime()) / 50.0F;
        t.cb$setLastPanoramaTime(now);
        return f > 7.0F ? 0.5F : f;
    }

    /** the per-screen time 1.20.6's Screen kept for advancePanoramaTime (mixin/CbClientScreenPanoramaMixin) */
    public interface CbPanoramaTime {
        long cb$lastPanoramaTime();

        void cb$setLastPanoramaTime(long time);
    }

    /** a call of advancePanoramaTime(): the screen's own when it is a 1.20.6 class, else 1.20.6's body */
    public static float cbPanoramaTime(net.minecraft.class_437 screen) {
        return screen instanceof CbLegacyScreen legacy ? legacy.method_59870() : cbAdvancePanoramaTime(screen);
    }

    // ------------------------------------------------------------------ Options.menuBackgroundBlurriness (class_315)

    /**
     * The menu background blurriness was a fraction (OptionInstance<Double>, 0..1, default 0.5) in 1.20.6 and is a
     * step (OptionInstance<Integer>, 0..10, default 5) since 1.21: one 1.21 step is a tenth.
     */
    public static double cbBlurriness(int steps) {
        return steps / 10.0;
    }

    public static int cbBlurrinessSteps(double fraction) {
        return (int) Math.round(fraction * 10.0);
    }

    /**
     * Options.menuBackgroundBlurriness() as 1.20.6's OptionInstance<Double>: a view of 1.21's option (the same
     * caption, tooltip, a 0..1 slider), whose value is always 1.21's in tenths and whose set sets 1.21's
     * (mixin/CbClientOptionInstanceMixin). Adaptive: a value that is already a 1.20.6 view passes.
     */
    private static final Map<net.minecraft.class_7172<?>, net.minecraft.class_7172<?>> BLUR_VIEWS = new MapMaker().weakKeys().makeMap();

    /** view -> 1.21's option it stands for */
    private static final Map<net.minecraft.class_7172<?>, net.minecraft.class_7172<?>> BLUR_STEPS = new MapMaker().weakKeys().makeMap();

    /** 1.21's option a 1.20.6 view stands for, or null when the option is no view (mixin/CbClientOptionInstanceMixin) */
    @SuppressWarnings("unchecked")
    public static net.minecraft.class_7172<Integer> cbViewSteps(Object option) {
        return BLUR_STEPS.isEmpty() ? null : (net.minecraft.class_7172<Integer>) BLUR_STEPS.get(option);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_7172 cbBlurrinessView(net.minecraft.class_7172 option) {
        if (option == null || option.method_41753() instanceof Double) {
            return option;
        }
        return BLUR_VIEWS.computeIfAbsent(option, o -> {
            net.minecraft.class_7172<Integer> steps = (net.minecraft.class_7172<Integer>) o;
            net.minecraft.class_7172<Double> view = new net.minecraft.class_7172<>("options.accessibility.menu_background_blurriness",
                net.minecraft.class_7172.method_42717(net.minecraft.class_2561.method_43471("options.accessibility.menu_background_blurriness.tooltip")),
                (caption, value) -> top.dext.centurybridge.step.v1_21.mixin.CbClientOptionsAccess.cb$percentValueOrOffLabel(caption, value),
                net.minecraft.class_7172.class_7177.field_37875, cbBlurriness(steps.method_41753()),
                value -> steps.method_41748(cbBlurrinessSteps(value)));
            BLUR_STEPS.put(view, steps);
            return view;
        });
    }

    /** a 1.20.6 Options subclass's own view, as 1.21's integer option: the step it stands for */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_7172 cbBlurrinessSteps(net.minecraft.class_7172 option) {
        if (option == null || option.method_41753() instanceof Integer) {
            return option;
        }
        net.minecraft.class_7172 linked = BLUR_STEPS.get(option);
        return linked != null ? linked : option;
    }

    // ------------------------------------------------------------------ SubtitleOverlay.Subtitle (class_359$class_360)

    /*
     * 1.20.6's Subtitle kept one location and the time it was last refreshed; 1.21's (package-private, opened by the
     * layer's access widener) keeps every place it was heard and drops old ones. mixin/CbClientSubtitleMixin gives
     * it back getLocation()/getTime() (the last refresh, kept as 1.20.6 kept it), and for a 1.20.6 subclass answers
     * 1.21's getClosest / isStillActive from the subclass's own getters, as 1.20.6's overlay used them.
     */

    private static java.lang.invoke.MethodHandle playedAt;

    /**
     * SoundPlayedAt(location, time), the package-private record 1.21's Subtitle answers getClosest with (its
     * constructor opened by the layer's access widener, so only reflection can name it from here).
     */
    public static Object cbPlayedAt(net.minecraft.class_243 location, long time) {
        try {
            if (playedAt == null) {
                Class<?> c = Class.forName("net.minecraft.class_359$class_9772");
                playedAt = java.lang.invoke.MethodHandles.publicLookup().findConstructor(c,
                    java.lang.invoke.MethodType.methodType(void.class, net.minecraft.class_243.class, long.class));
            }
            return playedAt.invoke(location, time);
        } catch (Throwable t) {
            throw new IllegalStateException("SubtitleOverlay.SoundPlayedAt(Vec3, long) not reachable", t);
        }
    }

    // ------------------------------------------------------------------ RecipeBookComponent.addItemToSlot (class_507)

    /**
     * PlaceRecipe.addItemToSlot(ingredients, slot, maxAmount, y, x) of 1.20.6 took an Iterator, and every 1.20.6
     * implementation (RecipeBookComponent's included) placed ingredients.next(); since 1.21 placeRecipe takes
     * next() itself and hands addItemToSlot(item, slot, maxAmount, x, y) the element -- with x and y swapped. A
     * 1.20.6 call gives 1.21's method the element the 1.20.6 body took.
     */
    public static Object cbNextIngredient(java.util.Iterator<?> ingredients) {
        return ingredients.next();
    }

    /** the element 1.21 hands a 1.20.6 override, as the iterator 1.20.6 handed it (the override takes next()) */
    public static java.util.Iterator<?> cbSingleIngredient(Object item) {
        return java.util.Collections.singletonList(item).iterator();
    }

    // ------------------------------------------------------------------ Camera.move (class_4184), RealmsScreen

    /** Camera.move(dx, dy, dz) took doubles in 1.20.6 and cast them to float first; 1.21 takes floats */
    public static float cbNarrow(double d) {
        return (float) d;
    }

    /** a 1.20.6 Camera subclass's move(double..) gets 1.21's floats, exactly */
    public static double cbWiden(float f) {
        return f;
    }

    /** RealmsScreen.COLOR_INFO of 1.20.6 (gone in 1.21) */
    public static final int field_33042 = 8226750;
}
