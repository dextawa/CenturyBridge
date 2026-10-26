package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallEntityRepresentation;

/**
 * 1.21.11's ItemStack entity representation (class_1799.field_24093 and its four members), which 26.1 dropped: the
 * entity a stack is shown by -- the item frame holding it, the item entity carrying it, the item display showing
 * it. Like 1.21.11 it is not copied (copy() builds a new stack) and an empty stack answers none.
 * <p>
 * Vanilla's path sets it where 1.21.11 did (CbWallItemFrameRepresentationMixin, CbWallItemEntityRepresentationMixin,
 * CbWallItemDisplayRepresentationMixin): an item frame when its item changes and clears it when the frame drops a
 * map, an item entity when its item is synced, an item display when it updates its render state. 26.1's own code
 * never reads it (it asks the frame), so only what mods read goes through these members.
 */
@Mixin(value = ItemStack.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallItemStackRepresentationMixin implements CbWallEntityRepresentation {

    @Unique
    private Entity cb$entityRepresentation;

    @Shadow
    public abstract boolean isEmpty();

    /** 1.21.11 ItemStack.isFramed (class_1799.method_7961) */
    @CbVanillaReads("CbWallItemFrameRepresentationMixin: an item frame sets the representation this reads")
    public boolean isFramed() {
        return this.cb$entityRepresentation instanceof ItemFrame;
    }

    /** 1.21.11 ItemStack.setEntityRepresentation (class_1799.method_27320) */
    @CbVanillaReads("CbWallItemFrameRepresentationMixin, CbWallItemEntityRepresentationMixin, "
        + "CbWallItemDisplayRepresentationMixin: item frames, item entities and item displays set it here")
    @Override
    public void setEntityRepresentation(Entity entity) {
        if (!isEmpty()) {
            this.cb$entityRepresentation = entity;
        }
    }

    /** 1.21.11 ItemStack.getFrame (class_1799.method_7945) */
    @CbVanillaReads("CbWallItemFrameRepresentationMixin: an item frame asks it before setting itself")
    @Override
    public ItemFrame getFrame() {
        return this.cb$entityRepresentation instanceof ItemFrame ? (ItemFrame) getEntityRepresentation() : null;
    }

    /** 1.21.11 ItemStack.getEntityRepresentation (class_1799.method_27319) */
    @CbVanillaReads("CbWallItemFrameRepresentationMixin, CbWallItemEntityRepresentationMixin, "
        + "CbWallItemDisplayRepresentationMixin: what they set is answered here")
    public Entity getEntityRepresentation() {
        return !isEmpty() ? this.cb$entityRepresentation : null;
    }
}
