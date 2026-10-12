package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_20_3.mixin.CbConfirmLinkScreenAccess;

/**
 * 1.20.2 GUI API on 1.20.3: selection lists (1.20.3 made them widgets), checkboxes, system toasts and
 * the confirm / notice / backup / data-pack screens, world selection and editing, tooltip positioners.
 */
@Environment(EnvType.CLIENT)
public final class CbGui {

    private static net.minecraft.class_310 mc() {
        return net.minecraft.class_310.method_1551();
    }

    // ------------------------------------------------------------------ AbstractSelectionList (class_350)

    /**
     * What 1.20.2's list knew that 1.20.3's widget does not: the "height" it was given (1.20.2 passed the
     * screen height there and drew between y0 and y1; 1.20.3's height is the list's own). Kept on the list
     * by a mixin.
     */
    public interface CbLegacyListGeometry {
        int cb$legacyHeight();

        void cb$setLegacyHeight(int height);
    }

    private static CbLegacyListGeometry legacy(net.minecraft.class_350<?> list) {
        return (CbLegacyListGeometry) list;
    }

    /** 1.20.2 (width, height, y0, y1, itemHeight) -> 1.20.3 (width, y1 - y0, y0, itemHeight): the list's own height */
    public static int cbListHeight(int y0, int y1) {
        return y1 - y0;
    }

    /** after a 1.20.2-shaped list constructor: the height the call passed, for the 1.20.2 "height" field */
    public static void cbListBuilt(net.minecraft.class_350<?> list, net.minecraft.class_310 mc, int width, int height,
                                   int y0, int y1, int itemHeight) {
        legacy(list).cb$setLegacyHeight(height);
    }

    public static void cbObjectListBuilt(net.minecraft.class_4280<?> list, net.minecraft.class_310 mc, int width, int height,
                                         int y0, int y1, int itemHeight) {
        legacy(list).cb$setLegacyHeight(height);
    }

    public static void cbContainerListBuilt(net.minecraft.class_4265<?> list, net.minecraft.class_310 mc, int width, int height,
                                            int y0, int y1, int itemHeight) {
        legacy(list).cb$setLegacyHeight(height);
    }

    public static void cbOptionsListBuilt(net.minecraft.class_353 list, net.minecraft.class_310 mc, int width, int height,
                                          int y0, int y1, int itemHeight) {
        legacy(list).cb$setLegacyHeight(height);
    }

    public static void cbServerListBuilt(net.minecraft.class_4267 list, net.minecraft.class_500 screen, net.minecraft.class_310 mc,
                                         int width, int height, int y0, int y1, int itemHeight) {
        legacy(list).cb$setLegacyHeight(height);
    }

    public static void cbSocialListBuilt(net.minecraft.class_5521 list, net.minecraft.class_5522 screen, net.minecraft.class_310 mc,
                                         int width, int height, int y0, int y1, int itemHeight) {
        legacy(list).cb$setLegacyHeight(height);
    }

    public static void cbWorldListBuilt(net.minecraft.class_528 list, net.minecraft.class_526 screen, net.minecraft.class_310 mc,
                                        int width, int height, int y0, int y1, int itemHeight, String filter,
                                        net.minecraft.class_528 previous) {
        legacy(list).cb$setLegacyHeight(height);
    }

    /**
     * RealmsObjectSelectionList(width, height, y0, y1, itemHeight) -> (width, height, y0, itemHeight): the
     * realms list passed the screen height and y1 on in 1.20.2 as well.
     */
    public static void cbRealmsListBuilt(net.minecraft.class_4904<?> list, int width, int height, int y0, int y1, int itemHeight) {
        legacy(list).cb$setLegacyHeight(height);
    }

    /** was AbstractSelectionList.width (field_22742): the widget's width */
    public static int field_22742(net.minecraft.class_350<?> list) {
        return list.method_25368();
    }

    public static void field_22742(net.minecraft.class_350<?> list, int width) {
        list.method_25358(width);
    }

    /**
     * was AbstractSelectionList.height (field_22743): the height the list was made with (1.20.2 called it
     * height and drew between y0 and y1); a list 1.20.3 code made has no 1.20.2 height and answers its own.
     */
    public static int field_22743(net.minecraft.class_350<?> list) {
        int h = legacy(list).cb$legacyHeight();
        return h != Integer.MIN_VALUE ? h : list.method_25364();
    }

    public static void field_22743(net.minecraft.class_350<?> list, int height) {
        legacy(list).cb$setLegacyHeight(height);
    }

    /** was AbstractSelectionList.y0 (field_19085), the top: the widget's y; a write keeps the bottom */
    public static int field_19085(net.minecraft.class_350<?> list) {
        return list.method_46427();
    }

    public static void field_19085(net.minecraft.class_350<?> list, int y0) {
        int bottom = list.method_55443();
        list.method_46419(y0);
        list.method_53533(bottom - y0);
    }

    /** was AbstractSelectionList.y1 (field_19086), the bottom: a write moves only the bottom */
    public static int field_19086(net.minecraft.class_350<?> list) {
        return list.method_55443();
    }

    public static void field_19086(net.minecraft.class_350<?> list, int y1) {
        list.method_53533(y1 - list.method_46427());
    }

    /** was AbstractSelectionList.x0 (field_19088), the left edge: the widget's x; a write keeps the right edge */
    public static int field_19088(net.minecraft.class_350<?> list) {
        return list.method_46426();
    }

    public static void field_19088(net.minecraft.class_350<?> list, int x0) {
        int right = list.method_55442();
        list.method_46421(x0);
        list.method_25358(right - x0);
    }

    /** was AbstractSelectionList.x1 (field_19087), the right edge: a write moves only the right edge */
    public static int field_19087(net.minecraft.class_350<?> list) {
        return list.method_55442();
    }

    public static void field_19087(net.minecraft.class_350<?> list, int x1) {
        list.method_25358(x1 - list.method_46426());
    }

    /** AbstractSelectionList.clickedHeader(x, y): 1.20.2's had no answer, and its caller then took the click */
    public interface CbLegacyClickedHeader {
        void method_25310(int x, int y);
    }

    public static boolean cbClickedHeader(CbLegacyClickedHeader list, int x, int y) {
        list.method_25310(x, y);
        return true;
    }

    // ------------------------------------------------------------------ Checkbox (class_4286)

    /** the 1.20.2 shape a checkbox was made with: its own size, and whether the label is drawn */
    public interface CbLegacyCheckbox {
        void cb$setLegacy(boolean showLabel);
    }

    /** new Checkbox(x, y, width, height, message, selected[, showLabel]): 1.20.3 builds it from the font, then the 1.20.2 size applies */
    public static net.minecraft.class_327 cbFont() {
        return mc().field_1772;
    }

    public static net.minecraft.class_4286.class_8930 cbNoValueChange() {
        return net.minecraft.class_4286.class_8930.field_47116;
    }

    public static void cbCheckboxBuilt(net.minecraft.class_4286 box, int x, int y, int width, int height,
                                       net.minecraft.class_2561 message, boolean selected) {
        cbCheckboxBuilt(box, x, y, width, height, message, selected, true);
    }

    public static void cbCheckboxBuilt(net.minecraft.class_4286 box, int x, int y, int width, int height,
                                       net.minecraft.class_2561 message, boolean selected, boolean showLabel) {
        box.method_55445(width, height);
        ((CbLegacyCheckbox) box).cb$setLegacy(showLabel);
    }

    // ------------------------------------------------------------------ SystemToast (class_370)

    /** SystemToast.SystemToastIds (1.20.2 enum) -> SystemToast.SystemToastId (1.20.3) */
    public static net.minecraft.class_370.class_9037 cbToastId(top.dext.centurybridge.step.v1_20_3.facade.class_370$class_371 id) {
        return id == null ? null : id.cb$id();
    }

    /** ... and back: a 1.20.3 id that has no 1.20.2 constant answers null */
    public static top.dext.centurybridge.step.v1_20_3.facade.class_370$class_371 cbToastIds(net.minecraft.class_370.class_9037 id) {
        return top.dext.centurybridge.step.v1_20_3.facade.class_370$class_371.cb$of(id);
    }

    /** was SystemToast.add(ToastComponent, SystemToastIds, Component, Component) (method_27024) */
    public static void method_27024(net.minecraft.class_374 toasts, top.dext.centurybridge.step.v1_20_3.facade.class_370$class_371 id,
                                    net.minecraft.class_2561 title, net.minecraft.class_2561 message) {
        net.minecraft.class_370.method_27024(toasts, cbToastId(id), title, message);
    }

    /** was SystemToast.addOrUpdate(ToastComponent, SystemToastIds, Component, Component) (method_1990) */
    public static void method_1990(net.minecraft.class_374 toasts, top.dext.centurybridge.step.v1_20_3.facade.class_370$class_371 id,
                                   net.minecraft.class_2561 title, net.minecraft.class_2561 message) {
        net.minecraft.class_370.method_1990(toasts, cbToastId(id), title, message);
    }

    /** was SystemToast.multiline(Minecraft, SystemToastIds, Component, Component) (method_29047) */
    public static net.minecraft.class_370 method_29047(net.minecraft.class_310 mc, top.dext.centurybridge.step.v1_20_3.facade.class_370$class_371 id,
                                                       net.minecraft.class_2561 title, net.minecraft.class_2561 message) {
        return net.minecraft.class_370.method_29047(mc, cbToastId(id), title, message);
    }

    /**
     * ToastComponent.getToast(Class, Object token): a 1.20.2 mod looks its system toasts up by the
     * SystemToastIds it made them with, which now carry the 1.20.3 SystemToastId as their token.
     */
    public static <T extends net.minecraft.class_368> T method_1997(net.minecraft.class_374 toasts, Class<? extends T> type, Object token) {
        Object t = token instanceof top.dext.centurybridge.step.v1_20_3.facade.class_370$class_371 ids ? ids.cb$id() : token;
        return toasts.method_1997(type, t);
    }

    // ------------------------------------------------------------------ link / notice / backup / data-pack screens

    /** a 1.20.2 "screen to go back to" as the 1.20.3 "what to do when closed" */
    public static Runnable cbReturnTo(net.minecraft.class_437 screen) {
        return () -> mc().method_1507(screen);
    }

    /** DatapackLoadFailureScreen's back button closed the screen in 1.20.2 (setScreen(null)) */
    public static Runnable cbCloseScreen() {
        return () -> mc().method_1507(null);
    }

    /** ConfirmLinkScreen's confirmation text for a link, as the 1.20.2 5-argument constructor made it */
    public static net.minecraft.class_2561 cbConfirmMessage(boolean trusted, String url) {
        return CbConfirmLinkScreenAccess.cb$confirmMessage(trusted, url);
    }

    /**
     * was ConfirmLinkScreen.confirmLinkNow(String, Screen, boolean) (method_49623): 1.20.2's body -- open the
     * link if confirmed, then go back to the screen; "trusted" chooses the confirmation wording (1.20.3's
     * confirmLinkNow(Screen, String) always asks as trusted).
     */
    public static void method_49623(String url, net.minecraft.class_437 last, boolean trusted) {
        net.minecraft.class_310 mc = mc();
        mc.method_1507(new net.minecraft.class_407(confirmed -> {
            if (confirmed) {
                net.minecraft.class_156.method_668().method_670(url);
            }
            mc.method_1507(last);
        }, url, trusted));
    }

    /** was ConfirmLinkScreen.confirmLink(String, Screen, boolean) (method_49625): a button that confirms the link */
    public static net.minecraft.class_4185.class_4241 method_49625(String url, net.minecraft.class_437 last, boolean trusted) {
        return button -> method_49623(url, last, trusted);
    }

    /** was NoticeWithLinkScreen.createWorldSymlinkWarningScreen(Screen) (method_52748) */
    public static net.minecraft.class_437 method_52748(net.minecraft.class_437 last) {
        return net.minecraft.class_8669.method_52748(cbReturnTo(last));
    }

    /** was NoticeWithLinkScreen.createPackSymlinkWarningScreen(Screen) (method_52750) */
    public static net.minecraft.class_437 method_52750(net.minecraft.class_437 last) {
        return net.minecraft.class_8669.method_52750(cbReturnTo(last));
    }

    // ------------------------------------------------------------------ world selection and editing

    /** the level name 1.20.2's EditWorldScreen showed: the summary's, or "" when the summary cannot be read */
    public static String cbLevelName(net.minecraft.class_32.class_5143 access) {
        try {
            net.minecraft.class_34 summary = access.method_29584(access.method_54545());
            return summary == null ? "" : summary.method_252();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * was EditWorldScreen.makeBackupAndShowToast(LevelStorageSource, String) (method_29784), 1.20.2's body:
     * open the level, back it up (with its own toast), and on failure show the access-failure toast.
     */
    public static void method_29784(net.minecraft.class_32 source, String levelId) {
        boolean opened = false;
        try (net.minecraft.class_32.class_5143 access = source.method_52236(levelId)) {
            opened = true;
            net.minecraft.class_524.method_2701(access);
        } catch (java.io.IOException e) {
            if (!opened) {
                net.minecraft.class_370.method_27023(mc(), levelId);
            }
            org.slf4j.LoggerFactory.getLogger(CbGui.class).warn("Failed to create backup of level {}", levelId, e);
        } catch (net.minecraft.class_8579 e) {
            org.slf4j.LoggerFactory.getLogger(CbGui.class).warn("{}", e.getMessage());
            net.minecraft.class_370.method_27023(mc(), levelId);
        }
    }

    /**
     * SelectWorldScreen.updateButtonStatus(boolean active, boolean deleteActive) of 1.20.2; 1.20.3 calls
     * updateButtonStatus(LevelSummary) on selection. A 1.20.2 screen subclass overriding the old shape gets
     * it with what 1.20.2's world list passed: a world entry selected (its summary) and not disabled, and
     * any entry selected; no entry selected (null) is (false, false), as 1.20.2's init and deselection gave.
     */
    public interface CbLegacyButtonStatus {
        void method_19940(boolean active, boolean deleteActive);
    }

    public static void cbUpdateButtonStatus(CbLegacyButtonStatus screen, net.minecraft.class_34 summary) {
        screen.method_19940(summary != null && !summary.method_33784(), summary != null);
    }

    /**
     * WorldSelectionList.Entry.isSelectable() (method_43465): a 1.20.2 mod's entry that answers it (adapterHooks
     * marks it) is selected the 1.20.2 way, by its answer (see the WorldSelectionList mixin).
     */
    public interface CbLegacySelectableEntry {
        boolean method_43465();
    }

    /** the screen a 1.20.2 createFreshLevel call leaves the player on when the new level cannot be made */
    public static net.minecraft.class_437 cbCurrentScreen() {
        return mc().field_1755;
    }

    // ------------------------------------------------------------------ AbstractWidget render / tooltips (class_339)

    /** a 1.20.2 widget's own render override, renamed (overrideRenames): the final 1.20.3 render calls it */
    public interface CbLegacyWidgetRender {
        void cb$render(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick);
    }

    private static final ThreadLocal<java.util.Set<Object>> RENDERING =
        ThreadLocal.withInitial(() -> java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));

    /** first entry into a widget's render on this thread: the legacy override runs; its super.render(..) is a re-entry */
    public static boolean cbRenderEnter(Object widget) {
        return RENDERING.get().add(widget);
    }

    public static void cbRenderExit(Object widget) {
        RENDERING.get().remove(widget);
    }

    /** a 1.20.2 widget that overrides createTooltipPositioner(): its answer places its tooltip (adapterHooks marks it) */
    public interface CbLegacyTooltipPositioner {
        net.minecraft.class_8000 method_47937();
    }

    private static final ThreadLocal<Object> TOOLTIP_WIDGET = new ThreadLocal<>();

    /** 1.20.3's AbstractWidget.render refreshing the widget's tooltip, with the widget known for the positioner */
    public static void cbRefreshTooltip(net.minecraft.class_7919 tooltip, Object widget, boolean hovered, boolean focused,
                                        net.minecraft.class_8030 area) {
        Object outer = TOOLTIP_WIDGET.get();
        TOOLTIP_WIDGET.set(widget);
        try {
            tooltip.method_54384(hovered, focused, area);
        } finally {
            TOOLTIP_WIDGET.set(outer);
        }
    }

    public static Object cbTooltipWidget() {
        return TOOLTIP_WIDGET.get();
    }

    /**
     * 1.20.2's AbstractWidget.createTooltipPositioner(): below or above the widget when it has the focus by
     * keyboard and is not hovered, else at the mouse -- built from the widget's area at this refresh, as
     * 1.20.2 built it from the widget at each tooltip update.
     */
    public static net.minecraft.class_8000 cbTooltipPositioner(net.minecraft.class_339 widget) {
        if (!widget.method_49606() && widget.method_25370() && mc().method_48186().method_48183()) {
            return new net.minecraft.class_7999(widget.method_48202());
        }
        return new net.minecraft.class_8092(widget.method_48202());
    }

    /** a positioner a 1.20.2 constructor made from a widget: it follows the widget (see the positioner mixins) */
    public interface CbWidgetTooltipArea {
        void cb$follow(net.minecraft.class_339 widget);
    }

    /** BelowOrAbove / MenuTooltipPositioner(AbstractWidget) -> (ScreenRectangle): the widget's area now */
    public static net.minecraft.class_8030 cbWidgetArea(net.minecraft.class_339 widget) {
        return widget.method_48202();
    }

    public static void cbFollowWidget(net.minecraft.class_7999 positioner, net.minecraft.class_339 widget) {
        ((CbWidgetTooltipArea) positioner).cb$follow(widget);
    }

    public static void cbFollowWidget(net.minecraft.class_8092 positioner, net.minecraft.class_339 widget) {
        ((CbWidgetTooltipArea) positioner).cb$follow(widget);
    }

    // ------------------------------------------------------------------ ReceivingLevelScreen (class_434)

    /** what the ReceivingLevelScreen mixin records and keeps */
    public interface CbLegacyReceivingLevel {
        /** was ReceivingLevelScreen.loadingPacketsReceived() (method_40040), given back by the mixin */
        void method_40040();

        boolean cb$loadingPacketsReceived();

        java.util.function.BooleanSupplier cb$levelReceived();
    }

    /**
     * 1.20.2's ReceivingLevelScreen closing test, for a screen made with the 1.20.2 constructor (1.20.3 takes
     * the test as a supplier and keeps the 30 s timeout itself): one tick after loadingPacketsReceived(),
     * close once the player is there and their section is ready -- out of the build height, compiled, or
     * the player spectating or dead.
     */
    public static final class CbLegacyLoading implements java.util.function.BooleanSupplier {
        net.minecraft.class_434 screen;
        private boolean oneTickSkipped;

        @Override
        public boolean getAsBoolean() {
            if (screen == null) {
                return false;
            }
            if (!oneTickSkipped) {
                oneTickSkipped = ((CbLegacyReceivingLevel) screen).cb$loadingPacketsReceived();
                return false;
            }
            net.minecraft.class_310 mc = mc();
            if (mc.field_1724 == null) {
                return false;
            }
            net.minecraft.class_2338 pos = mc.field_1724.method_24515();
            boolean outside = mc.field_1687 != null && mc.field_1687.method_31601(pos.method_10264());
            return outside || mc.field_1769.method_40050(pos) || mc.field_1724.method_7325() || !mc.field_1724.method_5805();
        }
    }

    /** new ReceivingLevelScreen(): its closing test is 1.20.2's */
    public static java.util.function.BooleanSupplier cbLegacyLoading() {
        return new CbLegacyLoading();
    }

    public static void cbReceivingLevelBuilt(net.minecraft.class_434 screen) {
        if (((CbLegacyReceivingLevel) screen).cb$levelReceived() instanceof CbLegacyLoading loading) {
            loading.screen = screen;
        }
    }

    private CbGui() {
    }
}
