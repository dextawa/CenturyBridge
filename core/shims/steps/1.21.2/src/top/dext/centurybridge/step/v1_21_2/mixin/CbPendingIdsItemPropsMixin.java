package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_10162;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbPendingIdsItems;

/** Item.Properties: its id and the functions the item derives from it (CbPendingIdsItems) */
@Mixin(class_1792.class_1793.class)
public abstract class CbPendingIdsItemPropsMixin implements CbPendingIdsItems.ItemProps {

    @Shadow
    private class_5321<class_1792> field_54117;

    @Shadow
    private class_10162<class_1792, String> field_54118;

    @Shadow
    private class_10162<class_1792, class_2960> field_54119;

    @Shadow
    protected abstract String method_63689();

    @Shadow
    public abstract class_2960 method_63691();

    @Override
    public class_5321<class_1792> cb$id() {
        return this.field_54117;
    }

    @Override
    public void cb$setId(class_5321<class_1792> id) {
        this.field_54117 = id;
    }

    @Override
    public class_10162<class_1792, String> cb$descriptionIdFn() {
        return this.field_54118;
    }

    @Override
    public class_10162<class_1792, class_2960> cb$modelFn() {
        return this.field_54119;
    }

    @Override
    public String cb$effectiveDescriptionId() {
        return this.method_63689();
    }

    @Override
    public class_2960 cb$effectiveModel() {
        return this.method_63691();
    }
}
