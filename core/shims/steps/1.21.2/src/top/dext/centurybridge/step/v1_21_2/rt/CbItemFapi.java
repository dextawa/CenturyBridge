package top.dext.centurybridge.step.v1_21_2.rt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2378;
import net.minecraft.class_2922;
import net.minecraft.class_5321;
import net.minecraft.class_5485;
import net.minecraft.class_6880;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import top.dext.centurybridge.step.v1_21_2.facade.GenerationStepCarving;

/**
 * s7item's Fabric API share on 1.21.2 (Fabric API 0.116 for 1.21.1 -> 0.106 for 1.21.2).
 * <ul>
 * <li>CustomIngredient.getMatchingStacks() became getMatchingItems() (item holders): a 1.21.1 ingredient answers 1.21.2
 *     with the holders of its stacks, in order, once each; a 1.21.1 call on a 1.21.2 ingredient gets a stack of each
 *     item (1.21.2's ingredients match items, not stacks).</li>
 * <li>CustomIngredientSerializer.getCodec(allowEmpty) became getCodec(): 1.21.2's ingredients are never empty (an
 *     empty one is an absent Optional), so 1.21.2's codec is 1.21.1's non-empty one, getCodec(false).</li>
 * <li>FabricItem / FabricItemStack.getCreatorNamespace: gone in 0.106 (nothing in Minecraft or Fabric API asked it; it
 *     was an API between mods): a call is answered by the item's own 1.21.1 override, else 0.116's default body.</li>
 * <li>FabricTooltipType.shouldDisplayAllInformation(): gone with its interface (the layer's facade): a tooltip flag
 *     implementing the facade answers itself, any other false, 0.116's default.</li>
 * <li>Biome modification carvers by GenerationStep.Carving: 1.21.2 has one carving step. AIR is it; LIQUID is
 *     s7level's second carver list on the biome's generation settings (CbLevelCarving), which the context's settings
 *     are reached for (Fabric API's implementation class keeps them in a private field; a mixin cannot target a
 *     Fabric API class past the build's mixin gate, so the field is read reflectively, once looked up).</li>
 * </ul>
 */
public final class CbItemFapi {
    private CbItemFapi() {
    }

    // ---- CustomIngredient ----

    /** a 1.21.1 CustomIngredient: getMatchingStacks() */
    public interface LegacyIngredient {
        List<class_1799> getMatchingStacks();
    }

    /** 1.21.2's getMatchingItems() of a 1.21.1 ingredient: its stacks' item holders, in order, once each */
    public static List<class_6880<class_1792>> matchingItems(LegacyIngredient self) {
        List<class_6880<class_1792>> out = new ArrayList<>();
        for (class_1799 stack : self.getMatchingStacks()) {
            if (!stack.method_7960()) {
                class_6880<class_1792> holder = stack.method_41409();
                if (!out.contains(holder)) {
                    out.add(holder);
                }
            }
        }
        return out;
    }

    /** getMatchingStacks(), called: a 1.21.1 ingredient's own, else a stack of each of 1.21.2's items */
    public static List<class_1799> getMatchingStacks(CustomIngredient ingredient) {
        if (ingredient instanceof LegacyIngredient legacy) {
            return legacy.getMatchingStacks();
        }
        List<class_1799> out = new ArrayList<>();
        for (class_6880<class_1792> item : ingredient.getMatchingItems()) {
            out.add(new class_1799(item));
        }
        return out;
    }

    /** a 1.21.1 CustomIngredientSerializer: getCodec(allowEmpty) */
    public interface LegacySerializer<T extends CustomIngredient> {
        com.mojang.serialization.MapCodec<T> getCodec(boolean allowEmpty);
    }

    /** 1.21.2's getCodec() of a 1.21.1 serializer: its non-empty codec */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static com.mojang.serialization.MapCodec codec(LegacySerializer self) {
        return self.getCodec(false);
    }

    /** getCodec(allowEmpty), called: a 1.21.1 serializer's own, else 1.21.2's one codec */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static com.mojang.serialization.MapCodec getCodec(CustomIngredientSerializer serializer, boolean allowEmpty) {
        if (serializer instanceof LegacySerializer legacy) {
            return legacy.getCodec(allowEmpty);
        }
        return serializer.getCodec();
    }

    // ---- getCreatorNamespace ----

    /** a 1.21.1 item declaring FabricItem.getCreatorNamespace(stack) */
    public interface LegacyCreator {
        String getCreatorNamespace(class_1799 stack);
    }

    /** FabricItem.getCreatorNamespace(stack), called on an item */
    public static String getCreatorNamespace(class_1792 item, class_1799 stack) {
        if (item instanceof LegacyCreator legacy) {
            return legacy.getCreatorNamespace(stack);
        }
        return superGetCreatorNamespace(item, stack);
    }

    /**
     * Fabric API 0.116's default: the namespace of the stack's item -- of a potion's / tipped arrow's potion, and of
     * an enchanted book's only stored enchantment, instead
     */
    public static String superGetCreatorNamespace(class_1792 item, class_1799 stack) {
        class_6880<?> owner = stack.method_41409();
        if (item instanceof net.minecraft.class_1812 || item instanceof net.minecraft.class_1833) {
            if (stack.method_57826(net.minecraft.class_9334.field_49651)) {
                java.util.Optional<class_6880<net.minecraft.class_1842>> potion =
                    stack.method_57824(net.minecraft.class_9334.field_49651).comp_2378();
                if (potion.isPresent()) {
                    owner = potion.get();
                }
            }
        } else if (stack.method_31574(net.minecraft.class_1802.field_8598) && stack.method_57826(net.minecraft.class_9334.field_49643)) {
            java.util.Set<?> stored = stack.method_57824(net.minecraft.class_9334.field_49643).method_57534();
            if (stored.size() == 1) {
                owner = (class_6880<?>) stored.iterator().next();
            }
        }
        return owner.method_40230().orElseThrow().method_29177().method_12836();
    }

    /** the same, keyed on Fabric API's FabricItem (every FabricItem is an Item: the interface is injected into Item) */
    public static String getCreatorNamespace(net.fabricmc.fabric.api.item.v1.FabricItem item, class_1799 stack) {
        return getCreatorNamespace((class_1792) item, stack);
    }

    /** an item's FabricItem.super.getCreatorNamespace(stack): 0.116's default */
    public static String superGetCreatorNamespace(net.fabricmc.fabric.api.item.v1.FabricItem item, class_1799 stack) {
        return superGetCreatorNamespace((class_1792) item, stack);
    }

    /** a class declaring FabricItemStack.getCreatorNamespace() */
    public interface LegacyStackCreator {
        String getCreatorNamespace();
    }

    /**
     * FabricItemStack.getCreatorNamespace(), called (the interface is injected into ItemStack): a class declaring it
     * answers itself, a stack with its item's
     */
    public static String getCreatorNamespace(net.fabricmc.fabric.api.item.v1.FabricItemStack stack) {
        Object receiver = stack;
        if (receiver instanceof LegacyStackCreator legacy) {
            return legacy.getCreatorNamespace();
        }
        return superGetCreatorNamespace(stack);
    }

    /** 0.116's default: ((ItemStack) this).getItem().getCreatorNamespace(this) */
    public static String superGetCreatorNamespace(net.fabricmc.fabric.api.item.v1.FabricItemStack stack) {
        class_1799 s = (class_1799) (Object) stack;
        return getCreatorNamespace(s.method_7909(), s);
    }

    // ---- FabricTooltipType ----

    /**
     * FabricTooltipType.shouldDisplayAllInformation(), called on a tooltip flag (the interface was injected into
     * TooltipFlag; the call names the facade, the receiver may be any TooltipFlag)
     */
    public static boolean shouldDisplayAllInformation(top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricTooltipType flag) {
        Object receiver = flag;
        if (receiver instanceof top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricTooltipType legacy) {
            return legacy.shouldDisplayAllInformation();
        }
        return false;
    }

    // ---- biome modification carvers ----

    /**
     * a 1.21.1 GenerationSettingsContext implementation: its addCarver / removeCarver(step, carver) (adapterHooks; the
     * mod's own methods implement this). 0.106 calls the step-less shapes, synthesized to {@link #addCarverAir} and
     * {@link #removeCarverAll}.
     */
    public interface LegacyCarvers {
        void addCarver(GenerationStepCarving step, class_5321<class_2922<?>> carver);

        boolean removeCarver(GenerationStepCarving step, class_5321<class_2922<?>> carver);
    }

    /** 0.106's addCarver(carver) on a 1.21.1 implementation: its air step, 1.21.2's one step */
    public static void addCarverAir(LegacyCarvers context, class_5321<class_2922<?>> carver) {
        context.addCarver(GenerationStepCarving.AIR, carver);
    }

    /** 0.106's removeCarver(carver) on a 1.21.1 implementation: 0.116's default -- removed from every step */
    public static boolean removeCarverAll(LegacyCarvers context, class_5321<class_2922<?>> carver) {
        boolean any = false;
        for (GenerationStepCarving step : GenerationStepCarving.values()) {
            if (context.removeCarver(step, carver)) {
                any = true;
            }
        }
        return any;
    }

    /** context.addCarver(step, carver) */
    public static void addCarver(BiomeModificationContext.GenerationSettingsContext context, GenerationStepCarving step,
            class_5321<class_2922<?>> carver) {
        if (context instanceof LegacyCarvers legacy) {
            legacy.addCarver(step, carver);
            return;
        }
        if (step == GenerationStepCarving.AIR) {
            context.addCarver(carver);
            return;
        }
        class_5485 settings = (class_5485) field(context, "generationSettings");
        @SuppressWarnings("unchecked")
        class_2378<class_2922<?>> carvers = (class_2378<class_2922<?>>) field(context, "carvers");
        CbLevelCarving.SettingsState state = (CbLevelCarving.SettingsState) settings;
        List<class_6880<class_2922<?>>> list = state.cb$liquidCarvers() == null ? new ArrayList<>()
            : new ArrayList<>(state.cb$liquidCarvers());
        list.add(carvers.method_46747(carver));
        state.cb$liquidCarvers(List.copyOf(list));
    }

    /** context.removeCarver(step, carver): whether the step had it */
    public static boolean removeCarver(BiomeModificationContext.GenerationSettingsContext context, GenerationStepCarving step,
            class_5321<class_2922<?>> carver) {
        if (context instanceof LegacyCarvers legacy) {
            return legacy.removeCarver(step, carver);
        }
        if (step == GenerationStepCarving.AIR) {
            return context.removeCarver(carver);
        }
        class_5485 settings = (class_5485) field(context, "generationSettings");
        CbLevelCarving.SettingsState state = (CbLevelCarving.SettingsState) settings;
        List<class_6880<class_2922<?>>> list = state.cb$liquidCarvers();
        if (list == null) {
            return false;
        }
        List<class_6880<class_2922<?>>> kept = new ArrayList<>();
        for (class_6880<class_2922<?>> h : list) {
            if (!h.method_40225(carver)) {
                kept.add(h);
            }
        }
        if (kept.size() == list.size()) {
            return false;
        }
        state.cb$liquidCarvers(List.copyOf(kept));
        return true;
    }

    /** BiomeModifications.addCarver(selector, step, carver): Fabric API 0.116's body on 0.106's modification */
    public static void addCarver(Predicate<BiomeSelectionContext> selector, GenerationStepCarving step, class_5321<class_2922<?>> carver) {
        BiomeModifications.create(carver.method_29177()).add(ModificationPhase.ADDITIONS, selector,
            context -> addCarver(context.getGenerationSettings(), step, carver));
    }

    private static final java.util.Map<String, Field> FIELDS = new java.util.concurrent.ConcurrentHashMap<>();

    private static Object field(Object context, String name) {
        try {
            Field f = FIELDS.computeIfAbsent(context.getClass().getName() + "#" + name, k -> {
                try {
                    Field found = context.getClass().getDeclaredField(name);
                    found.setAccessible(true);
                    return found;
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Fabric API's " + context.getClass().getName() + " has no " + name, e);
                }
            });
            return f.get(context);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
