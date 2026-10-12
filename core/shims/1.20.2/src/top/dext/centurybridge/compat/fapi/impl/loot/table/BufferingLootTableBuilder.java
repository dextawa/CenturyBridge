package top.dext.centurybridge.compat.fapi.impl.loot.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import top.dext.centurybridge.compat.fapi.loot.v1.FabricLootSupplier;
import top.dext.centurybridge.compat.fapi.loot.v1.FabricLootSupplierBuilder;

/**
 * 1.20.1's BufferingLootTableBuilder: the v1 builder a LootTableLoadingCallback is handed. It starts
 * as a copy of the loaded table (so the callback can read it) and records every change, which the
 * loot API v2 MODIFY listener then replays onto the table's real builder.
 */
public class BufferingLootTableBuilder extends FabricLootSupplierBuilder {

    private final List<Consumer<net.minecraft.class_52.class_53>> modifications = new ArrayList<>();

    private FabricLootSupplierBuilder addAction(Consumer<net.minecraft.class_52.class_53> action) {
        modifications.add(action);
        return this;
    }

    private FabricLootSupplierBuilder addV2Action(Consumer<net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder> action) {
        return addAction(builder -> action.accept((net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder) (Object) builder));
    }

    @Override
    public FabricLootSupplierBuilder pool(net.minecraft.class_55.class_56 pool) {
        super.pool(pool);
        return addAction(builder -> builder.method_336(pool));
    }

    @Override
    public FabricLootSupplierBuilder type(net.minecraft.class_176 type) {
        super.type(type);
        return addAction(builder -> builder.method_334(type));
    }

    @Override
    public FabricLootSupplierBuilder apply(net.minecraft.class_117.class_118 function) {
        super.apply(function);
        return addAction(builder -> builder.method_335(function));
    }

    @Override
    public FabricLootSupplierBuilder withPool(net.minecraft.class_55 pool) {
        super.withPool(pool);
        return addV2Action(builder -> builder.pool(pool));
    }

    @Override
    public FabricLootSupplierBuilder withFunction(net.minecraft.class_117 function) {
        super.withFunction(function);
        return addV2Action(builder -> builder.apply(function));
    }

    @Override
    public FabricLootSupplierBuilder withPools(Collection<net.minecraft.class_55> pools) {
        super.withPools(pools);
        return addV2Action(builder -> builder.pools(pools));
    }

    @Override
    public FabricLootSupplierBuilder withFunctions(Collection<net.minecraft.class_117> functions) {
        super.withFunctions(functions);
        return addV2Action(builder -> builder.apply(functions));
    }

    @Override
    public FabricLootSupplierBuilder copyFrom(net.minecraft.class_52 table, boolean copyType) {
        super.copyFrom(table, copyType);
        return addV2Action(builder -> {
            FabricLootSupplier extended = (FabricLootSupplier) table;
            builder.pools(extended.getPools());
            builder.apply(extended.getFunctions());
            if (copyType) {
                ((net.minecraft.class_52.class_53) (Object) builder).method_334(table.method_322());
            }
        });
    }

    /** the loaded table, unrecorded: what the callback reads before it changes anything */
    public void init(net.minecraft.class_52 table) {
        super.type(table.method_322());
        super.withPools(((FabricLootSupplier) table).getPools());
        super.withFunctions(((FabricLootSupplier) table).getFunctions());
    }

    public void applyTo(net.minecraft.class_52.class_53 builder) {
        for (Consumer<net.minecraft.class_52.class_53> modification : modifications) {
            modification.accept(builder);
        }
    }
}
