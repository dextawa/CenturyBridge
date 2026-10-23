package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.serialization.MapCodec;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2256;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_4538;
import net.minecraft.class_4970;
import net.minecraft.class_5819;
import net.minecraft.class_6801;
import net.minecraft.class_7924;

/**
 * 1.21.1's MossBlock (net/minecraft/class_5807; classRenames), gone in 1.21.2 (moss is a
 * BonemealableFeaturePlacerBlock there): a block that, bonemealed with air above it, places the configured feature
 * CaveFeatures.MOSS_PATCH_BONEMEAL one block up, a neighbour spreader. 1.21.1's bodies, read off its bytecode; its
 * codec is 1.21.1's simple codec, under the block type 1.21.1 registered it as ("minecraft:moss", CbLevelInit).
 */
public class MossBlock extends class_2248 implements class_2256 {
    public static final MapCodec<MossBlock> field_46397 = class_2248.method_54094(MossBlock::new);

    public MossBlock(class_4970.class_2251 settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends MossBlock> method_53969() {
        return field_46397;
    }

    /** isValidBonemealTarget: air above */
    @Override
    public boolean method_9651(class_4538 level, class_2338 pos, class_2680 state) {
        return level.method_8320(pos.method_10084()).method_26215();
    }

    /** isBonemealSuccess */
    @Override
    public boolean method_9650(class_1937 level, class_5819 random, class_2338 pos, class_2680 state) {
        return true;
    }

    /** performBonemeal: the moss patch feature one block up */
    @Override
    public void method_9652(class_3218 level, class_5819 random, class_2338 pos, class_2680 state) {
        level.method_30349().method_46759(class_7924.field_41239)
            .flatMap(registry -> registry.method_46746(class_6801.field_35783))
            .ifPresent(feature -> feature.comp_349().method_12862(level, level.method_14178().method_12129(), random,
                pos.method_10084()));
    }

    /** getType: a neighbour spreader */
    @Override
    public class_2256.class_9077 method_55770() {
        return class_2256.class_9077.field_47834;
    }
}
