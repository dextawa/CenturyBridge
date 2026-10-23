package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.class_2893.class_2895;
import net.minecraft.class_7061.class_7062;

/**
 * 1.20.6's UpdateOneTwentyOneStructures (net/minecraft/class_9002), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9002 {
   public static void method_55321(class_7891<class_3195> var0) {
      class_7871 var1 = var0.method_46799(class_7924.field_41236);
      class_7871 var2 = var0.method_46799(class_7924.field_41249);
      var0.method_46838(
         class_7058.field_47411,
         new class_5434(
            top.dext.centurybridge.step.v1_21.rt.CbData.method_42759(
               var1.method_46735(class_6908.field_47213),
               Arrays.stream(class_1311.values())
                  .collect(Collectors.toMap(var0x -> (class_1311)var0x, var0x -> new class_7061(class_7062.field_37199, class_6012.method_34990()))),
               class_2895.field_13172,
               class_5847.field_51413
            ),
            var2.method_46747(class_8998.field_47507),
            Optional.empty(),
            20,
            class_6124.method_35396(class_5843.method_33841(-40), class_5843.method_33841(-20)),
            false,
            Optional.empty(),
            116,
            class_8998.field_47510,
            top.dext.centurybridge.step.v1_21.rt.CbData.defaultPadding(),
            top.dext.centurybridge.step.v1_21.rt.CbData.defaultLiquidSettings()
         )
      );
   }
}
