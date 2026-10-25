package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * SimpleContainer's 1.21.5 ListTag forms on 1.21.6, which reads and writes the item list through ValueInput /
 * ValueOutput typed lists (javap of both):
 * <ul>
 * <li>fromTag(ListTag, Provider): clear, then add every entry ItemStack.CODEC parses under the provider's registry ops,
 *     skipping (and logging) the ones it cannot. 1.21.6's fromItems(TypedInputList) clears and adds every stack of the
 *     list; the list is read from a TagValueInput over the same ListTag with ItemStack.CODEC, which skips and reports
 *     undecodable entries the same way.</li>
 * <li>createTag(Provider): a ListTag of ItemStack.save(provider) of each non-empty stack, in slot order. 1.21.6's
 *     storeAsItemList(TypedOutputList) adds the same non-empty stacks in the same order to a TagValueOutput list with
 *     ItemStack.CODEC, which is what save encoded with.</li>
 * <li>PlayerEnderChestContainer overrode both with slots: fromTag cleared every slot, then set each entry's stack (an
 *     unparsable one as empty) at its "Slot" byte when 0 &lt;= slot &lt; size; createTag wrote a compound per non-empty
 *     slot, its "Slot" byte and the stack. 1.21.6's fromSlots / storeAsSlots do the same through ItemStackWithSlot.CODEC
 *     (an undecodable entry is skipped, leaving the cleared slot empty).</li>
 * </ul>
 */
public final class CbContainers {

    private CbContainers() {
    }

    /** SimpleContainer.fromTag(ListTag, Provider) */
    public static void method_7659(net.minecraft.class_1277 container, net.minecraft.class_2499 list, net.minecraft.class_7225.class_7874 provider) {
        if (container instanceof net.minecraft.class_1730 ender) {
            enderFromTag(ender, list, provider); // 1.21.5 dispatched to its override
            return;
        }
        net.minecraft.class_2487 holder = new net.minecraft.class_2487();
        holder.method_10566("Items", list);
        net.minecraft.class_8942.class_11340 reporter = CbValueIo.reporter();
        try {
            container.method_7659(CbValueIo.input(holder, provider, reporter).method_71437("Items", net.minecraft.class_1799.field_24671));
        } finally {
            CbValueIo.close(reporter);
        }
    }

    /** SimpleContainer.createTag(Provider) */
    public static net.minecraft.class_2499 method_7660(net.minecraft.class_1277 container, net.minecraft.class_7225.class_7874 provider) {
        if (container instanceof net.minecraft.class_1730 ender) {
            return enderCreateTag(ender, provider); // 1.21.5 dispatched to its override
        }
        net.minecraft.class_8942.class_11340 reporter = CbValueIo.reporter();
        try {
            net.minecraft.class_11362 out = CbValueIo.fresh(provider, reporter);
            container.method_7660(out.method_71467("Items", net.minecraft.class_1799.field_24671));
            return CbValueIo.result(out).method_10580("Items") instanceof net.minecraft.class_2499 items ? items : new net.minecraft.class_2499();
        } finally {
            CbValueIo.close(reporter);
        }
    }

    /** PlayerEnderChestContainer.fromTag(ListTag, Provider) */
    public static void enderFromTag(net.minecraft.class_1730 container, net.minecraft.class_2499 list, net.minecraft.class_7225.class_7874 provider) {
        net.minecraft.class_2487 holder = new net.minecraft.class_2487();
        holder.method_10566("Items", list);
        net.minecraft.class_8942.class_11340 reporter = CbValueIo.reporter();
        try {
            container.method_71390(CbValueIo.input(holder, provider, reporter).method_71437("Items", net.minecraft.class_11343.field_60354));
        } finally {
            CbValueIo.close(reporter);
        }
    }

    /** PlayerEnderChestContainer.createTag(Provider) */
    public static net.minecraft.class_2499 enderCreateTag(net.minecraft.class_1730 container, net.minecraft.class_7225.class_7874 provider) {
        net.minecraft.class_8942.class_11340 reporter = CbValueIo.reporter();
        try {
            net.minecraft.class_11362 out = CbValueIo.fresh(provider, reporter);
            container.method_71391(out.method_71467("Items", net.minecraft.class_11343.field_60354));
            return CbValueIo.result(out).method_10580("Items") instanceof net.minecraft.class_2499 items ? items : new net.minecraft.class_2499();
        } finally {
            CbValueIo.close(reporter);
        }
    }
}
