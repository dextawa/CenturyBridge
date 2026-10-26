package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.MapMaker;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.special.BannerSpecialRenderer;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.ConduitSpecialRenderer;
import net.minecraft.client.renderer.special.CopperGolemStatueSpecialRenderer;
import net.minecraft.client.renderer.special.HangingSignSpecialRenderer;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.StandingSignSpecialRenderer;
import net.minecraft.client.renderer.special.TridentSpecialRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix4f;

/**
 * 1.21.11 item model constructors whose 26.1 records gained fields (javap of both).
 * <ul>
 * <li>The unbaked item models (Composite, Conditional, RangeSelect, Select, the cuboid block model wrapper, the
 *     special model wrapper) gained an Optional&lt;Transformation&gt; applied around what they bake. 1.21.11's had
 *     none: the containers take Optional.empty() (their models bake as in 1.21.11).</li>
 * <li>26.1 moved the pose 1.21.11's special renderers applied inside their submit into the item definition's
 *     transformation (26.1's special renderers do not touch the pose stack; 26.1's vanilla items carry the pose in
 *     assets/minecraft/items/*.json). So a 1.21.11 special model wrapper takes, as its transformation, the pose
 *     1.21.11's renderer of that special model applied ({@link #legacyPose}); a special model 1.21.11 did not know
 *     (a mod's own, whose renderer poses itself as it did) takes none.</li>
 * <li>The special models that 26.1 split by attachment take the attachment 1.21.11's item drew: banner standing
 *     (BannerRenderer.submitSpecial used the standing models), chest single (the only chest), standing sign ground
 *     (SignRenderer.createSignModel(.., true)), hanging sign CEILING_MIDDLE (createSignModel(.., CEILING_MIDDLE)).
 *     26.1's shulker box model has no direction (the item pose is the definition's); the 1.21.11 direction is kept
 *     beside the record and posed by {@link #legacyPose}.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallItemModels {

    /** 1.21.11 ShulkerBoxSpecialRenderer.Unbaked.orientation of the records made through it (identity keys) */
    private static final Map<ShulkerBoxSpecialRenderer.Unbaked, Direction> SHULKER_ORIENTATION = new MapMaker().weakKeys().makeMap();

    private CbWallItemModels() {
    }

    /** 1.21.11 new CompositeModel.Unbaked(models) */
    public static CompositeModel.Unbaked composite(List<ItemModel.Unbaked> models) {
        return new CompositeModel.Unbaked(models, Optional.empty());
    }

    /** 1.21.11 new ConditionalItemModel.Unbaked(property, onTrue, onFalse) */
    public static ConditionalItemModel.Unbaked conditional(ConditionalItemModelProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) {
        return new ConditionalItemModel.Unbaked(Optional.empty(), property, onTrue, onFalse);
    }

    /** 1.21.11 new RangeSelectItemModel.Unbaked(property, scale, entries, fallback) */
    public static RangeSelectItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty property, float scale, List<RangeSelectItemModel.Entry> entries,
                                                          Optional<ItemModel.Unbaked> fallback) {
        return new RangeSelectItemModel.Unbaked(Optional.empty(), property, scale, entries, fallback);
    }

    /** 1.21.11 new SelectItemModel.Unbaked(unbakedSwitch, fallback) */
    public static SelectItemModel.Unbaked select(SelectItemModel.UnbakedSwitch<?, ?> unbakedSwitch, Optional<ItemModel.Unbaked> fallback) {
        return new SelectItemModel.Unbaked(Optional.empty(), unbakedSwitch, fallback);
    }

    /** 1.21.11 new BlockModelWrapper.Unbaked(model, tints) (26.1: CuboidItemModelWrapper.Unbaked) */
    public static CuboidItemModelWrapper.Unbaked cuboid(Identifier model, List<ItemTintSource> tints) {
        return new CuboidItemModelWrapper.Unbaked(model, Optional.empty(), tints);
    }

    /** 1.21.11 new SpecialModelWrapper.Unbaked(base, specialModel): the special model's 1.21.11 pose as transformation */
    public static SpecialModelWrapper.Unbaked specialWrapper(Identifier base, SpecialModelRenderer.Unbaked<?> specialModel) {
        return new SpecialModelWrapper.Unbaked(base, legacyPose(specialModel), specialModel);
    }

    /**
     * The pose 1.21.11's renderer of this special model applied before drawing (javap of 1.21.11's submit and its
     * helpers): banner BannerRenderer.method_65555 (angle 0), conduit, copper golem statue (below), skull and
     * player head SkullBlockRenderer.method_72958 (no direction, head turned 180), shield method_65707, trident,
     * shulker box ShulkerBoxRenderer.method_72162 (its orientation), standing sign SignRenderer.method_72164,
     * hanging sign HangingSignRenderer.method_49918 (angle 0) and the scale after it. Chest and decorated pot drew
     * without posing in both releases; models 1.21.11 did not have take none.
     */
    public static Optional<Transformation> legacyPose(SpecialModelRenderer.Unbaked<?> model) {
        Matrix4f m;
        if (model instanceof BannerSpecialRenderer.Unbaked) {
            m = new Matrix4f().translate(0.5F, 0.0F, 0.5F).scale(0.6666667F, -0.6666667F, -0.6666667F);
        } else if (model instanceof ConduitSpecialRenderer.Unbaked) {
            m = new Matrix4f().translate(0.5F, 0.5F, 0.5F);
        } else if (model instanceof CopperGolemStatueSpecialRenderer.Unbaked) {
            // 1.21.11: method_73018 (translate (0.5, 1.5, 0.5), scale (-1, -1, 1)), then the model turned its root
            // by the SOUTH facing it was given (CopperGolemStatueModel.method_73468: yRot pi, zRot pi); 26.1's model
            // takes no facing (Unit), and 26.1's own copper_golem_statue item definition carries the combined pose
            m = new Matrix4f().translate(0.5F, 1.5F, 0.5F).scale(1.0F, -1.0F, -1.0F);
        } else if (model instanceof SkullSpecialRenderer.Unbaked || model instanceof PlayerHeadSpecialRenderer.Unbaked) {
            m = new Matrix4f().translate(0.5F, 0.0F, 0.5F).scale(-1.0F, -1.0F, 1.0F).rotate(Axis.YP.rotationDegrees(180.0F));
        } else if (model instanceof ShieldSpecialRenderer.Unbaked || model instanceof TridentSpecialRenderer.Unbaked) {
            m = new Matrix4f().scale(1.0F, -1.0F, -1.0F);
        } else if (model instanceof ShulkerBoxSpecialRenderer.Unbaked shulker) {
            m = new Matrix4f().translate(0.5F, 0.5F, 0.5F).scale(0.9995F, 0.9995F, 0.9995F)
                .rotate(shulkerOrientation(shulker).getRotation()).scale(1.0F, -1.0F, -1.0F).translate(0.0F, -1.0F, 0.0F);
        } else if (model instanceof StandingSignSpecialRenderer.Unbaked) {
            m = new Matrix4f().translate(0.5F, 0.5F, 0.5F).scale(0.6666667F, -0.6666667F, -0.6666667F);
        } else if (model instanceof HangingSignSpecialRenderer.Unbaked) {
            m = new Matrix4f().translate(0.5F, 0.9375F, 0.5F).translate(0.0F, -0.3125F, 0.0F).scale(1.0F, -1.0F, -1.0F);
        } else {
            return Optional.empty();
        }
        return Optional.of(new Transformation(m));
    }

    /** 1.21.11 new BannerSpecialRenderer.Unbaked(baseColor) */
    public static BannerSpecialRenderer.Unbaked banner(DyeColor baseColor) {
        return new BannerSpecialRenderer.Unbaked(baseColor, BannerBlock.AttachmentType.GROUND);
    }

    /** 1.21.11 new BannerSpecialRenderer(baseColor, bannerRenderer) */
    public static BannerSpecialRenderer bannerRenderer(DyeColor baseColor, BannerRenderer renderer) {
        return new BannerSpecialRenderer(baseColor, renderer, BannerBlock.AttachmentType.GROUND);
    }

    /** the attachment a 1.21.11 BannerSpecialRenderer subclass's super(baseColor, renderer) drew: standing */
    public static BannerBlock.AttachmentType bannerAttachment() {
        return BannerBlock.AttachmentType.GROUND;
    }

    /** 1.21.11 new ChestSpecialRenderer.Unbaked(texture, openness) */
    public static ChestSpecialRenderer.Unbaked chest(Identifier texture, float openness) {
        return new ChestSpecialRenderer.Unbaked(texture, openness, ChestType.SINGLE);
    }

    /** 1.21.11 new StandingSignSpecialRenderer.Unbaked(woodType) */
    public static StandingSignSpecialRenderer.Unbaked standingSign(WoodType woodType) {
        return new StandingSignSpecialRenderer.Unbaked(woodType, PlainSignBlock.Attachment.GROUND);
    }

    /** 1.21.11 new StandingSignSpecialRenderer.Unbaked(woodType, texture) */
    public static StandingSignSpecialRenderer.Unbaked standingSign(WoodType woodType, Optional<Identifier> texture) {
        return new StandingSignSpecialRenderer.Unbaked(woodType, PlainSignBlock.Attachment.GROUND, texture);
    }

    /** 1.21.11 new HangingSignSpecialRenderer.Unbaked(woodType) */
    public static HangingSignSpecialRenderer.Unbaked hangingSign(WoodType woodType) {
        return new HangingSignSpecialRenderer.Unbaked(woodType, HangingSignBlock.Attachment.CEILING_MIDDLE);
    }

    /** 1.21.11 new HangingSignSpecialRenderer.Unbaked(woodType, texture) */
    public static HangingSignSpecialRenderer.Unbaked hangingSign(WoodType woodType, Optional<Identifier> texture) {
        return new HangingSignSpecialRenderer.Unbaked(woodType, HangingSignBlock.Attachment.CEILING_MIDDLE, texture);
    }

    /** 1.21.11 new ShulkerBoxSpecialRenderer.Unbaked(texture, openness, orientation) */
    public static ShulkerBoxSpecialRenderer.Unbaked shulkerBox(Identifier texture, float openness, Direction orientation) {
        ShulkerBoxSpecialRenderer.Unbaked unbaked = new ShulkerBoxSpecialRenderer.Unbaked(texture, openness);
        SHULKER_ORIENTATION.put(unbaked, orientation);
        return unbaked;
    }

    /** 1.21.11 ShulkerBoxSpecialRenderer.Unbaked.orientation(): UP unless made with another (1.21.11's default) */
    public static Direction shulkerOrientation(ShulkerBoxSpecialRenderer.Unbaked unbaked) {
        Direction orientation = SHULKER_ORIENTATION.get(unbaked);
        return orientation != null ? orientation : Direction.UP;
    }
}
