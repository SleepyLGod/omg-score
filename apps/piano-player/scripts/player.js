
MIDI.loader = new widgets.Loader({ message: "Loading: Soundfont...", background: "rgba(16,18,21,0.88)" });
var localTranscriptionApiUrl = "http://localhost:8084/transcription/audioToMidiWithFile";
var transcriptionApiUrl = window.OMG_TRANSCRIPTION_API_URL || defaultTranscriptionApiUrl();
var activeMidiUrl = null;
var convertedMidiUrl = null;
var localMidiUrl = null;
var midiReady = false;
var loopEnabled = false;
var loopRestartQueued = false;
var uploadFileInput = document.getElementById("audio-file");
var midiFileInput = document.getElementById("midi-file");
var convertButton = document.getElementById("convert-button");
var uploadStatus = document.getElementById("upload-status");
var conversionResult = document.getElementById("conversion-result");
var conversionResultText = document.getElementById("conversion-result-text");
var downloadMidiLink = document.getElementById("download-midi-link");
var retryConvertButton = document.getElementById("retry-convert-button");
var fileName = document.getElementById("file-name");
var midiFileName = document.getElementById("midi-file-name");
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
var restartButton = document.getElementById("restart-button");
var loopButton = document.getElementById("loop-button");
var resetViewButton = document.getElementById("reset-view-button");
var lowerKeyRow = document.getElementById("lower-key-row");
var middleKeyRow = document.getElementById("middle-key-row");
var upperKeyRow = document.getElementById("upper-key-row");
var noteNames = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"];
var keyboardLayoutRows = [
    {
        element: lowerKeyRow,
        keys: [
            { label: "Z", code: 90, offset: 0 },
            { label: "S", code: 83, offset: 1 },
            { label: "X", code: 88, offset: 2 },
            { label: "D", code: 68, offset: 3 },
            { label: "C", code: 67, offset: 4 },
            { label: "V", code: 86, offset: 5 },
            { label: "G", code: 71, offset: 6 },
            { label: "B", code: 66, offset: 7 },
            { label: "H", code: 72, offset: 8 },
            { label: "N", code: 78, offset: 9 },
            { label: "J", code: 74, offset: 10 },
            { label: "M", code: 77, offset: 11 },
            { label: ",", code: 188, offset: 12 }
        ]
    },
    {
        element: middleKeyRow,
        keys: [
            { label: "Q", code: 81, offset: 12 },
            { label: "2", code: 50, offset: 13 },
            { label: "W", code: 87, offset: 14 },
            { label: "3", code: 51, offset: 15 },
            { label: "E", code: 69, offset: 16 },
            { label: "R", code: 82, offset: 17 },
            { label: "5", code: 53, offset: 18 },
            { label: "T", code: 84, offset: 19 },
            { label: "6", code: 54, offset: 20 },
            { label: "Y", code: 89, offset: 21 },
            { label: "7", code: 55, offset: 22 },
            { label: "U", code: 85, offset: 23 }
        ]
    },
    {
        element: upperKeyRow,
        keys: [
            { label: "I", code: 73, offset: 24 },
            { label: "9", code: 57, offset: 25 },
            { label: "O", code: 79, offset: 26 },
            { label: "0", code: 48, offset: 27 },
            { label: "P", code: 80, offset: 28 },
            { label: "[", code: 219, offset: 29 },
            { label: "=", code: 187, offset: 30 },
            { label: "]", code: 221, offset: 31 }
        ]
    }
];
var keyCodeToOffset = {};

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

function hideConversionResult() {
    conversionResult.hidden = true;
    conversionResultText.textContent = "No MIDI generated yet";
    downloadMidiLink.removeAttribute("href");
    downloadMidiLink.removeAttribute("download");
}

function showConversionResult(downloadName, blob) {
    conversionResult.hidden = false;
    conversionResultText.textContent = downloadName + " (" + formatBytes(blob.size) + ")";
    downloadMidiLink.href = convertedMidiUrl;
    downloadMidiLink.download = downloadName;
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

function formatBytes(bytes) {
    if (!bytes) return "0 B";
    if (bytes < 1024) return bytes + " B";
    var kilobytes = bytes / 1024;
    if (kilobytes < 1024) return kilobytes.toFixed(1) + " KB";
    return (kilobytes / 1024).toFixed(1) + " MB";
}

function noteNameForOffset(offset) {
    var absoluteNote = controls.octave * 12 + offset;
    return noteNames[absoluteNote % 12] + Math.floor(absoluteNote / 12);
}

function renderKeyboardMap() {
    keyCodeToOffset = {};
    keyboardLayoutRows.forEach(function (row) {
        row.element.innerHTML = "";
        row.keys.forEach(function (key) {
            keyCodeToOffset[key.code] = key.offset;

            var keyLabel = document.createElement("span");
            keyLabel.textContent = key.label;

            var noteLabel = document.createElement("small");
            noteLabel.textContent = noteNameForOffset(key.offset);

            var keyElement = document.createElement("kbd");
            keyElement.appendChild(keyLabel);
            keyElement.appendChild(noteLabel);
            row.element.appendChild(keyElement);
        });
    });
}

function downloadNameFromSongName(songName) {
    return songName + ".mid";
}

function loadMidiFile(url, start) {
    activeMidiUrl = url;
    MIDI.Player.stop();
    releaseKeyboardNotes();
    setPlaybackState(false);
    MIDI.Player.timeWarp = 1 / controls.playbackSpeed;
    MIDI.Player.loadFile(url, function () {
        if (start) {
            startPlayback();
        }
    });
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

function setPlaybackState(isPlaying) {
    playButton.textContent = isPlaying ? "Pause" : "Play";
    playButton.setAttribute("aria-label", isPlaying ? "Pause" : "Play");
}

function startPlayback() {
    if (!activeMidiUrl || !midiReady) return;
    MIDI.Player.resume();
    setPlaybackState(true);
}

function pausePlayback() {
    MIDI.Player.pause();
    setPlaybackState(false);
}

function playPausePlayback() {
    if (MIDI.Player.playing) {
        pausePlayback();
    } else {
        startPlayback();
    }
}

function stopPlayback() {
    MIDI.Player.stop();
    releaseKeyboardNotes();
    setPlaybackState(false);
}

function restartPlayback() {
    if (!activeMidiUrl || !midiReady) return;
    loopRestartQueued = false;
    loadMidiFile(activeMidiUrl, true);
}

function setLoopEnabled(enabled) {
    loopEnabled = enabled;
    loopButton.classList.toggle("is-active", loopEnabled);
    loopButton.setAttribute("aria-pressed", loopEnabled ? "true" : "false");
}

function resetCameraView() {
    camera.position.set(-3.35, 5.0, 11.5);
    cameraControls.target.set(4.5, 0, 0);
    cameraControls.update(0);
}

function setupPlaybackAnimation() {
    MIDI.Player.setAnimation({
        interval: 250,
        callback: function (data) {
            if (!MIDI.Player.playing || !data.end) return;

            var reachedEnd = data.now >= data.end;
            if (!reachedEnd) {
                loopRestartQueued = false;
                return;
            }

            if (loopEnabled && !loopRestartQueued) {
                loopRestartQueued = true;
                window.setTimeout(restartPlayback, 0);
            } else if (!loopEnabled) {
                stopPlayback();
            }
        }
    });
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
    hideConversionResult();
    setUploadStatus(file ? "Ready to convert" : "Waiting");
    updateUploadButton();
};

midiFileInput.onchange = function () {
    var file = midiFileInput.files[0];
    midiFileName.textContent = file ? file.name : "No MIDI selected";
    if (!file) return;

    if (!midiReady) {
        setUploadStatus("Wait for soundfont, then open MIDI again.");
        return;
    }

    if (localMidiUrl) {
        URL.revokeObjectURL(localMidiUrl);
    }
    localMidiUrl = URL.createObjectURL(file);
    hideConversionResult();
    setUploadStatus("Local MIDI loaded");
    activeSongTitle.textContent = songNameFromFile(file);
    loadMidiFile(localMidiUrl, true);
};

retryConvertButton.onclick = function () {
    if (!convertButton.disabled) {
        convertButton.click();
    }
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
    var songName = songNameFromFile(file);
    formData.append("songName", songName);

    convertButton.disabled = true;
    hideConversionResult();
    setUploadStatus("Converting. This may take a minute.");

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
        var downloadName = downloadNameFromSongName(songName);
        setUploadStatus("Converted and loaded");
        showConversionResult(downloadName, blob);
        activeSongTitle.textContent = songName;
    }).catch(function (error) {
        console.error(error);
        setUploadStatus(conversionErrorMessage(error));
    }).finally(updateUploadButton);
};

function conversionErrorMessage(error) {
    var message = error && error.message ? error.message : "";
    if (message.indexOf("Failed to fetch") >= 0 || message.indexOf("NetworkError") >= 0) {
        return "Backend unavailable. Start Docker and retry.";
    }
    if (message.indexOf("Unsupported audio format") >= 0) {
        return "Unsupported file. Upload MP3 or WAV.";
    }
    if (message.indexOf("ONNX model not found") >= 0 || message.indexOf("missing model") >= 0) {
        return "Backend model missing. Check .isolation/models/transcription.onnx.";
    }
    if (message.indexOf("ffmpeg") >= 0 || message.indexOf("audio decode") >= 0) {
        return "Audio decode failed. Try another MP3/WAV file.";
    }
    if (message.indexOf("HTTP 500") >= 0) {
        return "Backend conversion failed. Check Docker logs.";
    }
    return message || "Conversion failed.";
}
    
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
        playPausePlayback();
    };
    this.stop = function ()//停止至开始
    {
        stopPlayback();
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
    if (typeof keyCodeToOffset[keyCode] !== "number") return -1;
    return keyCodeToOffset[keyCode] + controls.octave * 12;
    
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
    renderKeyboardMap();
    songSelect.onchange = function () {
        controls.song = songSelect.value;
        activeSongTitle.textContent = songTitleFromFile(controls.song);
        if (midiReady) {
            loadMidiFile("./vendor/MIDI/midi/" + controls.song, true);
        }
    };
    playButton.onclick = controls.play;
    stopButton.onclick = controls.stop;
    restartButton.onclick = restartPlayback;
    loopButton.onclick = function () {
        setLoopEnabled(!loopEnabled);
    };
    resetViewButton.onclick = resetCameraView;
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
        renderKeyboardMap();
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
        setupPlaybackAnimation();

        // Close the MIDI loader widget once the custom controls are ready.
        MIDI.loader.stop();
    });
};
    
window.addEventListener('resize', on_window_resize, false);
