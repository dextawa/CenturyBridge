package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.Objects;
import net.minecraft.class_332;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbSelectionLists;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8 AbstractSelectionList members 1.21.9 dropped (javap of both code jars), restored with 1.21.8's bodies over
 * 1.21.9's list: getEntry(index), isSelectedItem(index), getFirstElement(), remove(index), setSelectedIndex(index),
 * ensureVisible(entry) (1.21.8 kept one row of margin above and below, where 1.21.9's scrollToEntry keeps none),
 * renderHeader / renderDecorations (empty in 1.21.8, for subclasses) and the list header (the renderHeader flag and
 * headerHeight that 1.21.8's six-argument constructor set, CbSelectionLists.cbHeader).
 * The injections into 1.21.9's own methods (the header's place, renderHeader / renderDecorations calls) are
 * CbSelectionListHooksMixin's, so a later release dropping one of those methods loses only that hook.
 */
@Mixin(value = class_350.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSelectionListRestoredMixin<E extends class_350.class_351<E>> implements CbSelectionLists.LegacyHeader {

    @Shadow
    @Final
    protected int field_62109;

    @Shadow
    protected abstract void method_25330(E entry);

    @Shadow
    protected abstract int method_25340();

    @Shadow
    protected abstract void method_44397(class_332 graphics, int mouseX, int mouseY, float delta, E entry);

    private boolean field_22747;

    protected int field_22748;

    @Override
    public boolean cb$hasHeader() {
        return this.field_22747;
    }

    @Override
    public int cb$headerHeight() {
        return this.field_22748;
    }

    @Override
    public void cb$renderHeader(class_332 graphics, int x, int y) {
        this.method_25312(graphics, x, y);
    }

    @Override
    public void cb$renderDecorations(class_332 graphics, int mouseX, int mouseY) {
        this.method_25320(graphics, mouseX, mouseY);
    }

    @Override
    public void cb$header(int headerHeight) {
        this.field_22747 = true;
        this.field_22748 = headerHeight;
    }

    private class_350<E> cb$self() {
        return (class_350<E>) (Object) this;
    }

    /** AbstractSelectionList.getEntry(index) */
    protected E method_25326(int index) {
        return this.cb$self().method_25396().get(index);
    }

    /** AbstractSelectionList.isSelectedItem(index) */
    protected boolean method_25332(int index) {
        return Objects.equals(this.cb$self().method_25334(), this.cb$self().method_25396().get(index));
    }

    /** AbstractSelectionList.getFirstElement() */
    public E method_48200() {
        return this.cb$self().method_25396().get(0);
    }

    /** AbstractSelectionList.remove(index): 1.21.9's removeEntry answers nothing; the entry was in the list */
    protected E method_25338(int index) {
        E entry = this.cb$self().method_25396().get(index);
        this.method_25330(entry);
        return entry;
    }

    /** AbstractSelectionList.setSelectedIndex(index) */
    public void method_64847(int index) {
        if (index == -1) {
            this.cb$self().method_25313(null);
        } else if (this.method_25340() != 0) {
            this.cb$self().method_25313(this.method_25326(index));
        }
    }

    /** AbstractSelectionList.ensureVisible(entry): a row of margin above and below, as 1.21.8 */
    protected void method_25328(E entry) {
        class_350<E> self = this.cb$self();
        int itemHeight = this.field_62109;
        int rowTop = entry.method_73382();
        int above = rowTop - self.method_46427() - 4 - itemHeight;
        if (above < 0) {
            self.method_44382(self.method_44387() + above);
        }
        int below = self.method_55443() - rowTop - itemHeight - itemHeight;
        if (below < 0) {
            self.method_44382(self.method_44387() - below);
        }
    }

    /**
     * AbstractSelectionList.renderItem(graphics, mouseX, mouseY, delta, index, left, top, width, height), 1.21.8's shape:
     * 1.21.9's renderItem of that entry (the entry draws at its own row, which is the row 1.21.8 passed). A 1.21.8
     * override's super call lands here; 1.21.9 calls the new shape, which the step's hook hands to the override.
     */
    protected void method_44397(class_332 graphics, int mouseX, int mouseY, float delta, int index, int left, int top, int width,
                                int height) {
        this.method_44397(graphics, mouseX, mouseY, delta, this.cb$self().method_25396().get(index));
    }

    /**
     * AbstractSelectionList.renderSelection(graphics, top, width, height, outerColor, innerColor): 1.21.8's body (the
     * outer box two pixels around the row, the inner one a pixel inside it, centred on the list)
     */
    protected void method_44398(class_332 graphics, int top, int width, int height, int outerColor, int innerColor) {
        class_350<E> self = this.cb$self();
        int left = self.method_46426() + (self.method_25368() - width) / 2;
        int right = self.method_46426() + (self.method_25368() + width) / 2;
        graphics.method_25294(left, top - 2, right, top + height + 2, outerColor);
        graphics.method_25294(left + 1, top - 1, right - 1, top + height + 1, innerColor);
    }

    /** AbstractSelectionList.renderHeader(graphics, x, y): empty in 1.21.8 */
    @CbVanillaReads("CbSelectionListHooksMixin: called before 1.21.9's renderListItems when the list has a 1.21.8 header, as 1.21.8's renderWidget did")
    protected void method_25312(class_332 graphics, int x, int y) {
    }

    /** AbstractSelectionList.renderDecorations(graphics, mouseX, mouseY): empty in 1.21.8 */
    @CbVanillaReads("CbSelectionListHooksMixin: called at the end of 1.21.9's renderWidget, as 1.21.8's renderWidget did last")
    protected void method_25320(class_332 graphics, int mouseX, int mouseY) {
    }
}
