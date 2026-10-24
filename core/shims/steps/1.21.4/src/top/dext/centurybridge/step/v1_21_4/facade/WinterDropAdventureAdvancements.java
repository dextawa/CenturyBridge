package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.*;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_7225.class_7874;

/**
 * 1.21.3's WinterDropAdventureAdvancements (net/minecraft/class_10341, classRenames), a provider of 1.21.3's Winter Drop experimental data pack, gone in 1.21.4
 * where the Winter Drop is vanilla. The body is 1.21.3's (decompiled from its bytecode), every member it reaches in the form
 * the step's own wiring gives it on 1.21.4.
 */
public class WinterDropAdventureAdvancements implements class_7785 {
   @Override
   public void method_10335(class_7874 var1, Consumer<class_8779> var2) {
      class_8779 var3 = class_7785.method_55578("adventure/root");
      class_2412.method_55581(
         var3,
         var2,
         var1.method_46762(class_7924.field_41266),
         Stream.concat(class_2412.field_11294.stream(), Stream.of(top.dext.centurybridge.step.v1_21_4.rt.CbRestConstants.field_54561)).collect(Collectors.toList())
      );
      class_8779 var4 = class_7785.method_55578("adventure/sleep_in_bed");
      class_2412.method_49356(var1, var2, var4, top.dext.centurybridge.step.v1_21_4.rt.CbRestWorldgen.Presets.field_54706);
   }
}
