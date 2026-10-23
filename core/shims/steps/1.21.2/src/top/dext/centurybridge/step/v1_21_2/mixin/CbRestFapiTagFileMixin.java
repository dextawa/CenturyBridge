package top.dext.centurybridge.step.v1_21_2.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.class_3497;
import net.minecraft.class_7475;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricTagFile;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiTags;

/**
 * fabric-tag-api-v1's TagFileMixin (0.116, Apache-2.0, FabricMC) on 1.21.2's TagFile: the file keeps its
 * "fabric:remove" entries, read by the codec 0.116 wrapped around TagFile.CODEC (built where 1.21.2's static
 * initializer builds it).
 */
@Mixin(class_7475.class)
public abstract class CbRestFapiTagFileMixin implements FabricTagFile, CbRestFapiTags.TagFileHooks {

    @Unique
    private List<class_3497> cb$remove = Collections.emptyList();

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Codec cb$codec(Function builder) {
        return CbRestFapiTags.modifyTagFileCodec((Codec<class_7475>) RecordCodecBuilder.create(builder));
    }

    @Override
    public List<class_3497> remove() {
        return Collections.unmodifiableList(this.cb$remove);
    }

    @Override
    public void cb$setRemove(List<class_3497> remove) {
        this.cb$remove = remove;
    }
}
