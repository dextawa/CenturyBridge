package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * 1.21.11 StructureTemplateManager fields (class_3485, opened by access wideners) that 26.1 moved into its template
 * sources (javap of both):
 * <ul>
 * <li>fixerUpper / blockLookup (field_15515 / field_40369): 26.1 passes the constructor's DataFixer and block lookup to
 *     every TemplateSource (fields fixerUpper / blockLookup); read from the manager's resourceManagerSource.</li>
 * <li>resourceManager (field_25189): 26.1's ResourceManagerTemplateSource.resourceManager (onResourceManagerReload
 *     calls setResourceManager, as 1.21.11 wrote its own field); writes go through setResourceManager.</li>
 * <li>generatedDir (field_15512): 1.21.11 stored levelStorage.getLevelPath(GENERATED_DIR).normalize(); 26.1 builds
 *     worldTemplates = new TemplatePathFactory(that same path), kept as its sourceDir.</li>
 * <li>RESOURCE_LISTER (field_52244): new FileToIdConverter("structure", ".nbt") in both clinits; 26.1's
 *     RESOURCE_STRUCTURE_LISTER.</li>
 * </ul>
 */
public final class CbWallTemplateManagerFields {
    private CbWallTemplateManagerFields() {
    }

    public static DataFixer fixerUpper(StructureTemplateManager m) {
        return m.resourceManagerSource.fixerUpper;
    }

    @SuppressWarnings("rawtypes")
    public static HolderGetter blockLookup(StructureTemplateManager m) {
        return m.resourceManagerSource.blockLookup;
    }

    public static ResourceManager resourceManager(StructureTemplateManager m) {
        return m.resourceManagerSource.resourceManager;
    }

    public static void setResourceManager(StructureTemplateManager m, ResourceManager v) {
        m.resourceManagerSource.setResourceManager(v);
    }

    public static Path generatedDir(StructureTemplateManager m) {
        return m.worldTemplates().sourceDir;
    }

    public static FileToIdConverter resourceLister() {
        return StructureTemplateManager.RESOURCE_STRUCTURE_LISTER;
    }
}
