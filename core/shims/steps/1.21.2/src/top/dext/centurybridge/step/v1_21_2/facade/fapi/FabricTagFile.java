package top.dext.centurybridge.step.v1_21_2.facade.fapi;

import java.util.List;

/**
 * FabricTagFile of 1.21.1's Fabric API (fabric-tag-api-v1, absent from 1.21.2's Fabric API), classRenamed:
 * the entries a tag file's "fabric:remove" list names. 0.116 injected it into TagFile; the layer implements it
 * there (CbRestFapiTagFileMixin, rt/CbRestFapiTags).
 */
public interface FabricTagFile {

    default List<net.minecraft.class_3497> remove() {
        throw new AssertionError("Implemented via mixin");
    }
}
