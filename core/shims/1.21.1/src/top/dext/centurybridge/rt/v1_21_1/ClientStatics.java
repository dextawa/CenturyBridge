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

    /** was net/minecraft/class_4588.method_22919(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;FFFII)V */
    public void method_22919(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        ((net.minecraft.class_4588) (Object) this).method_22919(a0, a1, a2, a3, a4, 1.0f, a5, a6);
    }

    /** was net/minecraft/class_4588.method_22920(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;[FFFF[IIZ)V */
    public void method_22920(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float[] a2, float a3, float a4, float a5, int[] a6, int a7, boolean a8) {
        ((net.minecraft.class_4588) (Object) this).method_22920(a0, a1, a2, a3, a4, a5, 1.0f, a6, a7, a8);
    }

    /** was net/minecraft/class_636.method_2904()F */
    public static float method_2904(net.minecraft.class_636 self) {
        return (float) self.method_51888();
        // CB-CONFIDENCE: medium -- method_51888 is the likely int-returning replacement for the old float getBlockReachDistance; the int-to-float cast loses the 0.5 fractional reach in survival mode
    }

    /** was net/minecraft/class_7196.method_54618(Ljava/lang/String;Ljava/lang/Runnable;)V */
    public static void method_54618(net.minecraft.class_7196 self, java.lang.String a0, java.lang.Runnable a1) {
        self.method_57784(a0, a1);
    }

    /** was net/minecraft/class_7557$class_7558.field_40809Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40809() {
        return net.minecraft.class_7557.class_7558.field_40809.codec();
    }

    /** was net/minecraft/class_7557$class_7559.field_40810Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40810() {
        return net.minecraft.class_7557.class_7559.field_40810.codec();
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

    /** was net/minecraft/class_1100.method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;Lnet/minecraft/class_2960;)Lnet/minecraft/class_1087; */
    public net.minecraft.class_1087 method_4753(net.minecraft.class_7775 a0, java.util.function.Function a1, net.minecraft.class_3665 a2, net.minecraft.class_2960 a3) {
        return ((net.minecraft.class_1100) (Object) this).method_4753(a0, a1, a2);
    }

    /** was net/minecraft/class_1921.method_24296()Ljava/util/Optional; */
    public static java.util.Optional method_24296(net.minecraft.class_1921 self) {
        return self.method_23289();
    }

    /** was net/minecraft/class_310.method_1488()F */
    public static float method_1488(net.minecraft.class_310 self) {
        return (float) self.method_41734();
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

    /** was net/minecraft/class_6777.method_47621(Lnet/minecraft/class_310;Lnet/minecraft/class_437;Lnet/minecraft/class_315;)Lnet/minecraft/class_6777; */
    public static net.minecraft.class_6777 method_47621(net.minecraft.class_6777 self, net.minecraft.class_310 a0, net.minecraft.class_437 a1, net.minecraft.class_315 a2) {
        return new net.minecraft.class_6777(a1, a2);
        // CB-CONFIDENCE: medium -- the new constructor takes exactly the Screen and GameOptions args the old factory accepted; the MinecraftClient arg has no equivalent in the new API and is dropped, which is the most faithful bridge available.
    }

    /** was net/minecraft/class_918.method_30115(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;Lnet/minecraft/class_4587$class_4665;)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_30115(net.minecraft.class_918 self, net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, net.minecraft.class_4587.class_4665 a2) {
        return self.method_30114(a0, a1, a2);
    }

    /** was net/minecraft/class_290.field_1579Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1579() {
        return net.minecraft.class_296.field_52113;
    }

    /** was net/minecraft/class_4588.method_23919(FFFFFFFFFIIFFF)V */
    public static void method_23919(net.minecraft.class_4588 self, float a0, float a1, float a2, float a3, float a4, float a5, float a6, float a7, float a8, int a9, int a10, float a11, float a12, float a13) {
        self.method_23919(a0, a1, a2, ((int) (a5 * 255.0F) << 24) | ((int) (a3 * 255.0F) << 16) | ((int) (a4 * 255.0F) << 8) | (int) (a6 * 255.0F), a7, a8, a9, a10, a11, a12, a13);
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
