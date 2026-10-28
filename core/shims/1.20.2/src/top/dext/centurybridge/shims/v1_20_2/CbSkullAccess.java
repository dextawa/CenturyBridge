package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * SkullBlockEntity's by-name profile lookup (CbSkulls). Only the lookup: the profile cache / session service fields
 * 1.20.1's check read are gone from 1.20.3 (sessionService dropped, profileCache retyped), and this layer climbs on.
 */
@Mixin(net.minecraft.class_2631.class)
public interface CbSkullAccess {

    /** fetchGameProfile(name): the cached profile with its textures, completed on the main thread */
    @Invoker("method_52580")
    static CompletableFuture<Optional<GameProfile>> cb$fetchGameProfile(String name) {
        throw new AssertionError();
    }
}
