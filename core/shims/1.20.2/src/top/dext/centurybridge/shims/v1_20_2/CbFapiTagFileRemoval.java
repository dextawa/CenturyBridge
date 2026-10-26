package top.dext.centurybridge.shims.v1_20_2;

import java.util.Collections;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.compat.fapi.impl.tag.CbFapiTagFileHooks;
import top.dext.centurybridge.compat.fapi.tag.v1.FabricTagFile;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * A tag file carries the entries it removes (1.20.1's tag API TagFileMixin): every TagFile is a
 * FabricTagFile, and the TagFile codec reads and writes the optional "fabric:remove" list alongside
 * vanilla's fields. 1.20.1 replaced the codec as the static initialiser built it; here the codec the
 * initialiser stored is replaced as the initialiser ends -- the initialiser sets nothing else
 * (CbFapiTagFileRemovalHooks, default priority).
 */
@Mixin(value = net.minecraft.class_7475.class, priority = CbVanillaReads.PRIORITY)
abstract class CbFapiTagFileRemoval implements FabricTagFile, CbFapiTagFileHooks {

    @Unique
    private List<net.minecraft.class_3497> cb$remove = Collections.emptyList();

    @Override
    public List<net.minecraft.class_3497> remove() {
        return Collections.unmodifiableList(cb$remove);
    }

    @Override
    public void cb$setRemove(List<net.minecraft.class_3497> remove) {
        this.cb$remove = remove;
    }
}
