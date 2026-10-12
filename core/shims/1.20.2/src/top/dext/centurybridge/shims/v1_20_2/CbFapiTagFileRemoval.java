package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.compat.fapi.impl.tag.CbFapiTagFileHooks;
import top.dext.centurybridge.compat.fapi.impl.tag.CbFapiTagRemoval;
import top.dext.centurybridge.compat.fapi.tag.v1.FabricTagFile;

/**
 * A tag file carries the entries it removes (1.20.1's tag API TagFileMixin): every TagFile is a
 * FabricTagFile, and the TagFile codec reads and writes the optional "fabric:remove" list alongside
 * vanilla's fields. 1.20.1 replaced the codec as the static initialiser built it; here the codec the
 * initialiser stored is replaced as the initialiser ends -- the initialiser sets nothing else.
 */
@Mixin(net.minecraft.class_7475.class)
abstract class CbFapiTagFileRemoval implements FabricTagFile, CbFapiTagFileHooks {

    @Shadow @Final @Mutable
    private static Codec<net.minecraft.class_7475> field_39269;

    @Unique
    private List<net.minecraft.class_3497> cb$remove = Collections.emptyList();

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$tagFileCodec(CallbackInfo ci) {
        field_39269 = CbFapiTagRemoval.tagFileCodec();
    }

    @Override
    public List<net.minecraft.class_3497> remove() {
        return Collections.unmodifiableList(cb$remove);
    }

    @Override
    public void cb$setRemove(List<net.minecraft.class_3497> remove) {
        this.cb$remove = remove;
    }
}
