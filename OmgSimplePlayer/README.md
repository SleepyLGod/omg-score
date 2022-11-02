<h3 align="center">
    <img src="https://readme-typing-svg.herokuapp.com/?font=Roboto+Mono&size=25&width=350&color=46BEA3duration=1600&lines=🎵Omg+Simple+Player🎶" height="80"/></br>
    A JS applet reading standard MIDI files, emits JSON events in real time and plays them in the browser.
</h3>

## ⚡ Getting Started
Create a new player by instantiating `MidiPlayer.Player` with an event handler to be called for every MIDI event, then you can load and play a MIDI file.

Using MidiWriterJS is pretty simple.

```javascript
const MidiPlayer = require('midi-player-js');

// Initialize player and register event handler
const Player = new MidiPlayer.Player(function(event) {
	console.log(event);
});

// Load a MIDI file
Player.loadFile('./test.mid');
Player.play();
```

## 🎼 Player Events

There are a handful of events on the `Player` object which you can subscribe to using the `Player.on()` method. 

Some events pass data as the first argument of the callback as described below:

```javascript 
Player.on('fileLoaded', function() {
    // Do something when file is loaded
});

Player.on('playing', function(currentTick) {
    // Do something while player is playing
    // (this is repeatedly triggered within the play loop)
});

Player.on('midiEvent', function(event) {
    // Do something when a MIDI event is fired.
    // (this is the same as passing a function to MidiPlayer.Player() when instantiating.
});

Player.on('endOfFile', function() {
    // Do something when end of the file has been reached.
});
```

Note that because of a common practice called "running status" many MIDI files may use `Note on` events with `0` velocity in place of `Note off` events.

## 📕 Full Documentation & Resources

[**&#9836; Doc on my lark**](https://cao8drqmwu.feishu.cn/docx/AMq0djEQSoxPWNxjWM4cL6uBnOf "文档链接")

[Some test midi files](https://www.midishow.com/en/midi/5506.html "免费midi")

To be updated... 👀
