package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 collapsed the player's loose skin accessors into one SkinTextures
 * record (net.minecraft.world.entity.player.PlayerSkin). The old getters are still fully expressible -- each is
 * one component of that record -- so they come back as overloads on
 * AbstractClientPlayerEntity rather than as tombstones.
 *
 * These were the single largest cause of mod breakage in the corpus: six of the
 * fifteen at-risk mods die here, and they only died because the ledger looked
 * the symbols up on net.minecraft.client.player.LocalPlayer (the subclass mods call through) while the
 * declaration lives on net.minecraft.client.player.AbstractClientPlayer.
 */
@Mixin(net.minecraft.client.player.AbstractClientPlayer.class)
public abstract class PlayerSkinBridge {

    /** 1.20.1 getSkinTexture(): now SkinTextures.body().texturePath() (26.2 asset-info rework). */
    public net.minecraft.resources.Identifier method_3117() {
        return ((net.minecraft.client.player.AbstractClientPlayer) (Object) this).getSkin().body().texturePath();
    }

    /** 1.20.1 getModel(): now the model enum's serialized name, "slim" or "default". */
    public String method_3121() {
        return ((net.minecraft.util.StringRepresentable) ((net.minecraft.client.player.AbstractClientPlayer) (Object) this).getSkin().model()).getSerializedName();
    }

    /** 1.20.1 getCapeTexture(): may be null both before and after the rework. */
    public net.minecraft.resources.Identifier method_3119() {
        net.minecraft.core.ClientAsset.Texture t = ((net.minecraft.client.player.AbstractClientPlayer) (Object) this).getSkin().cape();
        return t == null ? null : t.texturePath();
    }

    /** 1.20.1 getElytraTexture(): may be null both before and after the rework. */
    public net.minecraft.resources.Identifier method_3122() {
        net.minecraft.core.ClientAsset.Texture t = ((net.minecraft.client.player.AbstractClientPlayer) (Object) this).getSkin().elytra();
        return t == null ? null : t.texturePath();
    }
}
