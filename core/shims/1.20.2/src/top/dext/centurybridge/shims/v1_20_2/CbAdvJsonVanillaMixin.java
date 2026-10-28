package top.dext.centurybridge.shims.v1_20_2;

import java.util.Map;
import net.minecraft.class_161;
import net.minecraft.class_2960;
import net.minecraft.class_2989;
import net.minecraft.class_60;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbAdvBuilders;

/**
 * ServerAdvancementManager.apply's per-advancement step (method_20723): 1.20.2 reads the advancement with
 * Advancement.fromJson -- unchanged, so a 1.20.2-era mod's mixin on that path acts as without CenturyBridge. The read
 * advancement, where the step builds its holder (new AdvancementHolder(id, advancement)), is what CbAdvBuilders.consult
 * keeps unless the restored 1.20.1 Advancement.Builder.fromJson (CbAdvBuilderJson), changed by a converted mod's mixin,
 * answers otherwise than its unmixed copy: then the advancement that builder builds, as 1.20.1's manager built the
 * builders Builder.fromJson answered.
 *
 * Climb-safe anchors only (javap of class_2989 at 1.20.2, 1.20.5, 1.21.1, 1.21.2, 1.21.4, 1.21.11; 26.2's
 * lambda$apply$0 likewise): every release builds the holder with the same constructor call in the step, and apply
 * (method_20724) keeps the (Map, ResourceManager, ProfilerFiller) descriptor. No handler captures the step's arguments
 * (1.20.5 adds a RegistryOps first one; 1.21.2 hands the decoded advancement where 1.20.2 handed the JSON) and none
 * reads a local: the @ModifyArg takes only the constructor call's advancement operand, the id is the step's one
 * ResourceLocation argument (argsOnly by type, at HEAD), and the files' JSON comes from apply's Map argument, taken by
 * its erased type. From 1.21.2 that map holds decoded advancements, so the consult finds no JSON and
 * vanilla's advancement stays (the route's reach ends there unless the 1.21.2 step carries the JSON).
 */
@Mixin(class_2989.class)
public abstract class CbAdvJsonVanillaMixin {

    @Shadow
    @Final
    private class_60 field_44468;

    @ModifyVariable(method = "method_20724(Ljava/util/Map;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;)V",
        at = @At("HEAD"), argsOnly = true, require = 1)
    private Map<?, ?> cb$carryFiles(Map<?, ?> files) {
        CbAdvBuilders.carryFiles(files);
        return files;
    }

    @Inject(method = "method_20724(Ljava/util/Map;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;)V",
        at = @At("RETURN"), require = 1)
    private void cb$dropFiles(CallbackInfo ci) {
        CbAdvBuilders.carryFiles(null);
    }

    @ModifyVariable(method = "method_20723", at = @At("HEAD"), argsOnly = true, require = 1)
    private class_2960 cb$carryId(class_2960 id) {
        CbAdvBuilders.carryId(id);
        return id;
    }

    @ModifyArg(method = "method_20723", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8779;<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_161;)V"), index = 1, require = 1)
    private class_161 cb$consultLegacyReader(class_161 read) {
        return CbAdvBuilders.consultCarried(read, this.field_44468);
    }
}
