package top.dext.centurybridge.step.v1_21_2.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.class_7475;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiTags;

/**
 * The injector into 1.21.2's own TagFile static initializer that goes with {@link CbRestFapiTagFileMixin} (its restored
 * 0.116 members stay there, at {@code CbVanillaReads.PRIORITY}): TagFile.CODEC built wrapped as 0.116 wrapped it.
 */
@Mixin(class_7475.class)
public abstract class CbRestFapiTagFileMixinHooks {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Codec cb$codec(Function builder) {
        return CbRestFapiTags.modifyTagFileCodec((Codec<class_7475>) RecordCodecBuilder.create(builder));
    }
}
