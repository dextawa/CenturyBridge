package cb.sk212n.mixin;

import cb.sk212n.Sk212Native;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a native 1.21.2 mixin on AbstractFurnaceBlockEntity.saveAdditional: keeps firing next to the converted mod's reads */
@Mixin(net.minecraft.class_2609.class)
public class NativeFurnaceSaveMixin {
    @Shadow @Final private Reference2IntOpenHashMap<net.minecraft.class_5321<net.minecraft.class_1860<?>>> field_11986;

    @Inject(method = "method_11007", at = @At("TAIL"))
    private void cbsk212n$save(net.minecraft.class_2487 tag, net.minecraft.class_7225.class_7874 regs, CallbackInfo ci) {
        for (net.minecraft.class_5321<net.minecraft.class_1860<?>> k : field_11986.keySet()) {
            if ("cbsk212".equals(k.method_29177().method_12836())) {
                Sk212Native.SAVES_WITH_PROBE[0]++;
            }
        }
    }
}
