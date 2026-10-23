package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 ServerLoginNetworkHandler methods a mod subclass overrides (adapterHooks), which 1.20.2 made
 * private or static; CbNetLoginOverridesBridge calls the override where vanilla 1.20.2 takes the step
 * the 1.20.1 method stood for. Each adapter is one method, so a subclass overriding either gets it.
 */
public final class CbNetLegacyLogin {

    /** acceptPlayer(): 1.20.2's verifyLoginAndFinishConnectionSetup(profile), which tick() calls */
    public interface Accept {

        void method_14384();
    }

    /** toOfflineProfile(profile): 1.20.2's static createOfflineProfile(name), which handleHello calls */
    public interface OfflineProfile {

        com.mojang.authlib.GameProfile method_14375(com.mojang.authlib.GameProfile profile);
    }

    private CbNetLegacyLogin() {
    }
}
