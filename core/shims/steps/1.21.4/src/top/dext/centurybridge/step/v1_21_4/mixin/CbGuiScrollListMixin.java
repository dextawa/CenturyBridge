package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * AbstractSelectionList's 1.21.3 scrolling surface on 1.21.4, where the list scrolls as an AbstractScrollArea.
 * The old methods 1.21.4 renamed are not given back: getScrollAmount / setScrollAmount / clampScrollAmount /
 * getMaxScroll / scrollbarVisible / getScrollbarPosition are scrollAmount / setScrollAmount / refreshScrollAmount /
 * maxScrollAmount / scrollbarVisible / scrollBarX, and a 1.21.3 mod's overrides (overrideRenames), calls (methodRenames)
 * and mixins (which follow methodRenames) all meet on the new name, the one 1.21.4's own code calls. The rest are given
 * back on the list, through the adapters of {@link CbGuiScroll}:
 * <ul>
 * <li>isValidMouseClick: 1.21.3's mouseClicked refused a press it refused first; a list overriding it is asked so again
 *     (not renamed onto isValidClickButton, which 1.21.4's scroll bar asks: a list overriding both keeps both);</li>
 * <li>getMaxPosition is contentHeight less the 4 pixels 1.21.4 adds to it; updateScrollingState is
 *     updateScrolling (both reconnected by override hooks);</li>
 * <li>setClampedScrollAmount: every 1.21.3 scroll set went through it (setScrollAmount called it), so 1.21.4's
 *     setScrollAmount on a list hands the value to a 1.21.3 override;</li>
 * <li>getDefaultScrollbarPosition: the default of getScrollbarPosition, so scrollBarX asks a 1.21.3 override
 *     (CbGuiScrollListMixinHooks, default priority);</li>
 * <li>clickedHeader(x, y): asked, as 1.21.3's mouseClicked did, when a press over the list hit no entry, with
 *     the point relative to the header's top left;</li>
 * <li>setRenderHeader(render, height): the header flag and height 1.21.4 takes in the constructor.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_350.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGuiScrollListMixin extends net.minecraft.class_9017 {

    /** renderHeader */
    @Shadow
    private boolean field_22747;

    /** headerHeight */
    @Shadow
    protected int field_22748;

    protected CbGuiScrollListMixin() {
        super(0, 0, 0, 0, null);
    }

    /** contentHeight() */
    @Shadow
    protected abstract int method_44395();

    /** scrollBarX() */
    @Shadow
    protected abstract int method_65507();

    private net.minecraft.class_350<?> cb$self() {
        return (net.minecraft.class_350<?>) (Object) this;
    }

    /** was setClampedScrollAmount(double) (method_60321): the amount clamped to 0..maxScroll */
    public void method_60321(double amount) {
        if (this instanceof CbGuiScroll.CbLegacyClampedScroll legacy && CbGuiScroll.enter(this, CbGuiScroll.CLAMPED_SCROLL)) {
            try {
                legacy.cb$setClampedScrollAmount(amount);
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.CLAMPED_SCROLL);
            }
            return;
        }
        super.method_44382(amount);
    }

    /** 1.21.4 setScrollAmount(double) on a list: 1.21.3's setScrollAmount went through setClampedScrollAmount */
    @Override
    public void method_44382(double amount) {
        if (this instanceof CbGuiScroll.CbLegacyClampedScroll legacy && CbGuiScroll.enter(this, CbGuiScroll.CLAMPED_SCROLL)) {
            try {
                legacy.cb$setClampedScrollAmount(amount);
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.CLAMPED_SCROLL);
            }
            return;
        }
        super.method_44382(amount);
    }

    /** was getMaxPosition() (method_25317): rows times their height plus the header; contentHeight adds 4 */
    protected int method_25317() {
        return this.method_44395() - 4;
    }

    /** was getDefaultScrollbarPosition() (method_57718): right of the rows, past the scroll bar's gap */
    protected int method_57718() {
        if (this instanceof CbGuiScroll.CbLegacyDefaultScrollbar legacy && CbGuiScroll.enter(this, CbGuiScroll.DEFAULT_SCROLLBAR)) {
            try {
                return legacy.cb$getDefaultScrollbarPosition();
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.DEFAULT_SCROLLBAR);
            }
        }
        // AbstractSelectionList.scrollBarX's own answer (1.21.3: getRealRowRight() + 10, the same place)
        return cb$self().method_31383() + net.minecraft.class_7528.field_55258 + 2;
    }

    /** was isValidMouseClick(int) (method_53812): the left button */
    protected boolean method_53812(int button) {
        if (this instanceof CbGuiScroll.CbLegacyValidMouseClick legacy && CbGuiScroll.enter(this, CbGuiScroll.VALID_MOUSE_CLICK)) {
            try {
                return legacy.cb$isValidMouseClick(button);
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.VALID_MOUSE_CLICK);
            }
        }
        return button == 0;
    }

    /** was updateScrollingState(double, double, int) (method_25318) */
    protected void method_25318(double mouseX, double mouseY, int button) {
        this.method_65505(mouseX, mouseY, button);
    }

    /** was setRenderHeader(boolean, int) (method_25315) */
    protected void method_25315(boolean renderHeader, int headerHeight) {
        this.field_22747 = renderHeader;
        this.field_22748 = headerHeight;
        if (!renderHeader) {
            this.field_22748 = 0;
        }
    }

    /** was clickedHeader(int, int) (method_25310): the header took no click */
    protected boolean method_25310(int x, int y) {
        if (this instanceof CbGuiScroll.CbLegacyClickedHeader legacy && CbGuiScroll.enter(this, CbGuiScroll.CLICKED_HEADER)) {
            try {
                return legacy.cb$clickedHeader(x, y);
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.CLICKED_HEADER);
            }
        }
        return false;
    }

    /**
     * 1.21.3's mouseClicked offered a press over the list that hit no entry to clickedHeader; 1.21.4 has no header
     * clicks. Only a list with a 1.21.3 clickedHeader override is asked.
     */
    @Override
    public boolean method_25402(double mouseX, double mouseY, int button) {
        // 1.21.3 order: a list with its own isValidMouseClick refuses the press before anything else
        if (this instanceof CbGuiScroll.CbLegacyValidMouseClick && !this.method_53812(button)) {
            return false;
        }
        if (super.method_25402(mouseX, mouseY, button)) {
            return true;
        }
        if (!(this instanceof CbGuiScroll.CbLegacyClickedHeader)) {
            return false;
        }
        net.minecraft.class_350<?> self = cb$self();
        if (!this.method_53812(button) || !this.method_25405(mouseX, mouseY) || self.method_19355(mouseX, mouseY).isPresent()) {
            return false;
        }
        int x = (int) (mouseX - (double) (this.method_46426() + this.field_22758 / 2 - self.method_25322() / 2));
        int y = (int) (mouseY - (double) this.method_46427()) + (int) this.method_44387() - 4;
        return this.method_25310(x, y);
    }
}
