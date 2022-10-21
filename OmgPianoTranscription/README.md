# pianotranscription_java
This project is an implement of [piano_transcription_inference](https://github.com/qiuqiangkong/piano_transcription_inference).

This project only requires ffmpeg, make sure you have installed ffmpeg in PATH.

# How to run
1. Clone this repo, download [ONNX](https://github.com/EveElseIf/pianotranscription_java/releases/download/blob/transcription.onnx) file and put it on the root of this repo.
2. Open this repo using IntelliJ IDEA or other IDEs, modify the input audio file path in the main function of pianotranscriptioncli/src/main/java/pianotranscriptioncli/Program.java
3. Build and run, the output file will be named "out.mid".
