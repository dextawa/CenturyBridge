package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.List;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinItemArmor;

/**
 * TagLoader.load for item tags: 1.20.4's DyeableLeatherItem items (rt/CbFinItemArmor.dyeableItems) join
 * minecraft:dyeable, the tag 1.20.5's dye recipe, cauldron washing, armor and horse-armor tints ask where
 * 1.20.4 asked "instanceof DyeableLeatherItem". Added as optional elements after the data packs' entries
 * (a pack that replaces the tag still gets them: in 1.20.4 dyeability was the item's, not data).
 */
@Mixin(net.minecraft.class_3503.class)
public abstract class CbFinItemDyeableTagMixin {

    @Shadow
    @Final
    private String field_15605;

    @Inject(method = "method_33174", at = @At("RETURN"))
    private void cbFin$dyeable(net.minecraft.class_3300 resources,
                               CallbackInfoReturnable<Map<net.minecraft.class_2960, List<net.minecraft.class_3503.class_5145>>> cir) {
        if (!"tags/items".equals(field_15605) && !"tags/item".equals(field_15605)) {
            return;
        }
        List<net.minecraft.class_2960> ids = CbFinItemArmor.dyeableItems();
        if (ids.isEmpty()) {
            return;
        }
        List<net.minecraft.class_3503.class_5145> entries = cir.getReturnValue()
            .computeIfAbsent(new net.minecraft.class_2960("minecraft", "dyeable"), k -> new java.util.ArrayList<>());
        for (net.minecraft.class_2960 id : ids) {
            entries.add(new net.minecraft.class_3503.class_5145(net.minecraft.class_3497.method_43942(id), "centurybridge (1.20.4 DyeableLeatherItem)"));
        }
    }
}
