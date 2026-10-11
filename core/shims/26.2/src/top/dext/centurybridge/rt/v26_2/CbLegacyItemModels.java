package top.dext.centurybridge.rt.v26_2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import top.dext.centurybridge.compat.fapi.CbColorProviderRegistry;
import top.dext.centurybridge.facade.v26_2.class_1800;
import top.dext.centurybridge.facade.v26_2.class_326;
import top.dext.centurybridge.facade.v26_2.class_5272;

/**
 * The 1.20.1 item model semantics on 26.x item model definitions, for the definitions the
 * converter writes from a mod's old models/item JSON:
 *
 * <ul>
 * <li>{@code centurybridge:legacy_overrides} -- the model plus its old "overrides": every
 * predicate is read through ModelPredicateProviderRegistry (the facade, which keeps what
 * mods register), an unregistered one reads -infinity, and the last override in file
 * order whose every threshold is met wins -- ModelOverrideList walked its overrides
 * reversed and took the first match.</li>
 * <li>{@code centurybridge:legacy_color} -- one tint index answered by the colour provider
 * the mod registered for the item (ColorProviderRegistry.ITEM), as ItemColors.getColor did;
 * an item without one gets what vanilla 1.20.1 registered itself for mod items, which is
 * the two colours of a spawn egg (every SpawnEggItem was registered, modded ones too), and
 * otherwise no tint. Old providers return RGB, the renderer ignored their alpha; 26.x
 * tints are ARGB, so the alpha is made opaque.</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbLegacyItemModels {

    public static final Identifier OVERRIDES = Identifier.fromNamespaceAndPath("centurybridge", "legacy_overrides");
    public static final Identifier LEGACY_COLOR = Identifier.fromNamespaceAndPath("centurybridge", "legacy_color");

    /** one old override: every predicate's value must reach its threshold */
    public record LegacyOverride(Map<Identifier, Float> predicate, Identifier model) {
        static final Codec<LegacyOverride> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(Identifier.CODEC, Codec.FLOAT).fieldOf("predicate").forGetter(LegacyOverride::predicate),
            Identifier.CODEC.fieldOf("model").forGetter(LegacyOverride::model)
        ).apply(i, LegacyOverride::new));
    }

    public record Unbaked(Identifier model, List<LegacyOverride> overrides, List<ItemTintSource> tints)
            implements ItemModel.Unbaked {

        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model),
            LegacyOverride.CODEC.listOf().fieldOf("overrides").forGetter(Unbaked::overrides),
            ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)
        ).apply(i, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(model);
            for (LegacyOverride o : overrides) {
                resolver.markDependency(o.model());
            }
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext ctx, Matrix4fc transform) {
            ItemModel base = new CuboidItemModelWrapper.Unbaked(model, Optional.empty(), tints).bake(ctx, transform);
            List<Identifier[]> keys = new ArrayList<>();
            List<float[]> thresholds = new ArrayList<>();
            List<ItemModel> models = new ArrayList<>();
            for (LegacyOverride o : overrides) {
                Identifier[] k = o.predicate().keySet().toArray(new Identifier[0]);
                float[] t = new float[k.length];
                for (int j = 0; j < k.length; j++) {
                    t[j] = o.predicate().get(k[j]);
                }
                keys.add(k);
                thresholds.add(t);
                models.add(new CuboidItemModelWrapper.Unbaked(o.model(), Optional.empty(), tints).bake(ctx, transform));
            }
            return new Baked(base, keys, thresholds, models);
        }
    }

    static final class Baked implements ItemModel {
        private final ItemModel base;
        private final List<Identifier[]> keys;
        private final List<float[]> thresholds;
        private final List<ItemModel> models;

        Baked(ItemModel base, List<Identifier[]> keys, List<float[]> thresholds, List<ItemModel> models) {
            this.base = base;
            this.keys = keys;
            this.thresholds = thresholds;
            this.models = models;
        }

        @Override
        public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext mode,
                ClientLevel world, ItemOwner owner, int seed) {
            ItemModel chosen = base;
            Item item = stack.getItem();
            LivingEntity entity = owner == null ? null : owner.asLivingEntity();
            for (int i = models.size() - 1; i >= 0; i--) {
                Identifier[] k = keys.get(i);
                float[] t = thresholds.get(i);
                boolean all = true;
                for (int j = 0; j < k.length && all; j++) {
                    class_1800 p = class_5272.method_27878(item, k[j]);
                    float v = p != null ? p.call(stack, world, entity, seed) : Float.NEGATIVE_INFINITY;
                    all = v >= t[j];
                }
                if (all) {
                    chosen = models.get(i);
                    break;
                }
            }
            chosen.update(state, stack, resolver, mode, world, owner, seed);
        }
    }

    public record LegacyColor(int index) implements ItemTintSource {
        public static final MapCodec<LegacyColor> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("index").forGetter(LegacyColor::index)
        ).apply(i, LegacyColor::new));

        @Override
        public int calculate(ItemStack stack, ClientLevel world, LivingEntity entity) {
            class_326 provider = CbColorProviderRegistry.ITEM.get(stack.getItem());
            int rgb = provider != null ? provider.getColor(stack, index) : Statics.cbLegacySpawnEggColor(stack.getItem(), index);
            return rgb == -1 ? -1 : rgb | 0xFF000000;
        }

        @Override
        public MapCodec<LegacyColor> type() {
            return CODEC;
        }
    }

    private CbLegacyItemModels() {
    }
}
