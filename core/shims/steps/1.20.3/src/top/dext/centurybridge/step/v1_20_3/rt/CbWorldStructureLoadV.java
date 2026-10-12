package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_3218;

/**
 * Marks a StructureBlockEntity class converted from 1.20.2 that overrides 1.20.2's loadStructure(level) (the
 * converter adds it to such a class; its own method implements it). 1.20.3 kept that name and descriptor for
 * loadStructureInfo, so a 1.20.2 call of loadStructure(level) reaches this override through the interface and
 * every other block entity the 1.20.2 way (CbWorldBlocks.loadStructure).
 */
public interface CbWorldStructureLoadV {
    boolean method_11376(class_3218 level);
}
