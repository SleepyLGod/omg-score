package pianotranscriptioncli;

public class Utils {
    public static short[] toShortLE(byte[] bytes) {
        short[] output = new short[bytes.length / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            var x = ((bytes[i + 1]) & 0xff) << 8;
            var y = bytes[i] & 0xff;
            output[i / 2] = (short) (x | y);
        }
        return output;
    }

    public static float[] normalizeShort(short[] shorts) {
        var output = new float[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            output[i] = (float) shorts[i] / 32767;
        }
        return output;
    }
}
