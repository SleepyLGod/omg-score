package libpianotranscription.midi;

public class NoteEvent {
    private final float onsetTime;
    private final float offsetTime;
    private final int midiNote;
    private final int velocity;

    public NoteEvent(float onsetTime, float offsetTime, int midiNote, int velocity) {
        this.onsetTime = onsetTime;
        this.offsetTime = offsetTime;
        this.midiNote = midiNote;
        this.velocity = velocity;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getMidiNote() {
        return midiNote;
    }

    public float getOffsetTime() {
        return offsetTime;
    }

    public float getOnsetTime() {
        return onsetTime;
    }
}