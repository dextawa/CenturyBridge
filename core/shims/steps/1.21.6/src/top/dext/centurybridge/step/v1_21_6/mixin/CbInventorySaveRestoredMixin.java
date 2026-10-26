package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_6.rt.CbSaveData;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's Inventory.save(ListTag) / load(ListTag), restored on Inventory with 1.21.5's bodies (javap): a compound per
 * non-empty main slot with its "Slot" byte, saved with the player's registries; load clears the main slots and sets
 * each entry's stack (an unparsable one as empty) at its slot when in range. 1.21.6's save / load(typed lists) stand.
 * Priority below every mod's.
 */
@Mixin(value = class_1661.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbInventorySaveRestoredMixin implements CbSaveData.LegacyInventory {

    @Shadow
    @Final
    private class_2371<class_1799> field_7547;

    @Shadow
    @Final
    public class_1657 field_7546;

    @CbVanillaReads("CbPlayerSaveRouteMixin: Player.addAdditionalSaveData, after 1.21.6's save, when a converted mod mixed it "
        + "(CbMixedMembers) or the inventory is a subclass: its list is stored as \"Inventory\", as 1.21.5's save did")
    @Override
    public class_2499 method_7384(class_2499 list) {
        for (int i = 0; i < this.field_7547.size(); i++) {
            if (!this.field_7547.get(i).method_7960()) {
                class_2487 tag = new class_2487();
                tag.method_10567("Slot", (byte) i);
                list.add(top.dext.centurybridge.step.v1_21_6.rt.CbItemStacks.method_57376(this.field_7547.get(i), this.field_7546.method_56673(), tag));
            }
        }
        return list;
    }

    @CbVanillaReads("CbPlayerSaveRouteMixin: Player.readAdditionalSaveData, after 1.21.6's load, when a converted mod mixed it "
        + "(CbMixedMembers) or the inventory is a subclass: it reads the saved \"Inventory\" list again, as 1.21.5's load did")
    @Override
    public void method_7397(class_2499 list) {
        this.field_7547.clear();
        for (int i = 0; i < list.size(); i++) {
            class_2487 tag = list.method_68582(i);
            int slot = tag.method_68562("Slot", (byte) 0) & 255;
            class_1799 stack = top.dext.centurybridge.step.v1_21_6.rt.CbItemStacks.method_57360(this.field_7546.method_56673(), tag).orElse(class_1799.field_8037);
            if (slot < this.field_7547.size()) {
                ((class_1661) (Object) this).method_5447(slot, stack);
            }
        }
    }
}
