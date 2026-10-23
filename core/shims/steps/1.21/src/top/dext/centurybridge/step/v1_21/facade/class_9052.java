package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_161.class_162;
import net.minecraft.class_170.class_171;
import net.minecraft.class_2019.class_2020;
import net.minecraft.class_2022.class_2023;
import net.minecraft.class_2048.class_2049;
import net.minecraft.class_2073.class_2074;
import net.minecraft.class_2080.class_2083;
import net.minecraft.class_2090.class_2091;
import net.minecraft.class_2096.class_2099;
import net.minecraft.class_2115.class_2117;
import net.minecraft.class_2135.class_2137;
import net.minecraft.class_3735.class_5278;
import net.minecraft.class_4550.class_4710;
import net.minecraft.class_4559.class_4560;
import net.minecraft.class_4711.class_4712;
import net.minecraft.class_7225.class_7874;
import net.minecraft.class_8508.class_8509;
import net.minecraft.class_9421.class_9422;

/**
 * 1.20.6's UpdateOneTwentyOneAdventureAdvancements (net/minecraft/class_9052), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_9052 implements class_7785 {
   @Override
   public void method_10335(class_7874 var1, Consumer<class_8779> var2) {
      class_8779 var3 = class_7785.method_55578("adventure/root");
      class_2412.method_55581(
         var3, var2, java.util.stream.Stream.concat(top.dext.centurybridge.step.v1_21.mixin.CbDataAdventureAccessor.cb$mobsToKill().stream().filter(e -> e != class_1299.field_47244 && e != class_1299.field_49148), Stream.of(class_1299.field_47244, class_1299.field_49148)).collect(Collectors.toList())
      );
      class_8779 var4 = class_162.method_707()
         .method_701(var3)
         .method_697(
            class_2246.field_47034,
            class_2561.method_43471("advancements.adventure.minecraft_trials_edition.title"),
            class_2561.method_43471("advancements.adventure.minecraft_trials_edition.description"),
            null,
            class_189.field_1254,
            true,
            true,
            false
         )
         .method_705(
            "minecraft_trials_edition",
            class_2137.method_43137(class_2091.method_53183(var1.method_46762(class_7924.field_41246).method_46747(class_7058.field_47411)))
         )
         .method_694(var2, "adventure/minecraft_trials_edition");
      class_162.method_707()
         .method_701(var4)
         .method_697(
            class_1802.field_47018,
            class_2561.method_43471("advancements.adventure.lighten_up.title"),
            class_2561.method_43471("advancements.adventure.lighten_up.description"),
            null,
            class_189.field_1254,
            true,
            true,
            false
         )
         .method_705(
            "lighten_up",
            class_4712.method_27981(
               class_2091.method_22484()
                  .method_27989(
                     class_4710.method_23880()
                        .method_27962(
                           class_2246.field_47075,
                           class_2246.field_47074,
                           class_2246.field_47073,
                           class_2246.field_47079,
                           class_2246.field_47078,
                           class_2246.field_47077
                        )
                        .method_27963(class_4560.method_22523().method_22527(class_8922.field_47082, true))
                  ),
               class_2074.method_8973().method_8977(class_2414.field_33964)
            )
         )
         .method_694(var2, "adventure/lighten_up");
      class_8779 var5 = class_162.method_707()
         .method_701(var4)
         .method_697(
            class_1802.field_47315,
            class_2561.method_43471("advancements.adventure.under_lock_and_key.title"),
            class_2561.method_43471("advancements.adventure.under_lock_and_key.description"),
            null,
            class_189.field_1254,
            true,
            true,
            false
         )
         .method_705(
            "under_lock_and_key",
            class_4712.method_27981(
               class_2091.method_22484().method_27989(class_4710.method_23880().method_27962(class_2246.field_48851)),
               class_2074.method_8973().method_8977(class_1802.field_47315)
            )
         )
         .method_694(var2, "adventure/under_lock_and_key");
      class_162.method_707()
         .method_701(var5)
         .method_697(
            class_1802.field_50139,
            class_2561.method_43471("advancements.adventure.revaulting.title"),
            class_2561.method_43471("advancements.adventure.revaulting.description"),
            null,
            class_189.field_1249,
            true,
            true,
            false
         )
         .method_705(
            "revaulting",
            class_4712.method_27981(
               class_2091.method_22484()
                  .method_27989(
                     class_4710.method_23880()
                        .method_27962(class_2246.field_48851)
                        .method_27963(class_4560.method_22523().method_22527(class_9197.field_50171, true))
                  ),
               class_2074.method_8973().method_8977(class_1802.field_50139)
            )
         )
         .method_694(var2, "adventure/revaulting");
      class_162.method_707()
         .method_701(var4)
         .method_697(
            class_1802.field_49098,
            class_2561.method_43471("advancements.adventure.blowback.title"),
            class_2561.method_43471("advancements.adventure.blowback.description"),
            null,
            class_189.field_1250,
            true,
            true,
            false
         )
         .method_703(class_171.method_750(40))
         .method_705(
            "blowback",
            class_2083.method_9001(
               class_2049.method_8916().method_8921(class_1299.field_47244),
               class_2023.method_8855()
                  .method_48785(class_8129.method_48965(class_8103.field_42247))
                  .method_8854(class_2049.method_8916().method_8921(class_1299.field_49075))
            )
         )
         .method_694(var2, "adventure/blowback");
      class_162.method_707()
         .method_701(var3)
         .method_697(
            class_1802.field_46791,
            class_2561.method_43471("advancements.adventure.crafters_crafting_crafters.title"),
            class_2561.method_43471("advancements.adventure.crafters_crafting_crafters.description"),
            null,
            class_189.field_1254,
            true,
            true,
            false
         )
         .method_705("crafter_crafted_crafter", class_8509.method_58450(top.dext.centurybridge.step.v1_21.rt.CbWorldResources.newResourceLocation("minecraft:crafter")))
         .method_694(var2, "adventure/crafters_crafting_crafters");
      class_162.method_707()
         .method_701(var4)
         .method_697(
            class_1802.field_49098,
            class_2561.method_43471("advancements.adventure.who_needs_rockets.title"),
            class_2561.method_43471("advancements.adventure.who_needs_rockets.description"),
            null,
            class_189.field_1254,
            true,
            true,
            false
         )
         .method_705(
            "who_needs_rockets",
            class_9422.method_58361(class_2025.method_8856(class_2099.method_9050(7.0)), class_2049.method_8916().method_8921(class_1299.field_47243))
         )
         .method_694(var2, "adventure/who_needs_rockets");
      class_162.method_707()
         .method_701(var4)
         .method_697(
            class_1802.field_49814,
            class_2561.method_43471("advancements.adventure.overoverkill.title"),
            class_2561.method_43471("advancements.adventure.overoverkill.description"),
            null,
            class_189.field_1250,
            true,
            true,
            false
         )
         .method_703(class_171.method_750(50))
         .method_705(
            "overoverkill",
            class_2117.method_35297(
               class_2020.method_8844()
                  .method_35118(class_2099.method_9050(100.0))
                  .method_8842(
                     class_2023.method_8855()
                        .method_48785(class_8129.method_48965(class_8103.field_50104))
                        .method_8854(
                           class_2049.method_8916()
                              .method_8921(class_1299.field_6097)
                              .method_53141(class_5278.method_27965().method_35195(class_2074.method_8973().method_8977(class_1802.field_49814)))
                        )
                  )
            )
         )
         .method_694(var2, "adventure/overoverkill");
   }
}
