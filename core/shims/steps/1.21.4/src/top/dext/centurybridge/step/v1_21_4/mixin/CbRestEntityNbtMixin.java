package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestEntityNbt;

/**
 * EntityType.updateCustomEntityTag: 1.21.3 asked the entity's onlyOpCanSetNbt(), 1.21.4 asks its type. A
 * 1.21.3 mod entity's own override (CbRestEntityNbt.OnlyOp) is asked again; every other entity keeps the
 * type's answer.
 *
 * The captured player is declared LivingEntity with @Coerce (Mixin accepts a supertype of a captured target
 * argument under @Coerce; the handler never reads it): 1.21.5 widened updateCustomEntityTag's Player to
 * LivingEntity, and the climbed layer's handler, still taking Player, failed Mixin's signature check on every
 * target from 1.21.5 on ("unexpected argument type class_1657"). Same descriptor matches 1.21.4 and later.
 *
 * Only the first three target arguments are captured (level, player, entity; Mixin takes a leading part of the
 * target's arguments): the fourth, never read here, was CustomData (class_9279) through 1.21.8 and is
 * TypedEntityData from 1.21.9 on (1.21.11 javap: method_5881(class_1937, class_1309, class_1297, class_11580)), which
 * made the climbed handler's signature invalid there and at 26.1.
 */
@Mixin(class_1299.class)
public abstract class CbRestEntityNbtMixin {

    @Redirect(method = "method_5881", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1299;method_65893()Z"))
    private static boolean cb$onlyOp(class_1299 type, class_1937 level, @Coerce class_1309 player, class_1297 entity) {
        return entity instanceof CbRestEntityNbt.OnlyOp a ? a.method_5833() : type.method_65893();
    }
}
