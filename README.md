<h3 align="center">
    <img src="https://readme-typing-svg.herokuapp.com/?font=Roboto+Mono&size=25&width=240&color=46BEA3duration=1600&lines=🎵Omg+Player🎶" height="80"/></br>
    <font>✨ Transferring piano pieces and playing them dynamically in the browser. 🌠</font>
</h3>

<div align="center">
  <p>
    <!-- ?style=flat-square -->
    <a href="#"><img src="https://custom-icon-badges.herokuapp.com/github/last-commit/SleepyLGod/omg-score" alt="omg-score"/></a>
    <a href="#"><img src="https://img.shields.io/badge/all_contributors-0x4-orange.svg" alt="omg-score"/></a>
    <a href="#"><img src="https://circleci.com/gh/codesandbox/codesandbox-client.svg?style=svg" alt="omg-score"/></a>
    <a href="#"><img src="https://www.browserstack.com/automate/badge.svg" alt="omg-score"/></a>
    <a href="#"><img src="https://img.shields.io/badge/PRs-welcome-gold.svg" alt="omg-score"/></a>
    <a href="#"><img src="https://img.shields.io/badge/first--timers--only-friendly-blue.svg" alt="omg-score"/></a>
  </p>
</div>  

## ⚡ Quick setup
```bash
git clone git@github.com:SleepyLGod/omg-score.git
```

## 🧊 Isolated Docker run

This path does not require installing Node, Java, Maven, or FFmpeg on the host.
Runtime caches, the ONNX model, and generated files stay under `.isolation/`.

```bash
mkdir -p .isolation/models
curl -L -o .isolation/models/transcription.onnx \
  https://github.com/EveElseIf/pianotranscription_java/releases/download/blob/transcription.onnx
docker compose up --build
```

Open the 3D piano frontend at:

```text
http://localhost:8080
```

The transcription backend listens on:

```text
http://localhost:8084
```

The MP3 upload flow uses `POST /transcription/mp3ToMidiWithFile` and returns the
generated MIDI file directly to the browser.

Stop the isolated services with:

```bash
docker compose down
```

If conversion fails with a missing model error, confirm that
`.isolation/models/transcription.onnx` exists before starting Compose.

## ⚙ Basic
+ The project is divided into 3 parts.
+ The [OmgSimplePlayer](./OmgSimplePlayer/) is the web applet to play standard midi files, and you can change the tempo.
+ The [OmgPianoPlayer](./OmgPianoPlayer) and [OmgPianoTranscription](./OmgPianoTranscription) is the web applet to convert mp3 files to midi files of piano pieces, and play them dynamically. The former is the frontend and the latter is the backend. You can see a three-dimensional piano model and adjust its spatial position freely. When the piano is being played, you can see the keys move with the notes, and you can also download the converted midi files and use them for other playback editors and editors.

## 🔨 Tasks
- [x] Convert mp3 files to standard midi files.
- [ ] Convert songs and pieces in other formats like wav to standard midi files.
- [x] Choose and upload local files freely.
- [x] Play all kinds of standard midi files in the web page simply.
- [x] Change the song tempo in the simple player.
- [x] Play midi files(piano pieces) in the web page dynamically.
- [ ] Change the song tempo in the dynamic player.
- [ ] Play midi files of various musical instruments in the web page dynamically.
- [ ] Modify the converting and transmitting speed.

## 🔪 Tools
+ Three.js
+ MIDI.js
+ Piano transcription inference
+ Maven & springboot

## 🙋‍♂️ Support
💙 If you like this project, give it a ⭐ and share it with friends!
