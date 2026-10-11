package top.dext.centurybridge.rt.v1_21_1;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1856;

/**
 * The client half of Statics: static-redirect targets whose bodies name client-only
 * classes. A dedicated server loads Statics for every server-side redirect, and one
 * client type in a method it verifies is a NoClassDefFoundError for the whole class;
 * these members are only ever reached from client code.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class ClientStatics {

    /**
     * 1.20.1 InventoryScreen.drawEntity(ctx, x, y, size, mouseX, mouseY, entity): x/y is
     * where the feet go, mouseX/mouseY are already relative to the entity (the caller
     * subtracts the cursor). 1.20.2 replaced it with a scissor-box form that takes
     * ABSOLUTE cursor coordinates, so feeding the old arguments into it drew nothing (a
     * zero size) or looked the wrong way. The 1.20.1 body is ported verbatim instead:
     * the same head/body rotation, applied to the entity around the old renderer
     * {@link #method_48472}, then restored.
     */
    public static void method_2486(net.minecraft.class_332 context,
            int x, int y, int size, float mouseX, float mouseY,
            net.minecraft.class_1309 entity) {
        float yaw = (float) Math.atan(mouseX / 40.0F);
        float pitch = (float) Math.atan(mouseY / 40.0F);
        org.joml.Quaternionf rotation = new org.joml.Quaternionf().rotateZ((float) Math.PI);
        org.joml.Quaternionf camera = new org.joml.Quaternionf().rotateX(pitch * 20.0F * ((float) Math.PI / 180.0F));
        rotation.mul(camera);
        float bodyYaw = entity.field_6283;
        float entityYaw = entity.method_36454();
        float entityPitch = entity.method_36455();
        float prevHeadYaw = entity.field_6259;
        float headYaw = entity.field_6241;
        entity.field_6283 = 180.0F + yaw * 20.0F;
        entity.method_36456(180.0F + yaw * 40.0F);
        entity.method_36457(-pitch * 20.0F);
        entity.field_6241 = entity.method_36454();
        entity.field_6259 = entity.method_36454();
        method_48472(context, x, y, size, rotation, camera, entity);
        entity.field_6283 = bodyYaw;
        entity.method_36456(entityYaw);
        entity.method_36457(entityPitch);
        entity.field_6259 = prevHeadYaw;
        entity.field_6241 = headYaw;
    }

    /**
     * 1.20.1 EntityRenderer.drawEntity(ctx,x,y,size,rot,cam,entity)
     * gained a float+Vector3f pivot at 1.20.2. 4 client call sites.
     */
    public static void method_48472(net.minecraft.class_332 context,
            int x, int y, int size,
            org.joml.Quaternionf rotation, org.joml.Quaternionf cameraAngle,
            net.minecraft.class_1309 entity) {
        net.minecraft.class_490.method_48472(context, (float) x, (float) y, size,
                new org.joml.Vector3f(0, 0, 0), rotation, cameraAngle, entity);
    }

    /** was net/minecraft/class_276.method_1232(I)V */
    public static void method_1232(net.minecraft.class_276 self, int a0) {
        self.method_58226(a0);
    }

    /** was net/minecraft/class_310.method_1540()Z */
    public static boolean method_1540(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: MinecraftClient.isDemo has no equivalent in 1.20.5");
    }

    /** was net/minecraft/class_310.method_27466(Z)V */
    public static void method_27466(net.minecraft.class_310 self, boolean a0) {
        try {
            java.lang.reflect.Field field = net.minecraft.class_310.class.getDeclaredField("field_24209");
            field.setAccessible(true);
            field.setBoolean(self, a0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** was net/minecraft/class_312.method_1606()V */
    public static void method_1606(net.minecraft.class_312 self) {
        self.method_55793();
    }

    /** was net/minecraft/class_315$2.field_28779Lnet/minecraft/class_315; */
    public static net.minecraft.class_315 field_28779() {
        return net.minecraft.class_310.method_1551().field_1690;
    }

    /** was net/minecraft/class_315$3.field_28781Lnet/minecraft/class_315; */
    public static net.minecraft.class_315 field_28781() {
        throw new UnsupportedOperationException("CenturyBridge: field_28781 was an instance field of a deleted anonymous class and cannot be accessed without a receiver");
    }

    /** was net/minecraft/class_383.method_41712()F */
    public static float method_41712(net.minecraft.class_383 self) {
        return (float)self.method_2031() / (float)self.method_2032();
    }

    /** was net/minecraft/class_383.method_41713()F */
    public static float method_41713(net.minecraft.class_383 self) {
        return 1.0f;
    }

    /** was net/minecraft/class_389.field_44801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_44801() {
        return net.minecraft.class_389.field_44801.codec();
    }

    /** was net/minecraft/class_4381.method_21155(I)V */
    public static void method_21155(net.minecraft.class_4381 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21155(int) switched tabs by ordinal index; 1.20.5 CreateWorldScreen uses class_4381$class_4382 tab objects via field_49448/field_49449 with no int-indexed equivalent");
    }

    /** was net/minecraft/class_4406.method_21325()V */
    public static void method_21325(net.minecraft.class_4406 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4406.method_21325() was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/class_4406.method_21344(I)V */
    public static void method_21344(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4406.method_21344(int) was removed in 1.20.5; merchant recipe index selection moved to the screen widget (class_4877) and no int-accepting equivalent remains on class_4406");
        // CB-CONFIDENCE: high -- the new API surface has no method accepting an int parameter, confirming the old recipe-index setter has no direct replacement
    }

    /** was net/minecraft/class_4406.method_25189(I)I */
    public static int method_25189(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25189(I)I was removed in 1.20.5; creative inventory tab/page layout was reworked with no direct equivalent");
    }

    /** was net/minecraft/class_4406.method_25193(I)I */
    public static int method_25193(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25193(I)I removed in 1.20.5, no direct replacement in MerchantScreen");
    }

    /** was net/minecraft/class_4588.method_22919(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;FFFII)V */
    public void method_22919(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        ((net.minecraft.class_4588) (Object) this).method_22919(a0, a1, a2, a3, a4, 1.0f, a5, a6);
    }

    /** was net/minecraft/class_4588.method_22920(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;[FFFF[IIZ)V */
    public void method_22920(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float[] a2, float a3, float a4, float a5, int[] a6, int a7, boolean a8) {
        ((net.minecraft.class_4588) (Object) this).method_22920(a0, a1, a2, a3, a4, a5, 1.0f, a6, a7, a8);
    }

    /** was net/minecraft/class_4722.method_24066(Ljava/util/function/Consumer;)V */
    public static void method_24066(net.minecraft.class_4722 self, java.util.function.Consumer a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4722.method_24066(Consumer) was removed in 1.20.5; the model loading pipeline no longer collects model identifiers via a consumer callback");
        // CB-CONFIDENCE: high -- method explicitly deleted with no direct replacement; the 1.20.5 model loading pipeline was reworked and no new API accepts a Consumer of identifiers
    }

    /** was net/minecraft/class_500.method_2528(Ljava/util/List;)V */
    public static void method_2528(net.minecraft.class_500 self, java.util.List a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_500.method_2528(List) was removed in 1.20.5 with no direct replacement; no method in the new API accepts a List");
        // CB-CONFIDENCE: high -- the method is explicitly deleted with no direct replacement, and no remaining method in class_500 accepts a List parameter
    }

    /** was net/minecraft/class_5235$class_5236.field_24299Lnet/minecraft/class_5235; */
    public static net.minecraft.class_5235 field_24299() {
        throw new UnsupportedOperationException("CenturyBridge: field_24299 was the outer class_5235 instance reference on class_5235$class_5236 (now synthetic this$0); cannot be returned from a parameterless static shim without a receiver");
    }

    /** was net/minecraft/class_5235.method_27630(Lnet/minecraft/class_5235;)Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 method_27630(net.minecraft.class_5235 self, net.minecraft.class_5235 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27630 was deleted in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/class_5289$class_5291.field_24585Lnet/minecraft/class_5289; */
    public static net.minecraft.class_5289 field_24585() {
        throw new UnsupportedOperationException("CenturyBridge: field_24585 was the synthetic outer class_5289 reference (this$0) of inner class class_5289$class_5291; it is an instance field bound to a specific instance and cannot be reconstructed as a static constant without a receiver");
    }

    /** was net/minecraft/class_5602.field_47444Lnet/minecraft/class_5601; */
    public static net.minecraft.class_5601 field_47444() {
        throw new UnsupportedOperationException("CenturyBridge: field_47444 was removed in 1.20.5 with no direct replacement; cannot determine which PositionType it corresponded to");
    }

    /** was net/minecraft/class_5602.field_47445Lnet/minecraft/class_5601; */
    public static net.minecraft.class_5601 field_47445() {
        throw new UnsupportedOperationException("CenturyBridge: field_47445 (a ParticleType constant in ParticleTypes) was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/class_636.method_2904()F */
    public static float method_2904(net.minecraft.class_636 self) {
        return (float) self.method_51888();
        // CB-CONFIDENCE: medium -- method_51888 is the likely int-returning replacement for the old float getBlockReachDistance; the int-to-float cast loses the 0.5 fractional reach in survival mode
    }

    /** was net/minecraft/class_6385$1.field_33799Lnet/minecraft/class_6385; */
    public static net.minecraft.class_6385 field_33799() {
        throw new UnsupportedOperationException("CenturyBridge: field_33799 was the synthetic this$0 outer-instance reference of class_6385$1, deleted in 1.20.5 because the anonymous class no longer references its enclosing class_6385; a per-instance outer reference cannot be rebuilt as a static constant");
    }

    /** was net/minecraft/class_638.method_2944(Lnet/minecraft/class_269;)V */
    public static void method_2944(net.minecraft.class_638 self, net.minecraft.class_269 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_638.method_2944(class_269) (setParticleManager) was removed; 1.20.5 class_638 only exposes M() to get the particle manager, with no setter");
        // CB-CONFIDENCE: high -- old method_2944 is the particle-manager setter; new ClientWorld exposes only getter M() and no class_269-accepting setter remains
    }

    /** was net/minecraft/class_7065.method_41160(I)V */
    public static void method_41160(net.minecraft.class_7065 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7065.method_41160(I)V was removed in 1.20.5 with no direct replacement; the new API surface has no int-parameter method on this class");
    }

    /** was net/minecraft/class_7065.method_44663(Lnet/minecraft/class_332;)V */
    public static void method_44663(net.minecraft.class_7065 self, net.minecraft.class_332 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_44663(class_332) was removed with no direct replacement; class_7065's new API exposes no method accepting class_332, rendering likely moved to the DrawContext side");
    }

    /** was net/minecraft/class_7196.method_54618(Ljava/lang/String;Ljava/lang/Runnable;)V */
    public static void method_54618(net.minecraft.class_7196 self, java.lang.String a0, java.lang.Runnable a1) {
        self.method_57784(a0, a1);
    }

    /** was net/minecraft/class_723$class_5877$1.field_29074Lnet/minecraft/class_723$class_5877; */
    public static net.minecraft.class_723.class_5877 field_29074() {
        throw new UnsupportedOperationException("CenturyBridge: field_29074 was a static class_723$class_5877 constant in the anonymous inner class class_723$class_5877$1, deleted in 1.20.5 with no replacement; the original constant value cannot be reconstructed from the new API");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface provides no way to reconstruct the original constant value
    }

    /** was net/minecraft/class_7557$class_7558.field_40809Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40809() {
        return net.minecraft.class_7557.class_7558.field_40809.codec();
    }

    /** was net/minecraft/class_7557$class_7559.field_40810Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40810() {
        return net.minecraft.class_7557.class_7559.field_40810.codec();
    }

    /** was net/minecraft/class_757.method_3168(Lnet/minecraft/class_2960;)V */
    public static void method_3168(net.minecraft.class_757 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_3168(Identifier) loadPostProcessor was removed in 1.20.5; post-processing pipeline reworked, no equivalent loader exists on class_757");
    }

    /** was net/minecraft/class_7919.method_54383(I)V */
    public static void method_54383(net.minecraft.class_7919 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_54383(int) was removed in 1.20.5 with no direct replacement; the new class_7919 API has no int-parameterized instance method to delegate to");
    }

    /** was net/minecraft/class_7919.method_54384(ZZLnet/minecraft/class_8030;)V */
    public static void method_54384(net.minecraft.class_7919 self, boolean a0, boolean a1, net.minecraft.class_8030 a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_7919.method_54384(ZZLnet/minecraft/class_8030;)V was deleted in 1.20.5 with no direct replacement; remaining class_7919 methods (b, method_47405-47408) accept incompatible parameter types and cannot express the (boolean, boolean, class_8030) contract");
    }

    /** was net/minecraft/class_7919.method_54385(ZZLnet/minecraft/class_8030;)Lnet/minecraft/class_8000; */
    public static net.minecraft.class_8000 method_54385(net.minecraft.class_7919 self, boolean a0, boolean a1, net.minecraft.class_8030 a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_54385 was removed in 1.20.5 with no direct replacement; class_7919's remaining API has no method returning class_8000 or accepting class_8030");
    }

    /** was net/minecraft/class_7954.field_41399Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41399() {
        return net.minecraft.class_7954.field_41399.codec();
    }

    /** was net/minecraft/class_7955.field_41402Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41402() {
        return net.minecraft.class_7955.field_41402.codec();
    }

    /** was net/minecraft/class_860$class_4605.field_20999Lnet/minecraft/class_860; */
    public static net.minecraft.class_860 field_20999() {
        return null;
    }

    /** was net/minecraft/class_8690$class_8694.field_45655Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45655() {
        return (com.mojang.serialization.Codec) net.minecraft.class_8690.class_8694.field_45655;
    }

    /** was net/minecraft/class_8973.method_55234()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_55234(net.minecraft.class_8973 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8973.method_55234() was deleted in 1.20.5 with no direct replacement; the only remaining method returning class_5607 is method_55233(int,int) which requires two int parameters not available from the old no-arg signature");
    }

    /** was net/minecraft/class_8973.method_55235()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_55235(net.minecraft.class_8973 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_55235() returned class_5607 with no arguments; the only new-API equivalent method_55233(II) requires two int parameters whose values cannot be inferred from the old call site");
    }

    /** was net/minecraft/class_962$1.field_4805Lnet/minecraft/class_962; */
    public static net.minecraft.class_962 field_4805() {
        throw new UnsupportedOperationException("CenturyBridge: field_4805 was the synthetic outer instance reference (this$0) of class_962$1; it is an instance field that cannot be reconstructed from a static context without an instance of the inner class");
    }

    /** was net/minecraft/class_1100.method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;Lnet/minecraft/class_2960;)Lnet/minecraft/class_1087; */
    public net.minecraft.class_1087 method_4753(net.minecraft.class_7775 a0, java.util.function.Function a1, net.minecraft.class_3665 a2, net.minecraft.class_2960 a3) {
        return ((net.minecraft.class_1100) (Object) this).method_4753(a0, a1, a2);
    }

    /** was net/minecraft/class_1921.method_23012(Lnet/minecraft/class_287;Lnet/minecraft/class_8251;)V */
    public static void method_23012(net.minecraft.class_1921 self, net.minecraft.class_287 a0, net.minecraft.class_8251 a1) {
        throw new UnsupportedOperationException("CenturyBridge: RenderLayer.draw(BufferBuilder, VertexConsumerProvider.Immediate) was removed in 1.21's rendering pipeline overhaul; class_1921 no longer owns buffer-drawing functionality");
    }

    /** was net/minecraft/class_1921.method_24296()Ljava/util/Optional; */
    public static java.util.Optional method_24296(net.minecraft.class_1921 self) {
        return self.method_23289();
    }

    /** was net/minecraft/class_290.field_1578Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1578() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1578 (type class_296) was removed in 1.21 with no direct equivalent");
    }

    /** was net/minecraft/class_290.field_1581Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1581() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1581 was a class_296 (VertexFormat) constant deleted in 1.21; the vertex format system was reworked and the new class_290 surface exposes only class_293 elements with no class_296 equivalent to reconstruct");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no replacement, and the new class_290 API surface contains zero class_296 references, so no faithful constant can be synthesized
    }

    /** was net/minecraft/class_290.field_1587Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1587() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1587 (class_296 ButtonWidget instance) was removed in 1.21 with no direct replacement; remaining fields are class_293 ClickableWidget");
        // CB-CONFIDENCE: high -- the field is deleted and the new API surface contains no class_296 fields, so no faithful constant can be produced
    }

    /** was net/minecraft/class_290.field_1591Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1591() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1591 (type class_296) was deleted in 1.21 with no direct replacement; new API surface exposes only class_293 (Drawable) fields, no class_296 equivalent exists");
    }

    /** was net/minecraft/class_290.field_20886Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_20886() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_20886 (normal Matrix3f) was removed in 1.21; MatrixStack no longer tracks a normal matrix");
    }

    /** was net/minecraft/class_290.field_20887Lnet/minecraft/class_293; */
    public static net.minecraft.class_293 field_20887() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_20887 was removed in 1.21 with no direct equivalent");
        // CB-CONFIDENCE: high -- the prompt explicitly states the field was deleted with no direct replacement; a tombstone is the honest bridge.
    }

    /** was net/minecraft/class_290.field_29335Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_29335() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_29335 (MatrixStack) was removed in 1.21 with no direct replacement; MatrixStack is now passed as a render parameter");
    }

    /** was net/minecraft/class_310.method_1488()F */
    public static float method_1488(net.minecraft.class_310 self) {
        return (float) self.method_41734();
    }

    /** was net/minecraft/class_3999.method_18131(Lnet/minecraft/class_289;)V */
    public static void method_18131(net.minecraft.class_3999 self, net.minecraft.class_289 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3999.method_18131(class_289) was removed in 1.21 with no direct replacement; the remaining method_18130 requires an additional class_1060 (Texture) argument and returns class_287 (Sprite), which is incompatible with the old void contract");
    }

    /** was net/minecraft/class_4588.method_22901(IIII)V */
    public static void method_22901(net.minecraft.class_4588 self, int a0, int a1, int a2, int a3) {
        self.method_1336(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_4588.method_22912(DDD)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22912(net.minecraft.class_4588 self, double a0, double a1, double a2) {
        return self.method_22912((float) a0, (float) a1, (float) a2);
    }

    /** was net/minecraft/class_4588.method_22916(I)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22916(net.minecraft.class_4588 self, int a0) {
        return self.method_39415(a0);
    }

    /** was net/minecraft/class_4588.method_22917(II)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22917(net.minecraft.class_4588 self, int a0, int a1) {
        return self.method_60796(a0, a1);
    }

    /** was net/minecraft/class_4588.method_23763(Lnet/minecraft/class_4587$class_4665;FFF)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_23763(net.minecraft.class_4588 self, net.minecraft.class_4587.class_4665 a0, float a1, float a2, float a3) {
        return self.method_56824(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_4877.method_25059(Lnet/minecraft/class_4881;)V */
    public static void method_25059(net.minecraft.class_4877 self, net.minecraft.class_4881 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25059(class_4881) was removed in 1.21 with no direct replacement; no field or method on class_4877 accepts class_4881");
    }

    /** was net/minecraft/class_6777.method_47621(Lnet/minecraft/class_310;Lnet/minecraft/class_437;Lnet/minecraft/class_315;)Lnet/minecraft/class_6777; */
    public static net.minecraft.class_6777 method_47621(net.minecraft.class_6777 self, net.minecraft.class_310 a0, net.minecraft.class_437 a1, net.minecraft.class_315 a2) {
        return new net.minecraft.class_6777(a1, a2);
        // CB-CONFIDENCE: medium -- the new constructor takes exactly the Screen and GameOptions args the old factory accepted; the MinecraftClient arg has no equivalent in the new API and is dropped, which is the most faithful bridge available.
    }

    /** was net/minecraft/class_783.field_4227Lnet/minecraft/class_787; */
    public static net.minecraft.class_787 field_4227() {
        throw new UnsupportedOperationException("CenturyBridge: class_783.field_4227 (static class_787) was deleted in 1.21 with no direct replacement; new class_783 is a record exposing no static class_787 constant, and the original value cannot be inferred from the new API surface");
    }

    /** was net/minecraft/class_918.method_30115(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;Lnet/minecraft/class_4587$class_4665;)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_30115(net.minecraft.class_918 self, net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, net.minecraft.class_4587.class_4665 a2) {
        return self.method_30114(a0, a1, a2);
    }

    /** was net/minecraft/class_927.method_4072(Lnet/minecraft/class_1308;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4072(net.minecraft.class_927 self, net.minecraft.class_1308 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_927.method_4072 (EntityRenderer.render) was removed in 1.21; the entity rendering pipeline was refactored to use EntityRenderState and no direct equivalent exists on class_927");
    }

    /** was net/minecraft/class_4381.method_57667(Lnet/minecraft/class_4381;)Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 method_57667(net.minecraft.class_4381 self, net.minecraft.class_4381 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes multiple class_4381-to-class_310 methods, but no verified equivalent for deleted method_57667");
    }

    /** was net/minecraft/class_290.field_1579Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1579() {
        return net.minecraft.class_296.field_52113;
    }

    /** was net/minecraft/class_332.<init>()V */
    public static net.minecraft.class_332 cbNewclass_332() {
        throw new UnsupportedOperationException("CenturyBridge: the new class_332 constructors require Minecraft client and vertex consumer state that the old no-argument constructor did not provide");
    }

    /** was net/minecraft/class_4189.field_26681Lnet/minecraft/class_353; */
    public static net.minecraft.class_353 field_26681() {
        throw new UnsupportedOperationException("CenturyBridge: the old instance option-list widget requires an AccessibilityOptionsScreen receiver and has no constant equivalent in the new API");
    }

    /** was net/minecraft/class_4588.method_23919(FFFFFFFFFIIFFF)V */
    public static void method_23919(net.minecraft.class_4588 self, float a0, float a1, float a2, float a3, float a4, float a5, float a6, float a7, float a8, int a9, int a10, float a11, float a12, float a13) {
        self.method_23919(a0, a1, a2, ((int) (a5 * 255.0F) << 24) | ((int) (a3 * 255.0F) << 16) | ((int) (a4 * 255.0F) << 8) | (int) (a6 * 255.0F), a7, a8, a9, a10, a11, a12, a13);
    }

    /** was net/minecraft/class_7193.<init>(Lnet/minecraft/class_5285;Lcom/mojang/serialization/Lifecycle;Lnet/minecraft/class_5455$class_6890;Lnet/minecraft/class_5350;)V */
    public static net.minecraft.class_7193 cbNewclass_7193(net.minecraft.class_5285 a0, com.mojang.serialization.Lifecycle a1, net.minecraft.class_5455.class_6890 a2, net.minecraft.class_5350 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the old Lifecycle and class_5455.class_6890 arguments cannot be faithfully converted to the required class_7723, class_7780, and class_7712 constructor components");
    }

    /**
     * superRedirects: a 1.20.1 override's super.render(context, mx, my, delta).
     * 1.20.1 Screen.render drew the widget list and nothing else; since 1.20.2
     * it draws the background first, over everything the mod just drew. Render
     * exactly what the old contract promised.
     */
    public static void cbRenderWidgets(net.minecraft.class_437 self, net.minecraft.class_332 context, int mouseX, int mouseY, float delta) {
        for (net.minecraft.class_4068 d : ((top.dext.centurybridge.shims.v1_21_1.ScreenDrawablesAccessor) self).cbDrawables()) {
            d.method_25394(context, mouseX, mouseY, delta);
        }
    }

    /** ctorRedirects: 1.20.1 new ServerInfo(name, address, local) -- the boolean
     *  became ServerInfo.ServerType in 1.20.2 (local ? LAN : OTHER). */
    public static net.minecraft.class_642 cbNewServerInfo(String name, String address, boolean local) {
        return new net.minecraft.class_642(name, address,
            local ? net.minecraft.class_642.class_8678.field_45609   // LAN
                  : net.minecraft.class_642.class_8678.field_45611); // OTHER
    }

    /** superCtorAdapts: the 1.20.1 ServerInfo ctor boolean, as ServerType */
    public static net.minecraft.class_642.class_8678 cbServerType(boolean local) {
        return local ? net.minecraft.class_642.class_8678.field_45609   // LAN
                     : net.minecraft.class_642.class_8678.field_45611;  // OTHER
    }

    /** was TexturedRenderLayers.getArmorTrims(): 1.20.5+ takes the decal flag (false = the base trims sheet) */
    public static net.minecraft.class_1921 method_48480() {
        return net.minecraft.class_4722.method_48480(false);
    }

    /** was ButtonWidget(x, y, w, h, text, onPress): the ctor went protected in 1.20.2 -- through the builder */
    public static net.minecraft.class_4185 cbNewclass_4185(int x, int y, int w, int h, net.minecraft.class_2561 text, net.minecraft.class_4185.class_4241 action) {
        return net.minecraft.class_4185.method_46430(text, action).method_46434(x, y, w, h).method_46431();
    }

    /** was net/minecraft/class_3887.method_23196(Lnet/minecraft/class_583;Lnet/minecraft/class_583;Lnet/minecraft/class_2960;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1309;FFFFFFFFF)V */
    public static void method_23196(net.minecraft.class_583 a0, net.minecraft.class_583 a1, net.minecraft.class_2960 a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5, net.minecraft.class_1309 a6, float a7, float a8, float a9, float a10, float a11, float a12, float a13, float a14, float a15) {
        int color = 0xFF000000 | ((int)(a13 * 255.0f) << 16) | ((int)(a14 * 255.0f) << 8) | (int)(a15 * 255.0f);
    }

    /** was net/minecraft/class_4722.method_33081(Lnet/minecraft/class_5321;)Lnet/minecraft/class_4730; */
    public static net.minecraft.class_4730 method_33081(net.minecraft.class_5321 a0) {
        return net.minecraft.class_4722.method_49341(a0);
    }

    /** was net/minecraft/class_4722.method_33083(Lnet/minecraft/class_5321;)Lnet/minecraft/class_4730; */
    public static net.minecraft.class_4730 method_33083(net.minecraft.class_5321 a0) {
        return net.minecraft.class_4722.method_49341(a0);
    }

    /** was net/minecraft/class_4881.method_25074(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_4881; */
    public static net.minecraft.class_4881 method_25074(com.google.gson.JsonObject a0) {
        return net.minecraft.class_4881.method_25074(a0.toString());
    }

    /** was net/minecraft/class_490.method_48472(Lnet/minecraft/class_332;FFILorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/class_1309;)V */
    public static void method_48472(net.minecraft.class_332 a0, float a1, float a2, int a3, org.joml.Vector3f a4, org.joml.Quaternionf a5, org.joml.Quaternionf a6, net.minecraft.class_1309 a7) {
        net.minecraft.class_490.method_48472(a0, a1, a2, (float) a3, a4, a5, a6, a7);
    }

    /** was net/minecraft/class_5272.method_27878(Lnet/minecraft/class_1792;Lnet/minecraft/class_2960;)Lnet/minecraft/class_1800; */
    public static net.minecraft.class_1800 method_27878(net.minecraft.class_1792 a0, net.minecraft.class_2960 a1) {
        return net.minecraft.class_5272.method_27878(new net.minecraft.class_1799(a0), a1);
    }

    /** was net/minecraft/class_7766.method_47664(Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_47664(java.util.List a0, java.util.concurrent.Executor a1) {
        return net.minecraft.class_7766.method_47664(net.minecraft.class_8684.create(a0), a0, a1);
    }

    /** was net/minecraft/class_796.method_3454(Lnet/minecraft/class_787;Lnet/minecraft/class_2350;Lnet/minecraft/class_4590;Lnet/minecraft/class_2960;)Lnet/minecraft/class_787; */
    public static net.minecraft.class_787 method_3454(net.minecraft.class_787 a0, net.minecraft.class_2350 a1, net.minecraft.class_4590 a2, net.minecraft.class_2960 a3) {
        return net.minecraft.class_796.method_3454(a0, a1, a2);
    }

    /** was net/minecraft/class_7973.method_47761(Ljava/util/List;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_47761(java.util.List a0) {
        return net.minecraft.class_7973.method_47761(a0).codec();
    }

    /** was net/minecraft/class_823.method_23802(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_630;Lnet/minecraft/class_4730;ZLjava/util/List;Z)V */
    public static void method_23802(net.minecraft.class_4587 a0, net.minecraft.class_4597 a1, int a2, int a3, net.minecraft.class_630 a4, net.minecraft.class_4730 a5, boolean a6, java.util.List a7, boolean a8) {
        net.minecraft.class_823.method_23802(a0, a1, a2, a3, a4, a5, a6, net.minecraft.class_1767.field_7967, new net.minecraft.class_9307(a7), a8);
    }

    /** was net/minecraft/class_823.method_29999(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_630;Lnet/minecraft/class_4730;ZLjava/util/List;)V */
    public static void method_29999(net.minecraft.class_4587 a0, net.minecraft.class_4597 a1, int a2, int a3, net.minecraft.class_630 a4, net.minecraft.class_4730 a5, boolean a6, java.util.List a7) {
        net.minecraft.class_823.method_29999(a0, a1, a2, a3, a4, a5, a6, net.minecraft.class_1767.field_7942, new net.minecraft.class_9307(a7));
    }

    /** was net/minecraft/class_836.method_3578(Lnet/minecraft/class_2484$class_2485;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_3578(net.minecraft.class_2484.class_2485 a0, com.mojang.authlib.GameProfile a1) {
        return net.minecraft.class_836.method_3578(a0, new net.minecraft.class_9296(a1));
    }

    /** was net/minecraft/class_8496.method_51260(Lnet/minecraft/class_310;Lnet/minecraft/class_542$class_8495;Lnet/minecraft/class_4011;Lnet/minecraft/class_4341;)V */
    public static void method_51260(net.minecraft.class_310 a0, net.minecraft.class_542.class_8495 a1, net.minecraft.class_4011 a2, net.minecraft.class_4341 a3) {
        net.minecraft.class_8496.method_51260(a0, a1, a3);
    }

    /** was net/minecraft/class_8973.method_55233()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_55233() {
        return net.minecraft.class_8973.method_55233(0, 0);
    }

    /** was net/minecraft/class_918.method_27952(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;ZZ)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_27952(net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, boolean a2, boolean a3) {
        return net.minecraft.class_918.method_27952(a0, a1, a3);
    }

    private ClientStatics() {
    }
}
