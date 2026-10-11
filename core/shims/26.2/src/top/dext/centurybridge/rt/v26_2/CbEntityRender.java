package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * 1.20.1 entity renderers over 26.x's extract/submit pipeline.
 *
 * 1.20.1: render(entity, yaw, tickDelta, matrices, buffers, light), drawing immediately,
 * getTexture(entity). 26.x: createRenderState() (abstract), extractRenderState(entity,
 * state, partialTick), submit(state, poseStack, collector, camera), and for living
 * renderers getTextureLocation(state). Every state the dispatcher builds remembers its
 * entity (CbRenderStateEntity), so the old methods are called with exactly what they were
 * called with before: the entity, its interpolated yaw, the partial tick, the pose stack
 * vanilla positions the entity with, a buffer source, the light vanilla computed. The
 * buffer source records (CbRecordingBuffers) and its recordings are submitted afterwards.
 *
 * super.render(...) inside an old render was the parent's drawing -- the name tag for a
 * plain EntityRenderer, the model and its features for a LivingEntityRenderer. The 26.x
 * parent draws those in its submit, so super.render calls the renderer's submit again with
 * the frame's state: the guard sends that second entry to super.submit.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbEntityRender {

    /** the frame a legacy render is running in, for super.render inside it */
    private record Frame(EntityRenderState state, SubmitNodeCollector collector, CameraRenderState camera) {}

    private static final ThreadLocal<ArrayDeque<Frame>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);
    /** renderer class -> the vanilla state class its renderer family extracts into, or null when a vanilla superclass implements createRenderState itself */
    private static final Map<Class<?>, Object> STATE_CLASS = new ConcurrentHashMap<>();
    private static final Object VANILLA_CREATES = new Object();

    // ---- createRenderState (adapterHooks, guarded) ----

    public static EntityRenderState createRenderState(CbLegacyRenderStateOwner self) {
        Object how = STATE_CLASS.computeIfAbsent(self.getClass(), CbEntityRender::stateClassFor);
        if (how == VANILLA_CREATES) {
            // a vanilla renderer above the mod's implements it, with the state type its own
            // code casts to: the second entry goes to super
            return ((EntityRenderer<?, ?>) self).createRenderState();
        }
        try {
            return (EntityRenderState) ((Class<?>) how).getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            return new EntityRenderState();
        }
    }

    /**
     * Above a 1.20.1 renderer sits a vanilla one (EntityRenderer, LivingEntityRenderer,
     * MobRenderer, HumanoidMobRenderer...). If it implements createRenderState, that wins.
     * Otherwise its state type parameter's bound is what its extract/submit code expects:
     * EntityRenderState, LivingEntityRenderState, HumanoidRenderState -- read from the
     * vanilla class itself (the mod's own generic signature predates the parameter).
     */
    private static Object stateClassFor(Class<?> renderer) {
        Class<?> vanilla = renderer.getSuperclass();
        while (vanilla != null && !vanilla.getName().startsWith("net.minecraft.")) {
            vanilla = vanilla.getSuperclass();
        }
        if (vanilla == null) {
            return EntityRenderState.class;
        }
        try {
            Method m = vanilla.getMethod("createRenderState");
            if (!Modifier.isAbstract(m.getModifiers())) {
                return VANILLA_CREATES;
            }
        } catch (NoSuchMethodException e) {
            // not reachable as public: fall through to the type parameter
        }
        for (Class<?> c = vanilla; c != null; c = c.getSuperclass()) {
            for (TypeVariable<?> tv : c.getTypeParameters()) {
                for (java.lang.reflect.Type b : tv.getBounds()) {
                    if (b instanceof Class<?> k && EntityRenderState.class.isAssignableFrom(k)
                            && !Modifier.isAbstract(k.getModifiers())) {
                        return k;
                    }
                }
            }
        }
        return EntityRenderState.class;
    }

    // ---- render -> submit (overrideHooks, bridge mode, guarded) ----

    /** the old render, whichever erasure the mod's renderer overrides (Entity / LivingEntity / Mob) */
    @FunctionalInterface
    private interface OldRender {
        void render(Entity entity, float yaw, float tickDelta, PoseStack matrices, class_4597 buffers, int light);
    }

    public static void submit(CbLegacyEntityRenderer self, EntityRenderState state, PoseStack poseStack,
                              SubmitNodeCollector collector, CameraRenderState camera) {
        run(self, self::submit, state, poseStack, collector, camera);
    }

    /** a LivingEntityRenderer subclass's render(LivingEntity, ...) */
    public static void submitLiving(CbLegacyLivingRenderer self, EntityRenderState state, PoseStack poseStack,
                                    SubmitNodeCollector collector, CameraRenderState camera) {
        run(self, (e, yaw, td, m, b, l) -> self.submit((net.minecraft.world.entity.LivingEntity) e, yaw, td, m, b, l),
            state, poseStack, collector, camera);
    }

    /** a MobRenderer subclass's render(Mob, ...) */
    public static void submitMob(CbLegacyMobRenderer self, EntityRenderState state, PoseStack poseStack,
                                 SubmitNodeCollector collector, CameraRenderState camera) {
        run(self, (e, yaw, td, m, b, l) -> self.method_4072((net.minecraft.world.entity.Mob) e, yaw, td, m, b, l),
            state, poseStack, collector, camera);
    }

    @SuppressWarnings("unchecked")
    private static void run(Object self, OldRender old, EntityRenderState state, PoseStack poseStack,
                            SubmitNodeCollector collector, CameraRenderState camera) {
        Entity entity = ((CbRenderStateEntity) state).centurybridge$entity();
        if (entity == null) {
            // a state that did not come through the dispatcher: nothing to hand the old method
            ((EntityRenderer<?, EntityRenderState>) self).submit(state, poseStack, collector, camera);
            return;
        }
        float tickDelta = ((CbRenderStateEntity) state).centurybridge$partialTick();
        float yaw = Mth.lerp(tickDelta, entity.yRotO, entity.getYRot());
        CbRecordingBuffers buffers = new CbRecordingBuffers();
        ArrayDeque<Frame> frames = FRAMES.get();
        frames.push(new Frame(state, collector, camera));
        try {
            old.render(entity, yaw, tickDelta, poseStack, buffers, state.lightCoords);
        } finally {
            frames.pop();
            buffers.submitTo(collector);
        }
    }

    /**
     * super.render(entity, yaw, tickDelta, matrices, buffers, light) from inside an old
     * render: the parent's part of the frame, which the parent now draws in submit. The
     * renderer's submit is entered again; the guard takes it to super.submit. Outside a
     * frame (a renderer calling another's render directly) there is no state to draw from.
     */
    public static void superRender(EntityRenderer<?, ?> self, Entity entity, float yaw, float tickDelta,
                                   PoseStack matrices, class_4597 buffers, int light) {
        Frame f = FRAMES.get().peek();
        if (f == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        EntityRenderer<?, EntityRenderState> r = (EntityRenderer<?, EntityRenderState>) self;
        r.submit(f.state(), matrices, f.collector(), f.camera());
    }

    /** super.render from a LivingEntityRenderer subclass (the call names LivingEntityRenderer.render) */
    public static void superRender(net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?, ?> self,
                                   net.minecraft.world.entity.LivingEntity entity, float yaw, float tickDelta,
                                   PoseStack matrices, class_4597 buffers, int light) {
        superRender((EntityRenderer<?, ?>) self, entity, yaw, tickDelta, matrices, buffers, light);
    }

    /** super.render from a MobRenderer subclass (the call names MobRenderer.render) */
    public static void superRender(net.minecraft.client.renderer.entity.MobRenderer<?, ?, ?> self,
                                   net.minecraft.world.entity.Mob entity, float yaw, float tickDelta,
                                   PoseStack matrices, class_4597 buffers, int light) {
        superRender((EntityRenderer<?, ?>) self, entity, yaw, tickDelta, matrices, buffers, light);
    }

    // ---- getTexture -> getTextureLocation (overrideHooks, bridge mode, guarded) ----

    public static Identifier getTextureLocation(CbLegacyTextured self, LivingEntityRenderState state) {
        Entity entity = ((CbRenderStateEntity) state).centurybridge$entity();
        return entity != null ? self.method_3931(entity) : MissingTextureAtlasSprite.getLocation();
    }

    private CbEntityRender() {
    }
}
