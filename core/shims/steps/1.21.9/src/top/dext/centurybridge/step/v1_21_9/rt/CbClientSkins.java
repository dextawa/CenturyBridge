package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1068;
import net.minecraft.class_1071;
import net.minecraft.class_8685;

/** 1.21.8's SkinManager lookups 1.21.9 removed, over 1.21.9's (client-only: SkinManager is). */
@Environment(EnvType.CLIENT)
public final class CbClientSkins {

    private CbClientSkins() {
    }

    /** SkinManager.lookupInsecure(profile): 1.21.9's lookup without the signature requirement */
    public static Supplier<class_8685> method_52858(class_1071 manager, GameProfile profile) {
        return manager.method_73544(profile, false);
    }

    /** SkinManager.getInsecureSkin(profile, fallback): the loaded skin if there is one, else the fallback (1.21.8's body) */
    public static class_8685 method_72180(class_1071 manager, GameProfile profile, class_8685 fallback) {
        return manager.method_52863(profile).getNow(Optional.empty()).orElse(fallback);
    }

    /** SkinManager.getInsecureSkin(profile): the loaded skin, else the profile's default skin (1.21.8's body) */
    public static class_8685 method_52862(class_1071 manager, GameProfile profile) {
        class_8685 skin = method_72180(manager, profile, null);
        return skin != null ? skin : class_1068.method_52854(profile);
    }
}
