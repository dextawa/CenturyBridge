package top.dext.centurybridge.step.v26_1.mixin;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallEditBoxFilter;

/**
 * 1.21.11 EditBox.setFilter(Predicate) (class_342.method_1890) and the filter it set (class_342.field_2104):
 * 1.21.11 tested the text the box would hold before setValue, insertText and deleteCharsToPos changed it, and left
 * the box untouched (no change, no responder call) when the filter refused. 26.1 has no filter; the same three tests
 * run in CbWallEditBoxFilterReadsMixin once a mod set one. 1.21.11's default filter (any non-null text) is not
 * applied to boxes no mod gave a filter: those are 26.1's, untouched.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = EditBox.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallEditBoxFilterMixin implements CbWallEditBoxFilter {

    @Unique
    private Predicate<String> cb$filter;

    /** 1.21.11 EditBox.setFilter(filter) */
    @CbVanillaReads("CbWallEditBoxFilterReadsMixin: setValue / insertText / deleteCharsToPos test the text against it")
    public void setFilter(Predicate<String> filter) {
        this.cb$filter = filter;
    }

    @Override
    public Predicate<String> cb$filter() {
        return this.cb$filter;
    }
}
