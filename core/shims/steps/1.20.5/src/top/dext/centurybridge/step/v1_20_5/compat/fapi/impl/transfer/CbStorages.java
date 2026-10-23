package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;

/**
 * Fabric API 1.20.4's NBT methods of the single-variant storages, which 1.20.5 replaced by registry-aware
 * ones over the variants' codecs: {@code writeNbt(nbt)} wrote {variant: the 1.20.4 variant NBT, amount},
 * {@code readNbt(nbt)} read it back (CbVariants' 1.20.4 formats), so a storage saved by 1.20.4 loads and
 * a converted mod reads what it writes. A mod storage overriding one of them is reached through its own
 * method ({@link LegacyWrite} / {@link LegacyRead}, added by adapter hooks); the {@code super*} statics are
 * the 1.20.4 bodies (superRedirects).
 */
public final class CbStorages {

    private CbStorages() {
    }

    /** a mod storage class declaring 1.20.4's SingleVariantStorage.writeNbt(nbt) */
    public interface LegacyWrite {
        void writeNbt(net.minecraft.class_2487 nbt);
    }

    /** a mod storage class declaring 1.20.4's SingleItemStorage / SingleFluidStorage.readNbt(nbt) */
    public interface LegacyRead {
        void readNbt(net.minecraft.class_2487 nbt);
    }

    @SuppressWarnings("rawtypes")
    public static void writeNbt(SingleVariantStorage storage, net.minecraft.class_2487 nbt) {
        if (storage instanceof LegacyWrite own) {
            own.writeNbt(nbt);
        } else {
            superWriteNbt(storage, nbt);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void superWriteNbt(SingleVariantStorage storage, net.minecraft.class_2487 nbt) {
        nbt.method_10566("variant", CbVariants.toNbt(storage.variant));
        nbt.method_10544("amount", storage.amount);
    }

    public static void readNbt(SingleItemStorage storage, net.minecraft.class_2487 nbt) {
        if (storage instanceof LegacyRead own) {
            own.readNbt(nbt);
        } else {
            superReadNbt(storage, nbt);
        }
    }

    public static void superReadNbt(SingleItemStorage storage, net.minecraft.class_2487 nbt) {
        storage.variant = CbVariants.itemFromNbt(nbt.method_10562("variant"));
        storage.amount = nbt.method_10537("amount");
    }

    public static void readNbt(SingleFluidStorage storage, net.minecraft.class_2487 nbt) {
        if (storage instanceof LegacyRead own) {
            own.readNbt(nbt);
        } else {
            superReadNbt(storage, nbt);
        }
    }

    public static void superReadNbt(SingleFluidStorage storage, net.minecraft.class_2487 nbt) {
        storage.variant = CbVariants.fluidFromNbt(nbt.method_10562("variant"));
        storage.amount = nbt.method_10537("amount");
    }
}
