package top.dext.centurybridge.shims.v1_21_11;

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

    /** 1.20.1 getSkinTexture(): now SkinTextures.body().texturePath() (1.21.11 asset-info rework). */
    public class_2960 method_3117() {
        return ((class_742) (Object) this).method_52814().comp_1626().comp_3627();
    }

    /** 1.20.1 getModel(): now the model enum's serialized name, "slim" or "default". */
    public String method_3121() {
        return ((net.minecraft.class_3542) ((class_742) (Object) this).method_52814().comp_1629()).method_15434();
    }

    /** 1.20.1 getCapeTexture(): may be null both before and after the rework. */
    public class_2960 method_3119() {
        net.minecraft.class_12079.class_12081 t = ((class_742) (Object) this).method_52814().comp_1627();
        return t == null ? null : t.comp_3627();
    }

    /** 1.20.1 getElytraTexture(): may be null both before and after the rework. */
    public class_2960 method_3122() {
        net.minecraft.class_12079.class_12081 t = ((class_742) (Object) this).method_52814().comp_1628();
        return t == null ? null : t.comp_3627();
    }
}
