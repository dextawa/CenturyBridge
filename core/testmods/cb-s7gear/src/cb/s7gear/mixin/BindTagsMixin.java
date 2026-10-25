package cb.s7gear.mixin;

import java.util.List;
import java.util.Map;
import net.minecraft.class_2370;
import net.minecraft.class_2960;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * a 1.21.1 mod mixin on MappedRegistry.bindTags(map): it records what vanilla binds for the item registry
 * (GearTestMod.tagReload) and adds a tag of its own, cbs7gear:mixin_bound = [stick]
 */
@Mixin(class_2370.class)
public abstract class BindTagsMixin {

    @Inject(method = "method_40257(Ljava/util/Map;)V", at = @At("HEAD"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void cbs7gear$bindTags(Map tags, CallbackInfo ci) {
        if ((Object) this != class_7923.field_41178) {
            return;
        }
        boolean planks = tags.containsKey(class_6862.method_40092(class_7924.field_41197, class_2960.method_60656("planks")));
        cb.s7gear.GearTestMod.SEEN_BINDS.add(tags.size() + (planks ? " planks" : " no-planks"));
        class_6862 mine = class_6862.method_40092(class_7924.field_41197, class_2960.method_60655("cbs7gear", "mixin_bound"));
        tags.put(mine, List.<class_6880>of(class_7923.field_41178.method_47983(net.minecraft.class_1802.field_8600)));
    }
}
