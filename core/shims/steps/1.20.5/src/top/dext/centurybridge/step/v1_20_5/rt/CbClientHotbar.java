package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's Hotbar on 1.20.5 (s4client): 1.20.4's saved hotbar was a list of nine item stacks (a
 * ForwardingList over a NonNullList) that mods read and wrote in place before HotbarManager.save(); 1.20.5 keeps
 * each slot as its encoded form (a Dynamic) and decodes on load. The list comes back as a live view of those
 * slots (mixin/CbClientHotbarMixin): reading a slot decodes it, writing one encodes it in its place -- with the
 * current registries, as 1.20.5's own load and store use them -- so a write reaches the file HotbarManager saves.
 */
@Environment(EnvType.CLIENT)
public final class CbClientHotbar {

    private CbClientHotbar() {
    }

    /** the slots a saved hotbar has, as mixin/CbClientHotbarMixin gives them access */
    public interface Slots {
        int cb$slotCount();

        net.minecraft.class_1799 cb$slot(int index);

        void cb$setSlot(int index, net.minecraft.class_1799 stack);
    }

    /** 1.20.4 Hotbar.delegate(): the nine stacks, live */
    public static java.util.List<net.minecraft.class_1799> view(Slots slots) {
        return new java.util.AbstractList<>() {
            @Override
            public int size() {
                return slots.cb$slotCount();
            }

            @Override
            public net.minecraft.class_1799 get(int index) {
                java.util.Objects.checkIndex(index, this.size());
                return slots.cb$slot(index);
            }

            @Override
            public net.minecraft.class_1799 set(int index, net.minecraft.class_1799 stack) {
                java.util.Objects.checkIndex(index, this.size());
                // NonNullList.set refused null, as 1.20.4's list did
                java.util.Objects.requireNonNull(stack);
                net.minecraft.class_1799 old = slots.cb$slot(index);
                slots.cb$setSlot(index, stack);
                return old;
            }
        };
    }

    /** a slot decoded as 1.20.5's Hotbar.load decodes it (empty when it does not decode) */
    public static net.minecraft.class_1799 decode(com.mojang.serialization.Dynamic<?> slot) {
        return net.minecraft.class_1799.field_49266.parse(net.minecraft.class_6903.method_56622(slot, CbRegistries.current()))
            .resultOrPartial(e -> org.slf4j.LoggerFactory.getLogger(CbClientHotbar.class).warn("Could not parse hotbar item: {}", e))
            .orElse(net.minecraft.class_1799.field_8037);
    }

    /** a stack encoded as 1.20.5's Hotbar.storeFrom encodes it, as a slot over the given ops (absent when it does not encode) */
    public static java.util.Optional<net.minecraft.class_2520> encode(net.minecraft.class_1799 stack) {
        return net.minecraft.class_1799.field_49266.encodeStart(CbRegistries.current().method_57093(net.minecraft.class_2509.field_11560), stack)
            .resultOrPartial(e -> org.slf4j.LoggerFactory.getLogger(CbClientHotbar.class).warn("Could not encode hotbar item: {}", e));
    }

    /** 1.20.4 Hotbar.createTag(): each stack's 1.20.4 tag, in order */
    public static net.minecraft.class_2499 createTag(Slots slots) {
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        for (net.minecraft.class_1799 stack : view(slots)) {
            list.add(CbItemTags.save(stack, new net.minecraft.class_2487()));
        }
        return list;
    }

    /** 1.20.4 Hotbar.fromTag(tag): each slot read from the tag's compound at its index (an empty stack past the end) */
    public static void fromTag(Slots slots, net.minecraft.class_2499 tag) {
        java.util.List<net.minecraft.class_1799> items = view(slots);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, CbItemTags.of(tag.method_10602(i)));
        }
    }

    /** 1.20.4 Hotbar.isEmpty(): every stack empty */
    public static boolean isEmpty(Slots slots) {
        for (net.minecraft.class_1799 stack : view(slots)) {
            if (!stack.method_7960()) {
                return false;
            }
        }
        return true;
    }
}
