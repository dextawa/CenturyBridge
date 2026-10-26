package cb.s203.mixin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.20.2 SkullBlockEntity's profile cache and session service, read and written as a 1.20.2 mod does */
@Mixin(net.minecraft.class_2631.class)
public interface SkullAccessor {

    @Accessor("field_12089")
    static net.minecraft.class_3312 cbs203$profileCache() {
        throw new AssertionError();
    }

    @Accessor("field_12089")
    static void cbs203$setProfileCache(net.minecraft.class_3312 cache) {
        throw new AssertionError();
    }

    @Accessor("field_12088")
    static MinecraftSessionService cbs203$sessionService() {
        throw new AssertionError();
    }
}
