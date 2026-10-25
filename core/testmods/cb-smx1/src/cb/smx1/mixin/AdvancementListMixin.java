package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AdvancementList.add(Map<ResourceLocation, Advancement.Builder>) (class_163.method_711), which the server's
 * advancement manager calls with every loaded advancement (1.20.2: AdvancementTree.addAll(Collection<AdvancementHolder>)).
 */
@Mixin(net.minecraft.class_163.class)
public abstract class AdvancementListMixin {
    @Inject(method = "method_711(Ljava/util/Map;)V", at = @At("RETURN"), remap = false)
    private void cbsmx1$added(CallbackInfo ci) {
        Driver.count("advancements-add");
    }
}
