package top.dext.centurybridge.step.v1_21_4.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.facade.ItemColor;
import top.dext.centurybridge.step.v1_21_4.facade.ItemColors;
import top.dext.centurybridge.step.v1_21_4.mixin.CbItemLayerAccess;
import top.dext.centurybridge.step.v1_21_4.mixin.CbItemStackRenderStateAccess;

/**
 * 1.21.3 item colour providers on 1.21.4. Mods registered them with Fabric API's ColorProviderRegistry.ITEM
 * (removed in 1.21.4, v0 and v1); 1.21.3 tinted every quad of an item whose face had a tint index with the
 * provider's answer for that index. The registrations are kept here, and the item model definitions the converter
 * writes for a 1.21.3 mod's items carry one {@code centurybridge:legacy_color} tint source per tint index, which
 * asks the provider -- no provider, no tint (-1), as before. 1.21.3 providers answered ARGB, which 1.21.4 tints are.
 */
@Environment(EnvType.CLIENT)
public final class CbItemColors {

    private CbItemColors() {
    }

    public static final net.minecraft.class_2960 LEGACY_COLOR = net.minecraft.class_2960.method_60655("centurybridge", "legacy_color");

    /** the providers registered through Fabric API, per item */
    private static final Map<net.minecraft.class_1792, ItemColor> REGISTERED = new IdentityHashMap<>();
    private static ItemColors defaults;

    public static synchronized void register(ItemColor provider, net.minecraft.class_1935... items) {
        for (net.minecraft.class_1935 item : items) {
            REGISTERED.put(item.method_8389(), provider);
        }
    }

    /** the item's registered provider; else the one 1.21.3's vanilla ItemColors held for it (Fabric API asked those) */
    public static synchronized ItemColor get(net.minecraft.class_1935 item) {
        ItemColor own = REGISTERED.get(item.method_8389());
        if (own != null) {
            return own;
        }
        if (defaults == null) {
            defaults = ItemColors.method_1706(net.minecraft.class_310.method_1551().method_1505());
        }
        return defaults.field_1996.method_10200(net.minecraft.class_1792.method_7880(item.method_8389()));
    }

    /** the colour a 1.21.3 mod's provider gives the stack, or -1 without one */
    public static synchronized int registeredColor(net.minecraft.class_1799 stack, int tintIndex) {
        ItemColor provider = REGISTERED.get(stack.method_7909());
        return provider == null ? -1 : provider.getColor(stack, tintIndex);
    }

    /**
     * The tint 1.21.4's own item model definition gives the stack at a tint index (-1 for none): what 1.21.3's
     * ItemColors answered for a vanilla item whose colour 1.21.4 moved into its definition (spawn eggs).
     */
    public static int definitionTint(net.minecraft.class_1799 stack, int tintIndex) {
        net.minecraft.class_10444 state = new net.minecraft.class_10444();
        net.minecraft.class_310.method_1551().method_65386().method_65596(state, stack, net.minecraft.class_811.field_4315, null, null, 0);
        CbItemStackRenderStateAccess access = (CbItemStackRenderStateAccess) state;
        if (access.cb$activeLayerCount() == 0) {
            return -1;
        }
        int[] tints = ((CbItemLayerAccess) access.cb$layers()[0]).cb$tints();
        return tintIndex >= 0 && tintIndex < tints.length ? tints[tintIndex] : -1;
    }

    /** centurybridge:legacy_color: the 1.21.3 provider's colour for one tint index */
    public record LegacyColor(int index) implements net.minecraft.class_10401 {
        public static final MapCodec<LegacyColor> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("index").forGetter(LegacyColor::index)
        ).apply(i, LegacyColor::new));

        @Override
        public int method_65389(net.minecraft.class_1799 stack, net.minecraft.class_638 level, net.minecraft.class_1309 entity) {
            return registeredColor(stack, this.index);
        }

        @Override
        public MapCodec<LegacyColor> method_65387() {
            return CODEC;
        }
    }
}
