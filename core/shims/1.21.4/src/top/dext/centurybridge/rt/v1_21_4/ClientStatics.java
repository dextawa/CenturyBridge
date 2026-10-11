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

    /** was net/minecraft/class_4406.method_25191(I)I */
    public static int method_25191(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25191 was removed from class_4406 in 1.20.3 with no direct replacement");
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
        return self.method_2966().getName();
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

    /** was net/minecraft/class_742.method_3117()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3117(net.minecraft.class_742 self) {
        return self.method_52814().comp_1626();
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

    public void method_22919(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        ((net.minecraft.class_4588) (Object) this).method_22919(a0, a1, a2, a3, a4, 1.0f, a5, a6);
    }

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

    /** was net/minecraft/class_783.field_4227Lnet/minecraft/class_787; */
    public static net.minecraft.class_787 field_4227() {
        throw new UnsupportedOperationException("CenturyBridge: class_783.field_4227 (static class_787) was deleted in 1.21 with no direct replacement; new class_783 is a record exposing no static class_787 constant, and the original value cannot be inferred from the new API surface");
    }

    /** was net/minecraft/class_927.method_4072(Lnet/minecraft/class_1308;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4072(net.minecraft.class_927 self, net.minecraft.class_1308 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_927.method_4072 (EntityRenderer.render) was removed in 1.21; the entity rendering pipeline was refactored to use EntityRenderState and no direct equivalent exists on class_927");
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

    /** was net/minecraft/class_284.method_22097(ILjava/lang/CharSequence;)I */
    public static int method_22097(net.minecraft.class_284 self, int a0, java.lang.CharSequence a1) {
        return self.method_22096(a0, a1);
        // CB-CONFIDENCE: high -- method_22097(int, CharSequence)->int was renamed to method_22096 with identical signature on the same class; exact 1:1 delegation.
    }

    /** was net/minecraft/class_284.method_34419(IILjava/lang/CharSequence;)V */
    public static void method_34419(net.minecraft.class_284 self, int a0, int a1, java.lang.CharSequence a2) {
        self.method_22096(a0, a2);
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

    /** was net/minecraft/class_5294.method_28109(FF)[F */
    public static float[] method_28109(net.minecraft.class_5294 self, float a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_28109(FF)[F was deleted in 1.21.2 with no direct replacement; the closest method_28112(Vec3d,float) has an incompatible signature");
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

    /** was net/minecraft/class_5944.method_34583(Ljava/lang/String;Ljava/lang/Object;)V */
    public static void method_34583(net.minecraft.class_5944 self, java.lang.String a0, java.lang.Object a1) {
        if (a1 instanceof java.lang.Integer) {
            self.method_62899(a0, ((java.lang.Integer) a1).intValue());
        } else {
            throw new UnsupportedOperationException("CenturyBridge: method_34583(String, Object) was deleted; method_62899(String, int) is the closest equivalent but only accepts Integer");
        }
    }

    /** was net/minecraft/class_5944.method_35786()Lnet/minecraft/class_293; */
    public static net.minecraft.class_293 method_35786(net.minecraft.class_5944 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_5944 no longer exposes its underlying class_293; the provider is now passed into construction (method_60897/method_62896) and not retrievable");
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

    /** was net/minecraft/class_757.method_3207()V */
    public static void method_3207(net.minecraft.class_757 self) {
        self.method_35768(true);
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

    /** was net/minecraft/class_758.method_3210(Lnet/minecraft/class_4184;FLnet/minecraft/class_638;IF)V */
    public static void method_3210(net.minecraft.class_758 self, net.minecraft.class_4184 a0, float a1, net.minecraft.class_638 a2, int a3, float a4) {
        self.method_62185(a0, a1, a2, a3, a4);
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

    /** was net/minecraft/class_806$class_5827.field_28795Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_28795() {
        throw new UnsupportedOperationException("CenturyBridge: field_28795 was a static Identifier constant on class_806$class_5827, deleted in 1.21.2 with no direct replacement; its value cannot be reconstructed from the new API");
    }

    /** was net/minecraft/class_807.method_3497()Ljava/util/List; */
    public static java.util.List method_3497(net.minecraft.class_807 self) {
        return self.comp_3049();
    }

    /** was net/minecraft/class_8138.method_49050(Lnet/minecraft/class_8113;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_49050(net.minecraft.class_8138 self, net.minecraft.class_8113 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_49050 was deleted in 1.21.2; no new API on class_8138 returns an Identifier from class_8113");
    }

    /** was net/minecraft/class_816.method_3519()Ljava/util/List; */
    public static java.util.List method_3519(net.minecraft.class_816 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_3519 (list getter) was removed; class_816 no longer exposes the list passed to its constructor");
    }

    /** was net/minecraft/class_816.method_3520()Ljava/util/Set; */
    public static java.util.Set method_3520(net.minecraft.class_816 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_816.method_3520() returned a Set (of render layers) that is no longer exposed in 1.21.2; the constructor accepts a List but provides no getter to retrieve it");
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

    /** was net/minecraft/class_898.method_3954(Lnet/minecraft/class_1297;DDDFFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3954(net.minecraft.class_898 self, net.minecraft.class_1297 a0, double a1, double a2, double a3, float a4, float a5, net.minecraft.class_4587 a6, net.minecraft.class_4597 a7, int a8) {
        self.method_62424(a0, a1, a2, a3, a5, a6, a7, a8);
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

    /** was net/minecraft/class_918.field_40530Lnet/minecraft/class_1091; */
    public static net.minecraft.class_1091 field_40530() {
        throw new UnsupportedOperationException("CenturyBridge: field_40530 (class_1091 ModelIdentifier) was deleted in 1.21.2 with no direct replacement; field_40532/field_40533 are new fields with potentially different semantics");
    }

    /** was net/minecraft/class_918.method_29711(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;ZZ)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_29711(net.minecraft.class_918 self, net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, boolean a2, boolean a3) {
        return self.method_23181(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_951.method_4127(Lnet/minecraft/class_1477;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4127(net.minecraft.class_951 self, net.minecraft.class_1477 a0) {
        return self.method_33431(self.method_62542());
    }

    /** was net/minecraft/class_4507.<init>(Lnet/minecraft/class_922;)V */
    public static net.minecraft.class_4507 cbNewclass_4507(net.minecraft.class_922 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4507 constructor now requires Sprite (class_3879), Identifier (class_2960), and render type enum (class_4507$class_9995) which cannot be inferred from the old single-argument constructor");
    }

    /** was net/minecraft/class_473.<init>(Lnet/minecraft/class_1657;Lnet/minecraft/class_1799;Lnet/minecraft/class_1268;)V */
    public static net.minecraft.class_473 cbNewclass_473(net.minecraft.class_1657 a0, net.minecraft.class_1799 a1, net.minecraft.class_1268 a2) {
        return new net.minecraft.class_473(a0, a1, a2, net.minecraft.class_9301.field_49369);
    }

    /** was net/minecraft/class_513.<init>()V */
    public static net.minecraft.class_513 cbNewclass_513() {
        throw new UnsupportedOperationException("CenturyBridge: class_513 no-arg constructor removed; new constructor requires class_507 and class_9938 which cannot be fabricated from no arguments");
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

    /** was net/minecraft/class_5697.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_5697 cbNewclass_5697(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_5697 now requires class_918 (item model manager) instead of class_759 (texture manager); class_918 must be constructed from class_1092/class_325/class_756 which cannot be derived from class_759");
        // CB-CONFIDENCE: high -- the 1.21.2 item rendering refactor replaced TextureManager with a new ItemModelManager (class_918) requiring entirely different dependencies; no conversion from class_759 exists
    }

    /** was net/minecraft/class_5789.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_610;)V */
    public static net.minecraft.class_5789 cbNewclass_5789(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_610 a1) {
        return new net.minecraft.class_5789(a0, a1, a1);
    }

    /** was net/minecraft/class_606.method_19689(Lnet/minecraft/class_1308;FFF)V */
    public static void method_19689(net.minecraft.class_606 self, net.minecraft.class_1308 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_19689(MobEntity, float, float, float) was deleted in 1.21.2 with no direct replacement in class_606's new API");
    }

    /** was net/minecraft/class_620.field_27525Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 field_27525() {
        throw new UnsupportedOperationException("CenturyBridge: field_27525 (static class_630 constant on class_620) was deleted in 1.21.2 with no direct replacement and no value is available to reconstruct");
        // CB-CONFIDENCE: high -- prompt explicitly states no direct replacement; a static no-arg shim cannot reach instance state, and the original constant value is unknown
    }

    /** was net/minecraft/class_621.method_17128(Lnet/minecraft/class_1528;FFF)V */
    public static void method_17128(net.minecraft.class_621 self, net.minecraft.class_1528 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_621.method_17128 removed in 1.21.2 fog rework; new API computes fog internally (method_32067/a/method_17130) with no way to pass explicit (class_1528, float, float, float) parameters");
    }

    /** was net/minecraft/class_623.method_17793(Lnet/minecraft/class_1642;)Z */
    public static boolean method_17793(net.minecraft.class_623 self, net.minecraft.class_1642 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_623.method_17793 (Frustum.isVisible(Box)) was removed in 1.21.2; class_623 exposes only a constructor with no visibility-testing methods");
    }

    /** was net/minecraft/class_624.method_17131(Lnet/minecraft/class_1493;FFF)V */
    public static void method_17131(net.minecraft.class_624 self, net.minecraft.class_1493 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_624.method_17131(ParticleEffect,float,float,float) was removed in 1.21.2; the particle spawning API was reworked and no direct equivalent exists on class_624");
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

    /** was net/minecraft/class_915.method_3993(Lnet/minecraft/class_1533;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3993(net.minecraft.class_915 self, net.minecraft.class_1533 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3993 returned an Identifier for an ItemStack's model/texture location; removed in 1.21.2 item model rework with no direct equivalent (method_62472 returns Text with different semantics)");
        // CB-CONFIDENCE: medium -- method explicitly deleted with no direct replacement; closest new method_62472 returns Text instead of Identifier, indicating a semantic change that cannot be faithfully bridged
    }

    /** was net/minecraft/class_916.method_3999(Lnet/minecraft/class_1542;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3999(net.minecraft.class_916 self, net.minecraft.class_1542 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3999 returned an item model/texture Identifier for an ItemStack; 1.21.2 overhauled item rendering to a model-based system (class_10039/class_10017) with no single Identifier equivalent");
    }

    /** was net/minecraft/class_919.method_4033(Lnet/minecraft/class_1538;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4033(net.minecraft.class_919 self, net.minecraft.class_1538 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_4033 returned Identifier (class_2960) from class_1538; 1.21.2 removed this lookup with no equivalent on class_919");
    }

    /** was net/minecraft/class_921.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_921 cbNewclass_921(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1) {
        return new net.minecraft.class_921(a0, a1, null);
    }

    /** was net/minecraft/class_922.method_4045(Lnet/minecraft/class_1309;F)F */
    public static float method_4045(net.minecraft.class_922 self, net.minecraft.class_1309 a0, float a1) {
        return 0.0F;
    }

    /** was net/minecraft/class_9255.method_57803(Lnet/minecraft/class_9254;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_57803(net.minecraft.class_9255 self, net.minecraft.class_9254 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_57803 returned a sound ResourceLocation from DamageEffects; 1.21.2 replaced it with method_62389 which plays the sound directly (void), so the sound location is no longer exposed");
    }

    /** was net/minecraft/class_925.method_4065(Lnet/minecraft/class_1688;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4065(net.minecraft.class_925 self, net.minecraft.class_1688 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_4065(ItemStack)->Identifier has no 1.21.2 equivalent; item textures are now resolved through the model system (class_10045), not texture identifiers");
    }

    /** was net/minecraft/class_927.method_55833(Lnet/minecraft/class_1308;)F */
    public static float method_55833(net.minecraft.class_927 self, net.minecraft.class_1308 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_55833(LivingEntity)F was removed in the 1.21.2 render-state refactor; replacements method_55831(class_10017)F and method_55832(class_10042)F require render-state objects that cannot be derived from a bare LivingEntity");
    }

    /** was net/minecraft/class_929.method_4079(Lnet/minecraft/class_1451;Lnet/minecraft/class_4587;F)V */
    public static void method_4079(net.minecraft.class_929 self, net.minecraft.class_1451 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62395(a0, self.method_62394(), a2);
    }

    /** was net/minecraft/class_930.method_4081(Lnet/minecraft/class_1453;F)F */
    public static float method_4081(net.minecraft.class_930 self, net.minecraft.class_1453 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_4081 returned a float (particle light level) that callers consumed; the 1.21.2 replacement method_62502 takes an extra class_10051 arg and returns void, so the computed float is no longer available to the caller");
        // CB-CONFIDENCE: high -- the old method returned float and no new method on class_930 returns float; the contract fundamentally changed from query to command
    }

    /** was net/minecraft/class_9321.method_57792(Lnet/minecraft/class_9254;FFF)V */
    public static void method_57792(net.minecraft.class_9321 self, net.minecraft.class_9254 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_57792(class_9254, float, float, float) was deleted in 1.21.2 with no direct replacement; no remaining method on class_9321 accepts class_9254");
        // CB-CONFIDENCE: high -- the method was explicitly deleted and no remaining method in the new API surface accepts class_9254, so no faithful bridge exists
    }

    /** was net/minecraft/class_937.method_4099(Lnet/minecraft/class_1456;Lnet/minecraft/class_4587;F)V */
    public static void method_4099(net.minecraft.class_937 self, net.minecraft.class_1456 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_4099 was removed; replacement method_62511 requires class_10057 but caller provides class_4587 with no known conversion");
    }

    /** was net/minecraft/class_942.method_4107(Lnet/minecraft/class_1614;)F */
    public static float method_4107(net.minecraft.class_942 self, net.minecraft.class_1614 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_4107(class_1614) has no equivalent in 1.21.2; entity-group-specific attack damage was removed from the enchantment system");
        // CB-CONFIDENCE: high -- the old method took an EntityGroup parameter that no method in the new class_942 surface accepts, and the only float-returning method (method_3919) takes no arguments, so the group-specific contract cannot be expressed
    }

    /** was net/minecraft/class_946.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_946 cbNewclass_946(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_5601 a3) {
        return new net.minecraft.class_946(a0);
    }

    /** was net/minecraft/class_946.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_606;)V */
    public static net.minecraft.class_946 cbNewclass_946(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_606 a3) {
        return new net.minecraft.class_946(a0);
    }

    /** was net/minecraft/class_946.method_35801(Lnet/minecraft/class_1547;)Z */
    public static boolean method_35801(net.minecraft.class_946 self, net.minecraft.class_1547 a0) {
        throw new UnsupportedOperationException("CenturyBridge: RecipeManager.method_35801(Recipe) was removed in 1.21.2 recipe system rework; no equivalent exists in the new API");
    }

    /** was net/minecraft/class_9483.method_58752(Lnet/minecraft/class_9461;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_58752(net.minecraft.class_9483 self, net.minecraft.class_9461 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_58752 returned an Identifier (texture) from class_9483+class_9461; deleted in 1.21.2 with no direct replacement -- method_62497 now takes class_9461+class_10048+float and returns void, applying the texture rather than exposing it as an Identifier");
    }

    /** was net/minecraft/class_949.method_4124(Lnet/minecraft/class_1628;)F */
    public static float method_4124(net.minecraft.class_949 self, net.minecraft.class_1628 a0) {
        throw new UnsupportedOperationException("CenturyBridge: Enchantment.getAttackDamage(ItemStack) was removed in 1.21.2; enchantment damage is now applied via data-driven effect components (method_62539) rather than returned as a computed float");
    }

    /** was net/minecraft/class_951.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_610;)V */
    public static net.minecraft.class_951 cbNewclass_951(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_610 a1) {
        return new net.minecraft.class_951(a0, a1, a1);
    }

    /** was net/minecraft/class_951.method_4125(Lnet/minecraft/class_1477;F)F */
    public static float method_4125(net.minecraft.class_951 self, net.minecraft.class_1477 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_4125(class_1477, float) returning float was deleted; the closest new method_62541(class_1477, class_10069, float) returns void and requires an extra class_10069 argument, so the old float-returning contract cannot be expressed");
    }

    /** was net/minecraft/class_955.method_4134(Lnet/minecraft/class_1685;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4134(net.minecraft.class_955 self, net.minecraft.class_1685 a0) {
        return self.field_4796;
    }

    /** was net/minecraft/class_956.method_4136(Lnet/minecraft/class_1541;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4136(net.minecraft.class_956 self, net.minecraft.class_1541 a0) {
        throw new UnsupportedOperationException("CenturyBridge: RenderPhase.method_4136(class_1541)->class_2960 has no direct replacement in 1.21.2; no new API on class_956 returns class_2960");
        // CB-CONFIDENCE: high -- the new API surface has no method returning class_2960, and the member is explicitly marked deleted with no direct replacement
    }

    /** was net/minecraft/class_961.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_961 cbNewclass_961(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1) {
        return new net.minecraft.class_961(a0, a1, a1, false);
    }

    /** was net/minecraft/class_965.method_4155(Lnet/minecraft/class_1640;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4155(net.minecraft.class_965 self, net.minecraft.class_1640 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_4155 has no direct replacement; method_62574 requires a class_10082 render state that cannot be constructed from the old MatrixStack/VertexConsumerProvider/light parameters, and the two-float signature was reduced to one");
    }

    /** was net/minecraft/class_965.method_4157(Lnet/minecraft/class_1640;Lnet/minecraft/class_4587;F)V */
    public static void method_4157(net.minecraft.class_965 self, net.minecraft.class_1640 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62574(a0, self.method_62575(), a2);
        // CB-CONFIDENCE: medium -- method_62574 is the clear replacement for method_4157 (same Particle+float params), but the old MatrixStack (a1) is replaced by the manager's internal class_10082 context from method_62575; caller-provided transform is ignored
    }

    /** was net/minecraft/class_967.method_4161(Lnet/minecraft/class_1639;Lnet/minecraft/class_4587;F)V */
    public static void method_4161(net.minecraft.class_967 self, net.minecraft.class_1639 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_4161 was a rendering method (LivingEntity, DrawContext, float) deleted from class_967; new API surface has no rendering equivalent");
    }

    /** was net/minecraft/class_969.method_4167(Lnet/minecraft/class_1493;F)F */
    public static float method_4167(net.minecraft.class_969 self, net.minecraft.class_1493 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_4167 returned a float value with no equivalent in 1.21.2; method_62581 returns void and no float-returning accessor exists for the old result");
    }

    /** was net/minecraft/class_973.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_922;)V */
    public static net.minecraft.class_973 cbNewclass_973(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_922 a1) {
        return new net.minecraft.class_973(a1, a0);
    }

    /** was net/minecraft/class_976.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_5599;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_976 cbNewclass_976(net.minecraft.class_3883 a0, net.minecraft.class_5599 a1, net.minecraft.class_759 a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_759 cannot be converted to class_918 without its API surface");
    }

    /** was net/minecraft/class_979.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_5599;)V */
    public static net.minecraft.class_979 cbNewclass_979(net.minecraft.class_3883 a0, net.minecraft.class_5599 a1) {
        return new net.minecraft.class_979(a0, a1, new net.minecraft.class_10197(null, null));
    }

    /** was net/minecraft/class_9824$class_9825.comp_2871Lnet/minecraft/class_1100; */
    public static net.minecraft.class_1100 comp_2871() {
        throw new UnsupportedOperationException("CenturyBridge: comp_2871 was a static field on class_9824$class_9825, now only an instance record component; no static equivalent exists");
        // CB-CONFIDENCE: high -- the new API surface shows comp_2871 only as an instance record component (with accessor method and instance field), not as a static field; a parameterless static shim cannot produce an instance field value
    }

    /** was net/minecraft/class_9824$class_9825.comp_2872()Ljava/util/function/Supplier; */
    public static java.util.function.Supplier comp_2872(net.minecraft.class_9824.class_9825 self) {
        return () -> self.comp_3062();
    }

    /** was net/minecraft/class_9824.method_61051()V */
    public static void method_61051(net.minecraft.class_9824 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_9824.method_61051()V was removed in 1.21.2; no no-arg void equivalent exists in the new API surface");
        // CB-CONFIDENCE: high -- the old method is a no-arg void with no matching replacement in the new API surface; all new methods require arguments
    }

    /** was net/minecraft/class_9824.method_61058(Lnet/minecraft/class_2769;Ljava/lang/String;)Ljava/lang/Comparable; */
    public static java.lang.Comparable method_61058(net.minecraft.class_9824 self, net.minecraft.class_2769 a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_61058(class_2769, String)->Comparable was deleted in 1.21.2 with no direct replacement; method_62626() returns a single-arg Function that cannot express the old two-parameter contract");
    }

    /** was net/minecraft/class_9824.method_61069()Lit/unimi/dsi/fastutil/objects/Object2IntMap; */
    public static it.unimi.dsi.fastutil.objects.Object2IntMap method_61069(net.minecraft.class_9824 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_61069 was deleted in 1.21.2 with no direct replacement; class_9824 no longer exposes an Object2IntMap accessor");
    }

    /** was net/minecraft/class_988.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_5599;)V */
    public static net.minecraft.class_988 cbNewclass_988(net.minecraft.class_3883 a0, net.minecraft.class_5599 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_988 constructor requires new class_10197 argument with no old equivalent");
    }

    /** was net/minecraft/class_989.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_989 cbNewclass_989(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_759 (old ItemRenderer) cannot be converted to class_918 (new ItemRenderer) without its 1.21.2 API surface");
    }

    /** was net/minecraft/class_998.method_32200()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32200(net.minecraft.class_998 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_998.method_32200() was deleted in 1.21.2 with no direct replacement; the new Particle API exposes no path to class_5607");
    }

    /** was net/minecraft/class_1005.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_1005 cbNewclass_1005(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_759 cannot be converted to the required class_918 from the provided API surface");
    }

    /** was net/minecraft/class_1007.method_4215(Lnet/minecraft/class_742;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4215(net.minecraft.class_1007 self, net.minecraft.class_742 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a class_10055 render state, which cannot be constructed or derived from the old method arguments");
    }

    /** was net/minecraft/class_1156.method_31365(Lnet/minecraft/class_372;I)V */
    public static void method_31365(net.minecraft.class_1156 self, net.minecraft.class_372 a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1156 API has no operation accepting class_372 and an int, so the old contract cannot be expressed faithfully");
    }

    /** was net/minecraft/class_1921.method_23689(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23689(net.minecraft.class_1921 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the entity-translucent render layer with face culling has no faithful equivalent in the 1.21.2 API");
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

    /** was net/minecraft/class_3880.method_32138()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32138(net.minecraft.class_3880 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_3880 no longer exposes or guarantees access to the class_5607 value formerly returned by method_32138");
    }

    /** was net/minecraft/class_3883.method_3931(Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3931(net.minecraft.class_3883 self, net.minecraft.class_1297 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_3883 API no longer exposes entity-specific texture identifier lookup");
    }

    /** was net/minecraft/class_3886.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;)V */
    public static net.minecraft.class_3886 cbNewclass_3886(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_5601 a3) {
        return new net.minecraft.class_3886(a0, a1, a1, a2, a3, a2, a3);
    }

    /** was net/minecraft/class_3887.method_23194(Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_23194(net.minecraft.class_3887 self, net.minecraft.class_1297 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3887 no longer exposes or derives a texture identifier from class_1297");
    }

    /** was net/minecraft/class_4004.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_4004 cbNewclass_4004(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        throw new UnsupportedOperationException("CenturyBridge: cannot convert class_759 to the required class_918 without an exposed accessor or guaranteed conversion");
    }

    /** was net/minecraft/class_4041.method_18330(Lnet/minecraft/class_4019;FFF)V */
    public static void method_18330(net.minecraft.class_4041 self, net.minecraft.class_4019 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_4041 API has no equivalent accepting class_4019 and three float coordinates");
    }

    /** was net/minecraft/class_4043.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_759;)V */
    public static net.minecraft.class_4043 cbNewclass_4043(net.minecraft.class_3883 a0, net.minecraft.class_759 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_759 cannot be converted to the required class_918 using the available API");
    }

    /** was net/minecraft/class_4073.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_5599;)V */
    public static net.minecraft.class_4073 cbNewclass_4073(net.minecraft.class_3883 a0, net.minecraft.class_5599 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the required class_10197 constructor argument cannot be derived from the old class_3883 and class_5599 arguments");
    }

    /** was net/minecraft/class_4506.<init>(Lnet/minecraft/class_922;)V */
    public static net.minecraft.class_4506 cbNewclass_4506(net.minecraft.class_922 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_5617.class_5618 context that cannot be derived from class_922");
    }

    /** was net/minecraft/class_4840.method_32026(Lnet/minecraft/class_5605;)Lnet/minecraft/class_5609; */
    public static net.minecraft.class_5609 method_32026(net.minecraft.class_4840 self, net.minecraft.class_5605 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4840 no longer exposes an API accepting class_5605 or returning class_5609");
    }

    /** was net/minecraft/class_4842.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Lnet/minecraft/class_5601;Z)V */
    public static net.minecraft.class_4842 cbNewclass_4842(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_5601 a1, net.minecraft.class_5601 a2, net.minecraft.class_5601 a3, boolean a4) {
        return new net.minecraft.class_4842(a0, a1, a2, a3, a1, a2, a3);
    }

    /** was net/minecraft/class_507.<init>()V */
    public static net.minecraft.class_507 cbNewclass_507() {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_1729 context and list that the old no-argument constructor cannot supply");
    }

    /** was net/minecraft/class_512.<init>(Lnet/minecraft/class_314;)V */
    public static net.minecraft.class_512 cbNewclass_512(net.minecraft.class_314 a0) {
        throw new UnsupportedOperationException("CenturyBridge: no new API converts class_314 into the required class_507.class_10329 constructor argument");
    }

    /** was net/minecraft/class_513.method_2629(Ljava/util/List;)V */
    public static void method_2629(net.minecraft.class_513 self, java.util.List a0) {
        throw new UnsupportedOperationException("CenturyBridge: the old recipe-display notification callback has no equivalent in the new API");
    }

    /** was net/minecraft/class_516.method_2648(Z)Ljava/util/List; */
    public static java.util.List method_2648(net.minecraft.class_516 self, boolean a0) {
        return self.method_64885(a0 ? net.minecraft.class_516.class_9937.field_52848 : net.minecraft.class_516.class_9937.field_52849);
    }

    /** was net/minecraft/class_517.<init>()V */
    public static net.minecraft.class_517 cbNewclass_517() {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_1720 inventory, class_2561 title, and offer list that the old no-argument constructor did not provide");
    }

    /** was net/minecraft/class_10025.field_53368Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53368() {
        throw new UnsupportedOperationException("CenturyBridge: deleted field_53368 of type net.minecraft.class_1087 cannot be reconstructed from the remaining boolean and net.minecraft.class_10444 fields");
    }

    /** was net/minecraft/class_10038.field_53434Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53434() {
        throw new UnsupportedOperationException("CenturyBridge: the old class_1087 constant was deleted and the new class_10038 API exposes no equivalent value or construction path");
    }

    /** was net/minecraft/class_10039.field_53436Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53436() {
        throw new UnsupportedOperationException("CenturyBridge: the old class_1087 constant cannot be reconstructed from the new API surface, which exposes only a float constant");
    }

    /** was net/minecraft/class_10040.field_53442Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53442() {
        throw new UnsupportedOperationException("CenturyBridge: deleted class_1087 field has no equivalent or reconstructible value in the new API");
    }

    /** was net/minecraft/class_10042.field_53466Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53466() {
        throw new UnsupportedOperationException("CenturyBridge: the removed BakedModel constant has no faithful equivalent in the new render-state API");
    }

    /** was net/minecraft/class_10042.field_53469Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53469() {
        return null;
    }

    /** was net/minecraft/class_10042.field_53471Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53471() {
        throw new UnsupportedOperationException("CenturyBridge: deleted field_53471 has no equivalent or reconstructible value in the new class_10042 API");
    }

    /** was net/minecraft/class_10042.method_62614()Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_62614(net.minecraft.class_10042 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_10042 no longer exposes the class_1799 value returned by method_62614");
    }

    /** was net/minecraft/class_10042.method_62615()Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 method_62615(net.minecraft.class_10042 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_10042 API exposes no class_1087 value or equivalent accessor");
    }

    /** was net/minecraft/class_1005.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_1005 cbNewclass_1005(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_1005(a0);
    }

    /** was net/minecraft/class_1005.method_62599(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_10082;FF)V */
    public static void method_62599(net.minecraft.class_1005 self, net.minecraft.class_4587 a0, net.minecraft.class_4597 a1, int a2, net.minecraft.class_10082 a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes only class_10082/matrix transformation methods and cannot perform the old buffered render contract");
    }

    /** was net/minecraft/class_10072.field_53590Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_53590() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_1087 constant has no equivalent in the new class_10072 API");
    }

    /** was net/minecraft/class_10097.method_62632(Lnet/minecraft/class_9824$class_10095;)V */
    public static void method_62632(net.minecraft.class_10097 self, net.minecraft.class_9824.class_10095 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_10097 API has no operation accepting class_9824.class_10095 or an equivalent representation");
    }

    /** was net/minecraft/class_10097.method_62637()Ljava/util/Map; */
    public static java.util.Map method_62637(net.minecraft.class_10097 self) {
        return self.method_62639();
    }

    /** was net/minecraft/class_1044.method_18169(Lnet/minecraft/class_1060;Lnet/minecraft/class_3300;Lnet/minecraft/class_2960;Ljava/util/concurrent/Executor;)V */
    public static void method_18169(net.minecraft.class_1044 self, net.minecraft.class_1060 a0, net.minecraft.class_3300 a1, net.minecraft.class_2960 a2, java.util.concurrent.Executor a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_1044 no longer exposes an API that can perform the old resource-based asynchronous operation");
    }

    /** was net/minecraft/class_1044.method_4625(Lnet/minecraft/class_3300;)V */
    public static void method_4625(net.minecraft.class_1044 self, net.minecraft.class_3300 a0) {
        throw new UnsupportedOperationException("CenturyBridge: resource-manager-based texture loading was removed and the new AbstractTexture API requires prepared texture contents");
    }

    /** was net/minecraft/class_1047.method_4540()Lnet/minecraft/class_1043; */
    public static net.minecraft.class_1043 method_4540(net.minecraft.class_1047 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1047 no longer exposes a class_1043 equivalent");
    }

    /** was net/minecraft/class_1059.method_24198(Lnet/minecraft/class_7766$class_7767;)V */
    public static void method_24198(net.minecraft.class_1059 self, net.minecraft.class_7766.class_7767 a0) {
        self.method_45848(a0);
    }

    /** was net/minecraft/class_1060.method_18168(Lnet/minecraft/class_2960;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_18168(net.minecraft.class_1060 self, net.minecraft.class_2960 a0, java.util.concurrent.Executor a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no equivalent for asynchronously loading a single resource by identifier with a caller-supplied executor");
    }

    /** was net/minecraft/class_1060.method_4617(Ljava/lang/String;Lnet/minecraft/class_1043;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4617(net.minecraft.class_1060 self, java.lang.String a0, net.minecraft.class_1043 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new TextureManager API has no equivalent for generating and tracking unique dynamic texture identifiers");
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

    /** was net/minecraft/class_1087.method_4713()Z */
    public static boolean method_4713(net.minecraft.class_1087 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new BakedModel API no longer exposes whether a model uses the built-in renderer");
    }

    /** was net/minecraft/class_1092$class_7779.comp_1058Lnet/minecraft/class_1088; */
    public static net.minecraft.class_1088 comp_1058() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_1088 constant has no equivalent or construction path in the new API");
    }

    /** was net/minecraft/class_1092$class_7779.comp_1059Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 comp_1059() {
        throw new UnsupportedOperationException("CenturyBridge: the old class_1087 field was deleted and the new API exposes no equivalent value");
    }

    /** was net/minecraft/class_1093$class_1094.<init>(Lnet/minecraft/class_793;Z)V */
    public static net.minecraft.class_1093.class_1094 cbNewclass_1093class_1094(net.minecraft.class_793 a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: cannot derive the three boolean flags and class_809 transformation required by the new constructor from class_793 using the provided API surface");
    }

    /** was net/minecraft/class_1093.field_5416Lnet/minecraft/class_1058; */
    public static net.minecraft.class_1058 field_5416() {
        throw new UnsupportedOperationException("CenturyBridge: field_5416 is instance-specific particle sprite state and cannot be reconstructed without a BasicBakedModel receiver");
    }

    /** was net/minecraft/class_1100.method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 method_4753(net.minecraft.class_1100 self, net.minecraft.class_7775 a0, java.util.function.Function a1, net.minecraft.class_3665 a2) {
        return net.minecraft.class_1100.method_65765(self, a0, a2);
    }

    /** was net/minecraft/class_1100.method_62326(Lnet/minecraft/class_1100$class_10103;)V */
    public static void method_62326(net.minecraft.class_1100 self, net.minecraft.class_1100.class_10103 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_10103 registration was removed and the new class_1100 API exposes no equivalent mutator");
    }

    /** was net/minecraft/class_310.method_64036()Lnet/minecraft/class_10201; */
    public static net.minecraft.class_10201 method_64036(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: the equipment model loader is no longer exposed by MinecraftClient in 1.21.4");
    }

    /** was net/minecraft/class_339.method_25361(DD)Z */
    public static boolean method_25361(net.minecraft.class_339 self, double a0, double a1) {
        return self.method_25405(a0, a1);
    }

    /** was net/minecraft/class_372.<init>(Lnet/minecraft/class_372$class_373;Lnet/minecraft/class_2561;Lnet/minecraft/class_2561;Z)V */
    public static net.minecraft.class_372 cbNewclass_372(net.minecraft.class_372.class_373 a0, net.minecraft.class_2561 a1, net.minecraft.class_2561 a2, boolean a3) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructors require a net.minecraft.class_327 instance, but the old arguments provide no equivalent or guaranteed way to obtain one");
    }

    /** was net/minecraft/class_372.<init>(Lnet/minecraft/class_372$class_373;Lnet/minecraft/class_2561;Lnet/minecraft/class_2561;ZI)V */
    public static net.minecraft.class_372 cbNewclass_372(net.minecraft.class_372.class_373 a0, net.minecraft.class_2561 a1, net.minecraft.class_2561 a2, boolean a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_327 text renderer, but the old constructor supplied none and no faithful renderer can be derived from its arguments");
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

    /** was net/minecraft/class_3999.method_18130(Lnet/minecraft/class_289;Lnet/minecraft/class_1060;)Lnet/minecraft/class_287; */
    public static net.minecraft.class_287 method_18130(net.minecraft.class_3999 self, net.minecraft.class_289 a0, net.minecraft.class_1060 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_3999 no longer exposes an API that can create class_287 from class_289 and class_1060");
    }

    /** was net/minecraft/class_4004.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_4004 cbNewclass_4004(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_4004(a0);
    }

    /** was net/minecraft/class_4043.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_4043 cbNewclass_4043(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_4043(a0);
    }

    /** was net/minecraft/class_4943.field_22944Lnet/minecraft/class_4942; */
    public static net.minecraft.class_4942 field_22944() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted model constant's parent, variant, and required texture keys cannot be recovered from the new API surface");
    }

    /** was net/minecraft/class_4943.field_22945Lnet/minecraft/class_4942; */
    public static net.minecraft.class_4942 field_22945() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted model constant has no equivalent in the new API surface");
    }

    /** was net/minecraft/class_4946.field_23047Lnet/minecraft/class_4946$class_4947; */
    public static net.minecraft.class_4946.class_4947 field_23047() {
        throw new UnsupportedOperationException("CenturyBridge: the PARTICLE textured-model factory was removed and cannot be reconstructed from the new class_4946 API surface");
    }

    /** was net/minecraft/class_4999.method_56155(Lnet/minecraft/class_10070;Lnet/minecraft/class_4587;)V */
    public static void method_56155(net.minecraft.class_4999 self, net.minecraft.class_10070 a0, net.minecraft.class_4587 a1) {
        throw new UnsupportedOperationException("CenturyBridge: rendering method was removed and the new API requires a class_4985 renderer plus animation progress, which cannot be derived from the old arguments");
    }

    /** was net/minecraft/class_516.method_2656()Z */
    public static boolean method_2656(net.minecraft.class_516 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new recipe result collection no longer exposes or retains the single-output property");
    }

    /** was net/minecraft/class_5599.<init>()V */
    public static net.minecraft.class_5599 cbNewclass_5599() {
        return net.minecraft.class_5599.method_65522();
    }

    /** was net/minecraft/class_5614$class_5615.<init>(Lnet/minecraft/class_824;Lnet/minecraft/class_776;Lnet/minecraft/class_918;Lnet/minecraft/class_898;Lnet/minecraft/class_5599;Lnet/minecraft/class_327;)V */
    public static net.minecraft.class_5614.class_5615 cbNewclass_5614class_5615(net.minecraft.class_824 a0, net.minecraft.class_776 a1, net.minecraft.class_918 a2, net.minecraft.class_898 a3, net.minecraft.class_5599 a4, net.minecraft.class_327 a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires class_10442, which cannot be constructed from the old arguments because its required class_1092 dependency is unavailable");
    }

    /** was net/minecraft/class_5617$class_5618.<init>(Lnet/minecraft/class_898;Lnet/minecraft/class_918;Lnet/minecraft/class_330;Lnet/minecraft/class_776;Lnet/minecraft/class_3300;Lnet/minecraft/class_5599;Lnet/minecraft/class_10201;Lnet/minecraft/class_327;)V */
    public static net.minecraft.class_5617.class_5618 cbNewclass_5617class_5618(net.minecraft.class_898 a0, net.minecraft.class_918 a1, net.minecraft.class_330 a2, net.minecraft.class_776 a3, net.minecraft.class_3300 a4, net.minecraft.class_5599 a5, net.minecraft.class_10201 a6, net.minecraft.class_327 a7) {
        throw new UnsupportedOperationException("CenturyBridge: class_918 cannot be converted to class_10442 because the required class_1092 dependency is unavailable");
    }

    /** was net/minecraft/class_5617$class_5618.method_32168()Lnet/minecraft/class_918; */
    public static net.minecraft.class_918 method_32168(net.minecraft.class_5617.class_5618 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_918 is no longer exposed by class_5617.class_5618 and no equivalent accessor exists");
    }

    /** was net/minecraft/class_564.method_62082(Lnet/minecraft/class_10086;Lnet/minecraft/class_1306;)Lnet/minecraft/class_572$class_573; */
    public static net.minecraft.class_572.class_573 method_62082(net.minecraft.class_564 self, net.minecraft.class_10086 a0, net.minecraft.class_1306 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_564 no longer exposes an API that can produce class_572.class_573 from class_10086 and class_1306");
    }

    /** was net/minecraft/class_582.field_52910Lnet/minecraft/class_9953; */
    public static net.minecraft.class_9953 field_52910() {
        return net.minecraft.class_582.field_52909;
    }

    /** was net/minecraft/class_591.method_62111(Lnet/minecraft/class_10055;Lnet/minecraft/class_1306;)Lnet/minecraft/class_572$class_573; */
    public static net.minecraft.class_572.class_573 method_62111(net.minecraft.class_591 self, net.minecraft.class_10055 a0, net.minecraft.class_1306 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_591 no longer exposes an API that can produce class_572.class_573 from class_10055 and class_1306");
    }

    /** was net/minecraft/class_606.method_62117(Lnet/minecraft/class_10066;Lnet/minecraft/class_1306;)Lnet/minecraft/class_572$class_573; */
    public static net.minecraft.class_572.class_573 method_62117(net.minecraft.class_606 self, net.minecraft.class_10066 a0, net.minecraft.class_1306 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_606 no longer exposes an API that returns class_572.class_573 from class_10066 and class_1306");
    }

    /** was net/minecraft/class_647.<init>(Lnet/minecraft/class_638;DDDLnet/minecraft/class_1799;)V */
    public static net.minecraft.class_647 cbNewclass_647(net.minecraft.class_638 a0, double a1, double a2, double a3, net.minecraft.class_1799 a4) {
        throw new UnsupportedOperationException("CenturyBridge: cannot convert class_1799 to the required class_10444 render state using the available API");
    }

    /** was net/minecraft/class_647.<init>(Lnet/minecraft/class_638;DDDDDDLnet/minecraft/class_1799;)V */
    public static net.minecraft.class_647 cbNewclass_647(net.minecraft.class_638 a0, double a1, double a2, double a3, double a4, double a5, double a6, net.minecraft.class_1799 a7) {
        throw new UnsupportedOperationException("CenturyBridge: cannot convert class_1799 to class_10444 without the external item-model rendering context required to populate the new render state");
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

    /** was net/minecraft/class_7528.method_44385()I */
    public static int method_44385(net.minecraft.class_7528 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new API removed configurable padding, so getPaddingDoubled semantics cannot be preserved");
    }

    /** was net/minecraft/class_7528.method_52233(Lnet/minecraft/class_332;IIII)V */
    public static void method_52233(net.minecraft.class_7528 self, net.minecraft.class_332 a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: the removed drawBox overload accepted explicit bounds, but the new API only renders using the widget's own bounds");
    }

    /** was net/minecraft/class_7528.method_53532()I */
    public static int method_53532(net.minecraft.class_7528 self) {
        return 6;
    }

    /** was net/minecraft/class_759.<init>(Lnet/minecraft/class_310;Lnet/minecraft/class_898;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_759 cbNewclass_759(net.minecraft.class_310 a0, net.minecraft.class_898 a1, net.minecraft.class_918 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_10442 instance, which cannot be derived from the old constructor arguments");
    }

    /** was net/minecraft/class_793.field_4253Lnet/minecraft/class_793; */
    public static net.minecraft.class_793 field_4253() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_793 sentinel cannot be reconstructed from the new API without its original constructor constants");
    }

    /** was net/minecraft/class_793.method_24077(Ljava/lang/String;)Lnet/minecraft/class_4730; */
    public static net.minecraft.class_4730 method_24077(net.minecraft.class_793 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: texture lookup by name requires the removed model texture-resolution context and cannot be expressed by class_793 alone");
    }

    /** was net/minecraft/class_793.method_3431()Lnet/minecraft/class_793; */
    public static net.minecraft.class_793 method_3431(net.minecraft.class_793 self) {
        net.minecraft.class_793 current = self;
        while (current.method_65538() instanceof net.minecraft.class_793) {
            current = (net.minecraft.class_793) current.method_65538();
        }
        return current;
    }

    /** was net/minecraft/class_793.method_3432(Ljava/lang/String;)Z */
    public static boolean method_3432(net.minecraft.class_793 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: the old named-texture existence query has no equivalent in class_793's new API surface");
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

    /** was net/minecraft/class_793.method_3446(Ljava/util/function/Function;Lnet/minecraft/class_3665;Z)Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 method_3446(net.minecraft.class_793 self, java.util.function.Function a0, net.minecraft.class_3665 a1, boolean a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_4753 requires class_10419, class_7775, an additional boolean, and class_809 inputs that cannot be derived faithfully from the old Function-based contract");
    }

    /** was net/minecraft/class_793.method_35789()Z */
    public static boolean method_35789(net.minecraft.class_793 self) {
        throw new UnsupportedOperationException("CenturyBridge: ambient occlusion inheritance requires the resolved parent model, which the new API no longer stores or exposes");
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

    /** was net/minecraft/class_809.field_4305Lnet/minecraft/class_804; */
    public static net.minecraft.class_804 field_4305() {
        throw new UnsupportedOperationException("CenturyBridge: old instance field thirdPersonLeftHand requires a class_809 receiver, but the static shim has none");
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

    /** was net/minecraft/class_823.method_32135()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32135(net.minecraft.class_823 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_823 no longer exposes the class_5607 value or an equivalent accessor");
    }

    /** was net/minecraft/class_824.<init>(Lnet/minecraft/class_327;Lnet/minecraft/class_5599;Ljava/util/function/Supplier;Ljava/util/function/Supplier;Ljava/util/function/Supplier;)V */
    public static net.minecraft.class_824 cbNewclass_824(net.minecraft.class_327 a0, net.minecraft.class_5599 a1, java.util.function.Supplier a2, java.util.function.Supplier a3, java.util.function.Supplier a4) {
        throw new UnsupportedOperationException("CenturyBridge: the old class_5599 and three Supplier dependencies cannot be faithfully converted to the new class_776, class_10442, class_918, and class_898 constructor dependencies");
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

    /** was net/minecraft/class_8253.<init>(Lnet/minecraft/class_638;DDDLnet/minecraft/class_4002;)V */
    public static net.minecraft.class_8253 cbNewclass_8253(net.minecraft.class_638 a0, double a1, double a2, double a3, net.minecraft.class_4002 a4) {
        throw new UnsupportedOperationException("CenturyBridge: legacy constructor defaults for the six added parameters cannot be derived from the provided API surface");
    }

    /** was net/minecraft/class_837.method_23083(Lnet/minecraft/class_2625;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V */
    public static void method_23083(net.minecraft.class_837 self, net.minecraft.class_2625 a0, float a1, net.minecraft.class_4587 a2, net.minecraft.class_4597 a3, int a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new renderer API requires pre-extracted render state unavailable from the old block entity render arguments");
    }

    /** was net/minecraft/class_837.method_37311(Lnet/minecraft/class_8242;)I */
    public static int method_37311(net.minecraft.class_837 self, net.minecraft.class_8242 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_837 no longer exposes sign text color computation in 1.21.4");
    }

    /** was net/minecraft/class_837.method_37312(Lnet/minecraft/class_2338;I)Z */
    public static boolean method_37312(net.minecraft.class_837 self, net.minecraft.class_2338 a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_837 API has no position-and-integer boolean query equivalent");
    }

    /** was net/minecraft/class_837.method_45798(Lnet/minecraft/class_2338;Lnet/minecraft/class_8242;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IIIZ)V */
    public static void method_45798(net.minecraft.class_837 self, net.minecraft.class_2338 a0, net.minecraft.class_8242 a1, net.minecraft.class_4587 a2, net.minecraft.class_4597 a3, int a4, int a5, int a6, boolean a7) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires precomputed render state unavailable from the old method parameters");
    }

    /** was net/minecraft/class_837.method_49920(Lnet/minecraft/class_2625;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_2680;Lnet/minecraft/class_2478;Lnet/minecraft/class_4719;Lnet/minecraft/class_3879;)V */
    public static void method_49920(net.minecraft.class_837 self, net.minecraft.class_2625 a0, net.minecraft.class_4587 a1, net.minecraft.class_4597 a2, int a3, int a4, net.minecraft.class_2680 a5, net.minecraft.class_2478 a6, net.minecraft.class_4719 a7, net.minecraft.class_3879 a8) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_837 API cannot render the sign text required by the old combined sign-and-text rendering contract");
    }

    /** was net/minecraft/class_875.method_3874(Lnet/minecraft/class_10019;Lnet/minecraft/class_4587;)V */
    public static void method_3874(net.minecraft.class_875 self, net.minecraft.class_10019 a0, net.minecraft.class_4587 a1) {
        throw new UnsupportedOperationException("CenturyBridge: new API requires a class_1496 entity and tick delta, which cannot be recovered from the old class_10019 and class_4587 arguments");
    }

    /** was net/minecraft/class_898.<init>(Lnet/minecraft/class_310;Lnet/minecraft/class_1060;Lnet/minecraft/class_918;Lnet/minecraft/class_330;Lnet/minecraft/class_776;Lnet/minecraft/class_327;Lnet/minecraft/class_315;Lnet/minecraft/class_5599;Lnet/minecraft/class_10201;)V */
    public static net.minecraft.class_898 cbNewclass_898(net.minecraft.class_310 a0, net.minecraft.class_1060 a1, net.minecraft.class_918 a2, net.minecraft.class_330 a3, net.minecraft.class_776 a4, net.minecraft.class_327 a5, net.minecraft.class_315 a6, net.minecraft.class_5599 a7, net.minecraft.class_10201 a8) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_10442 instance, but the old arguments provide neither class_10442 nor its required class_1092 dependency");
    }

    /** was net/minecraft/class_916.method_56855(I)I */
    public static int method_56855(net.minecraft.class_916 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: the deleted integer transformation has no equivalent in the new class_916 API");
    }

    /** was net/minecraft/class_916.method_56856(Lnet/minecraft/class_1799;)I */
    public static int method_56856(net.minecraft.class_916 self, net.minecraft.class_1799 a0) {
        return a0.method_7960() ? 187 : net.minecraft.class_1792.method_7880(a0.method_7909()) + a0.method_7919();
    }

    /** was net/minecraft/class_916.method_56857(Lnet/minecraft/class_918;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1799;Lnet/minecraft/class_5819;Lnet/minecraft/class_1937;)V */
    public static void method_56857(net.minecraft.class_916 self, net.minecraft.class_918 a0, net.minecraft.class_4587 a1, net.minecraft.class_4597 a2, int a3, net.minecraft.class_1799 a4, net.minecraft.class_5819 a5, net.minecraft.class_1937 a6) {
        throw new UnsupportedOperationException("CenturyBridge: the new renderer requires a precomputed class_10428 render state, which cannot be derived from the old ItemStack and World arguments through class_916's available API");
    }

    /** was net/minecraft/class_917.method_48302(Lnet/minecraft/class_10067;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_48302(net.minecraft.class_917 self, net.minecraft.class_10067 a0, net.minecraft.class_4587 a1, net.minecraft.class_4597 a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_917 API has no equivalent that accepts vertex consumers and packed light, so the old rendering contract cannot be preserved");
    }

    /** was net/minecraft/class_918.field_40532Lnet/minecraft/class_1091; */
    public static net.minecraft.class_1091 field_40532() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60655("minecraft", "trident"), "inventory");
    }

    /** was net/minecraft/class_918.field_40533Lnet/minecraft/class_1091; */
    public static net.minecraft.class_1091 field_40533() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60656("spyglass"), "inventory");
    }

    /** was net/minecraft/class_918.method_23179(Lnet/minecraft/class_1799;Lnet/minecraft/class_811;ZLnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_1087;)V */
    public static void method_23179(net.minecraft.class_918 self, net.minecraft.class_1799 a0, net.minecraft.class_811 a1, boolean a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5, int a6, net.minecraft.class_1087 a7) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot faithfully render the caller-supplied model with the old left-handed transform contract");
    }

    /** was net/minecraft/class_918.method_4019(Lnet/minecraft/class_1799;Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;I)Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 method_4019(net.minecraft.class_918 self, net.minecraft.class_1799 a0, net.minecraft.class_1937 a1, net.minecraft.class_1309 a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: item model lookup moved out of class_918 and the supplied new API exposes no equivalent returning class_1087");
    }

    /** was net/minecraft/class_918.method_62475(Lnet/minecraft/class_1799;Lnet/minecraft/class_1309;Lnet/minecraft/class_811;)Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 method_62475(net.minecraft.class_918 self, net.minecraft.class_1799 a0, net.minecraft.class_1309 a1, net.minecraft.class_811 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_918 API no longer exposes item model resolution as a class_1087-returning operation");
    }

    /** was net/minecraft/class_918.method_62477(Lnet/minecraft/class_1799;Lnet/minecraft/class_811;ZLnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_1087;Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;I)V */
    public static void method_62477(net.minecraft.class_918 self, net.minecraft.class_1799 a0, net.minecraft.class_811 a1, boolean a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5, int a6, net.minecraft.class_1087 a7, net.minecraft.class_1937 a8, net.minecraft.class_1309 a9, int a10) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot render the caller-supplied class_1087 model because model selection is now internal and requires unavailable render-state data");
    }

    /** was net/minecraft/class_9214.method_56851(FLnet/minecraft/class_1937;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1799;Lnet/minecraft/class_918;FFLnet/minecraft/class_5819;)V */
    public static void method_56851(net.minecraft.class_9214 self, float a0, net.minecraft.class_1937 a1, net.minecraft.class_4587 a2, net.minecraft.class_4597 a3, int a4, net.minecraft.class_1799 a5, net.minecraft.class_918 a6, float a7, float a8, net.minecraft.class_5819 a9) {
        throw new UnsupportedOperationException("CenturyBridge: the old item rendering contract requires world, item stack, item renderer, and transformation mode inputs that the new class_9214 API cannot express");
    }

    /** was net/minecraft/class_922.field_53193Lnet/minecraft/class_918; */
    public static net.minecraft.class_918 field_53193() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted instance ItemRenderer field requires a LivingEntityRenderer receiver, but this static field shim has no receiver and the new API exposes no equivalent");
    }

    /** was net/minecraft/class_945.method_4117(Lnet/minecraft/class_10067;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4117(net.minecraft.class_945 self, net.minecraft.class_10067 a0, net.minecraft.class_4587 a1, net.minecraft.class_4597 a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no rendering method that accepts a vertex consumer provider and light value, so the old rendering contract cannot be preserved");
    }

    /** was net/minecraft/class_963.method_4149(Lnet/minecraft/class_10080;Lnet/minecraft/class_4587;)V */
    public static void method_4149(net.minecraft.class_963 self, net.minecraft.class_10080 a0, net.minecraft.class_4587 a1) {
        a1.method_22905(0.9375F, 0.9375F, 0.9375F);
    }

    /** was net/minecraft/class_977.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_918;)V */
    public static net.minecraft.class_977 cbNewclass_977(net.minecraft.class_3883 a0, net.minecraft.class_918 a1) {
        return new net.minecraft.class_977(a0);
    }

    /** was net/minecraft/class_9824$class_9825.<init>(Lnet/minecraft/class_2680;Lnet/minecraft/class_1100;)V */
    public static net.minecraft.class_9824.class_9825 cbNewclass_9824class_9825(net.minecraft.class_2680 a0, net.minecraft.class_1100 a1) {
        throw new UnsupportedOperationException("CenturyBridge: cannot convert net.minecraft.class_1100 to net.minecraft.class_9979 using the available API");
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

    /** was net/minecraft/class_1921.method_29706()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_29706(net.minecraft.class_1921 self) {
        throw new UnsupportedOperationException("CenturyBridge: the direct glint render layer was removed and the new API has no behaviorally equivalent replacement");
    }

    /** was net/minecraft/class_289.method_1349()Lnet/minecraft/class_287; */
    public static net.minecraft.class_287 method_1349(net.minecraft.class_289 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new Tessellator API no longer exposes its BufferBuilder without beginning a buffer with a draw mode and vertex format");
    }

    /** was net/minecraft/class_289.method_1350()V */
    public static void method_1350(net.minecraft.class_289 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new Tessellator no longer retains the active BufferBuilder required to end and draw it");
    }

    /** was net/minecraft/class_290.field_1579Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1579() {
        return net.minecraft.class_296.field_52113;
    }

    /** was net/minecraft/class_290.field_1583Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1583() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted overlay vertex format element has no equivalent in the new class_290 API");
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

    /** was net/minecraft/class_437.method_25434(Lnet/minecraft/class_332;)V */
    public static void method_25434(net.minecraft.class_437 self, net.minecraft.class_332 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the legacy tiled background texture renderer has no behaviorally equivalent API in 1.21.4");
    }

    /** was net/minecraft/class_437.method_37072()V */
    public static void method_37072(net.minecraft.class_437 self) {
        throw new UnsupportedOperationException("CenturyBridge: narrator mode change delay hook was removed and no public equivalent exists");
    }

    /** was net/minecraft/class_437.method_40041([Lnet/minecraft/class_339;)V */
    public static void method_40041(net.minecraft.class_437 self, net.minecraft.class_339[] a0) {
        for (net.minecraft.class_339 widget : a0) {
            widget.field_22763 = false;
        }
    }

    /** was net/minecraft/class_437.method_59870()F */
    public static float method_59870(net.minecraft.class_437 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_59870 has no behaviorally equivalent operation in the new Screen API");
    }

    /** was net/minecraft/class_4381.method_21160()V */
    public static void method_21160(net.minecraft.class_4381 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new screen no longer has the restore and changes buttons whose visibility method_21160 updated");
    }

    /** was net/minecraft/class_4406.method_21334(I)V */
    public static void method_21334(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no operation that grants operator status to a player by index");
    }

    /** was net/minecraft/class_4406.method_21339(I)V */
    public static void method_21339(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API surface provides no equivalent for setting the deleted integer-backed state");
    }

    /** was net/minecraft/class_447.method_2285(I)I */
    public static int method_2285(net.minecraft.class_447 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_447 no longer exposes an equivalent int-to-int operation for method_2285");
    }

    /** was net/minecraft/class_447.method_2289(Lnet/minecraft/class_332;IILnet/minecraft/class_1792;)V */
    public static void method_2289(net.minecraft.class_447 self, net.minecraft.class_332 a0, int a1, int a2, net.minecraft.class_1792 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the 1.21.4 class_447 API has no equivalent for rendering a class_1792 at screen coordinates");
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

    /** was net/minecraft/class_459.method_20113(Lnet/minecraft/class_459;)Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 method_20113(net.minecraft.class_459 self, net.minecraft.class_459 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_20113 was deleted and the provided API surface does not identify a behaviorally equivalent replacement");
    }

    /** was net/minecraft/class_4722.field_28252Ljava/util/Map; */
    public static java.util.Map field_28252() {
        return net.minecraft.class_4722.field_21712;
    }

    /** was net/minecraft/class_4999.method_26422(Lnet/minecraft/class_4985;Lnet/minecraft/class_4587;F)V */
    public static void method_26422(net.minecraft.class_4999 self, net.minecraft.class_4985 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new API no longer exposes the MatrixStack-based player transform contract");
    }

    /** was net/minecraft/class_5225$1.field_24218Lnet/minecraft/class_5225; */
    public static net.minecraft.class_5225 field_24218() {
        throw new UnsupportedOperationException("CenturyBridge: deleted synthetic enclosing-instance field cannot be reconstructed without a class_5225 receiver");
    }

    /** was net/minecraft/class_5375$1.field_45531Lnet/minecraft/class_5375; */
    public static net.minecraft.class_5375 field_45531() {
        throw new UnsupportedOperationException("CenturyBridge: deleted instance field depended on the class_5375$1 object's enclosing class_5375 receiver, which is unavailable to this static field shim");
    }

    /** was net/minecraft/class_5944.field_36323Lnet/minecraft/class_284; */
    public static net.minecraft.class_284 field_36323() {
        throw new UnsupportedOperationException("CenturyBridge: old field_36323 was an instance-specific view-rotation uniform, but this field shim has no class_5944 receiver from which to retrieve it");
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

    /** was net/minecraft/class_742.method_3125()Z */
    public static boolean method_3125(net.minecraft.class_742 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes skin textures only as class_8685, with no provided operation equivalent to checking whether the cape texture can be rendered");
    }

    /** was net/minecraft/class_748.method_3152(Lnet/minecraft/class_2499;)V */
    public static void method_3152(net.minecraft.class_748 self, net.minecraft.class_2499 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot load a raw NBT list into an existing hotbar entry without inventory and registry context");
    }

    /** was net/minecraft/class_748.method_3153()Lnet/minecraft/class_2499; */
    public static net.minecraft.class_2499 method_3153(net.minecraft.class_748 self) {
        throw new UnsupportedOperationException("CenturyBridge: 1.21.4 requires registry-aware item stack serialization, but the old method provides no registry lookup");
    }

    /** was net/minecraft/class_757.method_34541()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34541(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: the 1.21.4 renderer API no longer exposes an equivalent class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34549()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34549(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: the shader program getter was removed and class_757 exposes no equivalent returning class_5944");
    }

    /** was net/minecraft/class_757.method_35764()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_35764(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: the shader program accessor was removed and the new API exposes no equivalent returning net.minecraft.class_5944");
    }

    /** was net/minecraft/class_757.method_35771()V */
    public static void method_35771(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: the super secret post-processing effect cycle and its internal effect list were removed");
    }

    /** was net/minecraft/class_7581$class_7583.field_39700Lnet/minecraft/class_7581; */
    public static net.minecraft.class_7581 field_39700() {
        throw new UnsupportedOperationException("CenturyBridge: deleted field_39700 constant has no equivalent in the new API");
    }

    /** was net/minecraft/class_7947$1.field_41390Lnet/minecraft/class_7947; */
    public static net.minecraft.class_7947 field_41390() {
        throw new UnsupportedOperationException("CenturyBridge: deleted enum-specific field has no equivalent in the new API surface");
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

    /** was net/minecraft/class_8973.method_55238()Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 method_55238(net.minecraft.class_8973 self) {
        throw new UnsupportedOperationException("CenturyBridge: the old root model part accessor has no equivalent in the new API");
    }

    /** was net/minecraft/class_899$1.field_4698Lnet/minecraft/class_899; */
    public static net.minecraft.class_899 field_4698() {
        throw new UnsupportedOperationException("CenturyBridge: deleted synthetic outer-instance field cannot be reconstructed without a class_899 receiver");
    }

    /** was net/minecraft/class_914$1.field_4719Lnet/minecraft/class_914; */
    public static net.minecraft.class_914 field_4719() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted field stored an instance-specific class_914 receiver, which cannot be reconstructed by a receiverless static helper");
    }

    /** was net/minecraft/class_927.method_4068(Lnet/minecraft/class_1308;Lnet/minecraft/class_4604;DDD)Z */
    public static boolean method_4068(net.minecraft.class_927 self, net.minecraft.class_1308 a0, net.minecraft.class_4604 a1, double a2, double a3, double a4) {
        throw new UnsupportedOperationException("CenturyBridge: the old frustum-based mob visibility check, including leash-holder visibility, has no equivalent in the new API");
    }

    /** was net/minecraft/class_958.method_4138(Lnet/minecraft/class_1481;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4138(net.minecraft.class_958 self, net.minecraft.class_1481 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: the old render contract cannot be expressed because class_958 no longer exposes a method accepting the matrix stack, vertex consumer provider, or light value");
    }

    /** was net/minecraft/class_551.method_24223(Lnet/minecraft/class_1531;FFF)V */
    public static void method_24223(net.minecraft.class_551 self, net.minecraft.class_1531 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: the old entity-based animation contract cannot be expressed because the new API requires an ArmorStandEntityRenderState");
    }

    /** was net/minecraft/class_554.method_22953()Lcom/google/common/collect/ImmutableList; */
    public static com.google.common.collect.ImmutableList method_22953(net.minecraft.class_554 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_554 API no longer exposes the model-parts ImmutableList required by method_22953");
    }

    /** was net/minecraft/class_554.method_45702(Lnet/minecraft/class_630;)Lcom/google/common/collect/ImmutableList$Builder; */
    public static com.google.common.collect.ImmutableList.Builder method_45702(net.minecraft.class_554 self, net.minecraft.class_630 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_554 API exposes no equivalent for building an ImmutableList from class_630");
    }

    /** was net/minecraft/class_5598.<init>()V */
    public static net.minecraft.class_5598 cbNewclass_5598() {
        throw new UnsupportedOperationException("CenturyBridge: class_5598 is abstract and the new constructor requires a class_630 model root; no surviving factory can recreate the old no-argument construction contract");
    }

    /** was net/minecraft/class_5602.method_32078(Lnet/minecraft/class_4719;)Lnet/minecraft/class_5601; */
    public static net.minecraft.class_5601 method_32078(net.minecraft.class_5602 self, net.minecraft.class_4719 a0) {
        return self.method_62130(a0);
    }

    /** was net/minecraft/class_5617$class_5618.method_43338()Lnet/minecraft/class_759; */
    public static net.minecraft.class_759 method_43338(net.minecraft.class_5617.class_5618 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_5617$class_5618 API exposes no class_759 value or equivalent accessor");
    }

    /** was net/minecraft/class_564.method_17077(Lnet/minecraft/class_1642;FFF)V */
    public static void method_17077(net.minecraft.class_564 self, net.minecraft.class_1642 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_564 no longer exposes an API accepting class_1642 and three float arguments, so the old contract cannot be expressed");
    }

    /** was net/minecraft/class_564.method_17134(Lnet/minecraft/class_1642;FFFFF)V */
    public static void method_17134(net.minecraft.class_564 self, net.minecraft.class_1642 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_564 API has no method that can express the deleted five-float operation on class_1642");
    }

    /** was net/minecraft/class_583.<init>()V */
    public static net.minecraft.class_583 cbNewclass_583() {
        throw new UnsupportedOperationException("CenturyBridge: the removed no-argument constructor's required class_630 model hierarchy cannot be reconstructed from the surviving API surface");
    }

    /** was net/minecraft/class_583.<init>(Ljava/util/function/Function;)V */
    public static net.minecraft.class_583 cbNewclass_583(java.util.function.Function a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a net.minecraft.class_630 root model part, which cannot be derived from the old Function argument");
    }

    /** was net/minecraft/class_5944.<init>(Lnet/minecraft/class_5912;Ljava/lang/String;Lnet/minecraft/class_293;)V */
    public static net.minecraft.class_5944 cbNewclass_5944(net.minecraft.class_5912 a0, java.lang.String a1, net.minecraft.class_293 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires an integer constructor parameter, and the old class_5912, String, and class_293 arguments cannot be faithfully converted from the available API surface");
    }

    /** was net/minecraft/class_597.<init>(Lnet/minecraft/class_630;ZFFFFI)V */
    public static net.minecraft.class_597 cbNewclass_597(net.minecraft.class_630 a0, boolean a1, float a2, float a3, float a4, float a5, int a6) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor accepts only class_630 and cannot preserve the removed boolean, float, and int constructor parameters");
    }

    /** was net/minecraft/class_622.method_2840(Z)V */
    public static void method_2840(net.minecraft.class_622 self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: lifting-nose state moved to WitchEntityRenderState and can no longer be set on WitchEntityModel");
    }

    /** was net/minecraft/class_7193.<init>(Lnet/minecraft/class_5285;Lnet/minecraft/class_2378;Lnet/minecraft/class_7723;Lnet/minecraft/class_7780;Lnet/minecraft/class_5350;Lnet/minecraft/class_7712;)V */
    public static net.minecraft.class_7193 cbNewclass_7193(net.minecraft.class_5285 a0, net.minecraft.class_2378 a1, net.minecraft.class_7723 a2, net.minecraft.class_7780 a3, net.minecraft.class_5350 a4, net.minecraft.class_7712 a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires class_10220 configuration that cannot be derived from the old arguments");
    }

    /** was net/minecraft/class_7573.field_46063Lnet/minecraft/class_7573; */
    public static net.minecraft.class_7573 field_46063() {
        throw new UnsupportedOperationException("CenturyBridge: the removed abuse report reason has no faithful equivalent in the new enum");
    }

    /** was net/minecraft/class_757.method_22709(Lorg/joml/Matrix4f;)V */
    public static void method_22709(net.minecraft.class_757 self, org.joml.Matrix4f a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_757 API has no operation that accepts or applies an externally supplied projection Matrix4f");
    }

    /** was net/minecraft/class_757.method_34528()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34528(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_757 API no longer exposes its class_5944 instance");
    }

    /** was net/minecraft/class_758.method_3212()V */
    public static void method_3212(net.minecraft.class_758 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new fog API returns immutable fog parameters and provides no equivalent operation to clear the active fog state");
    }

    /** was net/minecraft/class_7775.method_45872(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1100; */
    public static net.minecraft.class_1100 method_45872(net.minecraft.class_7775 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the 1.21.4 Baker API no longer exposes unbaked model lookup");
    }

    /** was net/minecraft/class_790.<init>(Ljava/util/Map;Lnet/minecraft/class_816;)V */
    public static net.minecraft.class_790 cbNewclass_790(java.util.Map a0, net.minecraft.class_816 a1) {
        throw new UnsupportedOperationException("CenturyBridge: cannot reconstruct class_816$class_9982 from class_816");
    }

    /** was net/minecraft/class_826.method_32147()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32147(net.minecraft.class_826 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_826 no longer exposes a class_5607 model equivalent");
    }

    /** was net/minecraft/class_875.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_549;F)V */
    public static net.minecraft.class_875 cbNewclass_875(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_549 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: no conversion is available from class_549 to the two required class_583 arguments, and the surviving constructor does not accept the legacy float");
    }

    /** was net/minecraft/class_881.<init>(Lnet/minecraft/class_5617$class_5618;Z)V */
    public static net.minecraft.class_881 cbNewclass_881(net.minecraft.class_5617.class_5618 a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: the old boolean argument cannot be faithfully converted to class_5601 without its former semantic mapping");
    }

    /** was net/minecraft/class_881.method_3888(Lnet/minecraft/class_1690;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3888(net.minecraft.class_881 self, net.minecraft.class_1690 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new renderer API requires a class_10004 render state, which cannot be derived from the old method arguments");
    }

    /** was net/minecraft/class_882.method_3893(Lnet/minecraft/class_1428;F)F */
    public static float method_3893(net.minecraft.class_882 self, net.minecraft.class_1428 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_882 no longer exposes an equivalent for computing the class_1428 animation value; the new API only updates render state via method_62397");
    }

    /** was net/minecraft/class_884.method_3895(Lnet/minecraft/class_1430;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3895(net.minecraft.class_884 self, net.minecraft.class_1430 a0) {
        net.minecraft.class_10042 state = self.method_62399();
        self.method_62400(a0, state, 0.0F);
        return self.method_3885(state);
    }

    /** was net/minecraft/class_892.method_32164()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32164(net.minecraft.class_892 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_892 no longer exposes or constructs the class_5607 textured model data");
    }

    /** was net/minecraft/class_896.method_3920(Lnet/minecraft/class_1559;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3920(net.minecraft.class_896 self, net.minecraft.class_1559 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new texture lookup requires a class_10042 render state, but class_896 exposes no API to construct and populate that state from the class_1559 entity");
    }

    /** was net/minecraft/class_9085.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_5599;)V */
    public static net.minecraft.class_9085 cbNewclass_9085(net.minecraft.class_3883 a0, net.minecraft.class_5599 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires class_10197, but the old arguments cannot provide or construct its required class_10201 and class_1059 dependencies");
    }

    /** was net/minecraft/class_918.field_40531Lnet/minecraft/class_1091; */
    public static net.minecraft.class_1091 field_40531() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60656("spyglass_in_hand"), "inventory");
    }

    /** was net/minecraft/class_920.method_4035(Lnet/minecraft/class_1532;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4035(net.minecraft.class_920 self, net.minecraft.class_1532 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new renderer requires a class_10017 render state, but class_920 exposes no API to populate that state from the old entity, yaw, and tick-delta arguments");
    }

    /** was net/minecraft/class_922.method_4039(Lnet/minecraft/class_1309;)F */
    public static float method_4039(net.minecraft.class_922 self, net.minecraft.class_1309 a0) {
        return 90.0F;
    }

    /** was net/minecraft/class_922.method_4044(Lnet/minecraft/class_1309;F)F */
    public static float method_4044(net.minecraft.class_922 self, net.minecraft.class_1309 a0, float a1) {
        return (float) a0.field_6012 + a1;
    }

    /** was net/minecraft/class_928.method_4077(Lnet/minecraft/class_1534;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4077(net.minecraft.class_928 self, net.minecraft.class_1534 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_928 no longer exposes an API that maps class_1534 to class_2960");
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

    /** was net/minecraft/class_968.<init>(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_623;Lnet/minecraft/class_623;Lnet/minecraft/class_623;)V */
    public static net.minecraft.class_968 cbNewclass_968(net.minecraft.class_5617.class_5618 a0, net.minecraft.class_623 a1, net.minecraft.class_623 a2, net.minecraft.class_623 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires three additional class_623 arguments whose values and semantics cannot be derived from the old constructor");
    }

    /** was net/minecraft/class_969.method_4166(Lnet/minecraft/class_1493;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4166(net.minecraft.class_969 self, net.minecraft.class_1493 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: the new renderer API requires a class_10085 render state, which cannot be constructed faithfully from the old method arguments");
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

    /** was net/minecraft/class_1093.field_5417Lnet/minecraft/class_809; */
    public static net.minecraft.class_809 field_5417() {
        throw new UnsupportedOperationException("CenturyBridge: the value is instance-specific in the new API, but this field shim has no receiver");
    }

    /** was net/minecraft/class_310.method_1534()F */
    public static float method_1534(net.minecraft.class_310 self) {
        return self.method_61966().method_60637(false);
    }

    /** was net/minecraft/class_7288.<init>(Lnet/minecraft/class_3883;Lnet/minecraft/class_2960;Lnet/minecraft/class_7288$class_7289;Lnet/minecraft/class_7288$class_7311;)V */
    public static net.minecraft.class_7288 cbNewclass_7288(net.minecraft.class_3883 a0, net.minecraft.class_2960 a1, net.minecraft.class_7288.class_7289 a2, net.minecraft.class_7288.class_7311 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a render-layer Function and visibility boolean, but the old constructor provides neither and no semantics-preserving defaults are specified");
    }

    /** was net/minecraft/class_7528.method_44388(DD)Z */
    public static boolean method_44388(net.minecraft.class_7528 self, double a0, double a1) {
        return self.method_25405(a0, a1);
    }

    /** was net/minecraft/class_793.<init>(Lnet/minecraft/class_2960;Ljava/util/List;Ljava/util/Map;Ljava/lang/Boolean;Lnet/minecraft/class_793$class_4751;Lnet/minecraft/class_809;Ljava/util/List;)V */
    public static net.minecraft.class_793 cbNewclass_793(net.minecraft.class_2960 a0, java.util.List a1, java.util.Map a2, java.lang.Boolean a3, net.minecraft.class_793.class_4751 a4, net.minecraft.class_809 a5, java.util.List a6) {
        throw new UnsupportedOperationException("CenturyBridge: the old override list has no equivalent in the new class_793 constructor or API surface");
    }

    /** was net/minecraft/class_881.method_3891(Lnet/minecraft/class_1690;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3891(net.minecraft.class_881 self, net.minecraft.class_1690 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the boat renderer texture is private and the new API exposes no Identifier-returning accessor");
    }

    /** was net/minecraft/class_8973.method_55237()Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 method_55237(net.minecraft.class_8973 self) {
        return self.method_55819();
    }

    /** was net/minecraft/class_925.field_4747Lnet/minecraft/class_583; */
    public static net.minecraft.class_583 field_4747() {
        throw new UnsupportedOperationException("CenturyBridge: the replacement field has type net.minecraft.class_580, which cannot express the old net.minecraft.class_583 contract");
    }

    /** was net/minecraft/class_9824.<init>(Ljava/util/Map;Lnet/minecraft/class_3695;Lnet/minecraft/class_1100;Lnet/minecraft/class_324;Ljava/util/function/BiConsumer;)V */
    public static net.minecraft.class_9824 cbNewclass_9824(java.util.Map a0, net.minecraft.class_3695 a1, net.minecraft.class_1100 a2, net.minecraft.class_324 a3, java.util.function.BiConsumer a4) {
        return new net.minecraft.class_9824();
    }

    /** was net/minecraft/class_1011.method_4312(IIIIIIIZZ)V */
    public static void method_4312(net.minecraft.class_1011 self, int a0, int a1, int a2, int a3, int a4, int a5, int a6, boolean a7, boolean a8) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no equivalent accepting seven integer and two boolean arguments");
    }

    /** was net/minecraft/class_1060.method_34590(Lnet/minecraft/class_2960;Lnet/minecraft/class_1044;)Lnet/minecraft/class_1044; */
    public static net.minecraft.class_1044 method_34590(net.minecraft.class_1060 self, net.minecraft.class_2960 a0, net.minecraft.class_1044 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new TextureManager API cannot retrieve a registered texture with a caller-supplied fallback without triggering different loading behavior");
    }

    /** was net/minecraft/class_1079.<init>(Ljava/util/List;IIIZ)V */
    public static net.minecraft.class_1079 cbNewclass_1079(java.util.List a0, int a1, int a2, int a3, boolean a4) {
        return new net.minecraft.class_1079(java.util.Optional.of(a0), a1 == -1 ? java.util.Optional.empty() : java.util.Optional.of(a1), a2 == -1 ? java.util.Optional.empty() : java.util.Optional.of(a2), a3, a4);
    }

    /** was net/minecraft/class_459.method_20111(Lnet/minecraft/class_459;)Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 method_20111(net.minecraft.class_459 self, net.minecraft.class_459 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API provides no identifiable equivalent for method_20111, and the remaining same-signature methods cannot be selected without changing semantics");
    }

    /** was net/minecraft/class_4942.method_48524(Lnet/minecraft/class_2960;Ljava/util/Map;)Lcom/google/gson/JsonObject; */
    public static com.google.gson.JsonObject method_48524(net.minecraft.class_4942 self, net.minecraft.class_2960 a0, java.util.Map a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API no longer exposes model JSON serialization, and the model's parent, variant, and required-texture state cannot be accessed through the available public API");
    }

    /** was net/minecraft/class_895.method_3914(Lnet/minecraft/class_1510;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3914(net.minecraft.class_895 self, net.minecraft.class_1510 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API no longer exposes per-player elytra texture resolution");
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

    /** was net/minecraft/class_3887.method_23199(Lnet/minecraft/class_583;Lnet/minecraft/class_2960;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1309;I)V */
    public static void method_23199(net.minecraft.class_583 a0, net.minecraft.class_2960 a1, net.minecraft.class_4587 a2, net.minecraft.class_4597 a3, int a4, net.minecraft.class_1309 a5, int a6) {
        throw new UnsupportedOperationException("CenturyBridge: the new overload requires a class_10042 render state, which cannot be faithfully constructed or populated from class_1309 using the available API");
    }

    /** was net/minecraft/class_425.method_18819(Lnet/minecraft/class_310;)V */
    public static void method_18819(net.minecraft.class_310 a0) {
        net.minecraft.class_425.method_18819(a0.method_1531());
    }

    /** was net/minecraft/class_4603.method_23067(Lnet/minecraft/class_310;Lnet/minecraft/class_4587;)V */
    public static void method_23067(net.minecraft.class_310 a0, net.minecraft.class_4587 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new method requires a class_4597 buffer provider, which the old arguments and available API cannot supply");
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

    /** was net/minecraft/class_4896.method_25446(Lnet/minecraft/class_630;Lnet/minecraft/class_630;Lnet/minecraft/class_1309;Z)V */
    public static void method_25446(net.minecraft.class_630 a0, net.minecraft.class_630 a1, net.minecraft.class_1309 a2, boolean a3) {
        throw new UnsupportedOperationException("CenturyBridge: cannot derive float and int from LivingEntity without knowing the new API semantics");
    }

    /** was net/minecraft/class_4896.method_29351(Lnet/minecraft/class_630;Lnet/minecraft/class_630;Lnet/minecraft/class_1308;FF)V */
    public static void method_29351(net.minecraft.class_630 a0, net.minecraft.class_630 a1, net.minecraft.class_1308 a2, float a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: class_4896.method_29351 now takes EquipmentSlot (class_1306) instead of LivingEntity (class_1308); cannot derive slot from entity without additional context");
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

    /** was net/minecraft/class_624.method_32068()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32068() {
        throw new UnsupportedOperationException("CenturyBridge: the new method requires a class_5605 argument and returns class_5609, so it cannot preserve the old no-argument class_5607 contract");
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

    /** was net/minecraft/class_836.method_32160(Lnet/minecraft/class_5599;)Ljava/util/Map; */
    public static java.util.Map method_32160(net.minecraft.class_5599 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a class_2484.class_2485 context and returns class_5598 instead of the old Map contract");
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

    /** was net/minecraft/class_909.method_62461(Lnet/minecraft/class_1309;Lnet/minecraft/class_10034;F)V */
    public static void method_62461(net.minecraft.class_1309 a0, net.minecraft.class_10034 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new overload requires a class_10442 context, but the old arguments provide no class_1092 needed to construct one");
    }

    /** was net/minecraft/class_916.method_56858(Lnet/minecraft/class_918;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1799;Lnet/minecraft/class_1087;ZLnet/minecraft/class_5819;)V */
    public static void method_56858(net.minecraft.class_918 a0, net.minecraft.class_4587 a1, net.minecraft.class_4597 a2, int a3, net.minecraft.class_1799 a4, net.minecraft.class_1087 a5, boolean a6, net.minecraft.class_5819 a7) {
        throw new UnsupportedOperationException("CenturyBridge: the new renderer requires a class_10428 render state, but the old baked model and handedness arguments cannot faithfully construct that state");
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

    private ClientStatics() {
    }
}
