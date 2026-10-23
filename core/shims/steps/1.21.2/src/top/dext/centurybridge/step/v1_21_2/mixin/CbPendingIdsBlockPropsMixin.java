package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Optional;
import net.minecraft.class_10162;
import net.minecraft.class_2248;
import net.minecraft.class_4970;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbPendingIdsItems;

/** BlockBehaviour.Properties: its id and the functions the block derives from it (CbPendingIdsItems) */
@Mixin(class_4970.class_2251.class)
public abstract class CbPendingIdsBlockPropsMixin implements CbPendingIdsItems.BlockProps {

    @Shadow
    private class_5321<class_2248> field_54006;

    @Shadow
    private class_10162<class_2248, Optional<class_5321<class_52>>> field_54007;

    @Shadow
    private class_10162<class_2248, String> field_54008;

    @Shadow
    protected abstract Optional<class_5321<class_52>> method_63505();

    @Shadow
    protected abstract String method_63506();

    @Override
    public class_5321<class_2248> cb$id() {
        return this.field_54006;
    }

    @Override
    public void cb$setId(class_5321<class_2248> id) {
        this.field_54006 = id;
    }

    @Override
    public class_10162<class_2248, Optional<class_5321<class_52>>> cb$dropsFn() {
        return this.field_54007;
    }

    @Override
    public class_10162<class_2248, String> cb$descriptionIdFn() {
        return this.field_54008;
    }

    @Override
    public Optional<class_5321<class_52>> cb$effectiveDrops() {
        return this.method_63505();
    }

    @Override
    public String cb$effectiveDescriptionId() {
        return this.method_63506();
    }
}
