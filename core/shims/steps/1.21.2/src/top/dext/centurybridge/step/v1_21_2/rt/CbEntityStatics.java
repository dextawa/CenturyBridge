package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1738;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2582;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3765;
import net.minecraft.class_4156;
import net.minecraft.class_4157;
import net.minecraft.class_7871;
import net.minecraft.class_8949;
import net.minecraft.class_9334;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_21_2.facade.ArmorMaterials;

/**
 * Static entity-area members 1.21.2 removed or reshaped, answered with 1.21.1's bodies:
 * <ul>
 * <li>Shoot.isFacingTarget(breeze, target) (the breeze's shoot behaviour): view vector . direction to target &gt; 0.5;
 *     1.21.2 dropped it, the operations it used remain.</li>
 * <li>PiglinAi.isWearingGold(entity): some armor slot holds an ArmorItem of the gold material. 1.21.2's
 *     isWearingSafeArmor asks the piglin_safe_armor tag instead; 1.21.1's material is read as s7item gives it back
 *     (CbItemArmor.method_7686: a mod's 1.21.1 material, or the 1.21.1 material of a vanilla one).</li>
 * <li>Raid.getLeaderBannerInstance(patterns): 1.21.2's getOminousBannerInstance is 1.21.1's body plus a RARITY of
 *     uncommon; 1.21.1's banner kept the item's own rarity, so it is set back to the item's (which drops the
 *     patch).</li>
 * <li>PoiRecord.codec(setDirty) / PoiSection.codec(setDirty): 1.21.2 encodes the Packed records (same fields:
 *     pos / type / free_tickets, Valid / Records) and unpacks them with the setDirty callback, as 1.21.1's codecs
 *     constructed the objects; a section that fails to decode was 1.21.1's error log and a fresh invalid section
 *     (1.21.2's Packed(false, []) unpacked).</li>
 * <li>NearestVisibleLivingEntities(entity, list): 1.21.2's constructor takes the level its targetability test runs in;
 *     1.21.1's test ran in the entity's own, which sensors (the only makers) have on the server
 *     (CbEntityLevels.serverLevel).</li>
 * <li>PaintingVariant(width, height, assetId): 1.21.2's variant also carries the title and author its tooltip shows.
 *     1.21.1's tooltip showed translatable painting.&lt;namespace&gt;.&lt;path&gt;.title (yellow) / .author (gray) of
 *     the variant's registry id; the constructor knows the asset id, which 1.21.1's vanilla variants and the
 *     usual mod registration share with the registry id, so the same keys are derived from it.</li>
 * </ul>
 */
public final class CbEntityStatics {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isFacingTarget(class_8949 breeze, class_1309 target) {
        class_243 view = breeze.method_5828(1.0F);
        class_243 toTarget = target.method_19538().method_1020(breeze.method_19538()).method_1029();
        return view.method_1026(toTarget) > 0.5;
    }

    public static boolean isWearingGold(class_1309 entity) {
        for (class_1799 stack : entity.method_56674()) {
            if (stack.method_7909() instanceof class_1738 armor
                && CbItemArmor.method_7686(armor).method_55838(ArmorMaterials.field_7895)) {
                return true;
            }
        }
        return false;
    }

    public static class_1799 getLeaderBannerInstance(class_7871<class_2582> patterns) {
        class_1799 banner = class_3765.method_61558(patterns);
        banner.method_57379(class_9334.field_50073, banner.method_7909().method_57347().method_57829(class_9334.field_50073));
        return banner;
    }

    public static Codec<class_4156> poiRecordCodec(Runnable setDirty) {
        return class_4156.class_9864.field_52452.xmap(packed -> packed.method_61450(setDirty), class_4156::method_61448);
    }

    public static Codec<class_4157> poiSectionCodec(Runnable setDirty) {
        return class_4157.class_9865.field_52453
            .xmap(packed -> packed.method_61453(setDirty), class_4157::method_61451)
            .orElseGet(class_156.method_29188("Failed to read POI section: ", LOGGER::error),
                () -> new class_4157.class_9865(false, List.of()).method_61453(setDirty));
    }

    public static class_3218 level(class_1309 entity) {
        return CbEntityLevels.serverLevel(entity);
    }

    public static Optional<class_2561> paintingTitle(class_2960 assetId) {
        return Optional.of(class_2561.method_43471(assetId.method_48747("painting", "title")).method_27692(class_124.field_1054));
    }

    public static Optional<class_2561> paintingAuthor(class_2960 assetId) {
        return Optional.of(class_2561.method_43471(assetId.method_48747("painting", "author")).method_27692(class_124.field_1080));
    }

    private CbEntityStatics() {
    }
}
