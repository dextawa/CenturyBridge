package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1277;
import net.minecraft.class_1799;
import net.minecraft.class_2499;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbSaveData;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's SimpleContainer.fromTag(ListTag, Provider) / createTag(Provider), restored on SimpleContainer with 1.21.5's
 * bodies (javap): a 1.21.5 call or super call links to them, a 1.21.5 subclass overrides them, a 1.21.5 mixin anchors
 * in them. 1.21.6 keeps the same names with typed-list descriptors (fromItems / storeAsItemList), which stand.
 * Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1277.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbContainerSaveRestoredMixin implements CbSaveData.LegacyContainer {

    @CbVanillaReads("CbInventoryCarrierRouteMixin: InventoryCarrier.readInventoryFromTag, after 1.21.6's read, when a converted "
        + "mod mixed it (CbMixedMembers) or the container is a subclass: it reads the saved \"Inventory\" list again, as "
        + "1.21.5's readInventoryFromTag did")
    @Override
    public void method_7659(class_2499 list, class_7225.class_7874 provider) {
        class_1277 self = (class_1277) (Object) this;
        self.method_5448();
        list.method_68589().flatMap((net.minecraft.class_2487 tag) -> top.dext.centurybridge.step.v1_21_6.rt.CbItemStacks.method_57360(provider, tag).stream()).forEach(self::method_5491);
    }

    @CbVanillaReads("CbInventoryCarrierRouteMixin: InventoryCarrier.writeInventoryToTag, after 1.21.6's write, when a converted "
        + "mod mixed it (CbMixedMembers) or the container is a subclass: its list is stored as \"Inventory\", as 1.21.5's "
        + "writeInventoryToTag did")
    @Override
    public class_2499 method_7660(class_7225.class_7874 provider) {
        class_1277 self = (class_1277) (Object) this;
        class_2499 list = new class_2499();
        for (int i = 0; i < self.method_5439(); i++) {
            class_1799 stack = self.method_5438(i);
            if (!stack.method_7960()) {
                list.add(top.dext.centurybridge.step.v1_21_6.rt.CbItemStacks.method_57358(stack, provider));
            }
        }
        return list;
    }
}
