package top.dext.centurybridge.step.v1_20_3.compat.fapi.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * 1.20.2 Fabric API's FilteringStorage.FilteringStorageView (1.20.3 keeps it private), as FilteringStorage's
 * 1.20.2 exactView returned it (CbFapiTransfer.filteringExactView): the backing view, with extraction only
 * where the filter's canExtract allows it.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
final class CbFilteringStorageView implements StorageView {

    private final FilteringStorage filter;
    private final StorageView backingView;

    CbFilteringStorageView(FilteringStorage filter, StorageView backingView) {
        this.filter = filter;
        this.backingView = backingView;
    }

    @Override
    public long extract(Object resource, long maxAmount, TransactionContext transaction) {
        boolean allowed;
        try {
            allowed = (boolean) CbFapiTransfer.FilteringAccess.CAN_EXTRACT.invoke(filter, resource);
        } catch (Throwable t) {
            throw CbFapiTransfer.rethrow(t);
        }
        return allowed ? backingView.extract(resource, maxAmount, transaction) : 0;
    }

    @Override
    public boolean isResourceBlank() {
        return backingView.isResourceBlank();
    }

    @Override
    public Object getResource() {
        return backingView.getResource();
    }

    @Override
    public long getAmount() {
        return backingView.getAmount();
    }

    @Override
    public long getCapacity() {
        return backingView.getCapacity();
    }

    @Override
    public StorageView getUnderlyingView() {
        return backingView.getUnderlyingView();
    }
}
