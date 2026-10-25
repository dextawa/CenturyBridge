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
 * Rebuilds the advancement identity map on datapack (re)load, and restores the
 * legacy get-by-id shape (Advancement instead of AdvancementEntry).
 */
@Mixin(value = class_2989.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class AdvancementLoaderBridge {

    public class_161 method_12896(class_2960 id) {
        class_8779 entry = ((class_2989) (Object) this).method_12896(id);
        return entry == null ? null : entry.comp_1920();
    }
}
