package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collections;
import java.util.List;
import net.minecraft.class_2960;
import net.minecraft.class_3495;
import net.minecraft.class_3497;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagenTags;

/** Fabric API 0.116's TagBuilderMixin removal half on 1.21.2's TagBuilder: the "fabric:remove" entries (CbItemDatagenTags). */
@Mixin(class_3495.class)
public abstract class CbItemTagBuilderMixin implements CbItemDatagenTags.Removals {

    @Unique
    private final List<class_3497> cb$remove = CbItemDatagenTags.newRemovals();

    @Override
    public void cb$removeElement(class_2960 id) {
        this.cb$remove.add(class_3497.method_43937(id));
    }

    @Override
    public void cb$removeTag(class_2960 id) {
        this.cb$remove.add(class_3497.method_43945(id));
    }

    @Override
    public List<class_3497> cb$getRemove() {
        return Collections.unmodifiableList(this.cb$remove);
    }
}
