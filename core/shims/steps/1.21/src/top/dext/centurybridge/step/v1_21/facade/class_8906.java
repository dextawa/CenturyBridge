package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_7225.class_7874;
import net.minecraft.class_7877.class_8993;

/**
 * 1.20.6's UpdateOneTwentyOneRegistries (net/minecraft/class_8906), part of the update_1_21 experiment, which 1.21 made
 * vanilla and removed: a 1.20.6 mod's references are renamed here (classRenames, s6fin-loot.json). The body is
 * 1.20.6's (decompiled from its bytecode), every member it reaches in the form the step's own wiring gives it
 * on 1.21 (CbLoot, CbData, CbWorldConstants, CbWorldResources, CbItemTags).
 */
public class class_8906 {
   private static final class_7877 field_47150 = new class_7877()
      .method_46777(class_7924.field_41236, class_9686::method_59883)
      .method_46777(class_7924.field_41249, class_8999::method_55315)
      .method_46777(class_7924.field_41246, class_9002::method_55321)
      .method_46777(class_7924.field_41248, class_9001::method_55319)
      .method_46777(class_7924.field_41247, class_9000::method_55317)
      .method_46777(class_7924.field_42534, class_9086::method_55843)
      .method_46777(class_7924.field_41252, class_9367::method_58189)
      .method_46777(class_7924.field_42082, class_9363::method_58185);

   public static CompletableFuture<class_8993> method_54659(CompletableFuture<class_7874> var0) {
      return class_8931.method_54840(var0, field_47150);
   }
}
