package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2438.class_7790;
import net.minecraft.class_7225.class_7874;

/**
 * 1.21.3's WinterDropLootTableProvider (net/minecraft/class_10343, classRenames), a provider of 1.21.3's Winter Drop experimental data pack, gone in 1.21.4
 * where the Winter Drop is vanilla. The body is 1.21.3's (decompiled from its bytecode), every member it reaches in the form
 * the step's own wiring gives it on 1.21.4.
 */
public class WinterDropLootTableProvider {
   public static class_2438 method_64939(class_7784 var0, CompletableFuture<class_7874> var1) {
      return new class_2438(var0, Set.of(), List.of(new class_7790(WinterDropBlockLoot::new, class_173.field_1172)), var1);
   }
}
