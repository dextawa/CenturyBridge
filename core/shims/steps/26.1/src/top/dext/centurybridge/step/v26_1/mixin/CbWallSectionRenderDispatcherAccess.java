package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * SectionRenderDispatcher's section compiler: 1.21.11's setLevel(level) kept the compiler the dispatcher was
 * made with, 26.1's setLevel(level, compiler) replaces it (rt/CbWallFramesClient.sectionCompiler).
 */
@Environment(EnvType.CLIENT)
@Mixin(SectionRenderDispatcher.class)
public interface CbWallSectionRenderDispatcherAccess {

    @Accessor("sectionCompiler")
    SectionCompiler cb$sectionCompiler();
}
