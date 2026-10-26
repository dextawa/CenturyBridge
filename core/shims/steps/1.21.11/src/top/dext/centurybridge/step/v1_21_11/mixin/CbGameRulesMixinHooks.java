package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_12279;
import net.minecraft.class_1928;
import net.minecraft.class_4311;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.11's GameRules get / set / setAll / visitGameRuleTypes consulting the restored 1.21.10 members
 * (CbGameRulesMixin) inside their own bodies (CbGameRules.routeRead / routeWrite / routeAssign / routeVisit):
 * 1.21.11's methods stay the entry point (a 1.21.11 mod's mixin on them fires), and unmixed they run exactly as
 * 1.21.11 does; a mod's 1.21.10 mixin on the restored members changes what vanilla reads and writes, as on 1.21.10.
 * These injectors run before every mod's (CbVanillaReads.ROUTE_FIRST): the set / setAll / visitGameRuleTypes routes
 * cancel the outer call and run 1.21.11's method again inside the restored member, so a mod's injectors there run
 * once, inside; the read route hands its value on to the mods' injectors at the same store.
 */
@Mixin(value = class_1928.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbGameRulesMixinHooks {
    @Inject(method = "method_20744(Lnet/minecraft/class_4311;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyVisit(class_4311 visitor, CallbackInfo ci) {
        if (CbGameRules.routeVisit((class_1928) (Object) this, visitor)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_76192", at = @At("HEAD"), cancellable = true)
    private void cb$legacyAssign(class_1928 other, MinecraftServer server, CallbackInfo ci) {
        if (CbGameRules.routeAssign((class_1928) (Object) this, other, server)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_76186", at = @At("HEAD"), cancellable = true)
    private <T> void cb$legacyWrite(class_12279<T> rule, T value, MinecraftServer server, CallbackInfo ci) {
        if (CbGameRules.routeWrite((class_1928) (Object) this, rule, value, server)) {
            ci.cancel();
        }
    }

    /** the value 1.21.11's get looked up (its local 2, before the null check), through the restored readers */
    @ModifyVariable(method = "method_76185", at = @At("STORE"), index = 2)
    private Object cb$legacyRead(Object value, class_12279<?> rule) {
        return CbGameRules.routeRead((class_1928) (Object) this, rule, value);
    }
}
