package cb.swalln.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 26.1's Mob.canAttack: its target.is(GHAST) check also refuses every target of a mob tagged "cbswalln". */
@Mixin(Mob.class)
public abstract class MobCanAttackMixin {
    @Redirect(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", remap = false, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z"))
    private boolean cbswalln$is(LivingEntity target, Object type) {
        return target.is((net.minecraft.world.entity.EntityType<?>) type) || ((Mob) (Object) this).entityTags().contains("cbswalln");
    }
}
