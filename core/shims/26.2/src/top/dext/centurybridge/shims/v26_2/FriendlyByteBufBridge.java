package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: writeNbt narrowed-parameter overload.
 * Old: writeNbt(CompoundTag); new: writeNbt(Tag). Work order: L2 x44 mods.
 */
@Mixin(net.minecraft.network.FriendlyByteBuf.class)
public abstract class FriendlyByteBufBridge {

    public net.minecraft.network.FriendlyByteBuf writeNbt(net.minecraft.nbt.CompoundTag nbt) {
        return ((net.minecraft.network.FriendlyByteBuf) (Object) this).writeNbt((net.minecraft.nbt.Tag) nbt);
    }

    /** removed unlimited-size NBT read; delegates to the standard read (size-cap approximation) */
    public net.minecraft.nbt.CompoundTag method_30617() {
        return ((net.minecraft.network.FriendlyByteBuf) (Object) this).readNbt();
    }
}
