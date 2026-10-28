package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.facade.TagManager;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/**
 * 1.21.1's TagManager apply step on 1.21.2's reload (ReloadableServerResources, class_5350).
 * <p>
 * 1.21.1's ReloadableServerResources made a TagManager of the composite registry access in its constructor and ran it
 * as its first reload listener; its apply step stored the loaded tags (method_40098), where Fabric API 1.21.1 and mods
 * written like it read them. 1.21.2 loads the tags before (TagLoader) and hands them to the constructor as pending tags
 * (field_52345, javap: putfield in the constructor; Fabric API 1.21.2 reads the same list at loadResources' HEAD).
 * At the constructor's RETURN -- inside loadResources, before the reload instance is made, so before every listener
 * prepares and applies -- a facade TagManager runs its apply step over those pending tags, so a converted 1.21.1 mixin
 * into it sees the reload's tags as it did on 1.21.1. 1.21.2's own path is unchanged: nothing it reads is replaced,
 * nothing is added to its listeners.
 */
@Mixin(net.minecraft.class_5350.class)
public abstract class CbRestTagResultsMixin {

    /** postponedTags: the tags this reload loaded, bound to the registries later (updateStaticRegistryTags) */
    @Shadow
    @Final
    private List<net.minecraft.class_2378.class_10106<?>> field_52345;

    /** fullRegistryHolder: made of the composite registry access in 1.21.2 as in 1.21.1 */
    @Shadow
    @Final
    private net.minecraft.class_9383.class_9385 field_49921;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$tagManagerApply(CallbackInfo ci) {
        TagManager.ofPending(CbRestServer.superHolderRegistries(field_49921), field_52345);
    }
}
