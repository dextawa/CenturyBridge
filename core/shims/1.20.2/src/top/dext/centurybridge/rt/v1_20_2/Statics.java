package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1856;

/**
 * Caller-side homes for dead STATIC methods (Mixin cannot add non-private
 * statics to targets). The converter rewrites old invokestatic call sites to
 * point here; signatures must match the dead originals exactly.
 */
public final class Statics {

    /** 1.20.1 Ingredient.fromJson(JsonElement), removed 1.20.2 (codec era). L3 x29. */
    public static class_1856 method_52177(JsonElement json) {
        return class_1856.field_46095.parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    /** 1.20.1 ShapedRecipe.outputFromJson (item/count/nbt legacy format), removed 1.20.2. L3 x11. */
    public static net.minecraft.class_1799 method_35228(com.google.gson.JsonObject json) {
        String itemId = net.minecraft.class_3518.method_15265(json, "item");
        net.minecraft.class_1792 item = net.minecraft.class_7923.field_41178
            .method_17966(new net.minecraft.class_2960(itemId))
            .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + itemId + "'"));
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(
            item, net.minecraft.class_3518.method_15282(json, "count", 1));
        if (json.has("nbt")) {
            try {
                stack.method_7980(net.minecraft.class_2522.method_10718(
                    net.minecraft.class_3518.method_15265(json, "nbt")));
            } catch (Exception e) {
                throw new JsonSyntaxException("Invalid nbt on output stack", e);
            }
        }
        return stack;
    }

    /** 1.20.1 FluidDrainable.tryDrainFluid gained a leading @Nullable player param in 1.20.2.
     *  Interface methods cannot be re-added via Mixin; call sites are redirected here. L2 x19. */
    public static net.minecraft.class_1799 method_9700(net.minecraft.class_2263 self,
            net.minecraft.class_1936 world, net.minecraft.class_2338 pos, net.minecraft.class_2680 state) {
        return self.method_9700(null, world, pos, state);
    }

    /** 1.20.1 Ingredient.fromJson(json, allowEmpty), removed 1.20.2. L3 x9. */
    public static class_1856 method_8102(JsonElement json, boolean allowEmpty) {
        var codec = allowEmpty ? class_1856.field_46095 : class_1856.field_46096;
        return codec.parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    /** 1.20.1 Fertilizable.canGrow lost its trailing client-side flag in 1.20.2. Interface -> call-site redirect. L2 x9. */
    public static boolean method_9651(net.minecraft.class_2256 self, net.minecraft.class_4538 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, boolean isClient) {
        return self.method_9651(world, pos, state);
    }

    /** legacy Recipe.getId(): ids moved to RecipeEntry in 1.20.2; the recipe's own getId() or its recorded id (CbRecipeIds). */
    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1860<?> self) {
        return CbRecipeIds.idOf(self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1869 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3955 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    /** 1.20.1 ContextAwarePredicate.ANY: the one sentinel CbAdvAny keeps for it (fieldRedirects point there) */
    public static final net.minecraft.class_5258 field_24388 = CbAdvAny.field_24388;

    /** 1.20.1 CriteriaTriggers.register(trigger) (Fabric API's CriterionRegistry compat calls it): CbAdvTriggers */
    public static net.minecraft.class_179<?> method_767(net.minecraft.class_179<?> trigger) {
        return CbAdvTriggers.method_767(trigger);
    }

    /** 1.20.1 RecipeSerializer.read(id, json): parsing moved to codec(). A converted 1.20.1
     *  serializer keeps its own reader; any other is parsed through its codec with the id
     *  published, so a legacy serializer nested inside still finds it. L3 x7. */
    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.class_1860<?>> T method_8121(
            net.minecraft.class_1865<T> self, net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        if (self instanceof CbLegacyRecipeSerializer legacy) {
            return (T) legacy.method_8121(id, json);
        }
        return CbRecipeSerializerSupport.decode(self.method_53736(), id, json);
    }

    /** 1.20.1 RecipeSerializer.read(id, buf): the id parameter is gone; a converted 1.20.1
     *  serializer keeps its own reader. L2 x6. */
    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.class_1860<?>> T method_8122(
            net.minecraft.class_1865<T> self, net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        if (self instanceof CbLegacyRecipeSerializer legacy) {
            return (T) legacy.method_8122(id, buf);
        }
        return CbRecipeSerializerSupport.withId(id, () -> self.method_8122(buf));
    }

    /** 1.20.1 ItemPredicate.fromJson: return became Optional in 1.20.2; old callers expect a
     *  match-anything predicate for absent json (the ANY constant is gone -> empty builder). L3 x6. */
    public static net.minecraft.class_2073 method_8969(JsonElement json) {
        return net.minecraft.class_2073.method_8969(json)
            .orElseGet(() -> net.minecraft.class_2073.class_2074.method_8973().method_8976());
    }

    /** 1.20.1 RecipeManager.deserialize: became protected and returns RecipeEntry. L3 x4. */
    public static net.minecraft.class_1860<?> method_17720(net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        try {
            var m = net.minecraft.class_1863.class.getDeclaredMethod(
                "method_17720", net.minecraft.class_2960.class, com.google.gson.JsonObject.class);
            m.setAccessible(true);
            net.minecraft.class_8786<?> entry = (net.minecraft.class_8786<?>) m.invoke(null, id, json);
            return CbRecipeIds.value(entry);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("CenturyBridge: recipe deserialize failed for " + id, e);
        }
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1867 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    /** 1.20.1 LocationPredicate.ANY: CbAdvAny's sentinel */
    public static final net.minecraft.class_2090 field_9685 = CbAdvAny.field_9685;

    /** 1.20.1 LocationPredicate.fromJson (same Optional-ization pattern as ItemPredicate). */
    public static net.minecraft.class_2090 method_9021(JsonElement json) {
        return net.minecraft.class_2090.method_9021(json).orElse(field_9685);
    }

    /** Optional-ized fromJson batch (1.20.2 predicate rework); invalid json throws like the old code did. */
    public static net.minecraft.class_2019 method_8839(JsonElement json) {
        return net.minecraft.class_2019.method_8839(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid damage predicate: " + json));
    }

    public static net.minecraft.class_2022 method_8846(JsonElement json) {
        return net.minecraft.class_2022.method_8846(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid damage source predicate: " + json));
    }

    public static net.minecraft.class_2025 method_8857(JsonElement json) {
        return net.minecraft.class_2025.method_8857(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid distance predicate: " + json));
    }

    public static net.minecraft.class_2048 method_8913(JsonElement json) {
        return net.minecraft.class_2048.method_8913(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid entity predicate: " + json));
    }

    public static net.minecraft.class_5258 method_51705(com.google.gson.JsonObject json, String key,
            net.minecraft.class_5257 deserializer) {
        return net.minecraft.class_2048.method_51705(json, key, deserializer)
            .orElse(field_24388);
    }

    /** 1.20.1's ItemPredicate / EntityPredicate / NbtPredicate ANY: CbAdvAny's sentinels */
    public static final net.minecraft.class_2073 field_9640 = CbAdvAny.field_9640;

    public static final net.minecraft.class_2048 field_9599 = CbAdvAny.field_9599;

    public static final net.minecraft.class_2105 field_9716 = CbAdvAny.field_9716;

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1874 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3862 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3861 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3920 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static boolean method_9651(net.minecraft.class_2302 self, net.minecraft.class_4538 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, boolean isClient) {
        return ((net.minecraft.class_2256) self).method_9651(world, pos, state);
    }

    /** 1.20.1 Waterloggable.tryFillWithFluid gained a leading player param (same pattern as FluidDrainable). */
    public static boolean method_10310(net.minecraft.class_2402 self, net.minecraft.class_1922 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_3611 fluid) {
        return self.method_10310(null, world, pos, state, fluid);
    }

    public static boolean method_10310(net.minecraft.class_3737 self, net.minecraft.class_1922 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_3611 fluid) {
        return ((net.minecraft.class_2402) self).method_10310(null, world, pos, state, fluid);
    }

    // ==== GENERATED BRIDGES (assemble.py) ====



























    /** was net/minecraft/class_2319.<init>(Ljava/util/function/Supplier;)V */
    public static net.minecraft.class_2319 cbNewclass_2319(java.util.function.Supplier a0) {
        return net.minecraft.class_2319.method_41999(a0);
    }

    /** was net/minecraft/class_2505.<init>(J)V -- NbtAccounter(maxBytes); 1.20.2's of(maxBytes) adds the
     *  depth limit of 512 that every 1.20.1 reader checked itself */
    public static net.minecraft.class_2505 cbNewclass_2505(long a0) {
        return net.minecraft.class_2505.method_53899(a0);
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;[Ljava/lang/Object;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0, java.lang.Object[] a1) {
        return new net.minecraft.class_2588(a0, a0, a1);
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0) {
        return new net.minecraft.class_2588(a0, a0, new Object[0]);
    }

    /** was net/minecraft/class_2915.<init>(Ljava/lang/String;Ljava/util/Optional;)V */
    public static net.minecraft.class_2915 cbNewclass_2915(java.lang.String a0, java.util.Optional a1) {
        return new net.minecraft.class_2915(a0, (java.util.UUID) a1.orElse(null));
    }

    /** was net/minecraft/class_3222.<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/class_3218;Lcom/mojang/authlib/GameProfile;)V */
    public static net.minecraft.class_3222 cbNewclass_3222(net.minecraft.server.MinecraftServer a0, net.minecraft.class_3218 a1, com.mojang.authlib.GameProfile a2) {
        return new net.minecraft.class_3222(a0, a1, a2, net.minecraft.class_8791.method_53821());
    }

    /** was net/minecraft/class_3259.<init>(Ljava/io/File;)V */
    public static net.minecraft.class_3259 cbNewclass_3259(java.io.File a0) {
        return new net.minecraft.class_3259(a0.getName(), a0.toPath(), false);
    }

    /** was net/minecraft/class_3272.<init>(Lnet/minecraft/class_2561;I)V */
    public static net.minecraft.class_3272 cbNewclass_3272(net.minecraft.class_2561 a0, int a1) {
        return new net.minecraft.class_3272(a0, a1, java.util.Optional.empty());
    }

    /** was net/minecraft/class_3283.<init>(Lnet/minecraft/class_3264;[Lnet/minecraft/class_3285;)V */
    public static net.minecraft.class_3283 cbNewclass_3283(net.minecraft.class_3264 a0, net.minecraft.class_3285[] a1) {
        return new net.minecraft.class_3283(a1);
    }

    /** was net/minecraft/class_3288.<init>(Ljava/lang/String;Lnet/minecraft/class_2561;ZLjava/util/function/Supplier;Lnet/minecraft/class_3272;Lnet/minecraft/class_3264;Lnet/minecraft/class_3288$class_3289;Lnet/minecraft/class_5352;)V */
    public static net.minecraft.class_3288 cbNewclass_3288(java.lang.String a0, net.minecraft.class_2561 a1, boolean a2, java.util.function.Supplier a3, net.minecraft.class_3272 a4, net.minecraft.class_3264 a5, net.minecraft.class_3288.class_3289 a6, net.minecraft.class_5352 a7) {
        return net.minecraft.class_3288.method_45275(a0, a1, a2, new net.minecraft.class_3288.class_7680() {
            public net.minecraft.class_3262 method_52424(java.lang.String name) {
                return (net.minecraft.class_3262) a3.get();
            }
        
            public net.minecraft.class_3262 method_52425(java.lang.String name, net.minecraft.class_3288.class_7679 metadata) {
                return (net.minecraft.class_3262) a3.get();
            }
        }, a5, a6, a7);
    }

    /** was net/minecraft/class_329.field_44654Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44654() {
        return new net.minecraft.class_2960("textures/gui/icons.png");
    }

    /** was net/minecraft/class_339.field_22757Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22757() {
        return new net.minecraft.class_2960("textures/gui/widgets.png");
    }










    // ==== END GENERATED ====

    // ---- 1.20.2 loot: the condition / function arrays became Lists ----

    /** ConditionalLootFunction / LootPoolEntry super(conditions[]) -> super(List) */
    public static java.util.List<net.minecraft.class_5341> cbConditionList(net.minecraft.class_5341[] conditions) {
        return java.util.List.of(conditions);
    }

    /** LeafEntry super(.., functions[]) -> super(.., List) */
    public static java.util.List<net.minecraft.class_117> cbFunctionList(net.minecraft.class_117[] functions) {
        return java.util.List.of(functions);
    }

    /** a mod reading LootPoolEntry.conditions (an array in 1.20.1) */
    public static net.minecraft.class_5341[] field_988(net.minecraft.class_79 self) {
        return ((top.dext.centurybridge.shims.v1_20_2.LootPoolEntryAccessor) self).cb$conditions()
            .toArray(new net.minecraft.class_5341[0]);
    }

    // ---- 1.20.1 JsonHelper typed item getters, gone with the codec rework; bodies ported ----

    /** JsonHelper.asItem(element, name) */
    public static net.minecraft.class_1792 method_15256(JsonElement element, String name) {
        if (element.isJsonPrimitive()) {
            String id = element.getAsString();
            return net.minecraft.class_7923.field_41178.method_17966(new net.minecraft.class_2960(id))
                .orElseThrow(() -> new JsonSyntaxException("Expected " + name + " to be an item, was unknown string '" + id + "'"));
        }
        throw new JsonSyntaxException("Expected " + name + " to be an item, was " + net.minecraft.class_3518.method_15266(element));
    }

    /** JsonHelper.getItem(json, name) */
    public static net.minecraft.class_1792 method_15288(com.google.gson.JsonObject json, String name) {
        if (json.has(name)) {
            return method_15256(json.get(name), name);
        }
        throw new JsonSyntaxException("Missing " + name + ", expected to find an item");
    }

    /** JsonHelper.getItem(json, name, fallback) */
    public static net.minecraft.class_1792 method_34916(com.google.gson.JsonObject json, String name, net.minecraft.class_1792 fallback) {
        return json.has(name) ? method_15256(json.get(name), name) : fallback;
    }

    private Statics() {
    }

    // =====================================================================
    // 1.20.2 kill-list closure batch (hand-written). Intermediary names;
    // bodies target the surviving 1.20.2 API.
    // =====================================================================

    // ---- Player skins: 1.20.2 folded the per-texture getters into the SkinTextures record
    // (class_8685: texture, textureUrl, cape, elytra, model, secure).

    // ---- ClientWorld.addEntity(int id, Entity) -> addEntity(Entity): the id is already on the entity.

    // ---- Advancements: 1.20.2 wraps Advancement in AdvancementEntry(id, advancement) and the
    // client tree in PlacedAdvancement.
    /** was net/minecraft/class_161$class_162.method_695(Lnet/minecraft/class_2960;)Lnet/minecraft/class_161; Builder.build(id) */
    public static net.minecraft.class_161 method_695(net.minecraft.class_161.class_162 self, net.minecraft.class_2960 id) {
        return self.method_695(id).comp_1920();
    }
    /** was net/minecraft/class_161$class_162.method_710()Ljava/util/Map; Builder.getCriteria (accessor on the ImmutableMap builder) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Map method_710(net.minecraft.class_161.class_162 self) {
        return ((top.dext.centurybridge.shims.v1_20_2.CbAdvancementBuilderCriteria) (Object) self).cbCriteria().build();
    }
    /** was net/minecraft/class_163.method_712()Ljava/util/Collection; AdvancementManager.getAdvancements */
    public static java.util.Collection<net.minecraft.class_161> method_712(net.minecraft.class_163 self) {
        java.util.List<net.minecraft.class_161> out = new java.util.ArrayList<>();
        for (net.minecraft.class_8781 p : self.method_53693()) out.add(p.method_53647());
        return out;
    }

    // ---- Registry-derived names that lost their getters.
    /** was net/minecraft/class_173.method_762(Lnet/minecraft/class_176;)Lnet/minecraft/class_2960; LootContextTypes.getId (registry map via accessor) */
    public static net.minecraft.class_2960 method_762(net.minecraft.class_176 set) {
        return top.dext.centurybridge.shims.v1_20_2.CbLootContextTypesAccessor.cbRegistry().inverse().get(set);
    }

    // ---- Predicates / criteria: builders became Optional-typed, factories return AdvancementCriterion.
    /** was net/minecraft/class_2090.method_9017(Lnet/minecraft/class_5321;)Lnet/minecraft/class_2090; LocationPredicate.feature(structure) */
    public static net.minecraft.class_2090 method_9017(net.minecraft.class_5321<net.minecraft.class_3195> key) {
        return net.minecraft.class_2090.class_2091.method_53183(key).method_9023();
    }
    /** was net/minecraft/class_2724.method_11779()Lnet/minecraft/class_5321; PlayerRespawnS2CPacket.getDimension */
    public static net.minecraft.class_5321<net.minecraft.class_1937> method_11779(net.minecraft.class_2724 self) { return self.comp_1728().comp_1554(); }
    /** was net/minecraft/class_3898.method_39975(IIIII)Z ChunkMap.isChunkInRange (1.20.1 algorithm) */
    public static boolean method_39975(int x1, int z1, int x2, int z2, int distance) {
        int i = Math.max(0, Math.abs(x1 - x2) - 1);
        int j = Math.max(0, Math.abs(z1 - z2) - 1);
        long l = Math.max(0, Math.max(i, j) - 1);
        long m = Math.min(i, j);
        long n = m * m + l * l;
        int k = distance * distance;
        return n < (long) k;
    }

    // ---- super(...) argument adapters for 1.20.2 constructor reshapes (engine superCtorAdapts:
    // a static converter runs on the stack tail right before INVOKESPECIAL <init>).
    /** ServerPlayerEntity(server, world, profile) gained SyncedClientOptions */
    public static net.minecraft.class_8791 cbDefaultClientOptions() { return net.minecraft.class_8791.method_53821(); }

    // ---- static-signature migrations: old return type recovered by unwrapping
    //      the 1.20.2 wrapper record (wiring-check closure cycle 64) ----

    /** 1.20.1 SynchronizeRecipesS2CPacket.readRecipe(buf) returned the Recipe directly;
     *  1.20.2 made it private and returns a RecipeEntry (class_8786). The @Invoker on
     *  GenClass2788Bridge reaches the private reader; comp_1933() is the wrapped recipe. */
    public static net.minecraft.class_1860<?> method_17817(net.minecraft.class_2540 buf) {
        net.minecraft.class_8786<?> entry =
            top.dext.centurybridge.shims.v1_20_2.GenClass2788Bridge.cbReadRecipeEntry(buf);
        return entry == null ? null : entry.comp_1933();
    }

    // ---- 1.20.1 -> 1.20.2 dead constructors (wiring-check closure cycle 62) ----

    /** was ServerboundCustomPayloadPacket(Identifier, PacketByteBuf): 1.20.2 packets
     *  carry a CustomPayload object. CbRawPayload wraps the old id + already-encoded
     *  bytes verbatim, byte-for-byte what the 1.20.1 packet put on the wire. */
    public static net.minecraft.class_2817 cbNewclass_2817(net.minecraft.class_2960 id, net.minecraft.class_2540 data) {
        return new net.minecraft.class_2817(new CbRawPayload(id, data));
    }

    /** LootConditionType(JsonSerializer) -> Codec: a legacy serializer is a codec in
     *  disguise (CbLootJson bridges toJson/fromJson both ways). */
    public static net.minecraft.class_5342 cbNewclass_5342(top.dext.centurybridge.facade.v1_20_2.class_5335 serializer) {
        return new net.minecraft.class_5342(CbLootJson.codec(serializer));
    }

    /** LootFunctionType(JsonSerializer) -> Codec */
    public static net.minecraft.class_5339 cbNewclass_5339(top.dext.centurybridge.facade.v1_20_2.class_5335 serializer) {
        return new net.minecraft.class_5339(CbLootJson.codec(serializer));
    }

    /** LootPoolEntryType(JsonSerializer) -> Codec */
    public static net.minecraft.class_5338 cbNewclass_5338(top.dext.centurybridge.facade.v1_20_2.class_5335 serializer) {
        return new net.minecraft.class_5338(CbLootJson.codec(serializer));
    }

    // ---- unbridgeable dead ctors: tombstone stubs (mod loads; lazy-fails only if
    //      a mod actually constructs these vanilla-internal screens/packets) ----

    /** SimpleOptionsScreen / CreditsScreen ctors are unbridgeable (deleted/package-private
     *  arg types); their ctorRedirect entries were removed and ledgered instead. */

    /** was net/minecraft/class_2102.method_9064(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2102; */
    public static net.minecraft.class_2102 method_9064(com.google.gson.JsonElement a0) {
        return net.minecraft.class_2102.method_9064(a0).orElseGet(() -> new net.minecraft.class_2102(java.util.Map.of()));
    }

    /** was net/minecraft/class_2535.method_10753(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/class_2535; */
    public static net.minecraft.class_2535 method_10753(java.net.InetSocketAddress a0, boolean a1) {
        return net.minecraft.class_2535.method_10753(a0, a1, null);
    }

}
