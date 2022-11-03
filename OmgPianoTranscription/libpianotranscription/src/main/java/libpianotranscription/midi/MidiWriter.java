package libpianotranscription.midi;

import javax.sound.midi.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MidiWriter {
    public static byte[] writeEventsToMidi(int startTime, List<NoteEvent> noteEvents, List<PedalEvent> pedalEvents) throws InvalidMidiDataException, IOException {
        var ticksPerBeat = 384;
        var beatsPerSecond = 2;
        var ticksPerSecond = ticksPerBeat * beatsPerSecond;
        var microsecondsPerBeat = (int) (1e6 / beatsPerSecond);

        // create midi sequence with 384 ticks per beat
        var s = new Sequence(Sequence.PPQ, ticksPerBeat);

        // track 0
        var t0 = s.createTrack();
        // set tempo
        var m1 = new MetaMessage();
        var b1 = new byte[]{(byte) (microsecondsPerBeat >> 16), (byte) (microsecondsPerBeat >> 8 & 0xff), (byte) (microsecondsPerBeat & 0xff)};
        m1.setMessage(0x51, b1, 3);
        var me1 = new MidiEvent(m1, 0);
        t0.add(me1);
        //set time signature
        var m2 = new MetaMessage();
        var b2 = new byte[]{0x4, 0x2, 0x18, 0x8};
        m2.setMessage(0x58, b2, 4);
        var me2 = new MidiEvent(m2, 0);
        t0.add(me2);
        //set end of track
        var m3 = new MetaMessage();
        var b3 = new byte[]{};
        m3.setMessage(0x2f, b3, 0);
        var me3 = new MidiEvent(m3, 1);
        t0.add(me3);

        // track 1
        var t1 = s.createTrack();

        // generate midi message roll
        var roll = new ArrayList<MidiMessage>();
        for (var note : noteEvents) {
            roll.add(new MidiMessage(note.getOnsetTime(), note.getMidiNote(), note.getVelocity(), 0));
            roll.add(new MidiMessage(note.getOffsetTime(), note.getMidiNote(), 0, 0));
        }
        if (pedalEvents.size() != 0) {
            var controlChange = 64;
            for (var pedal : pedalEvents) {
                roll.add(new MidiMessage(pedal.getOnsetTime(), controlChange, 127, 1));
                roll.add(new MidiMessage(pedal.getOffsetTime(), controlChange, 0, 1));
            }
        }
        roll.sort(Comparator.comparing(x -> x.getA()));

        // write midi message to track 1
        // var previousTicks = 0;
        for (var m : roll) {
            var thisTicks = (int) ((m.getA() - startTime) * ticksPerSecond);
            if (thisTicks >= 0) {
                // var diffTicks = thisTicks - previousTicks;
                // previousTicks = thisTicks;
                if (m.getType() == 0) {
                    var sm = new ShortMessage();
                    sm.setMessage(0x90, m.getB(), m.getC());
                    var me = new MidiEvent(sm, thisTicks);
                    t1.add(me);
                }
            }
        }
        var m4 = new MetaMessage();
        m4.setMessage(0x2f, new byte[0], 0x00);
        var me4 = new MidiEvent(m4, 1);
        t1.add(me4);

        var stream = new ByteArrayOutputStream();
        MidiSystem.write(s, 1, stream);
        return stream.toByteArray();
    }
}

class MidiMessage {
    private final float a;
    private final int b;
    private final int c;
    private final int type;

    /**
     * @param a    time
     * @param b    midinote or control change(64)
     * @param c    velocity or value
     * @param type 0 for midi note and 1 for control change
     */
    public MidiMessage(float a, int b, int c, int type) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.type = type;
    }

    public float getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getType() {
        return type;
    }
}
