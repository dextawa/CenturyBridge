package top.dext.centurybridge.rt.v1_21_11;

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
     * subtracts the cursor). Only the scissor-box form survives here, and it derives the
     * rotation from ABSOLUTE cursor coordinates against the box centre: atan((centre -
     * cursor) / 40). Passing centre - relative reproduces the 1.20.1 rotation exactly;
     * the box is the one {@link #method_48472} uses, so both old shapes place the entity
     * alike (clipped to the box, which the 1.20.1 renderer never did).
     */
    public static void method_2486(net.minecraft.class_332 context,
            int x, int y, int size, float mouseX, float mouseY,
            net.minecraft.class_1309 entity) {
        int x1 = x - size;
        int y1 = y - 2 * size;
        int x2 = x + size;
        int y2 = y;
        float centreX = (x1 + x2) / 2.0F;
        float centreY = (y1 + y2) / 2.0F;
        net.minecraft.class_490.method_2486(context, x1, y1, x2, y2, size, 0.0625F,
                centreX - mouseX, centreY - mouseY, entity);
    }

    /**
     * 1.20.1 EntityRenderer.drawEntity(ctx,x,y,size,rot,cam,entity)
     * gained a float+Vector3f pivot at 1.20.2. 4 client call sites.
     */
    public static void method_48472(net.minecraft.class_332 context,
            int x, int y, int size,
            org.joml.Quaternionf rotation, org.joml.Quaternionf cameraAngle,
            net.minecraft.class_1309 entity) {
        // 1.21.11 wall: the quaternion drawEntity is gone entirely; the surviving
        // box form derives rotation from mouse coords. Neutral mouse keeps the
        // entity rendered, caller-supplied rotation is lost (visual degradation).
        net.minecraft.class_490.method_2486(context, x - size, y - 2 * size, x + size, y,
                size, 0.0625f, 0.0f, 0.0f, entity);
    }

    public void method_4884(net.minecraft.class_1113 a0, net.minecraft.class_1146 a1) {
        ((net.minecraft.class_1145) (Object) this).method_4884(a0, a1, 1.0f);
    }

    /** was net/minecraft/class_310.method_1519()V */
    public static void method_1519(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_1519 (handleInputEvents) was removed and its functionality was merged into client tick");
    }

    /** was net/minecraft/class_310.method_1537(Z)V */
    public static void method_1537(net.minecraft.class_310 self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: setConnectedToRealms is no longer supported as this state is now automatically derived from the network handler.");
    }

    /** was net/minecraft/class_310.method_1589()Z */
    public static boolean method_1589(net.minecraft.class_310 self) {
        return true;
    }

    /** was net/minecraft/class_310.method_44649()Z */
    public static boolean method_44649(net.minecraft.class_310 self) {
        return self.method_47595();
    }

    /** was net/minecraft/class_339.method_47937()Lnet/minecraft/class_8000; */
    public static net.minecraft.class_8000 method_47937(net.minecraft.class_339 self) {
        try {
            for (java.lang.reflect.Field field : net.minecraft.class_339.class.getDeclaredFields()) {
                if (field.getType() == net.minecraft.class_8000.class) {
                    field.setAccessible(true);
                    return (net.minecraft.class_8000) field.get(self);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean method_25401(double a0, double a1, double a2) {
        return ((net.minecraft.class_364) (Object) this).method_25401(a0, a1, 0.0, a2);
    }

    /** was net/minecraft/class_4227.method_19671(Lnet/minecraft/class_243;)V */
    public static void method_19671(net.minecraft.class_4227 self, net.minecraft.class_243 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_19671 is no longer supported on FogData");
    }

    /** was net/minecraft/class_4227.method_19672(Lorg/joml/Vector3f;Lorg/joml/Vector3f;)V */
    public static void method_19672(net.minecraft.class_4227 self, org.joml.Vector3f a0, org.joml.Vector3f a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_19672 was deleted and cannot be reconstructed from the new Camera API");
    }

    /** was net/minecraft/class_4227.method_27268()Lnet/minecraft/class_243; */
    public static net.minecraft.class_243 method_27268(net.minecraft.class_4227 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_27268 was removed from class_4227 with no replacement");
    }

    /** was net/minecraft/class_4341.field_19576Lnet/minecraft/class_4341$class_4343; */
    public static net.minecraft.class_4341.class_4343 field_19576() {
        return net.minecraft.class_4341.field_45232;
    }

    /** was net/minecraft/class_4341.method_21001()V */
    public static void method_21001(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4341.method_21001() was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_4341.method_21008()V */
    public static void method_21008(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_21008 has no replacement");
    }

    /** was net/minecraft/class_4341.method_21012()V */
    public static void method_21012(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4341.method_21012 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_4341.method_21021()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21021(net.minecraft.class_4341 self) {
        return java.lang.Boolean.FALSE;
    }

    /** was net/minecraft/class_4341.method_21024()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21024(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: RealmsClient.trialAvailable (method_21024) was removed in 1.20.2");
    }

    /** was net/minecraft/class_4346.method_35685(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_4346; */
    public static net.minecraft.class_4346 method_35685(net.minecraft.class_4346 self, java.lang.String a0, java.lang.String a1) {
        self.method_21042(a0, a1);
        return self;
    }

    /** was net/minecraft/class_4358.field_19638Lnet/minecraft/class_4398; */
    public static net.minecraft.class_4398 field_19638() {
        throw new UnsupportedOperationException("CenturyBridge: class_4358.field_19638 (class_4398) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_4358.method_21066(Lnet/minecraft/class_4398;)V */
    public static void method_21066(net.minecraft.class_4358 self, net.minecraft.class_4398 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21066 (addLabel) is no longer supported as RealmsLabel has been removed in 1.20.2");
    }

    /** was net/minecraft/class_4419.method_21440()V */
    public static void method_21440(net.minecraft.class_4419 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_21440 was removed from ConnectScreen in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/class_4419.method_25228(I)I */
    public static int method_25228(net.minecraft.class_4419 self, int a0) {
        return 40 + a0 * 13;
    }

    /** was net/minecraft/class_447.method_19399()Lnet/minecraft/class_4280; */
    public static net.minecraft.class_4280 method_19399(net.minecraft.class_447 self) {
        throw new UnsupportedOperationException("CenturyBridge: State getter was removed and state is no longer accessible");
    }

    /** was net/minecraft/class_455.method_2320()I */
    public static int method_2320(net.minecraft.class_455 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_455.method_2320() (Difficulty.getId()) was removed in 1.20.2; difficulties are now registry-backed and identified by ResourceLocation via method_52753(), with no stable int id equivalent");
    }

    /** was net/minecraft/class_4667.method_45626(Lnet/minecraft/class_332;Lnet/minecraft/class_353;IIF)V */
    public static void method_45626(net.minecraft.class_4667 self, net.minecraft.class_332 a0, net.minecraft.class_353 a1, int a2, int a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_45626 was deleted in 1.20.3 and has no direct replacement");
    }

    public void method_2866(net.minecraft.class_161 a0) {
        ((net.minecraft.class_632.class_633) (Object) this).method_2866(new net.minecraft.class_8779(net.minecraft.class_2960.method_60655("centurybridge", "dummy"), a0));
    }

    /** was net/minecraft/class_632.method_2863()Lnet/minecraft/class_163; */
    public static net.minecraft.class_163 method_2863(net.minecraft.class_632 self) {
        return self.method_53814();
    }

    /** was net/minecraft/class_640.method_2967()Z */
    public static boolean method_2967(net.minecraft.class_640 self) {
        return self.method_45742();
    }

    /** was net/minecraft/class_640.method_2969()V */
    public static void method_2969(net.minecraft.class_640 self) {
        self.method_52810();
    }

    /** was net/minecraft/class_640.method_2977()Ljava/lang/String; */
    public static java.lang.String method_2977(net.minecraft.class_640 self) {
        return self.method_2966().name();
    }

    /** was net/minecraft/class_640.method_35757()Z */
    public static boolean method_35757(net.minecraft.class_640 self) {
        return self.method_45742();
    }

    /** was net/minecraft/class_7196.method_41890(Lnet/minecraft/class_32$class_5143;Z)Lnet/minecraft/class_6904; */
    public static net.minecraft.class_6904 method_41890(net.minecraft.class_7196 self, net.minecraft.class_32.class_5143 a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: synchronous world loading from session is no longer supported");
    }

    /** was net/minecraft/class_7196.method_41894(Lnet/minecraft/class_437;Ljava/lang/String;)V */
    public static void method_41894(net.minecraft.class_7196 self, net.minecraft.class_437 a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_7196.method_41894(Screen,String) created a world from the CreateWorldScreen instance's internal state (data packs, level storage, generator options); 1.20.3 replaced it with method_41895(String,class_1940,class_5285,Function,class_437) which requires those as explicit arguments, and the shim has no access to the receiver's fields to supply them");
    }

    /** was net/minecraft/class_742.method_3120(Lnet/minecraft/class_2960;Ljava/lang/String;)V */
    public static void method_3120(net.minecraft.class_742 self, net.minecraft.class_2960 a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: registerSkinTexture is not supported in 1.20.2 as skin loading is managed by PlayerSkinProvider");
    }

    /** was net/minecraft/class_742.method_3121()Ljava/lang/String; */
    public static java.lang.String method_3121(net.minecraft.class_742 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_3121 (getSkinType) was removed; use method_52814 (getSkinTextures) and extract the model from class_8685 instead");
    }

    /** was net/minecraft/class_742.method_3126()Z */
    public static boolean method_3126(net.minecraft.class_742 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_742.method_3126()Z was deleted in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_7529.method_44406()V */
    public static void method_44406(net.minecraft.class_7529 self) {
        return;
    }

    /** was net/minecraft/class_7578.field_39684Lnet/minecraft/class_7581$class_7586; */
    public static net.minecraft.class_7581.class_7586 field_39684() {
        throw new UnsupportedOperationException("CenturyBridge: field_39684 was removed in 1.20.2 with no direct replacement; the registered class_7581$class_7586 instance no longer exists");
    }

    /** was net/minecraft/class_757.method_34500()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34500(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: getPositionColorLightmapShader was removed");
    }

    /** was net/minecraft/class_7766.method_45829(Lnet/minecraft/class_2960;Lnet/minecraft/class_3298;)Lnet/minecraft/class_7764; */
    public static net.minecraft.class_7764 method_45829(net.minecraft.class_7766 self, net.minecraft.class_2960 a0, net.minecraft.class_3298 a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_45829 (SpriteLoader.load(Identifier, Resource)) was removed in 1.20.2; the new API is async (CompletableFuture-based) and takes a ResourceManager or List instead of a single Resource, with no synchronous single-resource equivalent");
    }

    /** was net/minecraft/class_8087.method_48613()V */
    public static void method_48613(net.minecraft.class_8087 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8087.method_48613 was removed in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/class_8088.method_48617()V */
    public static void method_48617(net.minecraft.class_8088 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8088.method_48617 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_8132.method_49001()Lnet/minecraft/class_7847; */
    public static net.minecraft.class_7847 method_49001(net.minecraft.class_8132 self) {
        throw new UnsupportedOperationException("CenturyBridge: HeaderAndFooterLayoutWidget no longer exposes its default positioner");
    }

    /** was net/minecraft/class_8132.method_49002()Lnet/minecraft/class_7847; */
    public static net.minecraft.class_7847 method_49002(net.minecraft.class_8132 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8132.method_49002() was deleted in 1.20.2 with no direct replacement returning class_7847");
    }

    /** was net/minecraft/class_8132.method_49003()Lnet/minecraft/class_7847; */
    public static net.minecraft.class_7847 method_49003(net.minecraft.class_8132 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49003() was removed in 1.20.2 with no direct replacement; the new class_8132 API does not produce class_7847 instances");
    }

    /** was net/minecraft/class_8209.method_49607()I */
    public static int method_49607(net.minecraft.class_8209 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49607 returned a creative tab index (int), but 1.20.2 replaced tab indices with ItemGroup references (class_8087); use method_49609() instead");
    }

    /** was net/minecraft/class_879.method_3882(Lnet/minecraft/class_1420;Lnet/minecraft/class_4587;FFF)V */
    public static void method_3882(net.minecraft.class_879 self, net.minecraft.class_1420 a0, net.minecraft.class_4587 a1, float a2, float a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_3882(class_1420, class_4587, float, float, float) was deleted in 1.20.3 with no direct replacement; remaining method_3883(class_1420) returns class_2960 and drops the MatrixStack/position parameters, so the old render-style contract cannot be expressed");
    }

    /** was net/minecraft/class_879.method_3884(Lnet/minecraft/class_1420;Lnet/minecraft/class_4587;F)V */
    public static void method_3884(net.minecraft.class_879 self, net.minecraft.class_1420 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_3884 is unsupported because modern rendering requires a VertexConsumerProvider and light map coordinates which are missing from this signature.");
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

    public void method_22919(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        ((net.minecraft.class_4588) (Object) this).method_22919(a0, a1, a2, a3, a4, 1.0f, a5, a6);
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

    /** was net/minecraft/class_1921.method_23012(Lnet/minecraft/class_287;Lnet/minecraft/class_8251;)V */
    public static void method_23012(net.minecraft.class_1921 self, net.minecraft.class_287 a0, net.minecraft.class_8251 a1) {
        throw new UnsupportedOperationException("CenturyBridge: RenderLayer.draw(BufferBuilder, VertexConsumerProvider.Immediate) was removed in 1.21's rendering pipeline overhaul; class_1921 no longer owns buffer-drawing functionality");
    }

    /** was net/minecraft/class_1921.method_24296()Ljava/util/Optional; */
    public static java.util.Optional method_24296(net.minecraft.class_1921 self) {
        return self.method_23289();
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

    public net.minecraft.class_4588 method_22912(double a0, double a1, double a2) {
        return ((net.minecraft.class_4588) (Object) this).method_22912((float) a0, (float) a1, (float) a2);
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

    /** was net/minecraft/class_927.method_4072(Lnet/minecraft/class_1308;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4072(net.minecraft.class_927 self, net.minecraft.class_1308 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_927.method_4072 (EntityRenderer.render) was removed in 1.21; the entity rendering pipeline was refactored to use EntityRenderState and no direct equivalent exists on class_927");
    }

    /** was net/minecraft/class_1921.method_23585()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23585(net.minecraft.class_1921 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_23585 was deleted with no direct replacement in 1.21.11");
        // CB-CONFIDENCE: high -- the member is explicitly marked deleted with no direct replacement; no equivalent exists in the new API surface
    }

    /** was net/minecraft/class_742.method_3117()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3117(net.minecraft.class_742 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_3117 was deleted in 1.21.11 with no direct replacement; new ClientPlayerEntity API has no method returning Identifier (class_2960)");
    }

    /** was net/minecraft/class_1011.method_24036()[B */
    public static byte[] method_24036(net.minecraft.class_1011 self) {
        java.nio.file.Path tempFile = null;
        try {
            tempFile = java.nio.file.Files.createTempFile("cb_nativeimage", ".png");
            self.method_4314(tempFile);
            return java.nio.file.Files.readAllBytes(tempFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null) {
                try { java.nio.file.Files.deleteIfExists(tempFile); } catch (java.io.IOException ignored) {}
            }
        }
    }

    /** was net/minecraft/class_1011.method_35621(IIB)V */
    public static void method_35621(net.minecraft.class_1011 self, int a0, int a1, byte a2) {
        self.method_61941(a0, a1, (int) a2);
    }

    /** was net/minecraft/class_1011.method_35624(III)V */
    public static void method_35624(net.minecraft.class_1011 self, int a0, int a1, int a2) {
        self.method_61941(a0, a1, a2);
    }

    /** was net/minecraft/class_1011.method_35625(II)B */
    public static byte method_35625(net.minecraft.class_1011 self, int a0, int a1) {
        return self.method_4311(a0, a1);
    }

    /** was net/minecraft/class_1011.method_35626(II)B */
    public static byte method_35626(net.minecraft.class_1011 self, int a0, int a1) {
        return self.method_4311(a0, a1);
    }

    /** was net/minecraft/class_1011.method_4305(III)V */
    public static void method_4305(net.minecraft.class_1011 self, int a0, int a1, int a2) {
        self.method_61941(a0, a1, a2);
        // CB-CONFIDENCE: high -- method_61941(III)V is the 1.21.2 remap of NativeImage.setPixelColor(int,int,int), same (x,y,color) contract
    }

    /** was net/minecraft/class_1011.method_4315(II)I */
    public static int method_4315(net.minecraft.class_1011 self, int a0, int a1) {
        return self.method_61940(a0, a1);
    }

    /** was net/minecraft/class_1041.method_15999(I)V */
    public static void method_15999(net.minecraft.class_1041 self, int a0) {
        self.method_35642(a0);
    }

    /** was net/minecraft/class_1041.method_16000()I */
    public static int method_16000(net.minecraft.class_1041 self) {
        throw new UnsupportedOperationException("CenturyBridge: Window.getFramerateLimit() (method_16000) was removed in 1.21.2; the framerate limit is no longer readable from Window (setter method_15997 remains but the value is not exposed)");
    }

    /** was net/minecraft/class_1060.method_22813(Lnet/minecraft/class_2960;)V */
    public static void method_22813(net.minecraft.class_1060 self, net.minecraft.class_2960 a0) {
        self.method_4615(a0);
    }

    /** was net/minecraft/class_1095.field_5425Lnet/minecraft/class_1058; */
    public static net.minecraft.class_1058 field_5425() {
        throw new UnsupportedOperationException("CenturyBridge: field_5425 (static Identifier on class_1095) was removed in 1.21.2 with no direct equivalent; the constant value cannot be reconstructed from the new API");
    }

    /** was net/minecraft/class_1095.field_5426Lnet/minecraft/class_809; */
    public static net.minecraft.class_809 field_5426() {
        throw new UnsupportedOperationException("CenturyBridge: field_5426 (class_809) was deleted from class_1095 with no direct replacement; the new API surface exposes no field or factory returning class_809, and the original constant value cannot be reconstructed");
    }

    /** was net/minecraft/class_1100.method_45785(Ljava/util/function/Function;)V */
    public static void method_45785(net.minecraft.class_1100 self, java.util.function.Function a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_45785 was removed with no direct replacement; method_62326 requires class_1100$class_10103 (constructor unknown) and method_4753 requires additional class_7775/class_3665 parameters not available from the old contract");
    }

    /** was net/minecraft/class_1156.method_31364(Lnet/minecraft/class_372;)V */
    public static void method_31364(net.minecraft.class_1156 self, net.minecraft.class_372 a0) {
        throw new UnsupportedOperationException("CenturyBridge: InGameHud.setSubtitle(Text) was removed in 1.21.2 with no direct replacement on class_1156; title/subtitle management moved elsewhere");
    }

    /** was net/minecraft/class_1921.method_29707()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_29707(net.minecraft.class_1921 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_29707 (outline) was removed in 1.21.2; outline rendering was reworked with no direct equivalent on class_1921");
    }

    /** was net/minecraft/class_299.method_1401(Ljava/lang/Iterable;Lnet/minecraft/class_5455;)V */
    public static void method_1401(net.minecraft.class_299 self, java.lang.Iterable a0, net.minecraft.class_5455 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_299.method_1401(Iterable, class_5455) was removed in 1.21.2; new API methods take class_10297/class_10298 instead of Iterable+Registry, no equivalent exists");
    }

    /** was net/minecraft/class_310.method_1524(I)V */
    public static void method_1524(net.minecraft.class_310 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_1524(int) (setFramerateTarget) was removed in 1.21.2 with no direct replacement on MinecraftClient");
    }

    /** was net/minecraft/class_310.method_16011()Lnet/minecraft/class_3695; */
    public static net.minecraft.class_3695 method_16011(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_16011 (getWindow) was removed in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/class_310.method_60646()Lnet/minecraft/class_9779; */
    public static net.minecraft.class_9779 method_60646(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: MinecraftClient.method_60646() returning class_9779 (ServerLinks) was removed in 1.21.2 with no direct replacement on class_310; server links must now be obtained through a different path not exposed on MinecraftClient");
    }

    /** was net/minecraft/class_315.method_1631(Lnet/minecraft/class_1664;Z)V */
    public static void method_1631(net.minecraft.class_315 self, net.minecraft.class_1664 a0, boolean a1) {
        self.method_1635(a0, a1);
    }

    /** was net/minecraft/class_315.method_1641(Lnet/minecraft/class_304;Lnet/minecraft/class_3675$class_306;)V */
    public static void method_1641(net.minecraft.class_315 self, net.minecraft.class_304 a0, net.minecraft.class_3675.class_306 a1) {
        throw new UnsupportedOperationException("CenturyBridge: FontManager.method_1641(TextRenderer, FontStorage) was removed in 1.21.2 with no direct replacement; the font storage/registration architecture was reworked and no method on class_315 accepts the old (class_304, class_3675.class_306) pair");
        // CB-CONFIDENCE: high -- the method is explicitly deleted with no direct replacement, and the new class_315 API surface contains no method accepting both a TextRenderer and a FontStorage-style argument, so a tombstone is the correct honest failure
    }

    /** was net/minecraft/class_332.method_51450()Lnet/minecraft/class_4597$class_4598; */
    public static net.minecraft.class_4597.class_4598 method_51450(net.minecraft.class_332 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_332.method_51450() has no replacement in 1.21.2; DrawContext no longer exposes its VertexConsumerProvider.Immediate via a public getter");
    }

    /** was net/minecraft/class_332.method_51741(Ljava/lang/Runnable;)V */
    public static void method_51741(net.minecraft.class_332 self, java.lang.Runnable a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_51741(Runnable) was deleted in 1.21.2 with no direct replacement; the DrawContext rendering pipeline was reworked and no method accepting a Runnable exists in the new API surface");
        // CB-CONFIDENCE: high -- the method is explicitly deleted and no new API accepts a Runnable, so no faithful bridge can be constructed
    }

    /** was net/minecraft/class_3680.method_17074(Lnet/minecraft/class_1451;FFF)V */
    public static void method_17074(net.minecraft.class_3680 self, net.minecraft.class_1451 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_3680.method_17074 was removed in 1.21.2 with no direct replacement; class_3680 now exposes only a constructor and field_52884 (class_9953) whose API surface is unavailable");
    }

    /** was net/minecraft/class_3680.method_17075(Lnet/minecraft/class_1451;FFFFF)V */
    public static void method_17075(net.minecraft.class_3680 self, net.minecraft.class_1451 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_3680.method_17075 was removed in 1.21.2 with no direct replacement; particle spawning moved to class_9953 (field_52884) whose API surface is not available");
    }

    /** was net/minecraft/class_3729.method_16460(Lnet/minecraft/class_1543;Lnet/minecraft/class_4587;F)V */
    public static void method_16460(net.minecraft.class_3729 self, net.minecraft.class_1543 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_3729.method_16460 was removed in 1.21.2; method_62430 requires class_10035 which cannot be derived from class_4587 (MatrixStack) without additional context");
    }

    /** was net/minecraft/class_3879.method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V */
    public static void method_2828(net.minecraft.class_3879 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3, int a4) {
        self.method_62100(a0, a1, a2, a3, a4);
    }

    /** was net/minecraft/class_3968.method_17790(Lnet/minecraft/class_1588;)Z */
    public static boolean method_17790(net.minecraft.class_3968 self, net.minecraft.class_1588 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3968.method_17790(class_1588)Z was deleted in 1.21.2; surviving methods take render-context types (class_10017/class_10034/class_10086) and return void, so the ItemStack->boolean contract has no equivalent");
    }

    /** was net/minecraft/class_3969.method_17792(Lnet/minecraft/class_1570;)Z */
    public static boolean method_17792(net.minecraft.class_3969 self, net.minecraft.class_1570 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3969.method_17792(class_1570) was removed in 1.21.2 with no direct replacement; the new class_3969 only exposes a constructor and no boolean query taking class_1570");
    }

    /** was net/minecraft/class_3999.field_17830Lnet/minecraft/class_3999; */
    public static net.minecraft.class_3999 field_17830() {
        throw new UnsupportedOperationException("CenturyBridge: field_17830 (EQUAL_DEPTH_TEST) was removed in 1.21.2; the RenderPhase depth-test system was refactored and this constant has no equivalent");
    }

    /** was net/minecraft/class_4381$class_4382.field_19760Lnet/minecraft/class_4381; */
    public static net.minecraft.class_4381 field_19760() {
        throw new UnsupportedOperationException("CenturyBridge: field_19760 is the synthetic outer-class reference (this$0) on class_4381$class_4382 and cannot be returned from a parameterless static helper");
    }

    /** was net/minecraft/class_4495.method_22111(Lnet/minecraft/class_4466;FFF)V */
    public static void method_22111(net.minecraft.class_4495 self, net.minecraft.class_4466 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_22111(class_4466, F, F, F) was removed in 1.21.2; remaining methods on class_4495 take class_10017/class_10002 with no known conversion from class_4466 + 3 floats");
    }

    /** was net/minecraft/class_4507.method_22130(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1297;FFFF)V */
    public static void method_22130(net.minecraft.class_4507 self, net.minecraft.class_4587 a0, net.minecraft.class_4597 a1, int a2, net.minecraft.class_1297 a3, float a4, float a5, float a6, float a7) {
        throw new UnsupportedOperationException("CenturyBridge: method_22130 took an Entity (class_1297) and 4 floats; the 1.21.2 API replaced it with methods taking class_10017/class_10055 and only 2 floats, with no Entity-based equivalent or documented float mapping");
    }

    /** was net/minecraft/class_459.method_57747(Lnet/minecraft/class_459;)Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 method_57747(net.minecraft.class_459 self, net.minecraft.class_459 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_459.method_57747 was deleted in 1.21.2 with no direct replacement among remaining Keyboard methods");
    }

    /** was net/minecraft/class_4722.method_24076()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_24076(net.minecraft.class_4722 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4722.method_24076 was deleted in 1.21.2 with no direct replacement; BufferBuilderStorage vertex consumer provider API was restructured");
        // CB-CONFIDENCE: medium -- the method is explicitly marked deleted with no direct replacement; multiple new class_1921-returning methods exist but none can be confidently identified as the semantic equivalent without knowing the original method's exact purpose
    }

    /** was net/minecraft/class_4840.field_27464Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 field_27464() {
        throw new UnsupportedOperationException("CenturyBridge: field_27464 was a static class_630 on class_4840, now only passed via constructor as an instance field; no static equivalent exists to reconstruct the old value");
    }

    /** was net/minecraft/class_4840.method_47656(Lnet/minecraft/class_5605;Lnet/minecraft/class_5609;)V */
    public static void method_47656(net.minecraft.class_4840 self, net.minecraft.class_5605 a0, net.minecraft.class_5609 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_4840.method_47656(class_5605, class_5609) was removed in 1.21.2 with no direct replacement; new particle methods use incompatible parameter types (class_10017/class_10034/class_10054)");
    }

    /** was net/minecraft/class_507.method_2596(Lnet/minecraft/class_8786;Ljava/util/List;)V */
    public static void method_2596(net.minecraft.class_507 self, net.minecraft.class_8786 a0, java.util.List a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_507.method_2596(class_8786, List) was removed in 1.21.2; chat signing API reworked to method_64868/method_64875/method_64876 with incompatible types (class_9934/class_10295/class_10298/class_10352), no conversion from class_8786 available");
    }

    /** was net/minecraft/class_507.method_2600(Lnet/minecraft/class_1735;)V */
    public static void method_2600(net.minecraft.class_507 self, net.minecraft.class_1735 a0) {
        self.method_62044(a0);
    }

    /** was net/minecraft/class_507.method_60344(Lnet/minecraft/class_1856;IIII)V */
    public static void method_60344(net.minecraft.class_507 self, net.minecraft.class_1856 a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_60344(Ingredient,int,int,int,int) deleted in 1.21.2; new class_507 API has no Ingredient-accepting method — rendering restructured around class_332/class_1735");
    }

    /** was net/minecraft/class_513.method_2630(Lnet/minecraft/class_507;)V */
    public static void method_2630(net.minecraft.class_513 self, net.minecraft.class_507 a0) {
        self.method_2638();
    }

    /** was net/minecraft/class_513.method_2631()Lnet/minecraft/class_8786; */
    public static net.minecraft.class_8786 method_2631(net.minecraft.class_513 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_513.method_2631() (returning class_8786) was removed in 1.21.2; no remaining accessor on class_513 yields class_8786 -- method_64878() returns the unrelated class_10298, so the old contract has no faithful bridge");
    }

    /** was net/minecraft/class_514.method_2643()Lnet/minecraft/class_8786; */
    public static net.minecraft.class_8786 method_2643(net.minecraft.class_514 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_514.method_2643() returned class_8786 (RecipeResultCollection), removed in 1.21.2; the new API exposes class_10298 via method_64881() instead, with no way to obtain or construct a class_8786 from class_514");
    }

    /** was net/minecraft/class_516.method_2647(Lnet/minecraft/class_3439;)V */
    public static void method_2647(net.minecraft.class_516 self, net.minecraft.class_3439 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_516.method_2647(DyeColor) was removed in 1.21.2 with no direct replacement; sign color is now handled via class_10298 (method_2653), which has a different contract (returns boolean, not void) and no DyeColor conversion is available in the new API surface");
    }

    /** was net/minecraft/class_516.method_2649(Lnet/minecraft/class_1662;IILnet/minecraft/class_3439;)V */
    public static void method_2649(net.minecraft.class_516 self, net.minecraft.class_1662 a0, int a1, int a2, net.minecraft.class_3439 a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_516.method_2649 (MerchantScreen trade-slot render helper taking Merchant/int/int/ItemRenderer) was removed in 1.21.2 with no direct equivalent; the new class_516 API surface exposes only query/filter methods (method_64884/64885/64886) and no ItemRenderer-based rendering path");
    }

    /** was net/minecraft/class_516.method_2657()Z */
    public static boolean method_2657(net.minecraft.class_516 self) {
        return self.method_2650().isEmpty();
    }

    /** was net/minecraft/class_516.method_48479()Lnet/minecraft/class_5455; */
    public static net.minecraft.class_5455 method_48479(net.minecraft.class_516 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_516 no longer stores a class_5455 (RegistryWrapper.WrapperLookup); method_48479 has no equivalent in 1.21.2");
    }

    /** was net/minecraft/class_517.method_17065()Ljava/util/Set; */
    public static java.util.Set method_17065(net.minecraft.class_517 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_17065 returned a Set and was deleted in 1.21.2 with no direct replacement in class_517");
    }

    /** was net/minecraft/class_518.method_2659()Lnet/minecraft/class_507; */
    public static net.minecraft.class_507 method_2659(net.minecraft.class_518 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_518.method_2659() was deleted in 1.21.2 with no direct replacement; new class_518 API (method_16891, method_64862) cannot produce class_507");
    }

    /** was net/minecraft/class_521.method_20140(Lnet/minecraft/class_521;I)I */
    public static int method_20140(net.minecraft.class_521 self, net.minecraft.class_521 a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_521.method_20140(class_521, int) was removed in 1.21.2 with no direct equivalent in the new API surface");
    }

    /** was net/minecraft/class_537.method_2784(Lnet/minecraft/class_332;FI)V */
    public static void method_2784(net.minecraft.class_537 self, net.minecraft.class_332 a0, float a1, int a2) {
        self.method_2784(a0, a1, (float) a2);
    }

    /** was net/minecraft/class_549.method_17084(Lnet/minecraft/class_1496;FFF)V */
    public static void method_17084(net.minecraft.class_549 self, net.minecraft.class_1496 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17084(ParticleEffect, float, float, float) was removed in 1.21.2; the replacement a(class_10017) takes a different parameter type that cannot be constructed from the old ParticleEffect + position without its API surface");
    }

    /** was net/minecraft/class_554.method_22952(Lnet/minecraft/class_1690;FFFFF)V */
    public static void method_22952(net.minecraft.class_554 self, net.minecraft.class_1690 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: ParticleManager.method_22952(Particle,5float) was removed in 1.21.2; the new API has no per-particle method accepting a Particle and float parameters");
    }

    /** was net/minecraft/class_557.method_24184(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V */
    public static void method_24184(net.minecraft.class_557 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: class_557.method_24184(MatrixStack, VertexConsumerProvider, int, int, int) was removed in 1.21.2; no remaining member accepts MatrixStack/VertexConsumerProvider, rendering must migrate to DrawContext");
    }

    /** was net/minecraft/class_5601.method_35743()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_35743(net.minecraft.class_5601 self) {
        return self.comp_2995();
    }

    /** was net/minecraft/class_5601.method_35744()Ljava/lang/String; */
    public static java.lang.String method_35744(net.minecraft.class_5601 self) {
        return self.comp_2996();
    }

    /** was net/minecraft/class_5684.method_32661()I */
    public static int method_32661(net.minecraft.class_5684 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_32661 now requires a TextRenderer (class_327) argument not available in the old no-arg signature");
    }

    /** was net/minecraft/class_5684.method_32666(Lnet/minecraft/class_327;IILnet/minecraft/class_332;)V */
    public static void method_32666(net.minecraft.class_5684 self, net.minecraft.class_327 a0, int a1, int a2, net.minecraft.class_332 a3) {
        self.method_32666(a0, a1, a2, -1, 15728880, a3);
    }

    /** was net/minecraft/class_571.method_17089(Lnet/minecraft/class_1584;FFF)V */
    public static void method_17089(net.minecraft.class_571 self, net.minecraft.class_1584 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17089 was removed in 1.21.2 model refactor; no method in the new API surface accepts (class_1584, float, float, float)");
    }

    /** was net/minecraft/class_574.method_17095(Lnet/minecraft/class_1439;FFF)V */
    public static void method_17095(net.minecraft.class_574 self, net.minecraft.class_1439 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17095(ItemStack,float,float,float) was removed in 1.21.2; the renderer now uses state objects (class_10017/class_10037) that cannot be constructed from the old parameters without additional rendering context");
    }

    /** was net/minecraft/class_576.method_17098(Lnet/minecraft/class_1621;FFF)V */
    public static void method_17098(net.minecraft.class_576 self, net.minecraft.class_1621 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_576.method_17098(class_1621,float,float,float) was deleted in 1.21.2 with no direct replacement; new API uses different render context types (class_10017/class_10067) that cannot be constructed from the old parameters");
    }

    /** was net/minecraft/class_5789.method_33431(Lnet/minecraft/class_5776;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_33431(net.minecraft.class_5789 self, net.minecraft.class_5776 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33431(class_5776)->class_2960 was deleted in 1.21.2; new class_5789 returns class_2960 only from a(class_10069) and b(class_10042), neither of which accepts class_5776");
    }

    /** was net/minecraft/class_583.method_2816(Lnet/minecraft/class_1297;FFF)V */
    public static void method_2816(net.minecraft.class_583 self, net.minecraft.class_1297 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_583.method_2816(Entity,float,float,float) was deleted in 1.21.2; the new model API uses method_2819(class_10017) with a render-state object that cannot be constructed from the old (Entity,float,float,float) parameters");
    }

    /** was net/minecraft/class_586.method_17102(Lnet/minecraft/class_1440;FFF)V */
    public static void method_17102(net.minecraft.class_586 self, net.minecraft.class_1440 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17102 (vertex with matrix transform) was removed in 1.21.2; VertexConsumer no longer accepts matrix-transformed float coordinates directly -- the new API uses class_10017/class_10042/class_10050 types whose semantics are incompatible with the old float-coordinate submission pattern");
    }

    /** was net/minecraft/class_591.method_2823(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V */
    public static void method_2823(net.minecraft.class_591 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_591.method_2823(MatrixStack, VertexConsumerProvider, int, int) was removed; 1.21.2 splash rendering requires new context types (class_10034/class_10055/class_1306) not derivable from the old parameters");
        // CB-CONFIDENCE: high -- old signature explicitly deleted with no direct replacement; new API requires context objects unavailable from old params
    }

    /** was net/minecraft/class_591.method_2824(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V */
    public static void method_2824(net.minecraft.class_591 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_591.method_2824(MatrixStack, VertexConsumerProvider, int, int) was removed in 1.21.2; the rendering API now uses DrawContext instead of separate MatrixStack/VertexConsumerProvider, and no equivalent method exists in the new API surface");
    }

    /** was net/minecraft/class_5944.field_29482Lnet/minecraft/class_284; */
    public static net.minecraft.class_284 field_29482() {
        return null;
    }

    /** was net/minecraft/class_5944.field_29483Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_29483() {
        return org.slf4j.LoggerFactory.getLogger(net.minecraft.class_5944.class);
    }

    /** was net/minecraft/class_5944.method_35787()Ljava/lang/String; */
    public static java.lang.String method_35787(net.minecraft.class_5944 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_5944.method_35787() returned the shader program name, which is no longer stored or accessible on the new class_5944");
    }

    /** was net/minecraft/class_596.method_17115(Lnet/minecraft/class_1463;FFF)V */
    public static void method_17115(net.minecraft.class_596 self, net.minecraft.class_1463 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_17115(class_1463, float, float, float) was removed in 1.21.2; the rendering pipeline was restructured (method_17117 now takes class_10059) and no direct equivalent exists");
    }

    /** was net/minecraft/class_598.method_17118(Lnet/minecraft/class_1472;FFF)V */
    public static void method_17118(net.minecraft.class_598 self, net.minecraft.class_1472 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_598.method_17118(class_1472,float,float,float) was removed in 1.21.2 with no direct replacement; the surviving methods (a(class_10017), a(class_10042), method_17119(class_10063)) require new context types that cannot be constructed from the old arguments");
    }

    /** was net/minecraft/class_601.method_17120(Lnet/minecraft/class_1472;FFF)V */
    public static void method_17120(net.minecraft.class_601 self, net.minecraft.class_1472 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_601.method_17120(class_1472,float,float,float) was deleted in 1.21.2; camera update was restructured around new context types (class_10017/class_10042/class_10063) and none accept the old GameRenderer+3-float contract");
        // CB-CONFIDENCE: high -- the new API surface contains no method accepting class_1472 or three floats; the old contract is genuinely inexpressible
    }

    /** was net/minecraft/class_602.method_2829()Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 method_2829(net.minecraft.class_602 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_602.method_2829() returned class_630 (the reference passed to its constructor) but 1.21.2 removed this getter; no method on class_602 returns class_630 anymore");
    }

    /** was net/minecraft/class_638.method_8435(J)V */
    public static void method_8435(net.minecraft.class_638 self, long a0) {
        throw new UnsupportedOperationException("CenturyBridge: ClientWorld.tickEntities(long) was removed in 1.21.2; entity ticking restructured with no method accepting a time parameter");
    }

    /** was net/minecraft/class_7214.method_41966(Lnet/minecraft/class_7110;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_41966(net.minecraft.class_7214 self, net.minecraft.class_7110 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7214.method_41966 was removed; replacement b() requires class_10042 but old arg is class_7110 with no available conversion");
        // CB-CONFIDENCE: high -- the old parameter type class_7110 and the new b() parameter type class_10042 are distinct intermediary classes with no conversion surface provided, so no faithful bridge can be written
    }

    /** was net/minecraft/class_757.field_29403Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 field_29403() {
        return null;
    }

    /** was net/minecraft/class_757.field_3993Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_3993() {
        return org.slf4j.LoggerFactory.getLogger(net.minecraft.class_757.class);
    }

    /** was net/minecraft/class_757.field_4015Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 field_4015() {
        return net.minecraft.class_310.method_1551();
    }

    /** was net/minecraft/class_757.field_4024Lnet/minecraft/class_279; */
    public static net.minecraft.class_279 field_4024() {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.camera (field_4024) was removed in 1.21.2 with no direct replacement; new API surface exposes no accessor returning class_279");
        // CB-CONFIDENCY: high -- prompt explicitly states deletion with no direct replacement, and no method in the new class_757 surface returns class_279
    }

    /** was net/minecraft/class_757.method_3183()Lnet/minecraft/class_279; */
    public static net.minecraft.class_279 method_3183(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() was removed in 1.21.2; camera is no longer stored on or accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_3194()Lnet/minecraft/class_330; */
    public static net.minecraft.class_330 method_3194(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() (method_3194) was removed in 1.21.2; Camera is no longer stored in GameRenderer and no equivalent accessor exists on class_757");
    }

    /** was net/minecraft/class_757.method_34496()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34496(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34496() returned PostEffectProcessor (class_5944) for direct manipulation; 1.21.2 reworked post-processing to class_9779-based pipelines and no longer exposes the processor instance");
    }

    /** was net/minecraft/class_757.method_34497()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34497(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() was removed in 1.21.2; camera is no longer stored on or accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_34498()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34498(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34498() returned the loaded ShaderEffect (class_5944); in 1.21.2 the post-effect pipeline was reworked and GameRenderer no longer holds a class_5944 instance (only an identifier via method_62906)");
    }

    /** was net/minecraft/class_757.method_34499()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34499(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34499() returned the managed PostEffectProcessor (class_5944); in 1.21.2 post-effect handling was moved out of class_757 and no equivalent accessor exists on the new GameRenderer surface");
    }

    /** was net/minecraft/class_757.method_34501()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34501(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34501() returning PostEffectProcessor was removed in 1.21.2; post-effect processing was reworked with no equivalent on GameRenderer");
    }

    /** was net/minecraft/class_757.method_34502()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34502(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34502() returned the PostEffectProcessor (class_5944); in 1.21.2 the post-effect pipeline was reworked and class_757 no longer holds/exposes a class_5944 instance (only method_62906() exposes the post-effect Identifier), so the old contract cannot be faithfully bridged");
    }

    /** was net/minecraft/class_757.method_34503()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34503(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34503() returned TickDeltaManager (class_5944) which was removed in 1.21.2; the render tick counter (class_9779) is now passed as a parameter to render methods and is no longer stored in or accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_34504()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34504(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34504() (getPostProcessor) was removed in 1.21.2 with no direct replacement; post-processing is no longer exposed on GameRenderer");
    }

    /** was net/minecraft/class_757.method_34505()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34505(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34505() returned a PostEffectProcessor (class_5944) with no direct equivalent in 1.21.2; the post-effect pipeline was reworked around class_9779 and no accessor on class_757 yields class_5944 anymore");
    }

    /** was net/minecraft/class_757.method_34506()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34506(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34506() returned a PostEffectProcessor (class_5944) which no longer exists in 1.21.2; the post-processing pipeline was reworked");
    }

    /** was net/minecraft/class_757.method_34507()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34507(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34507() returned the post-processing ShaderEffect (class_5944), which was removed from GameRenderer in 1.21.2; the post-processing pipeline is now managed outside class_757");
    }

    /** was net/minecraft/class_757.method_34508()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34508(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34508() returned the post-processing PostEffectProcessor (class_5944), which is no longer held by GameRenderer in 1.21.2; the post-effect pipeline was reworked and no equivalent instance is exposed");
    }

    /** was net/minecraft/class_757.method_34509()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34509(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34509() (getPostProcessor returning PostEffectProcessor) has no equivalent in 1.21.2; post-processing was reworked and class_5944 is no longer held by class_757");
    }

    /** was net/minecraft/class_757.method_34510()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34510(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34510() returned the PostEffectProcessor (class_5944); in 1.21.2 GameRenderer only exposes a setter method_34521(class_5912) for the renamed processor type and no getter, so the instance cannot be retrieved.");
    }

    /** was net/minecraft/class_757.method_34511()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34511(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34511() returned a PostEffectProcessor (class_5944) for the blur effect, which is no longer exposed on GameRenderer in 1.21.2; blur is now controlled via boolean flags (method_35765/method_35768-35770) and the processor is managed internally");
    }

    /** was net/minecraft/class_757.method_34512()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34512(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (class_5944); the post-processing pipeline was restructured in 1.21.2 and the processor is not held by class_757");
    }

    /** was net/minecraft/class_757.method_34513()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34513(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34513() returned a PostEffectProcessor (class_5944); in 1.21.2 the post-effect pipeline was restructured out of class_757 and no accessor on the new GameRenderer surface yields a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34514()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34514(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34514() (getPostProcessor returning PostEffectProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and GameRenderer no longer holds a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34515()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34515(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34515() returning class_5944 was removed in 1.21.2; no method on class_757 returns class_5944 in the new API");
    }

    /** was net/minecraft/class_757.method_34516()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34516(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34516 (returns class_5944 PostEffectProcessor) is deleted in 1.21.2; post-effect pipeline reworked around class_9779 with no accessor returning the old type");
        // CB-CONFIDENCE: high -- the new class_757 API surface contains no method returning class_5944, and the post-effect system was reworked to class_9779 (method_3188/method_3192), so the old contract is genuinely inexpressible.
    }

    /** was net/minecraft/class_757.method_34518()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34518(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34518 (getPostProcessor returning class_5944) has no equivalent in 1.21.2; post-processing was reworked to class_9779 with only setters (method_3188/method_3192) and no getter exposed");
    }

    /** was net/minecraft/class_757.method_34519()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34519(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34519 (getPostProcessor) removed in 1.21.2; post-processing reworked to class_9779, no getter for class_5944 remains on GameRenderer");
    }

    /** was net/minecraft/class_757.method_34520()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34520(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getPostProcessor() (method_34520) removed in 1.21.2; post-effect system reworked, class_757 no longer exposes class_5944");
    }

    /** was net/minecraft/class_757.method_34523()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34523(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34523() (getPostProcessor returning PostEffectProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and class_757 no longer exposes a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34524()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34524(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34524() returned the active PostEffectProcessor (class_5944), but 1.21.2 reworked post-effects to use class_9779 and no longer exposes a class_5944 instance from GameRenderer");
    }

    /** was net/minecraft/class_757.method_34525()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34525(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a ShaderEffect (class_5944); post-processing pipeline reworked in 1.21.2 with no equivalent accessor");
    }

    /** was net/minecraft/class_757.method_34526()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34526(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34526() returned PostEffectProcessor (class_5944), removed in 1.21.2 post-processing rework; no equivalent accessor on class_757");
    }

    /** was net/minecraft/class_757.method_34527()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34527(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer#method_34527 (getPostProcessor) removed in 1.21.2; post-effect processor no longer exposed by GameRenderer");
    }

    /** was net/minecraft/class_757.method_34529()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34529(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34529 returned a class_5944 PostEffectProcessor (blur post-effect) but 1.21.2 refactored the post-effect pipeline to class_9779; GameRenderer no longer exposes a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34530()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34530(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34530 returned GameRenderer's PostEffectProcessor (class_5944), which is no longer exposed in 1.21.2; the post-effect pipeline was reworked to a frame-graph-based system with no direct accessor for the processor");
    }

    /** was net/minecraft/class_757.method_34531()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34531(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer holds or exposes a PostEffectProcessor (class_5944); post-processing was reworked in 1.21.2 and the processor is managed through a different mechanism (see method_62906/method_3188/method_3192)");
    }

    /** was net/minecraft/class_757.method_34532()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34532(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34532() was removed in 1.21.2; post-effect processing was refactored and GameRenderer no longer holds or exposes a PostEffectProcessor (class_5944)");
    }

    /** was net/minecraft/class_757.method_34533()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34533(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34533 returned a PostEffectProcessor (class_5944) which is no longer exposed by GameRenderer in 1.21.2; the post-processing pipeline was reworked to use class_9779 (see method_3188/method_3192)");
    }

    /** was net/minecraft/class_757.method_34534()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34534(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer holds or exposes a PostEffectProcessor (class_5944); in 1.21.2 post-effect processors are passed externally as class_9779 to method_3188/method_3192, so the getter has no faithful equivalent");
    }

    /** was net/minecraft/class_757.method_34535()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34535(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34535 (getPostProcessor returning class_5944) was removed in 1.21.2; GameRenderer no longer holds a class_5944 ShaderEffect reference -- post-processing was restructured");
    }

    /** was net/minecraft/class_757.method_34536()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34536(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34536 (blur PostEffectProcessor getter) was removed in 1.21.2; post-processing is now driven by class_9779 via method_3188/method_3192 and is no longer exposed as a retrievable instance on class_757");
    }

    /** was net/minecraft/class_757.method_34538(Lnet/minecraft/class_5912;)V */
    public static void method_34538(net.minecraft.class_757 self, net.minecraft.class_5912 a0) {
        self.method_34521(a0);
    }

    /** was net/minecraft/class_757.method_34539()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34539(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34539() returned class_5944 (PostEffectProcessor), but 1.21.2 removed it from GameRenderer; post-processing was refactored to use class_9779 via method_3188/method_3192 with no accessor for the old type");
    }

    /** was net/minecraft/class_757.method_34540()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34540(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34540 returned PostEffectProcessor (class_5944) which is no longer exposed by GameRenderer in 1.21.2; post-processing pipeline was refactored");
    }

    /** was net/minecraft/class_757.method_34542()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34542(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34542 (getPostProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and class_757 no longer holds a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34543()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34543(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34543() returned a PostEffectProcessor (class_5944) that GameRenderer no longer holds in 1.21.2; the post-effect pipeline was restructured (see class_9779 and method_3188/method_3192)");
    }

    /** was net/minecraft/class_757.method_34546()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34546(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34546() returned PostEffectProcessor (class_5944), which has no equivalent in 1.21.2 GameRenderer; the post-effect pipeline was restructured with no direct replacement");
    }

    /** was net/minecraft/class_757.method_34547()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34547(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34547() returned a PostEffectProcessor (class_5944) which is no longer exposed by GameRenderer in 1.21.2; the post-processing pipeline was reworked and no equivalent accessor exists on class_757");
    }

    /** was net/minecraft/class_757.method_34548()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34548(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34548() returned a class_5944 PostEffectProcessor; in 1.21.2 the post-effect pipeline was reworked and GameRenderer no longer exposes individual PostEffectProcessor instances");
    }

    /** was net/minecraft/class_757.method_35767(Ljava/lang/String;)Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_35767(net.minecraft.class_757 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_35767(String)PostEffectProcessor was removed in 1.21.2; post-effect processor loading was restructured out of class_757 with no replacement method on this class");
    }

    /** was net/minecraft/class_757.method_36432()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_36432(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer stores a PostEffectProcessor (class_5944); post-effects are now identified by Identifier (class_2960) via method_62906, and the processor is managed externally");
    }

    /** was net/minecraft/class_757.method_36433()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_36433(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_36433() (getPostProcessor -> PostEffectProcessor) was removed in 1.21.2; the post-processing pipeline is no longer exposed as a class_5944 instance on GameRenderer");
    }

    /** was net/minecraft/class_757.method_42595()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_42595(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_42595 (PostEffectProcessor accessor) was removed in 1.21.2; post-processing pipeline reworked with no equivalent accessor on class_757");
        // CB-CONFIDENCE: high -- the new API surface has no method returning class_5944 and the member is explicitly marked deleted with no direct replacement
    }

    /** was net/minecraft/class_757.method_45774()Lnet/minecraft/class_3302; */
    public static net.minecraft.class_3302 method_45774(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_45774() was deleted in 1.21.2 with no direct replacement; the Camera type it returned (class_3302) is no longer exposed by GameRenderer, whose getCamera (method_19418) now returns class_4184");
    }

    /** was net/minecraft/class_757.method_49037()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_49037(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_49037() returned its PostEffectProcessor, which no longer exists on GameRenderer in 1.21.2; post-processing moved to the new render pipeline");
    }

    /** was net/minecraft/class_757.method_49038()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_49038(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_49038() returned a PostEffectProcessor (class_5944) that GameRenderer no longer holds in 1.21.2; the post-effect pipeline was reworked and no equivalent accessor exists on class_757");
    }

    /** was net/minecraft/class_757.method_51771()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51771(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_51771 (returning class_5944) was removed from GameRenderer in 1.21.2; the post-effect/frustum object it returned is no longer accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_51772()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51772(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_51772() returned a PostEffectProcessor (class_5944) that is no longer held by GameRenderer in 1.21.2; post-effect processing was restructured out of this class");
    }

    /** was net/minecraft/class_757.method_51773()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51773(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (class_5944) in 1.21.2; the post-processing pipeline was moved out of GameRenderer and no field or method of that type remains on class_757");
    }

    /** was net/minecraft/class_757.method_51774()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51774(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_51774() returned the PostEffectProcessor (class_5944); 1.21.2 reworked post-processing to a pipeline and GameRenderer no longer exposes a PostEffectProcessor instance");
    }

    /** was net/minecraft/class_757.method_55249()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_55249(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_55249 (getPostProcessor returning PostEffectProcessor/class_5944) was removed in 1.21.2; the post-effect system was reworked around class_9779 and no class_5944 accessor remains on GameRenderer");
    }

    /** was net/minecraft/class_757.method_56846()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_56846(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (class_5944); the post-effect pipeline was reworked in 1.21.2 and the getter has no equivalent on class_757");
    }

    /** was net/minecraft/class_758.method_23792()V */
    public static void method_23792(net.minecraft.class_758 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_23792 was removed in 1.21.2 with no direct replacement in class_758");
    }

    /** was net/minecraft/class_7754.method_45710(Lnet/minecraft/class_630;)Lcom/google/common/collect/ImmutableList$Builder; */
    public static com.google.common.collect.ImmutableList.Builder method_45710(net.minecraft.class_7754 self, net.minecraft.class_630 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7754.method_45710(Text) returned ImmutableList.Builder for multi-segment text processing; 1.21.2 restructured to return single OrderedText via method_45714/method_62112 with no builder or list equivalent");
    }

    /** was net/minecraft/class_7754.method_45711(Lnet/minecraft/class_1690;FFFFF)V */
    public static void method_45711(net.minecraft.class_7754 self, net.minecraft.class_1690 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_7754.method_45711(VertexConsumer, float, float, float, float, float) was removed in 1.21.2 with no direct replacement; the new API only exposes method_45714()/method_62112() returning class_5607, and no equivalent vertex-drawing method exists on class_7754");
    }

    /** was net/minecraft/class_7754.method_45713(Lnet/minecraft/class_5610;)V */
    public static void method_45713(net.minecraft.class_7754 self, net.minecraft.class_5610 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7754.method_45713(class_5610) was removed in 1.21.2 with no direct replacement; the new class_7754 API only exposes getters (method_45714/method_62112 returning class_5607) and no mutation path accepting a class_5610");
    }

    /** was net/minecraft/class_7754.method_45715()Lcom/google/common/collect/ImmutableList; */
    public static com.google.common.collect.ImmutableList method_45715(net.minecraft.class_7754 self) {
        return com.google.common.collect.ImmutableList.of(self.method_45714(), self.method_62112());
    }

    /** was net/minecraft/class_7761$class_7762.field_40525Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 field_40525() {
        throw new UnsupportedOperationException("CenturyBridge: field_40525 was a static class_630 field deleted in 1.21.2; new API only has instance fields field_40526/40527/40528 with no static equivalent");
        // CB-CONFIDENCE: high -- the old field was static and deleted; the new API exposes only instance fields with no static constant or factory to reconstruct the old value
    }

    /** was net/minecraft/class_790$class_6247.field_32795Lnet/minecraft/class_790; */
    public static net.minecraft.class_790 field_32795() {
        return null;
    }

    /** was net/minecraft/class_790.method_3422()Z */
    public static boolean method_3422(net.minecraft.class_790 self) {
        return true;
    }

    /** was net/minecraft/class_806$class_5827.field_28795Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_28795() {
        throw new UnsupportedOperationException("CenturyBridge: field_28795 was a static Identifier constant on class_806$class_5827, deleted in 1.21.2 with no direct replacement; its value cannot be reconstructed from the new API");
    }

    /** was net/minecraft/class_8138.method_49050(Lnet/minecraft/class_8113;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_49050(net.minecraft.class_8138 self, net.minecraft.class_8113 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_49050 was deleted in 1.21.2; no new API on class_8138 returns an Identifier from class_8113");
    }

    /** was net/minecraft/class_826.method_32148()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32148(net.minecraft.class_826 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_826.method_32148() has no equivalent in 1.21.2; the new class_826 exposes only a constructor and a void render method, with no accessor returning class_5607");
    }

    /** was net/minecraft/class_826.method_32149()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32149(net.minecraft.class_826 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_826.method_32149() returned a class_5607 (PostEffectProcessor) but the 1.21.2 class_826 surface exposes no field or method yielding one; post-effect handling was reworked out of this class");
        // CB-CONFIDENCE: high -- the new API surface lists no member returning class_5607, so no faithful bridge exists
    }

    /** was net/minecraft/class_837.method_45793(Lnet/minecraft/class_4587;IILnet/minecraft/class_3879;Lnet/minecraft/class_4588;)V */
    public static void method_45793(net.minecraft.class_837 self, net.minecraft.class_4587 a0, int a1, int a2, net.minecraft.class_3879 a3, net.minecraft.class_4588 a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_45793 cannot be bridged -- new method_45800 requires class_4597 render context (replacing VertexConsumerProvider) and class_4719 transform mode, neither derivable from old parameters");
    }

    /** was net/minecraft/class_876.method_23153(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_4588;IIIFFIIII)V */
    public static void method_23153(net.minecraft.class_876 self, net.minecraft.class_4587.class_4665 a0, net.minecraft.class_4588 a1, int a2, int a3, int a4, float a5, float a6, int a7, int a8, int a9, int a10) {
        throw new UnsupportedOperationException("CenturyBridge: SignBlockEntityRenderer.method_23153 rendered sign text from individual style parameters (MatrixStack.Entry, text colors, glow flags, scale); 1.21.2 restructured sign text rendering to require a SignText (class_10017) object and a full MatrixStack (class_4587), neither constructable from the old method's primitive parameters");
    }

    /** was net/minecraft/class_878.method_3881(Lnet/minecraft/class_1545;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3881(net.minecraft.class_878 self, net.minecraft.class_1545 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3881(Identifier)->Packet has no 1.21.2 equivalent; new b(class_10042)->Packet requires a RecipeCollection (class_10042) not derivable from an Identifier via class_878's API");
    }

    /** was net/minecraft/class_880.method_3885(Lnet/minecraft/class_1549;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3885(net.minecraft.class_880 self, net.minecraft.class_1549 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3885 was deleted with no direct replacement; method b takes class_10042 not class_1549, no conversion available");
    }

    /** was net/minecraft/class_880.method_3886(Lnet/minecraft/class_1549;Lnet/minecraft/class_4587;F)V */
    public static void method_3886(net.minecraft.class_880 self, net.minecraft.class_1549 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_880.method_3886 was deleted in 1.21.2 with no direct replacement; the remaining API surface (constructor + identifier lookup) cannot reproduce the former render-path behavior");
    }

    /** was net/minecraft/class_885.method_3896(Lnet/minecraft/class_1431;Lnet/minecraft/class_4587;FFFF)V */
    public static void method_3896(net.minecraft.class_885 self, net.minecraft.class_1431 a0, net.minecraft.class_4587 a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_3896 was removed in 1.21.2; the new render API (method a) requires a pre-populated RenderState (class_10042) which cannot be constructed from the old (class_1431, 4 floats) parameters");
        // CB-CONFIDENCE: medium -- method explicitly deleted; new API uses RenderState-based paradigm incompatible with old direct-data parameters
    }

    /** was net/minecraft/class_885.method_3897(Lnet/minecraft/class_1431;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3897(net.minecraft.class_885 self, net.minecraft.class_1431 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_885.method_3897(class_1431) was deleted in 1.21.2; the structurally similar b(class_10042) takes a different parameter type with no known conversion from class_1431");
    }

    /** was net/minecraft/class_891.method_3905(Lnet/minecraft/class_1670;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3905(net.minecraft.class_891 self, net.minecraft.class_1670 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3905 returned class_2960 (Identifier) from class_1670 (Camera), but 1.21.2 class_891 has no Identifier-returning equivalent; the API was restructured around class_10017 and int-returning methods");
    }

    /** was net/minecraft/class_891.method_3906(Lnet/minecraft/class_1670;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3906(net.minecraft.class_891 self, net.minecraft.class_1670 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_3906(ParticleEffect,float,float,VertexConsumerProvider,LightmapTextureManager,int) was deleted in 1.21.2; the replacement a(class_10017,...) requires a class_10017 that cannot be constructed from the old ParticleEffect+float parameters (only obtainable via d(), which would ignore caller-supplied args)");
    }

    /** was net/minecraft/class_892.method_3909(Lnet/minecraft/class_1511;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3909(net.minecraft.class_892 self, net.minecraft.class_1511 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3909 returned a per-particle texture Identifier (class_2960); 1.21.2 particles use sprite-atlas descriptors (class_10014 via method_62416 / class_10017 via d) with no equivalent single texture Identifier");
    }

    /** was net/minecraft/class_893.method_3910(Lnet/minecraft/class_1577;Lnet/minecraft/class_4587;F)V */
    public static void method_3910(net.minecraft.class_893 self, net.minecraft.class_1577 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_3910 rendered an ItemStack (class_1577) via MatrixStack (class_4587); class_893 in 1.21.2 only exposes Identifier-returning methods (a/b) and no longer handles ItemStack rendering");
    }

    /** was net/minecraft/class_894.method_3911(Lnet/minecraft/class_1560;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3911(net.minecraft.class_894 self, net.minecraft.class_1560 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        self.method_62422(a0, self.method_62421(), a1);
    }

    /** was net/minecraft/class_895.method_32165()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32165(net.minecraft.class_895 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_895.method_32165() returned a class_5607 (PostEffectProcessor) that class_895 no longer exposes in 1.21.2; the post-effect pipeline was reworked and no equivalent accessor exists");
    }

    /** was net/minecraft/class_8974.method_55239(Lnet/minecraft/class_9236;FFFFF)V */
    public static void method_55239(net.minecraft.class_8974 self, net.minecraft.class_9236 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_8974.method_55239(class_9236,float,float,float,float,float) was removed in 1.21.2 with no direct replacement; new a(class_10017) cannot be bridged without class_10017's constructor API to wrap the old (Random + 5 floats) arguments");
    }

    /** was net/minecraft/class_897.method_3931(Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3931(net.minecraft.class_897 self, net.minecraft.class_1297 a0) {
        throw new UnsupportedOperationException("CenturyBridge: EntityRenderer.getTexture(Entity) was removed in 1.21.2; textures are now resolved through the render state system, not via EntityRenderer");
        // CB-CONFIDENCE: high -- the new class_897 API surface has no method returning class_2960 (Identifier); getTexture was genuinely removed in the 1.21.2 entity renderer refactor
    }

    /** was net/minecraft/class_8987.method_55269(Lnet/minecraft/class_9236;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_55269(net.minecraft.class_8987 self, net.minecraft.class_9236 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_55269 was removed in 1.21.2 with no direct replacement; the new class_8987 API exposes no path from class_9236 to class_2960");
    }

    /** was net/minecraft/class_8987.method_55270(Lnet/minecraft/class_9236;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_55270(net.minecraft.class_8987 self, net.minecraft.class_9236 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_55270 was removed; replacement a() requires class_10017 (not class_9236) and drops the two position floats with no available conversion");
    }

    /** was net/minecraft/class_900.method_3963(Lnet/minecraft/class_1669;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3963(net.minecraft.class_900 self, net.minecraft.class_1669 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3963 returned an Identifier (model/texture id) for an ItemStack; 1.21.2 replaced identifier-based lookups with state-based rendering via method_62429(class_1669, class_10020, float) and no longer exposes the Identifier");
    }

    /** was net/minecraft/class_901.method_3964(Lnet/minecraft/class_1540;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3964(net.minecraft.class_901 self, net.minecraft.class_1540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_901.method_3964(Vec3d)->Identifier was removed in 1.21.2 with no direct replacement; no method on the new class_901 returns an Identifier");
    }

    /** was net/minecraft/class_902.method_3967(Lnet/minecraft/class_1303;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3967(net.minecraft.class_902 self, net.minecraft.class_1303 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3967(Camera) returned class_2960 (Matrix4f) computed from the camera alone; 1.21.2 replaced this with method_62434(Camera, class_10022, float) which writes into a class_10022 holder and requires a tick-delta parameter, and class_2960 itself was removed in the JOML migration");
    }

    /** was net/minecraft/class_903.method_3969(Lnet/minecraft/class_1671;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3969(net.minecraft.class_903 self, net.minecraft.class_1671 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_903.method_3969(ItemStack)Identifier was removed in 1.21.2; ItemRenderer no longer resolves an ItemStack to a texture Identifier (texture/sprite resolution moved to the new model system, and no method on class_903 returns class_2960)");
    }

    /** was net/minecraft/class_905.method_3973(Lnet/minecraft/class_1571;Lnet/minecraft/class_4587;F)V */
    public static void method_3973(net.minecraft.class_905 self, net.minecraft.class_1571 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62448(a0, self.method_62449(), a2);
    }

    /** was net/minecraft/class_906.method_3975(Lnet/minecraft/class_1536;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3975(net.minecraft.class_906 self, net.minecraft.class_1536 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3975 returned an Identifier derived from the Camera; 1.21.2 replaced it with boolean predicates (method_62441/method_62442) and direct rendering (method_62443), so the Identifier is no longer exposed");
    }

    /** was net/minecraft/class_908.method_3980(Lnet/minecraft/class_1570;Lnet/minecraft/class_4587;F)V */
    public static void method_3980(net.minecraft.class_908 self, net.minecraft.class_1570 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62450(a0, self.method_62451(), a2);
    }

    /** was net/minecraft/class_912.method_3985(Lnet/minecraft/class_1642;Lnet/minecraft/class_4587;F)V */
    public static void method_3985(net.minecraft.class_912 self, net.minecraft.class_1642 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_912.method_3985(ItemStack, MatrixStack, float) was removed in 1.21.2; class_912 no longer exposes rendering methods, only identifier accessors a() and b()");
    }

    /** was net/minecraft/class_918.method_29711(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;ZZ)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_29711(net.minecraft.class_918 self, net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, boolean a2, boolean a3) {
        return self.method_23181(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_951.method_4127(Lnet/minecraft/class_1477;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4127(net.minecraft.class_951 self, net.minecraft.class_1477 a0) {
        return self.method_33431(self.method_62542());
    }

    /** was net/minecraft/class_1043.<init>(Lnet/minecraft/class_1011;)V */
    public static net.minecraft.class_1043 cbNewclass_1043(net.minecraft.class_1011 a0) {
        return new net.minecraft.class_1043(() -> "CenturyBridge dynamic texture", a0);
    }

    /** was net/minecraft/class_1086.field_5347Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5347() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5349Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5349() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5350Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5350() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5352Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5352() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5353Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5353() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5355Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5355() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5358Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5358() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5361Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5361() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_1086.field_5366Lnet/minecraft/class_1086; */
    public static net.minecraft.class_1086 field_5366() {
        return net.minecraft.class_1086.field_63619;
    }

    /** was net/minecraft/class_304.<init>(Ljava/lang/String;Lnet/minecraft/class_3675$class_307;ILjava/lang/String;)V */
    public static net.minecraft.class_304 cbNewclass_304(java.lang.String a0, net.minecraft.class_3675.class_307 a1, int a2, java.lang.String a3) {
        throw new UnsupportedOperationException("CenturyBridge: the legacy String category cannot be converted to the required class_304.class_11900 category using the provided API surface");
    }

    /** was net/minecraft/class_304.<init>(Ljava/lang/String;ILjava/lang/String;)V */
    public static net.minecraft.class_304 cbNewclass_304(java.lang.String a0, int a1, java.lang.String a2) {
        throw new UnsupportedOperationException("CenturyBridge: the legacy String category cannot be converted to the required class_304.class_11900 category using the available API");
    }

    /** was net/minecraft/class_3879.<init>(Ljava/util/function/Function;)V */
    public static net.minecraft.class_3879 cbNewclass_3879(java.util.function.Function a0) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a net.minecraft.class_630 instance that cannot be derived from the old Function argument");
    }

    /** was net/minecraft/class_3999.field_17828Lnet/minecraft/class_3999; */
    public static net.minecraft.class_3999 field_17828() {
        return net.minecraft.class_3999.field_62622;
    }

    /** was net/minecraft/class_3999.field_17829Lnet/minecraft/class_3999; */
    public static net.minecraft.class_3999 field_17829() {
        return net.minecraft.class_3999.field_62622;
    }

    /** was net/minecraft/class_437$class_6390.field_33825Lnet/minecraft/class_6379; */
    public static net.minecraft.class_6379 field_33825() {
        throw new UnsupportedOperationException("CenturyBridge: old field_33825 was instance-specific, but this zero-argument static shim has no receiver from which to call comp_4463()");
    }

    /** was net/minecraft/class_4588.method_22912(DDD)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22912(net.minecraft.class_4588 self, double a0, double a1, double a2) {
        return self.method_22912((float) a0, (float) a1, (float) a2);
    }

    /** was net/minecraft/class_4588.method_22918(Lorg/joml/Matrix4f;FFF)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22918(net.minecraft.class_4588 self, org.joml.Matrix4f a0, float a1, float a2, float a3) {
        return self.method_22918((org.joml.Matrix4fc) a0, a1, a2, a3);
    }

    /** was net/minecraft/class_4588.method_23919(FFFFFFFFFIIFFF)V */
    public static void method_23919(net.minecraft.class_4588 self, float a0, float a1, float a2, float a3, float a4, float a5, float a6, float a7, float a8, int a9, int a10, float a11, float a12, float a13) {
        self.method_23919(a0, a1, a2, ((int) (a6 * 255.0F) << 24) | ((int) (a3 * 255.0F) << 16) | ((int) (a4 * 255.0F) << 8) | (int) (a5 * 255.0F), a7, a8, a9, a10, a11, a12, a13);
    }

    /** was net/minecraft/class_5603.field_27707F */
    public static float field_27707() {
        return net.minecraft.class_5603.field_27701.comp_3002();
    }

    /** was net/minecraft/class_583.<init>()V */
    public static net.minecraft.class_583 cbNewclass_583() {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a net.minecraft.class_630 instance, but the old zero-argument constructor provides no equivalent value");
    }

    /** was net/minecraft/class_6367.<init>(IIZZ)V */
    public static net.minecraft.class_6367 cbNewclass_6367(int a0, int a1, boolean a2, boolean a3) {
        return new net.minecraft.class_6367("", a0, a1, a2);
    }

    /** was net/minecraft/class_7186.<init>(FLorg/joml/Vector3f;Lnet/minecraft/class_7179$class_7180;)V */
    public static net.minecraft.class_7186 cbNewclass_7186(float a0, org.joml.Vector3f a1, net.minecraft.class_7179.class_7180 a2) {
        return new net.minecraft.class_7186(a0, a1, a2);
    }

    /** was net/minecraft/class_746.field_17892Lnet/minecraft/class_638; */
    public static net.minecraft.class_638 field_17892() {
        return net.minecraft.class_310.method_1551().field_1687;
    }

    /** was net/minecraft/class_7775.method_45872(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1100; */
    public static net.minecraft.class_1100 method_45872(net.minecraft.class_7775 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: replacement returns net.minecraft.class_10819, not net.minecraft.class_1100, and no conversion API is available");
    }

    /** was net/minecraft/class_777.<init>([IILnet/minecraft/class_2350;Lnet/minecraft/class_1058;Z)V */
    public static net.minecraft.class_777 cbNewclass_777(int[] a0, int a1, net.minecraft.class_2350 a2, net.minecraft.class_1058 a3, boolean a4) {
        throw new UnsupportedOperationException("CenturyBridge: the old packed int[] vertex data cannot be faithfully converted to the new constructor's four Vector3fc vertices and four packed long values without the removed vertex-format decoding contract");
    }

    /** was net/minecraft/class_785.<init>(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Ljava/util/Map;Lnet/minecraft/class_789;Z)V */
    public static net.minecraft.class_785 cbNewclass_785(org.joml.Vector3f a0, org.joml.Vector3f a1, java.util.Map a2, net.minecraft.class_789 a3, boolean a4) {
        return new net.minecraft.class_785(a0, a1, a2, a3, a4, 0);
    }

    /** was net/minecraft/class_785.field_4232Lnet/minecraft/class_789; */
    public static net.minecraft.class_789 field_4232() {
        throw new UnsupportedOperationException("CenturyBridge: old field_4232 was an instance-specific rotation, but the static shim has no class_785 receiver");
    }

    /** was net/minecraft/class_793.field_4249Lnet/minecraft/class_796; */
    public static net.minecraft.class_796 field_4249() {
        return new net.minecraft.class_796();
    }

    /** was net/minecraft/class_793.field_4253Lnet/minecraft/class_793; */
    public static net.minecraft.class_793 field_4253() {
        throw new UnsupportedOperationException("CenturyBridge: the removed field value cannot be reconstructed from the new class_793 API without its original component constants");
    }

    /** was net/minecraft/class_804.<init>(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Lorg/joml/Vector3f;)V */
    public static net.minecraft.class_804 cbNewclass_804(org.joml.Vector3f a0, org.joml.Vector3f a1, org.joml.Vector3f a2) {
        return new net.minecraft.class_804(a0, a1, a2);
    }

    /** was net/minecraft/class_827.method_3569(Lnet/minecraft/class_2586;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V */
    public static void method_3569(net.minecraft.class_827 self, net.minecraft.class_2586 a0, float a1, net.minecraft.class_4587 a2, net.minecraft.class_4597 a3, int a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new split extraction/render API requires render state and queue/context parameters that cannot be derived from the old arguments");
    }

    /** was net/minecraft/class_970.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_572;Lnet/minecraft/class_572;Lnet/minecraft/class_1092;)V */
    public static net.minecraft.class_970 cbNewclass_970(net.minecraft.class_3883 a0, net.minecraft.class_572 a1, net.minecraft.class_572 a2, net.minecraft.class_1092 a3) {
        throw new UnsupportedOperationException("CenturyBridge: old class_572 and class_1092 constructor arguments cannot be converted to required class_11677 and class_10197 types from the available API");
    }

    /** was net/minecraft/class_989.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_989 cbNewclass_989(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        return new net.minecraft.class_989(a0);
    }

    /**
     * superRedirects: a 1.20.1 override's super.render(context, mx, my, delta).
     * 1.20.1 Screen.render drew the widget list and nothing else; since 1.20.2
     * it draws the background first, over everything the mod just drew. Render
     * exactly what the old contract promised.
     */
    public static void cbRenderWidgets(net.minecraft.class_437 self, net.minecraft.class_332 context, int mouseX, int mouseY, float delta) {
        for (net.minecraft.class_4068 d : ((top.dext.centurybridge.shims.v1_21_11.ScreenDrawablesAccessor) self).cbDrawables()) {
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

    /** was GameRenderer.getBasicProjectionMatrix(double fov) -> float since 1.21.2 */
    public static org.joml.Matrix4f method_22973(net.minecraft.class_757 self, double fov) {
        return self.method_22973((float) fov);
    }

    /** was ModelPart.render(matrices, vertices, light, overlay, r, g, b, a): one packed ARGB int since 1.21 */
    public static void method_22699(net.minecraft.class_630 self, net.minecraft.class_4587 matrices, net.minecraft.class_4588 vertices, int light, int overlay,
            float red, float green, float blue, float alpha) {
        int argb = (Math.round(alpha * 255f) << 24) | (Math.round(red * 255f) << 16) | (Math.round(green * 255f) << 8) | Math.round(blue * 255f);
        self.method_22699(matrices, vertices, light, overlay, argb);
    }

    /** was RenderLayers.getEntityBlockLayer(state, direct): the direct/buffered flag left in 1.21.5 */
    public static net.minecraft.class_1921 method_23683(net.minecraft.class_2680 state, boolean direct) {
        return net.minecraft.class_4696.method_23683(state);
    }

    /** was ButtonWidget(x, y, w, h, text, onPress): the ctor went protected in 1.20.2 -- through the builder */
    public static net.minecraft.class_4185 cbNewclass_4185(int x, int y, int w, int h, net.minecraft.class_2561 text, net.minecraft.class_4185.class_4241 action) {
        return net.minecraft.class_4185.method_46430(text, action).method_46434(x, y, w, h).method_46431();
    }

    /** was net/minecraft/class_3675.method_15985(II)Lnet/minecraft/class_3675$class_306; */
    public static net.minecraft.class_3675.class_306 method_15985(int a0, int a1) {
        return net.minecraft.class_3675.method_15985(new net.minecraft.class_11908(a0, a1, 0));
    }

    /** was net/minecraft/class_3887.method_23196(Lnet/minecraft/class_583;Lnet/minecraft/class_583;Lnet/minecraft/class_2960;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1309;FFFFFFFFF)V */
    public static void method_23196(net.minecraft.class_583 a0, net.minecraft.class_583 a1, net.minecraft.class_2960 a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5, net.minecraft.class_1309 a6, float a7, float a8, float a9, float a10, float a11, float a12, float a13, float a14, float a15) {
        int color = 0xFF000000 | ((int)(a13 * 255.0f) << 16) | ((int)(a14 * 255.0f) << 8) | (int)(a15 * 255.0f);
    }

    /** was net/minecraft/class_3887.method_23196(Lnet/minecraft/class_583;Lnet/minecraft/class_583;Lnet/minecraft/class_2960;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1309;FFFFFFI)V */
    public static void method_23196(net.minecraft.class_583 a0, net.minecraft.class_583 a1, net.minecraft.class_2960 a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5, net.minecraft.class_1309 a6, float a7, float a8, float a9, float a10, float a11, float a12, int a13) {
        throw new UnsupportedOperationException("CenturyBridge: class_10042 render state (item stacks, arm poses, 9 floats, 8 booleans) cannot be reconstructed from old params (BakedModel a1, LivingEntity a6, 6 floats) — entity/item/pose data must be pre-computed by the new pipeline");
    }

    /** was net/minecraft/class_4696.method_23679(Lnet/minecraft/class_2680;)Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23679(net.minecraft.class_2680 a0) {
        return net.minecraft.class_4696.method_23683(a0);
    }

    /** was net/minecraft/class_4696.method_23680(Lnet/minecraft/class_3610;)Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23680(net.minecraft.class_3610 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new fluid-layer API returns class_11515 BlockRenderLayer, which cannot be faithfully converted to the old class_1921 RenderLayer contract");
    }

    /** was net/minecraft/class_4722.method_33081(Lnet/minecraft/class_5321;)Lnet/minecraft/class_4730; */
    public static net.minecraft.class_4730 method_33081(net.minecraft.class_5321 a0) {
        return net.minecraft.class_4722.method_49341(a0);
    }

    /** was net/minecraft/class_4722.method_33083(Lnet/minecraft/class_5321;)Lnet/minecraft/class_4730; */
    public static net.minecraft.class_4730 method_33083(net.minecraft.class_5321 a0) {
        return net.minecraft.class_4722.method_49341(a0);
    }

    /** was net/minecraft/class_4722.method_48480()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_48480() {
        return net.minecraft.class_4722.method_48480(false);
    }

    /** was net/minecraft/class_4881.method_25074(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_4881; */
    public static net.minecraft.class_4881 method_25074(com.google.gson.JsonObject a0) {
        return net.minecraft.class_4881.method_25074(a0.toString());
    }

    /** was net/minecraft/class_4896.method_25446(Lnet/minecraft/class_630;Lnet/minecraft/class_630;Lnet/minecraft/class_1309;Z)V */
    public static void method_25446(net.minecraft.class_630 a0, net.minecraft.class_630 a1, net.minecraft.class_1309 a2, boolean a3) {
        throw new UnsupportedOperationException("CenturyBridge: cannot derive float and int from LivingEntity without knowing the new API semantics");
    }

    /** was net/minecraft/class_4896.method_29351(Lnet/minecraft/class_630;Lnet/minecraft/class_630;Lnet/minecraft/class_1308;FF)V */
    public static void method_29351(net.minecraft.class_630 a0, net.minecraft.class_630 a1, net.minecraft.class_1308 a2, float a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: class_4896.method_29351 now takes EquipmentSlot (class_1306) instead of LivingEntity (class_1308); cannot derive slot from entity without additional context");
    }

    /** was net/minecraft/class_596.method_32034()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32034() {
        return net.minecraft.class_596.method_32034(false);
    }

    /** was net/minecraft/class_7532.method_44443(Lnet/minecraft/class_332;Lnet/minecraft/class_2960;III)V */
    public static void method_44443(net.minecraft.class_332 a0, net.minecraft.class_2960 a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: class_7532.method_44443 now requires class_8685 (SpriteContents) instead of class_2960 (Identifier); class_8685 cannot be constructed from an Identifier alone, and class_7532 exposes no accessor to obtain its class_8685");
    }

    /** was net/minecraft/class_7532.method_44445(Lnet/minecraft/class_332;Lnet/minecraft/class_2960;IIIZZ)V */
    public static void method_44445(net.minecraft.class_332 a0, net.minecraft.class_2960 a1, int a2, int a3, int a4, boolean a5, boolean a6) {
        net.minecraft.class_7532.method_44445(a0, a1, a2, a3, a4, a5, a6, 0);
    }

    /** was net/minecraft/class_758.method_3211(Lnet/minecraft/class_4184;Lnet/minecraft/class_758$class_4596;FZF)V */
    public static void method_3211(net.minecraft.class_4184 a0, net.minecraft.class_758.class_4596 a1, float a2, boolean a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_3211 now requires a Vector4f (computed by method_62185 which needs a class_638 world) that was previously internal; no equivalent without world access");
    }

    /** was net/minecraft/class_775.method_29708(Lnet/minecraft/class_1920;Lnet/minecraft/class_2338;Lnet/minecraft/class_3610;Lnet/minecraft/class_2680;Lnet/minecraft/class_2350;Lnet/minecraft/class_3610;)Z */
    public static boolean method_29708(net.minecraft.class_1920 a0, net.minecraft.class_2338 a1, net.minecraft.class_3610 a2, net.minecraft.class_2680 a3, net.minecraft.class_2350 a4, net.minecraft.class_3610 a5) {
        return net.minecraft.class_775.method_29708(a2, a3, a4, a5);
    }

    /** was net/minecraft/class_7973.method_47761(Ljava/util/List;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_47761(java.util.List a0) {
        return net.minecraft.class_7973.method_47761(a0).codec();
    }

    /** was net/minecraft/class_837.method_32154()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32154() {
        return net.minecraft.class_837.method_32154(false);
    }

    /** was net/minecraft/class_892.method_23155(Lnet/minecraft/class_1511;F)F */
    public static float method_23155(net.minecraft.class_1511 a0, float a1) {
        return net.minecraft.class_892.method_23155(a1);
    }

    private ClientStatics() {
    }
}
