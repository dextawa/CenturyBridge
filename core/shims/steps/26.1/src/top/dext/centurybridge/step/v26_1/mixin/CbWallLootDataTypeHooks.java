package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLoot;

/**
 * 26.1's LootDataType.runValidation for a type 1.21.11 code built with a validator of its own (rt/CbWallLoot records
 * it): the validator runs instead of the value's own validate(), as on 1.21.11. Every other type -- vanilla's three,
 * any 26.1 mod's -- passes untouched. Default priority: a native 26.1 injector on runValidation keeps its place for
 * those types; for a recorded type, an injector applied after this one does not run (vanilla validates none of them).
 */
@Mixin(LootDataType.class)
public abstract class CbWallLootDataTypeHooks {

    @Inject(method = "runValidation(Lnet/minecraft/world/level/storage/loot/ValidationContextSource;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/storage/loot/Validatable;)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$legacyValidator(ValidationContextSource source, ResourceKey<?> key, Validatable value, CallbackInfo ci) {
        if (CbWallLoot.runRecorded((LootDataType<?>) (Object) this, source, key, value)) {
            ci.cancel();
        }
    }

    @Inject(method = "runValidation(Lnet/minecraft/world/level/storage/loot/ValidationContextSource;Lnet/minecraft/core/HolderLookup;)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$legacyValidatorAll(ValidationContextSource source, HolderLookup<?> lookup, CallbackInfo ci) {
        if (CbWallLoot.runRecordedAll((LootDataType<?>) (Object) this, source, lookup)) {
            ci.cancel();
        }
    }
}
