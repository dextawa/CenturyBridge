package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** SkullBlockEntity's profile services and its by-name profile lookup (CbSkulls). */
@Mixin(net.minecraft.class_2631.class)
public interface CbSkullAccess {

    @Accessor("field_12089")
    static net.minecraft.class_3312 cb$profileCache() {
        throw new AssertionError();
    }

    @Accessor("field_12088")
    static MinecraftSessionService cb$sessionService() {
        throw new AssertionError();
    }

    /** fetchGameProfile(name): the cached profile with its textures, completed on the main thread */
    @Invoker("method_52580")
    static CompletableFuture<Optional<GameProfile>> cb$fetchGameProfile(String name) {
        throw new AssertionError();
    }
}
