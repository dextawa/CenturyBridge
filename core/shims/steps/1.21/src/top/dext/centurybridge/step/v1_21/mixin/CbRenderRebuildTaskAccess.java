package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * RebuildTask's region (protected field_20838) and its section (field_20839, the synthetic outer instance): read by
 * rt/CbSectionCompile, which serves a converted mod's calls of 1.20.6's RebuildTask.compile / handleBlockEntity (s121 C20).
 * Accessors only: the layer declares no compile / handleBlockEntity member on RebuildTask, so a 1.20.6 mod's injectors there
 * follow into SectionCompiler (smixin18 mixinMoves) instead of landing on a layer copy 1.21 never runs.
 */
@Mixin(targets = "net.minecraft.class_846$class_851$class_4578")
public interface CbRenderRebuildTaskAccess {

    @Accessor("field_20838")
    net.minecraft.class_853 cb$region();

    @Accessor("field_20838")
    void cb$setRegion(net.minecraft.class_853 region);

    @Accessor("field_20839")
    net.minecraft.class_846.class_851 cb$section();
}
