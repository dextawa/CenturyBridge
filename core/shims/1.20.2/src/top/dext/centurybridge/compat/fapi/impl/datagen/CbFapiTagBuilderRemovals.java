package top.dext.centurybridge.compat.fapi.impl.datagen;

import java.util.List;

/**
 * The removals a tag builder collects for data generation (the removal half of 1.20.1's datagen
 * TagBuilderHooks; 1.20.2's Fabric API keeps the replace half itself). Every TagBuilder implements it
 * through this layer's mixin.
 */
public interface CbFapiTagBuilderRemovals {

    void cb$removeElement(net.minecraft.class_2960 id);

    void cb$removeTag(net.minecraft.class_2960 id);

    List<net.minecraft.class_3497> cb$getRemove();
}
