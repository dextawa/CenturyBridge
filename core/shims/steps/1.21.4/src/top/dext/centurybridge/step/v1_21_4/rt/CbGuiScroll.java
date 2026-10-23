package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 scrolling GUI API on 1.21.4. 1.21.4 moved scrolling into AbstractScrollArea (class_7528, which
 * 1.21.3 called AbstractScrollWidget): AbstractSelectionList and AbstractContainerWidget now extend it, and
 * 1.21.3's AbstractScrollWidget lives on as AbstractTextAreaWidget (class_10415).
 *
 * The old methods vanilla no longer calls under any name are handed back through adapters: a mod override is
 * renamed to the adapter's method (overrideRenames) and the vanilla place that used to call it asks the
 * adapter; the old method itself (a mixin cover) asks it too, so calls by name still reach the override.
 * {@link #enter} keeps a mod's own super call from coming back into its override: inside the dispatch the
 * cover answers what vanilla 1.21.3 answered.
 */
@Environment(EnvType.CLIENT)
public final class CbGuiScroll {

    private CbGuiScroll() {
    }

    /** AbstractSelectionList.setClampedScrollAmount(double) (method_60321) */
    public static final int CLAMPED_SCROLL = 1;
    /** AbstractSelectionList.getDefaultScrollbarPosition() (method_57718) */
    public static final int DEFAULT_SCROLLBAR = 1 << 1;
    /** AbstractSelectionList.clickedHeader(int, int) (method_25310) */
    public static final int CLICKED_HEADER = 1 << 2;
    /** AbstractScrollWidget.withinContentAreaPoint(double, double) (method_44388) */
    public static final int CONTENT_AREA_POINT = 1 << 3;
    /** AbstractWidget.clicked(double, double) (method_25361) */
    public static final int CLICKED = 1 << 4;
    /** AbstractSelectionList.isValidMouseClick(int) (method_53812) */
    public static final int VALID_MOUSE_CLICK = 1 << 5;

    private static final ThreadLocal<Map<Object, Integer>> ACTIVE = ThreadLocal.withInitial(IdentityHashMap::new);

    /** true when the dispatch of {@code which} to the adapter is not already running for {@code self} on this thread */
    public static boolean enter(Object self, int which) {
        Map<Object, Integer> m = ACTIVE.get();
        int cur = m.getOrDefault(self, 0);
        if ((cur & which) != 0) {
            return false;
        }
        m.put(self, cur | which);
        return true;
    }

    public static void exit(Object self, int which) {
        Map<Object, Integer> m = ACTIVE.get();
        int cur = m.getOrDefault(self, 0) & ~which;
        if (cur == 0) {
            m.remove(self);
        } else {
            m.put(self, cur);
        }
    }

    // ------------------------------------------------------------------ adapters (the renamed overrides)

    /** a 1.21.3 list overriding setClampedScrollAmount(double): every scroll set went through it */
    public interface CbLegacyClampedScroll {
        void cb$setClampedScrollAmount(double amount);
    }

    /** a 1.21.3 list overriding getDefaultScrollbarPosition(): getScrollbarPosition's default answer */
    public interface CbLegacyDefaultScrollbar {
        int cb$getDefaultScrollbarPosition();
    }

    /** a 1.21.3 list overriding clickedHeader(int, int): asked when a click hit no entry */
    public interface CbLegacyClickedHeader {
        boolean cb$clickedHeader(int x, int y);
    }

    /** a 1.21.3 AbstractScrollWidget overriding withinContentAreaPoint(double, double): mouseClicked asked it */
    public interface CbLegacyContentAreaPoint {
        boolean cb$withinContentAreaPoint(double x, double y);
    }

    /** a 1.21.3 widget overriding clicked(double, double): AbstractWidget.mouseClicked asked it */
    public interface CbLegacyClicked {
        boolean cb$clicked(double x, double y);
    }

    /** a 1.21.3 list overriding isValidMouseClick(int): its mouseClicked refused a press the override refused */
    public interface CbLegacyValidMouseClick {
        boolean cb$isValidMouseClick(int button);
    }

    /** the old void updateScrollingState(double, double, int) a 1.21.3 list declares (the override hook's adapter) */
    public interface CbLegacyUpdateScrollingState {
        void method_25318(double x, double y, int button);
    }

    // ------------------------------------------------------------------ override hooks

    /**
     * 1.21.4 updateScrolling(x, y, button) answers whether the press started dragging the scroll bar; 1.21.3's
     * updateScrollingState only set that state. The list's own override sets it, the answer is the state.
     */
    public static boolean cbUpdateScrolling(CbLegacyUpdateScrollingState list, double x, double y, int button) {
        list.method_25318(x, y, button);
        return ((top.dext.centurybridge.step.v1_21_4.mixin.CbGuiScrollAreaAccess) list).cb$scrolling();
    }

    /** 1.21.3 getMaxPosition() is 1.21.4 contentHeight() without the 4 pixels of padding 1.21.4 counts in it */
    public static int cbContentHeight(int maxPosition) {
        return maxPosition + 4;
    }

    // ------------------------------------------------------------------ AbstractSelectionList.SCROLLBAR_WIDTH (field_45909)

    /** 1.21.3 AbstractSelectionList.SCROLLBAR_WIDTH (6) is AbstractScrollArea.SCROLLBAR_WIDTH since 1.21.4 */
    public static int field_45909() {
        return net.minecraft.class_7528.field_55258;
    }
}
