package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 26.1.2 Sheets members 26.2 dropped or regrouped (javap of both).
 * <ul>
 * <li>cutoutBlockSheet() / translucentBlockSheet(): 26.1.2's CUTOUT_BLOCK_SHEET = RenderTypes.entityCutoutCull(LOCATION_BLOCKS),
 * TRANSLUCENT_BLOCK_SHEET = RenderTypes.entityTranslucentCullItemTarget(LOCATION_BLOCKS), made once in Sheets' static
 * initializer; 26.2 keeps both factories (memoized per texture, so the same RenderType object every call) and dropped the
 * two fields.</li>
 * <li>CHEST_COPPER_UNAFFECTED / _EXPOSED / _WEATHERED / _OXIDIZED: 26.2 holds the four in Sheets.CHEST_COPPER, a
 * WeatheringCopperCollection.ByState (unaffected, exposed, weathered, oxidized), built from the same sprites.</li>
 * <li>DECORATED_POT_SPRITES / getDecoratedPotSprite(key): 26.1.2 mapped each registered pattern's key to
 * DECORATED_POT_MAPPER.apply(pattern.assetId()) (Collectors.toMap over BuiltInRegistries.DECORATED_POT_PATTERN, a
 * HashMap); 26.2 keeps the mapper, the registry and assetId, and its DecoratedPotRenderer builds the same sprites keyed by
 * item. The map is built the 26.1.2 way on first use. Known difference: 26.2's renderer does not read it, so an entry a mod
 * puts into it is answered by getDecoratedPotSprite but not drawn by vanilla's pot renderer.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS26Sheets {
    private CbS26Sheets() {
    }

    private static Map<ResourceKey<DecoratedPotPattern>, SpriteId> decoratedPotSprites;

    /** 26.1.2 Sheets.cutoutBlockSheet() */
    public static RenderType cutoutBlockSheet() {
        return RenderTypes.entityCutoutCull(TextureAtlas.LOCATION_BLOCKS);
    }

    /** 26.1.2 Sheets.translucentBlockSheet() */
    public static RenderType translucentBlockSheet() {
        return RenderTypes.entityTranslucentCullItemTarget(TextureAtlas.LOCATION_BLOCKS);
    }

    /** 26.1.2 Sheets.CHEST_COPPER_UNAFFECTED */
    public static MultiblockChestResources<SpriteId> CHEST_COPPER_UNAFFECTED() {
        return Sheets.CHEST_COPPER.unaffected();
    }

    /** 26.1.2 Sheets.CHEST_COPPER_EXPOSED */
    public static MultiblockChestResources<SpriteId> CHEST_COPPER_EXPOSED() {
        return Sheets.CHEST_COPPER.exposed();
    }

    /** 26.1.2 Sheets.CHEST_COPPER_WEATHERED */
    public static MultiblockChestResources<SpriteId> CHEST_COPPER_WEATHERED() {
        return Sheets.CHEST_COPPER.weathered();
    }

    /** 26.1.2 Sheets.CHEST_COPPER_OXIDIZED */
    public static MultiblockChestResources<SpriteId> CHEST_COPPER_OXIDIZED() {
        return Sheets.CHEST_COPPER.oxidized();
    }

    /** 26.1.2 Sheets.DECORATED_POT_SPRITES */
    public static synchronized Map<ResourceKey<DecoratedPotPattern>, SpriteId> DECORATED_POT_SPRITES() {
        Map<ResourceKey<DecoratedPotPattern>, SpriteId> m = decoratedPotSprites;
        if (m == null) {
            m = decoratedPotSprites = BuiltInRegistries.DECORATED_POT_PATTERN.listElements()
                .collect(Collectors.toMap(Holder.Reference::key,
                    p -> Sheets.DECORATED_POT_MAPPER.apply(p.value().assetId())));
        }
        return m;
    }

    /** 26.1.2 Sheets.getDecoratedPotSprite(key): null for a null key, else the map's entry */
    public static SpriteId getDecoratedPotSprite(ResourceKey<DecoratedPotPattern> key) {
        return key == null ? null : DECORATED_POT_SPRITES().get(key);
    }
}
