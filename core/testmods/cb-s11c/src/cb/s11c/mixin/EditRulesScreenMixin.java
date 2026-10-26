package cb.s11c.mixin;

import cb.s11c.S11cRules;
import cb.s11c.SubRows;
import java.util.List;
import net.minecraft.class_1928;
import net.minecraft.class_2561;
import net.minecraft.class_5235;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** check 5: on the screen's init the mod adds its two row subclasses to the rule list */
@Mixin(class_5235.class)
public class EditRulesScreenMixin {
    @Shadow
    private class_1928 field_24298;
    @Shadow
    private class_5235.class_5241 field_49903;

    @Inject(method = "method_25426", at = @At("TAIL"))
    private void cbS11c$subRows(CallbackInfo ci) {
        class_5235 self = (class_5235) (Object) this;
        SubRows.add(field_49903, new SubRows.SubBool(self, class_2561.method_43470("CB Sub Bool Row"), List.of(), "cbSubBool",
            field_24298.method_20746(S11cRules.SUB_BOOL)));
        SubRows.add(field_49903, new SubRows.SubInt(self, class_2561.method_43470("CB Sub Int Row"), List.of(), "cbSubInt",
            field_24298.method_20746(S11cRules.SUB_INT)));
    }
}
