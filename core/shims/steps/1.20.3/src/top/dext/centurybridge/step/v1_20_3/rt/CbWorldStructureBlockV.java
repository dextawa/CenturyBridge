package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_3218;
import net.minecraft.class_3499;

/**
 * What CbWorldStructureBlockEntityMixin adds back to StructureBlockEntity: 1.20.2's loadStructure(level,
 * unlessSameSize), loadStructure(level, unlessSameSize, template) and getStructurePath(), with their 1.20.2
 * bodies (1.20.3 split them into placeStructure / placeStructureIfSameSize / loadStructureInfo).
 */
public interface CbWorldStructureBlockV {
    boolean method_11368(class_3218 level, boolean unlessSameSize);

    boolean method_21864(class_3218 level, boolean unlessSameSize, class_3499 template);

    String method_21865();
}
