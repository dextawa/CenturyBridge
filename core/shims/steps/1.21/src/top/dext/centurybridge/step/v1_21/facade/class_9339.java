package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.function.BiConsumer;
import net.minecraft.class_52.class_53;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneShearingLoot (net/minecraft/class_9339), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9339 implements class_7791, top.dext.centurybridge.step.v1_21.rt.CbLoot.Generate {
   /** 1.21's generate(consumer): 1.20.6's on the registries the pack was built with, made current (CbLoot.generate) */
   @Override
   public void method_10399(BiConsumer<class_5321<class_52>, class_53> consumer) {
      top.dext.centurybridge.step.v1_21.rt.CbLoot.generate(this, consumer);
   }

   /** 1.20.6's generate(registries, consumer) */
   @Override
   public void method_10399(class_7874 var1, BiConsumer<class_5321<class_52>, class_53> var2) {
      var2.accept(
         class_39.field_49427,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(2.0F))
                  .method_351(class_77.method_411(class_1802.field_17516).method_438(class_141.method_621(class_44.method_32448(1.0F))))
                  .method_351(class_77.method_411(class_1802.field_17517).method_438(class_141.method_621(class_44.method_32448(1.0F))))
            )
      );
   }
}
