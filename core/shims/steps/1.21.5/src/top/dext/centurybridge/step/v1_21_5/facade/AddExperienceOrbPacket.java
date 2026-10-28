package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.UUID;
import net.minecraft.class_1299;
import net.minecraft.class_1303;
import net.minecraft.class_243;
import net.minecraft.class_2540;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2604;
import net.minecraft.class_3231;
import net.minecraft.class_9139;

/**
 * 1.21.4's ClientboundAddExperienceOrbPacket (net/minecraft/class_2606), gone in 1.21.5: an experience orb is spawned
 * on the client by ClientboundAddEntityPacket (class_2604) like any entity, and its value travels as entity data
 * (ExperienceOrb.DATA_VALUE, class_1303.field_55950, sent with the pairing data). javap -c -p 1.21.4 class_2606 /
 * 1.21.5 class_2604, class_1303.
 *
 * Converted references name this class (classRenames). It is a 1.21.5 ClientboundAddEntityPacket for the orb, so a
 * packet a 1.21.4 mod builds (an orb subclass's getAddEntityPacket, a packet it sends) goes out as 1.21.5 sends an
 * orb, and handling it runs 1.21.5's handleAddEntity. It also keeps 1.21.4's own fields (id, x, y, z, value: the
 * 1.21.4 constructor's values) and answers 1.21.4's getters from them. STREAM_CODEC is 1.21.4's wire form (varint id,
 * three doubles, short value), for mods that write or read the packet themselves.
 */
public class AddExperienceOrbPacket extends class_2604 {
    /** STREAM_CODEC: 1.21.4's encoding (write / the buffer constructor) */
    public static final class_9139<class_2540, AddExperienceOrbPacket> field_47897 =
        class_2596.method_56443(AddExperienceOrbPacket::method_55856, AddExperienceOrbPacket::new);

    /** id */
    public final int field_11974;
    /** x */
    public final double field_11972;
    /** y */
    public final double field_11971;
    /** z */
    public final double field_11970;
    /** value */
    public final int field_11973;

    /** 1.21.4: id, the server entity's base position, the orb's value */
    public AddExperienceOrbPacket(class_1303 orb, class_3231 serverEntity) {
        super(orb, serverEntity);
        class_243 pos = serverEntity.method_60942();
        this.field_11974 = orb.method_5628();
        this.field_11972 = pos.field_1352;
        this.field_11971 = pos.field_1351;
        this.field_11970 = pos.field_1350;
        this.field_11973 = orb.method_5919();
    }

    /** 1.21.4's buffer constructor (private there; public: a facade's members stand in for the class's) */
    public AddExperienceOrbPacket(class_2540 buf) {
        this(Wire.read(buf));
    }

    private AddExperienceOrbPacket(Wire w) {
        // 1.21.4's wire form has no UUID: one derived from the id (1.21.5's client keys the entity by id)
        super(w.id, new UUID(0L, w.id), w.x, w.y, w.z, 0.0F, 0.0F, class_1299.field_6044, 0, class_243.field_1353, 0.0);
        this.field_11974 = w.id;
        this.field_11972 = w.x;
        this.field_11971 = w.y;
        this.field_11970 = w.z;
        this.field_11973 = w.value;
    }

    /** 1.21.4's wire order: varint id, x, y, z, short value */
    private record Wire(int id, double x, double y, double z, int value) {
        static Wire read(class_2540 buf) {
            int id = buf.method_10816();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            return new Wire(id, x, y, z, buf.readShort());
        }
    }

    /** write (private in 1.21.4) */
    public void method_55856(class_2540 buf) {
        buf.method_10804(this.field_11974);
        buf.method_52940(this.field_11972);
        buf.method_52940(this.field_11971);
        buf.method_52940(this.field_11970);
        buf.method_52998(this.field_11973);
    }

    /** handle(listener): 1.21.4 called handleAddExperienceOrb; 1.21.5's listener spawns the orb from handleAddEntity */
    public void method_11182(class_2602 listener) {
        listener.method_11112(this);
    }

    /** 1.21.5's handle (Packet.handle reaches it): through 1.21.4's handle, so a mod subclass's override of it runs */
    @Override
    public void method_11178(class_2602 listener) {
        this.method_11182(listener);
    }

    /** getId */
    public int method_11183() {
        return this.field_11974;
    }

    /** getX */
    public double method_11185() {
        return this.field_11972;
    }

    /** getY */
    public double method_11181() {
        return this.field_11971;
    }

    /** getZ */
    public double method_11180() {
        return this.field_11970;
    }

    /** getValue */
    public int method_11184() {
        return this.field_11973;
    }
}
