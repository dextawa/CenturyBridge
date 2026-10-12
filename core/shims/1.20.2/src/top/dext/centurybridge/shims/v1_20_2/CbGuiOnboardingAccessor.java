package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The onboarding screen's logo renderer, which 1.20.1 handed on to the title screen it closed to (CbGuiScreens). */
@Mixin(net.minecraft.class_8032.class)
public interface CbGuiOnboardingAccessor {

    @Accessor("field_41841")
    net.minecraft.class_8020 cb$logo();
}
