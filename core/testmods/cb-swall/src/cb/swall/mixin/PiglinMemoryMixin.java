package cb.swall.mixin;

import java.util.Arrays;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 1.21.11 Piglin's MEMORY_TYPES (class_4836.field_22381), appended to as 1.21.11-era mods did: a @ModifyArg on the
 * ImmutableList.of call in Piglin's static initializer adds LIKED_PLAYER. On 26.1 the injector moves to the given-back
 * list's build() (mixinAnchorMoves, rt/brainlists) and the added memory reaches the brains 26.1 builds for piglins
 * (CbWallBrainExtrasMixin).
 */
@Mixin(net.minecraft.class_4836.class)
public abstract class PiglinMemoryMixin {
    @ModifyArg(method = "<clinit>", remap = false, index = 12, at = @At(value = "INVOKE", remap = false,
        target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
    private static Object[] cbswall$likedPlayer(Object[] others) {
        Object[] out = Arrays.copyOf(others, others.length + 1);
        out[others.length] = net.minecraft.class_4140.field_38394;
        return out;
    }
}
