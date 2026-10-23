package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneDamageTypeTagsProvider (net/minecraft/class_8996), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_8996 extends class_2474<class_8110> {
   public class_8996(class_7784 var1, CompletableFuture<class_7874> var2) {
      super(var1, class_7924.field_42534, var2);
   }

   @Override
   public void method_10514(class_7874 var1) {
      this.method_10512(top.dext.centurybridge.step.v1_21.rt.CbItemTags.field_47214()).method_40565(class_8111.field_42321, class_8111.field_42322);
      this.method_10512(class_8103.field_42247).method_46835(class_8111.field_47737);
      this.method_10512(class_8103.field_46211).method_46835(class_8111.field_47737);
   }
}
