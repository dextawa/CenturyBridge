package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2438.class_7790;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneLootTableProvider (net/minecraft/class_8903), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_8903 {
   public static class_2438 method_54653(class_7784 var0, CompletableFuture<class_7874> var1) {
      return new class_2438(
         var0,
         Set.of(),
         List.of(
            top.dext.centurybridge.step.v1_21.rt.CbLoot.newSubProviderEntry(class_8902::new, class_173.field_1172),
            top.dext.centurybridge.step.v1_21.rt.CbLoot.newSubProviderEntry(class_8994::new, class_173.field_1179),
            top.dext.centurybridge.step.v1_21.rt.CbLoot.newSubProviderEntry(class_9249::new, class_173.field_1173),
            top.dext.centurybridge.step.v1_21.rt.CbLoot.newSubProviderEntry(class_9339::new, class_173.field_49446),
            top.dext.centurybridge.step.v1_21.rt.CbLoot.newSubProviderEntry(class_9485::new, class_173.field_50217)
         ),
         var1
      );
   }
}
