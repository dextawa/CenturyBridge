package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientHotbar;

/**
 * Hotbar as 1.20.4's list of nine stacks (rt/CbClientHotbar): the List it was (a ForwardingList), its
 * delegate(), isEmpty(), createTag() and fromTag(tag), over a live view of 1.20.5's encoded slots. A write
 * encodes the stack into its slot, as 1.20.5's storeFrom encodes the inventory's.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_748.class)
public abstract class CbClientHotbarMixin implements CbClientHotbar.Slots, java.util.List<net.minecraft.class_1799> {

    @Shadow
    private java.util.List<com.mojang.serialization.Dynamic<?>> field_48947;

    @Shadow
    @Final
    private static com.mojang.serialization.Dynamic<?> field_48946;

    @Override
    public int cb$slotCount() {
        return this.field_48947.size();
    }

    @Override
    public net.minecraft.class_1799 cb$slot(int index) {
        return CbClientHotbar.decode(this.field_48947.get(index));
    }

    @Override
    public void cb$setSlot(int index, net.minecraft.class_1799 stack) {
        com.mojang.serialization.Dynamic<?> slot = CbClientHotbar.encode(stack)
            .<com.mojang.serialization.Dynamic<?>>map(tag -> new com.mojang.serialization.Dynamic<>(net.minecraft.class_2509.field_11560, tag))
            .orElse(field_48946);
        java.util.List<com.mojang.serialization.Dynamic<?>> slots = new java.util.ArrayList<>(this.field_48947);
        slots.set(index, slot);
        this.field_48947 = slots;
    }

    /** 1.20.4 Hotbar.delegate(): the nine stacks, live */
    protected java.util.List<net.minecraft.class_1799> delegate() {
        return CbClientHotbar.view(this);
    }

    /** 1.20.4 Hotbar.createTag() */
    public net.minecraft.class_2499 method_3153() {
        return CbClientHotbar.createTag(this);
    }

    /** 1.20.4 Hotbar.fromTag(tag) */
    public void method_3152(net.minecraft.class_2499 tag) {
        CbClientHotbar.fromTag(this, tag);
    }

    // ---------------------------------------------------------------- 1.20.4's ForwardingList over delegate()

    @Override
    public boolean isEmpty() {
        return CbClientHotbar.isEmpty(this);
    }

    @Override
    public int size() {
        return this.delegate().size();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate().contains(o);
    }

    @Override
    public java.util.Iterator<net.minecraft.class_1799> iterator() {
        return this.delegate().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.delegate().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.delegate().toArray(a);
    }

    @Override
    public boolean add(net.minecraft.class_1799 stack) {
        return this.delegate().add(stack);
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate().remove(o);
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        return this.delegate().containsAll(c);
    }

    @Override
    public boolean addAll(java.util.Collection<? extends net.minecraft.class_1799> c) {
        return this.delegate().addAll(c);
    }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends net.minecraft.class_1799> c) {
        return this.delegate().addAll(index, c);
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        return this.delegate().removeAll(c);
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        return this.delegate().retainAll(c);
    }

    @Override
    public void clear() {
        this.delegate().clear();
    }

    @Override
    public net.minecraft.class_1799 get(int index) {
        return this.delegate().get(index);
    }

    @Override
    public net.minecraft.class_1799 set(int index, net.minecraft.class_1799 stack) {
        return this.delegate().set(index, stack);
    }

    @Override
    public void add(int index, net.minecraft.class_1799 stack) {
        this.delegate().add(index, stack);
    }

    @Override
    public net.minecraft.class_1799 remove(int index) {
        return this.delegate().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate().lastIndexOf(o);
    }

    @Override
    public java.util.ListIterator<net.minecraft.class_1799> listIterator() {
        return this.delegate().listIterator();
    }

    @Override
    public java.util.ListIterator<net.minecraft.class_1799> listIterator(int index) {
        return this.delegate().listIterator(index);
    }

    @Override
    public java.util.List<net.minecraft.class_1799> subList(int from, int to) {
        return this.delegate().subList(from, to);
    }
}
