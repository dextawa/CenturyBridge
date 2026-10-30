package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.facade.BeeDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.BrainDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.BreezeDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.GameEventDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.GameEventListenerDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.GameTestAddMarkerDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.GameTestClearMarkersDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.GoalDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.HiveDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.NeighborUpdatesDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.PathfindingDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.PoiAddedDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.PoiRemovedDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.PoiTicketCountDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.RaidsDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.RedstoneWireOrientationsDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.StructuresDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.VillageSectionsDebugPayload;
import top.dext.centurybridge.step.v1_21_9.facade.WorldGenAttemptDebugPayload;

/**
 * 1.21.8's ClientboundCustomPayloadPacket (class_2658) built its GAMEPLAY_STREAM_CODEC from a list of payload types:
 * brand, then the 19 debug payloads (javap -c of 1.21.8's static init; this mixin adds them in that order), then whatever its list consumer
 * method_58270 (empty) added. 1.21.9 keeps the list and the empty consumer but lists brand alone. The consumer adds the
 * layer's debug payload records back with their own TYPE and STREAM_CODEC, so a payload a converted mod builds is
 * encoded and decoded as in 1.21.8 instead of falling to the unknown-payload codec (which writes nothing for it).
 * Vanilla 1.21.9 never sends these ids; the ids are 1.21.8's, so no 1.21.9 type is shadowed.
 */
@Mixin(net.minecraft.class_2658.class)
public abstract class CbDebugPayloadTypesMixin {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "method_58270", at = @At("HEAD"))
    private static void cb$debugPayloadTypes(ArrayList list, CallbackInfo ci) {
        list.add(new net.minecraft.class_8710.class_9155(BeeDebugPayload.field_48651, BeeDebugPayload.field_48650));
        list.add(new net.minecraft.class_8710.class_9155(BrainDebugPayload.field_48653, BrainDebugPayload.field_48652));
        list.add(new net.minecraft.class_8710.class_9155(BreezeDebugPayload.field_48657, BreezeDebugPayload.field_48656));
        list.add(new net.minecraft.class_8710.class_9155(GameEventDebugPayload.field_48661, GameEventDebugPayload.field_48660));
        list.add(new net.minecraft.class_8710.class_9155(GameEventListenerDebugPayload.field_48663, GameEventListenerDebugPayload.field_48662));
        list.add(new net.minecraft.class_8710.class_9155(GameTestAddMarkerDebugPayload.field_48665, GameTestAddMarkerDebugPayload.field_48664));
        list.add(new net.minecraft.class_8710.class_9155(GameTestClearMarkersDebugPayload.field_48667, GameTestClearMarkersDebugPayload.field_48666));
        list.add(new net.minecraft.class_8710.class_9155(GoalDebugPayload.field_48669, GoalDebugPayload.field_48668));
        list.add(new net.minecraft.class_8710.class_9155(HiveDebugPayload.field_48671, HiveDebugPayload.field_48670));
        list.add(new net.minecraft.class_8710.class_9155(NeighborUpdatesDebugPayload.field_48673, NeighborUpdatesDebugPayload.field_48672));
        list.add(new net.minecraft.class_8710.class_9155(PathfindingDebugPayload.field_48675, PathfindingDebugPayload.field_48674));
        list.add(new net.minecraft.class_8710.class_9155(PoiAddedDebugPayload.field_48677, PoiAddedDebugPayload.field_48676));
        list.add(new net.minecraft.class_8710.class_9155(PoiRemovedDebugPayload.field_48679, PoiRemovedDebugPayload.field_48678));
        list.add(new net.minecraft.class_8710.class_9155(PoiTicketCountDebugPayload.field_48681, PoiTicketCountDebugPayload.field_48680));
        list.add(new net.minecraft.class_8710.class_9155(RaidsDebugPayload.field_48683, RaidsDebugPayload.field_48682));
        list.add(new net.minecraft.class_8710.class_9155(RedstoneWireOrientationsDebugPayload.field_52320, RedstoneWireOrientationsDebugPayload.field_52321));
        list.add(new net.minecraft.class_8710.class_9155(StructuresDebugPayload.field_48685, StructuresDebugPayload.field_48684));
        list.add(new net.minecraft.class_8710.class_9155(VillageSectionsDebugPayload.field_48687, VillageSectionsDebugPayload.field_48686));
        list.add(new net.minecraft.class_8710.class_9155(WorldGenAttemptDebugPayload.field_48689, WorldGenAttemptDebugPayload.field_48688));
    }
}
