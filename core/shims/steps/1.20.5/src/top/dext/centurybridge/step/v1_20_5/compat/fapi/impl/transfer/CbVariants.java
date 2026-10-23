package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.transfer;

import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemTags;

/**
 * Fabric API 1.20.4's NBT side of the transfer API on 1.20.5's components. A 1.20.4 variant carried NBT:
 * an item variant the item stack's tag, a fluid variant mod data. 1.20.5 variants carry component changes:
 * <ul>
 * <li>an item variant's NBT is the 1.20.4 tag of its stack, kept on the stack's components the way 1.20.4
 *     code sees an item stack's tag (CbItemTags: read through the componentization fix backwards, written
 *     through the game's own upgrade), so {@code of(item, tag)} and {@code getNbt()} round-trip as 1.20.4's
 *     did and vanilla components read the same data;</li>
 * <li>a fluid variant's NBT, which no vanilla component speaks for, is its minecraft:custom_data;</li>
 * <li>a variant of another kind answers its custom data the same way.</li>
 * </ul>
 * The 1.20.4 NBT and packet formats ({item|fluid, tag} / present + raw id + tag) are written and read as
 * 1.20.4's ItemVariantImpl / FluidVariantImpl did, so what a 1.20.4 mod saved or sent reads back. A mod's own
 * 1.20.4 variant class ({@link Legacy}, added by an adapter hook to a class declaring getNbt()) answers with
 * its own methods; the {@code super*} statics are the 1.20.4 default bodies (superRedirects).
 */
public final class CbVariants {

    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/fabric-transfer-api-v1");

    private CbVariants() {
    }

    /** a mod's own 1.20.4 TransferVariant implementation (its 1.20.4 methods; the defaults are 1.20.4's) */
    public interface Legacy {
        net.minecraft.class_2487 getNbt();

        net.minecraft.class_2487 toNbt();

        void toPacket(net.minecraft.class_2540 buf);

        default boolean hasNbt() {
            return getNbt() != null;
        }

        default boolean nbtMatches(net.minecraft.class_2487 other) {
            return Objects.equals(getNbt(), other);
        }

        default net.minecraft.class_2487 copyNbt() {
            net.minecraft.class_2487 nbt = getNbt();
            return nbt == null ? null : nbt.method_10553();
        }

        default net.minecraft.class_2487 copyOrCreateNbt() {
            net.minecraft.class_2487 nbt = getNbt();
            return nbt == null ? new net.minecraft.class_2487() : nbt.method_10553();
        }
    }

    // ---------------------------------------------------------------- NBT <-> components

    /** a 1.20.4 item tag as the component changes of a stack of the item */
    public static net.minecraft.class_9326 itemComponents(net.minecraft.class_1792 item, net.minecraft.class_2487 tag) {
        if (tag == null) {
            return net.minecraft.class_9326.field_49588;
        }
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(item);
        CbItemTags.setTag(stack, tag.method_10553());
        return stack.method_57380();
    }

    /** 1.20.4 NBT that no vanilla component speaks for, as custom data */
    public static net.minecraft.class_9326 customDataComponents(net.minecraft.class_2487 nbt) {
        if (nbt == null) {
            return net.minecraft.class_9326.field_49588;
        }
        return net.minecraft.class_9326.method_57841()
            .method_57854(net.minecraft.class_9334.field_49628, net.minecraft.class_9279.method_57456(nbt)).method_57852();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static net.minecraft.class_2487 customData(net.minecraft.class_9326 changes) {
        Optional<?> data = changes.method_57845((net.minecraft.class_9331) net.minecraft.class_9334.field_49628);
        return data == null || data.isEmpty() ? null : ((net.minecraft.class_9279) data.get()).method_57461();
    }

    // ---------------------------------------------------------------- TransferVariant's 1.20.4 members

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2487 getNbt(TransferVariant variant) {
        if (variant instanceof Legacy legacy) {
            return legacy.getNbt();
        }
        if (variant instanceof ItemVariant item) {
            return variant.hasComponents() ? CbItemTags.getTag(item.toStack()) : null;
        }
        return customData(variant.getComponents());
    }

    @SuppressWarnings("rawtypes")
    public static boolean hasNbt(TransferVariant variant) {
        return variant instanceof Legacy legacy ? legacy.hasNbt() : superHasNbt(variant);
    }

    @SuppressWarnings("rawtypes")
    public static boolean superHasNbt(TransferVariant variant) {
        return getNbt(variant) != null;
    }

    @SuppressWarnings("rawtypes")
    public static boolean nbtMatches(TransferVariant variant, net.minecraft.class_2487 other) {
        return variant instanceof Legacy legacy ? legacy.nbtMatches(other) : superNbtMatches(variant, other);
    }

    @SuppressWarnings("rawtypes")
    public static boolean superNbtMatches(TransferVariant variant, net.minecraft.class_2487 other) {
        return Objects.equals(getNbt(variant), other);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2487 copyNbt(TransferVariant variant) {
        return variant instanceof Legacy legacy ? legacy.copyNbt() : superCopyNbt(variant);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2487 superCopyNbt(TransferVariant variant) {
        net.minecraft.class_2487 nbt = getNbt(variant);
        return nbt == null ? null : nbt.method_10553();
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2487 copyOrCreateNbt(TransferVariant variant) {
        return variant instanceof Legacy legacy ? legacy.copyOrCreateNbt() : superCopyOrCreateNbt(variant);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2487 superCopyOrCreateNbt(TransferVariant variant) {
        net.minecraft.class_2487 nbt = getNbt(variant);
        return nbt == null ? new net.minecraft.class_2487() : nbt.method_10553();
    }

    /** 1.20.4's {item|fluid: id, tag?} */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2487 toNbt(TransferVariant variant) {
        if (variant instanceof Legacy legacy) {
            return legacy.toNbt();
        }
        net.minecraft.class_2487 result = new net.minecraft.class_2487();
        if (variant instanceof ItemVariant item) {
            result.method_10582("item", net.minecraft.class_7923.field_41178.method_10221(item.getItem()).toString());
        } else if (variant instanceof FluidVariant fluid) {
            result.method_10582("fluid", net.minecraft.class_7923.field_41173.method_10221(fluid.getFluid()).toString());
        } else {
            throw new IllegalArgumentException("transfer variant " + variant + " is of a 1.20.5 kind with no 1.20.4 NBT form");
        }
        net.minecraft.class_2487 nbt = getNbt(variant);
        if (nbt != null) {
            result.method_10566("tag", nbt.method_10553());
        }
        return result;
    }

    /** 1.20.4's present + raw id + tag */
    @SuppressWarnings("rawtypes")
    public static void toPacket(TransferVariant variant, net.minecraft.class_2540 buf) {
        if (variant instanceof Legacy legacy) {
            legacy.toPacket(buf);
            return;
        }
        if (!(variant instanceof ItemVariant) && !(variant instanceof FluidVariant)) {
            throw new IllegalArgumentException("transfer variant " + variant + " is of a 1.20.5 kind with no 1.20.4 packet form");
        }
        if (variant.isBlank()) {
            buf.method_52964(false);
            return;
        }
        buf.method_52964(true);
        buf.method_10804(variant instanceof ItemVariant item ? net.minecraft.class_1792.method_7880(item.getItem())
            : net.minecraft.class_7923.field_41173.method_10206(((FluidVariant) variant).getFluid()));
        buf.method_10794(getNbt(variant));
    }

    /** 1.20.5's getComponents() of a mod's 1.20.4 variant (adapter hook bridge): its NBT as custom data */
    public static net.minecraft.class_9326 getComponents(Legacy variant) {
        return customDataComponents(variant.getNbt());
    }

    // ---------------------------------------------------------------- ItemVariant / FluidVariant statics

    public static ItemVariant itemOf(net.minecraft.class_1935 item, net.minecraft.class_2487 tag) {
        net.minecraft.class_1792 it = item.method_8389();
        return ItemVariant.of(it, itemComponents(it, tag));
    }

    public static ItemVariant itemFromNbt(net.minecraft.class_2487 nbt) {
        try {
            net.minecraft.class_1792 item = net.minecraft.class_7923.field_41178.method_10223(new net.minecraft.class_2960(nbt.method_10558("item")));
            net.minecraft.class_2487 tag = nbt.method_10545("tag") ? nbt.method_10562("tag") : null;
            return itemOf(item, tag);
        } catch (RuntimeException e) {
            LOGGER.debug("Tried to load an invalid ItemVariant from NBT: {}", nbt, e);
            return ItemVariant.blank();
        }
    }

    public static ItemVariant itemFromPacket(net.minecraft.class_2540 buf) {
        if (!buf.readBoolean()) {
            return ItemVariant.blank();
        }
        net.minecraft.class_1792 item = net.minecraft.class_1792.method_7875(buf.method_10816());
        net.minecraft.class_2487 nbt = buf.method_10798();
        return itemOf(item, nbt);
    }

    public static FluidVariant fluidOf(net.minecraft.class_3611 fluid, net.minecraft.class_2487 nbt) {
        return FluidVariant.of(fluid, customDataComponents(nbt));
    }

    public static FluidVariant fluidFromNbt(net.minecraft.class_2487 compound) {
        try {
            net.minecraft.class_3611 fluid = net.minecraft.class_7923.field_41173.method_10223(new net.minecraft.class_2960(compound.method_10558("fluid")));
            net.minecraft.class_2487 nbt = compound.method_10545("tag") ? compound.method_10562("tag") : null;
            return fluidOf(fluid, nbt);
        } catch (RuntimeException e) {
            LOGGER.debug("Tried to load an invalid FluidVariant from NBT: {}", compound, e);
            return FluidVariant.blank();
        }
    }

    public static FluidVariant fluidFromPacket(net.minecraft.class_2540 buf) {
        if (!buf.readBoolean()) {
            return FluidVariant.blank();
        }
        net.minecraft.class_3611 fluid = net.minecraft.class_7923.field_41173.method_10200(buf.method_10816());
        net.minecraft.class_2487 nbt = buf.method_10798();
        return fluidOf(fluid, nbt);
    }
}
