package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

/**
 * Beds, client side: what 26.1.2's bed renderer reached that 26.2 dropped with it (javap of both).
 * <ul>
 * <li>ModelLayers.BED_HEAD / BED_FOOT: 26.1.2's locations ("minecraft:bed_head" / "bed_foot", layer "main"), their
 * definitions (facade/BedRenderer.createHeadLayer / createFootLayer) added to 26.2's layer definitions
 * (mixin/CbS26BedLayersMixin).</li>
 * <li>the bed sheet: 26.1.2 stitched entity/bed/* into an atlas of its own (atlases/beds.json, textures/atlas/beds.png);
 * 26.2 has no such atlas and keeps the block-entity sprites it still has (conduit, bell, enchanting book) in the block
 * atlas (Sheets.BLOCK_ENTITIES_MAPPER: blocks atlas, "entity"). The layer's resources add 26.1.2's entity/bed directory
 * source to the block atlas (assets/minecraft/atlases/blocks.json), so 26.1.2's BED_SHEET is the block atlas, its
 * BED_MAPPER (prefix "entity/bed") names the same sprites there, and AtlasIds.BEDS is AtlasIds.BLOCKS. 26.1.2's sixteen
 * vanilla bed sprites, which 26.2 no longer ships, are rebuilt there from 26.2's bed block textures
 * (rt/CbS26LegacyBedSprites).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS26BedsClient {
    private CbS26BedsClient() {}

    private static final ModelLayerLocation BED_HEAD = new ModelLayerLocation(Identifier.withDefaultNamespace("bed_head"), "main");
    private static final ModelLayerLocation BED_FOOT = new ModelLayerLocation(Identifier.withDefaultNamespace("bed_foot"), "main");
    private static final SpriteMapper BED_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_BLOCKS, "entity/bed");
    private static final SpriteId[] BED_TEXTURES = new SpriteId[DyeColor.values().length];

    static {
        for (DyeColor color : DyeColor.values()) {
            BED_TEXTURES[color.getId()] = createBedSprite(color);
        }
    }

    /** 26.1.2 ModelLayers.BED_HEAD */
    public static ModelLayerLocation BED_HEAD() {
        return BED_HEAD;
    }

    /** 26.1.2 ModelLayers.BED_FOOT */
    public static ModelLayerLocation BED_FOOT() {
        return BED_FOOT;
    }

    /** 26.1.2 Sheets.BED_SHEET: the atlas holding the bed sprites, on 26.2 the block atlas */
    public static Identifier BED_SHEET() {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    /** 26.1.2 Sheets.BED_MAPPER: the bed sheet, prefix "entity/bed" */
    public static SpriteMapper BED_MAPPER() {
        return BED_MAPPER;
    }

    /** 26.1.2 Sheets.createBedSprite(color): BED_MAPPER.apply(colorToResourceSprite(color)) */
    public static SpriteId createBedSprite(DyeColor color) {
        return BED_MAPPER.apply(colorToResourceSprite(color));
    }

    /**
     * 26.1.2 Sheets.colorToResourceSprite(color), its body: Identifier.withDefaultNamespace(color.getName()) -- the
     * colour's bed sprite name (26.2 kept the like colorToShulkerSprite, not this one)
     */
    public static Identifier colorToResourceSprite(DyeColor color) {
        return Identifier.withDefaultNamespace(color.getName());
    }

    /** 26.1.2 Sheets.getBedSprite(color): the sprite createBedSprite gives, made once per colour */
    public static SpriteId getBedSprite(DyeColor color) {
        return BED_TEXTURES[color.getId()];
    }

    private static final net.minecraft.client.data.models.model.ModelTemplate BED_INVENTORY = new net.minecraft.client.data.models.model.ModelTemplate(
        java.util.Optional.of(Identifier.withDefaultNamespace("item/template_bed")), java.util.Optional.empty(),
        net.minecraft.client.data.models.model.TextureSlot.PARTICLE);

    /**
     * 26.1.2 ModelTemplates.BED_INVENTORY (data generation): createItem("template_bed", PARTICLE), the item model parented
     * on minecraft:item/template_bed that 26.1.2 bed items used (26.2 composes bed items from its bed block models and
     * dropped both; the layer's resources give the template model back)
     */
    public static net.minecraft.client.data.models.model.ModelTemplate BED_INVENTORY() {
        return BED_INVENTORY;
    }
}
