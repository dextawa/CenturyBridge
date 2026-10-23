package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2474.class_8211;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneStructureTagsProvider (net/minecraft/class_9447), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9447 extends class_2474<class_3195> {
   public class_9447(class_7784 var1, CompletableFuture<class_7874> var2, CompletableFuture<class_8211<class_3195>> var3) {
      super(var1, class_7924.field_41246, var2, var3);
   }

   @Override
   protected void method_10514(class_7874 var1) {
      this.method_10512(class_7045.field_49958).method_46835(class_7058.field_47411);
   }
}
