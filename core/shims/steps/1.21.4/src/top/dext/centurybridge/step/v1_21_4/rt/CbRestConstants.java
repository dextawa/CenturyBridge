package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.Comparator;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2464;
import net.minecraft.class_2960;
import net.minecraft.class_3230;
import net.minecraft.class_6862;
import net.minecraft.class_7696;
import net.minecraft.class_7701;
import net.minecraft.class_7924;

/**
 * 1.21.3 constants gone in 1.21.4 (fieldRedirects), each with 1.21.3's value or the 1.21.4 value that
 * stands for the same thing.
 * <ul>
 * <li>BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS / FLOWERS / TRIM_TEMPLATES: the same tag keys. 1.21.4's
 *     data dropped these tags; the layer ships 1.21.3's tag files (data/minecraft/tags/..., every entry
 *     checked to exist in 1.21.4), so the keys hold 1.21.3's contents.</li>
 * <li>InventoryMenu.BLOCK_ATLAS: 1.21.3's value, minecraft:textures/atlas/blocks.png.</li>
 * <li>AbstractFurnaceBlockEntity.UNKNOWN_LIT_DURATION: 1.21.3's value 0.</li>
 * <li>TicketType.POST_TELEPORT: 1.21.3's ticket type ("post_teleport", Integer order, timeout 5), made as
 *     1.21.3 made it.</li>
 * <li>FeatureFlags.WINTER_DROP: the Winter Drop content is part of vanilla in 1.21.4, enabled whenever
 *     FeatureFlags.VANILLA is; that flag stands for it.</li>
 * <li>RenderShape.ENTITYBLOCK_ANIMATED: 1.21.4 has MODEL and INVISIBLE; a block drawn only by its block
 *     entity renderer (1.21.3's ENTITYBLOCK_ANIMATED: no block model) is INVISIBLE, which 1.21.4's renderer
 *     treats the same way for the world.</li>
 * </ul>
 */
public final class CbRestConstants {

    private CbRestConstants() {
    }

    /** BlockTags.TALL_FLOWERS */
    public static final class_6862<class_2248> field_20338 = class_6862.method_40092(class_7924.field_41254, class_2960.method_60656("tall_flowers"));
    /** ItemTags.TALL_FLOWERS */
    public static final class_6862<class_1792> field_20343 = class_6862.method_40092(class_7924.field_41197, class_2960.method_60656("tall_flowers"));
    /** ItemTags.FLOWERS */
    public static final class_6862<class_1792> field_20344 = class_6862.method_40092(class_7924.field_41197, class_2960.method_60656("flowers"));
    /** ItemTags.TRIM_TEMPLATES */
    public static final class_6862<class_1792> field_41892 = class_6862.method_40092(class_7924.field_41197, class_2960.method_60656("trim_templates"));
    /** InventoryMenu.BLOCK_ATLAS */
    public static final class_2960 field_21668 = class_2960.method_60656("textures/atlas/blocks.png");
    /** AbstractFurnaceBlockEntity.UNKNOWN_LIT_DURATION */
    public static final int field_52634 = 0;
    /** TicketType.POST_TELEPORT */
    public static final class_3230<Integer> field_19347 = class_3230.method_20628("post_teleport", Comparator.<Integer>naturalOrder(), 5);
    /** FeatureFlags.WINTER_DROP */
    public static final class_7696 field_54592 = class_7701.field_40177;
    /**
     * EntityType.CREAKING_TRANSIENT: 1.21.4 summons heart-bound creakings as EntityType.CREAKING (the transient
     * behaviour is Creaking's own when it has a home heart); an entity made from it is a Creaking, not the
     * CreakingTransient facade class
     */
    public static final net.minecraft.class_1299<?> field_54561 = net.minecraft.class_1299.field_54560;
    /** RenderShape.ENTITYBLOCK_ANIMATED */
    public static final class_2464 field_11456 = class_2464.field_11455;
}
