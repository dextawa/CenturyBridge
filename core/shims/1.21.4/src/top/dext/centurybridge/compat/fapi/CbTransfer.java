package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.class_1799;

/**
 * fabric-transfer-api-v1 members removed between 1.20.1 and 1.21.4 (instanceRedirects, receiver first):
 * Storage.simulateInsert/simulateExtract were default methods, now the StorageUtil statics they wrapped;
 * Storage.exactView(resource) was a linear search over the views; ContainerItemContext.withInitial(stack)
 * built a context over one mutable slot seeded with the stack (withConstant is NOT the same: it refuses
 * mutation).
 */
public final class CbTransfer {

    public static <T> long simulateInsert(Storage<T> self, T resource, long maxAmount, TransactionContext tx) {
        return StorageUtil.simulateInsert(self, resource, maxAmount, tx);
    }

    public static <T> long simulateExtract(Storage<T> self, T resource, long maxAmount, TransactionContext tx) {
        return StorageUtil.simulateExtract(self, resource, maxAmount, tx);
    }

    public static <T> StorageView<T> exactView(Storage<T> self, T resource) {
        for (StorageView<T> view : self) {
            if (view.getResource().equals(resource)) {
                return view;
            }
        }
        return null;
    }

    public static <T> StorageView<T> exactView(CombinedStorage<T, ?> self, T resource) {
        return exactView((Storage<T>) self, resource);
    }

    public static ContainerItemContext withInitial(class_1799 initial) {
        return ContainerItemContext.ofSingleSlot(new SingleStackStorage() {
            private class_1799 stack = initial;

            @Override
            protected class_1799 getStack() {
                return stack;
            }

            @Override
            protected void setStack(class_1799 stack) {
                this.stack = stack;
            }
        });
    }

    private CbTransfer() {
    }
}
