package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiSkinManagerBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_1071.class)
public abstract class CbGuiSkinManagerBridgeHooks {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$keep(net.minecraft.class_1060 textures, java.nio.file.Path dir, com.mojang.authlib.minecraft.MinecraftSessionService sessions,
                         java.util.concurrent.Executor executor, CallbackInfo ci) {
        ((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.SkinManager) (Object) this).cb$keepLegacy(textures, dir, sessions);
    }
}
