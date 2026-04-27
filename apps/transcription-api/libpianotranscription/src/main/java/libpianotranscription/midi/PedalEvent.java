package libpianotranscription.midi;

public class PedalEvent {//踏板事件,记录每一个pedalEvents对象的开始时刻和结束时刻
    private final float onsetTime;
    private final float offsetTime;

    public PedalEvent(float onsetTime, float offsetTime) {
        this.onsetTime = onsetTime;
        this.offsetTime = offsetTime;
    }

    public float getOffsetTime() {
        return offsetTime;
    }

    public float getOnsetTime() {
        return onsetTime;
    }
}