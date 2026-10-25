package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.joml.Vector3fc;
import top.dext.centurybridge.step.v26_2.rt.CbS26BedsClient;

/**
 * 26.1.2's BedSpecialRenderer (gone on 26.2, which draws bed items as a composite of its bed block models): reached
 * by classRenames, 26.1.2's body (javap): one half of the bed drawn by a BedRenderer with a bed sprite. Its Unbaked
 * form is the item model type "minecraft:bed" 26.1.2's item models name ({"type": "minecraft:bed", "texture": ...,
 * "part": ...}), registered with 26.2's special model renderers (mixin/CbS26BedSpecialMixin).
 */
@Environment(EnvType.CLIENT)
public class BedSpecialRenderer implements NoDataSpecialModelRenderer {
    private final BedRenderer bedRenderer;
    private final SpriteId sprite;
    private final BedPart part;

    public BedSpecialRenderer(BedRenderer bedRenderer, SpriteId sprite, BedPart part) {
        this.bedRenderer = bedRenderer;
        this.sprite = sprite;
        this.part = part;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        this.bedRenderer.submitPiece(this.part, this.sprite, poseStack, collector, lightCoords, overlayCoords, null, outlineColor);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        this.bedRenderer.getExtents(this.part, output);
    }

    /** 26.1.2's BedSpecialRenderer.Unbaked: texture (a bed sprite id, entity/bed/&lt;id&gt; of the bed sheet) and part */
    @Environment(EnvType.CLIENT)
    public record Unbaked(Identifier texture, BedPart part) implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            BedPart.CODEC.fieldOf("part").forGetter(Unbaked::part)
        ).apply(i, Unbaked::new));

        public Unbaked(DyeColor color, BedPart part) {
            this(CbS26BedsClient.colorToResourceSprite(color), part);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public BedSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new BedSpecialRenderer(new BedRenderer(context), CbS26BedsClient.BED_MAPPER().apply(this.texture), this.part);
        }
    }
}
