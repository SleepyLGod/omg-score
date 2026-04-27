
MIDI.loader = new widgets.Loader({ message: "Loading: Soundfont...", background: "rgba(16,18,21,0.88)" });
var localTranscriptionApiUrl = "http://localhost:8084/transcription/audioToMidiWithFile";
var transcriptionApiUrl = window.OMG_TRANSCRIPTION_API_URL || defaultTranscriptionApiUrl();
var activeMidiUrl = null;
var convertedMidiUrl = null;
var midiReady = false;
var uploadFileInput = document.getElementById("audio-file");
var convertButton = document.getElementById("convert-button");
var uploadStatus = document.getElementById("upload-status");
var fileName = document.getElementById("file-name");
var midiStatus = document.getElementById("midi-status");
var midiStatusText = document.getElementById("midi-status-text");
var songSelect = document.getElementById("song-select");
var activeSongTitle = document.getElementById("active-song-title");
var speedSlider = document.getElementById("speed-slider");
var speedValue = document.getElementById("speed-value");
var octaveSlider = document.getElementById("octave-slider");
var octaveValue = document.getElementById("octave-value");
var keyboardOctaveValue = document.getElementById("keyboard-octave-value");
var noteColorInput = document.getElementById("note-color");
var playButton = document.getElementById("play-button");
var stopButton = document.getElementById("stop-button");

function defaultTranscriptionApiUrl() {
    var host = window.location.hostname;
    if (host === "localhost" || host === "127.0.0.1" || host === "") {
        return localTranscriptionApiUrl;
    }
    return "";
}

function setUploadStatus(message) {
    uploadStatus.textContent = message;
}

function setMidiStatus(message, statusClass) {
    midiStatus.className = "status-pill" + (statusClass ? " " + statusClass : "");
    midiStatusText.textContent = message;
}

function updateUploadButton() {
    convertButton.disabled = !midiReady || !uploadFileInput.files.length;
}

function songNameFromFile(file) {
    return file.name.replace(/\.[^/.]+$/, "") || "upload";
}

function loadMidiFile(url, start) {
    activeMidiUrl = url;
    MIDI.Player.stop();
    MIDI.Player.timeWarp = 1 / controls.playbackSpeed;
    MIDI.Player.loadFile(url, start ? MIDI.Player.start : undefined);
}

function loadConvertedMidi(blob) {
    if (convertedMidiUrl) {
        URL.revokeObjectURL(convertedMidiUrl);
    }
    convertedMidiUrl = URL.createObjectURL(blob);
    loadMidiFile(convertedMidiUrl, true);
}

function reloadActiveMidi() {
    if (activeMidiUrl) {
        loadMidiFile(activeMidiUrl, true);
    }
}

function responseError(response) {
    return response.text().then(function (message) {
        message = message || response.statusText || "Request failed";
        throw new Error("HTTP " + response.status + ": " + message);
    });
}

uploadFileInput.onchange = function () {
    var file = uploadFileInput.files[0];
    fileName.textContent = file ? file.name : "No file selected";
    updateUploadButton();
};
convertButton.onclick = function () {
    var file = uploadFileInput.files[0];
    if (!file) return;
    if (!transcriptionApiUrl) {
        setUploadStatus("Needs local Docker backend");
        return;
    }

    var formData = new FormData();
    formData.append("file", file);
    formData.append("songName", songNameFromFile(file));

    convertButton.disabled = true;
    setUploadStatus("Converting");

    fetch(transcriptionApiUrl, {
        method: "POST",
        body: formData
    }).then(function (response) {
        if (!response.ok) {
            return responseError(response);
        }
        return response.blob();
    }).then(function (blob) {
        loadConvertedMidi(blob);
        setUploadStatus("Loaded");
        activeSongTitle.textContent = songNameFromFile(file);
    }).catch(function (error) {
        console.error(error);
        setUploadStatus(error.message || "Failed");
    }).finally(updateUploadButton);
};
    
var scene = new THREE.Scene();
scene.fog = new THREE.Fog(0x08090b, 12, 34);
    
var camera = new THREE.PerspectiveCamera(30, window.innerWidth / window.innerHeight, 2.0, 5000);
camera.position.x = -3.35;
camera.position.z = 11.5;
camera.position.y = 5.0;
    
floor = new THREE.Mesh(new THREE.PlaneGeometry(8000, 8000), new THREE.MeshPhongMaterial({ color: 0x121315, shininess: 16 }));
floor.rotation.x = - 90 * (Math.PI / 180); //桌面与钢琴的夹角，默认为垂直
floor.position.y = -0.52; //桌面的纵坐标
floor.receiveShadow = true; //接受钢琴投影在桌面上
scene.add(floor); //添加到场景中  
    
var spotlight = new THREE.DirectionalLight(0xfff2d6); //平行光强度
spotlight.position.set(1.5, 4.2, -6.5); //光源位置
spotlight.target.position.set(5.2, -5, 6.2); //光源指向
spotlight.shadowDarkness = 0.58; //影子深度
spotlight.intensity = 1.18; //光照强度
spotlight.castShadow = true; //是否产生阴影
//决定有多少像素用来构成阴影
spotlight.shadowMapWidth = 2048;
spotlight.shadowMapHeight = 2048;
//投影相机
spotlight.shadowCameraNear = 5.0; //表示到距离光源的哪一个位置开始生成阴影
spotlight.shadowCameraFar = 20.0; //表示到距离光源的哪一个位置可以生成阴影
spotlight.shadowBias = 0.0025; //解决自遮挡阴影瑕疵(shadow acne)
//投影边界
spotlight.shadowCameraLeft = -8.85;
spotlight.shadowCameraRight = 5.5;
spotlight.shadowCameraTop = 4;
spotlight.shadowCameraBottom = 0;
scene.add(spotlight);
var fillLight = new THREE.DirectionalLight(0x7ce7df, 0.34);
fillLight.position.set(1, 1, 1).normalize();
scene.add(fillLight);
var rimLight = new THREE.DirectionalLight(0xf0b35a, 0.38);
rimLight.position.set(-1, -1, -1).normalize();
scene.add(rimLight);
var ambientLight = new THREE.AmbientLight(0x303236);
scene.add(ambientLight);
    
var controls = new function () {
    this.key_attack_time = 9.0; //按键时间，小的时候有变化感
    this.key_max_rotation = 0.72; //琴键旋转角
    this.octave = 2; //八度
    this.song = "game_of_thrones.mid"; //选择一个预览
    this.playbackSpeed = 1.0;
    this.noteOnColor = [240, 179, 90, 1.0]; //颜色数组
    this.play = function ()//播放
    {
        MIDI.Player.resume();
    };
    this.stop = function ()//停止至开始
    {
        MIDI.Player.stop();
    }
};
var songsToFiles = {
    "Game Of Thrones Theme, Ramin Djawadi": "game_of_thrones.mid",
    "Mario Overworld Theme (Super Mario Bros 3), Koji Kondo": "mario_-_overworld_theme.mid",
    "He's a Pirate (Pirates of the Caribbean), Klaus Badelt": "hes_a_pirate.mid",
    "Hedwigs Theme (Harry Potter), John Williams": "hedwigs_theme.mid",
    "Something There (Beauty and the Beast), Alan Menken": "something_there.mid",
    "Cruel Angel Thesis (Neon Genesis Evangelion)": "cruel_angel__s_thesis.mid",
    "Me cuesta tanto olvidarte (Mecano)": "me_cuesta.mid",
    "Sonata No. 14 C# minor (Moonlight), Beethoven": "mond_1.mid",
    "For Elise, Beethoven": "for_elise_by_beethoven.mid",
    "Asturias (Leyenda), Albeniz": "alb_se5_format0.mid",
    "Aragon (Fantasia), Albeniz": "alb_se6.mid",
    "Prelude and Fugue in C major BWV 846, Bach": "bach_846.mid",
    "Fantasia C major, Schubert": "schub_d760_1.mid",
    "Sonata No. 16 C major, Mozart": "mz_545_1.mid",
    "Sonata No. 11 A major (K331, First Movement), Mozart": "mz_331_1.mid",
    "March - Song of the Lark, Tchaikovsky": "ty_maerz.mid",
    "Piano Sonata in C major, Hoboken, Haydn": "haydn_35_1.mid",
    "Etudes, Opus 25, Chopin": "chpn_op25_e1.mid",
    "Polonaise Ab major, Opus 53, Chopin": "chpn_op53.mid",
    "No. 2 - Oriental, Granados": "gra_esp_2.mid",
    "Bohemian Rhapsody, Queen": "bohemian1.mid",
};

function songTitleFromFile(fileName) {
    for (var title in songsToFiles) {
        if (songsToFiles[title] === fileName) {
            return title;
        }
    }
    return fileName;
}

function populateSongSelect() {
    for (var title in songsToFiles) {
        var option = document.createElement("option");
        option.value = songsToFiles[title];
        option.textContent = title;
        songSelect.appendChild(option);
    }
    songSelect.value = controls.song;
}

function releaseKeyboardNotes() {
    if (!keys_down) return;
    for (keyCode in keys_down) {
        if (typeof keys_down[keyCode] === "number") {
            releasePianoKey(keys_down[keyCode], "keyboard:" + keyCode);
        }
    }
    keys_down = [];
}

function setNoteColorFromHex(hex) {
    var color = new THREE.Color(hex);
    controls.noteOnColor = [
        Math.round(color.r * 255),
        Math.round(color.g * 255),
        Math.round(color.b * 255),
        1.0
    ];
    noteOnColor = color;
}

var keyState = Object.freeze({ unpressed: {}, note_on: {}, pressed: {}, note_off: {} }); //冻结四个要素，不能被修改
    
var renderer = new THREE.WebGLRenderer({ antialias: true }); //开启反锯齿
renderer.setSize(window.innerWidth, window.innerHeight); //设置渲染区域尺寸         
renderer.setClearColor(0x08090b, 1);
renderer.shadowMapEnabled = true;  //阴影效果
renderer.gammaInput = true;
renderer.gammaOutput = true;
renderer.physicallyBasedShading = true;
renderer.domElement.className = "stage-canvas";
document.body.appendChild(renderer.domElement);
     
var material = new THREE.MeshLambertMaterial({ color: 0x606060 }); //颜色没什么关系
    
noteOnColor = new THREE.Color().setRGB(controls.noteOnColor[0] / 256.0, controls.noteOnColor[1] / 256.0, controls.noteOnColor[2] / 256.0);
    
var loader = new THREE.ColladaLoader();
loader.load('./vendor/obj/piano.dae', prepare_scene);
    
var cameraControls = new THREE.OrbitAndPanControls(camera, renderer.domElement); //实现鼠标和键盘控制
cameraControls.target.set(4.5, 0, 0); //三维旋转中心
var raycaster = new THREE.Raycaster();
var pointer = new THREE.Vector2();
var pianoKeyMeshes = [];
var activeKeyHolds = {};
var activePointerKey = null;
    
var clock = new THREE.Clock();
    
function on_window_resize() //设置大小
{
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix(); //每渲染一次重新计算一次
    
    renderer.setSize(window.innerWidth, window.innerHeight); //渲染区域大小
}
//场景准备
function prepare_scene(collada) {
    collada.scene.traverse(initialize_keys);
    scene.add(collada.scene);
}
var keys_down = [];
var keys_obj = [];
    
function initialize_keys(obj) {
    keys_obj.push(obj);
    obj.rotation.x = -Math.PI / 4.0; //旋转度
    obj.rotation.y = 0;
    obj.rotation.z = 0;
    obj.keyState = keyState.unpressed; //未按状态
    obj.clock = new THREE.Clock(false); //设置时钟
    obj.castShadow = true; //阴影
    obj.receiveShadow = true;
    
    // only add meshes in the material redefinition (to make keys change their color when pressed)
    //按键颜色改变
    if (obj instanceof THREE.Mesh) {
        old_material = obj.material; //材质不变
        obj.material = new THREE.MeshPhongMaterial({ color: old_material.color }); //
        obj.material.shininess = 35.0;
        obj.material.specular = new THREE.Color().setRGB(0.25, 0.25, 0.25);
        obj.material.note_off = obj.material.color.clone();
        if (pianoKeyIndexFromObject(obj) !== null) {
            pianoKeyMeshes.push(obj);
        }
    
    }
    
}
//琴键状态
function key_status(keyName, status) {
    var obj = scene.getObjectByName(keyName, true);
    if (obj != undefined) {
        obj.clock.start();
        obj.clock.elapsedTime = 0;
        obj.keyState = status;
    }
}

function pianoKeyName(keyIndex) {
    return "_" + keyIndex;
}

function midiNoteFromPianoKey(keyIndex) {
    return keyIndex + 21;
}

function pianoKeyIndexFromObject(obj) {
    var current = obj;
    while (current) {
        if (/^_\d+$/.test(current.name)) {
            return parseInt(current.name.substring(1), 10);
        }
        current = current.parent;
    }
    return null;
}

function pressPianoKey(keyIndex, holdId) {
    if (typeof keyIndex !== "number") return;
    if (!activeKeyHolds[keyIndex]) {
        activeKeyHolds[keyIndex] = {};
    }
    if (activeKeyHolds[keyIndex][holdId]) return;

    var firstHold = Object.keys(activeKeyHolds[keyIndex]).length === 0;
    activeKeyHolds[keyIndex][holdId] = true;
    if (firstHold) {
        key_status(pianoKeyName(keyIndex), keyState.note_on);
        if (midiReady) {
            MIDI.setVolume(0, 127);
            MIDI.noteOn(0, midiNoteFromPianoKey(keyIndex), 127, 0);
        }
    }
}

function releasePianoKey(keyIndex, holdId) {
    if (typeof keyIndex !== "number" || !activeKeyHolds[keyIndex]) return;
    delete activeKeyHolds[keyIndex][holdId];
    if (Object.keys(activeKeyHolds[keyIndex]).length === 0) {
        delete activeKeyHolds[keyIndex];
        key_status(pianoKeyName(keyIndex), keyState.note_off);
        if (midiReady) {
            MIDI.setVolume(0, 127);
            MIDI.noteOff(0, midiNoteFromPianoKey(keyIndex), 0.08);
        }
    }
}

function keyIndexFromClientPoint(clientX, clientY) {
    var rect = renderer.domElement.getBoundingClientRect();
    pointer.x = ((clientX - rect.left) / rect.width) * 2 - 1;
    pointer.y = -((clientY - rect.top) / rect.height) * 2 + 1;
    raycaster.setFromCamera(pointer, camera);

    var intersections = raycaster.intersectObjects(pianoKeyMeshes, true);
    for (var i = 0; i < intersections.length; i++) {
        var keyIndex = pianoKeyIndexFromObject(intersections[i].object);
        if (keyIndex !== null) {
            return keyIndex;
        }
    }
    return null;
}

function onPianoMouseDown(event) {
    if (event.button !== 0) return;
    var keyIndex = keyIndexFromClientPoint(event.clientX, event.clientY);
    if (keyIndex === null) return;

    event.preventDefault();
    event.stopImmediatePropagation();
    activePointerKey = keyIndex;
    pressPianoKey(keyIndex, "pointer");
    document.addEventListener("mouseup", onPianoMouseUp, true);
}

function onPianoMouseUp() {
    if (activePointerKey !== null) {
        releasePianoKey(activePointerKey, "pointer");
        activePointerKey = null;
    }
    document.removeEventListener("mouseup", onPianoMouseUp, true);
}

function onPianoTouchStart(event) {
    if (!event.changedTouches.length) return;
    var touch = event.changedTouches[0];
    var keyIndex = keyIndexFromClientPoint(touch.clientX, touch.clientY);
    if (keyIndex === null) return;

    event.preventDefault();
    event.stopImmediatePropagation();
    activePointerKey = keyIndex;
    pressPianoKey(keyIndex, "pointer");
}

function onPianoTouchMove(event) {
    if (activePointerKey !== null) {
        event.preventDefault();
        event.stopImmediatePropagation();
    }
}

function onPianoTouchEnd(event) {
    if (activePointerKey !== null) {
        event.preventDefault();
        event.stopImmediatePropagation();
        releasePianoKey(activePointerKey, "pointer");
        activePointerKey = null;
    }
}

function frame() {
    requestAnimationFrame(frame);
    
    var delta = clock.getDelta();
    
    update(delta);
    
    render(delta);
    
}
function smoothstep(a, b, x) {
    if (x < a) return 0.0;
    if (x > b) return 1.0;
    var y = (x - a) / (b - a);
    return y * y * (3.0 - 2.0 * y);
}
    
function mix(a, b, x) {
    return a + (b - a) * Math.min(Math.max(x, 0.0), 1.0);
}
//更新琴键状态***********
function update_key(obj, delta) {
    if (obj.keyState == keyState.note_on) { //按下
        obj.rotation.x = mix(-Math.PI / 4.0, -controls.key_max_rotation, smoothstep(0.0, 1.0, controls.key_attack_time * obj.clock.getElapsedTime())); //根据时间变化
        if (obj.rotation.x >= -controls.key_max_rotation) {
            obj.keyState = keyState.pressed;
            obj.clock.elapsedTime = 0;
        }
        obj.material.color = noteOnColor;
    }
    else if (obj.keyState == keyState.note_off) {
        obj.rotation.x = mix(-controls.key_max_rotation, -Math.PI / 4.0, smoothstep(0.0, 1.0, controls.key_attack_time * obj.clock.getElapsedTime()));
        if (obj.rotation.x <= -Math.PI / 4.0) {
            obj.keyState = keyState.unpressed;
            obj.clock.elapsedTime = 0;
        }
        obj.material.color = obj.material.note_off;
    }
}
//*************
function update(delta) {
    cameraControls.update(delta);
    for (i in keys_obj) {
        update_key(keys_obj[i], delta);
    }
    
}
//渲染
function render(delta) {
    renderer.render(scene, camera);
};
    
frame();
//******************
function keyCode_to_keyIndex(keyCode) {
    var note = -1;
    //-----------------------------------
    if (keyCode == 90) note = 0; // C 0
    if (keyCode == 83) note = 1; // C#0
    if (keyCode == 88) note = 2; // D 0
    if (keyCode == 68) note = 3; // D#0
    if (keyCode == 67) note = 4; // E 0
    if (keyCode == 86) note = 5; // F 0
    if (keyCode == 71) note = 6; // F#0
    if (keyCode == 66) note = 7; // G 0
    if (keyCode == 72) note = 8; // G#0
    if (keyCode == 78) note = 9; // A 0
    if (keyCode == 74) note = 10; // A#0
    if (keyCode == 77) note = 11; // B 0
    if (keyCode == 188) note = 12; // C 0
    
    //-----------------------------------
    if (keyCode == 81) note = 12; // C 1
    if (keyCode == 50) note = 13; // C#1
    if (keyCode == 87) note = 14; // D 1
    if (keyCode == 51) note = 15; // D#1
    if (keyCode == 69) note = 16; // E 1
    if (keyCode == 82) note = 17; // F 1
    if (keyCode == 53) note = 18; // F#1
    if (keyCode == 84) note = 19; // G 1
    if (keyCode == 54) note = 20; // G#1
    if (keyCode == 89) note = 21; // A 1
    if (keyCode == 55) note = 22; // A#1
    if (keyCode == 85) note = 23; // B 1
    //-----------------------------------
    if (keyCode == 73) note = 24; // C 2
    if (keyCode == 57) note = 25; // C#2
    if (keyCode == 79) note = 26; // D 2
    if (keyCode == 48) note = 27; // D#2
    if (keyCode == 80) note = 28; // E 2
    if (keyCode == 219) note = 29; // F 2
    if (keyCode == 187) note = 30; // F#2
    if (keyCode == 221) note = 31; // G 2
    //-----------------------------------
    
    if (note == -1) return -1;
    
    return note + controls.octave * 12;
    
}

function keyCode_to_note(keyCode) {
    var keyIndex = keyCode_to_keyIndex(keyCode);
    if (keyIndex == -1) return -1;
    return pianoKeyName(keyIndex);
}

function isKeyboardInputTarget(target) {
    if (!target || !target.tagName) return false;
    var tagName = target.tagName.toLowerCase();
    return tagName === "input" || tagName === "select" || tagName === "button" || tagName === "textarea";
}
    
window.onkeydown = function (ev) {
    if (isKeyboardInputTarget(ev.target)) return;
    if (typeof keys_down[ev.keyCode] !== "number") {
        var keyIndex = keyCode_to_keyIndex(ev.keyCode);
        if (keyIndex != -1) {
            keys_down[ev.keyCode] = keyIndex;
            pressPianoKey(keyIndex, "keyboard:" + ev.keyCode);
            ev.preventDefault();
        }
    }
}
    
window.onkeyup = function (ev) {
    if (typeof keys_down[ev.keyCode] === "number") {
        releasePianoKey(keys_down[ev.keyCode], "keyboard:" + ev.keyCode);
        delete keys_down[ev.keyCode];
        ev.preventDefault();
    }
    
}

renderer.domElement.addEventListener("mousedown", onPianoMouseDown, true);
renderer.domElement.addEventListener("touchstart", onPianoTouchStart, true);
renderer.domElement.addEventListener("touchmove", onPianoTouchMove, true);
renderer.domElement.addEventListener("touchend", onPianoTouchEnd, true);
renderer.domElement.addEventListener("touchcancel", onPianoTouchEnd, true);
    
window.onload = function () {
    populateSongSelect();
    songSelect.onchange = function () {
        controls.song = songSelect.value;
        activeSongTitle.textContent = songTitleFromFile(controls.song);
        if (midiReady) {
            loadMidiFile("./vendor/MIDI/midi/" + controls.song, true);
        }
    };
    playButton.onclick = controls.play;
    stopButton.onclick = controls.stop;
    speedSlider.oninput = function () {
        controls.playbackSpeed = parseFloat(speedSlider.value);
        speedValue.textContent = controls.playbackSpeed.toFixed(1) + "x";
    };
    speedSlider.onchange = reloadActiveMidi;
    octaveSlider.oninput = function () {
        controls.octave = parseInt(octaveSlider.value, 10);
        octaveValue.textContent = controls.octave;
        keyboardOctaveValue.textContent = controls.octave;
        releaseKeyboardNotes();
    };
    noteColorInput.oninput = function () {
        setNoteColorFromHex(noteColorInput.value);
    };
    MIDI.loadPlugin(function () {
        //MIDI.Player.loadFile(song[0], MIDI.Player.start);
        midiReady = true;
        setMidiStatus("Ready", "status-ready");
        updateUploadButton();
        loadMidiFile("./vendor/MIDI/midi/" + controls.song);
    
        MIDI.Player.addListener(function (data) {
            var pianoKey = data.note - MIDI.pianoKeyOffset - 3;
            if (data.message === 144) {
                key_status("_" + pianoKey, keyState.note_on);
            }
            else {
                key_status("_" + pianoKey, keyState.note_off);
            }
        });

        // Close the MIDI loader widget once the custom controls are ready.
        MIDI.loader.stop();
    });
};
    
window.addEventListener('resize', on_window_resize, false);
