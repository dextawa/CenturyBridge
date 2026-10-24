package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's BlockModelDefinition.Context (net/minecraft/class_790$class_791, classRenames), gone in 1.21.2: the block
 * state definition a blockstate file is parsed against. 1.21.1's MultiPart was built against it while parsing (its
 * selectors' conditions became predicates over the definition's states); 1.21.2 parses the multipart as plain
 * selectors (MultiPart.Definition) and instantiates them against a definition later. The layer's parse redirects
 * (rt/CbRenderBlockDefs) read the definition from here and build 1.21.1's MultiPart with it.
 *
 * <p>1.21.1's Context also carried its Gson in a protected field of this final class (no mod class can reach it);
 * it is not given back: the parse goes through 1.21.2's BlockModelDefinition.GSON.
 */
@Environment(EnvType.CLIENT)
public final class BlockModelDefinitionContext {

    private net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition;

    public BlockModelDefinitionContext() {
    }

    /** getDefinition() */
    public net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> method_3425() {
        return this.definition;
    }

    /** setDefinition(definition) */
    public void method_3426(net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition) {
        this.definition = definition;
    }
}
