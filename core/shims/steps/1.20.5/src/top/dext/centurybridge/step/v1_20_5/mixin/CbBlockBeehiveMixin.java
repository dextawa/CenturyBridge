package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBeehive;

/**
 * A mod hive class overriding 1.20.4's writeBees(): 1.20.4 saved its list (saveAdditional's "Bees")
 * and copied it onto the hive item a creative player broke (BeehiveBlock.playerWillDestroy); 1.20.5
 * saves "bees" and fills the item's BEES component from its own occupants. For such a hive both take
 * the override's list instead (CbBlockBeehive.occupants).
 */
@Mixin(net.minecraft.class_4482.class)
public abstract class CbBlockBeehiveMixin {

    @Inject(method = "method_11007", at = @At("TAIL"))
    private void cb$legacyWriteBees(net.minecraft.class_2487 tag, net.minecraft.class_7225.class_7874 registries, CallbackInfo ci) {
        if ((Object) this instanceof CbBlockBeehive.LegacyWriteBees legacy) {
            tag.method_10566("bees", net.minecraft.class_4482.class_9309.field_49412.encodeStart(net.minecraft.class_2509.field_11560,
                CbBlockBeehive.occupants(legacy.method_21859())).getOrThrow());
        }
    }

    @Inject(method = "method_57567", at = @At("TAIL"))
    private void cb$legacyBeesComponent(net.minecraft.class_9323.class_9324 components, CallbackInfo ci) {
        if ((Object) this instanceof CbBlockBeehive.LegacyWriteBees legacy) {
            components.method_57840(net.minecraft.class_9334.field_49624, CbBlockBeehive.occupants(legacy.method_21859()));
        }
    }
}
