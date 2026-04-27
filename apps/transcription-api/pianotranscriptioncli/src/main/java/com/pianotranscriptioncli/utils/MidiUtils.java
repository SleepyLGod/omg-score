package com.pianotranscriptioncli.utils;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiUtils {

    /**
     * 读取并播放midi文件
     * &#064;参考文档  <a href="https://docs.oracle.com/javase/7/docs/api/javax/sound/midi/package-summary.html">...</a>
     * @param output 完整路径（带后缀）
     */
    public static void Reduce(String output) {
        try {
            Sequence sequence = MidiSystem.getSequence(new File(output));
            long length = sequence.getMicrosecondLength(); // 获取序列的总时间（微秒）
            int trackCount = sequence.getTracks().length; // 获取序列的音轨数
            float divType = sequence.getDivisionType(); // 获取序列的（计时方式？）
            int resolution = sequence.getResolution(); // 获取序列的时间解析度

            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo(); // 获取所有 midi 设备的信息
            Sequencer sequencer = MidiSystem.getSequencer(); // 获取默认的音序器
            Synthesizer synthsizer = MidiSystem.getSynthesizer(); // 获取默认的合成器
            Receiver receiver = MidiSystem.getReceiver(); // 获取默认的接收器
            Transmitter transmitter = MidiSystem.getTransmitter(); // 获取默认的传输器
            if(sequencer == null) {
                throw new IOException("未找到可用音序器！");
            }
            sequencer.setSequence(sequence); // 设置midi序列

            sequencer.start(); // 开始播放当前序列

            sequencer.stop(); // 停止播放当前序列

//            sequencer.setTempoFactor(float factor); // 设置速度比率 (1.0f 为原速)
//
//            sequencer.setMicrosecondPosition(long microseconds); // 设置播放位置到指定微秒
//
//            sequencer.setTrackMute(int track, boolean mute); // 开启或关闭一条音轨的静音模式
//
//            sequencer.setTrackSolo(int track, boolean solo); // 开启或关闭一条音轨的独奏模式

        } catch (InvalidMidiDataException | IOException | MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

}
