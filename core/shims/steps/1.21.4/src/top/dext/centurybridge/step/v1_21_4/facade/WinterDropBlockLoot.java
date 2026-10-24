package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.*;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.class_7225.class_7874;

/**
 * 1.21.3's WinterDropBlockLoot (net/minecraft/class_10342, classRenames), a provider of 1.21.3's Winter Drop experimental data pack, gone in 1.21.4
 * where the Winter Drop is vanilla. The body is 1.21.3's (decompiled from its bytecode), every member it reaches in the form
 * the step's own wiring gives it on 1.21.4.
 * <p>
 * 1.21.3 ran it with enabled features {WINTER_DROP}: generate(output) emitted, and demanded, the table of every block that flag
 * enabled -- the Winter Drop's blocks -- and nothing else. Every 1.21.4 block is under the vanilla flag, so that check would demand
 * every vanilla block's table; generate(output) keeps 1.21.3's check over the Winter Drop's blocks, the blocks whose tables
 * 1.21.3's winter_drop pack held (data/minecraft/datapacks/winter_drop/.../loot_table/blocks, 22 tables).
 */
public class WinterDropBlockLoot extends class_7788 {
   public WinterDropBlockLoot(class_7874 var1) {
      super(Set.of(), class_7699.method_45398(top.dext.centurybridge.step.v1_21_4.rt.CbRestConstants.field_54592), var1);
   }

   @Override
   public void method_10379() {
      this.method_46025(class_2246.field_54735);
      this.method_46025(class_2246.field_54712);
      this.method_46025(class_2246.field_54715);
      this.method_46025(class_2246.field_54716);
      this.method_46025(class_2246.field_54734);
      this.method_46025(class_2246.field_54713);
      this.method_46025(class_2246.field_54718);
      this.method_46025(class_2246.field_54722);
      this.method_46025(class_2246.field_54720);
      this.method_46025(class_2246.field_54723);
      this.method_46025(class_2246.field_54724);
      this.method_46025(class_2246.field_54726);
      this.method_46025(class_2246.field_54730);
      this.method_46025(class_2246.field_54728);
      this.method_45994(class_2246.field_54732, var1 -> this.method_64930(var1));
      this.method_45994(class_2246.field_54733, var1 -> this.method_65053(var1));
      this.method_46025(class_2246.field_54731);
      this.method_46023(class_2246.field_54725);
      this.method_45994(class_2246.field_54727, var1 -> this.method_45980(var1));
      this.method_45994(class_2246.field_54729, var1 -> this.method_46022(var1));
      this.method_45994(class_2246.field_54714, var1 -> this.method_45986(var1, class_2246.field_54712, field_40605));
      this.method_46024(class_2246.field_54717);
   }

   /** the loot tables of the blocks 1.21.3's WINTER_DROP flag enabled (1.21.3's winter_drop pack, loot_table/blocks) */
   public static final Set<String> WINTER_DROP_BLOCK_TABLES = Set.of(
      "creaking_heart", "pale_hanging_moss", "pale_moss_block", "pale_moss_carpet", "pale_oak_button", "pale_oak_door",
      "pale_oak_fence", "pale_oak_fence_gate", "pale_oak_hanging_sign", "pale_oak_leaves", "pale_oak_log", "pale_oak_planks",
      "pale_oak_pressure_plate", "pale_oak_sapling", "pale_oak_sign", "pale_oak_slab", "pale_oak_stairs", "pale_oak_trapdoor",
      "pale_oak_wood", "potted_pale_oak_sapling", "stripped_pale_oak_log", "stripped_pale_oak_wood");

   /** generate(output): 1.21.3's body over the blocks its enabled features (WINTER_DROP) enabled */
   @Override
   public void method_10399(BiConsumer<class_5321<class_52>, class_52.class_53> output) {
      this.method_10379();
      Set<class_5321<class_52>> seen = new HashSet<>();
      for (class_2248 block : class_7923.field_41175) {
         block.method_26162().ifPresent(key -> {
            String path = key.method_29177().method_12832();
            if (!key.method_29177().method_12836().equals("minecraft") || !path.startsWith("blocks/")
                  || !WINTER_DROP_BLOCK_TABLES.contains(path.substring("blocks/".length())) || !seen.add(key)) {
               return;
            }
            class_52.class_53 table = this.field_40610.remove(key);
            if (table == null) {
               throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", key.method_29177(),
                  class_7923.field_41175.method_10221(block)));
            }
            output.accept(key, table);
         });
      }
      if (!this.field_40610.isEmpty()) {
         throw new IllegalStateException("Created block loot tables for non-blocks: " + this.field_40610.keySet());
      }
   }
}
