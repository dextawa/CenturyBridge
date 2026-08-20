package top.dext.centurybridge.shims.v1_20_4;

import net.minecraft.class_2960;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 collapsed the player's loose skin accessors into one SkinTextures
 * record (class_8685). The old getters are still fully expressible -- each is
 * one component of that record -- so they come back as overloads on
 * AbstractClientPlayerEntity rather than as tombstones.
 *
 * These were the single largest cause of mod breakage in the corpus: six of the
 * fifteen at-risk mods die here, and they only died because the ledger looked
 * the symbols up on class_746 (the subclass mods call through) while the
 * declaration lives on class_742.
 */
@Mixin(class_742.class)
public abstract class PlayerSkinBridge {

    /** 1.20.1 getSkinTexture(): now SkinTextures.texture(). */
    public class_2960 method_3117() {
        return ((class_742) (Object) this).method_52814().comp_1626();
    }

    /** 1.20.1 getModel(): now SkinTextures.model(), "slim" or "default". */
    public String method_3121() {
        return ((class_742) (Object) this).method_52814().comp_1911();
    }

    /** 1.20.1 getCapeTexture(): now SkinTextures.capeTexture(), may be null. */
    public class_2960 method_3119() {
        return ((class_742) (Object) this).method_52814().comp_1627();
    }

    /** 1.20.1 getElytraTexture(): now SkinTextures.elytraTexture(), may be null. */
    public class_2960 method_3122() {
        return ((class_742) (Object) this).method_52814().comp_1628();
    }
}
