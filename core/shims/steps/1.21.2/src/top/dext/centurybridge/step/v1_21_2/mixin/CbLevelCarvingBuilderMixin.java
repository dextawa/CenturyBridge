package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2922;
import net.minecraft.class_5485;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelCarving;

/** BiomeGenerationSettings.PlainBuilder's LIQUID carvers, handed to the settings build() makes (CbLevelCarving) */
@Mixin(class_5485.class_7868.class)
public abstract class CbLevelCarvingBuilderMixin implements CbLevelCarving.BuilderState {

    @Unique
    private final List<class_6880<class_2922<?>>> cb$liquidCarvers = new ArrayList<>();

    @Override
    public List<class_6880<class_2922<?>>> cb$liquidCarvers() {
        return this.cb$liquidCarvers;
    }

    @Inject(method = "method_46671", at = @At("RETURN"))
    private void cb$built(CallbackInfoReturnable<class_5485> cir) {
        CbLevelCarving.built((class_5485.class_7868) (Object) this, cir.getReturnValue());
    }
}
