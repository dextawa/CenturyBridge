package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2446.class_10114;
import net.minecraft.class_7225.class_7874;

/**
 * 1.21.3's WinterDropRecipeProvider (net/minecraft/class_10344, classRenames), a provider of 1.21.3's Winter Drop experimental data pack, gone in 1.21.4
 * where the Winter Drop is vanilla. The body is 1.21.3's (decompiled from its bytecode), every member it reaches in the form
 * the step's own wiring gives it on 1.21.4.
 */
public class WinterDropRecipeProvider extends class_2446 {
   public WinterDropRecipeProvider(class_7874 var1, class_8790 var2) {
      super(var1, var2);
   }

   @Override
   public void method_10419() {
      // 1.21.3: generateForEnabledBlockFamilies({WINTER_DROP}) -- of the block families that generate recipes, only the variants
      // the Winter Drop flag enabled, i.e. the pale oak family's. Every 1.21.4 block is under the vanilla flag, so the same call
      // would write every family's recipes; the pale oak family is generated alone, under the flags this provider runs with.
      if (class_5793.field_54877.method_33478()) {
         this.method_33535(class_5793.field_54877, class_7699.method_45398(top.dext.centurybridge.step.v1_21_4.rt.CbRestConstants.field_54592));
      }
      this.method_46208(class_1802.field_54623, class_2246.field_54716);
      this.method_24475(class_2246.field_54735, class_3489.field_54555, 4);
      this.method_24476(class_2246.field_54734, class_2246.field_54715);
      this.method_24476(class_2246.field_54713, class_2246.field_54716);
      this.method_24478(class_1802.field_54620, class_2246.field_54735);
      this.method_42754(class_1802.field_54621, class_1802.field_54620);
      this.method_24885(class_2246.field_54732, class_2246.field_54731);
   }

   public static class Runner extends class_10114 {
      public Runner(class_7784 var1, CompletableFuture<class_7874> var2) {
         super(var1, var2);
      }

      @Override
      public class_2446 method_62766(class_7874 var1, class_8790 var2) {
         return new WinterDropRecipeProvider(var1, var2);
      }

      @Override
      public String method_10321() {
         return "Winter Drop Recipes";
      }
   }
}
