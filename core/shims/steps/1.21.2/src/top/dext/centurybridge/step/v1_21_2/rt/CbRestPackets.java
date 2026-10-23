package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 1.21.1's game packet API on 1.21.2's packets.
 *
 * Several 1.21.1 packets 1.21.2 made records that carry other values: the player position and the entity
 * teleport packet a PositionMoveRotation (position, delta movement, rotations) and a Relative set (1.21.1:
 * a RelativeMovement set over x/y/z/yRot/xRot -- the same enum, five constants of the nine); the time
 * packet a day-time-ticks flag where 1.21.1 encoded it as the day time's sign; the explosion packet only
 * what the client renders (centre, the knockback the player takes, the one particle, the sound). A 1.21.1
 * constructor makes the 1.21.2 packet of what it was given -- the explosion is sent as 1.21.1's server
 * made its effect seen (the large particle where 1.21.1's client showed it), the blocks to blow left out
 * since a 1.21.2 client changes no block on an explosion packet (the server's block updates do); a 1.21.1
 * getter answers from the record, and for the values the record no longer has, from what 1.21.2's own
 * handler does with it (the time packet's day time: negative when the day does not advance, as 1.21.1
 * encoded it). Packets 1.21.1 built through the record's canonical shape keep it, so encoding is 1.21.2's.
 */
public final class CbRestPackets {

    private CbRestPackets() {
    }

    // ------------------------------------------------------------------ RelativeMovement sets

    /** 1.21.1's RelativeMovement set as 1.21.2's Relative set: the same constants (1.21.2 added delta ones) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set<net.minecraft.class_2709> relatives(Set relatives) {
        return relatives == null ? null : (Set<net.minecraft.class_2709>) relatives;
    }

    // ------------------------------------------------------------------ ClientboundPlayerPositionPacket (class_2708)

    /**
     * new ClientboundPlayerPositionPacket(x, y, z, yRot, xRot, relatives, id): 1.21.1's packet put the
     * player at (x, y, z) with those rotations, each one relative to the player's own when its constant
     * is in the set, and kept the player's movement when the position was relative on that axis (1.21.1's
     * handleMovePlayer zeroed the delta on every absolute axis). 1.21.2: the same values as a
     * PositionMoveRotation with no delta movement, and the set with the relative axes' DELTA_ constants
     * added, which tells 1.21.2's client to keep the delta there -- 1.21.1's handling exactly.
     */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2708 newPlayerPosition(double x, double y, double z, float yRot, float xRot, Set relatives, int id) {
        Set<net.minecraft.class_2709> set = withKeptDelta(relatives(relatives));
        return new net.minecraft.class_2708(id,
            new net.minecraft.class_10182(new net.minecraft.class_243(x, y, z), net.minecraft.class_243.field_1353, yRot, xRot), set);
    }

    /** a RelativeMovement set of 1.21.1 with, for each relative position axis, the matching DELTA_ constant */
    public static Set<net.minecraft.class_2709> withKeptDelta(Set<net.minecraft.class_2709> relatives) {
        EnumSet<net.minecraft.class_2709> out = EnumSet.noneOf(net.minecraft.class_2709.class);
        out.addAll(relatives);
        if (relatives.contains(net.minecraft.class_2709.field_12400)) {
            out.add(net.minecraft.class_2709.field_54090);
        }
        if (relatives.contains(net.minecraft.class_2709.field_12398)) {
            out.add(net.minecraft.class_2709.field_54091);
        }
        if (relatives.contains(net.minecraft.class_2709.field_12403)) {
            out.add(net.minecraft.class_2709.field_54092);
        }
        return out;
    }

    /** getX() */
    public static double method_11734(net.minecraft.class_2708 p) {
        return p.comp_3228().comp_3148().field_1352;
    }

    /** getY() */
    public static double method_11735(net.minecraft.class_2708 p) {
        return p.comp_3228().comp_3148().field_1351;
    }

    /** getZ() */
    public static double method_11738(net.minecraft.class_2708 p) {
        return p.comp_3228().comp_3148().field_1350;
    }

    /** getYRot() */
    public static float method_11736(net.minecraft.class_2708 p) {
        return p.comp_3228().comp_3150();
    }

    /** getXRot() */
    public static float method_11739(net.minecraft.class_2708 p) {
        return p.comp_3228().comp_3151();
    }

    /** getId() */
    public static int method_11737(net.minecraft.class_2708 p) {
        return p.comp_3133();
    }

    /** getRelativeArguments(): the set's 1.21.1 constants (x, y, z, yRot, xRot) */
    public static Set<net.minecraft.class_2709> method_11733(net.minecraft.class_2708 p) {
        return legacyRelatives(p.comp_3229());
    }

    static Set<net.minecraft.class_2709> legacyRelatives(Set<net.minecraft.class_2709> relatives) {
        EnumSet<net.minecraft.class_2709> out = EnumSet.noneOf(net.minecraft.class_2709.class);
        for (net.minecraft.class_2709 r : relatives) {
            if (r == net.minecraft.class_2709.field_12400 || r == net.minecraft.class_2709.field_12398 || r == net.minecraft.class_2709.field_12403
                    || r == net.minecraft.class_2709.field_12401 || r == net.minecraft.class_2709.field_12397) {
                out.add(r);
            }
        }
        return out;
    }

    // ------------------------------------------------------------------ ClientboundTeleportEntityPacket (class_2777)

    /**
     * new ClientboundTeleportEntityPacket(entity): 1.21.1 sent the entity's tracking position, rotations and
     * ground flag, all absolute; 1.21.2's ClientboundEntityPositionSyncPacket.of(entity) is that packet's
     * successor, and the teleport packet of the same values is (id, PositionMoveRotation.of(entity) -- its
     * position, known movement and rotations --, no relatives, onGround).
     */
    public static net.minecraft.class_2777 newTeleportEntity(net.minecraft.class_1297 entity) {
        return new net.minecraft.class_2777(entity.method_5628(), net.minecraft.class_10182.method_63638(entity), Set.of(), entity.method_24828());
    }

    /** getId() */
    public static int method_11916(net.minecraft.class_2777 p) {
        return p.comp_3237();
    }

    /** getX() */
    public static double method_11917(net.minecraft.class_2777 p) {
        return p.comp_3238().comp_3148().field_1352;
    }

    /** getY() */
    public static double method_11919(net.minecraft.class_2777 p) {
        return p.comp_3238().comp_3148().field_1351;
    }

    /** getZ() */
    public static double method_11918(net.minecraft.class_2777 p) {
        return p.comp_3238().comp_3148().field_1350;
    }

    /** getyRot(): the rotation packed into a byte, as 1.21.1 carried it */
    public static byte method_11920(net.minecraft.class_2777 p) {
        return net.minecraft.class_3532.method_62995(p.comp_3238().comp_3150());
    }

    /** getxRot() */
    public static byte method_11921(net.minecraft.class_2777 p) {
        return net.minecraft.class_3532.method_62995(p.comp_3238().comp_3151());
    }

    /** isOnGround() */
    public static boolean method_11923(net.minecraft.class_2777 p) {
        return p.comp_3240();
    }

    // ------------------------------------------------------------------ ClientboundMoveEntityPacket / RotateHead

    /** ClientboundMoveEntityPacket.getyRot(): 1.21.1 answered the packed byte, 1.21.2 unpacks it to degrees */
    public static byte method_11649(net.minecraft.class_2684 p) {
        return net.minecraft.class_3532.method_62995(p.method_11649());
    }

    /** ClientboundMoveEntityPacket.getxRot() */
    public static byte method_11650(net.minecraft.class_2684 p) {
        return net.minecraft.class_3532.method_62995(p.method_11650());
    }

    /** ClientboundRotateHeadPacket.getYHeadRot() */
    public static byte method_11787(net.minecraft.class_2726 p) {
        return net.minecraft.class_3532.method_62995(p.method_11787());
    }

    /** an override of one of those getters answering the byte, as 1.21.2's degrees */
    public static float degrees(byte packed) {
        return net.minecraft.class_3532.method_62994(packed);
    }

    // ------------------------------------------------------------------ ClientboundSetTimePacket (class_2761)

    /** getGameTime() */
    public static long method_11871(net.minecraft.class_2761 p) {
        return p.comp_3219();
    }

    /**
     * getDayTime(): 1.21.1 carried the day time negated (and -1 for 0) when daylight did not advance;
     * 1.21.2 carries the flag on its own -- the same value, encoded as 1.21.1 encoded it.
     */
    public static long method_11873(net.minecraft.class_2761 p) {
        long day = p.comp_3220();
        if (p.comp_3221()) {
            return day;
        }
        long v = -day;
        return v == 0L ? -1L : v;
    }

    // ------------------------------------------------------------------ ClientboundExplodePacket (class_2664)

    /**
     * new ClientboundExplodePacket(x, y, z, power, toBlow, knockback, blockInteraction, small, large, sound):
     * 1.21.1's client made the explosion seen with the large particle when the explosion broke blocks and
     * had radius 2 or more, else the small one, and pushed the player by the knockback; 1.21.2's packet
     * carries the centre, the knockback, the one particle and the sound. The blocks 1.21.1's client broke
     * itself are the server's to update in 1.21.2 (it sends block updates).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2664 newExplode(double x, double y, double z, float power, List toBlow, net.minecraft.class_243 knockback,
                                                      net.minecraft.class_1927.class_4179 interaction, net.minecraft.class_2394 small,
                                                      net.minecraft.class_2394 large, net.minecraft.class_6880 sound) {
        boolean blocks = interaction != net.minecraft.class_1927.class_4179.field_40878;
        net.minecraft.class_2394 particle = !(power < 2.0F) && blocks ? large : small;
        return new net.minecraft.class_2664(new net.minecraft.class_243(x, y, z), Optional.ofNullable(knockback), particle, sound);
    }

    /** getX() */
    public static double method_11475(net.minecraft.class_2664 p) {
        return p.comp_2883().field_1352;
    }

    /** getY() */
    public static double method_11477(net.minecraft.class_2664 p) {
        return p.comp_2883().field_1351;
    }

    /** getZ() */
    public static double method_11478(net.minecraft.class_2664 p) {
        return p.comp_2883().field_1350;
    }

    /** getKnockbackX(): 0 when the player is not pushed, as 1.21.1's null knockback read */
    public static float method_11472(net.minecraft.class_2664 p) {
        return (float) p.comp_2884().map(v -> v.field_1352).orElse(0.0).doubleValue();
    }

    /** getKnockbackY() */
    public static float method_11473(net.minecraft.class_2664 p) {
        return (float) p.comp_2884().map(v -> v.field_1351).orElse(0.0).doubleValue();
    }

    /** getKnockbackZ() */
    public static float method_11474(net.minecraft.class_2664 p) {
        return (float) p.comp_2884().map(v -> v.field_1350).orElse(0.0).doubleValue();
    }

    /** getExplosionSound() */
    public static net.minecraft.class_6880<net.minecraft.class_3414> method_55331(net.minecraft.class_2664 p) {
        return p.comp_2886();
    }

    /**
     * getSmallExplosionParticles() / getLargeExplosionParticles(): the one particle 1.21.2's packet
     * carries is the one 1.21.1's client chose of the two, so both answer it.
     */
    public static net.minecraft.class_2394 method_55329(net.minecraft.class_2664 p) {
        return p.comp_2885();
    }

    /** getLargeExplosionParticles() */
    public static net.minecraft.class_2394 method_55330(net.minecraft.class_2664 p) {
        return p.comp_2885();
    }

    // ------------------------------------------------------------------ ServerboundPlayerInputPacket (class_2851)

    /**
     * new ServerboundPlayerInputPacket(xxa, zza, jumping, shiftKeyDown): 1.21.1 sent a passenger's steering
     * impulses; 1.21.2 sends the keys: an impulse's sign is its key (forward/backward, left/right), jump and
     * shift as given, no sprint -- the keys from which 1.21.2's server computes the same steering.
     */
    public static net.minecraft.class_2851 newPlayerInput(float xxa, float zza, boolean jumping, boolean shift) {
        return new net.minecraft.class_2851(new net.minecraft.class_10185(zza > 0.0F, zza < 0.0F, xxa > 0.0F, xxa < 0.0F, jumping, shift, false));
    }

    /** getXxa(): left is +1, right -1, both or neither 0 (1.21.1's impulse of the keys) */
    public static float method_12372(net.minecraft.class_2851 p) {
        net.minecraft.class_10185 in = p.comp_3139();
        return in.comp_3161() == in.comp_3162() ? 0.0F : (in.comp_3161() ? 1.0F : -1.0F);
    }

    /** getZza() */
    public static float method_12373(net.minecraft.class_2851 p) {
        net.minecraft.class_10185 in = p.comp_3139();
        return in.comp_3159() == in.comp_3160() ? 0.0F : (in.comp_3159() ? 1.0F : -1.0F);
    }

    /** isJumping() */
    public static boolean method_12371(net.minecraft.class_2851 p) {
        return p.comp_3139().comp_3163();
    }

    /** isShiftKeyDown() */
    public static boolean method_12370(net.minecraft.class_2851 p) {
        return p.comp_3139().comp_3164();
    }

    // ------------------------------------------------------------------ small record changes

    /** new ClientboundCooldownPacket(item, duration): 1.21.2 cools down the item's cooldown group, the item's id by default */
    public static net.minecraft.class_2656 newCooldown(net.minecraft.class_1792 item, int duration) {
        return new net.minecraft.class_2656(net.minecraft.class_7923.field_41178.method_10221(item), duration);
    }

    /** ClientboundCooldownPacket.item(): the item of the cooldown group's id (the item's own group) */
    public static net.minecraft.class_1792 cooldownItem(net.minecraft.class_2656 p) {
        return net.minecraft.class_7923.field_41178.method_63535(p.comp_3082());
    }

    /** a list of 1.21.1 explosion block positions (no 1.21.2 counterpart): handed back as given */
    static List<Object> copy(List<?> list) {
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    // ------------------------------------------------------------------ ServerboundMovePlayerPacket

    /**
     * The horizontal-collision flag 1.21.2 added to the movement packets, for a packet a 1.21.1 mod builds:
     * 1.21.1's packet had no such flag and its server never took one from the client, so the packet says
     * none (false) -- the server's own collision check of the move applies.
     */
    public static boolean noHorizontalCollision() {
        return false;
    }

    // ------------------------------------------------------------------ values 1.21.2 records gained

    /**
     * CommonPlayerSpawnInfo's sea level (1.21.2 sends the level's, a value 1.21.1 did not send): 1.21.1's
     * client used 63 for every level (Level.getSeaLevel was that constant), so a spawn info a 1.21.1 mod
     * builds carries 63.
     */
    public static int legacySeaLevel() {
        return 63;
    }

    /**
     * ClientInformation's particle status (1.21.2 tells the server the client's setting): a ClientInformation
     * a 1.21.1 mod builds says ALL, what 1.21.2's own default (ClientInformation.createDefault) says.
     */
    public static net.minecraft.class_4066 defaultParticleStatus() {
        return net.minecraft.class_4066.field_18197;
    }

    /**
     * ClientboundPlayerInfoUpdatePacket.Entry's tab-list order (new in 1.21.2): 0, the order every player has
     * unless a 1.21.2 mod gives one (ServerPlayer.getTabListOrder), which leaves 1.21.1's listing by name.
     */
    public static int defaultListOrder() {
        return 0;
    }

    /**
     * strictErrorHandling() of 1.21.1's ClientboundGameProfilePacket / CommonListenerCookie: 1.21.1's server
     * always sent true, and 1.21.2's client always disconnects on a packet error (1.21.1's behaviour for
     * true), so the flag is true.
     */
    public static boolean strictErrorHandling(net.minecraft.class_2901 packet) {
        return true;
    }


    /** GameConfig.GameData's captureTracyImages (new in 1.21.2): false, 1.21.2's default without the launch option */
    public static boolean noTracyCapture() {
        return false;
    }
}
