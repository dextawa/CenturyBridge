package top.dext.centurybridge.step.v1_20_3.compat.fapi.transfer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * 1.20.2 Fabric API transfer members gone from 1.20.3's.
 * <ul>
 * <li>ContainerItemContext.withInitial(stack) / withInitial(variant, amount): a context starting with those
 *     contents (CbInitialContentsContainerItemContext, 1.20.2's implementation).</li>
 * <li>Storage.simulateInsert / simulateExtract / exactView, default methods of 1.20.2's Storage: their
 *     bodies were StorageUtil.simulateInsert(this, ..) / StorageUtil.simulateExtract(this, ..) (both still in
 *     1.20.3) and null; FilteringStorage overrode exactView with its backing storage's view, filtered.
 *     A call answers what the receiver answered in 1.20.2: its own override when its class (a 1.20.2
 *     implementation) declares one, else the default's body; a super call answers the default's body.
 *     1.20.2's own Fabric API called none of them but FilteringStorage's exactView (on its backing
 *     storage, as here), so a 1.20.2 override is only ever asked through these calls.</li>
 * </ul>
 * A 1.20.2 override is found by name and descriptor on the receiver's class -- 1.20.3's Storage declares
 * none of these, so only a 1.20.2 class can -- whatever Fabric API base class it extends.
 */
public final class CbFapiTransfer {

    private static final MethodType SIMULATE = MethodType.methodType(long.class, Object.class, long.class, TransactionContext.class);
    private static final MethodType EXACT_VIEW = MethodType.methodType(StorageView.class, Object.class);

    private static final ClassValue<Optional<MethodHandle>> OWN_SIMULATE_INSERT = own("simulateInsert", SIMULATE);
    private static final ClassValue<Optional<MethodHandle>> OWN_SIMULATE_EXTRACT = own("simulateExtract", SIMULATE);
    private static final ClassValue<Optional<MethodHandle>> OWN_EXACT_VIEW = own("exactView", EXACT_VIEW);

    // ---------------------------------------------------------------- ContainerItemContext.withInitial

    /** ContainerItemContext.withInitial(stack): 1.20.2's withInitial(ItemVariant.of(stack), stack.getCount()) */
    public static ContainerItemContext withInitial(net.minecraft.class_1799 initialContent) {
        return withInitial(ItemVariant.of(initialContent), initialContent.method_7947());
    }

    /** ContainerItemContext.withInitial(variant, amount) */
    public static ContainerItemContext withInitial(ItemVariant initialVariant, long initialAmount) {
        StoragePreconditions.notNegative(initialAmount);
        return new CbInitialContentsContainerItemContext(initialVariant, initialAmount);
    }

    // ---------------------------------------------------------------- Storage

    /** storage.simulateInsert(resource, maxAmount, transaction) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static long simulateInsert(Storage storage, Object resource, long maxAmount, TransactionContext transaction) {
        Optional<MethodHandle> own = OWN_SIMULATE_INSERT.get(storage.getClass());
        if (own.isPresent()) {
            try {
                return (long) own.get().invoke(storage, resource, maxAmount, transaction);
            } catch (Throwable t) {
                throw rethrow(t);
            }
        }
        return StorageUtil.simulateInsert(storage, resource, maxAmount, transaction);
    }

    /** storage.simulateExtract(resource, maxAmount, transaction) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static long simulateExtract(Storage storage, Object resource, long maxAmount, TransactionContext transaction) {
        Optional<MethodHandle> own = OWN_SIMULATE_EXTRACT.get(storage.getClass());
        if (own.isPresent()) {
            try {
                return (long) own.get().invoke(storage, resource, maxAmount, transaction);
            } catch (Throwable t) {
                throw rethrow(t);
            }
        }
        return StorageUtil.simulateExtract(storage, resource, maxAmount, transaction);
    }

    /** storage.exactView(resource) */
    @SuppressWarnings("rawtypes")
    public static StorageView exactView(Storage storage, Object resource) {
        Optional<MethodHandle> own = OWN_EXACT_VIEW.get(storage.getClass());
        if (own.isPresent()) {
            try {
                return (StorageView) own.get().invoke(storage, resource);
            } catch (Throwable t) {
                throw rethrow(t);
            }
        }
        if (storage instanceof FilteringStorage filtering) {
            return filteringExactView(filtering, resource);
        }
        return null; // Storage's default: no view is quickly available
    }

    /** Storage.super.simulateInsert(..): the default's body */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static long superSimulateInsert(Storage storage, Object resource, long maxAmount, TransactionContext transaction) {
        return StorageUtil.simulateInsert(storage, resource, maxAmount, transaction);
    }

    /** Storage.super.simulateExtract(..): the default's body */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static long superSimulateExtract(Storage storage, Object resource, long maxAmount, TransactionContext transaction) {
        return StorageUtil.simulateExtract(storage, resource, maxAmount, transaction);
    }

    /** Storage.super.exactView(resource): the default's body */
    @SuppressWarnings("rawtypes")
    public static StorageView superExactView(Storage storage, Object resource) {
        return null;
    }

    // ---------------------------------------------------------------- FilteringStorage

    /** filteringStorage.exactView(resource): its own override, else FilteringStorage's (1.20.2's) */
    @SuppressWarnings("rawtypes")
    public static StorageView exactView(FilteringStorage storage, Object resource) {
        return exactView((Storage) storage, resource);
    }

    /** super.exactView(resource) in a FilteringStorage subclass: FilteringStorage's 1.20.2 body */
    @SuppressWarnings("rawtypes")
    public static StorageView superFilteringExactView(FilteringStorage storage, Object resource) {
        return filteringExactView(storage, resource);
    }

    /**
     * FilteringStorage.exactView in 1.20.2: the backing storage's view of the resource (asked as any
     * storage is, {@link #exactView(Storage, Object)}), wrapped so that extraction goes through the
     * filter's canExtract -- 1.20.2's FilteringStorageView, which 1.20.3 keeps private
     * (CbFilteringStorageView is the same view).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static StorageView filteringExactView(FilteringStorage storage, Object resource) {
        Storage backing;
        try {
            backing = (Storage) ((Supplier) FilteringAccess.BACKING.invoke(storage)).get();
        } catch (Throwable t) {
            throw rethrow(t);
        }
        StorageView view = exactView(backing, resource);
        return view == null ? null : new CbFilteringStorageView(storage, view);
    }

    /** FilteringStorage's protected backingStorage and canExtract */
    static final class FilteringAccess {
        static final MethodHandle BACKING;
        static final MethodHandle CAN_EXTRACT;

        static {
            try {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(FilteringStorage.class, MethodHandles.lookup());
                BACKING = lookup.findGetter(FilteringStorage.class, "backingStorage", Supplier.class);
                CAN_EXTRACT = lookup.findVirtual(FilteringStorage.class, "canExtract",
                    MethodType.methodType(boolean.class, Object.class));
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        private FilteringAccess() {
        }
    }

    // ---------------------------------------------------------------- helpers

    /** the method of that name and type a 1.20.2 class declares or inherits (public, as the interface's was) */
    private static ClassValue<Optional<MethodHandle>> own(String name, MethodType type) {
        return new ClassValue<>() {
            @Override
            protected Optional<MethodHandle> computeValue(Class<?> c) {
                Method m;
                try {
                    m = c.getMethod(name, type.parameterArray());
                } catch (NoSuchMethodException e) {
                    return Optional.empty();
                }
                if (m.getReturnType() != type.returnType()) {
                    return Optional.empty();
                }
                try {
                    m.setAccessible(true);
                    return Optional.of(MethodHandles.lookup().unreflect(m));
                } catch (IllegalAccessException | RuntimeException e) {
                    throw new IllegalStateException("CenturyBridge: cannot call " + c.getName() + "." + name
                        + " (a 1.20.2 Storage override)", e);
                }
            }
        };
    }

    static RuntimeException rethrow(Throwable t) {
        if (t instanceof RuntimeException r) {
            return r;
        }
        if (t instanceof Error e) {
            throw e;
        }
        return new IllegalStateException(t);
    }

    private CbFapiTransfer() {
    }
}
