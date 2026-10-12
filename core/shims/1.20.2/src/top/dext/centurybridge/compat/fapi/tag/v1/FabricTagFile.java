package top.dext.centurybridge.compat.fapi.tag.v1;

import java.util.List;

/**
 * Fabric API tag API v1's FabricTagFile (fabric-tag-api-v1, which 0.92 backported to 1.20.1;
 * 1.20.2's Fabric API does not have it): the entries a tag file removes from the tag, read from its
 * "fabric:remove" list. Every TagFile implements it through this layer's mixin
 * (CbFapiTagFileRemoval), as 1.20.1's TagFileMixin made it; any other implementor keeps this default,
 * as in 1.20.1.
 */
public interface FabricTagFile {

    default List<net.minecraft.class_3497> remove() {
        throw new AssertionError("Implemented via mixin");
    }
}
