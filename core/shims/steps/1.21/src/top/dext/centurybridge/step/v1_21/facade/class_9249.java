package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import net.minecraft.class_2048.class_2049;
import net.minecraft.class_47.class_50;

/**
 * 1.20.6's UpdateOneTwentyOneEntityLoot (net/minecraft/class_9249), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9249 extends class_7789 {
   protected class_9249() {
      super(class_7699.method_45399(class_7701.field_40177, top.dext.centurybridge.step.v1_21.rt.CbWorldConstants.field_46779()), class_7699.method_45398(top.dext.centurybridge.step.v1_21.rt.CbWorldConstants.field_46779()), top.dext.centurybridge.step.v1_21.rt.CbLoot.registries());
   }

   @Override
   public void method_10400() {
      this.method_46029(
         class_1299.field_47244,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_49821)
                        .method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F)))
                        .method_438(class_125.method_547(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(1.0F, 2.0F)))
                  )
                  .method_356(class_221.method_939())
            )
      );
      this.method_46029(
         class_1299.field_49148,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_8107)
                        .method_438(class_141.method_621(class_5662.method_32462(0.0F, 2.0F)))
                        .method_438(class_125.method_547(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(0.0F, 1.0F)))
                  )
            )
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_8606)
                        .method_438(class_141.method_621(class_5662.method_32462(0.0F, 2.0F)))
                        .method_438(class_125.method_547(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(0.0F, 1.0F)))
                  )
            )
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_8087)
                        .method_438(class_141.method_621(class_5662.method_32462(0.0F, 1.0F)))
                        .method_438(class_125.method_547(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(0.0F, 1.0F)).method_551(1))
                        .method_438(class_6662.method_38927(class_1847.field_8982))
                  )
                  .method_356(class_221.method_939())
            )
      );
      this.method_46029(
         class_1299.field_6105,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_50140)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_9476.method_58737(class_5662.method_32462(0.0F, 4.0F)))
                  )
                  .method_356(class_215.method_917(class_50.field_935, class_2049.method_8916().method_43094(class_9471.field_50163)))
            )
      );
   }
}
