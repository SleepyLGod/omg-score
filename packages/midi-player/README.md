# MIDI Player Package

A JavaScript MIDI parser/player package. It reads standard MIDI files, emits
JSON events in real time, and can be used by browser or Node-based players.

This package lives at `packages/midi-player` inside the OMG Score repository.

## Development

```bash
cd packages/midi-player
npm ci
npm test
```

## Dependency Audit

The package avoids known vulnerable development-tooling chains by removing the
unused `watch` dependency, using JSDoc 4, and overriding Mocha's transitive
`serialize-javascript` dependency to a patched release.

## ⚡ Getting Started
Create a new player by instantiating `MidiPlayer.Player` with an event handler to be called for every MIDI event, then you can load and play a MIDI file.

Using the player is pretty simple.

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
