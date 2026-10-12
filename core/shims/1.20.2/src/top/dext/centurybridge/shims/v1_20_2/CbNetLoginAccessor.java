package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_3248;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * ServerLoginNetworkHandler's 1.20.2 acceptance step (verifyLoginAndFinishConnectionSetup, private),
 * the profile it accepts, and its offline-profile factory (protected static), for 1.20.1's
 * acceptPlayer() and toOfflineProfile(profile) (CbNet.method_14384 / method_14375).
 */
@Mixin(class_3248.class)
public interface CbNetLoginAccessor {

    @Accessor("field_45029")
    GameProfile cb$authenticatedProfile();

    @Invoker("method_52419")
    void cb$verifyLoginAndFinishConnectionSetup(GameProfile profile);

    @Invoker("method_52418")
    static GameProfile cb$createOfflineProfile(String name) {
        throw new AssertionError();
    }
}
