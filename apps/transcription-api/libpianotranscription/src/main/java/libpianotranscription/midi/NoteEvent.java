package libpianotranscription.midi;

public class NoteEvent {
    private final float onsetTime;  //启动时间
    private final float offsetTime;  //终止时间
    private final int midiNote;    //midi记录
    private final int velocity;  //速度

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