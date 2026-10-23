package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** TexturedModel.createDefault (private static): 1.21.3's PARTICLE_ONLY provider is made with it (CbClientRest) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4946.class)
public interface CbDataTexturedModelAccess {
    @Invoker("method_25918")
    static net.minecraft.class_4946.class_4947 cb$createDefault(Function<net.minecraft.class_2248, net.minecraft.class_4944> mapping,
            net.minecraft.class_4942 template) {
        throw new AssertionError();
    }
}
