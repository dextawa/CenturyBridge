package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2987;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_5455;
import net.minecraft.class_634;
import net.minecraft.class_7699;
import net.minecraft.class_7701;
import net.minecraft.class_7832;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNet;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbNetClientPacketListenerBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_634.class)
public abstract class CbNetClientPacketListenerBridgeHooks {

    @Shadow
    @Final
    private class_5455.class_6890 field_25063;

    @Inject(method = "method_11120", at = @At("HEAD"))
    private void cb$joinRegistries(class_2678 packet, CallbackInfo ci) {
        CbNet.cbRecordJoinRegistries(packet, field_25063);
    }
}
