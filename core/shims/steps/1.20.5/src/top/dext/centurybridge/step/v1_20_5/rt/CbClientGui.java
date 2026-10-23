package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's GUI members on 1.20.5 (s4client, group B): widgets, tooltips, screens, debug charts. Each
 * static is the 1.20.4 member, its body 1.20.4's written against 1.20.5's API.
 */
@Environment(EnvType.CLIENT)
public final class CbClientGui {

    // ------------------------------------------------------------------ tooltips

    /**
     * 1.20.4 Tooltip's own show delay and hover state (its fields msDelay / hoverOrFocusStart /
     * wasDisplayed; 1.20.5 keeps them on the widget's WidgetTooltipHolder). Per Tooltip, as 1.20.4 kept them.
     */
    private static final class TooltipState {
        int delay;
        long start;
        boolean shown;
    }

    private static final java.util.Map<net.minecraft.class_7919, TooltipState> TOOLTIPS =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private static TooltipState state(net.minecraft.class_7919 tooltip) {
        return TOOLTIPS.computeIfAbsent(tooltip, t -> new TooltipState());
    }

    /** 1.20.4 Tooltip.setDelay(ms) */
    public static void method_54383(net.minecraft.class_7919 tooltip, int delay) {
        state(tooltip).delay = delay;
    }

    /**
     * 1.20.4 Tooltip.refreshTooltipForNextRenderPass(hovered, focused, area): once the widget has been hovered
     * (or keyboard-focused) for the delay, the tooltip goes to the screen for this frame, positioned as 1.20.4
     * positioned it (below/above the widget for a keyboard focus, at the mouse otherwise)
     */
    public static void method_54384(net.minecraft.class_7919 tooltip, boolean hovered, boolean focused, net.minecraft.class_8030 area) {
        TooltipState s = state(tooltip);
        boolean show = hovered || focused && net.minecraft.class_310.method_1551().method_48186().method_48183();
        if (show != s.shown) {
            if (show) {
                s.start = net.minecraft.class_156.method_658();
            }
            s.shown = show;
        }
        if (show && net.minecraft.class_156.method_658() - s.start > (long) s.delay) {
            net.minecraft.class_437 screen = net.minecraft.class_310.method_1551().field_1755;
            if (screen != null) {
                screen.method_47412(tooltip, positioner(hovered, focused, area), focused);
            }
        }
    }

    /** 1.20.4 Tooltip.createTooltipPositioner(hovered, focused, area), which 1.20.5's WidgetTooltipHolder keeps unchanged */
    private static net.minecraft.class_8000 positioner(boolean hovered, boolean focused, net.minecraft.class_8030 area) {
        return !hovered && focused && net.minecraft.class_310.method_1551().method_48186().method_48183()
            ? new net.minecraft.class_7999(area) : new net.minecraft.class_8092(area);
    }

    /** 1.20.4 AbstractWidget.setTooltipDelay(ms): 1.20.5 takes a Duration */
    public static java.time.Duration millis(int delay) {
        return java.time.Duration.ofMillis(delay);
    }

    /** a 1.20.4 widget's own setTooltipDelay(int) is handed the delay 1.20.5 sets as a Duration */
    public static int millisOf(java.time.Duration delay) {
        return (int) Math.min(Integer.MAX_VALUE, delay.toMillis());
    }

    // ------------------------------------------------------------------ chat

    /** a 1.20.4 ChatScreen class's own handleChatInput(message, addToHistory), answering whether it handled the input */
    public interface CbLegacyChatInput {
        boolean method_44056(String message, boolean addToHistory);
    }

    /** what 1.20.4's ChatScreen.handleChatInput answered: true, every path of its body (1.20.5's returns nothing) */
    public static boolean handled() {
        return true;
    }

    /** 1.20.5's handleChatInput reaching a 1.20.4 class's own (its answer is what keyPressed acts on: mixin/CbClientChatScreenMixin) */
    public static void cbChatInput(CbLegacyChatInput screen, String message, boolean addToHistory) {
        screen.method_44056(message, addToHistory);
    }

    /** a 1.20.4 Screen class's own renderDirtBackground(graphics), its background without a level */
    public interface CbLegacyDirtBackground {
        void method_25434(net.minecraft.class_332 graphics);
    }

    // ------------------------------------------------------------------ backgrounds

    /**
     * 1.20.4's dirt backgrounds. 1.20.5 ships neither gui/options_background.png nor gui/light_dirt_background.png;
     * 1.20.4's options background is pixel for pixel the dirt block texture, which 1.20.5 still ships, and its light
     * dirt background is that texture with every channel scaled by 0.298 (rounded) -- exact for every pixel of
     * 1.20.4's file. Both are made from the dirt texture the client has and registered under 1.20.4's ids the first
     * time 1.20.4 code names one, so blits of those ids draw what they drew.
     */
    private static final net.minecraft.class_2960 OPTIONS_BACKGROUND = new net.minecraft.class_2960("textures/gui/options_background.png");
    private static final net.minecraft.class_2960 LIGHT_DIRT_BACKGROUND = new net.minecraft.class_2960("textures/gui/light_dirt_background.png");
    private static final net.minecraft.class_2960 DIRT = new net.minecraft.class_2960("textures/block/dirt.png");
    private static volatile boolean dirtRegistered;

    private static void registerDirt() {
        if (dirtRegistered) {
            return;
        }
        synchronized (CbClientGui.class) {
            if (dirtRegistered) {
                return;
            }
            net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
            try (java.io.InputStream in = mc.method_1478().getResourceOrThrow(DIRT).method_14482()) {
                net.minecraft.class_1011 dirt = net.minecraft.class_1011.method_4309(in);
                net.minecraft.class_1011 light = new net.minecraft.class_1011(dirt.method_4307(), dirt.method_4323(), false);
                for (int y = 0; y < dirt.method_4323(); y++) {
                    for (int x = 0; x < dirt.method_4307(); x++) {
                        int abgr = dirt.method_4315(x, y);
                        int a = abgr >>> 24, b = dim((abgr >> 16) & 0xFF), g = dim((abgr >> 8) & 0xFF), r = dim(abgr & 0xFF);
                        light.method_4305(x, y, a << 24 | b << 16 | g << 8 | r);
                    }
                }
                mc.method_1531().method_4616(OPTIONS_BACKGROUND, new net.minecraft.class_1043(dirt));
                mc.method_1531().method_4616(LIGHT_DIRT_BACKGROUND, new net.minecraft.class_1043(light));
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException("1.20.4 dirt backgrounds from " + DIRT, e);
            }
            dirtRegistered = true;
        }
    }

    private static int dim(int channel) {
        return Math.round(channel * 0.298F);
    }

    /** 1.20.4 Screen.BACKGROUND_LOCATION: the options background */
    public static net.minecraft.class_2960 field_44669() {
        registerDirt();
        return OPTIONS_BACKGROUND;
    }

    /** 1.20.4 CreateWorldScreen.HEADER_SEPERATOR: 1.20.5's header separator (Screen.HEADER_SEPARATOR), the same texture file */
    public static net.minecraft.class_2960 field_43081() {
        return new net.minecraft.class_2960("textures/gui/header_separator.png");
    }

    /** 1.20.4 CreateWorldScreen.FOOTER_SEPERATOR: 1.20.5's footer separator, the same texture file */
    public static net.minecraft.class_2960 field_43082() {
        return new net.minecraft.class_2960("textures/gui/footer_separator.png");
    }

    /** 1.20.4 CreateWorldScreen.LIGHT_DIRT_BACKGROUND */
    public static net.minecraft.class_2960 field_44672() {
        registerDirt();
        return LIGHT_DIRT_BACKGROUND;
    }

    /**
     * 1.20.4 Screen.renderDirtBackground(graphics): the options background tiled over the screen at 32 px,
     * darkened to a quarter, as 1.20.4 drew it
     */
    public static void method_25434(net.minecraft.class_437 screen, net.minecraft.class_332 graphics) {
        if (screen instanceof CbLegacyDirtBackground legacy) {
            legacy.method_25434(graphics);
            return;
        }
        cbSuperDirtBackground(screen, graphics);
    }

    /** a 1.20.4 screen's super.renderDirtBackground(graphics): 1.20.4 Screen's body (CreateWorldScreen's own, under it) */
    public static void cbSuperDirtBackground(net.minecraft.class_437 screen, net.minecraft.class_332 graphics) {
        if (screen instanceof net.minecraft.class_525) {
            graphics.method_25291(field_44672(), 0, 0, 0, 0.0F, 0.0F, screen.field_22789, screen.field_22790, 32, 32);
            return;
        }
        graphics.method_51422(0.25F, 0.25F, 0.25F, 1.0F);
        graphics.method_25291(field_44669(), 0, 0, 0, 0.0F, 0.0F, screen.field_22789, screen.field_22790, 32, 32);
        graphics.method_51422(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** 1.20.4 Screen.hideWidgets(widgets...): each widget made invisible */
    public static void method_40041(net.minecraft.class_339[] widgets) {
        for (net.minecraft.class_339 w : widgets) {
            w.field_22764 = false;
        }
    }

    /** 1.20.4 TitleScreen.CUBE_MAP: the panorama cube map, which 1.20.5 keeps as Screen.CUBE_MAP */
    public static net.minecraft.class_751 field_17774() {
        return top.dext.centurybridge.step.v1_20_5.mixin.CbClientScreenAccessor.cb$cubeMap();
    }

    // ------------------------------------------------------------------ widgets

    /** 1.20.4 FocusableTextWidget(maxWidth, message, font, alwaysShowBorder): 1.20.4's fixed 4 px padding */
    public static int padding() {
        return 4;
    }

    /** 1.20.4 SpriteIconButton's narration: the default button narration (1.20.5 takes it as a parameter) */
    public static net.minecraft.class_4185.class_7841 defaultNarration() {
        return top.dext.centurybridge.step.v1_20_5.mixin.CbClientButtonAccessor.cb$defaultNarration();
    }

    /** 1.20.4 ConnectScreen.startConnecting(.., quickPlay): no transfer state (1.20.5's new parameter; null is a fresh connection) */
    public static net.minecraft.class_9112 noTransfer() {
        return null;
    }

    /**
     * 1.20.4 RealmsObjectSelectionList.getScrollbarPosition(): its public override answered the row's left
     * edge plus its width (1.20.5 dropped the override; the protected AbstractSelectionList one is another
     * position)
     */
    public static int method_25329(net.minecraft.class_4904<?> list) {
        if (list instanceof CbLegacyScrollbar legacy) {
            return legacy.method_25329();
        }
        return cbSuperScrollbar(list);
    }

    /** a 1.20.4 subclass's super.getScrollbarPosition(): 1.20.4 RealmsObjectSelectionList's body */
    public static int cbSuperScrollbar(net.minecraft.class_4904<?> list) {
        return list.method_25342() + list.method_25322();
    }

    /**
     * 1.20.4 OptionsList.addBig(option): the full-width row of the option's widget -- 1.20.5's addBig adds the same
     * row -- answering its index, which addEntry answered: the entry count less one
     */
    public static int method_20406(net.minecraft.class_353 list, net.minecraft.class_7172<?> option) {
        if (list instanceof CbLegacyAddBig legacy) {
            return legacy.method_20406(option);
        }
        list.method_20406(option);
        return list.method_25396().size() - 1;
    }

    /** a 1.20.4 subclass's super.addBig(option): 1.20.5's row, its index */
    public static int cbSuperAddBig(net.minecraft.class_353 list, net.minecraft.class_7172<?> option) {
        boolean entered = Guard.cbEnter(list);
        try {
            list.method_20406(option);
        } finally {
            if (entered) {
                Guard.cbExit(list);
            }
        }
        return list.method_25396().size() - 1;
    }

    /** a 1.20.4 OptionsList subclass's own addBig(option), answering the row's index */
    public interface CbLegacyAddBig {
        int method_20406(net.minecraft.class_7172<?> option);
    }

    /** 1.20.5's addBig(option) reaching a 1.20.4 subclass's own (the index it answered is not asked for) */
    public static void cbAddBig(CbLegacyAddBig list, net.minecraft.class_7172<?> option) {
        list.method_20406(option);
    }

    // ------------------------------------------------------------------ models, skulls, items

    /** 1.20.4 WolfModel.createBodyLayer(): 1.20.5's mesh (no armor deformation) as a 64x32 layer, as 1.20.4 made it */
    public static net.minecraft.class_5607 wolfBodyLayer() {
        return net.minecraft.class_5607.method_32110(net.minecraft.class_624.method_32068(net.minecraft.class_5605.field_27715), 64, 32);
    }

    /** 1.20.4 SkullBlockRenderer.getRenderType(type, profile): 1.20.5 takes a ResolvableProfile; null stays null */
    public static net.minecraft.class_9296 resolvable(com.mojang.authlib.GameProfile profile) {
        return profile == null ? null : new net.minecraft.class_9296(profile);
    }

    /** 1.20.4 InventoryScreen.renderEntityInInventory(.., scale, ..): the int scale 1.20.5 takes as a float */
    public static float scale(int scale) {
        return scale;
    }

    /** 1.20.4 ClientBundleTooltip(tooltip): 1.20.5 takes the contents the tooltip carries */
    public static net.minecraft.class_9276 bundleContents(net.minecraft.class_5631 tooltip) {
        return tooltip.comp_2377();
    }

    /** 1.20.4 GlyphProviderDefinition.CODEC: 1.20.5's map codec (the same type-dispatched object) as a codec */
    public static com.mojang.serialization.Codec<net.minecraft.class_389> field_44801() {
        return net.minecraft.class_389.field_44801.codec();
    }

    /** a 1.20.4 RealmsObjectSelectionList subclass's own getScrollbarPosition() */
    public interface CbLegacyScrollbar {
        int method_25329();
    }

    /** 1.20.4 ItemProperties.getProperty(item, id): 1.20.5 looks the property up on a stack; a stack of the item */
    public static net.minecraft.class_1799 stackOf(net.minecraft.class_1792 item) {
        return new net.minecraft.class_1799(item);
    }

    // ------------------------------------------------------------------ fonts

    /**
     * the font options in effect, as 1.20.5's FontManager.getFontOptions(options) computes them: uniform when the
     * unicode font is forced, Japanese glyph variants when chosen
     */
    public static java.util.Set<net.minecraft.class_9243> fontOptions() {
        java.util.Set<net.minecraft.class_9243> set = java.util.EnumSet.noneOf(net.minecraft.class_9243.class);
        net.minecraft.class_315 options = net.minecraft.class_310.method_1551().field_1690;
        if (options.method_42437().method_41753()) {
            set.add(net.minecraft.class_9243.field_49112);
        }
        if (options.method_57018().method_41753()) {
            set.add(net.minecraft.class_9243.field_49113);
        }
        return set;
    }

    // ------------------------------------------------------------------ debug charts

    /** a 1.20.4 SampleLogger where 1.20.5's charts take its SampleStorage: every 1.20.4 logger is a LocalSampleLogger */
    public static net.minecraft.class_9194 storage(net.minecraft.class_8743 logger) {
        return CbFinNetLoggers.logger(logger);
    }

    /**
     * 1.20.4 AbstractDebugChart.logger, read where 1.20.5 keeps the chart's SampleStorage: the logger it was
     * made with (every SampleStorage a 1.20.4-made chart holds is a LocalSampleLogger, which is the logger)
     */
    public static net.minecraft.class_8743 cbChartLogger(net.minecraft.class_8755 chart) {
        net.minecraft.class_9194 storage = ((top.dext.centurybridge.step.v1_20_5.mixin.CbClientDebugChartAccessor) chart).cb$storage();
        if (storage == null || storage instanceof net.minecraft.class_8743) {
            return (net.minecraft.class_8743) storage;
        }
        throw new IllegalStateException("a 1.20.4 debug chart's logger is a sample logger; got " + storage.getClass().getName());
    }

    /**
     * 1.20.4 IntegratedServer.logTickTime(nanos): the client overlay's tick duration, as 1.20.4's override
     * fed it. 1.20.5's integrated server feeds the overlay itself (getTickTimeLogger); a 1.20.4 caller adds
     * a sample as its call did.
     */
    public static void method_53620(net.minecraft.class_1132 server, long nanos) {
        ((top.dext.centurybridge.step.v1_20_5.rt.CbClientGui.DebugOverlayApi) net.minecraft.class_310.method_1551().method_53526())
            .method_53535(nanos);
    }

    /**
     * a 1.20.4 DebugScreenOverlay subclass's getFrameTimeLogger / getTickTimeLogger answer, as the
     * LocalSampleLogger 1.20.5 asks for: every 1.20.4 sample logger is one (1.20.5's own loggers, and a
     * 1.20.4 subclass or new SampleLogger() is facade/SampleLogger, a LocalSampleLogger)
     */
    public static net.minecraft.class_9191 cbOverlayLogger(net.minecraft.class_8743 logger) {
        if (logger == null || logger instanceof net.minecraft.class_9191) {
            return (net.minecraft.class_9191) logger;
        }
        throw new IllegalStateException("a 1.20.4 sample logger is a LocalSampleLogger; got " + logger.getClass().getName());
    }

    /** the layer's DebugScreenOverlay.logTickDuration (mixin/CbClientDebugOverlayMixin), for its callers in the layer */
    public interface DebugOverlayApi {
        void method_53535(long nanos);
    }

    // ------------------------------------------------------------------ font sets

    /** a 1.20.4 FontSet subclass's own reload(providers) */
    public interface CbLegacyFontReload {
        void method_2004(java.util.List<net.minecraft.class_390> providers);
    }

    /**
     * 1.20.5's FontSet.reload(providers, options) reaching a 1.20.4 subclass's reload(providers): the providers the
     * options select, as 1.20.5's own reload selects them (each provider whose filter the options pass)
     */
    public static void cbFontReload(CbLegacyFontReload set, java.util.List<net.minecraft.class_390.class_9241> providers,
                                    java.util.Set<net.minecraft.class_9243> options) {
        java.util.List<net.minecraft.class_390> selected = new java.util.ArrayList<>();
        for (net.minecraft.class_390.class_9241 p : providers) {
            if (p.comp_2349().method_57032(options)) {
                selected.add(p.comp_2348());
            }
        }
        set.method_2004(selected);
    }

    // ------------------------------------------------------------------ options list

    /**
     * The screen 1.20.5's OptionsList(minecraft, width, unused, screen) places its rows by (screen.width / 2 - 155)
     * and sizes itself from: for 1.20.4's OptionsList(minecraft, width, height, y, itemHeight), which placed its
     * rows by the list's own width, an options screen as wide as the list (the size is 1.20.4's, set after the
     * constructor: {@link #cbOptionsListBuilt})
     */
    public static net.minecraft.class_4667 optionsHost(int width) {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_4667 host = new net.minecraft.class_4667(null, mc.field_1690, net.minecraft.class_2561.method_43473());
        host.field_22789 = width;
        return host;
    }

    /** after 1.20.4's OptionsList(minecraft, width, height, y, itemHeight): the list's size, position and row height, as 1.20.4's constructor set them */
    public static void cbOptionsListBuilt(net.minecraft.class_353 list, int width, int height, int y, int itemHeight) {
        ((top.dext.centurybridge.step.v1_20_5.mixin.CbClientListItemHeightAccessor) list).cb$setItemHeight(itemHeight);
        list.method_57714(width, height, y);
    }

    /**
     * OptionsList.getMouseOver(x, y)'s answer as 1.20.4 typed it, an Optional of a widget: 1.20.5 types it as
     * any GuiEventListener, finding it among each row's children -- the row's widgets, which 1.20.4 searched.
     * A widget passes unchanged either way (a 1.20.4 override's answer is one 1.20.5 takes); anything else, which
     * only a row with non-widget children could hold and 1.20.4 never answered, is no widget: empty
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Optional cbWidgetOnly(java.util.Optional found) {
        return found.filter(net.minecraft.class_339.class::isInstance);
    }

    private CbClientGui() {
    }
}
