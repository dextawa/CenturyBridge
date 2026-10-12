package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1428;
import net.minecraft.class_1429;
import net.minecraft.class_1492;
import net.minecraft.class_1496;
import net.minecraft.class_1501;
import net.minecraft.class_1506;
import net.minecraft.class_1510;
import net.minecraft.class_1531;
import net.minecraft.class_1547;
import net.minecraft.class_1559;
import net.minecraft.class_1584;
import net.minecraft.class_1590;
import net.minecraft.class_1593;
import net.minecraft.class_1606;
import net.minecraft.class_1614;
import net.minecraft.class_1628;
import net.minecraft.class_1634;
import net.minecraft.class_1642;
import net.minecraft.class_1657;
import net.minecraft.class_1688;
import net.minecraft.class_1690;
import net.minecraft.class_3532;
import net.minecraft.class_3732;
import net.minecraft.class_4048;
import net.minecraft.class_4050;
import net.minecraft.class_4760;
import net.minecraft.class_4836;
import net.minecraft.class_4985;
import net.minecraft.class_5136;
import net.minecraft.class_5418;
import net.minecraft.class_7298;
import net.minecraft.class_7689;
import net.minecraft.class_8153;
import org.joml.Vector3f;
import top.dext.centurybridge.shims.v1_20_2.CbEnderDragonBodyAccess;
import top.dext.centurybridge.shims.v1_20_2.CbHorseRidingAccess;

/**
 * 1.20.1 riding offsets on 1.20.2's passenger attachment points.
 *
 * 1.20.1 placed a passenger at
 * <pre>  vehicle.getY() + vehicle.getPassengersRidingOffset() + passenger.getMyRidingOffset()</pre>
 * (plus what a vehicle's own positionRider added: a rearing horse, a camel's sit/stand animation,
 * a chicken's fixed half height, a removed boat or camel). 1.20.2 places it at
 * <pre>  vehicle.position() + rotate(vehicle.getPassengerAttachmentPoint(passenger, dims, scale)).y
 *       + passenger.getMyRidingOffset(vehicle)</pre>
 * with every vanilla number retuned (the default seat went from 0.75 to 1.0 of the vehicle's
 * height, a player's own offset from -0.35 to -0.6). Neither old method is called any more.
 *
 * A converted vehicle that overrides getPassengersRidingOffset gets 1.20.2's attachment point
 * synthesized ({@link #vehicleAttachment}): the vertical part is 1.20.1's whole formula --
 * the mod's offset, the passenger's 1.20.1 offset, the vanilla extras of the class it extends --
 * minus what 1.20.2 will add for the passenger anyway, so the passenger ends up exactly where
 * 1.20.1 put it; the horizontal part (seat layout, rearing shift) stays vanilla 1.20.2's, which
 * is where 1.20.2 moved 1.20.1's own horizontal offsets. A converted passenger that overrides
 * getMyRidingOffset gets 1.20.2's getMyRidingOffset(vehicle) synthesized
 * ({@link #passengerOffset}) the same way against the vehicle's vanilla 1.20.2 attachment point.
 * Pairs of vanilla entities are left to 1.20.2 entirely: nothing here runs for them.
 *
 * The 1.20.1 formulas below are the bodies of every 1.20.1 class that declared the two methods,
 * most specific class first, written against the same members in 1.20.2.
 */
public final class CbRiding {

    // ---- 1.20.1 getPassengersRidingOffset, per vanilla class

    public static double vanillaVehicleOffset(class_1297 e) {
        if (e instanceof class_7689 camel) {
            class_4048 dims = camel.method_18377(camel.method_45350() ? class_4050.field_40118 : class_4050.field_18076);
            return dims.field_18068 - (camel.method_6109() ? 0.35F : 0.6F);
        }
        if (e instanceof class_1501 llama) {
            return (double) llama.method_17682() * 0.6;
        }
        if (e instanceof class_1492 chested) {
            return entityDefault(chested) - 0.25;
        }
        if (e instanceof class_1506 skeletonHorse) {
            return entityDefault(skeletonHorse) - 0.1875;
        }
        if (e instanceof class_8153) {
            return 1.8;
        }
        if (e instanceof class_1510 dragon) {
            return ((CbEnderDragonBodyAccess) dragon).cb$body().method_17682();
        }
        if (e instanceof class_1593 phantom) {
            return phantom.method_5751();
        }
        if (e instanceof class_1584) {
            return 2.1;
        }
        if (e instanceof class_1628 spider) {
            return spider.method_17682() * 0.5F;
        }
        if (e instanceof class_4985 strider) {
            float speed = Math.min(0.25F, strider.field_42108.method_48566());
            float pos = strider.field_42108.method_48569();
            return (double) strider.method_17682() - 0.19 + (double) (0.12F * class_3532.method_15362(pos * 1.5F) * 2.0F * speed);
        }
        if (e instanceof class_5136 zoglin) {
            return (double) zoglin.method_17682() - (zoglin.method_6109() ? 0.2 : 0.15);
        }
        if (e instanceof class_4760 hoglin) {
            return (double) hoglin.method_17682() - (hoglin.method_6109() ? 0.2 : 0.15);
        }
        if (e instanceof class_4836 piglin) {
            return (double) piglin.method_17682() * 0.92;
        }
        if (e instanceof class_1688) {
            return 0.0;
        }
        if (e instanceof class_1690 boat) {
            return boat.method_47885() == class_1690.class_1692.field_40161 ? 0.25 : -0.1;
        }
        return entityDefault(e);
    }

    /** 1.20.1 Entity.getPassengersRidingOffset: three quarters of the current height */
    private static double entityDefault(class_1297 e) {
        return (double) e.method_17682() * 0.75;
    }

    // ---- 1.20.1 getMyRidingOffset, per vanilla class

    public static double vanillaPassengerOffset(class_1297 e) {
        if (e instanceof class_1657) {
            return -0.35;
        }
        if (e instanceof class_1531 stand) {
            return stand.method_6912() ? 0.0 : (double) 0.1F;
        }
        if (e instanceof class_1606 shulker) {
            class_1297 vehicle = shulker.method_5854();
            if (vehicle != null && (vehicle instanceof class_1690 || vehicle.method_5864() == class_1299.field_6096)) {
                return 0.1875 - ((CbLegacyRiding) vehicle).method_5621();
            }
            return 0.0;
        }
        if (e instanceof class_1590 zombifiedPiglin) {
            return zombifiedPiglin.method_6109() ? -0.05 : -0.45;
        }
        if (e instanceof class_1642 zombie) {
            return zombie.method_6109() ? 0.0 : -0.45;
        }
        if (e instanceof class_5418 piglin) {
            return piglin.method_6109() ? -0.05 : -0.45;
        }
        if (e instanceof class_1547) {
            return -0.6;
        }
        if (e instanceof class_1559 || e instanceof class_1614) {
            return 0.1;
        }
        if (e instanceof class_1634 || e instanceof class_7298) {
            return 0.4;
        }
        if (e instanceof class_3732) {
            return -0.45;
        }
        if (e instanceof class_1429) {
            return 0.14;
        }
        return 0.0;
    }

    // ---- 1.20.1 positionRider: the passenger's height above the vehicle's feet

    /**
     * Where 1.20.1 put {@code passenger} on {@code vehicle}, relative to the vehicle's y: the
     * positionRider of the vanilla class the vehicle is, with the vehicle's and the passenger's
     * 1.20.1 offsets as they answer them (a converted override, else 1.20.1's formula).
     */
    public static double legacyPassengerHeight(class_1297 vehicle, class_1297 passenger) {
        double my = ((CbLegacyRiding) passenger).method_5678();
        if (vehicle instanceof class_7689 camel) {
            // Camel.positionRider: a removed camel holds its passengers at 0.01, without their offset
            if (camel.method_31481()) {
                return 0.01F;
            }
            boolean first = camel.method_5685().indexOf(passenger) <= 0;
            return (float) (((CbLegacyRiding) camel).method_5621() + camelAnimation(camel, first) + my);
        }
        double offset = ((CbLegacyRiding) vehicle).method_5621();
        if (vehicle instanceof class_1501) {
            return offset + my; // Llama.positionRider: no rearing term
        }
        if (vehicle instanceof class_1496 horse) {
            float stand = ((CbHorseRidingAccess) horse).cb$standAnimO();
            return stand > 0.0F ? offset + my + (double) (0.15F * stand) : offset + my;
        }
        if (vehicle instanceof class_1428 chicken) {
            // Chicken.positionRider: half its height, whatever its riding offset says
            return chicken.method_17682() * 0.5 + my;
        }
        if (vehicle instanceof class_1690 boat) {
            return (float) ((boat.method_31481() ? 0.01 : offset) + my);
        }
        return offset + my;
    }

    /**
     * What 1.20.1 Camel.getBodyAnchorAnimationYOffset(first, 0) added to the riding offset: the
     * sit / stand transition (partial tick 0, as positionRider called it).
     */
    private static double camelAnimation(class_7689 camel, boolean first) {
        float scale = camel.method_17825();
        float standing = scale * 1.43F;
        float sitting = standing - scale * 0.2F;
        float diff = standing - sitting;
        boolean transition = camel.method_45351();
        boolean isSitting = camel.method_45350();
        double d = 0.0;
        if (transition) {
            int total = isSitting ? 40 : 52;
            int split;
            float factor;
            if (isSitting) {
                split = 28;
                factor = first ? 0.5F : 0.1F;
            } else {
                split = first ? 24 : 32;
                factor = first ? 0.6F : 0.35F;
            }
            float t = class_3532.method_15363((float) camel.method_45355(), 0.0F, (float) total);
            boolean early = t < (float) split;
            float delta = early ? t / (float) split : (t - (float) split) / (float) (total - split);
            float mid = standing - factor * sitting;
            d += isSitting
                ? (double) class_3532.method_16439(delta, early ? standing : mid, early ? mid : diff)
                : (double) class_3532.method_16439(delta, early ? diff - standing : diff - mid, early ? diff - mid : 0.0F);
        }
        if (isSitting && !transition) {
            d += diff;
        }
        return d;
    }

    // ---- reconnection

    /**
     * 1.20.2 getPassengerAttachmentPoint of a converted vehicle overriding getPassengersRidingOffset:
     * vanilla's horizontal seat, 1.20.1's height. 1.20.2 adds the passenger's
     * getMyRidingOffset(vehicle) on top, so that is taken off here.
     */
    public static Vector3f vehicleAttachment(CbLegacyRidingVehicle vehicle, class_1297 passenger, class_4048 dimensions, float scale) {
        Vector3f vanilla = vehicle.cb$passengerAttachment(passenger, dimensions, scale);
        class_1297 self = (class_1297) vehicle;
        double y = legacyPassengerHeight(self, passenger) - passenger.method_52536(self);
        return new Vector3f(vanilla.x(), (float) y, vanilla.z());
    }

    /**
     * 1.20.2 getMyRidingOffset(vehicle) of a converted passenger overriding getMyRidingOffset: on a
     * vanilla vehicle, 1.20.1's height minus the attachment point 1.20.2 seats it at; on a converted
     * vehicle that vehicle's own attachment point already answers 1.20.1's whole height for this
     * passenger (whatever this returns, it is subtracted there and added back here).
     */
    public static float passengerOffset(CbLegacyRidingPassenger passenger, class_1297 vehicle) {
        if (vehicle instanceof CbLegacyRidingVehicle) {
            return (float) passenger.method_5678();
        }
        class_1297 self = (class_1297) passenger;
        double seat = vehicle.method_52538(self).field_1351 - vehicle.method_23318();
        return (float) (legacyPassengerHeight(vehicle, self) - seat);
    }

    private CbRiding() {
    }
}
