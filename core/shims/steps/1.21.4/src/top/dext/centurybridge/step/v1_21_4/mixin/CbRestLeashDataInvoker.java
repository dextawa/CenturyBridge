package top.dext.centurybridge.step.v1_21_4.mixin;

import com.mojang.datafixers.util.Either;
import java.util.UUID;
import net.minecraft.class_2338;
import net.minecraft.class_9817;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Leashable.LeashData(Either) (package-private), for 1.21.3's readLeashData body (CbRestLeash). */
@Mixin(class_9817.class_9818.class)
public interface CbRestLeashDataInvoker {

    @Invoker("<init>")
    static class_9817.class_9818 cb$create(Either<UUID, class_2338> delayed) {
        throw new AssertionError();
    }
}
