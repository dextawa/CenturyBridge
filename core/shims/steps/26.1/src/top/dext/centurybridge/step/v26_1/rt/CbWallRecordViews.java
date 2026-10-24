package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import java.util.UUID;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryValidator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldStem;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.WorldData;

/**
 * 1.21.11 record members 26.1 reshaped around the same data:
 * <ul>
 * <li>CatVariant / WolfVariant held one asset; 26.1 holds an adult and a baby one. 1.21.11 drew babies with the
 * adult asset, so a 1.21.11 variant is 26.1's with that asset for both, and its asset is the adult one.</li>
 * <li>WorldStem / LevelDataAndDimensions held the WorldData; 26.1 holds it with the world generation settings
 * split off (WorldDataAndGenSettings.data()).</li>
 * <li>RegistryDataLoader.RegistryData held a requiredNonEmpty flag; 26.1 holds a validator, nonEmpty() for it.</li>
 * <li>Mth.createInsecureUUID() drew from Mth's own thread-safe random source, which 26.1 dropped with the no-argument
 * method: the layer keeps one of the same kind.</li>
 * </ul>
 */
public final class CbWallRecordViews {

    /** 1.21.11 Mth.RANDOM: RandomSource.createThreadSafe() */
    private static final RandomSource RANDOM = RandomSource.createThreadSafe();

    private CbWallRecordViews() {
    }

    /** 1.21.11 new CatVariant(assetInfo, spawnConditions) */
    public static CatVariant catVariant(ClientAsset.ResourceTexture assetInfo, SpawnPrioritySelectors spawnConditions) {
        return new CatVariant(assetInfo, assetInfo, spawnConditions);
    }

    /** 1.21.11 CatVariant.assetInfo() */
    public static ClientAsset.ResourceTexture catAssetInfo(CatVariant variant) {
        return variant.adultAssetInfo();
    }

    /** 1.21.11 new WolfVariant(assetInfo, spawnConditions) */
    public static WolfVariant wolfVariant(WolfVariant.AssetInfo assetInfo, SpawnPrioritySelectors spawnConditions) {
        return new WolfVariant(assetInfo, assetInfo, spawnConditions);
    }

    /** 1.21.11 WolfVariant.assetInfo() */
    public static WolfVariant.AssetInfo wolfAssetInfo(WolfVariant variant) {
        return variant.adultInfo();
    }

    /** 1.21.11 WorldStem.worldData() */
    public static WorldData stemWorldData(WorldStem stem) {
        return stem.worldDataAndGenSettings().data();
    }

    /** 1.21.11 LevelDataAndDimensions.worldData() */
    public static WorldData levelWorldData(LevelDataAndDimensions levelData) {
        return levelData.worldDataAndGenSettings().data();
    }

    /** 1.21.11 new RegistryDataLoader.RegistryData(key, elementCodec, requiredNonEmpty) */
    public static <T> RegistryDataLoader.RegistryData<T> registryData(ResourceKey<? extends Registry<T>> key, Codec<T> elementCodec,
                                                                    boolean requiredNonEmpty) {
        return new RegistryDataLoader.RegistryData<>(key, elementCodec, requiredNonEmpty ? RegistryValidator.nonEmpty() : RegistryValidator.none());
    }

    /** 1.21.11 RegistryDataLoader.RegistryData.requiredNonEmpty() */
    public static boolean requiredNonEmpty(RegistryDataLoader.RegistryData<?> data) {
        return data.validator() == RegistryValidator.NON_EMPTY;
    }

    /** 1.21.11 Mth.createInsecureUUID() */
    public static UUID createInsecureUUID() {
        return Mth.createInsecureUUID(RANDOM);
    }

    /** 1.21.11 SetOminousBottleAmplifierFunction.amplifier(): the field (the layer's access widener opens it) */
    public static net.minecraft.world.level.storage.loot.providers.number.NumberProvider amplifier(
            net.minecraft.world.level.storage.loot.functions.SetOminousBottleAmplifierFunction function) {
        return function.amplifier;
    }
}
