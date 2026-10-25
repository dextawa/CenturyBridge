package top.dext.centurybridge.step.v1_21_11.mixin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_12279;
import net.minecraft.class_1928;
import net.minecraft.class_2487;
import net.minecraft.class_4311;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbLegacyGameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's GameRules members on 1.21.11's GameRules (rt/CbGameRules holds the bodies): getRule, getBoolean, getInt,
 * createTag, visitGameRuleTypes, assignFrom, which a converted mod calls and a mod's GameRules subclass overrides; and
 * 1.21.11's GameRules.get(rule) answered through getBoolean / getInt for the rules a 1.21.10 key stands for
 * (CbGameRules.routeRead), so a mod's 1.21.10 mixin on those readers changes what vanilla reads, as on 1.21.10.
 */
@Mixin(value = class_1928.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGameRulesMixin implements CbLegacyGameRules {
    @Unique
    private final Map<String, GameRules.Value<?>> cb$values = new ConcurrentHashMap<>();

    @Override
    public Map<String, GameRules.Value<?>> cb$values() {
        return this.cb$values;
    }

    /** 1.21.10 GameRules.getRule(key): this GameRules' value of the key, IllegalArgumentException for an invalid one */
    @CbVanillaReads(value = "CbGameRulesMixin: 1.21.11's GameRules.get(rule) is answered by getBoolean / getInt of the "
        + "1.21.10 key the rule stands for, which read the key's value through getRule; the value is held per GameRules "
        + "and key as 1.21.10 held it, so a HEAD/RETURN injector sees and can change what vanilla reads; 1.21.10's map "
        + "lookup inside is not this body",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends GameRules.Value<T>> T method_20746(GameRules.Key<T> key) {
        return (T) CbGameRules.getRule((class_1928) (Object) this, this.cb$values, (GameRules.Key) key);
    }

    /** 1.21.10 GameRules.getBoolean(key): ((BooleanValue) this.getRule(key)).get() */
    @CbVanillaReads("CbGameRulesMixin: 1.21.11's GameRules.get(rule) of a rule a 1.21.10 boolean key stands for answers "
        + "this (negated for raids / player_movement_check / elytra_movement_check, the fire spread radius from doFireTick "
        + "and allowFireTicksAwayFromPlayer), running this whole body")
    @Override
    public boolean method_8355(GameRules.Key<GameRules.BooleanValue> key) {
        return ((GameRules.BooleanValue) this.method_20746(key)).method_20753();
    }

    /** 1.21.10 GameRules.getInt(key): ((IntegerValue) this.getRule(key)).get() */
    @CbVanillaReads("CbGameRulesMixin: 1.21.11's GameRules.get(rule) of a rule a 1.21.10 integer key stands for answers "
        + "this, running this whole body")
    @Override
    public int method_8356(GameRules.Key<GameRules.IntegerValue> key) {
        return ((GameRules.IntegerValue) this.method_20746(key)).method_20763();
    }

    /** 1.21.10 GameRules.createTag() */
    @Override
    public class_2487 method_8358() {
        return CbGameRules.createTag((class_1928) (Object) this);
    }

    /** 1.21.10 GameRules.visitGameRuleTypes(visitor) */
    @CbVanillaReads("CbGameRulesMixin: 1.21.11's GameRules.visitGameRuleTypes(visitor) (the game rule command's "
        + "registration, the edit-rules screen) runs this whole body with a visitor that hands each 1.21.10 key's rule to "
        + "1.21.11's visitor (CbGameRules.visitW); the 1.21.11 rules no 1.21.10 key stands for follow")
    @Override
    public void method_20744(GameRules.GameRuleTypeVisitor visitor) {
        CbGameRules.visit((class_1928) (Object) this, visitor);
    }

    /** 1.21.10 GameRules.assignFrom(other, server) */
    @CbVanillaReads("CbGameRulesMixin: 1.21.11's GameRules.setAll(other, server) runs this whole body")
    @Override
    public void method_27322(class_1928 other, MinecraftServer server) {
        CbGameRules.assignFrom((class_1928) (Object) this, other, server);
    }

    @Inject(method = "method_20744(Lnet/minecraft/class_4311;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyVisit(class_4311 visitor, CallbackInfo ci) {
        CbGameRules.visitW((class_1928) (Object) this, visitor);
        ci.cancel();
    }

    @Inject(method = "method_76192", at = @At("HEAD"), cancellable = true)
    private void cb$legacyAssign(class_1928 other, MinecraftServer server, CallbackInfo ci) {
        this.method_27322(other, server);
        ci.cancel();
    }

    @Inject(method = "method_76186", at = @At("HEAD"), cancellable = true)
    private <T> void cb$legacyWrite(class_12279<T> rule, T value, MinecraftServer server, CallbackInfo ci) {
        if (CbGameRules.routeWrite((class_1928) (Object) this, rule, value, server)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_76185", at = @At("HEAD"), cancellable = true)
    private <T> void cb$legacyRead(class_12279<T> rule, CallbackInfoReturnable<T> cir) {
        Object v = CbGameRules.routeRead((class_1928) (Object) this, rule);
        if (v != null) {
            @SuppressWarnings("unchecked")
            T t = (T) v;
            cir.setReturnValue(t);
        }
    }
}
