package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** UserCache's executor: 1.20.1 ran findByNameAsync consumers on it (CbServer.super_method_37156). */
@Mixin(net.minecraft.class_3312.class)
public interface UserCacheExecutorAccessor {

    @Accessor("field_33861")
    java.util.concurrent.Executor cb$executor();
}
