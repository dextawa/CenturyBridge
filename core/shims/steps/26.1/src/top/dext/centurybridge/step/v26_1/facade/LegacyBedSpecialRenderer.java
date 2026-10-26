package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.special.BedSpecialRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.joml.Vector3fc;
import top.dext.centurybridge.step.v26_1.rt.CbWallBed;

/**
 * 1.21.11's BedSpecialRenderer: the whole bed item (head and foot) as one special model. 26.1's BedSpecialRenderer
 * draws one part (26.1's bed item is a composite of a head and a foot special model, each placed by its item
 * definition); this one is a 26.1 BedSpecialRenderer (of the head, the part 26.1's record must name) whose submit and
 * getExtents are 1.21.11's (javap class_10500): bedRenderer.submitSpecial(pose, collector, light, overlay, material,
 * outline) and bedRenderer.getExtents(consumer), both 1.21.11's whole bed (rt/CbWallBed), so a 1.21.11 bed renderer's
 * own overrides of those are what it draws.
 * <p>
 * Made by 1.21.11's new BedSpecialRenderer(bedRenderer, material) (ctorRedirects), by baking a record a 1.21.11
 * constructor made (CbWallBed.bake, {@link Unbaked}), and the superclass of a 1.21.11 mod's BedSpecialRenderer subclass
 * (superRenames): its super(bedRenderer, material), super.submit(context, ...) and super.getExtents(consumer) are these.
 */
@Environment(EnvType.CLIENT)
public class LegacyBedSpecialRenderer extends BedSpecialRenderer {
    private final BedRenderer bedRenderer;
    private final SpriteId sprite;

    public LegacyBedSpecialRenderer(BedRenderer bedRenderer, SpriteId sprite) {
        super(bedRenderer, sprite, BedPart.HEAD);
        this.bedRenderer = bedRenderer;
        this.sprite = sprite;
    }

    /**
     * A subclass's super.submit(displayContext, pose, collector, light, overlay, foil, outline): 1.21.11's body, the
     * whole bed (the context unread). Super calls into this class keep their intermediary names past the namespace
     * wall (the subclass's own methods take 26.1's), so super's members are declared under them.
     */
    public void method_65699(ItemDisplayContext displayContext, PoseStack pose, SubmitNodeCollector collector, int light, int overlay,
                             boolean hasFoil, int outline) {
        CbWallBed.submitSpecial(this.bedRenderer, pose, collector, light, overlay, this.sprite, outline);
    }

    /** a subclass's super call of 26.1's submit shape (its override hook falling through to super): the whole bed */
    public void method_65699(PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outline) {
        CbWallBed.submitSpecial(this.bedRenderer, pose, collector, light, overlay, this.sprite, outline);
    }

    /** a subclass's super.getExtents(consumer): the whole bed's */
    public void method_72175(Consumer<Vector3fc> consumer) {
        CbWallBed.callExtents(this.bedRenderer, consumer);
    }

    @Override
    public void submit(PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outline) {
        CbWallBed.submitSpecial(this.bedRenderer, pose, collector, light, overlay, this.sprite, outline);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        CbWallBed.callExtents(this.bedRenderer, consumer);
    }

    /**
     * The whole-bed special model a 1.21.11 special model wrapper holds in place of the record a 1.21.11 constructor
     * made (CbWallItemModels.specialWrapper): 26.1's wrapper bakes its special model itself, and 26.1's record bakes one
     * part. Its codec is 1.21.11's record codec (texture only); 26.1's special model types do not list it, as they do
     * not list any type a mod has not registered.
     */
    @Environment(EnvType.CLIENT)
    public record Unbaked(Identifier texture) implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture)
        ).apply(i, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Void> bake(SpecialModelRenderer.BakingContext context) {
            return new LegacyBedSpecialRenderer(new BedRenderer(context), Sheets.BED_MAPPER.apply(this.texture));
        }
    }
}
