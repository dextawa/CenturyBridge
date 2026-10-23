package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneEntityTypeTagsProvider (net/minecraft/class_8997), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_8997 extends class_7889<class_1299<?>> {
   public class_8997(class_7784 var1, CompletableFuture<class_7874> var2) {
      super(var1, class_7924.field_41266, var2, var0 -> var0.method_40124().method_40237());
   }

   @Override
   protected void method_10514(class_7874 var1) {
      this.method_46827(class_3483.field_42971).method_46829(class_1299.field_47244);
      this.method_46827(class_3483.field_48286).method_46829(class_1299.field_47244);
      this.method_46827(class_3483.field_47578).method_46829(class_1299.field_47244);
      this.method_46827(class_3483.field_22415).method_46830(class_1299.field_47243, class_1299.field_49075);
      this.method_46827(class_3483.field_48713)
         .method_46830(
            class_1299.field_47244,
            class_1299.field_6137,
            class_1299.field_49148,
            class_1299.field_6098,
            class_1299.field_6051,
            class_1299.field_6071,
            class_1299.field_6079,
            class_1299.field_6084,
            class_1299.field_6069
         );
      this.method_46827(class_3483.field_15507).method_46829(class_1299.field_49148);
      this.method_46827(class_3483.field_50106).method_46829(class_1299.field_6125);
      this.method_46827(class_3483.field_50105).method_46829(class_1299.field_6069);
      this.method_46827(class_3483.field_51503).method_46830(class_1299.field_47243, class_1299.field_49075);
   }
}
