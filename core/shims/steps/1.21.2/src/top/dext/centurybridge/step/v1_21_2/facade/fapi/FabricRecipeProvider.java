package top.dext.centurybridge.step.v1_21_2.facade.fapi;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.class_161;
import net.minecraft.class_1860;
import net.minecraft.class_2405;
import net.minecraft.class_2446;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_5797;
import net.minecraft.class_7225;
import net.minecraft.class_7403;
import net.minecraft.class_7784;
import net.minecraft.class_7924;
import net.minecraft.class_8779;
import net.minecraft.class_8790;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagenRecipes;

/**
 * Fabric API 0.116's FabricRecipeProvider (superRenames; dev-time datagen) on 0.106's. 0.116's was a RecipeProvider
 * whose subclass implements buildRecipes(output); 0.106's is a RecipeProvider.Runner whose subclass makes an
 * instance-helper RecipeProvider. This class extends 0.106's (so an instance stands where that type is expected) and is
 * 0.116's: run(output) waits for the registries and runs run(output, registries), 0.116's body -- each recipe the
 * subclass's buildRecipes saves is encoded with the registries' JSON ops, given the resource conditions attached to it
 * (FabricDataGenHelper, unchanged between the two), and written with its advancement under recipe/ and advancement/,
 * a duplicate id refused. While buildRecipes runs, a 1.21.2 RecipeProvider bound to that output answers 1.21.1's static
 * helpers (CbItemDatagenRecipes). 0.106's own path (createRecipeProvider) builds through the same buildRecipes.
 */
public abstract class FabricRecipeProvider extends net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider {

    /** recipePathProvider (1.21.1 RecipeProvider's) */
    public final class_7784.class_7489 field_39378;
    /** advancementPathProvider */
    public final class_7784.class_7489 field_39379;
    private final CompletableFuture<class_7225.class_7874> cb$registries;

    public FabricRecipeProvider(FabricDataOutput output, CompletableFuture<class_7225.class_7874> registries) {
        super(output, registries);
        this.field_39378 = output.method_60917(class_7924.field_52178);
        this.field_39379 = output.method_60917(class_7924.field_52177);
        this.cb$registries = registries;
    }

    /** buildRecipes(output) */
    public abstract void method_10419(class_8790 output);

    /** getName(): 1.21.1 RecipeProvider's (final there), 0.106's Runner leaves it to the subclass */
    @Override
    public final String method_10321() {
        return "Recipes";
    }

    /** a call of 0.116's buildRecipes(output) through FabricRecipeProvider (instanceRedirects): the instance's */
    public static void buildRecipes(net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider provider, class_8790 output) {
        ((FabricRecipeProvider) provider).method_10419(output);
    }

    /** a call of 0.116's run(output, registries) through FabricRecipeProvider (instanceRedirects): the instance's */
    public static CompletableFuture<?> run(net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider provider,
            class_7403 writer, class_7225.class_7874 registries) {
        return ((FabricRecipeProvider) provider).method_56888(writer, registries);
    }

    /** 0.106's createRecipeProvider(registries, output): a provider whose buildRecipes is the subclass's 0.116 one */
    @Override
    protected class_2446 method_62766(class_7225.class_7874 registries, class_8790 output) {
        return CbItemDatagenRecipes.delegating(registries, output, this::method_10419);
    }

    /**
     * withConditions(exporter, conditions): 0.116's -- an output attaching the conditions to each recipe it passes on,
     * answering getRecipeIdentifier as the exporter does (0.106's does not forward it)
     */
    @Override
    public class_8790 withConditions(class_8790 exporter, ResourceCondition... conditions) {
        com.google.common.base.Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
        return new class_8790() {
            @Override
            public void method_53819(class_5321<class_1860<?>> key, class_1860<?> recipe, class_8779 advancement) {
                FabricDataGenHelper.addConditions(recipe, conditions);
                exporter.method_53819(key, recipe, advancement);
            }

            @Override
            public class_161.class_162 method_53818() {
                return exporter.method_53818();
            }

            @Override
            public void method_62738() {
                exporter.method_62738();
            }

            /** FabricRecipeExporter.getRecipeIdentifier: the exporter's */
            public class_2960 getRecipeIdentifier(class_2960 id) {
                return ((FabricRecipeExporter) (Object) exporter).getRecipeIdentifier(id);
            }
        };
    }

    /** getRecipeIdentifier(id): the id's path in the mod's namespace (0.116's, as 0.106's) */
    @Override
    public class_2960 getRecipeIdentifier(class_2960 id) {
        return super.getRecipeIdentifier(id);
    }

    /** run(output) */
    @Override
    public CompletableFuture<?> method_10319(class_7403 writer) {
        return this.cb$registries.thenCompose(registries -> this.method_56888(writer, registries));
    }

    /** run(output, registries): 0.116's body */
    public CompletableFuture<?> method_56888(class_7403 writer, class_7225.class_7874 registries) {
        Set<class_2960> generated = new HashSet<>();
        List<CompletableFuture<?>> writes = new ArrayList<>();
        FabricRecipeProvider self = this;
        class_8790 exporter = new class_8790() {
            @Override
            public void method_53819(class_5321<class_1860<?>> key, class_1860<?> recipe, class_8779 advancement) {
                class_2960 id = key.method_29177();
                if (!generated.add(id)) {
                    throw new IllegalStateException("Duplicate recipe " + id);
                }
                JsonObject json = class_1860.field_47319.encodeStart(registries.method_57093(JsonOps.INSTANCE), recipe)
                    .getOrThrow(IllegalStateException::new).getAsJsonObject();
                ResourceCondition[] conditions = FabricDataGenHelper.consumeConditions(recipe);
                FabricDataGenHelper.addConditions(json, conditions);
                writes.add(class_2405.method_10320(writer, json, self.field_39378.method_44107(id)));
                if (advancement != null) {
                    JsonObject adv = class_161.field_47179.encodeStart(registries.method_57093(JsonOps.INSTANCE), advancement.comp_1920())
                        .getOrThrow(IllegalStateException::new).getAsJsonObject();
                    FabricDataGenHelper.addConditions(adv, conditions);
                    writes.add(class_2405.method_10320(writer, adv, self.field_39379.method_44107(advancement.comp_1919())));
                }
            }

            @Override
            public class_161.class_162 method_53818() {
                return class_161.class_162.method_51698().method_708(class_5797.field_39377);
            }

            @Override
            public void method_62738() {
            }

            /** FabricRecipeExporter.getRecipeIdentifier: the provider's (the mod's namespace) */
            public class_2960 getRecipeIdentifier(class_2960 id) {
                return self.getRecipeIdentifier(id);
            }
        };
        CbItemDatagenRecipes.bindFor(registries, exporter);
        try {
            this.method_10419(exporter);
        } finally {
            CbItemDatagenRecipes.unbindFor();
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }
}
