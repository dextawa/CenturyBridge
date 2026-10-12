package top.dext.centurybridge.step.v1_20_3.rt;

/**
 * Marks a StructureBlockEntity subclass converted from 1.20.2 (the converter adds it to every such class): where
 * 1.20.2 asked a structure block to load its structure -- the LOAD_AREA packet, a LOAD-mode block's redstone
 * trigger -- 1.20.3 asks such a block the 1.20.2 way (CbWorldStructureBlockV), so its overrides are heard again.
 */
public interface CbWorldLegacyStructureBlock {
}
