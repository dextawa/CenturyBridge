package top.dext.centurybridge.step.v1_21_5.rt;

import com.google.common.hash.HashCode;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.class_10923;
import net.minecraft.class_10936;
import net.minecraft.class_10938;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2813;
import net.minecraft.class_5455;
import net.minecraft.class_6903;

/**
 * 1.21.4's ServerboundContainerClickPacket members on 1.21.5's record (containerId, stateId, short slotNum, byte
 * buttonNum, clickType, changedSlots, carriedItem), which carries the stacks as HashedStack (item, count and a hash
 * of each component), not ItemStacks. javap -c -p 1.21.4 / 1.21.5 class_2813, class_10938, class_636.
 */
public final class CbContainerClick {
    private CbContainerClick() {
    }

    /** getSlotNum(): the record's short slotNum, widened */
    public static int method_12192(class_2813 p) {
        return p.comp_3844();
    }

    /** getButtonNum(): the record's byte buttonNum, widened */
    public static int method_12193(class_2813 p) {
        return p.comp_3845();
    }

    /**
     * getCarriedItem(): the item and count the packet holds. 1.21.5 sends only a hash of each component, so the stack
     * has the item's default components (lost: what the client's stack had beyond them).
     */
    public static class_1799 method_12190(class_2813 p) {
        return stack(p.comp_3848());
    }

    /** getChangedSlots(): each slot's item and count, as getCarriedItem */
    public static Int2ObjectMap<class_1799> method_34678(class_2813 p) {
        Int2ObjectMap<class_1799> out = new Int2ObjectOpenHashMap<>();
        for (Int2ObjectMap.Entry<class_10938> e : p.comp_3847().int2ObjectEntrySet()) {
            out.put(e.getIntKey(), stack(e.getValue()));
        }
        return out;
    }

    private static class_1799 stack(class_10938 h) {
        return h instanceof class_10938.class_10939 a ? new class_1799(a.comp_3876(), a.comp_3877()) : class_1799.field_8037;
    }

    /**
     * new ServerboundContainerClickPacket(containerId, stateId, slotNum, buttonNum, clickType, carriedItem, changedSlots):
     * the stacks hashed as 1.21.5's client hashes them (MultiPlayerGameMode.handleInventoryMouseClick: HashedStack.create
     * with ClientPacketListener's generator, CRC32C over the registries' serialization of each component). Slot and
     * button are narrowed as 1.21.5's client sends them (short, byte).
     */
    public static class_2813 newPacket(int containerId, int stateId, int slotNum, int buttonNum, class_1713 clickType,
                                       class_1799 carried, Int2ObjectMap<class_1799> changed) {
        class_10936.class_10937 gen = generator();
        Int2ObjectMap<class_10938> hashed = new Int2ObjectOpenHashMap<>();
        for (Int2ObjectMap.Entry<class_1799> e : changed.int2ObjectEntrySet()) {
            hashed.put(e.getIntKey(), class_10938.method_68853(e.getValue(), gen));
        }
        return new class_2813(containerId, stateId, (short) slotNum, (byte) buttonNum, clickType, hashed,
            class_10938.method_68853(carried, gen));
    }

    private static class_10936.class_10937 generator() {
        class_5455 access = CbVariants.registries();
        if (access == null) {
            throw new IllegalStateException("1.21.5 hashes a container click's stacks with the world's registries and no world is loaded");
        }
        class_6903<HashCode> ops = access.method_57093(class_10923.field_58093);
        return component -> ((HashCode) component.method_57943(ops).getOrThrow(
            msg -> new IllegalArgumentException("Failed to hash " + component + ": " + msg))).asInt();
    }
}
