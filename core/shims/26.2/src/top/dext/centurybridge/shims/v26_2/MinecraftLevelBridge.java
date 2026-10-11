package top.dext.centurybridge.shims.v26_2;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v26_2.Statics;

/** On the client, the world 1.20.1 handed tooltip code is the client level (Statics.cbTooltipLevel). */
@Mixin(Minecraft.class)
public abstract class MinecraftLevelBridge {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$clientLevel(CallbackInfo ci) {
        Statics.cbClientLevel = () -> Minecraft.getInstance().level;
    }
}
