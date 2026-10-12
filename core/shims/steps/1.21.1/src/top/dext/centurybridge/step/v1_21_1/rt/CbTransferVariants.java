package top.dext.centurybridge.step.v1_21_1.rt;

import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

/**
 * 1.21.1's Fabric API (transfer 5.4.4) gave transfer variants three abstract methods a variant written
 * against 1.21 cannot have. Fabric API made each abstract (no default to inherit); each is given here as
 * Fabric's own implementations compute it (FluidVariantImpl, ItemVariantImpl, TransferApiImpl of 5.4.4),
 * from what a 1.21 variant already answers -- its object and its component changes:
 * <ul>
 * <li>TransferVariant.getComponentMap(): an ItemVariant's is its stack's component map (ItemVariantImpl:
 *     the stack toStack() makes); any other variant's is its object's default components -- an item's own,
 *     nothing for a fluid (FluidVariantImpl: ComponentMap.EMPTY) or any other object -- with its component
 *     changes applied.</li>
 * <li>FluidVariant / ItemVariant.withComponentChanges(changes): the variant of the same fluid / item with the
 *     variant's changes and then the given ones (TransferApiImpl.mergeChanges).</li>
 * </ul>
 * TransferVariant itself declares withComponentChanges as a default that throws; FluidVariant and ItemVariant
 * redeclare it abstract with their own return type, which a class compiled against 1.21.1 implements and
 * javac bridges from TransferVariant's descriptor. The bridge is given too, so a call made through a
 * TransferVariant reaches the implementation instead of the throwing default.
 */
public final class CbTransferVariants {

    private CbTransferVariants() {
    }

    public static net.minecraft.class_9323 getComponentMap(TransferVariant<?> variant) {
        if (variant instanceof ItemVariant item) {
            return item.toStack().method_57353();
        }
        net.minecraft.class_9323 base = variant.getObject() instanceof net.minecraft.class_1792 it
            ? it.method_57347() : net.minecraft.class_9323.field_49584;
        net.minecraft.class_9326 changes = variant.getComponents();
        return changes.method_57848() ? base : net.minecraft.class_9335.method_57935(base, changes);
    }

    /** {@link #getComponentMap(TransferVariant)} for a class implementing FluidVariant (the fill names its receiver type) */
    public static net.minecraft.class_9323 getComponentMap(FluidVariant variant) {
        return getComponentMap((TransferVariant<?>) variant);
    }

    /** {@link #getComponentMap(TransferVariant)} for a class implementing ItemVariant */
    public static net.minecraft.class_9323 getComponentMap(ItemVariant variant) {
        return getComponentMap((TransferVariant<?>) variant);
    }

    public static FluidVariant withComponentChanges(FluidVariant variant, net.minecraft.class_9326 changes) {
        return FluidVariant.of(variant.getFluid(), merge(variant.getComponents(), changes));
    }

    public static TransferVariant<?> withComponentChangesBridge(FluidVariant variant, net.minecraft.class_9326 changes) {
        return withComponentChanges(variant, changes);
    }

    public static ItemVariant withComponentChanges(ItemVariant variant, net.minecraft.class_9326 changes) {
        return ItemVariant.of(variant.getItem(), merge(variant.getComponents(), changes));
    }

    public static TransferVariant<?> withComponentChangesBridge(ItemVariant variant, net.minecraft.class_9326 changes) {
        return withComponentChanges(variant, changes);
    }

    /** TransferApiImpl.mergeChanges: the base changes, then the applied ones over them */
    @SuppressWarnings("unchecked")
    static net.minecraft.class_9326 merge(net.minecraft.class_9326 base, net.minecraft.class_9326 applied) {
        net.minecraft.class_9326.class_9327 builder = net.minecraft.class_9326.method_57841();
        for (net.minecraft.class_9326 changes : new net.minecraft.class_9326[] {base, applied}) {
            for (Map.Entry<net.minecraft.class_9331<?>, Optional<?>> entry : changes.method_57846()) {
                if (entry.getValue().isPresent()) {
                    builder.method_57854((net.minecraft.class_9331<Object>) entry.getKey(), entry.getValue().get());
                } else {
                    builder.method_57853(entry.getKey());
                }
            }
        }
        return builder.method_57852();
    }
}
