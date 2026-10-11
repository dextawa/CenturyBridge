package top.dext.centurybridge.rt.v1_20_4;

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

    /** was net/minecraft/class_1145.method_4884(Lnet/minecraft/class_1113;Lnet/minecraft/class_1146;)V */
    public void method_4884(net.minecraft.class_1113 a0, net.minecraft.class_1146 a1) {
        ((net.minecraft.class_1145) (Object) this).method_4884(a0, a1, 1.0f);
    }

    /** was net/minecraft/class_1921.method_23585()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23585(net.minecraft.class_1921 self) {
        return net.minecraft.class_1921.method_23584(new net.minecraft.class_2960("textures/misc/enchanted_glint_item.png"));
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
        self.method_52708(a0, a1, a2, a3, a4, a5, a6, a7, a8);
    }

    /** was net/minecraft/class_332.method_48586(Lnet/minecraft/class_2960;IIIIIIIII)V */
    public static void method_48586(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
        self.method_52709(a0, a1, a2, a3, a4, a5, a6, a7, a8, a9);
    }

    /** was net/minecraft/class_332.method_48587(Lnet/minecraft/class_2960;IIIIIIIIIIII)V */
    public static void method_48587(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9, int a10, int a11, int a12) {
        int i = Math.min(a5, a3 / 2);
        int j = Math.min(a5, a3 / 2);
        int k = Math.min(a6, a4 / 2);
        int l = Math.min(a6, a4 / 2);
        if (a3 == a5 && a4 == a6) {
            self.method_25290(a0, a1, a2, (float)a9, (float)a10, a3, a4, a11, a12);
        } else if (a4 == a6) {
            self.method_25293(a0, a1, a2, i, a4, (float)a9, (float)a10, i, a4, a11, a12);
            self.method_25293(a0, a1 + i, a2, a3 - i - j, a4, (float)(a9 + a5), (float)a10, a7, a8, a11, a12);
            self.method_25293(a0, a1 + a3 - j, a2, j, a4, (float)(a9 + a5 + a7), (float)a10, j, a4, a11, a12);
        } else if (a3 == a5) {
            self.method_25293(a0, a1, a2, a3, k, (float)a9, (float)a10, a3, k, a11, a12);
            self.method_25293(a0, a1, a2 + k, a3, a4 - k - l, (float)a9, (float)(a10 + a6), a7, a8, a11, a12);
            self.method_25293(a0, a1, a2 + a4 - l, a3, l, (float)a9, (float)(a10 + a6 + a8), a3, l, a11, a12);
        } else {
            self.method_25293(a0, a1, a2, i, k, (float)a9, (float)a10, i, k, a11, a12);
            self.method_25293(a0, a1 + i, a2, a3 - i - j, k, (float)(a9 + a5), (float)a10, a7, k, a11, a12);
            self.method_25293(a0, a1 + a3 - j, a2, j, k, (float)(a9 + a5 + a7), (float)a10, j, k, a11, a12);
            self.method_25293(a0, a1, a2 + k, i, a4 - k - l, (float)a9, (float)(a10 + a6), i, a8, a11, a12);
            self.method_25293(a0, a1 + i, a2 + k, a3 - i - j, a4 - k - l, (float)(a9 + a5), (float)(a10 + a6), a7, a8, a11, a12);
            self.method_25293(a0, a1 + a3 - j, a2 + k, j, a4 - k - l, (float)(a9 + a5 + a7), (float)(a10 + a6), j, a8, a11, a12);
            self.method_25293(a0, a1, a2 + a4 - l, i, l, (float)a9, (float)(a10 + a6 + a8), i, l, a11, a12);
            self.method_25293(a0, a1 + i, a2 + a4 - l, a3 - i - j, l, (float)(a9 + a5), (float)(a10 + a6 + a8), a7, l, a11, a12);
            self.method_25293(a0, a1 + a3 - j, a2 + a4 - l, j, l, (float)(a9 + a5 + a7), (float)(a10 + a6 + a8), j, l, a11, a12);
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

    /** was net/minecraft/class_364.method_25401(DDD)Z */
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

    /** was net/minecraft/class_632$class_633.method_2866(Lnet/minecraft/class_161;)V */
    public void method_2866(net.minecraft.class_161 a0) {
        ((net.minecraft.class_632.class_633) (Object) this).method_2866(new net.minecraft.class_8779(new net.minecraft.class_2960("centurybridge", "dummy"), a0));
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

    /**
     * superRedirects: a 1.20.1 override's super.render(context, mx, my, delta).
     * 1.20.1 Screen.render drew the widget list and nothing else; since 1.20.2
     * it draws the background first, over everything the mod just drew. Render
     * exactly what the old contract promised.
     */
    public static void cbRenderWidgets(net.minecraft.class_437 self, net.minecraft.class_332 context, int mouseX, int mouseY, float delta) {
        for (net.minecraft.class_4068 d : ((top.dext.centurybridge.shims.v1_20_4.ScreenDrawablesAccessor) self).cbDrawables()) {
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

    /** was net/minecraft/class_4185.<init>(IIIILnet/minecraft/class_2561;Lnet/minecraft/class_4185$class_4241;)V -- the ctor is
     *  protected since 1.19.3; Button.builder is the public path */
    public static net.minecraft.class_4185 cbNewclass_4185(int x, int y, int w, int h, net.minecraft.class_2561 text, net.minecraft.class_4185.class_4241 action) {
        return net.minecraft.class_4185.method_46430(text, action).method_46434(x, y, w, h).method_46431();
    }

    /** was net/minecraft/class_332.<init>()V -- a real DrawContext over the client's entity vertex consumers */
    public static net.minecraft.class_332 cbNewclass_332() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        return new net.minecraft.class_332(mc, mc.method_22940().method_23000());
    }

    /** was net/minecraft/class_746.method_3117()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3117(net.minecraft.class_746 self) { return self.method_52814().comp_1626(); }

    /** was net/minecraft/class_746.method_3121()Ljava/lang/String; */
    public static String method_3121(net.minecraft.class_746 self) { return self.method_52814().comp_1629().method_52856(); }

    /** was net/minecraft/class_742.method_3119()Lnet/minecraft/class_2960; getCapeTexture */
    public static net.minecraft.class_2960 method_3119(net.minecraft.class_742 self) { return self.method_52814().comp_1627(); }

    /** was net/minecraft/class_742.method_3122()Lnet/minecraft/class_2960; getElytraTexture */
    public static net.minecraft.class_2960 method_3122(net.minecraft.class_742 self) { return self.method_52814().comp_1628(); }

    /** was net/minecraft/class_742.method_3125()Z isCapeLoaded */
    public static boolean method_3125(net.minecraft.class_742 self) { return self.method_52814().comp_1627() != null; }

    /** was net/minecraft/class_742.method_3127()Z isSkinLoaded */
    public static boolean method_3127(net.minecraft.class_742 self) { return self.method_52814().comp_1626() != null; }

    /** was net/minecraft/class_640.method_2968()Lnet/minecraft/class_2960; PlayerListEntry.getSkinTexture */
    public static net.minecraft.class_2960 method_2968(net.minecraft.class_640 self) { return self.method_52810().comp_1626(); }

    /** was net/minecraft/class_640.method_2957()Lnet/minecraft/class_2960; getElytraTexture */
    public static net.minecraft.class_2960 method_2957(net.minecraft.class_640 self) { return self.method_52810().comp_1628(); }

    /** was net/minecraft/class_640.method_2979()Lnet/minecraft/class_2960; getCapeTexture */
    public static net.minecraft.class_2960 method_2979(net.minecraft.class_640 self) { return self.method_52810().comp_1627(); }

    /** was net/minecraft/class_1068.method_4647(Ljava/util/UUID;)Ljava/lang/String; DefaultSkinHelper.getModel */
    public static String method_4647(java.util.UUID uuid) { return net.minecraft.class_1068.method_4648(uuid).comp_1629().method_52856(); }

    /** was net/minecraft/class_320.method_1673()Ljava/lang/String; Session.getUuid (1.20.2 stores a UUID) */
    public static String method_1673(net.minecraft.class_320 self) { java.util.UUID u = self.method_44717(); return u == null ? "" : u.toString(); }

    /** was net/minecraft/class_320.method_1677()Lcom/mojang/authlib/GameProfile; Session.getProfile */
    public static com.mojang.authlib.GameProfile method_1677(net.minecraft.class_320 self) {
        return new com.mojang.authlib.GameProfile(self.method_44717(), self.method_1676());
    }

    /** was net/minecraft/class_638.method_2942(ILnet/minecraft/class_1297;)V -- ClientWorld.addEntity(id, entity):
     *  1.20.1 discarded whatever entity held that id, then added this one */
    public static void method_2942(net.minecraft.class_638 self, int id, net.minecraft.class_1297 e) {
        self.method_2945(id, net.minecraft.class_1297.class_5529.field_26999);
        self.method_53875(e);
    }

    /** was net/minecraft/class_632.method_2864(Lnet/minecraft/class_161;Z)V selectTab(Advancement, boolean): locate its entry in the tree */
    public static void method_2864(net.minecraft.class_632 self, net.minecraft.class_161 adv, boolean b) {
        for (net.minecraft.class_8781 p : self.method_53814().method_53693()) {
            if (p.method_53647() == adv) { self.method_2864(p.method_53649(), b); return; }
        }
        self.method_2864((net.minecraft.class_8779) null, b);
    }

    /** was net/minecraft/class_505.method_2565(Lnet/minecraft/class_1860;)V RecipeBookWidget.showGhostRecipe(Recipe): find its RecipeEntry */
    public static void method_2565(net.minecraft.class_505 self, net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_638 world = net.minecraft.class_310.method_1551().field_1687;
        if (world == null) return;
        for (net.minecraft.class_8786<?> e : world.method_8433().method_8126()) {
            if (e.comp_1933() == recipe) { self.method_2565(e); return; }
        }
    }

    /** was net/minecraft/class_332.method_49697(Lnet/minecraft/class_2960;IIIIIIIIII)V drawNineSlicedTexture(sideW, sideH) */
    public static void method_49697(net.minecraft.class_332 self, net.minecraft.class_2960 tex, int x, int y, int w, int h, int sw, int sh, int cw, int ch, int u, int v) {
        method_48587(self, tex, x, y, w, h, sw, sh, sw, sh, cw, ch, u, v);
    }

    /** ButtonWidget(x, y, w, h, text, action) gained a NarrationSupplier; the vanilla default narrates the supplied message */
    public static net.minecraft.class_4185.class_7841 cbDefaultNarration() { return s -> s.get(); }

    /** was net/minecraft/class_1071.method_4654(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map; PlayerSkinProvider.getTextures.
     *  Rebuilt from the synchronous SkinTextures: SKIN carries the real URL + model metadata; CAPE/ELYTRA
     *  entries are present when the texture is, with the registered texture id standing in for a URL. */
    public static java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, com.mojang.authlib.minecraft.MinecraftProfileTexture> method_4654(net.minecraft.class_1071 self, com.mojang.authlib.GameProfile profile) {
        net.minecraft.class_8685 t = self.method_52862(profile);
        java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, com.mojang.authlib.minecraft.MinecraftProfileTexture> m =
            new java.util.EnumMap<>(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.class);
        if (t.comp_1911() != null) {
            m.put(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN,
                new com.mojang.authlib.minecraft.MinecraftProfileTexture(t.comp_1911(), java.util.Map.of("model", t.comp_1629().method_52856())));
        }
        if (t.comp_1627() != null) {
            m.put(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.CAPE,
                new com.mojang.authlib.minecraft.MinecraftProfileTexture(t.comp_1627().toString(), null));
        }
        if (t.comp_1628() != null) {
            m.put(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA,
                new com.mojang.authlib.minecraft.MinecraftProfileTexture(t.comp_1628().toString(), null));
        }
        return m;
    }

    /** was net/minecraft/class_4722.method_48480()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_48480() {
        return net.minecraft.class_4722.method_48480(false);
    }

    /** was net/minecraft/class_7766.method_47664(Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_47664(java.util.List a0, java.util.concurrent.Executor a1) {
        return net.minecraft.class_7766.method_47664(net.minecraft.class_8684.create(a0), a0, a1);
    }

    /** was net/minecraft/class_8496.method_51260(Lnet/minecraft/class_310;Lnet/minecraft/class_542$class_8495;Lnet/minecraft/class_4011;Lnet/minecraft/class_4341;)V */
    public static void method_51260(net.minecraft.class_310 a0, net.minecraft.class_542.class_8495 a1, net.minecraft.class_4011 a2, net.minecraft.class_4341 a3) {
        net.minecraft.class_8496.method_51260(a0, a1, a3);
    }

    private ClientStatics() {
    }
}
