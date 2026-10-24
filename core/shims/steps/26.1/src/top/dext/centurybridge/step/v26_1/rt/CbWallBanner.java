package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

/** 1.21.11 BannerRenderer members on 26.1's */
@Environment(EnvType.CLIENT)
public final class CbWallBanner {

    private CbWallBanner() {
    }

    /** a mod's call of 1.21.11's submitSpecial(pose, collector, light, overlay, color, patterns, outline): the standing banner */
    public static void submitSpecial(BannerRenderer renderer, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, DyeColor color,
        BannerPatternLayers patterns, int outline) {
        renderer.submitSpecial(CbWallFramesClient.standingBanner(), pose, collector, light, overlay, color, patterns, outline);
    }
}
