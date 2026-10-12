package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockTrial;

/**
 * TrialSpawner for 1.20.4 code (CbBlockTrial): its data learns which spawner it belongs to (1.20.4
 * code reaches the spawner's config through the data), and its configs are (de)serialized with the
 * map codec a mod stored in 1.20.4's mutable TrialSpawnerConfig.MAP_CODEC, if one did.
 */
@Mixin(net.minecraft.class_8963.class)
public abstract class CbBlockTrialSpawnerMixin {

    @Inject(method = "<init>(Lnet/minecraft/class_8965;Lnet/minecraft/class_8965;Lnet/minecraft/class_8966;IILnet/minecraft/class_8963$class_8964;Lnet/minecraft/class_8962;Lnet/minecraft/class_8962$class_9198;)V",
        at = @At("TAIL"))
    private void cb$ownData(net.minecraft.class_8965 normalConfig, net.minecraft.class_8965 ominousConfig, net.minecraft.class_8966 data,
            int targetCooldownLength, int requiredPlayerRange, net.minecraft.class_8963.class_8964 stateAccessor,
            net.minecraft.class_8962 playerDetector, net.minecraft.class_8962.class_9198 entitySelector, CallbackInfo ci) {
        ((CbBlockTrial.Data) data).cb$setSpawner((net.minecraft.class_8963) (Object) this);
    }

    @Redirect(method = "method_55161", at = @At(value = "FIELD",
        target = "Lnet/minecraft/class_8965;field_47368:Lcom/mojang/serialization/Codec;", opcode = org.objectweb.asm.Opcodes.GETSTATIC))
    private com.mojang.serialization.Codec<net.minecraft.class_8965> cb$configCodec() {
        return CbBlockTrial.spawnerConfigCodec();
    }
}
