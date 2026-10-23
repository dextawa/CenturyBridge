package top.dext.centurybridge.step.v1_21_4.rt;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 client members of the last families on 1.21.4: model data generation, pick-from-inventory, vanilla particle render
 * types' begin.
 * <ul>
 * <li>Model generators: 1.21.3's wrote model files only (block items' as delegating item models, the items 1.21.3's provider then
 *     skipped); 1.21.4's also hand item definitions to an ItemModelOutput. A 1.21.3 generator gets an output that writes what
 *     1.21.3 wrote for it: nothing for ItemModelGenerators (its item models are model files already), a delegating item model and
 *     the skipped item for BlockModelGenerators -- a plain model reference that is not the item's own model file.</li>
 * <li>MultiPlayerGameMode.handlePickItem(slot): 1.21.3 asked the server to pick inventory slot {@code slot} (the server swapped it
 *     with the suitable hotbar slot and selected that); 1.21.4 has no such request, so the client makes the same swap with a SWAP
 *     click on the inventory menu and selects the slot, which 1.21.4's server applies the same way.</li>
 * <li>ParticleRenderType.begin(tesselator, textureManager): 1.21.4's types are records naming a RenderType; a 1.21.3 call on a
 *     vanilla type sets the state 1.21.3's did and begins the same buffer.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbClientRest {
    private CbClientRest() {
    }

    // ---- model generators ----

    /** ItemModelGenerators(output): 1.21.3's wrote no item definitions */
    public static net.minecraft.class_10405 cbNoItemDefinitions(BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> models) {
        return new net.minecraft.class_10405() {
            @Override
            public void method_65460(net.minecraft.class_1792 item, net.minecraft.class_10439.class_10441 model) {
            }

            @Override
            public void method_65459(net.minecraft.class_1792 from, net.minecraft.class_1792 to) {
            }
        };
    }

    /** BlockModelGenerators(blockStates, models, skippedAutoModels): 1.21.3's delegating item models and skipped items */
    public static net.minecraft.class_10405 cbDelegatingItemModels(BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> models,
        Consumer<net.minecraft.class_1792> skipped) {
        return new net.minecraft.class_10405() {
            @Override
            public void method_65460(net.minecraft.class_1792 item, net.minecraft.class_10439.class_10441 model) {
                if (model instanceof net.minecraft.class_10430.class_10431 plain) {
                    delegate(models, item, plain.comp_3383());
                }
                skipped.accept(item);
            }

            @Override
            public void method_65459(net.minecraft.class_1792 from, net.minecraft.class_1792 to) {
                delegate(models, to, net.minecraft.class_4941.method_25840(from));
                skipped.accept(to);
            }
        };
    }

    /** 1.21.3 BlockModelGenerators.delegateItemModel: the item's model file, parented to the model */
    private static void delegate(BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> models, net.minecraft.class_1792 item,
        net.minecraft.class_2960 model) {
        net.minecraft.class_2960 own = net.minecraft.class_4941.method_25840(item);
        if (own.equals(model)) {
            return; // the generator wrote the item's own model file
        }
        net.minecraft.class_10411 json = () -> {
            JsonObject o = new JsonObject();
            o.addProperty("parent", model.toString());
            return o;
        };
        models.accept(own, json);
    }

    private static final net.minecraft.class_2960 TRIM_TYPE = net.minecraft.class_2960.method_60656("trim_type");
    private static net.minecraft.class_4942 bannerInventory;
    private static net.minecraft.class_4942 skullInventory;
    private static net.minecraft.class_4946.class_4947 particleOnly;

    /** ItemModelGenerators.TRIM_TYPE_PREDICATE_ID */
    public static net.minecraft.class_2960 field_42086() {
        return TRIM_TYPE;
    }

    /** ModelTemplates.BANNER_INVENTORY (item/template_banner, no slots; one instance, as 1.21.3's constant) */
    public static synchronized net.minecraft.class_4942 field_22944() {
        if (bannerInventory == null) {
            bannerInventory = new net.minecraft.class_4942(java.util.Optional.of(net.minecraft.class_2960.method_60656("item/template_banner")),
                java.util.Optional.empty());
        }
        return bannerInventory;
    }

    /** ModelTemplates.SKULL_INVENTORY (item/template_skull, no slots) */
    public static synchronized net.minecraft.class_4942 field_22945() {
        if (skullInventory == null) {
            skullInventory = new net.minecraft.class_4942(java.util.Optional.of(net.minecraft.class_2960.method_60656("item/template_skull")),
                java.util.Optional.empty());
        }
        return skullInventory;
    }

    /** TexturedModel.PARTICLE_ONLY: the block's particle texture, ModelTemplates.PARTICLE_ONLY */
    public static synchronized net.minecraft.class_4946.class_4947 field_23047() {
        if (particleOnly == null) {
            particleOnly = top.dext.centurybridge.step.v1_21_4.mixin.CbDataTexturedModelAccess.cb$createDefault(net.minecraft.class_4944::method_25901,
                net.minecraft.class_4943.field_22908);
        }
        return particleOnly;
    }

    /** ModelTemplate's 1.21.3 members (CbDataModelTemplateMixin) */
    public interface CbLegacyModelTemplate {
        JsonObject method_48524(net.minecraft.class_2960 id, Map<net.minecraft.class_4945, net.minecraft.class_2960> textures);
    }

    /** 1.21.3 ModelTemplate.createBaseTemplate(id, textures): the parent and the textures */
    public static JsonObject baseTemplate(java.util.Optional<net.minecraft.class_2960> parent, Map<net.minecraft.class_4945, net.minecraft.class_2960> textures) {
        JsonObject o = new JsonObject();
        parent.ifPresent(p -> o.addProperty("parent", p.toString()));
        if (!textures.isEmpty()) {
            JsonObject t = new JsonObject();
            textures.forEach((slot, texture) -> t.addProperty(slot.method_25912(), texture.toString()));
            o.add("textures", t);
        }
        return o;
    }

    // ---- pick item ----

    /** a 1.21.3 MultiPlayerGameMode subclass overriding handlePickItem(slot) */
    public interface CbLegacyPickItem {
        void method_2916(int slot);
    }

    /** was MultiPlayerGameMode.handlePickItem(slot) (method_2916): a 1.21.3 subclass's own, else 1.21.3's */
    public static void method_2916(net.minecraft.class_636 gameMode, int slot) {
        if (gameMode instanceof CbLegacyPickItem own) {
            own.method_2916(slot);
        } else {
            superPickItem(gameMode, slot);
        }
    }

    /**
     * 1.21.3's handlePickItem(slot): the server swapped the picked inventory slot into the selected hotbar slot (its pick packet is
     * gone in 1.21.4): the same swap as a container click, then the selected slot
     */
    public static void superPickItem(net.minecraft.class_636 gameMode, int slot) {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_746 player = mc.field_1724;
        if (player == null) {
            return;
        }
        net.minecraft.class_1661 inventory = player.method_31548();
        int hotbar = inventory.method_7386();
        int menuSlot = slot < 9 ? 36 + slot : slot;
        gameMode.method_2906(player.field_7498.field_7763, menuSlot, hotbar, net.minecraft.class_1713.field_7791, player);
        inventory.field_7545 = hotbar;
        mc.method_1562().method_52787(new net.minecraft.class_2868(hotbar));
    }

    // ---- ParticleRenderType.begin ----

    /** was ParticleRenderType.begin(tesselator, textureManager) (method_18130) on a vanilla type: 1.21.3's state and buffer */
    public static net.minecraft.class_287 method_18130(net.minecraft.class_3999 type, net.minecraft.class_289 tesselator, net.minecraft.class_1060 textures) {
        if (type == net.minecraft.class_3999.field_17827) {
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
            com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.class_10142.field_53874);
            com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, net.minecraft.class_1059.field_5275);
        } else if (type == net.minecraft.class_3999.field_17828) {
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
            com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.class_10142.field_53874);
            com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, net.minecraft.class_1059.field_17898);
        } else if (type == net.minecraft.class_3999.field_17829) {
            com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
            com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.class_10142.field_53874);
            com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, net.minecraft.class_1059.field_17898);
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        } else if (type == net.minecraft.class_3999.field_17831) {
            com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        } else {
            return null; // NO_RENDER, and a type 1.21.3 did not have
        }
        return tesselator.method_60827(net.minecraft.class_293.class_5596.field_27382, net.minecraft.class_290.field_1584);
    }
}
