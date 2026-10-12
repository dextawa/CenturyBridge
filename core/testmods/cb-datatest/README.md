# cb-datatest

A 1.20.4-era mod (intermediary names, fabric-lifecycle-events-v1 only) with tracked entity data
written every way 1.20.4 allowed it:

- `Probe` defines in `defineSynchedData()` and again in its constructor after `super(..)`;
- `ProbeChild` extends `Probe` and chains to its mod parent's `defineSynchedData()`;
- `ProbeCloud` extends vanilla `AreaEffectCloud` and chains to vanilla's.

When the server has started it constructs each, reads, sets and packs the data (a cat variant
among it, which 1.20.5's serializer carries as a holder), sends it through the 1.20.4 wire format
(`DataValue.write` / `DataValue.read`) into a second instance, checks a duplicate define is refused
with 1.20.4's message, and spawns one into the overworld.

`NetTest` then works the 1.20.4 packet surface: `write(buf)` / `new P(buf)` round trips (a plain
class, a packet 1.20.5 made a record, one carrying an item stack through a registry codec),
`Packet.write` through the interface, a 1.20.4 subclass of the record-ified cooldown packet (its
getter override answers, `super(buf)` and super getters work), a unit packet, `nextProtocol()`, and
a mod's own `Packet` implementation (written through the interface, given a `type()`).

Every check prints a `CBDATATEST OK` / `CBDATATEST FAIL` line; `CBDATATEST DONE` ends a clean run.

Build against 1.20.4 (`--release 17`), convert one step, boot a 1.20.5 Fabric server with
fabric-api and the 1.20.5 step layer:

    javac --release 17 -cp "data/jars/client-1.20.4-intermediary.jar:<client-1.20.4 libraries>:<fabric-lifecycle-events-v1 + fabric-api-base jars>" \
          -d /tmp/dt core/testmods/cb-datatest/src/cb/datatest/*.java
    cp core/testmods/cb-datatest/res/fabric.mod.json /tmp/dt/ && (cd /tmp/dt && jar cf cb-datatest.jar .)
    java -Dcb.wiring=core/segments/steps/1.20.4__1.20.5.json -cp core/build/classes:<asm+gson> \
         top.dext.centurybridge.Main convert core/segments 1.20.4 1.20.5 /tmp/dt/conv /tmp/dt/cb-datatest.jar
