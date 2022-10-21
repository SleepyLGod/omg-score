# 🎶 OmgPianoTranscription
⭐ The project is an implement of [**piano transcription inference**](https://github.com/qiuqiangkong/piano_transcription_inference).

### Build & Run
+ **FFMPEG** is required, please [install](https://www.gyan.dev/ffmpeg/builds/) it in PATH.
+ Download [**ONNX file**](https://github.com/EveElseIf/pianotranscription_java/releases/download/blob/transcription.onnx) and put it on the root of Folder`pianotranscriptioncli/src/main/resources`.
+ Modify the input audio file path in the main function of `pianotranscriptioncli/src/main/java/pianotranscriptioncli/Program.java`
+ Build and run the project, and you'll find the output midi file in Folder`pianotranscriptioncli/src/main/resources/output`.
