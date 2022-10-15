# &#9836; OmgParser

**OmgParser** is a JavaScript applet which reads standard MIDI files, emits JSON events in real time, and then converts the events and play them in the web page.

## Getting Started

Using MidiWriterJS is pretty simple.  Create a new player by instantiating `MidiPlayer.Player` with an event handler to be called for every MIDI event.  Then you can load and play a MIDI file.

```js
const MidiPlayer = require('midi-player-js');

// Initialize player and register event handler
const Player = new MidiPlayer.Player(function(event) {
	console.log(event);
});

// Load a MIDI file
Player.loadFile('./test.mid');
Player.play();
```

## Player Events

There are a handful of events on the `Player` object which you can subscribe to using the `Player.on()` method.  Some events pass data as the first argument of the callback as described below:

```js 
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

## Full Documentation

[**Doc on my lark**](https://cao8drqmwu.feishu.cn/docx/AMq0djEQSoxPWNxjWM4cL6uBnOf "文档链接")

[Some test midi files](https://www.midishow.com/en/midi/5506.html "免费midi")

To be updated... 👀
