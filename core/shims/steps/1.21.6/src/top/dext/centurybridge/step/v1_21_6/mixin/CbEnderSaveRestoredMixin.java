package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1277;
import net.minecraft.class_1730;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's PlayerEnderChestContainer.fromTag / createTag overrides (with "Slot" bytes), restored with 1.21.5's bodies
 * (javap), overriding the restored SimpleContainer members (CbContainerSaveRestoredMixin). 1.21.6's fromSlots /
 * storeAsSlots stand. Priority below every mod's.
 */
@Mixin(value = class_1730.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEnderSaveRestoredMixin extends class_1277 {

    private CbEnderSaveRestoredMixin(int size) {
        super(size);
    }

    @CbVanillaReads("CbPlayerSaveRouteMixin: Player.readAdditionalSaveData, after 1.21.6's load, when a converted mod mixed it "
        + "(CbMixedMembers) or the container is a subclass: it reads the saved \"EnderItems\" list again, as 1.21.5's did")
    public void method_7659(class_2499 list, class_7225.class_7874 provider) {
        for (int i = 0; i < this.method_5439(); i++) {
            this.method_5447(i, class_1799.field_8037);
        }
        for (int i = 0; i < list.size(); i++) {
            class_2487 tag = list.method_68582(i);
            int slot = tag.method_68562("Slot", (byte) 0) & 255;
            if (slot >= 0 && slot < this.method_5439()) {
                this.method_5447(slot, top.dext.centurybridge.step.v1_21_6.rt.CbItemStacks.method_57360(provider, tag).orElse(class_1799.field_8037));
            }
        }
    }

    @CbVanillaReads("CbPlayerSaveRouteMixin: Player.addAdditionalSaveData, after 1.21.6's save, when a converted mod mixed it "
        + "(CbMixedMembers) or the container is a subclass: its list is stored as \"EnderItems\", as 1.21.5's save did")
    public class_2499 method_7660(class_7225.class_7874 provider) {
        class_2499 list = new class_2499();
        for (int i = 0; i < this.method_5439(); i++) {
            class_1799 stack = this.method_5438(i);
            if (!stack.method_7960()) {
                class_2487 tag = new class_2487();
                tag.method_10567("Slot", (byte) i);
                list.add(top.dext.centurybridge.step.v1_21_6.rt.CbItemStacks.method_57376(stack, provider, tag));
            }
        }
        return list;
    }
}
