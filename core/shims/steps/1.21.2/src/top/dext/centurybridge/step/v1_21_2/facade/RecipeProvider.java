package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_161;
import net.minecraft.class_1860;
import net.minecraft.class_2405;
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
 * 1.21.1's RecipeProvider (net/minecraft/class_2446) as the superclass of a 1.21.1 mod's recipe provider (superRenames;
 * dev-time datagen). 1.21.2 split it into an instance-helper RecipeProvider bound to registries and an output, and a
 * RecipeProvider.Runner data provider that makes one; a 1.21.1 subclass is the data provider itself, with
 * buildRecipes(output) and static helpers. This class is 1.21.1's data provider, bodies 1.21.1's: run waits for the
 * registries, run(output, registries) hands buildRecipes an output that saves each recipe (and its advancement) as
 * JSON under data/&lt;ns&gt;/recipe and data/&lt;ns&gt;/advancement, refusing a duplicate id. While buildRecipes runs, a
 * 1.21.2 provider bound to that output answers the 1.21.1 static helpers (CbItemDatagenRecipes). It extends 1.21.2's
 * RecipeProvider (bound to the built-in registries and an output that takes nothing) so that an instance stands where
 * that type is expected. The members 1.21.1 kept package-private are public (an access widener never reaches a
 * CenturyBridge class); 1.21.1's private registries future is cb$registries (1.21.2's class has a field of that name).
 */
public abstract class RecipeProvider extends net.minecraft.class_2446 implements class_2405 {

    /** recipePathProvider */
    public final class_7784.class_7489 field_39378;
    /** advancementPathProvider */
    public final class_7784.class_7489 field_39379;
    /** registries (1.21.1's field of this name; 1.21.2's RecipeProvider has a registries field of its own type) */
    public final CompletableFuture<class_7225.class_7874> cb$registries;

    public RecipeProvider(class_7784 output, CompletableFuture<class_7225.class_7874> registries) {
        super(CbItemDatagenRecipes.builtIn(), CbItemDatagenRecipes.nowhere());
        this.field_39378 = output.method_60917(class_7924.field_52178);
        this.field_39379 = output.method_60917(class_7924.field_52177);
        this.cb$registries = registries;
    }

    /** run(output): once the registries are there */
    @Override
    public final CompletableFuture<?> method_10319(class_7403 output) {
        return this.cb$registries.thenCompose(registries -> this.method_56888(output, registries));
    }

    /** run(output, registries): every recipe buildRecipes saves, written as JSON with its advancement */
    protected CompletableFuture<?> method_56888(class_7403 output, class_7225.class_7874 registries) {
        Set<class_2960> ids = new java.util.HashSet<>();
        List<CompletableFuture<?>> writes = new ArrayList<>();
        RecipeProvider self = this;
        class_8790 recipes = new class_8790() {
            @Override
            public void method_53819(class_5321<class_1860<?>> key, class_1860<?> recipe, class_8779 advancement) {
                class_2960 id = key.method_29177();
                if (!ids.add(id)) {
                    throw new IllegalStateException("Duplicate recipe " + id);
                }
                writes.add(class_2405.method_53496(output, registries, class_1860.field_47319, recipe, self.field_39378.method_44107(id)));
                if (advancement != null) {
                    writes.add(self.method_46206(output, registries, advancement));
                }
            }

            @Override
            public class_161.class_162 method_53818() {
                return class_161.class_162.method_51698().method_708(class_5797.field_39377);
            }

            /** 1.21.1's output had no root advancement of its own */
            @Override
            public void method_62738() {
            }
        };
        CbItemDatagenRecipes.bindFor(registries, recipes);
        try {
            this.method_10419(recipes);
        } finally {
            CbItemDatagenRecipes.unbindFor();
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    /** buildAdvancement(output, registries, advancement): the advancement as JSON */
    protected CompletableFuture<?> method_46206(class_7403 output, class_7225.class_7874 registries, class_8779 advancement) {
        return class_2405.method_53496(output, registries, class_161.field_47179, advancement.comp_1920(),
            this.field_39379.method_44107(advancement.comp_1919()));
    }

    /** buildRecipes(output) */
    protected abstract void method_10419(class_8790 output);

    /**
     * 1.21.2's buildRecipes(): a 1.21.1 provider builds through buildRecipes(output) in its own run; nothing asks this
     * side (the facade is a 1.21.2 RecipeProvider only so that it stands where one is typed)
     */
    @Override
    public void method_10419() {
    }

    /** getName() */
    @Override
    public final String method_10321() {
        return "Recipes";
    }
}
