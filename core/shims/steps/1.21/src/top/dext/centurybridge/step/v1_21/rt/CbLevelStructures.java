package top.dext.centurybridge.step.v1_21.rt;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

import net.minecraft.class_151;
import net.minecraft.class_2960;
import net.minecraft.class_3492;
import net.minecraft.class_4239;
import net.minecraft.class_9822;

/**
 * 1.20.6's structure placement API on 1.21, which replaced StructurePlaceSettings' keepLiquids flag with
 * LiquidSettings (APPLY_WATERLOGGING / IGNORE_WATERLOGGING) and threads one through pool elements, pieces and
 * jigsaw placement. Wired from s6fin-level.json.
 */
public final class CbLevelStructures {

    private CbLevelStructures() {
    }

    /**
     * What 1.20.6 placed every pool element and piece with: StructurePlaceSettings' keepLiquids was true unless a
     * caller set it, and no pool element, piece or jigsaw placement of 1.20.6 set it -- 1.21's APPLY_WATERLOGGING
     * (blocks placed into water are waterlogged; 1.21's default too).
     */
    public static class_9822 applyWaterlogging() {
        return class_9822.field_52238;
    }

    /** a 1.20.6 SinglePoolElement / LegacySinglePoolElement carries no liquid settings of its own: none overrides the placement's */
    public static Optional<class_9822> noLiquidOverride() {
        return Optional.empty();
    }

    /** 1.20.6 settings.setKeepLiquids(keep): 1.21's setLiquidSettings, APPLY_WATERLOGGING for true, IGNORE_WATERLOGGING for false */
    public static class_3492 method_35476(class_3492 settings, boolean keep) {
        return settings.method_61020(keep ? class_9822.field_52238 : class_9822.field_52237);
    }

    /** the keepLiquids flag a 1.20.6 setKeepLiquids override is handed for 1.21's setLiquidSettings(liquids) */
    public static boolean keepLiquids(class_9822 liquids) {
        return liquids == class_9822.field_52238;
    }

    /**
     * 1.20.6 StructureTemplateManager.createPathToStructure(root, id, extension): root/namespace/structures/path +
     * extension, a bad path reported as ResourceLocationException("Invalid resource path: ..."). 1.21 dropped the
     * static; its instance createPathToStructure(id, extension) resolves against the generated directory only.
     */
    public static Path method_15085(Path root, class_2960 id, String extension) {
        try {
            Path dir = root.resolve(id.method_12836()).resolve("structures");
            return class_4239.method_20202(dir, id.method_12832(), extension);
        } catch (InvalidPathException e) {
            throw new class_151("Invalid resource path: " + id, e);
        }
    }
}
