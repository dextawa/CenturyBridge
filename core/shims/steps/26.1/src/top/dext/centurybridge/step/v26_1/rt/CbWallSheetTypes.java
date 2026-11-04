package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;

/**
 * 1.21.11 Sheets render-type constants (class_4722, opened by access wideners) 26.1 no longer keeps. 1.21.11's clinit
 * (javap) computed each from a sheet id through a RenderTypes factory; 26.1 keeps the same ids (Sheets.*_SHEET,
 * TextureAtlas.LOCATION_BLOCKS) and the same memoizing factories (entityCutout / entitySolid / bannerPattern /
 * entityCutoutCull, each a Function/BiFunction lookup in both), so the same call answers the same cached type:
 * SHULKER_BOX / SIGN = entityCutout(SHULKER_SHEET / SIGN_SHEET), BED = entitySolid(BED_SHEET),
 * BANNER / SHIELD = bannerPattern(BANNER_SHEET / SHIELD_SHEET), CHEST = entityCutoutCull(CHEST_SHEET),
 * SOLID_BLOCK_SHEET = entitySolid(LOCATION_BLOCKS).
 */
@Environment(EnvType.CLIENT)
public final class CbWallSheetTypes {
    private CbWallSheetTypes() {
    }

    public static RenderType shulkerBoxSheetType() {
        return RenderTypes.entityCutout(Sheets.SHULKER_SHEET);
    }

    public static RenderType bedSheetType() {
        return RenderTypes.entitySolid(Sheets.BED_SHEET);
    }

    public static RenderType bannerSheetType() {
        return RenderTypes.bannerPattern(Sheets.BANNER_SHEET);
    }

    public static RenderType shieldSheetType() {
        return RenderTypes.bannerPattern(Sheets.SHIELD_SHEET);
    }

    public static RenderType signSheetType() {
        return RenderTypes.entityCutout(Sheets.SIGN_SHEET);
    }

    public static RenderType chestSheetType() {
        return RenderTypes.entityCutoutCull(Sheets.CHEST_SHEET);
    }

    public static RenderType solidBlockSheet() {
        return RenderTypes.entitySolid(TextureAtlas.LOCATION_BLOCKS);
    }
}
