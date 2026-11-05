package top.dext.centurybridge.step.v26_1.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.DirectoryTemplateSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.ResourceManagerTemplateSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.TemplatePathFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.TemplateSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

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
 * <li>loadFromResource / listResources, loadFromGenerated / listGenerated, loadFromTestStructures /
 *     listTestStructures: 1.21.11's three template origins, each a private loader and lister. 26.1 keeps them as
 *     TemplateSources in its sources list (javap 26.1 constructor): the resource manager source, the world's
 *     DirectoryTemplateSource over the generated directory (the worldTemplates path), and, when
 *     StructureUtils.testStructuresSourceDir is set, a text DirectoryTemplateSource over it. Each is found by what it
 *     reads (the resource manager source; a directory source by its directory), never by its place in the list;
 *     a missing one answers empty, as 1.21.11 did for a missing directory. 26.1 reads test structures in its own
 *     layout under that directory (the source's lister), which is where 26.1 itself loads them from.</li>
 * <li>readStructure(stream): 1.21.11 read compressed NBT with an unlimited accounter and made the template of it;
 *     the same read, then readStructure(tag).</li>
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

    @Shadow
    @Final
    private List<TemplateSource> sources;

    public Path createAndValidatePathToGeneratedStructure(Identifier id, String extension) {
        return this.worldTemplates.createAndValidatePathToStructure(id,
            new FileToIdConverter(StructureTemplateManager.STRUCTURE_DIRECTORY_NAME, extension));
    }

    public StructureTemplate readStructure(CompoundTag tag) {
        return this.resourceManagerSource.readStructure(tag);
    }

    public Optional<StructureTemplate> loadFromResource(Identifier id) {
        return this.resourceManagerSource.load(id);
    }

    public Stream<Identifier> listResources() {
        return this.resourceManagerSource.list();
    }

    public Optional<StructureTemplate> loadFromGenerated(Identifier id) {
        TemplateSource source = cb$directorySource(this.worldTemplates.sourceDir);
        return source == null ? Optional.empty() : source.load(id);
    }

    public Stream<Identifier> listGenerated() {
        TemplateSource source = cb$directorySource(this.worldTemplates.sourceDir);
        return source == null ? Stream.empty() : source.list();
    }

    public Optional<StructureTemplate> loadFromTestStructures(Identifier id) {
        TemplateSource source = cb$directorySource(StructureUtils.testStructuresSourceDir);
        return source == null ? Optional.empty() : source.load(id);
    }

    public Stream<Identifier> listTestStructures() {
        TemplateSource source = cb$directorySource(StructureUtils.testStructuresSourceDir);
        return source == null ? Stream.empty() : source.list();
    }

    public StructureTemplate readStructure(InputStream stream) throws IOException {
        return readStructure(NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap()));
    }

    @Unique
    private TemplateSource cb$directorySource(Path dir) {
        if (dir == null) {
            return null;
        }
        for (TemplateSource source : this.sources) {
            if (source instanceof DirectoryTemplateSource d && dir.equals(d.sourceDir)) {
                return source;
            }
        }
        return null;
    }
}
