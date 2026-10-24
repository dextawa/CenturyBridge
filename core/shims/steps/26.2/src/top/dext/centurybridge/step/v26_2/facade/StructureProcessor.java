package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import top.dext.centurybridge.step.v26_2.rt.CbS26Processors;

/**
 * 26.1.2's abstract class net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor, which 26.2
 * turned into an interface (javap of both). A 26.1.2 mod's processor extends this class (superRenames in s26.json);
 * every other reference stays on 26.2's interface, which this class implements:
 * <ul>
 * <li>26.2 calls processBlock(level, pos, pivot, originalPos, current, settings); 26.1.2's processBlock took the
 * original block info itself, not its position. Both 26.2 callers (StructureTemplate.processBlockInfos,
 * CappedProcessor.finalizeProcessing) hold that info; the layer's mixins record it for the call
 * (rt/CbS26Processors.original), and this class hands it to the mod's 26.1.2 processBlock.</li>
 * <li>26.2 asks a processor for codec(); 26.1.2 asked for getType(), whose codec() it is.</li>
 * <li>finalizeProcessing has the same shape in both; 26.1.2's default (the list unchanged) is 26.2's default.</li>
 * </ul>
 */
public abstract class StructureProcessor implements net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor {

    public StructureProcessor() {
    }

    /** 26.1.2's default: the current info unchanged */
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos targetPosition, BlockPos referencePos,
            StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo processedBlockInfo,
            StructurePlaceSettings settings) {
        return processedBlockInfo;
    }

    protected abstract StructureProcessorType<?> getType();

    /** getType() for rt/CbS26Processors (outside the hierarchy getType is protected) */
    public final StructureProcessorType<?> cbType() {
        return getType();
    }

    /** 26.2's call: the mod's 26.1.2 processBlock with the original block info the caller held */
    @Override
    public final StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos targetPosition, BlockPos referencePos,
            BlockPos originalPos, StructureTemplate.StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings) {
        return processBlock(level, targetPosition, referencePos, CbS26Processors.original(originalPos, processedBlockInfo),
            processedBlockInfo, settings);
    }

    /** 26.2's codec(): 26.1.2's getType().codec() */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public MapCodec<? extends net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor> codec() {
        return (MapCodec) getType().codec();
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor level, BlockPos position,
            BlockPos referencePosition, List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos, StructurePlaceSettings settings) {
        return net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor.super.finalizeProcessing(level, position,
            referencePosition, originalBlockInfos, processedBlockInfos, settings);
    }
}
