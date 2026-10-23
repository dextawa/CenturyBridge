package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.function.BiConsumer;
import net.minecraft.class_109.class_4954;
import net.minecraft.class_52.class_53;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneChestLoot (net/minecraft/class_8994), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_8994 implements class_7791, top.dext.centurybridge.step.v1_21.rt.CbLoot.Generate {
   /** 1.21's generate(consumer): 1.20.6's on the registries the pack was built with, made current (CbLoot.generate) */
   @Override
   public void method_10399(BiConsumer<class_5321<class_52>, class_53> consumer) {
      top.dext.centurybridge.step.v1_21.rt.CbLoot.generate(this, consumer);
   }

   /** 1.20.6's generate(registries, consumer) */
   @Override
   public void method_10399(class_7874 var1, BiConsumer<class_5321<class_52>, class_53> var2) {
      var2.accept(
         class_39.field_47421,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8107).method_438(class_141.method_621(class_5662.method_32462(4.0F, 8.0F))))
            )
      );
      var2.accept(
         class_39.field_47425,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8705).method_438(class_141.method_621(class_44.method_32448(1.0F))))
            )
      );
      var2.accept(
         class_39.field_47422,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8705).method_438(class_141.method_621(class_44.method_32448(1.0F))).method_437(4))
                  .method_351(class_77.method_411(class_1802.field_8107).method_438(class_141.method_621(class_5662.method_32462(4.0F, 8.0F))).method_437(4))
                  .method_351(class_77.method_411(class_1802.field_8543).method_438(class_141.method_621(class_5662.method_32462(4.0F, 8.0F))).method_437(6))
                  .method_351(class_77.method_411(class_1802.field_8803).method_438(class_141.method_621(class_5662.method_32462(4.0F, 8.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_8814).method_438(class_141.method_621(class_5662.method_32462(4.0F, 8.0F))).method_437(6))
                  .method_351(
                     class_77.method_411(class_1802.field_8436)
                        .method_438(class_6662.method_38927(class_1847.field_8996))
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F)))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8436)
                        .method_438(class_6662.method_38927(class_1847.field_8982))
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F)))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8436)
                        .method_438(class_6662.method_38927(class_1847.field_8975))
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F)))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_6662.method_38927(class_1847.field_8996))
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F)))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_6662.method_38927(class_1847.field_8982))
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F)))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_6662.method_38927(class_1847.field_8975))
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F)))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_6662.method_38927(class_1847.field_8963))
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F)))
                        .method_437(1)
                  )
            )
      );
      var2.accept(
         class_39.field_47426,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8687).method_438(class_141.method_621(class_5662.method_32462(1.0F, 8.0F))).method_437(100))
                  .method_351(class_77.method_411(class_1802.field_8759).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))).method_437(100))
                  .method_351(class_77.method_411(class_1802.field_27063).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))).method_437(100))
                  .method_351(class_77.method_411(class_1802.field_8107).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))).method_437(100))
                  .method_351(class_77.method_411(class_1802.field_8620).method_438(class_141.method_621(class_5662.method_32462(1.0F, 6.0F))).method_437(50))
                  .method_351(class_77.method_411(class_1802.field_27022).method_438(class_141.method_621(class_5662.method_32462(1.0F, 6.0F))).method_437(50))
                  .method_351(class_77.method_411(class_1802.field_47315).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))).method_437(20))
                  .method_351(class_77.method_411(class_1802.field_8695).method_438(class_141.method_621(class_5662.method_32462(1.0F, 6.0F))).method_437(20))
                  .method_351(class_77.method_411(class_1802.field_8477).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))).method_437(5))
                  .method_351(class_77.method_411(class_1802.field_8733).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))).method_437(5))
                  .method_351(class_77.method_411(class_1802.field_8603).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))).method_437(1))
            )
      );
      var2.accept(
         class_39.field_47416,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_5662.method_32462(3.0F, 5.0F))
                  .method_351(class_77.method_411(class_1802.field_8107).method_438(class_141.method_621(class_5662.method_32462(4.0F, 14.0F))).method_437(2))
                  .method_351(
                     class_77.method_411(class_1802.field_8087)
                        .method_438(class_141.method_621(class_5662.method_32462(4.0F, 8.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8982))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8087)
                        .method_438(class_141.method_621(class_5662.method_32462(4.0F, 8.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8996))
                        .method_437(1)
                  )
                  .method_351(class_77.method_411(class_1802.field_8512).method_438(class_141.method_621(class_5662.method_32462(2.0F, 4.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_28659).method_438(class_141.method_621(class_5662.method_32462(2.0F, 10.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_8651).method_438(class_141.method_621(class_5662.method_32462(3.0F, 6.0F))).method_437(1))
                  .method_351(class_77.method_411(class_1802.field_28654).method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F))).method_437(1))
                  .method_351(class_77.method_411(class_1802.field_8324).method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F))).method_437(1))
                  .method_351(class_77.method_411(class_1802.field_27021).method_438(class_141.method_621(class_5662.method_32462(5.0F, 10.0F))).method_437(1))
                  .method_351(class_77.method_411(class_1802.field_8810).method_438(class_141.method_621(class_5662.method_32462(3.0F, 6.0F))).method_437(1))
                  .method_351(
                     class_77.method_411(class_1802.field_8574)
                        .method_438(class_141.method_621(class_44.method_32448(2.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8986))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8574)
                        .method_438(class_141.method_621(class_44.method_32448(2.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8978))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8387)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.15F, 0.8F)))
                        .method_437(2)
                  )
                  .method_351(class_77.method_411(class_1802.field_8103).method_438(class_141.method_621(class_44.method_32448(1.0F))))
            )
      );
      var2.accept(
         class_39.field_47420,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_5662.method_32462(2.0F, 3.0F))
                  .method_351(class_77.method_411(class_1802.field_47315).method_438(class_141.method_621(class_44.method_32448(1.0F))).method_437(1))
                  .method_351(class_77.method_411(class_1802.field_8600).method_438(class_141.method_621(class_5662.method_32462(2.0F, 5.0F))).method_437(5))
                  .method_351(class_77.method_411(class_1802.field_8406).method_438(class_141.method_621(class_44.method_32448(1.0F))).method_437(10))
                  .method_351(class_77.method_411(class_1802.field_20414).method_438(class_141.method_621(class_5662.method_32462(2.0F, 8.0F))).method_437(10))
                  .method_351(class_77.method_411(class_1802.field_8107).method_438(class_141.method_621(class_5662.method_32462(5.0F, 10.0F))).method_437(10))
            )
      );
      var2.accept(
         class_39.field_47418,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_5662.method_32462(1.0F, 3.0F))
                  .method_351(class_77.method_411(class_1802.field_8603).method_438(class_141.method_621(class_44.method_32448(1.0F))).method_437(1))
                  .method_351(class_77.method_411(class_1802.field_8733).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))).method_437(5))
                  .method_351(
                     class_77.method_411(class_1802.field_8556)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.1F, 0.5F)))
                        .method_437(5)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8377)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.1F, 0.5F)))
                        .method_437(5)
                  )
                  .method_351(class_77.method_411(class_1802.field_8477).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))).method_437(10))
                  .method_351(class_77.method_411(class_1802.field_17534).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))).method_437(20))
                  .method_351(class_77.method_411(class_1802.field_27063).method_438(class_141.method_621(class_5662.method_32462(8.0F, 20.0F))).method_437(20))
                  .method_351(class_77.method_411(class_1802.field_8773).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))).method_437(20))
            )
      );
      var2.accept(
         class_39.field_47419,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_5662.method_32462(1.0F, 3.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_8556)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.4F, 0.9F)))
                        .method_438(class_109.method_489(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries()))
                        .method_437(1)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8377)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.15F, 0.8F)))
                        .method_437(1)
                  )
                  .method_351(class_77.method_411(class_1802.field_8477).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))).method_437(1))
                  .method_351(
                     class_77.method_411(class_1802.field_8251)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.15F, 0.8F)))
                        .method_437(1)
                  )
                  .method_351(class_77.method_411(class_1802.field_8550).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))).method_437(1))
                  .method_351(
                     class_77.method_411(class_1802.field_8825)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.15F, 0.8F)))
                        .method_437(4)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8335)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.15F, 0.8F)))
                        .method_437(4)
                  )
                  .method_351(class_77.method_411(class_1802.field_40213).method_438(class_141.method_621(class_5662.method_32462(5.0F, 15.0F))).method_437(10))
                  .method_351(class_77.method_411(class_1802.field_8512).method_438(class_141.method_621(class_5662.method_32462(6.0F, 10.0F))).method_437(10))
            )
      );
      var2.accept(
         class_39.field_47417,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_5662.method_32462(1.0F, 3.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_8475)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.4F, 0.9F)))
                        .method_438(class_109.method_489(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries()))
                        .method_437(1)
                  )
                  .method_351(class_77.method_411(class_1802.field_20414).method_438(class_141.method_621(class_5662.method_32462(2.0F, 8.0F))).method_437(1))
                  .method_351(
                     class_77.method_411(class_1802.field_8062)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.15F, 0.8F)))
                        .method_437(2)
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8387)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_149.method_633(class_5662.method_32462(0.15F, 0.8F)))
                        .method_437(2)
                  )
                  .method_351(class_77.method_411(class_1802.field_8634).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_40236).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_40213).method_438(class_141.method_621(class_5662.method_32462(3.0F, 6.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_16482).method_438(class_141.method_621(class_5662.method_32462(2.0F, 10.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_8810).method_438(class_141.method_621(class_5662.method_32462(3.0F, 6.0F))).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_27021).method_438(class_141.method_621(class_5662.method_32462(8.0F, 20.0F))).method_437(3))
            )
      );
      var2.accept(
         class_39.field_49845,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8687).method_437(3).method_438(class_141.method_621(class_5662.method_32462(2.0F, 4.0F))))
                  .method_351(class_77.method_411(class_1802.field_8255).method_437(3).method_438(class_149.method_633(class_5662.method_32462(0.5F, 1.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_8102)
                        .method_437(3)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(5.0F, 15.0F))))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8399)
                        .method_437(2)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(5.0F, 20.0F))))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8475)
                        .method_437(2)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(0.0F, 10.0F))))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8523)
                        .method_437(2)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(0.0F, 10.0F))))
                  )
                  .method_351(class_77.method_411(class_1802.field_8071).method_437(2).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_8529)
                        .method_437(2)
                        .method_438(
                           new class_4954()
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9118))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9112))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9131))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9130))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9099))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9129))
                        )
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8529)
                        .method_437(2)
                        .method_438(
                           new class_4954()
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9104))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9120))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9117))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9106))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9101))
                        )
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8058)
                        .method_437(1)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(5.0F, 15.0F))))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8556)
                        .method_437(1)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(5.0F, 15.0F))))
                  )
            )
      );
      var2.accept(
         class_39.field_49844,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8107).method_437(4).method_438(class_141.method_621(class_5662.method_32462(2.0F, 8.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_8087)
                        .method_437(4)
                        .method_438(class_141.method_621(class_5662.method_32462(2.0F, 8.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8982))
                  )
                  .method_351(class_77.method_411(class_1802.field_8687).method_437(4).method_438(class_141.method_621(class_5662.method_32462(2.0F, 4.0F))))
                  .method_351(class_77.method_411(class_1802.field_49098).method_437(3).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))))
                  .method_351(class_77.method_411(class_1802.field_8620).method_437(3).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))))
                  .method_351(class_77.method_411(class_1802.field_20417).method_437(3).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_50140)
                        .method_437(2)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_9476.method_58737(class_5662.method_32462(0.0F, 1.0F)))
                  )
                  .method_351(class_77.method_411(class_1802.field_49098).method_437(1).method_438(class_141.method_621(class_5662.method_32462(4.0F, 12.0F))))
                  .method_351(class_77.method_411(class_1802.field_8477).method_437(1).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))))
            )
      );
      var2.accept(
         class_39.field_49846,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8463).method_437(6))
                  .method_351(class_77.method_411(class_1802.field_49818).method_437(3))
                  .method_351(class_77.method_411(class_1802.field_49816).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_8547).method_437(1))
            )
      );
      var2.accept(
         class_39.field_47415,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_83.method_428(class_39.field_49845).method_437(8))
                  .method_351(class_83.method_428(class_39.field_49844).method_437(2))
            )
            .method_336(class_55.method_347().method_352(class_5662.method_32462(1.0F, 3.0F)).method_351(class_83.method_428(class_39.field_49844)))
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_356(class_219.method_932(0.25F))
                  .method_351(class_83.method_428(class_39.field_49846))
            )
      );
      var2.accept(
         class_39.field_50196,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8733).method_437(6))
                  .method_351(class_77.method_411(class_1802.field_8773).method_437(4))
                  .method_351(
                     class_77.method_411(class_1802.field_8399)
                        .method_437(4)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(5.0F, 20.0F))))
                  )
                  .method_351(class_77.method_411(class_1802.field_8463).method_437(4))
                  .method_351(
                     class_77.method_411(class_1802.field_8556)
                        .method_437(3)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(10.0F, 20.0F))))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8058)
                        .method_437(3)
                        .method_438(top.dext.centurybridge.step.v1_21.rt.CbLoot.method_484(class_106.method_481(top.dext.centurybridge.step.v1_21.rt.CbLoot.registries(), class_5662.method_32462(10.0F, 20.0F))))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8529)
                        .method_437(2)
                        .method_438(
                           new class_4954()
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9121))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9116))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9123))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9110))
                              .method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_9108))
                        )
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8529)
                        .method_437(2)
                        .method_438(new class_4954().method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_50158)).method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_50157)))
                  )
                  .method_351(class_77.method_411(class_1802.field_8529).method_437(1).method_438(new class_4954().method_25992(top.dext.centurybridge.step.v1_21.rt.CbLoot.enchantment(class_1893.field_50159))))
                  .method_351(class_77.method_411(class_1802.field_8603).method_437(1))
            )
      );
      var2.accept(
         class_39.field_50195,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8687).method_437(5).method_438(class_141.method_621(class_5662.method_32462(4.0F, 10.0F))))
                  .method_351(class_77.method_411(class_1802.field_49098).method_437(4).method_438(class_141.method_621(class_5662.method_32462(8.0F, 12.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_8087)
                        .method_437(3)
                        .method_438(class_141.method_621(class_5662.method_32462(4.0F, 12.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8976))
                  )
                  .method_351(class_77.method_411(class_1802.field_8477).method_437(2).method_438(class_141.method_621(class_5662.method_32462(2.0F, 3.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_50140)
                        .method_437(1)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_9476.method_58737(class_5662.method_32462(2.0F, 4.0F)))
                  )
            )
      );
      var2.accept(
         class_39.field_50197,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8367).method_437(3))
                  .method_351(class_77.method_411(class_1802.field_49817).method_437(3))
                  .method_351(class_77.method_411(class_1802.field_49815).method_437(2))
                  .method_351(class_77.method_411(class_1802.field_49813).method_437(1))
            )
      );
      var2.accept(
         class_39.field_50194,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_83.method_428(class_39.field_50196).method_437(8))
                  .method_351(class_83.method_428(class_39.field_50195).method_437(2))
            )
            .method_336(class_55.method_347().method_352(class_5662.method_32462(1.0F, 3.0F)).method_351(class_83.method_428(class_39.field_50195)))
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_356(class_219.method_932(0.75F))
                  .method_351(class_83.method_428(class_39.field_50197))
            )
      );
      var2.accept(
         class_39.field_47423,
         class_52.method_324()
            .method_336(class_55.method_347().method_352(class_44.method_32448(1.0F)).method_351(class_77.method_411(class_1802.field_47315)))
      );
      var2.accept(
         class_39.field_47424,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8544).method_437(3).method_438(class_141.method_621(class_44.method_32448(1.0F))))
                  .method_351(class_77.method_411(class_1802.field_8229).method_437(3).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))))
                  .method_351(class_77.method_411(class_1802.field_8512).method_437(2).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_8574)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8986))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8574)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_9005))
                  )
                  .method_351(class_77.method_411(class_1802.field_8511).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))))
            )
      );
      var2.accept(
         class_39.field_50198,
         class_52.method_324()
            .method_336(class_55.method_347().method_352(class_44.method_32448(1.0F)).method_351(class_77.method_411(class_1802.field_50139)))
      );
      var2.accept(
         class_39.field_50199,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8176).method_437(3).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))))
                  .method_351(class_77.method_411(class_1802.field_8512).method_437(3).method_438(class_141.method_621(class_5662.method_32462(2.0F, 4.0F))))
                  .method_351(class_77.method_411(class_1802.field_8071).method_437(2).method_438(class_141.method_621(class_5662.method_32462(1.0F, 2.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_8574)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8986))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8574)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8978))
                  )
                  .method_351(class_77.method_411(class_1802.field_8511).method_438(class_141.method_621(class_5662.method_32462(1.0F, 4.0F))))
            )
      );
      var2.accept(
         class_39.field_50200,
         class_52.method_324()
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_50153))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_50155))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_50154))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_50156))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8978))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_9005))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8150)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8974))
                  )
            )
            .method_336(
               class_55.method_347()
                  .method_352(class_44.method_32448(1.0F))
                  .method_351(class_77.method_411(class_1802.field_8107).method_438(class_141.method_621(class_44.method_32448(1.0F))))
                  .method_351(
                     class_77.method_411(class_1802.field_8107)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8982))
                  )
                  .method_351(
                     class_77.method_411(class_1802.field_8107)
                        .method_438(class_141.method_621(class_44.method_32448(1.0F)))
                        .method_438(class_6662.method_38927(class_1847.field_8976))
                  )
                  .method_351(class_77.method_411(class_1802.field_8814).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))))
                  .method_351(class_77.method_411(class_1802.field_49098).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))))
                  .method_351(class_77.method_411(class_1802.field_8287).method_438(class_141.method_621(class_5662.method_32462(1.0F, 3.0F))))
            )
      );
   }
}
