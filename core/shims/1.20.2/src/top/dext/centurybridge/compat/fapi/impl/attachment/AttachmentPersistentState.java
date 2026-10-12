package top.dext.centurybridge.compat.fapi.impl.attachment;

/**
 * 1.20.1's AttachmentPersistentState: a world's persistent attachments, saved with the world's other
 * persistent data under "fabric_attachments" (the id Fabric API uses), dirty while there is something
 * to save or something was saved before.
 */
public class AttachmentPersistentState extends net.minecraft.class_18 {

    public static final String ID = "fabric_attachments";

    private final AttachmentTargetImpl worldTarget;
    private final boolean wasSerialized;

    public AttachmentPersistentState(net.minecraft.class_3218 world) {
        this.worldTarget = (AttachmentTargetImpl) world;
        this.wasSerialized = worldTarget.cb$hasPersistentAttachments();
    }

    public static AttachmentPersistentState read(net.minecraft.class_3218 world, net.minecraft.class_2487 nbt) {
        ((AttachmentTargetImpl) world).cb$readAttachmentsFromNbt(nbt);
        return new AttachmentPersistentState(world);
    }

    @Override
    public boolean method_79() {
        return wasSerialized || worldTarget.cb$hasPersistentAttachments();
    }

    @Override
    public net.minecraft.class_2487 method_75(net.minecraft.class_2487 nbt) {
        worldTarget.cb$writeAttachmentsToNbt(nbt);
        return nbt;
    }
}
