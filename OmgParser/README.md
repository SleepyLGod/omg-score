# &#9836; OmgParser

OmgParser is a JavaScript applet which reads standard MIDI files and emits JSON events in real time.  

This player does not generate any audio, but by attaching a handler to the event emitter you can trigger any code you like which could play audio, control visualizations, feed into a MIDI interface, etc.

## Demos

To be updated... 👀

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


## Full API Documentation

To be updated... 👀
