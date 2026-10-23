package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.class_7225.class_7874;
import net.minecraft.class_7803.class_8905;

/**
 * 1.20.6's UpdateOneTwentyOneRecipeProvider (net/minecraft/class_8904), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_8904 extends class_2446 {
   public class_8904(class_7784 var1, CompletableFuture<class_7874> var2) {
      super(var1, var2);
   }

   @Override
   public void method_10419(class_8790 var1) {
      method_46207(var1, class_7699.method_45398(top.dext.centurybridge.step.v1_21.rt.CbWorldConstants.field_46779()));
      class_2447.method_10437(class_7800.field_40636, class_2246.field_46797)
         .method_10434('#', class_1802.field_8620)
         .method_10434('C', class_1802.field_8465)
         .method_10434('R', class_1802.field_8725)
         .method_10434('D', class_1802.field_8878)
         .method_10439("###")
         .method_10439("#C#")
         .method_10439("RDR")
         .method_10429("has_dropper", method_10426(class_1802.field_8878))
         .method_10431(var1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47027, class_2246.field_27165, 2);
      method_33717(var1, class_7800.field_40634, class_2246.field_47028, class_2246.field_27165);
      method_33717(var1, class_7800.field_40635, class_2246.field_47029, class_2246.field_27165);
      method_33717(var1, class_7800.field_40634, class_2246.field_47034, class_2246.field_27165);
      method_33717(var1, class_7800.field_40634, class_2246.field_47030, class_2246.field_27165);
      method_33715(var1, class_7800.field_40634, class_2246.field_47031, class_2246.field_27165, 2);
      method_33717(var1, class_7800.field_40634, class_2246.field_47032, class_2246.field_27165);
      method_33717(var1, class_7800.field_40635, class_2246.field_47033, class_2246.field_27165);
      method_33717(var1, class_7800.field_40634, class_2246.field_47035, class_2246.field_27165);
      method_33715(var1, class_7800.field_40634, class_2246.field_47036, class_2246.field_27165, 2);
      method_33717(var1, class_7800.field_40634, class_2246.field_47037, class_2246.field_27165);
      method_33717(var1, class_7800.field_40635, class_2246.field_47038, class_2246.field_27165);
      method_33717(var1, class_7800.field_40634, class_2246.field_47039, class_2246.field_27165);
      method_33715(var1, class_7800.field_40634, class_2246.field_47031, class_2246.field_47030, 2);
      method_33717(var1, class_7800.field_40634, class_2246.field_47032, class_2246.field_47030);
      method_33717(var1, class_7800.field_40635, class_2246.field_47033, class_2246.field_47030);
      method_33717(var1, class_7800.field_40634, class_2246.field_47035, class_2246.field_47030);
      method_33715(var1, class_7800.field_40634, class_2246.field_47036, class_2246.field_47030, 2);
      method_33717(var1, class_7800.field_40634, class_2246.field_47037, class_2246.field_47030);
      method_33717(var1, class_7800.field_40635, class_2246.field_47038, class_2246.field_47030);
      method_33717(var1, class_7800.field_40634, class_2246.field_47039, class_2246.field_47030);
      method_33715(var1, class_7800.field_40634, class_2246.field_47036, class_2246.field_47035, 2);
      method_33717(var1, class_7800.field_40634, class_2246.field_47037, class_2246.field_47035);
      method_33717(var1, class_7800.field_40635, class_2246.field_47038, class_2246.field_47035);
      method_33717(var1, class_7800.field_40634, class_2246.field_47039, class_2246.field_47035);
      method_33715(var1, class_7800.field_40634, class_2246.field_47057, class_2246.field_27119, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47056, class_2246.field_27118, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47055, class_2246.field_27117, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47054, class_2246.field_27116, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47061, class_2246.field_27133, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47060, class_2246.field_27135, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47059, class_2246.field_27134, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47058, class_2246.field_33407, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47057, class_2246.field_27124, 1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47056, class_2246.field_27123, 1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47055, class_2246.field_27122, 1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47054, class_2246.field_27121, 1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47061, class_2246.field_27138, 1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47060, class_2246.field_27137, 1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47059, class_2246.field_27136, 1);
      method_33715(var1, class_7800.field_40634, class_2246.field_47058, class_2246.field_33408, 1);
      method_54829(var1, class_2246.field_47064, class_2246.field_27119);
      method_54829(var1, class_2246.field_47065, class_2246.field_27118);
      method_54829(var1, class_2246.field_47066, class_2246.field_27117);
      method_54829(var1, class_2246.field_47067, class_2246.field_27116);
      method_54829(var1, class_2246.field_47068, class_2246.field_27133);
      method_54829(var1, class_2246.field_47069, class_2246.field_27135);
      method_54829(var1, class_2246.field_47070, class_2246.field_27134);
      method_54829(var1, class_2246.field_47071, class_2246.field_33407);
      method_54830(var1, class_2246.field_47072, class_2246.field_27119);
      method_54830(var1, class_2246.field_47073, class_2246.field_27118);
      method_54830(var1, class_2246.field_47074, class_2246.field_27117);
      method_54830(var1, class_2246.field_47075, class_2246.field_27116);
      method_54830(var1, class_2246.field_47076, class_2246.field_27133);
      method_54830(var1, class_2246.field_47077, class_2246.field_27135);
      method_54830(var1, class_2246.field_47078, class_2246.field_27134);
      method_54830(var1, class_2246.field_47079, class_2246.field_33407);
      method_33715(var1, class_7800.field_40634, class_2246.field_47064, class_2246.field_27119, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47065, class_2246.field_27118, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47066, class_2246.field_27117, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47067, class_2246.field_27116, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47068, class_2246.field_27133, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47069, class_2246.field_27135, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47070, class_2246.field_27134, 4);
      method_33715(var1, class_7800.field_40634, class_2246.field_47071, class_2246.field_33407, 4);
      method_58269().forEach(var1x -> method_48530(var1, var1x.comp_2012(), var1x.comp_2013()));
      method_48533(var1, class_1802.field_49817, class_1802.field_49821);
      method_48533(var1, class_1802.field_49818, class_1802.field_27071);
      class_2450.method_10448(class_7800.field_40642, class_1802.field_49098, 4)
         .method_10454(class_1802.field_49821)
         .method_10442("has_breeze_rod", method_10426(class_1802.field_49821))
         .method_10431(var1);
      class_2447.method_10436(class_7800.field_40639, class_1802.field_49814, 1)
         .method_10434('I', class_1802.field_49821)
         .method_10434('#', class_2246.field_49830)
         .method_10439(" # ")
         .method_10439(" I ")
         .method_10429("has_breeze_rod", method_10426(class_1802.field_49821))
         .method_10429("has_heavy_core", method_10426(class_2246.field_49830))
         .method_10431(var1);
      method_34854(var1, class_7699.method_45398(top.dext.centurybridge.step.v1_21.rt.CbWorldConstants.field_46779()));
   }

   public static Stream<class_8905> method_58269() {
      return Stream.of(class_1802.field_49818, class_1802.field_49817).map(var0 -> new class_8905(var0, top.dext.centurybridge.step.v1_21.rt.CbWorldResources.newResourceLocation(method_33716(var0) + "_smithing_trim")));
   }
}
