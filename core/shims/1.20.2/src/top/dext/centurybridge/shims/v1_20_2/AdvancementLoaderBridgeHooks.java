package top.dext.centurybridge.shims.v1_20_2;

import top.dext.centurybridge.rt.v1_20_2.Trackers;

import java.util.Map;
import net.minecraft.class_161;
import net.minecraft.class_2960;
import net.minecraft.class_2989;
import net.minecraft.class_3300;
import net.minecraft.class_3695;
import net.minecraft.class_8779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The injectors into 1.20.2's own methods that go with {@link AdvancementLoaderBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_2989.class)
public abstract class AdvancementLoaderBridgeHooks {

    @Inject(method = "method_20724", at = @At("TAIL"))
    private void cb$track(Map<class_2960, com.google.gson.JsonElement> map,
                          class_3300 rm, class_3695 profiler, CallbackInfo ci) {
        Trackers.ADVANCEMENT_ENTRIES.clear();
        for (class_8779 entry : ((class_2989) (Object) this).method_12893()) {
            Trackers.ADVANCEMENT_ENTRIES.put(entry.comp_1920(), entry);
        }
    }
}
