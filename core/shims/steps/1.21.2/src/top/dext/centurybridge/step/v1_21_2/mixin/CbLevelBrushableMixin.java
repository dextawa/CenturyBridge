package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelBlockEntities;

/**
 * BrushableBlockEntity: 1.21.2's unpackLootTable is private; 1.21.1's was overridable and brush() called it. A 1.21.1
 * subclass's override is asked where 1.21.2's brush() unpacks, and the private method is reachable for 1.21.1's calls
 * (CbLevelBlockEntities).
 */
@Mixin(net.minecraft.class_8174.class)
public abstract class CbLevelBrushableMixin implements CbLevelBlockEntities.BrushableAccess {

    @Shadow
    abstract void method_49217(class_3218 level, class_1657 player, class_1799 brush);

    @Override
    public void cb$unpackLootTable(class_3218 level, class_1657 player, class_1799 brush) {
        this.method_49217(level, player, brush);
    }

    @Redirect(method = "method_49215", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_8174;method_49217(Lnet/minecraft/class_3218;Lnet/minecraft/class_1657;Lnet/minecraft/class_1799;)V"))
    private void cb$unpack(net.minecraft.class_8174 self, class_3218 level, class_1657 player, class_1799 brush) {
        if (self instanceof CbLevelBlockEntities.LegacyUnpackLootTable legacy) {
            legacy.method_49217(player);
        } else {
            this.method_49217(level, player, brush);
        }
    }
}
