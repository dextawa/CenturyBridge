"""26.2 render bridge wiring (R1 entity renderers, R2 entity models). Idempotent."""
import json

SP = 'core/segments/shims-26.2.json'
MX = 'core/shims/26.2/resources/cb-26.2.mixins.json'
RT = 'top/dext/centurybridge/rt/v26_2/'
G = RT + 'guard/'
ES = 'Lnet/minecraft/client/renderer/entity/state/EntityRenderState;'
LS = 'Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;'
POSE = 'Lcom/mojang/blaze3d/vertex/PoseStack;'
COLL = 'Lnet/minecraft/client/renderer/SubmitNodeCollector;'
CAM = 'Lnet/minecraft/client/renderer/state/level/CameraRenderState;'
ID = 'Lnet/minecraft/resources/Identifier;'

d = json.load(open(SP, encoding='utf-8'))

hooks = [h for h in d['overrideHooks'] if not h.get('bridge', '').startswith(RT + 'CbEntityRender')
         and not h.get('bridge', '').startswith(RT + 'CbModels')]
hooks += [
    # render(entity, yaw, tickDelta, matrices, buffers, light) -> submit(state, poseStack, collector, camera),
    # at whichever erasure the mod's renderer overrides (one new shape: the first matching hook wins)
    {
        'old': 'method_4072(Lnet/minecraft/class_1308;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V',
        'new': f'submit({ES}{POSE}{COLL}{CAM})V',
        'bridge': f'{RT}CbEntityRender.submitMob(L{RT}CbLegacyMobRenderer;{ES}{POSE}{COLL}{CAM})V',
        'adapter': f'{RT}CbLegacyMobRenderer',
        'guard': f'{G}RenderSubmitGuard',
        'onlyUnder': 'net/minecraft/class_927',
    },
    {
        'old': 'method_4054(Lnet/minecraft/class_1309;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V',
        'new': f'submit({ES}{POSE}{COLL}{CAM})V',
        'bridge': f'{RT}CbEntityRender.submitLiving(L{RT}CbLegacyLivingRenderer;{ES}{POSE}{COLL}{CAM})V',
        'adapter': f'{RT}CbLegacyLivingRenderer',
        'guard': f'{G}RenderSubmitGuard',
        'onlyUnder': 'net/minecraft/class_922',
    },
    {
        'old': 'method_3936(Lnet/minecraft/class_1297;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V',
        'new': f'submit({ES}{POSE}{COLL}{CAM})V',
        'bridge': f'{RT}CbEntityRender.submit(L{RT}CbLegacyEntityRenderer;{ES}{POSE}{COLL}{CAM})V',
        'adapter': f'{RT}CbLegacyEntityRenderer',
        'guard': f'{G}RenderSubmitGuard',
        'onlyUnder': 'net/minecraft/class_897',
    },
    {   # getTexture(entity) -> getTextureLocation(state), the abstract one living renderers draw with
        'old': 'method_3931(Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960;',
        'new': f'getTextureLocation({LS}){ID}',
        'bridge': f'{RT}CbEntityRender.getTextureLocation(L{RT}CbLegacyTextured;{LS}){ID}',
        'adapter': f'{RT}CbLegacyTextured',
        'guard': f'{G}RenderTextureGuard',
        'onlyUnder': 'net/minecraft/class_922',
    },
    {   # setAngles(entity, 5 floats) -> setupAnim(state)
        'old': 'method_2819(Lnet/minecraft/class_1297;FFFFF)V',
        'new': 'setupAnim(Ljava/lang/Object;)V',
        'bridge': f'{RT}CbModels.setupAnim(L{RT}CbLegacyEntityModel;Ljava/lang/Object;)V',
        'adapter': f'{RT}CbLegacyEntityModel',
        'guard': f'{G}ModelAnimGuard',
        'onlyUnder': 'net/minecraft/class_583',
    },
]
# feature renderers: render(matrices, buffers, light, entity, 6 floats) -> submit(poseStack, collector, light, state, yRot, xRot)
hooks = [h for h in hooks if not h.get('bridge', '').startswith(RT + 'CbRenderLayers')]
hooks.append({
    'old': 'method_4199(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1297;FFFFFF)V',
    'new': f'submit({POSE}{COLL}I{ES}FF)V',
    'bridge': f'{RT}CbRenderLayers.submit(L{RT}CbLegacyRenderLayer;{POSE}{COLL}I{ES}FF)V',
    'adapter': f'{RT}CbLegacyRenderLayer',
    'guard': f'{G}RenderLayerGuard',
    'onlyUnder': 'net/minecraft/class_3887',
})
d['overrideHooks'] = hooks
# FeatureRenderer's static helpers mods draw with, and LivingEntityRenderer.getOverlay(entity, f)
d['staticRedirects']['net/minecraft/class_3887.method_23199(Lnet/minecraft/class_583;Lnet/minecraft/class_2960;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1309;FFF)V'] = RT + 'CbRenderLayers'
d['staticRedirects']['net/minecraft/class_3887.method_23196(Lnet/minecraft/class_583;Lnet/minecraft/class_583;Lnet/minecraft/class_2960;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1309;FFFFFFFFF)V'] = RT + 'CbRenderLayers'
d['staticRedirects']['net/minecraft/class_922.method_23622(Lnet/minecraft/class_1309;F)I'] = RT + 'CbRenderLayers#overlay'
for k in [k for k in d.get('ledger', {}) if k in d['staticRedirects']
          or k in ('net/minecraft/class_3887.method_23199', 'net/minecraft/class_3887.method_23196')]:
    print('unledger', k)
    del d['ledger'][k]

ah = [h for h in d.get('adapterHooks', []) if h['adapter'] not in (RT + 'CbLegacyRenderStateOwner', RT + 'CbLegacyModelRender')]
ah += [
    {   # createRenderState(): abstract on 26.x EntityRenderer, never written by a 1.20.1 renderer
        'under': 'net/minecraft/class_897',
        'adapter': f'{RT}CbLegacyRenderStateOwner',
        'guard': f'{G}RenderStateGuard',
        'requires': [],
        'synth': [{'new': f'createRenderState(){ES}',
                   'call': f'{RT}CbEntityRender.createRenderState(L{RT}CbLegacyRenderStateOwner;){ES}'}],
    },
    {   # a model's own render(matrices, vertices, light, overlay, r, g, b, a): drawn by ModelRenderBridge
        'under': 'net/minecraft/class_3879',
        'adapter': f'{RT}CbLegacyModelRender',
        'requires': ['method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;IIFFFF)V'],
        'synth': [],
    },
]
# block entity renderers (an interface: no vanilla super above most of them)
BES = 'Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;'
OLD_BER = 'method_3569(Lnet/minecraft/class_2586;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V'
ah = [h for h in ah if h['adapter'] != RT + 'CbLegacyBlockEntityRenderer']
ah += [
    {   # createRenderState(): abstract on 26.x BlockEntityRenderer
        'under': 'net/minecraft/class_827', 'adapter': f'{RT}CbLegacyBlockEntityRenderer',
        'guard': f'{G}BlockEntityStateGuard', 'requires': [OLD_BER],
        'synth': [{'new': f'createRenderState(){BES}',
                   'call': f'{RT}CbBlockEntityRender.createRenderState(L{RT}CbLegacyBlockEntityRenderer;){BES}'}],
    },
    {   # render(blockEntity, tickDelta, matrices, buffers, light, overlay) -> submit(state, poseStack, collector, camera)
        'under': 'net/minecraft/class_827', 'adapter': f'{RT}CbLegacyBlockEntityRenderer',
        'guard': f'{G}BlockEntitySubmitGuard', 'requires': [OLD_BER],
        'synth': [{'new': f'submit({BES}{POSE}{COLL}{CAM})V',
                   'call': f'{RT}CbBlockEntityRender.submit(L{RT}CbLegacyBlockEntityRenderer;{BES}{POSE}{COLL}{CAM})V'}],
    },
]
d['adapterHooks'] = ah

# super.render(...) inside an old render, per owner the call names (receiver typed as it)
sr = d.setdefault('superRedirects', {})
sr['net/minecraft/class_897.method_3936(Lnet/minecraft/class_1297;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V'] = RT + 'CbEntityRender#superRender'
sr['net/minecraft/class_922.method_4054(Lnet/minecraft/class_1309;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V'] = RT + 'CbEntityRender#superRender'
sr['net/minecraft/class_927.method_4072(Lnet/minecraft/class_1308;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V'] = RT + 'CbEntityRender#superRender'

# model.render(...) called on a model
d['instanceRedirects']['net/minecraft/class_3879.method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;IIFFFF)V'] = RT + 'CbModels'

# models take their root at super() since 1.21.2: the constructor's own ModelPart
root = 'param:Lnet/minecraft/class_630; ' + RT + 'CbModels#emptyRoot()Lnet/minecraft/class_630;'
scr = d.setdefault('superCtorReshapes', {})
scr['net/minecraft/class_583.<init>()V'] = {'desc': '(Lnet/minecraft/class_630;)V', 'ops': [root]}
scr['net/minecraft/class_583.<init>(Ljava/util/function/Function;)V'] = {
    'desc': '(Lnet/minecraft/class_630;Ljava/util/function/Function;)V', 'ops': [root, 'keep:0']}
# SinglePartEntityModel's facade takes the same root (CbModels / class_5597 facade)
scr['net/minecraft/class_5597.<init>()V'] = {'desc': '(Lnet/minecraft/class_630;)V', 'ops': [root]}
scr['net/minecraft/class_5597.<init>(Ljava/util/function/Function;)V'] = {
    'desc': '(Lnet/minecraft/class_630;Ljava/util/function/Function;)V', 'ops': [root, 'keep:0']}
scr['net/minecraft/class_3879.<init>(Ljava/util/function/Function;)V'] = {
    'desc': '(Lnet/minecraft/class_630;Ljava/util/function/Function;)V', 'ops': [root, 'keep:0']}

# the ledgers these replace
led = d.get('ledger', {})
for k in [k for k in led if k.startswith('net/minecraft/class_583.<init>') or k.startswith('net/minecraft/class_3879.<init>')]:
    print('unledger', k)
    del led[k]

open(SP, 'w', encoding='utf-8', newline='\n').write(json.dumps(d, indent=1, ensure_ascii=False) + '\n')

c = json.load(open(MX, encoding='utf-8'))
for m in ['EntityRenderStateEntityBridge', 'EntityRendererCaptureBridge', 'ModelRenderBridge',
          'BlockEntityRenderStateBridge', 'BlockEntityRenderDispatcherCaptureBridge']:
    if m not in c['client']:
        c['client'].append(m)
c['client'].sort()
open(MX, 'w', encoding='utf-8', newline='\n').write(json.dumps(c, indent=2, ensure_ascii=False) + '\n')
print('ok')
