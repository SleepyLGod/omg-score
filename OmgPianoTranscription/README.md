<h3 align="center">
    <img src="https://readme-typing-svg.herokuapp.com/?font=Roboto+Mono&size=25&width=430&color=46BEA3duration=1600&lines=🎵Omg+Piano+Transcription🎶" height="80"/></br>
    ⭐ The project is an implement of <a href="https://github.com/qiuqiangkong/piano_transcription_inference">piano transcription inference</a>. 🌠
</h3>

## 🔨 Build & Run
+ **FFMPEG** is required, please [install](https://www.gyan.dev/ffmpeg/builds/) it in PATH.
+ Download [**ONNX file**](https://github.com/EveElseIf/pianotranscription_java/releases/download/blob/transcription.onnx) and put it on the root of Folder`pianotranscriptioncli/src/main/resources`.
+ Modify the input audio file path in the main function of `pianotranscriptioncli/src/main/java/pianotranscriptioncli/Program.java`
+ Well, if your running system is not Windows, you need to change the string of the file path of the input and output files to make it work.
+ Build and run the project, feel free to run the [`test.http`](./pianotranscriptioncli/src/main/resources/test.http) file in the resource folder if you use IDEA as the IDE.

  And you'll find the output midi file in Folder`pianotranscriptioncli/src/main/resources/output`. 

  By the way, you can see the process on the command lines in your terminal.
+ 🎉 Go bears! Just enjoy it!
