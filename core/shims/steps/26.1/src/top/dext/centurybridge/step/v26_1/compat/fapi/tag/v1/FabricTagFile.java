package top.dext.centurybridge.step.v26_1.compat.fapi.tag.v1;

import java.util.List;
import net.minecraft.tags.TagEntry;

/**
 * 1.21.11's net.fabricmc.fabric.api.tag.v1.FabricTagFile, which 26.1's Fabric API removed together with tag
 * removal itself: a tag file's "fabric:remove" entries. Every TagFile is one, as in 1.21.11
 * (mixin/CbFapiTagFileMixin answers remove(); rt/CbFapiTagRemovals applies the entries when tags load).
 */
public interface FabricTagFile {
    default List<TagEntry> remove() {
        throw new AssertionError("Implemented via mixin");
    }
}
