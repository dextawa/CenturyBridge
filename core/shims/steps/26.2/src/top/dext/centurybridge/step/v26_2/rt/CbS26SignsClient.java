package top.dext.centurybridge.step.v26_2.rt;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix4f;

/**
 * 26.1.2's sign bodies, for the sign blocks 26.2 does not draw itself. 26.2 draws a sign's board, stick and chains
 * as block models (blockstates/&lt;wood&gt;_sign.json -&gt; models/block/&lt;wood&gt;_sign_rot_N, parent
 * template_sign_rot_N, texture block/&lt;wood&gt;_sign) and its sign renderers draw only the text; the sign model
 * layers, Sheets' sign sprites and the signs atlas are gone. A 26.1.2 mod's sign block ships a blockstate whose
 * model has a particle texture only (26.1.2 drew the body from the block entity), so 26.2 would draw no body for it.
 * For blocks that are not 26.2's own (registry namespace other than minecraft) the layer draws the body the way
 * 26.1.2's StandingSignRenderer / HangingSignRenderer did: the same layer definitions (createSignLayer,
 * createHangingSignLayer), layer names (ModelLayers.create*SignModelName), body transformations
 * (bodyTransformation: base rotation, wall offset, 2/3 scale for plain signs, unit scale for hanging ones), render
 * type (RenderTypes.entityCutout) and sprites (entity/signs/&lt;wood&gt;, entity/signs/hanging/&lt;wood&gt;), the
 * sprites now living in the blocks atlas (the layer's atlases/blocks.json adds the entity/signs directory, as 26.1.2's
 * atlases/signs.json listed it). 26.2's own blocks keep 26.2's block models.
 */
@Environment(EnvType.CLIENT)
public final class CbS26SignsClient {
    private CbS26SignsClient() {}

    /** 26.1.2 Sheets.SIGN_SHEET (textures/atlas/signs.png): the sign sprites live in 26.2's blocks atlas */
    public static final Identifier SIGN_SHEET = TextureAtlas.LOCATION_BLOCKS;
    public static final SpriteMapper SIGN_MAPPER = new SpriteMapper(SIGN_SHEET, "entity/signs");
    public static final SpriteMapper HANGING_SIGN_MAPPER = new SpriteMapper(SIGN_SHEET, "entity/signs/hanging");

    public static Identifier SIGN_SHEET() {
        return SIGN_SHEET;
    }

    public static SpriteMapper SIGN_MAPPER() {
        return SIGN_MAPPER;
    }

    public static SpriteMapper HANGING_SIGN_MAPPER() {
        return HANGING_SIGN_MAPPER;
    }

    /** 26.1.2 Sheets.SIGN_SPRITES / HANGING_SIGN_SPRITES: one sprite per wood type known when first asked */
    private static Map<WoodType, SpriteId> signSprites;
    private static Map<WoodType, SpriteId> hangingSignSprites;

    public static Map<WoodType, SpriteId> SIGN_SPRITES() {
        if (signSprites == null) {
            signSprites = WoodType.values().collect(Collectors.toMap(Function.identity(), w -> SIGN_MAPPER.defaultNamespaceApply(w.name())));
        }
        return signSprites;
    }

    public static Map<WoodType, SpriteId> HANGING_SIGN_SPRITES() {
        if (hangingSignSprites == null) {
            hangingSignSprites = WoodType.values().collect(Collectors.toMap(Function.identity(), w -> HANGING_SIGN_MAPPER.defaultNamespaceApply(w.name())));
        }
        return hangingSignSprites;
    }

    /** 26.1.2 Sheets.getSignSprite: the map's sprite (a wood type registered later maps the same way) */
    public static SpriteId getSignSprite(WoodType wood) {
        SpriteId s = SIGN_SPRITES().get(wood);
        return s != null ? s : SIGN_MAPPER.defaultNamespaceApply(wood.name());
    }

    public static SpriteId getHangingSignSprite(WoodType wood) {
        SpriteId s = HANGING_SIGN_SPRITES().get(wood);
        return s != null ? s : HANGING_SIGN_MAPPER.defaultNamespaceApply(wood.name());
    }

    /** 26.1.2 ModelLayers.createStandingSignModelName: minecraft:sign/standing/&lt;wood&gt;, layer main */
    public static ModelLayerLocation createStandingSignModelName(WoodType wood) {
        return new ModelLayerLocation(Identifier.withDefaultNamespace("sign/standing/" + wood.name()), "main");
    }

    public static ModelLayerLocation createWallSignModelName(WoodType wood) {
        return new ModelLayerLocation(Identifier.withDefaultNamespace("sign/wall/" + wood.name()), "main");
    }

    public static ModelLayerLocation createHangingSignModelName(WoodType wood, HangingSignBlock.Attachment attachment) {
        return new ModelLayerLocation(Identifier.withDefaultNamespace("hanging_sign/" + wood.name() + "/" + attachment.getSerializedName()), "main");
    }

    /** 26.1.2 StandingSignRenderer.createSignLayer(standing) */
    public static LayerDefinition createSignLayer(boolean standing) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
        if (standing) {
            root.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 64, 32);
    }

    /** 26.1.2 HangingSignRenderer.createHangingSignLayer(attachment) */
    public static LayerDefinition createHangingSignLayer(HangingSignBlock.Attachment attachment) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
        if (attachment == HangingSignBlock.Attachment.WALL) {
            root.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
        }
        if (attachment == HangingSignBlock.Attachment.WALL || attachment == HangingSignBlock.Attachment.CEILING) {
            PartDefinition chains = root.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
            chains.addOrReplaceChild("chainL1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
                PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
            chains.addOrReplaceChild("chainL2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
                PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
            chains.addOrReplaceChild("chainR1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
                PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
            chains.addOrReplaceChild("chainR2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
                PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
        }
        if (attachment == HangingSignBlock.Attachment.CEILING_MIDDLE) {
            root.addOrReplaceChild("vChains", CubeListBuilder.create().texOffs(14, 6).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 64, 32);
    }

    /** 26.1.2's sign layers for every wood type, as 26.1.2's LayerDefinitions.createRoots added them */
    public static void addLayers(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> roots) {
        LayerDefinition standing = createSignLayer(true);
        LayerDefinition wall = createSignLayer(false);
        Map<HangingSignBlock.Attachment, LayerDefinition> hanging = new java.util.EnumMap<>(HangingSignBlock.Attachment.class);
        for (HangingSignBlock.Attachment a : HangingSignBlock.Attachment.values()) {
            hanging.put(a, createHangingSignLayer(a));
        }
        WoodType.values().forEach(wood -> {
            roots.put(createStandingSignModelName(wood), standing);
            roots.put(createWallSignModelName(wood), wall);
            for (HangingSignBlock.Attachment a : HangingSignBlock.Attachment.values()) {
                roots.put(createHangingSignModelName(wood, a), hanging.get(a));
            }
        });
    }

    /** 26.1.2 StandingSignRenderer.createSignModel */
    public static Model.Simple createSignModel(EntityModelSet models, WoodType wood, PlainSignBlock.Attachment attachment) {
        ModelLayerLocation layer = switch (attachment) {
            case GROUND -> createStandingSignModelName(wood);
            case WALL -> createWallSignModelName(wood);
        };
        return new Model.Simple(models.bakeLayer(layer), RenderTypes::entityCutout);
    }

    /** 26.1.2 HangingSignRenderer.createSignModel */
    public static Model.Simple createHangingSignModel(EntityModelSet models, WoodType wood, HangingSignBlock.Attachment attachment) {
        return new Model.Simple(models.bakeLayer(createHangingSignModelName(wood, attachment)), RenderTypes::entityCutout);
    }

    /** 26.1.2 StandingSignRenderer.bodyTransformation(attachment, yRot) */
    public static Transformation standingBody(PlainSignBlock.Attachment attachment, float yRot) {
        Matrix4f m = new Matrix4f().translate(0.5F, 0.5F, 0.5F).rotate(Axis.YP.rotationDegrees(-yRot));
        if (attachment == PlainSignBlock.Attachment.WALL) {
            m.translate(0.0F, -0.3125F, -0.4375F);
        }
        return new Transformation(m.scale(0.6666667F, -0.6666667F, -0.6666667F));
    }

    /** 26.1.2 HangingSignRenderer.bodyTransformation(yRot) */
    public static Transformation hangingBody(float yRot) {
        Matrix4f m = new Matrix4f().translation(0.5F, 0.9375F, 0.5F).rotate(Axis.YP.rotationDegrees(-yRot)).translate(0.0F, -0.3125F, 0.0F);
        return new Transformation(m.scale(1.0F, -1.0F, -1.0F));
    }

    /** 26.1.2 StandingSignRenderer / HangingSignRenderer.submitSpecial */
    public static void submitSpecial(SpriteGetter sprites, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords,
            Model.Simple model, SpriteId sprite) {
        collector.submitModel(model, Unit.INSTANCE, poseStack, lightCoords, overlayCoords, -1, sprite, sprites, 0, null);
    }

    /** a sign block 26.2 does not draw a body for: not one of 26.2's own blocks */
    public static boolean legacyBody(Block block) {
        return !Identifier.DEFAULT_NAMESPACE.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace());
    }
}
