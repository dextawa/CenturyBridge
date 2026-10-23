package top.dext.centurybridge.step.v26_1.rt;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * 1.21.11's Sheets on 26.1.
 * <ul>
 * <li>The sheet render types: 1.21.11 built them once from the sheet atlases (shulker boxes and signs
 *     entityCutoutNoCull, beds and the block atlas entitySolid, banners and shields bannerPattern, chests
 *     entityCutout); 26.1 keeps the atlases and the factories, which answer the same render type per atlas.</li>
 * <li>Sign materials and chest locations: renamed (SIGN_MATERIALS is SIGN_SPRITES, getSignMaterial is
 *     getSignSprite) or folded (each chest's single / left / right location is a MultiblockChestResources).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallSheets {

    private CbWallSheets() {
    }

    // ---- sheet render types ----

    /** 1.21.11 Sheets.shulkerBoxSheet() */
    public static RenderType shulkerBoxSheet() {
        return RenderTypes.entityCutout(Sheets.SHULKER_SHEET);
    }

    /** 1.21.11 Sheets.bedSheet() */
    public static RenderType bedSheet() {
        return RenderTypes.entitySolid(Sheets.BED_SHEET);
    }

    /** 1.21.11 Sheets.bannerSheet() */
    public static RenderType bannerSheet() {
        return RenderTypes.bannerPattern(Sheets.BANNER_SHEET);
    }

    /** 1.21.11 Sheets.shieldSheet() */
    public static RenderType shieldSheet() {
        return RenderTypes.bannerPattern(Sheets.SHIELD_SHEET);
    }

    /** 1.21.11 Sheets.signSheet() */
    public static RenderType signSheet() {
        return RenderTypes.entityCutout(Sheets.SIGN_SHEET);
    }

    /** 1.21.11 Sheets.hangingSignSheet(): the sign sheet's render type, as 1.21.11 answered */
    public static RenderType hangingSignSheet() {
        return RenderTypes.entityCutout(Sheets.SIGN_SHEET);
    }

    /** 1.21.11 Sheets.chestSheet() */
    public static RenderType chestSheet() {
        return RenderTypes.entityCutoutCull(Sheets.CHEST_SHEET);
    }

    /** 1.21.11 Sheets.solidBlockSheet() */
    public static RenderType solidBlockSheet() {
        return RenderTypes.entitySolid(TextureAtlas.LOCATION_BLOCKS);
    }

    // ---- sprites ----

    /** 1.21.11 Sheets.getSignMaterial(WoodType) */
    public static SpriteId getSignMaterial(WoodType type) {
        return Sheets.getSignSprite(type);
    }

    /** 1.21.11 Sheets.getHangingSignMaterial(WoodType) */
    public static SpriteId getHangingSignMaterial(WoodType type) {
        return Sheets.getHangingSignSprite(type);
    }

    /** 1.21.11 Sheets.chooseMaterial(ChestMaterialType, ChestType) */
    public static SpriteId chooseMaterial(ChestRenderState.ChestMaterialType material, ChestType type) {
        return Sheets.chooseSprite(material, type);
    }

    /** 1.21.11 Sheets.SIGN_MATERIALS */
    public static Map<WoodType, SpriteId> field_21712() {
        return Sheets.SIGN_SPRITES;
    }

    /** 1.21.11 Sheets.HANGING_SIGN_MATERIALS */
    public static Map<WoodType, SpriteId> field_40515() {
        return Sheets.HANGING_SIGN_SPRITES;
    }

    /** 1.21.11 Sheets.CHEST_TRAP_LOCATION */
    public static SpriteId field_21714() {
        return Sheets.CHEST_TRAPPED.single();
    }

    /** 1.21.11 Sheets.CHEST_TRAP_LOCATION_LEFT */
    public static SpriteId field_21715() {
        return Sheets.CHEST_TRAPPED.left();
    }

    /** 1.21.11 Sheets.CHEST_TRAP_LOCATION_RIGHT */
    public static SpriteId field_21716() {
        return Sheets.CHEST_TRAPPED.right();
    }

    /** 1.21.11 Sheets.CHEST_XMAS_LOCATION */
    public static SpriteId field_21717() {
        return Sheets.CHEST_CHRISTMAS.single();
    }

    /** 1.21.11 Sheets.CHEST_XMAS_LOCATION_LEFT */
    public static SpriteId field_21718() {
        return Sheets.CHEST_CHRISTMAS.left();
    }

    /** 1.21.11 Sheets.CHEST_XMAS_LOCATION_RIGHT */
    public static SpriteId field_21719() {
        return Sheets.CHEST_CHRISTMAS.right();
    }

    /** 1.21.11 Sheets.CHEST_LOCATION */
    public static SpriteId field_21720() {
        return Sheets.CHEST_REGULAR.single();
    }

    /** 1.21.11 Sheets.CHEST_LOCATION_LEFT */
    public static SpriteId field_21721() {
        return Sheets.CHEST_REGULAR.left();
    }

    /** 1.21.11 Sheets.CHEST_LOCATION_RIGHT */
    public static SpriteId field_21722() {
        return Sheets.CHEST_REGULAR.right();
    }

    /** 1.21.11 Sheets.COPPER_CHEST_LOCATION */
    public static SpriteId field_61745() {
        return Sheets.CHEST_COPPER_UNAFFECTED.single();
    }

    /** 1.21.11 Sheets.COPPER_CHEST_LOCATION_LEFT */
    public static SpriteId field_61746() {
        return Sheets.CHEST_COPPER_UNAFFECTED.left();
    }

    /** 1.21.11 Sheets.COPPER_CHEST_LOCATION_RIGHT */
    public static SpriteId field_61747() {
        return Sheets.CHEST_COPPER_UNAFFECTED.right();
    }

    /** 1.21.11 Sheets.EXPOSED_COPPER_CHEST_LOCATION */
    public static SpriteId field_61748() {
        return Sheets.CHEST_COPPER_EXPOSED.single();
    }

    /** 1.21.11 Sheets.EXPOSED_COPPER_CHEST_LOCATION_LEFT */
    public static SpriteId field_61749() {
        return Sheets.CHEST_COPPER_EXPOSED.left();
    }

    /** 1.21.11 Sheets.EXPOSED_COPPER_CHEST_LOCATION_RIGHT */
    public static SpriteId field_61750() {
        return Sheets.CHEST_COPPER_EXPOSED.right();
    }

    /** 1.21.11 Sheets.WEATHERED_COPPER_CHEST_LOCATION */
    public static SpriteId field_61751() {
        return Sheets.CHEST_COPPER_WEATHERED.single();
    }

    /** 1.21.11 Sheets.WEATHERED_COPPER_CHEST_LOCATION_LEFT */
    public static SpriteId field_61752() {
        return Sheets.CHEST_COPPER_WEATHERED.left();
    }

    /** 1.21.11 Sheets.WEATHERED_COPPER_CHEST_LOCATION_RIGHT */
    public static SpriteId field_61753() {
        return Sheets.CHEST_COPPER_WEATHERED.right();
    }

    /** 1.21.11 Sheets.OXIDIZED_COPPER_CHEST_LOCATION */
    public static SpriteId field_61754() {
        return Sheets.CHEST_COPPER_OXIDIZED.single();
    }

    /** 1.21.11 Sheets.OXIDIZED_COPPER_CHEST_LOCATION_LEFT */
    public static SpriteId field_61755() {
        return Sheets.CHEST_COPPER_OXIDIZED.left();
    }

    /** 1.21.11 Sheets.OXIDIZED_COPPER_CHEST_LOCATION_RIGHT */
    public static SpriteId field_61756() {
        return Sheets.CHEST_COPPER_OXIDIZED.right();
    }
}
