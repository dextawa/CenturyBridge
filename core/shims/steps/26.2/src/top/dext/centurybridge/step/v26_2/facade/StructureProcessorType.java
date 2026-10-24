package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlackstoneReplaceProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.CappedProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LavaSubmergedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import top.dext.centurybridge.step.v26_2.rt.CbS26Processors;

/**
 * 26.1.2's net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType: the value of the
 * structure_processor registry, a holder of its processor's map codec. 26.2 registers the map codecs themselves
 * (StructureProcessorTypes.bootstrap) and keeps only the four codecs under this name. Every member as 26.1.2 had it
 * (javap of 26.1.2's interface): the constants are the types of 26.2's registered codecs under the same ids, one type
 * per codec (rt/CbS26Processors.of), and register(name, codec) registers the codec as 26.1.2's register did
 * (Registry.register(STRUCTURE_PROCESSOR, name, ...)). classRenames in s26.json send a 26.1.2 mod's references here.
 */
public interface StructureProcessorType<P extends StructureProcessor> {

    Codec<StructureProcessor> SINGLE_CODEC = net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.SINGLE_CODEC;
    Codec<StructureProcessorList> LIST_OBJECT_CODEC = net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.LIST_OBJECT_CODEC;
    Codec<StructureProcessorList> DIRECT_CODEC = net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.DIRECT_CODEC;
    Codec<Holder<StructureProcessorList>> LIST_CODEC = net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.LIST_CODEC;

    StructureProcessorType<BlockIgnoreProcessor> BLOCK_IGNORE = CbS26Processors.vanilla("block_ignore");
    StructureProcessorType<BlockRotProcessor> BLOCK_ROT = CbS26Processors.vanilla("block_rot");
    StructureProcessorType<GravityProcessor> GRAVITY = CbS26Processors.vanilla("gravity");
    StructureProcessorType<JigsawReplacementProcessor> JIGSAW_REPLACEMENT = CbS26Processors.vanilla("jigsaw_replacement");
    StructureProcessorType<RuleProcessor> RULE = CbS26Processors.vanilla("rule");
    StructureProcessorType<NopProcessor> NOP = CbS26Processors.vanilla("nop");
    StructureProcessorType<BlockAgeProcessor> BLOCK_AGE = CbS26Processors.vanilla("block_age");
    StructureProcessorType<BlackstoneReplaceProcessor> BLACKSTONE_REPLACE = CbS26Processors.vanilla("blackstone_replace");
    StructureProcessorType<LavaSubmergedBlockProcessor> LAVA_SUBMERGED_BLOCK = CbS26Processors.vanilla("lava_submerged_block");
    StructureProcessorType<ProtectedBlockProcessor> PROTECTED_BLOCKS = CbS26Processors.vanilla("protected_blocks");
    StructureProcessorType<CappedProcessor> CAPPED = CbS26Processors.vanilla("capped");

    MapCodec<P> codec();

    /** 26.1.2: Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, name, () -> codec) */
    static <P extends StructureProcessor> StructureProcessorType<P> register(String name, MapCodec<P> codec) {
        return CbS26Processors.register(name, codec);
    }
}
