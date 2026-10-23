package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityLoot;

/**
 * Mob's final 1.21.2 getLootTable: with no loot table of the mob's own, 1.21.1's final getLootTable asked the
 * overridable getDefaultLootTable, which 1.21.2 no longer has (CbEntityLoot#mobLootTable).
 */
@Mixin(class_1308.class)
public abstract class CbEntityLootMobMixin {

    @Shadow
    private Optional<class_5321<class_52>> field_6198;

    @Inject(method = "method_5991", at = @At("HEAD"), cancellable = true)
    private void cb$defaultLootTable(CallbackInfoReturnable<Optional<class_5321<class_52>>> cir) {
        if (this.field_6198.isPresent()) {
            return;
        }
        Optional<class_5321<class_52>> r = CbEntityLoot.mobLootTable((class_1297) (Object) this);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
