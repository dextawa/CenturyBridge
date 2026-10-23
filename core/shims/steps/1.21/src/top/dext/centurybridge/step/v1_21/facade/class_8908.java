package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2474.class_8211;
import net.minecraft.class_7225.class_7874;

/**
 * 1.20.6's UpdateOneTwentyOneItemTagsProvider (net/minecraft/class_8908), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_8908 extends class_7805 {
   public class_8908(
      class_7784 var1, CompletableFuture<class_7874> var2, CompletableFuture<class_8211<class_1792>> var3, CompletableFuture<class_8211<class_2248>> var4
   ) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void method_10514(class_7874 var1) {
      this.method_46827(class_3489.field_15526).method_46830(class_1802.field_46999, class_1802.field_47004, class_1802.field_47008);
      this.method_46827(class_3489.field_15535).method_46830(class_1802.field_46989, class_1802.field_47003, class_1802.field_47007);
      this.method_46827(class_3489.field_15560).method_46830(class_1802.field_47000, class_1802.field_47005, class_1802.field_47016);
      this.method_46827(class_3489.field_15553)
         .method_46830(
            class_1802.field_46982,
            class_1802.field_46983,
            class_1802.field_46984,
            class_1802.field_46985,
            class_1802.field_46986,
            class_1802.field_46987,
            class_1802.field_46988,
            class_1802.field_46990
         );
      this.method_46827(class_3489.field_15548)
         .method_46830(
            class_1802.field_46991,
            class_1802.field_46992,
            class_1802.field_46993,
            class_1802.field_46994,
            class_1802.field_46995,
            class_1802.field_46996,
            class_1802.field_46997,
            class_1802.field_46998
         );
      this.method_46827(class_3489.field_50109).method_46829(class_1802.field_49814);
      this.method_46827(class_3489.field_42610).method_46830(class_1802.field_49819, class_1802.field_49820, class_1802.field_49822);
      this.method_46827(class_3489.field_44701).method_46829(class_1802.field_49819).method_46829(class_1802.field_49820).method_46829(class_1802.field_49822);
      this.method_46827(class_3489.field_41892).method_46829(class_1802.field_49817).method_46829(class_1802.field_49818);
      this.method_46827(class_3489.field_48310).method_46829(class_1802.field_49814);
      this.method_46827(class_3489.field_48305).method_46829(class_1802.field_49814);
      this.method_46827(class_3489.field_50107).method_46829(class_1802.field_49814);
      this.method_46827(class_3489.field_48314).method_46829(class_1802.field_49814);
      this.method_46827(class_3489.field_42617).method_46829(class_1802.field_49814);
   }
}
