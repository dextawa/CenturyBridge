package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.List;

/**
 * 1.20.6's UpdateOneTwentyOneProcessorLists (net/minecraft/class_9000), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9000 {
   public static final class_5321<class_5497> field_47512 = class_5321.method_29179(
      class_7924.field_41247, top.dext.centurybridge.step.v1_21.rt.CbWorldResources.newResourceLocation("trial_chambers_copper_bulb_degradation")
   );

   public static void method_55317(class_7891<class_5497> var0) {
      method_55318(
         var0,
         field_47512,
         List.of(
            new class_3826(
               List.of(
                  new class_3821(
                     new class_3824(class_2246.field_47076, 0.1F),
                     class_3818.field_16868,
                     class_2246.field_47079.method_9564().method_11657(class_8922.field_47082, Boolean.valueOf(true))
                  ),
                  new class_3821(
                     new class_3824(class_2246.field_47076, 0.33333334F),
                     class_3818.field_16868,
                     class_2246.field_47078.method_9564().method_11657(class_8922.field_47082, Boolean.valueOf(true))
                  ),
                  new class_3821(
                     new class_3824(class_2246.field_47076, 0.5F),
                     class_3818.field_16868,
                     class_2246.field_47077.method_9564().method_11657(class_8922.field_47082, Boolean.valueOf(true))
                  )
               )
            ),
            new class_6378(class_3481.field_33757)
         )
      );
   }

   private static void method_55318(class_7891<class_5497> var0, class_5321<class_5497> var1, List<class_3491> var2) {
      var0.method_46838(var1, new class_5497(var2));
   }
}
