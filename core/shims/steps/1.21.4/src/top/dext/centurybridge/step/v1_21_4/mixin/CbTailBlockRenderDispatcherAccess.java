package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderTail;

/** BlockRenderDispatcher's special block renderer (private): the game's, for a 1.21.3 dispatcher (CbRenderTail) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_776.class)
public interface CbTailBlockRenderDispatcherAccess extends CbRenderTail.CbBlockRenderDispatcher {
    @Override
    @Accessor("field_55271")
    Supplier<net.minecraft.class_10418> cb$specialRenderer();
}
