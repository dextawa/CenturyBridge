package top.dext.centurybridge.step.v1_21_2.rt;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.BlockModelDefinitionContext;

/**
 * 1.21.1's blockstate-file model (BlockModelDefinition / MultiPart / their deserializers) over 1.21.2's.
 *
 * <p>1.21.1 parsed a blockstate file against a block state definition (BlockModelDefinition.Context): the multipart
 * became a MultiPart -- an UnbakedModel holding the definition and its selectors (each a condition and a
 * MultiVariant) -- built while parsing. 1.21.2 parses the multipart as MultiPart.Definition (the selectors alone) and
 * builds the MultiPart (now an UnbakedBlockStateModel of predicate/variant pairs) with Definition.instantiate(state
 * definition) when the blockstate is loaded for a block.
 *
 * <p>Here a 1.21.1 MultiPart is 1.21.2's MultiPart carrying the definition and selectors it was built from
 * (CbRenderBlockDefMultiPartMixin; every MultiPart 1.21.2's Definition.instantiate builds gets them,
 * CbRenderBlockDefInstantiateMixin, and a mod's own new MultiPart(definition, selectors) is built from the same
 * pairs). A BlockModelDefinition remembers the state definition it was parsed against and the 1.21.1 MultiPart it
 * answers (CbRenderBlockDefMixin), which 1.21.1 built once and kept. Where a 1.21.1 MultiPart goes into 1.21.2 as a
 * Definition (a mod's constructor, a mod's override answering one), the Definition made from its selectors is tied
 * back to that object, so the definition answers it again rather than a copy.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBlockDefs {

    /** BlockModelDefinition's 1.21.1 state (CbRenderBlockDefMixin) */
    public interface Def {
        net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> cb$stateDefinition();

        void cb$setStateDefinition(net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition);

        net.minecraft.class_816 cb$multiPart();

        void cb$setMultiPart(net.minecraft.class_816 multiPart);

        /** 1.21.2's field: the parsed multipart selectors, or null */
        net.minecraft.class_816.class_9982 cb$definition();

        /** 1.21.2's field: the variants */
        Map<String, net.minecraft.class_807> cb$variants();
    }

    /** MultiPart's 1.21.1 state (CbRenderBlockDefMultiPartMixin) */
    public interface Part {
        net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> cb$stateDefinition();

        List<net.minecraft.class_819> cb$selectors();

        void cb$attach(net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, List<net.minecraft.class_819> selectors);
    }

    /** MultiPart.Deserializer's 1.21.1 context (CbRenderBlockDefDeserializerMixin); null for one 1.21.2 built */
    public interface Deser {
        BlockModelDefinitionContext cb$context();

        void cb$setContext(BlockModelDefinitionContext context);

        /** 1.21.2's private getSelectors(context, json) */
        List<net.minecraft.class_819> cb$getSelectors(JsonDeserializationContext context, com.google.gson.JsonArray json);
    }

    /** the 1.21.1 MultiPart a Definition stands for, where one went into 1.21.2 as that Definition (weak, identity) */
    private static final Map<net.minecraft.class_816.class_9982, net.minecraft.class_816> PARTS = new MapMaker().weakKeys().makeMap();

    private CbRenderBlockDefs() {
    }

    // ---- MultiPart ----

    /** 1.21.2's MultiPart for selectors over a definition, carrying both (Definition.instantiate's own construction) */
    public static net.minecraft.class_816 instantiate(net.minecraft.class_816.class_9982 definition,
                                                      net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> stateDefinition) {
        return definition.method_62339(stateDefinition);
    }

    /** the 1.21.1 MultiPart a Definition stands for: the object it was made from, else one built against the state definition */
    public static net.minecraft.class_816 partOf(net.minecraft.class_816.class_9982 definition,
                                                 net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> stateDefinition) {
        if (definition == null) {
            return null;
        }
        net.minecraft.class_816 part = PARTS.get(definition);
        if (part != null) {
            return part;
        }
        if (stateDefinition == null) {
            throw new IllegalStateException("1.21.1 MultiPart asked of a blockstate multipart parsed without a block state definition (1.21.1 built every MultiPart against one)");
        }
        return instantiate(definition, stateDefinition);
    }

    /** a 1.21.1 MultiPart as the Definition 1.21.2 takes (its selectors), tied back to it */
    public static net.minecraft.class_816.class_9982 definitionOf(net.minecraft.class_816 part) {
        if (part == null) {
            return null;
        }
        net.minecraft.class_816.class_9982 definition = new net.minecraft.class_816.class_9982(((Part) part).cb$selectors());
        PARTS.put(definition, part);
        return definition;
    }

    /** new MultiPart(definition, selectors) / super(definition, selectors): 1.21.2's predicate/variant pairs, as instantiate builds them */
    public static List<?> parts(net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, List<net.minecraft.class_819> selectors) {
        List<net.minecraft.class_816.class_9983> parts = new ArrayList<>(selectors.size());
        for (net.minecraft.class_819 selector : selectors) {
            parts.add(new net.minecraft.class_816.class_9983(selector.method_3530(definition), selector.method_3529()));
        }
        return List.copyOf(parts);
    }

    /** after new MultiPart(definition, selectors): the 1.21.1 state it keeps */
    public static void attach(net.minecraft.class_816 part, net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition,
                              List<net.minecraft.class_819> selectors) {
        ((Part) part).cb$attach(definition, selectors);
    }

    // ---- BlockModelDefinition ----

    /** fromStream(context, reader): 1.21.2's parse, the multipart built against the context's definition as 1.21.1 did */
    public static net.minecraft.class_790 method_3424(BlockModelDefinitionContext context, Reader reader) {
        return parsed(net.minecraft.class_790.method_3424(reader), context);
    }

    /** fromJsonElement(context, json) */
    public static net.minecraft.class_790 method_45787(BlockModelDefinitionContext context, JsonElement json) {
        return parsed(net.minecraft.class_790.method_45787(json), context);
    }

    private static net.minecraft.class_790 parsed(net.minecraft.class_790 model, BlockModelDefinitionContext context) {
        Def def = (Def) model;
        net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> state = context.method_3425();
        def.cb$setStateDefinition(state);
        if (def.cb$definition() != null) {
            def.cb$setMultiPart(partOf(def.cb$definition(), state));
        }
        return model;
    }

    /**
     * getMultiPart(), a virtual call: 1.21.2's getMultiPart() (a mod class's 1.21.1 override answers it through its
     * override hook, tying its MultiPart to the Definition) as 1.21.1's MultiPart
     */
    public static net.minecraft.class_816 getMultiPart(net.minecraft.class_790 model) {
        net.minecraft.class_816.class_9982 definition = model.method_3421();
        Def def = (Def) model;
        if (definition == null) {
            return null;
        }
        if (definition == def.cb$definition()) {
            return superGetMultiPart(model);
        }
        return partOf(definition, def.cb$stateDefinition());
    }

    /** 1.21.1 BlockModelDefinition's own getMultiPart() (a mod's super call; reads the instance): its MultiPart, built once */
    public static net.minecraft.class_816 superGetMultiPart(net.minecraft.class_790 model) {
        Def def = (Def) model;
        net.minecraft.class_816 part = def.cb$multiPart();
        if (part == null && def.cb$definition() != null) {
            part = partOf(def.cb$definition(), def.cb$stateDefinition());
            def.cb$setMultiPart(part);
        }
        return part;
    }

    /** new BlockModelDefinition(variants, multiPart): 1.21.1 copied the variants into its own map */
    public static Map<String, net.minecraft.class_807> variants(Map<String, net.minecraft.class_807> variants) {
        return new LinkedHashMap<>(variants);
    }

    /** after new BlockModelDefinition(variants, multiPart) */
    public static void defined(net.minecraft.class_790 model, net.minecraft.class_816 part) {
        Def def = (Def) model;
        def.cb$setMultiPart(part);
        if (part != null) {
            def.cb$setStateDefinition(((Part) part).cb$stateDefinition());
        }
    }

    /**
     * new BlockModelDefinition(List): 1.21.1 merged the definitions in order -- each one's variants added, and a
     * multipart definition (isMultiPart(), a virtual call) clearing the variants before it and taking over the multipart
     */
    public static Map<String, net.minecraft.class_807> mergedVariants(List<net.minecraft.class_790> models) {
        Map<String, net.minecraft.class_807> variants = new LinkedHashMap<>();
        for (net.minecraft.class_790 model : models) {
            if (((Legacy) model).method_3422()) {
                variants.clear();
            }
            variants.putAll(((Def) model).cb$variants());
        }
        return variants;
    }

    /** the merged multipart's Definition (see {@link #mergedVariants}) */
    public static net.minecraft.class_816.class_9982 mergedDefinition(List<net.minecraft.class_790> models) {
        return definitionOf(mergedPart(models));
    }

    /** after new BlockModelDefinition(List) */
    public static void merged(net.minecraft.class_790 model, List<net.minecraft.class_790> models) {
        defined(model, mergedPart(models));
    }

    private static net.minecraft.class_816 mergedPart(List<net.minecraft.class_790> models) {
        net.minecraft.class_816 part = null;
        for (net.minecraft.class_790 model : models) {
            if (((Legacy) model).method_3422()) {
                part = superGetMultiPart(model);
            }
        }
        return part;
    }

    /** BlockModelDefinition's 1.21.1 isMultiPart(), as CbRenderBlockDefMixin declares it (a mod may override it) */
    public interface Legacy {
        boolean method_3422();
    }

    // ---- the deserializers ----

    /** after new MultiPart.Deserializer(context) */
    public static void deserializer(net.minecraft.class_816.class_817 deserializer, BlockModelDefinitionContext context) {
        ((Deser) deserializer).cb$setContext(context);
    }

    private static net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> stateOf(net.minecraft.class_816.class_817 deserializer) {
        BlockModelDefinitionContext context = ((Deser) deserializer).cb$context();
        return context == null ? null : context.method_3425();
    }

    /** MultiPart.Deserializer.deserialize(json, type, context) answering 1.21.1's MultiPart */
    public static net.minecraft.class_816 deserializePart(net.minecraft.class_816.class_817 deserializer, JsonElement json, Type type, JsonDeserializationContext context) {
        return partOf(deserializer.method_3523(json, type, context), stateOf(deserializer));
    }

    /** a mod deserializer's super.deserialize(..): 1.21.1's body, new MultiPart(context.getDefinition(), getSelectors(..)) */
    public static net.minecraft.class_816 superDeserializePart(net.minecraft.class_816.class_817 deserializer, JsonElement json, Type type, JsonDeserializationContext context) {
        List<net.minecraft.class_819> selectors = ((Deser) deserializer).cb$getSelectors(context, json.getAsJsonArray());
        return instantiate(new net.minecraft.class_816.class_9982(selectors), stateOf(deserializer));
    }

    /** the deserializer's JsonDeserializer.deserialize answer: 1.21.1's MultiPart for one built with a 1.21.1 context */
    public static Object deserialized(net.minecraft.class_816.class_817 deserializer, Object answer) {
        if (((Deser) deserializer).cb$context() == null || !(answer instanceof net.minecraft.class_816.class_9982 definition)) {
            return answer;
        }
        return partOf(definition, stateOf(deserializer));
    }

    /**
     * BlockModelDefinition.Deserializer.getMultiPart(context, json) in 1.21.1's shape (also a mod's super call): 1.21.1's
     * body, the "multipart" array deserialized as a MultiPart through the Gson the caller's context belongs to
     */
    public static net.minecraft.class_816 method_3427(net.minecraft.class_790.class_792 deserializer, JsonDeserializationContext context, JsonObject json) {
        if (json.has("multipart")) {
            return (net.minecraft.class_816) context.deserialize(net.minecraft.class_3518.method_15261(json, "multipart"), net.minecraft.class_816.class);
        }
        return null;
    }
}
