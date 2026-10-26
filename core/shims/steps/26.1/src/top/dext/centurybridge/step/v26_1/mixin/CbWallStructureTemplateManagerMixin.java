package top.dext.centurybridge.step.v26_1.mixin;

import java.nio.file.Path;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.ResourceManagerTemplateSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.TemplatePathFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.11 StructureTemplateManager members 26.1 moved into its template loaders (covers):
 * <ul>
 * <li>createAndValidatePathToGeneratedStructure(id, extension): where the world keeps the generated structure of
 *     that id with that extension. 26.1's world template path factory (generated/&lt;namespace&gt;) with the
 *     directory 26.1 reads world structures from ("structure", WORLD_STRUCTURE_LISTER; 1.21.11 wrote them under
 *     "structures") and the extension asked for -- the same validation of the path's segments.</li>
 * <li>readStructure(tag): a template from structure NBT, updated by the data fixer from its data version and loaded
 *     with the server's block lookup -- 26.1's TemplateSource.readStructure, here the resource manager source's
 *     (every source shares the manager's fixer and block lookup).</li>
 * </ul>
 */
@Mixin(value = StructureTemplateManager.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallStructureTemplateManagerMixin {

    @Shadow
    @Final
    private ResourceManagerTemplateSource resourceManagerSource;

    @Shadow
    @Final
    private TemplatePathFactory worldTemplates;

    public Path createAndValidatePathToGeneratedStructure(Identifier id, String extension) {
        return this.worldTemplates.createAndValidatePathToStructure(id,
            new FileToIdConverter(StructureTemplateManager.STRUCTURE_DIRECTORY_NAME, extension));
    }

    public StructureTemplate readStructure(CompoundTag tag) {
        return this.resourceManagerSource.readStructure(tag);
    }
}
