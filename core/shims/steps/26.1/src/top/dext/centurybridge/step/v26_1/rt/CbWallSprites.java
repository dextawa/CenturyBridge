package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

/**
 * 1.21.11's model-baking sprite getter (resources/model/SpriteGetter, net/minecraft/class_9826) on 26.1's
 * MaterialBaker.
 *
 * 1.21.11 looked a model texture up by atlas and id (a SpriteId: Material in 1.21.11 terms) and answered
 * the sprite; 26.1 looks it up by id alone (Material: id plus a forceTranslucent flag 1.21.11 did not have)
 * and answers a Material.Baked (sprite plus that flag). Every texture a 1.21.11 model referred to carried the
 * virtual atlas ModelManager.BLOCK_OR_ITEM (TextureSlots made them so), which 1.21.11 resolved in the item
 * atlas and then the block atlas -- what 26.1's baker does for every Material. A mod asking 1.21.11's
 * getter for the BLOCKS or ITEMS atlas explicitly got only that atlas; 26.1's baker cannot be asked so, and
 * answers the id from either (the same sprite unless the id exists in both atlases).
 */
@Environment(EnvType.CLIENT)
public final class CbWallSprites {

    /** 1.21.11 ModelManager.BLOCK_OR_ITEM: the virtual atlas of model textures, gone with atlases on materials */
    private static final Identifier BLOCK_OR_ITEM = Identifier.withDefaultNamespace("block_or_item");

    private CbWallSprites() {
    }

    /** getter for 1.21.11 ModelManager.BLOCK_OR_ITEM */
    public static Identifier field_64468() {
        return BLOCK_OR_ITEM;
    }

    /** 1.21.11 SpriteGetter.get(Material, ModelDebugName) */
    public static TextureAtlasSprite get(MaterialBaker baker, SpriteId id, ModelDebugName name) {
        return baker.get(new Material(id.texture()), name).sprite();
    }

    /** 1.21.11 SpriteGetter.reportMissingReference(String, ModelDebugName) */
    public static TextureAtlasSprite reportMissingReference(MaterialBaker baker, String reference, ModelDebugName name) {
        return baker.reportMissingReference(reference, name).sprite();
    }

    /** 1.21.11 SpriteGetter.resolveSlot(TextureSlots, String, ModelDebugName) */
    public static TextureAtlasSprite resolveSlot(MaterialBaker baker, TextureSlots slots, String slot, ModelDebugName name) {
        return baker.resolveSlot(slots, slot, name).sprite();
    }

    /** 1.21.11 TextureSlots.getMaterial(String): the slot's texture in the virtual model atlas, as 1.21.11 made it */
    public static SpriteId getMaterial(TextureSlots slots, String slot) {
        Material m = slots.getMaterial(slot);
        return m == null ? null : new SpriteId(BLOCK_OR_ITEM, m.sprite());
    }

    /** A 1.21.11 SpriteGetter implementation: its own get and reportMissingReference */
    public interface LegacySpriteGetter {
        TextureAtlasSprite get(SpriteId id, ModelDebugName name);

        TextureAtlasSprite reportMissingReference(String reference, ModelDebugName name);
    }

    /** A 1.21.11 SpriteGetter implementation overriding resolveSlot */
    public interface LegacySlotResolver {
        TextureAtlasSprite resolveSlot(TextureSlots slots, String slot, ModelDebugName name);
    }

    /** MaterialBaker.get of a 1.21.11 getter: its sprite for the id in the virtual model atlas, with the material's flag */
    public static Material.Baked bakerGet(LegacySpriteGetter getter, Material material, ModelDebugName name) {
        return new Material.Baked(getter.get(new SpriteId(BLOCK_OR_ITEM, material.sprite()), name), material.forceTranslucent());
    }

    /** MaterialBaker.reportMissingReference of a 1.21.11 getter */
    public static Material.Baked bakerReportMissingReference(LegacySpriteGetter getter, String reference, ModelDebugName name) {
        return new Material.Baked(getter.reportMissingReference(reference, name), false);
    }

    /** MaterialBaker.resolveSlot of a 1.21.11 getter overriding it: its sprite, with the slot material's flag */
    public static Material.Baked bakerResolveSlot(LegacySlotResolver getter, TextureSlots slots, String slot, ModelDebugName name) {
        Material m = slots.getMaterial(slot);
        return new Material.Baked(getter.resolveSlot(slots, slot, name), m != null && m.forceTranslucent());
    }
}
