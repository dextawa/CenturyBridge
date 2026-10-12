package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import top.dext.centurybridge.step.v1_20_5.rt.CbLootData;

/**
 * 1.20.4's LootDataManager: every predicate, item modifier and loot table, by LootDataId. 1.20.5 has no
 * such object -- loot data is the server's reloadable registries -- so a manager here answers from a
 * view of those (rt/CbLootData.manager / server); one a mod makes itself is empty until its reload,
 * which loads the given resources the way 1.20.5 does (CbLootData.load). The empty loot table's id
 * answers LootTable.EMPTY as in 1.20.4.
 */
public class LootDataManager implements net.minecraft.class_3302, LootDataResolver {

    /** LOGGER */
    public static final org.slf4j.Logger field_975 = com.mojang.logging.LogUtils.getLogger();

    /** GSON */
    public static final com.google.gson.Gson field_45793 = new com.google.gson.GsonBuilder().create();

    /** EMPTY_LOOT_TABLE_KEY */
    public static final LootDataId<net.minecraft.class_52> field_44491 =
        new LootDataId<>(net.minecraft.class_8490.field_44498, net.minecraft.class_39.field_844.method_29177());

    private volatile CbLootData.View view;

    public LootDataManager() {
        this(CbLootData.EMPTY);
    }

    public LootDataManager(CbLootData.View view) {
        this.view = view;
    }

    /** the view this manager answers from */
    public CbLootData.View cbView() {
        return view;
    }

    /** reload(barrier, resources, ...): the resources loaded as 1.20.5 loads loot data, taken on the game thread */
    @Override
    public final CompletableFuture<Void> method_25931(net.minecraft.class_3302.class_4045 barrier, net.minecraft.class_3300 resources,
                                                       net.minecraft.class_3695 preparationsProfiler, net.minecraft.class_3695 reloadProfiler,
                                                       Executor backgroundExecutor, Executor gameExecutor) {
        return CbLootData.load(resources, backgroundExecutor)
            .thenCompose(barrier::method_18352)
            .thenAcceptAsync(v -> this.view = v, gameExecutor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getElement(LootDataId<T> id) {
        return (T) view.element(id.comp_1474, id.comp_1475);
    }

    /** getKeys(type) */
    public Collection<net.minecraft.class_2960> method_51193(net.minecraft.class_8490<?> type) {
        return view.keys(type);
    }
}
