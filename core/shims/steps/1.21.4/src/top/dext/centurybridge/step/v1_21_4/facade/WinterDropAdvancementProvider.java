package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_7225.class_7874;

/**
 * 1.21.3's WinterDropAdvancementProvider (net/minecraft/class_10340, classRenames), a provider of 1.21.3's Winter Drop experimental data pack, gone in 1.21.4
 * where the Winter Drop is vanilla. The body is 1.21.3's (decompiled from its bytecode), every member it reaches in the form
 * the step's own wiring gives it on 1.21.4.
 */
public class WinterDropAdvancementProvider {
   public static class_2409 method_64929(class_7784 var0, CompletableFuture<class_7874> var1) {
      return new class_2409(var0, var1, List.of(new WinterDropAdventureAdvancements()));
   }
}
