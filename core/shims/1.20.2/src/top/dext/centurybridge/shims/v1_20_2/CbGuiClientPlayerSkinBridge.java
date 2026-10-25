package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbGuiOverrides;

/**
 * AbstractClientPlayer's 1.20.1 skin getters, which 1.20.2 folded into getSkinTextures() (a PlayerSkin
 * record). Each is 1.20.1's body over the player's tab-list entry, read through 1.20.2's entry:
 * isCapeLoaded / isElytraLoaded = the entry exists; isSkinLoaded = it exists and holds a downloaded skin
 * (not the default it starts with); getSkinTextureLocation / getModelName = the entry's skin, or the
 * UUID's default skin without an entry; getCloakTextureLocation / getElytraTextureLocation = the entry's,
 * or none.
 *
 * In 1.20.1 the renderers asked these getters (player texture, model, arm, cape, elytra), so a subclass
 * that overrode one changed what was drawn; 1.20.2's renderers ask getSkinTextures(). For a class that
 * overrides any of them, getSkinTextures() answers from the getters, combined as 1.20.1's renderers
 * combined them -- the cape only while isCapeLoaded, the elytra only while isElytraLoaded. Players of
 * every other class get 1.20.2's value untouched.
 */
@Mixin(value = net.minecraft.class_742.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiClientPlayerSkinBridge implements top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.SkinGetters {

    @Shadow
    protected abstract net.minecraft.class_640 method_3123();

    private java.util.UUID cb$uuid() {
        return ((net.minecraft.class_1297) (Object) this).method_5667();
    }

    /** 1.20.1 isCapeLoaded() */
    public boolean method_3125() {
        return this.method_3123() != null;
    }

    /** 1.20.1 isSkinLoaded(): a tab-list entry whose skin has been downloaded */
    public boolean method_3127() {
        net.minecraft.class_640 entry = this.method_3123();
        return entry != null && !entry.method_52810().comp_1626()
            .equals(net.minecraft.class_1068.method_52854(entry.method_2966()).comp_1626());
    }

    /** 1.20.1 getSkinTextureLocation() */
    public net.minecraft.class_2960 method_3117() {
        net.minecraft.class_640 entry = this.method_3123();
        return entry == null ? net.minecraft.class_1068.method_4648(this.cb$uuid()).comp_1626() : entry.method_52810().comp_1626();
    }

    /** 1.20.1 getCloakTextureLocation() */
    public net.minecraft.class_2960 method_3119() {
        net.minecraft.class_640 entry = this.method_3123();
        return entry == null ? null : entry.method_52810().comp_1627();
    }

    /** 1.20.1 isElytraLoaded() */
    public boolean method_3126() {
        return this.method_3123() != null;
    }

    /** 1.20.1 getElytraTextureLocation() */
    public net.minecraft.class_2960 method_3122() {
        net.minecraft.class_640 entry = this.method_3123();
        return entry == null ? null : entry.method_52810().comp_1628();
    }

    /** 1.20.1 getModelName() */
    public String method_3121() {
        net.minecraft.class_640 entry = this.method_3123();
        return (entry == null ? net.minecraft.class_1068.method_4648(this.cb$uuid()) : entry.method_52810()).comp_1629().method_52856();
    }
}
