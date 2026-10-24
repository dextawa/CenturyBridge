package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_7225.class_7874;
import net.minecraft.class_7877.class_8993;

/**
 * 1.21.3's WinterDropRegistries (net/minecraft/class_10346, classRenames), a provider of 1.21.3's Winter Drop experimental data pack, gone in 1.21.4
 * where the Winter Drop is vanilla. The body is 1.21.3's (decompiled from its bytecode), every member it reaches in the form
 * the step's own wiring gives it on 1.21.4.
 */
public class WinterDropRegistries {
   public static final class_7877 field_54880 = new class_7877()
      .method_46777(class_7924.field_41236, WinterDropBiomes::method_64957)
      .method_46777(class_7924.field_43089, top.dext.centurybridge.step.v1_21_4.rt.CbRestWorldgen::method_64761);

   public static CompletableFuture<class_8993> method_64951(CompletableFuture<class_7874> var0) {
      return class_8931.method_54840(var0, field_54880).thenApply(var0x -> {
         class_7887.method_49382(var0x.comp_2113().method_46762(class_7924.field_41245), var0x.comp_2113().method_46762(class_7924.field_41236));
         return (class_8993)var0x;
      });
   }
}
