package top.dext.centurybridge.step.v1_20_3.compat.fapi.transfer;

import java.util.Collections;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * 1.20.2 Fabric API's InitialContentsContainerItemContext (gone from 1.20.3's with
 * ContainerItemContext.withInitial), ported as it was: a context whose main slot starts with the given
 * contents and can then be changed like any storage (a SingleVariantStorage of unlimited capacity), with
 * no additional slots, whose overflow insertion accepts -- and discards -- everything.
 */
public final class CbInitialContentsContainerItemContext implements ContainerItemContext {

    private final SingleVariantStorage<ItemVariant> backingSlot = new SingleVariantStorage<>() {
        @Override
        protected ItemVariant getBlankVariant() {
            return ItemVariant.blank();
        }

        @Override
        protected long getCapacity(ItemVariant variant) {
            return Long.MAX_VALUE;
        }
    };

    public CbInitialContentsContainerItemContext(ItemVariant initialVariant, long initialAmount) {
        backingSlot.variant = initialVariant;
        backingSlot.amount = initialAmount;
    }

    @Override
    public SingleSlotStorage<ItemVariant> getMainSlot() {
        return backingSlot;
    }

    @Override
    public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
        StoragePreconditions.notBlankNotNegative(itemVariant, maxAmount);
        return maxAmount;
    }

    @Override
    public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
        return Collections.emptyList();
    }
}
