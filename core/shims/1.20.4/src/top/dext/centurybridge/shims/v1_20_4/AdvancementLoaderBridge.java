package top.dext.centurybridge.shims.v1_20_4;

import top.dext.centurybridge.rt.v1_20_4.Trackers;

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
 * Rebuilds the advancement identity map on datapack (re)load, and restores the
 * legacy get-by-id shape (Advancement instead of AdvancementEntry).
 */
@Mixin(class_2989.class)
public abstract class AdvancementLoaderBridge {

    @Inject(method = "method_20724", at = @At("TAIL"))
    private void cb$track(Map<class_2960, com.google.gson.JsonElement> map,
                          class_3300 rm, class_3695 profiler, CallbackInfo ci) {
        Trackers.ADVANCEMENT_ENTRIES.clear();
        for (class_8779 entry : ((class_2989) (Object) this).method_12893()) {
            Trackers.ADVANCEMENT_ENTRIES.put(entry.comp_1920(), entry);
        }
    }

    public class_161 method_12896(class_2960 id) {
        class_8779 entry = ((class_2989) (Object) this).method_12896(id);
        return entry == null ? null : entry.comp_1920();
    }
}
