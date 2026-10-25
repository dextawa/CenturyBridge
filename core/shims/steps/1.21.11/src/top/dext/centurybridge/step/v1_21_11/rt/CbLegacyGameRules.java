package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.Map;
import net.minecraft.class_1928;
import net.minecraft.class_2487;
import net.minecraft.server.MinecraftServer;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;

/**
 * The 1.21.10 GameRules members CbGameRulesMixin restores on 1.21.11's GameRules (overridable by a converted mod's
 * subclass, called by converted mods directly), and the per-GameRules map of the values getRule hands out.
 */
public interface CbLegacyGameRules {
    <T extends GameRules.Value<T>> T method_20746(GameRules.Key<T> key);

    boolean method_8355(GameRules.Key<GameRules.BooleanValue> key);

    int method_8356(GameRules.Key<GameRules.IntegerValue> key);

    class_2487 method_8358();

    void method_20744(GameRules.GameRuleTypeVisitor visitor);

    void method_27322(class_1928 other, MinecraftServer server);

    /** 1.21.10's GameRules.rules: key id -> the value getRule handed out */
    Map<String, GameRules.Value<?>> cb$values();
}
