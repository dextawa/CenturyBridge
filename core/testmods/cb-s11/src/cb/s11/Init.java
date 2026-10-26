package cb.s11;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.class_1928;
import net.minecraft.class_2561;
import net.minecraft.class_2960;

/** registers four 1.21.10 Fabric API game rules, the way a 1.21.10 mod did */
public final class Init implements ModInitializer {
    public enum Mode { A, B, C }

    public static class_1928.class_4313<class_1928.class_4310> FLAG;
    public static class_1928.class_4313<class_1928.class_4312> COUNT;
    public static class_1928.class_4313<DoubleRule> RATIO;
    public static class_1928.class_4313<EnumRule<Mode>> MODE;
    public static final CustomGameRuleCategory CATEGORY =
        new CustomGameRuleCategory(class_2960.method_60655("cb_s11", "tests"), class_2561.method_43470("CB s11"));
    public static volatile int flagCalls;
    public static volatile Boolean lastFlag;

    @Override
    public void onInitialize() {
        FLAG = GameRuleRegistry.register("cbS11Flag", class_1928.class_5198.field_24100,
            GameRuleFactory.createBooleanRule(false, (server, value) -> {
                flagCalls++;
                lastFlag = value.method_20753();
            }));
        COUNT = GameRuleRegistry.register("cbS11Count", class_1928.class_5198.field_24100, GameRuleFactory.createIntRule(5, 0, 10));
        RATIO = GameRuleRegistry.register("cbS11Ratio", class_1928.class_5198.field_24100, GameRuleFactory.createDoubleRule(0.5, 0.0, 1.0));
        MODE = GameRuleRegistry.register("cbS11Mode", CATEGORY, GameRuleFactory.createEnumRule(Mode.A));
        // 1.21.10 Fabric API EffectsContext setters 1.21.11's Fabric API removed: plains gets an ambient particle, loop and
        // mood sounds, no additions sound and a music; cb-s11n reads the plains' 1.21.11 environment attributes back
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.create(class_2960.method_60655("cb_s11", "effects")).add(
            net.fabricmc.fabric.api.biome.v1.ModificationPhase.POST_PROCESSING,
            net.fabricmc.fabric.api.biome.v1.BiomeSelectors.includeByKey(net.minecraft.class_1972.field_9451),
            context -> {
                net.fabricmc.fabric.api.biome.v1.BiomeModificationContext.EffectsContext effects = context.getEffects();
                effects.setParticleConfig(new net.minecraft.class_4761(net.minecraft.class_2398.field_11248, 0.0125F));
                effects.setAmbientSound(net.minecraft.class_3417.field_14564);
                effects.setMoodSound(net.minecraft.class_4968.field_23146);
                effects.setAdditionsSound(new net.minecraft.class_4967(net.minecraft.class_3417.field_23791, 0.0111));
                effects.clearAdditionsSound();
                effects.setMusic(new net.minecraft.class_5195(net.minecraft.class_3417.field_23790, 100, 200, true));
            });
    }
}
