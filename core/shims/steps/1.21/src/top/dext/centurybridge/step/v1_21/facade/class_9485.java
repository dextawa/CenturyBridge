package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.function.BiConsumer;
import net.minecraft.class_52.class_53;
import net.minecraft.class_5642.class_6158;
import net.minecraft.class_7225.class_7226;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneEquipmentLoot (net/minecraft/class_9485), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9485 implements class_7791, top.dext.centurybridge.step.v1_21.rt.CbLoot.Generate {
   /** 1.21's generate(consumer): 1.20.6's on the registries the pack was built with, made current (CbLoot.generate) */
   @Override
   public void method_10399(BiConsumer<class_5321<class_52>, class_53> consumer) {
      top.dext.centurybridge.step.v1_21.rt.CbLoot.generate(this, consumer);
   }

   /** 1.20.6's generate(registries, consumer) */
   @Override
   public void method_10399(class_7874 var1, BiConsumer<class_5321<class_52>, class_53> var2) {
      class_7226<class_8056> var3 = var1.method_46759(class_7924.field_42082).orElseThrow();
      class_7226<class_8054> var4 = var1.method_46759(class_7924.field_42083).orElseThrow();
      class_8053 var5 = new class_8053(var4.method_46746(class_8055.field_42008).orElseThrow(), var3.method_46746(class_8057.field_49827).orElseThrow());
      class_8053 var6 = new class_8053(var4.method_46746(class_8055.field_42008).orElseThrow(), var3.method_46746(class_8057.field_49828).orElseThrow());
      var2.accept(
         class_39.field_50201,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_83.method_57631(method_58773(class_1802.field_8283, class_1802.field_8873, var6).method_338()).method_437(4))
                  .method_351(class_83.method_57631(method_58773(class_1802.field_8743, class_1802.field_8523, var5).method_338()).method_437(2))
                  .method_351(class_83.method_57631(method_58773(class_1802.field_8805, class_1802.field_8058, var5).method_338()).method_437(1))
            )
      );
      var2.accept(
         class_39.field_51415,
         class_52.method_324()
            .method_336(class_55.method_347().method_352(class_44.method_32448(1.0F)).method_351(class_83.method_428(class_39.field_50201)))
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8371).method_437(4))
                  .method_351(
                     class_77.method_411(class_1802.field_8371).method_438(new class_6158().method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9118), class_44.method_32448(1.0F)))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8371).method_438(new class_6158().method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9121), class_44.method_32448(1.0F)))
                  )
                  .method_351(class_77.method_411(class_1802.field_8802))
            )
      );
      var2.accept(
         class_39.field_51414,
         class_52.method_324()
            .method_336(class_55.method_347().method_352(class_44.method_32448(1.0F)).method_351(class_83.method_428(class_39.field_50201)))
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8102).method_437(2))
                  .method_351(
                     class_77.method_411(class_1802.field_8102).method_438(new class_6158().method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9103), class_44.method_32448(1.0F)))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8102).method_438(new class_6158().method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9116), class_44.method_32448(1.0F)))
                  )
            )
      );
   }

   public static class_53 method_58773(class_1792 var0, class_1792 var1, class_8053 var2) {
      return class_52.method_324()
         .method_336(
            class_55.method_347()
               .method_352(class_44.method_32448(1.0F))
               .method_356(class_219.method_932(0.5F))
               .method_351(
                  class_77.method_411(var0)
                     .method_438(class_9320.method_57650(class_9334.field_49607, var2))
                     .method_438(
                        new class_6158()
                           .method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9111), class_44.method_32448(4.0F))
                           .method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9096), class_44.method_32448(4.0F))
                           .method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9095), class_44.method_32448(4.0F))
                     )
               )
         )
         .method_336(
            class_55.method_347()
               .method_352(class_44.method_32448(1.0F))
               .method_356(class_219.method_932(0.5F))
               .method_351(
                  class_77.method_411(var1)
                     .method_438(class_9320.method_57650(class_9334.field_49607, var2))
                     .method_438(
                        new class_6158()
                           .method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9111), class_44.method_32448(4.0F))
                           .method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9096), class_44.method_32448(4.0F))
                           .method_35539(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9095), class_44.method_32448(4.0F))
                     )
               )
         );
   }
}
