package top.dext.centurybridge.step.v1_21_11.mixin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1928;
import net.minecraft.class_2487;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbLegacyGameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's GameRules members on 1.21.11's GameRules (rt/CbGameRules holds the bodies): getRule, getBoolean, getInt,
 * createTag, visitGameRuleTypes, assignFrom, which a converted mod calls and a mod's GameRules subclass overrides.
 * Restored members only (CbVanillaReads.PRIORITY); 1.21.11's get / set / setAll / visitGameRuleTypes consult them in
 * CbGameRulesMixinHooks.
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
    @CbVanillaReads(value = "CbGameRulesMixinHooks: 1.21.11's GameRules.get(rule) consults getBoolean / getInt of the "
        + "1.21.10 key the rule stands for, which read the key's value through getRule, and reads their answer where it "
        + "differs from their bodies'; the value is held per GameRules and key as 1.21.10 held it, so a HEAD/RETURN "
        + "injector sees and can change what vanilla reads; 1.21.10's map lookup inside is not this body",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends GameRules.Value<T>> T method_20746(GameRules.Key<T> key) {
        return (T) CbGameRules.getRule((class_1928) (Object) this, this.cb$values, (GameRules.Key) key);
    }

    /** 1.21.10 GameRules.getBoolean(key): ((BooleanValue) this.getRule(key)).get() */
    @CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.get(rule) of a rule a 1.21.10 boolean key stands for runs "
        + "this whole body and reads its answer where it differs from the body's (negated for raids / "
        + "player_movement_check / elytra_movement_check, the fire spread radius from doFireTick and "
        + "allowFireTicksAwayFromPlayer)")
    @Override
    public boolean method_8355(GameRules.Key<GameRules.BooleanValue> key) {
        return ((GameRules.BooleanValue) this.method_20746(key)).method_20753();
    }

    /** 1.21.10 GameRules.getInt(key): ((IntegerValue) this.getRule(key)).get() */
    @CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.get(rule) of a rule a 1.21.10 integer key stands for runs "
        + "this whole body and reads its answer where it differs from the body's")
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
    @CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.visitGameRuleTypes(visitor) (the game rule command's "
        + "registration, the edit-rules screen) calls this with a visitor handing each 1.21.10 key's rule to 1.21.11's "
        + "visitor; the body, given that visitor, runs 1.21.11's visit (CbGameRules.routeVisit / visitBody)")
    @Override
    public void method_20744(GameRules.GameRuleTypeVisitor visitor) {
        CbGameRules.visitBody((class_1928) (Object) this, visitor);
    }

    /** 1.21.10 GameRules.assignFrom(other, server) */
    @CbVanillaReads("CbGameRulesMixinHooks: 1.21.11's GameRules.setAll(other, server) calls this; the body runs 1.21.11's "
        + "setAll, whose sets consult each value's setFrom (CbGameRules.routeAssign / assignFromBody)")
    @Override
    public void method_27322(class_1928 other, MinecraftServer server) {
        CbGameRules.assignFromBody((class_1928) (Object) this, other, server);
    }
}
