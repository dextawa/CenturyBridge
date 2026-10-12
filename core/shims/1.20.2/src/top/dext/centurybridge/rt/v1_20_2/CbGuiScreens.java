package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 AccessibilityOnboardingScreen(options): closing it (Continue, Escape) saved the options, cleared
 * the narrator and opened a fading TitleScreen that kept the onboarding screen's logo. 1.20.2's constructor
 * takes what to do after the save-and-clear as a Runnable; the 1.20.1 one gets the 1.20.1 ending -- for
 * `new AccessibilityOnboardingScreen(options)` and a subclass's super(options) alike (superCtorReshapes).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiScreens {

    /**
     * superCtorReshapes push: run when the onboarding screen closes, while it is still the current screen:
     * TitleScreen(fading, that screen's logo), 1.20.1's onClose target.
     */
    public static Runnable cbOnboardingDone() {
        return () -> {
            net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
            net.minecraft.class_437 current = mc.field_1755;
            net.minecraft.class_8020 logo = current instanceof net.minecraft.class_8032 onboarding
                ? ((top.dext.centurybridge.shims.v1_20_2.CbGuiOnboardingAccessor) onboarding).cb$logo()
                : new net.minecraft.class_8020(true);
            mc.method_1507(new net.minecraft.class_442(true, logo));
        };
    }

    private CbGuiScreens() {
    }
}
