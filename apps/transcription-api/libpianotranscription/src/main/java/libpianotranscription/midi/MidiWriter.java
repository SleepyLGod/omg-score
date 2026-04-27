package libpianotranscription.midi;

import javax.sound.midi.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MidiWriter {
    /*
         MIDIEvent 包含一条 MIDI 消息和一个以刻度表示的相应时间戳，并且可以表示存储在 MIDI 文件或序列对象中的 MIDI 事件信息。
         滴答的持续时间由 MIDI 文件或序列对象中包含的计时信息指定。
         在 Java Sound 中，MidiEvent 对象通常包含在 Track 中，Tracks 也同样包含在 Sequence 中
         此MidiWriter.java部分的主要功能就在于将MP3文件经过转化后得到的数据以字节流的形式进行存储并作为返回值返回,这是 MIDI 文件在计算机中存储的一种数据形式
         此外也完成了将 MIDI 类型的文件的字节流写入提供的输出流
     */
    public static byte[] writeEventsToMidi(int startTime, List<NoteEvent> noteEvents, List<PedalEvent> pedalEvents) throws InvalidMidiDataException, IOException {
        var ticksPerBeat = 384;    //设置每节拍滴答频率
        var beatsPerSecond = 2;   //设置每秒节拍数
        var ticksPerSecond = ticksPerBeat * beatsPerSecond;  //每秒滴答频率=每秒节拍数*每节拍滴答频率
        var microsecondsPerBeat = (int) (1e6 / beatsPerSecond);  //每节拍微秒时长= 每秒节拍数的倒数 * 1e6(化为微秒单位)

        // create midi sequence with 384 ticks per beat --- 创建每节拍 384 tick 的 midi 序列
        var s = new Sequence(Sequence.PPQ, ticksPerBeat);

        // track 0   在该midi序列s下创建第一条轨道t0
        var t0 = s.createTrack();
        // set tempo  设定节奏
        var m1 = new MetaMessage();
        /*
        MIDI 标准用字节表示 MIDI 数据。但是，由于 JavaTM 使用带符号字节，所以 Java Sound API 表示 MIDI 数据时使用整数而不是字节。
        例如，MidiMessage 的 getStatus() 方法返回用整数表示的 MIDI 状态字节。
        如果处理来源于 Java Sound 之外的 MIDI 数据，而现在又编码为带符号字节，可使用以下转换将字节转换为整数:int i = (int)(byte & 0xFF)
         */
        var b1 = new byte[]{(byte) (microsecondsPerBeat >> 16), (byte) (microsecondsPerBeat >> 8 & 0xff), (byte) (microsecondsPerBeat & 0xff)};
        /* 对m1 设置 MetaMessage 的消息参数。由于元消息只允许一个状态字节值 0xFF，因此这里不需要指定。对所有元消息的 getStatus 调用返回 0xFF。
        MIDI 最核心的功能是用于传输实时的音乐演奏信息，这些信息本质上是一条条包含了音高、力度、效果器参数等信息的指令，我们将这些指令称之为 MIDI 消息（MIDI message）。
        一条 MIDI 消息通常由数个字节组成，其中第一个字节被称为 STATUS byte，
        其后面有跟有数个 DATA bytes。STATUS byte 第七位为 1，而 DATA byte 第七位为 0。
        形参：
        type - 元消息类型（必须小于 128） , 应该是 MetaMessage 中状态字节之后的字节的有效值
        data - MIDI 消息中的数据字节  , 应该包含 MetaMessage 的所有后续字节。换句话说，指定 MetaMessage 类型的字节不被视为数据字节
        length - 数据字节数组中的字节数  */
        m1.setMessage(0x51, b1, 3);
        /*
        构造一个新的 MidiEvent。
        message – event中包含的 MIDI
        tick – 事件的时间戳，以 MIDI tick 为单位
         */
        var me1 = new MidiEvent(m1, 0);
        //将me1加入到轨道t0中
        t0.add(me1);
        //set time signature---设置时间签名
        var m2 = new MetaMessage();
        var b2 = new byte[]{0x4, 0x2, 0x18, 0x8};
        m2.setMessage(0x58, b2, 4);
        var me2 = new MidiEvent(m2, 0);
        t0.add(me2);  //将me2加入到轨道t0中
        //set end of track --- 设置t0轨道的末尾me3
        var m3 = new MetaMessage();
        var b3 = new byte[]{};
        m3.setMessage(0x2f, b3, 0);
        var me3 = new MidiEvent(m3, 1);
        t0.add(me3);

        // track 1
        var t1 = s.createTrack();

        // generate midi message roll --- 生成 midi 信息 roll
        /*  MidiMessage的四个参数：
         * @param a    time
         * @param b    midinote or control change(64)
         * @param c    velocity or value
         * @param type :0 表示 midi note ; 1 表示 control change
         */
        var roll = new ArrayList<MidiMessage>();
        for (var note : noteEvents) {
            roll.add(new MidiMessage(note.getOnsetTime(), note.getMidiNote(), note.getVelocity(), 0));
            roll.add(new MidiMessage(note.getOffsetTime(), note.getMidiNote(), 0, 0));
        }
        if (pedalEvents.size() != 0) {
            var controlChange = 64;  //共64个音色库可进行选择
            for (var pedal : pedalEvents) {   //对于每一个pedalEvents对象,记录其开始时刻和结束时刻
                roll.add(new MidiMessage(pedal.getOnsetTime(), controlChange, 127, 1));
                roll.add(new MidiMessage(pedal.getOffsetTime(), controlChange, 0, 1)); //力度0就是音符关（Note off）
            }
        }
        roll.sort(Comparator.comparing(x -> x.getA()));    //根据参数a,即OnsetTime对roll中加入的几个MidiMessage对象进行排序

        // write midi message to track 1
        //var previousTicks = 0;
        for (var m : roll) {
            var thisTicks = (int) ((m.getA() - startTime) * ticksPerSecond);//对于每一个roll中的对象计算其总ticks数
            if (thisTicks >= 0) {
                //var diffTicks = thisTicks - previousTicks;
                //previousTicks = thisTicks;
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
        /*
        MidiSystem 类提供对已安装的 MIDI 系统资源的访问，包括合成器、音序器和 MIDI 输入和输出端口等设备。
        一个典型的简单 MIDI 应用程序可能首先调用一个或多个 MidiSystem 方法来了解安装了哪些设备并获取该应用程序所需的设备。
        该类还具有用于读取包含标准 MIDI 文件数据或音库的文件、流和 URL 的方法。我们可以在 MidiSystem 中查询指定 MIDI 文件的格式。
         write方法:将表示 MIDI 文件类型的文件的字节流写入提供的输出流。
         形参:
              @param  in - 包含要写入文件的 MIDI 数据的序列
              @param  fileType - 要写入输出流的文件的文件类型
              @param  out - 应该写入文件数据的流
         */
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