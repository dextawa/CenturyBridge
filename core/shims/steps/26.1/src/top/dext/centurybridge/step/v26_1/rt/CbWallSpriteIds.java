package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;

/**
 * Sprite and texture constants 26.1 moved.
 * <ul>
 * <li>ModelBakery.LAVA_STILL / LAVA_FLOW / WATER_STILL / WATER_FLOW / WATER_OVERLAY: 1.21.11 named the block atlas
 *     sprites "block/&lt;name&gt;" (Sheets' block sprite factory); 26.1 keeps the same textures in its fluid models
 *     (FluidStateModelSet) and names block atlas sprites through Sheets.BLOCKS_MAPPER, as its ModelBakery.FIRE_0
 *     still is.</li>
 * <li>ModelBakery.BANNER_BASE / SHIELD_BASE / NO_PATTERN_SHIELD are 26.1's Sheets.BANNER_BASE / SHIELD_BASE /
 *     SHIELD_BASE_NO_PATTERN (the same sprites on the banner and shield sheets).</li>
 * <li>ChestSpecialRenderer.*_CHEST_TEXTURE: 1.21.11 named a chest texture set by one id ("normal", "christmas",
 *     ...); 26.1 keeps the set (single, left, right) as MultiblockChestResources whose single texture is that id.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallSpriteIds {

    private CbWallSpriteIds() {
    }

    public static SpriteId lavaStill() {
        return Sheets.BLOCKS_MAPPER.defaultNamespaceApply("lava_still");
    }

    public static SpriteId lavaFlow() {
        return Sheets.BLOCKS_MAPPER.defaultNamespaceApply("lava_flow");
    }

    public static SpriteId waterStill() {
        return Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_still");
    }

    public static SpriteId waterFlow() {
        return Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_flow");
    }

    public static SpriteId waterOverlay() {
        return Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_overlay");
    }

    public static SpriteId bannerBase() {
        return Sheets.BANNER_BASE;
    }

    public static SpriteId shieldBase() {
        return Sheets.SHIELD_BASE;
    }

    public static SpriteId noPatternShield() {
        return Sheets.SHIELD_BASE_NO_PATTERN;
    }

    public static Identifier giftChest() {
        return ChestSpecialRenderer.CHRISTMAS.single();
    }

    public static Identifier normalChest() {
        return ChestSpecialRenderer.REGULAR.single();
    }

    public static Identifier trappedChest() {
        return ChestSpecialRenderer.TRAPPED.single();
    }

    public static Identifier copperChest() {
        return ChestSpecialRenderer.COPPER_UNAFFECTED.single();
    }

    public static Identifier exposedCopperChest() {
        return ChestSpecialRenderer.COPPER_EXPOSED.single();
    }

    public static Identifier weatheredCopperChest() {
        return ChestSpecialRenderer.COPPER_WEATHERED.single();
    }

    public static Identifier oxidizedCopperChest() {
        return ChestSpecialRenderer.COPPER_OXIDIZED.single();
    }
}
