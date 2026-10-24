package top.dext.centurybridge.step.v1_20_3.rt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;

/** authlib 5.0.47's MinecraftSessionService.getTextures shape, which a converted mod's session service still declares */
public interface CbLibSessionOld {
    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure)
        throws InsecurePublicKeyException;
}
