package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.*;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2474.class_8211;
import net.minecraft.class_7225.class_7874;

/**
 * 1.21.3's WinterDropBiomeTagsProvider (net/minecraft/class_10347, classRenames), a provider of 1.21.3's Winter Drop experimental data pack, gone in 1.21.4
 * where the Winter Drop is vanilla. The body is 1.21.3's (decompiled from its bytecode), every member it reaches in the form
 * the step's own wiring gives it on 1.21.4.
 */
public class WinterDropBiomeTagsProvider extends class_2474<class_1959> {
   public WinterDropBiomeTagsProvider(class_7784 var1, CompletableFuture<class_7874> var2, CompletableFuture<class_8211<class_1959>> var3) {
      super(var1, class_7924.field_41236, var2, var3);
   }

   @Override
   public void method_10514(class_7874 var1) {
      this.method_10512(class_6908.field_36517).method_46835(WinterDropBiomes.field_54881);
      this.method_10512(class_6908.field_37395).method_46835(WinterDropBiomes.field_54881);
      this.method_10512(class_6908.field_37393).method_46835(WinterDropBiomes.field_54881);
      this.method_10512(class_6908.field_47213).method_46835(WinterDropBiomes.field_54881);
   }
}
