package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import net.minecraft.class_5483.class_1964;

/**
 * 1.20.6's UpdateOneTwentyOneBiomeData (net/minecraft/class_9686), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9686 {
   public static void method_59883(class_7891<class_1959> var0) {
      class_7871 var1 = var0.method_46799(class_7924.field_41245);
      class_7871 var2 = var0.method_46799(class_7924.field_41238);
      class_1964 var3 = new class_1964(class_1299.field_49148, 50, 4, 4);
      var0.method_46838(class_1972.field_38748, top.dext.centurybridge.step.v1_21.rt.CbData.method_43232(var1, var2, var1x -> var1x.method_31011(class_1311.field_6302, var3)));
      var0.method_46838(class_1972.field_9471, top.dext.centurybridge.step.v1_21.rt.CbData.method_30708(var1, var2, var1x -> var1x.method_31011(class_1311.field_6302, var3)));
   }
}
