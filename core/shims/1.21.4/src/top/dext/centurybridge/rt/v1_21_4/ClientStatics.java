package top.dext.centurybridge.rt.v1_21_4;

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

    public void method_4884(net.minecraft.class_1113 a0, net.minecraft.class_1146 a1) {
        ((net.minecraft.class_1145) (Object) this).method_4884(a0, a1, 1.0f);
    }

    /** was net/minecraft/class_1921.method_23585()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23585(net.minecraft.class_1921 self) {
        return net.minecraft.class_1921.method_23584(net.minecraft.class_2960.method_60654("textures/misc/enchanted_glint_item.png"));
    }

    /** was net/minecraft/class_310.method_1589()Z */
    public static boolean method_1589(net.minecraft.class_310 self) {
        return true;
    }

    /** was net/minecraft/class_310.method_44649()Z */
    public static boolean method_44649(net.minecraft.class_310 self) {
        return self.method_47595();
    }

    /** was net/minecraft/class_332.method_48585(Lnet/minecraft/class_2960;IIIIIIII)V */
    public static void method_48585(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8) {
        self.method_52708(net.minecraft.class_1921::method_62277, a0, a1, a2, a3, a4, a5, a6, a7, a8);
    }

    /** was net/minecraft/class_332.method_48586(Lnet/minecraft/class_2960;IIIIIIIII)V */
    public static void method_48586(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
        self.method_52708(net.minecraft.class_1921::method_62277, a0, a1, a2, a3, a4, a5, a6, a8, a9);
    }

    /** was net/minecraft/class_332.method_48587(Lnet/minecraft/class_2960;IIIIIIIIIIII)V */
    public static void method_48587(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9, int a10, int a11, int a12) {
        int i = Math.min(a5, a3 / 2);
        int j = Math.min(a5, a3 / 2);
        int k = Math.min(a6, a4 / 2);
        int l = Math.min(a6, a4 / 2);
        if (a3 == a5 && a4 == a6) {
            self.method_25290(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, a3, a4, a11, a12);
        } else if (a4 == a6) {
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, i, a4, i, a4, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2, (float)(a9 + a5), (float)a10, a3 - i - j, a4, a7, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2, (float)(a9 + a5 + a7), (float)a10, j, a4, j, a4, a11, a12, -1);
        } else if (a3 == a5) {
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, a3, k, a3, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + k, (float)a9, (float)(a10 + a6), a3, a4 - k - l, a7, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + a4 - l, (float)a9, (float)(a10 + a6 + a8), a3, l, a3, l, a11, a12, -1);
        } else {
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, i, k, i, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2, (float)(a9 + a5), (float)a10, a3 - i - j, k, a7, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2, (float)(a9 + a5 + a7), (float)a10, j, k, j, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + k, (float)a9, (float)(a10 + a6), i, a4 - k - l, i, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2 + k, (float)(a9 + a5), (float)(a10 + a6), a3 - i - j, a4 - k - l, a7, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2 + k, (float)(a9 + a5 + a7), (float)(a10 + a6), j, a4 - k - l, j, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + a4 - l, (float)a9, (float)(a10 + a6 + a8), i, l, i, l, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2 + a4 - l, (float)(a9 + a5), (float)(a10 + a6 + a8), a3 - i - j, l, a7, l, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2 + a4 - l, (float)(a9 + a5 + a7), (float)(a10 + a6 + a8), j, l, j, l, a11, a12, -1);
        }
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

    /** was net/minecraft/class_4341.field_19576Lnet/minecraft/class_4341$class_4343; */
    public static net.minecraft.class_4341.class_4343 field_19576() {
        return net.minecraft.class_4341.field_45232;
    }

    /** was net/minecraft/class_4341.method_21021()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21021(net.minecraft.class_4341 self) {
        return java.lang.Boolean.FALSE;
    }

    /** was net/minecraft/class_4346.method_35685(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_4346; */
    public static net.minecraft.class_4346 method_35685(net.minecraft.class_4346 self, java.lang.String a0, java.lang.String a1) {
        self.method_21042(a0, a1);
        return self;
    }

    /** was net/minecraft/class_4419.method_25228(I)I */
    public static int method_25228(net.minecraft.class_4419 self, int a0) {
        return 40 + a0 * 13;
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
        return self.method_2966().getName();
    }

    /** was net/minecraft/class_640.method_35757()Z */
    public static boolean method_35757(net.minecraft.class_640 self) {
        return self.method_45742();
    }

    /** was net/minecraft/class_742.method_3117()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3117(net.minecraft.class_742 self) {
        return self.method_52814().comp_1626();
    }

    /** was net/minecraft/class_7529.method_44406()V */
    public static void method_44406(net.minecraft.class_7529 self) {
        return;
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

    public void method_22919(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        ((net.minecraft.class_4588) (Object) this).method_22919(a0, a1, a2, a3, a4, 1.0f, a5, a6);
    }

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

    /** was net/minecraft/class_6777.method_47621(Lnet/minecraft/class_310;Lnet/minecraft/class_437;Lnet/minecraft/class_315;)Lnet/minecraft/class_6777; */
    public static net.minecraft.class_6777 method_47621(net.minecraft.class_6777 self, net.minecraft.class_310 a0, net.minecraft.class_437 a1, net.minecraft.class_315 a2) {
        return new net.minecraft.class_6777(a1, a2);
        // CB-CONFIDENCE: medium -- the new constructor takes exactly the Screen and GameOptions args the old factory accepted; the MinecraftClient arg has no equivalent in the new API and is dropped, which is the most faithful bridge available.
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

    /** was net/minecraft/class_1060.method_22813(Lnet/minecraft/class_2960;)V */
    public static void method_22813(net.minecraft.class_1060 self, net.minecraft.class_2960 a0) {
        self.method_4615(a0);
    }

    /** was net/minecraft/class_284.method_22097(ILjava/lang/CharSequence;)I */
    public static int method_22097(net.minecraft.class_284 self, int a0, java.lang.CharSequence a1) {
        return self.method_22096(a0, a1);
        // CB-CONFIDENCE: high -- method_22097(int, CharSequence)->int was renamed to method_22096 with identical signature on the same class; exact 1:1 delegation.
    }

    /** was net/minecraft/class_284.method_34419(IILjava/lang/CharSequence;)V */
    public static void method_34419(net.minecraft.class_284 self, int a0, int a1, java.lang.CharSequence a2) {
        self.method_22096(a0, a2);
    }

    /** was net/minecraft/class_315.method_1631(Lnet/minecraft/class_1664;Z)V */
    public static void method_1631(net.minecraft.class_315 self, net.minecraft.class_1664 a0, boolean a1) {
        self.method_1635(a0, a1);
    }

    /** was net/minecraft/class_3879.method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V */
    public static void method_2828(net.minecraft.class_3879 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3, int a4) {
        self.method_62100(a0, a1, a2, a3, a4);
    }

    /** was net/minecraft/class_507.method_2600(Lnet/minecraft/class_1735;)V */
    public static void method_2600(net.minecraft.class_507 self, net.minecraft.class_1735 a0) {
        self.method_62044(a0);
    }

    /** was net/minecraft/class_513.method_2630(Lnet/minecraft/class_507;)V */
    public static void method_2630(net.minecraft.class_513 self, net.minecraft.class_507 a0) {
        self.method_2638();
    }

    /** was net/minecraft/class_516.method_2657()Z */
    public static boolean method_2657(net.minecraft.class_516 self) {
        return self.method_2650().isEmpty();
    }

    /** was net/minecraft/class_537.method_2784(Lnet/minecraft/class_332;FI)V */
    public static void method_2784(net.minecraft.class_537 self, net.minecraft.class_332 a0, float a1, int a2) {
        self.method_2784(a0, a1, (float) a2);
    }

    /** was net/minecraft/class_5601.method_35743()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_35743(net.minecraft.class_5601 self) {
        return self.comp_2995();
    }

    /** was net/minecraft/class_5601.method_35744()Ljava/lang/String; */
    public static java.lang.String method_35744(net.minecraft.class_5601 self) {
        return self.comp_2996();
    }

    /** was net/minecraft/class_5684.method_32666(Lnet/minecraft/class_327;IILnet/minecraft/class_332;)V */
    public static void method_32666(net.minecraft.class_5684 self, net.minecraft.class_327 a0, int a1, int a2, net.minecraft.class_332 a3) {
        self.method_32666(a0, a1, a2, -1, 15728880, a3);
    }

    /** was net/minecraft/class_5944.field_29482Lnet/minecraft/class_284; */
    public static net.minecraft.class_284 field_29482() {
        return null;
    }

    /** was net/minecraft/class_5944.field_29483Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_29483() {
        return org.slf4j.LoggerFactory.getLogger(net.minecraft.class_5944.class);
    }

    /** was net/minecraft/class_5944.method_34583(Ljava/lang/String;Ljava/lang/Object;)V */
    public static void method_34583(net.minecraft.class_5944 self, java.lang.String a0, java.lang.Object a1) {
        if (a1 instanceof java.lang.Integer) {
            self.method_62899(a0, ((java.lang.Integer) a1).intValue());
        } else {
            throw new UnsupportedOperationException("CenturyBridge: method_34583(String, Object) was deleted; method_62899(String, int) is the closest equivalent but only accepts Integer");
        }
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

    /** was net/minecraft/class_757.method_3207()V */
    public static void method_3207(net.minecraft.class_757 self) {
        self.method_35768(true);
    }

    /** was net/minecraft/class_757.method_34538(Lnet/minecraft/class_5912;)V */
    public static void method_34538(net.minecraft.class_757 self, net.minecraft.class_5912 a0) {
        self.method_34521(a0);
    }

    /** was net/minecraft/class_758.method_3210(Lnet/minecraft/class_4184;FLnet/minecraft/class_638;IF)V */
    public static void method_3210(net.minecraft.class_758 self, net.minecraft.class_4184 a0, float a1, net.minecraft.class_638 a2, int a3, float a4) {
        self.method_62185(a0, a1, a2, a3, a4);
    }

    /** was net/minecraft/class_7754.method_45715()Lcom/google/common/collect/ImmutableList; */
    public static com.google.common.collect.ImmutableList method_45715(net.minecraft.class_7754 self) {
        return com.google.common.collect.ImmutableList.of(self.method_45714(), self.method_62112());
    }

    /** was net/minecraft/class_790$class_6247.field_32795Lnet/minecraft/class_790; */
    public static net.minecraft.class_790 field_32795() {
        return null;
    }

    /** was net/minecraft/class_790.method_3422()Z */
    public static boolean method_3422(net.minecraft.class_790 self) {
        return true;
    }

    /** was net/minecraft/class_790.method_3423()Ljava/util/Map; */
    public static java.util.Map method_3423(net.minecraft.class_790 self) {
        java.util.Map map = new java.util.HashMap();
        for (Object key : self.method_35791()) {
            map.put(key, self.method_35792(key.toString()));
        }
        return map;
    }

    /** was net/minecraft/class_790.method_35790(Ljava/lang/String;)Z */
    public static boolean method_35790(net.minecraft.class_790 self, java.lang.String a0) {
        return self.method_35791().contains(a0);
    }

    /** was net/minecraft/class_807.method_3497()Ljava/util/List; */
    public static java.util.List method_3497(net.minecraft.class_807 self) {
        return self.comp_3049();
    }

    /** was net/minecraft/class_894.method_3911(Lnet/minecraft/class_1560;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3911(net.minecraft.class_894 self, net.minecraft.class_1560 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        self.method_62422(a0, self.method_62421(), a1);
    }

    /** was net/minecraft/class_898.method_3954(Lnet/minecraft/class_1297;DDDFFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3954(net.minecraft.class_898 self, net.minecraft.class_1297 a0, double a1, double a2, double a3, float a4, float a5, net.minecraft.class_4587 a6, net.minecraft.class_4597 a7, int a8) {
        self.method_62424(a0, a1, a2, a3, a5, a6, a7, a8);
    }

    /** was net/minecraft/class_905.method_3973(Lnet/minecraft/class_1571;Lnet/minecraft/class_4587;F)V */
    public static void method_3973(net.minecraft.class_905 self, net.minecraft.class_1571 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62448(a0, self.method_62449(), a2);
    }

    /** was net/minecraft/class_908.method_3980(Lnet/minecraft/class_1570;Lnet/minecraft/class_4587;F)V */
    public static void method_3980(net.minecraft.class_908 self, net.minecraft.class_1570 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62450(a0, self.method_62451(), a2);
    }

    /** was net/minecraft/class_918.method_29711(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;ZZ)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_29711(net.minecraft.class_918 self, net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, boolean a2, boolean a3) {
        return self.method_23181(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_951.method_4127(Lnet/minecraft/class_1477;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4127(net.minecraft.class_951 self, net.minecraft.class_1477 a0) {
        return self.method_33431(self.method_62542());
    }

    /** was net/minecraft/class_473.<init>(Lnet/minecraft/class_1657;Lnet/minecraft/class_1799;Lnet/minecraft/class_1268;)V */
    public static net.minecraft.class_473 cbNewclass_473(net.minecraft.class_1657 a0, net.minecraft.class_1799 a1, net.minecraft.class_1268 a2) {
        return new net.minecraft.class_473(a0, a1, a2, net.minecraft.class_9301.field_49369);
    }

    /** was net/minecraft/class_514.<init>()V */
    public static net.minecraft.class_514 cbNewclass_514() {
        return new net.minecraft.class_514(() -> 0);
    }

    /** was net/minecraft/class_516.<init>(Lnet/minecraft/class_5455;Ljava/util/List;)V */
    public static net.minecraft.class_516 cbNewclass_516(net.minecraft.class_5455 a0, java.util.List a1) {
        return new net.minecraft.class_516(a1);
        // CB-CONFIDENCE: high -- the new constructor takes exactly the List argument; the class_5455 parameter is simply dropped in 1.21.2
    }

    /** was net/minecraft/class_542$class_545.<init>(ZLjava/lang/String;Ljava/lang/String;ZZ)V */
    public static net.minecraft.class_542.class_545 cbNewclass_542class_545(boolean a0, java.lang.String a1, java.lang.String a2, boolean a3, boolean a4) {
        return new net.minecraft.class_542.class_545(a0, a1, a2, a3, a4, false);
    }

    /** was net/minecraft/class_5789.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_610;)V */
    public static net.minecraft.class_5789 cbNewclass_5789(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_610 a1) {
        return new net.minecraft.class_5789(a0, a1, a1);
    }

    /** was net/minecraft/class_630$class_593.field_3502[Lnet/minecraft/class_630$class_618; */
    public static net.minecraft.class_630.class_618[] field_3502() {
        return new net.minecraft.class_630.class_618[0];
    }

    /** was net/minecraft/class_6367.<init>(IIZZ)V */
    public static net.minecraft.class_6367 cbNewclass_6367(int a0, int a1, boolean a2, boolean a3) {
        return new net.minecraft.class_6367(a0, a1, a2);
    }

    /** was net/minecraft/class_638.<init>(Lnet/minecraft/class_634;Lnet/minecraft/class_638$class_5271;Lnet/minecraft/class_5321;Lnet/minecraft/class_6880;IILjava/util/function/Supplier;Lnet/minecraft/class_761;ZJ)V */
    public static net.minecraft.class_638 cbNewclass_638(net.minecraft.class_634 a0, net.minecraft.class_638.class_5271 a1, net.minecraft.class_5321 a2, net.minecraft.class_6880 a3, int a4, int a5, java.util.function.Supplier a6, net.minecraft.class_761 a7, boolean a8, long a9) {
        return new net.minecraft.class_638(a0, a1, a2, a3, a4, a5, a7, a8, a9, a5);
    }

    /** was net/minecraft/class_7193.<init>(Lnet/minecraft/class_5285;Lnet/minecraft/class_7723;Lnet/minecraft/class_7780;Lnet/minecraft/class_5350;Lnet/minecraft/class_7712;)V */
    public static net.minecraft.class_7193 cbNewclass_7193(net.minecraft.class_5285 a0, net.minecraft.class_7723 a1, net.minecraft.class_7780 a2, net.minecraft.class_5350 a3, net.minecraft.class_7712 a4) {
        return new net.minecraft.class_7193(new net.minecraft.class_7726(a0, a1), a2, a3, a4);
    }

    /** was net/minecraft/class_7551.<init>(Lnet/minecraft/class_437;Lnet/minecraft/class_7573;Ljava/util/function/Consumer;)V */
    public static net.minecraft.class_7551 cbNewclass_7551(net.minecraft.class_437 a0, net.minecraft.class_7573 a1, java.util.function.Consumer a2) {
        return new net.minecraft.class_7551(a0, a1, net.minecraft.class_8776.field_46064, a2);
    }

    /** was net/minecraft/class_7763.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_7763 cbNewclass_7763(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1) {
        return new net.minecraft.class_7763(a0);
    }

    /** was net/minecraft/class_777.<init>([IILnet/minecraft/class_2350;Lnet/minecraft/class_1058;Z)V */
    public static net.minecraft.class_777 cbNewclass_777(int[] a0, int a1, net.minecraft.class_2350 a2, net.minecraft.class_1058 a3, boolean a4) {
        return new net.minecraft.class_777(a0, a1, a2, a3, a4, 0);
    }

    /** was net/minecraft/class_785.<init>(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Ljava/util/Map;Lnet/minecraft/class_789;Z)V */
    public static net.minecraft.class_785 cbNewclass_785(org.joml.Vector3f a0, org.joml.Vector3f a1, java.util.Map a2, net.minecraft.class_789 a3, boolean a4) {
        return new net.minecraft.class_785(a0, a1, a2, a3, a4, 0);
    }

    /** was net/minecraft/class_790.<init>(Ljava/util/List;)V */
    public static net.minecraft.class_790 cbNewclass_790(java.util.List a0) {
        net.minecraft.class_816.class_9982 tree = new net.minecraft.class_816.class_9982(a0);
        java.util.Map map = new java.util.HashMap();
        for (Object key : tree.method_62338()) {
            net.minecraft.class_2689 id = (net.minecraft.class_2689) key;
            map.put(id, tree.method_62339(id));
        }
        return new net.minecraft.class_790(map, tree);
    }

    /** was net/minecraft/class_8690$class_8691.<init>(IILnet/minecraft/class_8690$class_8691$class_8692;)V */
    public static net.minecraft.class_8690.class_8691 cbNewclass_8690class_8691(int a0, int a1, net.minecraft.class_8690.class_8691.class_8692 a2) {
        return new net.minecraft.class_8690.class_8691(a0, a1, a2, false);
    }

    /** was net/minecraft/class_915.method_23175(Lnet/minecraft/class_1533;Lnet/minecraft/class_2561;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IF)V */
    public static void method_23175(net.minecraft.class_915 self, net.minecraft.class_1533 a0, net.minecraft.class_2561 a1, net.minecraft.class_4587 a2, net.minecraft.class_4597 a3, int a4, float a5) {
        self.method_62473(a0, self.method_62471(), a5);
    }

    /** was net/minecraft/class_921.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_921 cbNewclass_921(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1) {
        return new net.minecraft.class_921(a0, a1, null);
    }

    /** was net/minecraft/class_922.method_4045(Lnet/minecraft/class_1309;F)F */
    public static float method_4045(net.minecraft.class_922 self, net.minecraft.class_1309 a0, float a1) {
        return 0.0F;
    }

    /** was net/minecraft/class_929.method_4079(Lnet/minecraft/class_1451;Lnet/minecraft/class_4587;F)V */
    public static void method_4079(net.minecraft.class_929 self, net.minecraft.class_1451 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62395(a0, self.method_62394(), a2);
    }

    /** was net/minecraft/class_946.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_946 cbNewclass_946(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_5601 a3) {
        return new net.minecraft.class_946(a0);
    }

    /** was net/minecraft/class_946.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_606;)V */
    public static net.minecraft.class_946 cbNewclass_946(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_606 a3) {
        return new net.minecraft.class_946(a0);
    }

    /** was net/minecraft/class_951.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_610;)V */
    public static net.minecraft.class_951 cbNewclass_951(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_610 a1) {
        return new net.minecraft.class_951(a0, a1, a1);
    }

    /** was net/minecraft/class_955.method_4134(Lnet/minecraft/class_1685;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4134(net.minecraft.class_955 self, net.minecraft.class_1685 a0) {
        return self.field_4796;
    }

    /** was net/minecraft/class_961.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_961 cbNewclass_961(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1) {
        return new net.minecraft.class_961(a0, a1, a1, false);
    }

    /** was net/minecraft/class_965.method_4157(Lnet/minecraft/class_1640;Lnet/minecraft/class_4587;F)V */
    public static void method_4157(net.minecraft.class_965 self, net.minecraft.class_1640 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62574(a0, self.method_62575(), a2);
        // CB-CONFIDENCE: medium -- method_62574 is the clear replacement for method_4157 (same Particle+float params), but the old MatrixStack (a1) is replaced by the manager's internal class_10082 context from method_62575; caller-provided transform is ignored
    }

    /** was net/minecraft/class_973.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_922;)V */
    public static net.minecraft.class_973 cbNewclass_973(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_922 a1) {
        return new net.minecraft.class_973(a1, a0);
    }

    /** was net/minecraft/class_979.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_5599;)V */
    public static net.minecraft.class_979 cbNewclass_979(net.minecraft.class_3883 a0, net.minecraft.class_5599 a1) {
        return new net.minecraft.class_979(a0, a1, new net.minecraft.class_10197(null, null));
    }

    /** was net/minecraft/class_9824$class_9825.comp_2872()Ljava/util/function/Supplier; */
    public static java.util.function.Supplier comp_2872(net.minecraft.class_9824.class_9825 self) {
        return () -> self.comp_3062();
    }

    /** was net/minecraft/class_276.method_22594(IIZ)V */
    public static void method_22594(net.minecraft.class_276 self, int a0, int a1, boolean a2) {
        if (a2) {
            self.method_1237(a0, a1);
            return;
        }
        throw new UnsupportedOperationException("CenturyBridge: 1.21.2 cannot draw a framebuffer while preserving the old disableBlend=false behavior");
    }

    /** was net/minecraft/class_382$class_328.<init>(FFFFFFFFF)V */
    public static net.minecraft.class_382.class_328 cbNewclass_382class_328(float a0, float a1, float a2, float a3, float a4, float a5, float a6, float a7, float a8) {
        return new net.minecraft.class_382.class_328(a0, a1, a2, a3, a4, ((int) (a8 * 255.0F) << 24) | ((int) (a5 * 255.0F) << 16) | ((int) (a6 * 255.0F) << 8) | (int) (a7 * 255.0F));
    }

    /** was net/minecraft/class_3886.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_3886 cbNewclass_3886(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_5601 a3) {
        return new net.minecraft.class_3886(a0, a1, a1, a2, a3, a2, a3);
    }

    /** was net/minecraft/class_4842.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Z)V */
    public static net.minecraft.class_4842 cbNewclass_4842(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_5601 a3, boolean a4) {
        return new net.minecraft.class_4842(a0, a1, a2, a3, a1, a2, a3);
    }

    /** was net/minecraft/class_516.method_2648(Z)Ljava/util/List; */
    public static java.util.List method_2648(net.minecraft.class_516 self, boolean a0) {
        return self.method_64885(a0 ? net.minecraft.class_516.class_9937.field_52848 : net.minecraft.class_516.class_9937.field_52849);
    }

    /** was net/minecraft/class_10042.field_53469Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53469() {
        return null;
    }

    /** was net/minecraft/class_1005.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_1005 cbNewclass_1005(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_1005(a0);
    }

    /** was net/minecraft/class_10097.method_62637()Ljava/util/Map; */
    public static java.util.Map method_62637(net.minecraft.class_10097 self) {
        return self.method_62639();
    }

    /** was net/minecraft/class_1059.method_24198(Lnet/minecraft/class_7766$class_7767;)V */
    public static void method_24198(net.minecraft.class_1059 self, net.minecraft.class_7766.class_7767 a0) {
        self.method_45848(a0);
    }

    /** was net/minecraft/class_1079.field_21768Lnet/minecraft/class_1079; */
    public static net.minecraft.class_1079 field_21768() {
        return new net.minecraft.class_1079(java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(), 1, false);
    }

    /** was net/minecraft/class_1079.method_4684()I */
    public static int method_4684(net.minecraft.class_1079 self) {
        return self.comp_3456();
    }

    /** was net/minecraft/class_1079.method_4685()Z */
    public static boolean method_4685(net.minecraft.class_1079 self) {
        return self.comp_3457();
    }

    /** was net/minecraft/class_1080.<init>(II)V */
    public static net.minecraft.class_1080 cbNewclass_1080(int a0, int a1) {
        return new net.minecraft.class_1080(a0, a1 == -1 ? java.util.Optional.empty() : java.util.Optional.of(a1));
    }

    /** was net/minecraft/class_1080.method_4690()I */
    public static int method_4690(net.minecraft.class_1080 self) {
        return self.comp_3451();
    }

    /** was net/minecraft/class_1084.method_4696()Z */
    public static boolean method_4696(net.minecraft.class_1084 self) {
        return self.comp_3459();
    }

    /** was net/minecraft/class_1084.method_4697()Z */
    public static boolean method_4697(net.minecraft.class_1084 self) {
        return self.comp_3460();
    }

    /** was net/minecraft/class_1100.method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 method_4753(net.minecraft.class_1100 self, net.minecraft.class_7775 a0, java.util.function.Function a1, net.minecraft.class_3665 a2) {
        return net.minecraft.class_1100.method_65765(self, a0, a2);
    }

    /** was net/minecraft/class_339.method_25361(DD)Z */
    public static boolean method_25361(net.minecraft.class_339 self, double a0, double a1) {
        return self.method_25405(a0, a1);
    }

    /** was net/minecraft/class_382$class_10364.<init>(FFILnet/minecraft/class_382;Lnet/minecraft/class_2583;F)V */
    public static net.minecraft.class_382.class_10364 cbNewclass_382class_10364(float a0, float a1, int a2, net.minecraft.class_382 a3, net.minecraft.class_2583 a4, float a5) {
        return new net.minecraft.class_382.class_10364(a0, a1, a2, 0, a3, a4, a5, 0.0F);
    }

    /** was net/minecraft/class_3888$class_3889.method_17168()Ljava/lang/String; */
    public static java.lang.String method_17168(net.minecraft.class_3888.class_3889 self) {
        return self.method_15434();
    }

    /** was net/minecraft/class_3888$class_3889.method_17170(Ljava/lang/String;)Lnet/minecraft/class_3888$class_3889; */
    public static net.minecraft.class_3888.class_3889 method_17170(net.minecraft.class_3888.class_3889 self, java.lang.String a0) {
        for (net.minecraft.class_3888.class_3889 value : net.minecraft.class_3888.class_3889.values()) {
            if (value.method_15434().equals(a0)) {
                return value;
            }
        }
        return null;
    }

    /** was net/minecraft/class_3888.method_17167()Lnet/minecraft/class_3888$class_3889; */
    public static net.minecraft.class_3888.class_3889 method_17167(net.minecraft.class_3888 self) {
        return self.comp_3458();
    }

    /** was net/minecraft/class_4004.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_4004 cbNewclass_4004(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_4004(a0);
    }

    /** was net/minecraft/class_4043.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_4043 cbNewclass_4043(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_4043(a0);
    }

    /** was net/minecraft/class_5599.<init>()V */
    public static net.minecraft.class_5599 cbNewclass_5599() {
        return net.minecraft.class_5599.method_65522();
    }

    /** was net/minecraft/class_582.field_52910Lnet/minecraft/class_9953; */
    public static net.minecraft.class_9953 field_52910() {
        return net.minecraft.class_582.field_52909;
    }

    /** was net/minecraft/class_693.<init>(Lnet/minecraft/class_898;Lnet/minecraft/class_4599;Lnet/minecraft/class_638;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;)V */
    public static net.minecraft.class_693 cbNewclass_693(net.minecraft.class_898 a0, net.minecraft.class_4599 a1, net.minecraft.class_638 a2, net.minecraft.class_1297 a3, net.minecraft.class_1297 a4) {
        return new net.minecraft.class_693(a0, a2, a3, a4);
    }

    /** was net/minecraft/class_7288.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_2960;Lnet/minecraft/class_7288$class_7289;Lnet/minecraft/class_7288$class_7311;Ljava/util/function/Function;)V */
    public static net.minecraft.class_7288 cbNewclass_7288(net.minecraft.class_3883 a0, net.minecraft.class_2960 a1, net.minecraft.class_7288.class_7289 a2, net.minecraft.class_7288.class_7311 a3, java.util.function.Function a4) {
        return new net.minecraft.class_7288(a0, a1, a2, a3, a4, false);
    }

    /** was net/minecraft/class_751.method_18143(Lnet/minecraft/class_1060;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_18143(net.minecraft.class_751 self, net.minecraft.class_1060 a0, java.util.concurrent.Executor a1) {
        self.method_65815(a0);
        return java.util.concurrent.CompletableFuture.completedFuture(null);
    }

    /** was net/minecraft/class_7528.method_44381()I */
    public static int method_44381(net.minecraft.class_7528 self) {
        return 4;
    }

    /** was net/minecraft/class_7528.method_44383(II)Z */
    public static boolean method_44383(net.minecraft.class_7528 self, int a0, int a1) {
        double scrollY = self.method_44387();
        return (double) a1 - scrollY >= 0.0 && (double) a0 - scrollY <= (double) self.method_25364();
    }

    /** was net/minecraft/class_7528.method_53532()I */
    public static int method_53532(net.minecraft.class_7528 self) {
        return 6;
    }

    /** was net/minecraft/class_793.method_3431()Lnet/minecraft/class_793; */
    public static net.minecraft.class_793 method_3431(net.minecraft.class_793 self) {
        net.minecraft.class_793 current = self;
        while (current.method_65538() instanceof net.minecraft.class_793) {
            current = (net.minecraft.class_793) current.method_65538();
        }
        return current;
    }

    /** was net/minecraft/class_793.method_3439(Ljava/lang/String;)Z */
    public static boolean method_3439(net.minecraft.class_793 self, java.lang.String a0) {
        return a0.charAt(0) == '#';
    }

    /** was net/minecraft/class_793.method_3443()Lnet/minecraft/class_809; */
    public static net.minecraft.class_809 method_3443(net.minecraft.class_793 self) {
        return self.method_65540();
    }

    /** was net/minecraft/class_793.method_3444()Z */
    public static boolean method_3444(net.minecraft.class_793 self) {
        java.lang.Boolean ambientOcclusion = self.method_65537();
        return ambientOcclusion == null || ambientOcclusion.booleanValue();
    }

    /** was net/minecraft/class_809.<init>(Lnet/minecraft/class_809;)V */
    public static net.minecraft.class_809 cbNewclass_809(net.minecraft.class_809 a0) {
        return new net.minecraft.class_809(a0.comp_3368(), a0.comp_3369(), a0.comp_3370(), a0.comp_3371(), a0.comp_3372(), a0.comp_3373(), a0.comp_3374(), a0.comp_3375());
    }

    /** was net/minecraft/class_809.field_4300Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4300() {
        return net.minecraft.class_809.field_4301.comp_3368();
    }

    /** was net/minecraft/class_809.field_4302Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4302() {
        return net.minecraft.class_804.field_4284;
    }

    /** was net/minecraft/class_809.field_4303Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4303() {
        return net.minecraft.class_809.field_4301.comp_3374();
    }

    /** was net/minecraft/class_809.field_4304Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4304() {
        return net.minecraft.class_809.field_4301.comp_3368();
    }

    /** was net/minecraft/class_809.field_4306Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4306() {
        return net.minecraft.class_809.field_4301.comp_3375();
    }

    /** was net/minecraft/class_809.field_4307Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4307() {
        return net.minecraft.class_809.field_4301.comp_3369();
    }

    /** was net/minecraft/class_809.field_4311Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4311() {
        return net.minecraft.class_804.field_4284;
    }

    /** was net/minecraft/class_809.method_3501(Lnet/minecraft/class_811;)Z */
    public static boolean method_3501(net.minecraft.class_809 self, net.minecraft.class_811 a0) {
        return !self.method_3503(a0).equals(net.minecraft.class_809.field_4301.method_3503(a0));
    }

    /** was net/minecraft/class_813.method_3510()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3510(net.minecraft.class_813 self) {
        return self.comp_3379();
    }

    /** was net/minecraft/class_813.method_3511()I */
    public static int method_3511(net.minecraft.class_813 self) {
        return self.comp_3382();
    }

    /** was net/minecraft/class_824.method_23077(Lnet/minecraft/class_2586;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)Z */
    public static boolean method_23077(net.minecraft.class_824 self, net.minecraft.class_2586 a0, net.minecraft.class_4587 a1, net.minecraft.class_4597 a2, int a3, int a4) {
        net.minecraft.class_827 renderer = self.method_3550(a0);
        if (renderer == null) {
            return false;
        }
        renderer.method_3569(a0, 0.0F, a1, a2, a3, a4);
        return true;
    }

    /** was net/minecraft/class_916.method_56856(Lnet/minecraft/class_1799;)I */
    public static int method_56856(net.minecraft.class_916 self, net.minecraft.class_1799 a0) {
        return a0.method_7960() ? 187 : net.minecraft.class_1792.method_7880(a0.method_7909()) + a0.method_7919();
    }

    /** was net/minecraft/class_918.field_40532Lnet/minecraft/class_1091; */
    public static net.minecraft.class_1091 field_40532() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60655("minecraft", "trident"), "inventory");
    }

    /** was net/minecraft/class_918.field_40533Lnet/minecraft/class_1091; */
    public static net.minecraft.class_1091 field_40533() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60656("spyglass"), "inventory");
    }

    /** was net/minecraft/class_963.method_4149(Lnet/minecraft/class_10080;Lnet/minecraft/class_4587;)V */
    public static void method_4149(net.minecraft.class_963 self, net.minecraft.class_10080 a0, net.minecraft.class_4587 a1) {
        a1.method_22905(0.9375F, 0.9375F, 0.9375F);
    }

    /** was net/minecraft/class_977.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_977 cbNewclass_977(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_977(a0);
    }

    /** was net/minecraft/class_9824.<init>(Lnet/minecraft/class_1100;)V */
    public static net.minecraft.class_9824 cbNewclass_9824(net.minecraft.class_1100 a0) {
        return new net.minecraft.class_9824();
    }

    /** was net/minecraft/class_990.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_990 cbNewclass_990(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_990(a0);
    }

    /** was net/minecraft/class_996.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_776;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_996 cbNewclass_996(net.minecraft.class_3883 a0, net.minecraft.class_776 a1, net.minecraft.class_918 a2) {
        return new net.minecraft.class_996(a0, a1);
    }

    /** was net/minecraft/class_9991.<init>(Lnet/minecraft/class_5617$class_5618;FLnet/minecraft/class_5601;Lnet/minecraft/class_5601;Z)V */
    public static net.minecraft.class_9991 cbNewclass_9991(net.minecraft.class_5617.class_5618 a0, float a1, net.minecraft.class_5601 a2, net.minecraft.class_5601 a3, boolean a4) {
        return new net.minecraft.class_9991(a0, a2, a3, a4);
    }

    /** was net/minecraft/class_1921.method_27948()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_27948(net.minecraft.class_1921 self) {
        return net.minecraft.class_1921.method_27949();
    }

    /** was net/minecraft/class_290.field_1579Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1579() {
        return net.minecraft.class_296.field_52113;
    }

    /** was net/minecraft/class_310.method_22107()Z */
    public static boolean method_22107(net.minecraft.class_310 self) {
        return false;
    }

    /** was net/minecraft/class_340.method_53535(J)V */
    public static void method_53535(net.minecraft.class_340 self, long a0) {
        self.method_53534(a0);
    }

    /** was net/minecraft/class_4069.method_20086(Lnet/minecraft/class_364;)V */
    public static void method_20086(net.minecraft.class_4069 self, net.minecraft.class_364 a0) {
        self.method_25395(a0);
    }

    /** was net/minecraft/class_437.method_40041([Lnet/minecraft/class_339;)V */
    public static void method_40041(net.minecraft.class_437 self, net.minecraft.class_339[] a0) {
        for (net.minecraft.class_339 widget : a0) {
            widget.field_22763 = false;
        }
    }

    /** was net/minecraft/class_4588.method_1344()V */
    public static void method_1344(net.minecraft.class_4588 self) {
        return;
    }

    /** was net/minecraft/class_4588.method_22912(DDD)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22912(net.minecraft.class_4588 self, double a0, double a1, double a2) {
        return self.method_22912((float) a0, (float) a1, (float) a2);
    }

    /** was net/minecraft/class_4588.method_22919(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;FFFII)V */
    public static void method_22919(net.minecraft.class_4588 self, net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        self.method_22919(a0, a1, a2, a3, a4, 1.0F, a5, a6);
    }

    /** was net/minecraft/class_4588.method_22920(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;[FFFF[IIZ)V */
    public static void method_22920(net.minecraft.class_4588 self, net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float[] a2, float a3, float a4, float a5, int[] a6, int a7, boolean a8) {
        self.method_22920(a0, a1, a2, a3, a4, a5, 1.0F, a6, a7, a8);
    }

    /** was net/minecraft/class_4588.method_23919(FFFFFFFFFIIFFF)V */
    public static void method_23919(net.minecraft.class_4588 self, float a0, float a1, float a2, float a3, float a4, float a5, float a6, float a7, float a8, int a9, int a10, float a11, float a12, float a13) {
        self.method_23919(a0, a1, a2, ((int) (a6 * 255.0F) << 24) | ((int) (a3 * 255.0F) << 16) | ((int) (a4 * 255.0F) << 8) | (int) (a5 * 255.0F), a7, a8, a9, a10, a11, a12, a13);
    }

    /** was net/minecraft/class_4588.method_35666()V */
    public static void method_35666(net.minecraft.class_4588 self) {
        return;
    }

    /** was net/minecraft/class_4722.field_28252Ljava/util/Map; */
    public static java.util.Map field_28252() {
        return net.minecraft.class_4722.field_21712;
    }

    /** was net/minecraft/class_636.method_2926()Z */
    public static boolean method_2926(net.minecraft.class_636 self) {
        return self.method_2914();
    }

    /** was net/minecraft/class_640.method_2968()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_2968(net.minecraft.class_640 self) {
        return self.method_52810().comp_1626();
    }

    /** was net/minecraft/class_7065.method_44068()I */
    public static int method_44068(net.minecraft.class_7065 self) {
        return 9;
    }

    /** was net/minecraft/class_7956.field_41407Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41407() {
        return net.minecraft.class_7956.field_41407.codec();
    }

    /** was net/minecraft/class_7957.field_41409Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41409() {
        return net.minecraft.class_7957.field_41409.codec();
    }

    /** was net/minecraft/class_8066.field_42077Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42077() {
        return net.minecraft.class_8066.field_42077.codec();
    }

    /** was net/minecraft/class_8690$class_8691.field_45650Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45650() {
        return net.minecraft.class_8690.class_8691.field_45650.codec();
    }

    /** was net/minecraft/class_8690$class_8693.field_45654Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45654() {
        return net.minecraft.class_8690.class_8693.field_45654.codec();
    }

    /** was net/minecraft/class_8973.method_55236()Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 method_55236(net.minecraft.class_8973 self) {
        return self.method_63512();
    }

    /** was net/minecraft/class_5602.method_32078(Lnet/minecraft/class_4719;)Lnet/minecraft/class_5601; */
    public static net.minecraft.class_5601 method_32078(net.minecraft.class_5602 self, net.minecraft.class_4719 a0) {
        return self.method_62130(a0);
    }

    /** was net/minecraft/class_884.method_3895(Lnet/minecraft/class_1430;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3895(net.minecraft.class_884 self, net.minecraft.class_1430 a0) {
        net.minecraft.class_10042 state = self.method_62399();
        self.method_62400(a0, state, 0.0F);
        return self.method_3885(state);
    }

    /** was net/minecraft/class_918.field_40531Lnet/minecraft/class_1091; */
    public static net.minecraft.class_1091 field_40531() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60656("spyglass_in_hand"), "inventory");
    }

    /** was net/minecraft/class_922.method_4039(Lnet/minecraft/class_1309;)F */
    public static float method_4039(net.minecraft.class_922 self, net.minecraft.class_1309 a0) {
        return 90.0F;
    }

    /** was net/minecraft/class_922.method_4044(Lnet/minecraft/class_1309;F)F */
    public static float method_4044(net.minecraft.class_922 self, net.minecraft.class_1309 a0, float a1) {
        return (float) a0.field_6012 + a1;
    }

    /** was net/minecraft/class_942.method_4108(Lnet/minecraft/class_1614;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4108(net.minecraft.class_942 self, net.minecraft.class_1614 a0) {
        return self.method_3885(self.method_62528());
    }

    /** was net/minecraft/class_946.method_4119(Lnet/minecraft/class_1547;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4119(net.minecraft.class_946 self, net.minecraft.class_1547 a0) {
        return self.method_62529(null);
    }

    /** was net/minecraft/class_948.method_4122(Lnet/minecraft/class_1473;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4122(net.minecraft.class_948 self, net.minecraft.class_1473 a0) {
        net.minecraft.class_10429 state = self.method_62537();
        self.method_62536(a0, state, 0.0F);
        return self.method_65571(state);
    }

    /** was net/minecraft/class_949.method_4123(Lnet/minecraft/class_1628;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4123(net.minecraft.class_949 self, net.minecraft.class_1628 a0) {
        net.minecraft.class_10042 state = self.method_62540();
        self.method_62539(a0, state, 0.0F);
        return self.method_3885(state);
    }

    /** was net/minecraft/class_972.<init>(Lnet/minecraft/class_3883;)V */
    public static net.minecraft.class_972 cbNewclass_972(net.minecraft.class_3883 a0) {
        return new net.minecraft.class_972(a0, net.minecraft.class_5599.method_65522(), new net.minecraft.class_10201());
    }

    /** was net/minecraft/class_977.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_977 cbNewclass_977(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        return new net.minecraft.class_977(a0);
    }

    /** was net/minecraft/class_978.<init>(Lnet/minecraft/class_3883;)V */
    public static net.minecraft.class_978 cbNewclass_978(net.minecraft.class_3883 a0) {
        return new net.minecraft.class_978(a0, net.minecraft.class_5599.field_55267);
    }

    /** was net/minecraft/class_990.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_990 cbNewclass_990(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        return new net.minecraft.class_990(a0);
    }

    /** was net/minecraft/class_310.method_1534()F */
    public static float method_1534(net.minecraft.class_310 self) {
        return self.method_61966().method_60637(false);
    }

    /** was net/minecraft/class_7528.method_44388(DD)Z */
    public static boolean method_44388(net.minecraft.class_7528 self, double a0, double a1) {
        return self.method_25405(a0, a1);
    }

    /** was net/minecraft/class_8973.method_55237()Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 method_55237(net.minecraft.class_8973 self) {
        return self.method_55819();
    }

    /** was net/minecraft/class_9824.<init>(Ljava/util/Map;Lnet/minecraft/class_3695;Lnet/minecraft/class_1100;Lnet/minecraft/class_324;Ljava/util/function/BiConsumer;)V */
    public static net.minecraft.class_9824 cbNewclass_9824(java.util.Map a0, net.minecraft.class_3695 a1, net.minecraft.class_1100 a2, net.minecraft.class_324 a3, java.util.function.BiConsumer a4) {
        return new net.minecraft.class_9824();
    }

    /** was net/minecraft/class_1079.<init>(Ljava/util/List;IIIZ)V */
    public static net.minecraft.class_1079 cbNewclass_1079(java.util.List a0, int a1, int a2, int a3, boolean a4) {
        return new net.minecraft.class_1079(java.util.Optional.of(a0), a1 == -1 ? java.util.Optional.empty() : java.util.Optional.of(a1), a2 == -1 ? java.util.Optional.empty() : java.util.Optional.of(a2), a3, a4);
    }

    /**
     * superRedirects: a 1.20.1 override's super.render(context, mx, my, delta).
     * 1.20.1 Screen.render drew the widget list and nothing else; since 1.20.2
     * it draws the background first, over everything the mod just drew. Render
     * exactly what the old contract promised.
     */
    public static void cbRenderWidgets(net.minecraft.class_437 self, net.minecraft.class_332 context, int mouseX, int mouseY, float delta) {
        for (net.minecraft.class_4068 d : ((top.dext.centurybridge.shims.v1_21_4.ScreenDrawablesAccessor) self).cbDrawables()) {
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

    /** was net/minecraft/class_4668$class_4683.<init>(Lnet/minecraft/class_2960;ZZ)V -- RenderPhase.Texture: 1.21.2 made blur a TriState */
    public static net.minecraft.class_4668.class_4683 cbNewclass_4668class_4683(net.minecraft.class_2960 id, boolean blur, boolean mipmap) {
        return new net.minecraft.class_4668.class_4683(id, blur ? net.minecraft.class_9851.field_52394 : net.minecraft.class_9851.field_52395, mipmap);
    }

    /** was GameRenderer.getBasicProjectionMatrix(double fov) -> float since 1.21.2 */
    public static org.joml.Matrix4f method_22973(net.minecraft.class_757 self, double fov) {
        return self.method_22973((float) fov);
    }

    /** was net/minecraft/class_308.method_1452(Lorg/joml/Matrix4f;)V */
    public static void method_1452(org.joml.Matrix4f a0) {
        net.minecraft.class_308.method_1452();
    }

    /** was net/minecraft/class_308.method_27869(Lorg/joml/Matrix4f;)V */
    public static void method_27869(org.joml.Matrix4f a0) {
        net.minecraft.class_308.method_27869();
    }

    /** was net/minecraft/class_425.method_18819(Lnet/minecraft/class_310;)V */
    public static void method_18819(net.minecraft.class_310 a0) {
        net.minecraft.class_425.method_18819(a0.method_1531());
    }

    /** was net/minecraft/class_4696.method_23678(Lnet/minecraft/class_1799;Z)Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23678(net.minecraft.class_1799 a0, boolean a1) {
        net.minecraft.class_4696.method_23682(a1);
        return net.minecraft.class_4696.method_23678(a0);
        // CB-CONFIDENCE: medium -- the removed boolean parameter maps to the method_23682(Z)V setter on the same class, inferring it sets the state the old boolean controlled
    }

    /** was net/minecraft/class_4696.method_23683(Lnet/minecraft/class_2680;Z)Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23683(net.minecraft.class_2680 a0, boolean a1) {
        net.minecraft.class_4696.method_23682(a1);
        return net.minecraft.class_4696.method_23683(a0);
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

    /** was net/minecraft/class_490.method_48472(Lnet/minecraft/class_332;FFILorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/class_1309;)V */
    public static void method_48472(net.minecraft.class_332 a0, float a1, float a2, int a3, org.joml.Vector3f a4, org.joml.Quaternionf a5, org.joml.Quaternionf a6, net.minecraft.class_1309 a7) {
        net.minecraft.class_490.method_48472(a0, a1, a2, (float) a3, a4, a5, a6, a7);
    }

    /** was net/minecraft/class_5602.method_45719(Lnet/minecraft/class_4719;)Lnet/minecraft/class_5601; */
    public static net.minecraft.class_5601 method_45719(net.minecraft.class_4719 a0) {
        return net.minecraft.class_5602.method_62130(a0);
    }

    /** was net/minecraft/class_596.method_32034()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32034() {
        return net.minecraft.class_596.method_32034(false);
    }

    /** was net/minecraft/class_7532.method_44445(Lnet/minecraft/class_332;Lnet/minecraft/class_2960;IIIZZ)V */
    public static void method_44445(net.minecraft.class_332 a0, net.minecraft.class_2960 a1, int a2, int a3, int a4, boolean a5, boolean a6) {
        net.minecraft.class_7532.method_44445(a0, a1, a2, a3, a4, a5, a6, 0);
    }

    /** was net/minecraft/class_775.method_29708(Lnet/minecraft/class_1920;Lnet/minecraft/class_2338;Lnet/minecraft/class_3610;Lnet/minecraft/class_2680;Lnet/minecraft/class_2350;Lnet/minecraft/class_3610;)Z */
    public static boolean method_29708(net.minecraft.class_1920 a0, net.minecraft.class_2338 a1, net.minecraft.class_3610 a2, net.minecraft.class_2680 a3, net.minecraft.class_2350 a4, net.minecraft.class_3610 a5) {
        return net.minecraft.class_775.method_29708(a2, a3, a4, a5);
    }

    /** was net/minecraft/class_7761.method_45795()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_45795() {
        return net.minecraft.class_7761.method_45795(net.minecraft.class_7761.class_10381.field_55158);
    }

    /** was net/minecraft/class_7766.method_47664(Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_47664(java.util.List a0, java.util.concurrent.Executor a1) {
        return net.minecraft.class_7766.method_47664(net.minecraft.class_8684.create(a0), a0, a1);
    }

    /** was net/minecraft/class_7973.method_47761(Ljava/util/List;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_47761(java.util.List a0) {
        return net.minecraft.class_7973.method_47761(a0).codec();
    }

    /** was net/minecraft/class_822.method_3545(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_2960;FFJII[FFF)V */
    public static void method_3545(net.minecraft.class_4587 a0, net.minecraft.class_4597 a1, net.minecraft.class_2960 a2, float a3, float a4, long a5, int a6, int a7, float[] a8, float a9, float a10) {
        net.minecraft.class_822.method_3545(a0, a1, a2, a3, a4, a5, a6, a7, 0xFF000000 | ((int) (a8[0] * 255.0F) << 16) | ((int) (a8[1] * 255.0F) << 8) | (int) (a8[2] * 255.0F), a9, a10);
    }

    /** was net/minecraft/class_823.method_23802(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_630;Lnet/minecraft/class_4730;ZLnet/minecraft/class_1767;Lnet/minecraft/class_9307;Z)V */
    public static void method_23802(net.minecraft.class_4587 a0, net.minecraft.class_4597 a1, int a2, int a3, net.minecraft.class_630 a4, net.minecraft.class_4730 a5, boolean a6, net.minecraft.class_1767 a7, net.minecraft.class_9307 a8, boolean a9) {
        net.minecraft.class_823.method_23802(a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, false);
    }

    /** was net/minecraft/class_836.method_3578(Lnet/minecraft/class_2484$class_2485;Lnet/minecraft/class_9296;)Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_3578(net.minecraft.class_2484.class_2485 a0, net.minecraft.class_9296 a1) {
        return net.minecraft.class_836.method_65832(a0, a1);
    }

    /** was net/minecraft/class_8496.method_51260(Lnet/minecraft/class_310;Lnet/minecraft/class_542$class_8495;Lnet/minecraft/class_4011;Lnet/minecraft/class_4341;)V */
    public static void method_51260(net.minecraft.class_310 a0, net.minecraft.class_542.class_8495 a1, net.minecraft.class_4011 a2, net.minecraft.class_4341 a3) {
        net.minecraft.class_8496.method_51260(a0, a1, a3);
    }

    /** was net/minecraft/class_892.method_23155(Lnet/minecraft/class_1511;F)F */
    public static float method_23155(net.minecraft.class_1511 a0, float a1) {
        return net.minecraft.class_892.method_23155(a1);
    }

    /** was net/minecraft/class_895.method_3917(FFFFILnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3917(float a0, float a1, float a2, float a3, int a4, net.minecraft.class_4587 a5, net.minecraft.class_4597 a6, int a7) {
        net.minecraft.class_895.method_3917(a0, a1, a2, a3, a5, a6, a7);
    }

    /** was net/minecraft/class_918.method_27952(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;ZZ)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_27952(net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, boolean a2, boolean a3) {
        return net.minecraft.class_918.method_27952(a0, a1, a3);
    }

    /** was net/minecraft/class_9945.method_62079()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_62079() {
        return net.minecraft.class_9945.method_62079(1.0F);
    }

    /** was net/minecraft/class_9945.method_62080()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_62080() {
        return net.minecraft.class_9945.method_62080(net.minecraft.class_9945.field_55113);
    }

    /** Fabric API's ColorProviderRegistry.ITEM (gone in 1.21.4's Fabric API): see CbItemColorRegistry */
    public static net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry<net.minecraft.class_1935,
            top.dext.centurybridge.facade.v1_21_4.class_326> cbItemColorRegistry() {
        return CbItemColorRegistry.INSTANCE;
    }

    private ClientStatics() {
    }

    /** was net/minecraft/class_638.method_2942(ILnet/minecraft/class_1297;)V -- ClientWorld.addEntity(id, entity):
     *  1.20.1 discarded whatever entity held that id, then added this one */
    public static void method_2942(net.minecraft.class_638 self, int id, net.minecraft.class_1297 e) {
        self.method_2945(id, net.minecraft.class_1297.class_5529.field_26999);
        self.method_53875(e);
    }
}
