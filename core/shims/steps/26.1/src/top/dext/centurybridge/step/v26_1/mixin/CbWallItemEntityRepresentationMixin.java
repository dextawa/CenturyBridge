package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.rt.CbWallEntityRepresentation;

/**
 * 1.21.11's ItemEntity set itself as its item's entity representation (CbWallItemStackRepresentationMixin) when its
 * DATA_ITEM was synced: its onSyncedDataUpdated(accessor) override, which 26.1 dropped (ItemEntity inherits Entity's),
 * is given back with that body: Entity's, then the representation.
 */
@Mixin(ItemEntity.class)
public abstract class CbWallItemEntityRepresentationMixin extends Entity {

    @Shadow
    @Final
    private static EntityDataAccessor<ItemStack> DATA_ITEM;

    private CbWallItemEntityRepresentationMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    /** 1.21.11 ItemEntity.onSyncedDataUpdated (class_1542.method_5674) */
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_ITEM.equals(accessor)) {
            ((CbWallEntityRepresentation) (Object) getItem()).setEntityRepresentation(this);
        }
    }
}
