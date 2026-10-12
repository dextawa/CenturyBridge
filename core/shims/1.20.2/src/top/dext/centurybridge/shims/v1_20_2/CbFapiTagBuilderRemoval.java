package top.dext.centurybridge.shims.v1_20_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.compat.fapi.impl.datagen.CbFapiTagBuilderRemovals;

/**
 * A tag builder collects the removals data generation writes into its tag file (the removal half of
 * 1.20.1's datagen TagBuilderMixin): an element removal is a required element entry, a tag removal a
 * required tag entry, as 1.20.1 made them.
 */
@Mixin(net.minecraft.class_3495.class)
abstract class CbFapiTagBuilderRemoval implements CbFapiTagBuilderRemovals {

    @Unique
    private final List<net.minecraft.class_3497> cb$remove = new ArrayList<>();

    @Override
    public void cb$removeElement(net.minecraft.class_2960 id) {
        cb$remove.add(net.minecraft.class_3497.method_43937(id));
    }

    @Override
    public void cb$removeTag(net.minecraft.class_2960 id) {
        cb$remove.add(net.minecraft.class_3497.method_43945(id));
    }

    @Override
    public List<net.minecraft.class_3497> cb$getRemove() {
        return Collections.unmodifiableList(cb$remove);
    }
}
