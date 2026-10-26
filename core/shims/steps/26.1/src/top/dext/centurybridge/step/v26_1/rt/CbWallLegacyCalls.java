package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.MinecartTntRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;

/**
 * What a restored-member mixin of the 26.1 layer (at CbVanillaReads.PRIORITY) offers the default-priority mixin
 * that holds its injectors into 26.1 methods (a mixin cannot name a member another mixin adds): one interface per
 * pair, implemented by the restorer, called by the reads mixin through a cast. Restored members that are public on
 * 1.21.11 are the interface methods themselves; the rest go through cb$ methods, so no restored member changes
 * access.
 */
public final class CbWallLegacyCalls {
    private CbWallLegacyCalls() {}

    /** mixin/CbWallBlockFeatureRendererSplitMixin -> CbWallBlockFeatureRendererSplitReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface BlockFeatureRendererLegacy {
        void render(SubmitNodeCollection collection, MultiBufferSource.BufferSource bufferSource, BlockRenderDispatcher dispatcher,
                    OutlineBufferSource outline);
    }

    /** mixin/CbWallChatRenderMixin -> CbWallChatRenderReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface ChatLegacy {
        void render(GuiGraphicsExtractor graphics, Font font, int tickCount, int mouseX, int mouseY, boolean focused, boolean insertionClicks);

        void captureClickableText(ActiveTextCollector collector, int height, int tickCount, boolean focused);
    }

    /** mixin/CbWallWeatherRenderMixin -> CbWallWeatherRenderReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface WeatherLegacy {
        void render(MultiBufferSource bufferSource, Vec3 cameraPos, WeatherRenderState state);
    }

    /** mixin/CbWallScreenEffectMixin -> CbWallScreenEffectReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface ScreenEffectLegacy {
        /** the old renderScreenEffect(sleeping, partialTick, collector), seeing the values 26.1's call passed */
        void cb$renderLegacyFramed(boolean firstPerson, boolean sleeping, float partialTick, SubmitNodeCollector collector, boolean hideGui);
    }

    /** mixin/CbWallTntMinecartContentsMixin -> CbWallTntMinecartContentsReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface TntContentsLegacy {
        /** the protected 1.21.11 submitMinecartContents(tntState, blockState, ..), called virtually */
        void cb$submitLegacyContents(MinecartTntRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light);
    }

    /** mixin/CbWallAudioLibraryMixin -> CbWallAudioLibraryReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface AudioLibraryLegacy {
        /** remember the system's default device as the one last seen (1.21.11 did at init) */
        void cb$rememberDefaultDevice();
    }

    /** mixin/CbWallSimpleContainerMixin -> CbWallSimpleContainerReadsMixin */
    public interface ContainerListenersLegacy {
        /** tell the listeners 1.21.11's addListener added, in order */
        void cb$notifyListeners();
    }

    /** mixin/CbWallItemLayerMixin -> CbWallItemLayerReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface ItemLayerLegacy {
        void cb$clearLegacy();

        /** the tints array prepareTintLayers handed out, or null */
        int[] cb$legacyTints();

        /** the render type setRenderType set, or null */
        RenderType cb$legacyRenderType();
    }

    /** mixin/CbWallEntityFluidMixin -> CbWallEntityFluidReadsMixin */
    public interface EntityFluidLegacy {
        /** the heights 1.21.11's measurement recorded for tags 26.1 does not track, or null when none was measured */
        Object2DoubleMap<TagKey<Fluid>> cb$legacyFluidHeightsIfAny();

        /** the restored updateFluidHeightAndDoFluidPushing's answer inside vanilla's path, vanilla's own value given */
        boolean cb$vanillaPath(TagKey<Fluid> tag, double speed, boolean vanilla);
    }

    /** mixin/CbWallMinecraftMixin -> CbWallMinecraftReadsMixin */
    @Environment(EnvType.CLIENT)
    public interface MinecraftLegacy {
        /** 1.21.11 Minecraft.noRender */
        boolean cb$noRender();

        void cb$clearNoRender();
    }
}
