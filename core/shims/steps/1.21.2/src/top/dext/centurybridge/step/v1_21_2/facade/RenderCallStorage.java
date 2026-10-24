package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's RenderCallStorage (net/minecraft/class_4491, classRenames), gone in 1.21.2: a ring of four queues of
 * render calls, one recording while another is processed. 1.21.1's bodies (it touches nothing else of the game);
 * every member 1.21.1 declared keeps its name, public (a mod's access widener cannot reach a CenturyBridge class).
 */
@Environment(EnvType.CLIENT)
public class RenderCallStorage {

    public final List<ConcurrentLinkedQueue<net.minecraft.class_4573>> field_20453 = ImmutableList.of(
        new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
    public volatile boolean field_31899;
    public volatile int field_20454;
    public volatile boolean field_31900;
    public volatile int field_20455;
    public volatile int field_20456;

    public RenderCallStorage() {
        this.field_20454 = this.field_20455 = this.field_20456 + 1;
    }

    /** canBeginRecording() */
    public boolean method_35599() {
        return !this.field_31899 && this.field_20454 == this.field_20455;
    }

    /** beginRecording() */
    public boolean method_35601() {
        if (this.field_31899) {
            throw new RuntimeException("ALREADY RECORDING !!!");
        }
        if (this.method_35599()) {
            this.field_20454 = (this.field_20455 + 1) % this.field_20453.size();
            this.field_31899 = true;
            return true;
        }
        return false;
    }

    /** recordRenderCall(call) */
    public void method_35600(net.minecraft.class_4573 call) {
        if (!this.field_31899) {
            throw new RuntimeException("NOT RECORDING !!!");
        }
        ConcurrentLinkedQueue<net.minecraft.class_4573> queue = this.method_35608();
        queue.add(call);
    }

    /** endRecording() */
    public void method_35602() {
        if (this.field_31899) {
            this.field_31899 = false;
        } else {
            throw new RuntimeException("NOT RECORDING !!!");
        }
    }

    /** canBeginProcessing() */
    public boolean method_35603() {
        return !this.field_31900 && this.field_20454 != this.field_20455;
    }

    /** beginProcessing() */
    public boolean method_35604() {
        if (this.field_31900) {
            throw new RuntimeException("ALREADY PROCESSING !!!");
        }
        if (this.method_35603()) {
            this.field_31900 = true;
            return true;
        }
        return false;
    }

    /** process() */
    public void method_35605() {
        if (!this.field_31900) {
            throw new RuntimeException("NOT PROCESSING !!!");
        }
    }

    /** endProcessing() */
    public void method_35606() {
        if (this.field_31900) {
            this.field_31900 = false;
            this.field_20456 = this.field_20455;
            this.field_20455 = this.field_20454;
        } else {
            throw new RuntimeException("NOT PROCESSING !!!");
        }
    }

    /** startCollectingQueue() */
    public ConcurrentLinkedQueue<net.minecraft.class_4573> method_35607() {
        return this.field_20453.get(this.field_20456);
    }

    /** getRecordingQueue() */
    public ConcurrentLinkedQueue<net.minecraft.class_4573> method_35608() {
        return this.field_20453.get(this.field_20454);
    }

    /** getProcessedQueue() */
    public ConcurrentLinkedQueue<net.minecraft.class_4573> method_35609() {
        return this.field_20453.get(this.field_20455);
    }
}
