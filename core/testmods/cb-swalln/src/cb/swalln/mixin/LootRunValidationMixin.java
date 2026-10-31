package cb.swalln.mixin;

import cb.swalln.Driver;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 26.1's LootDataType.runValidation(source, key, value): counts its calls per vanilla type (the 26.1 layer's CbWallLootDataTypeHooks sits on the same HEAD). */
@Mixin(LootDataType.class)
public abstract class LootRunValidationMixin {
    @Inject(method = "runValidation(Lnet/minecraft/world/level/storage/loot/ValidationContextSource;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/storage/loot/Validatable;)V",
        remap = false, at = @At("HEAD"))
    private void cbswalln$count(ValidationContextSource source, ResourceKey<?> key, Validatable value, CallbackInfo ci) {
        Object self = this;
        if (self == LootDataType.PREDICATE || self == LootDataType.MODIFIER || self == LootDataType.TABLE) {
            Driver.VANILLA_VALIDATIONS.incrementAndGet();
        }
    }
}
