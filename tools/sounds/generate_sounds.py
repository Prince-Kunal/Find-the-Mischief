#!/usr/bin/env python3
"""
Generates the app's original sound effects from pure sine waves and noise.
Nothing is downloaded or sampled, so the sounds are ours to ship.

    python3 tools/sounds/generate_sounds.py

Writes 16-bit mono WAV files into app/src/main/res/raw/. Uses only the Python
standard library. Android's SoundPool plays WAV directly, so no conversion is needed.
"""
import math
import random
import struct
import wave
from pathlib import Path

RATE = 44100
OUT = Path(__file__).resolve().parents[2] / "app" / "src" / "main" / "res" / "raw"


def silence(seconds):
    return [0.0] * int(RATE * seconds)


def mix_into(buf, samples, start_s=0.0, gain=1.0):
    start = int(start_s * RATE)
    for i, s in enumerate(samples):
        if start + i < len(buf):
            buf[start + i] += s * gain


def bell(freq, seconds, decay, attack=0.004, detune=0.0, vibrato=0.0):
    """A sine 'bell' with a soft attack and exponential decay. A detuned twin adds shimmer."""
    out = []
    for i in range(int(RATE * seconds)):
        t = i / RATE
        env = min(1.0, t / attack) * math.exp(-t / decay)
        wobble = 1.0 + vibrato * math.sin(2 * math.pi * 6.0 * t)
        s = math.sin(2 * math.pi * freq * wobble * t)
        if detune:
            s = 0.5 * s + 0.5 * math.sin(2 * math.pi * (freq + detune) * t)
        out.append(s * env)
    return out


def fade_out(buf, seconds):
    n = int(RATE * seconds)
    for i in range(min(n, len(buf))):
        buf[-1 - i] *= i / n


def normalise(buf, peak=0.89):
    top = max(abs(s) for s in buf) or 1.0
    return [s * peak / top for s in buf]


def write(name, buf, peak=0.89):
    buf = normalise(buf, peak)
    OUT.mkdir(parents=True, exist_ok=True)
    path = OUT / f"{name}.wav"
    with wave.open(str(path), "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(RATE)
        w.writeframes(b"".join(struct.pack("<h", int(max(-1, min(1, s)) * 32767)) for s in buf))
    print(f"{path.relative_to(OUT.parents[4])}  {len(buf) / RATE:.2f}s")


def pass_chime():
    """< 0.5 s: a quick rising sparkle of three bell tones (E6, B6, E7) with shimmer."""
    buf = silence(0.45)
    for k, (f, start) in enumerate([(1318.5, 0.0), (1975.5, 0.05), (2637.0, 0.10)]):
        mix_into(buf, bell(f, 0.45 - start, decay=0.12, detune=4.0), start, gain=1.0 - 0.15 * k)
        mix_into(buf, bell(f * 2.01, 0.45 - start, decay=0.05), start, gain=0.15)
    fade_out(buf, 0.04)
    return buf


def reveal_glow():
    """A soft swell: a low drone that blooms upwards, like a rune stone lighting up."""
    seconds = 0.6
    buf = []
    phase = [0.0, 0.0, 0.0]
    for i in range(int(RATE * seconds)):
        t = i / RATE
        env = math.sin(math.pi * min(1.0, t / seconds)) ** 1.5
        f0 = 330 + 220 * (t / seconds) ** 0.7
        s = 0.0
        for j, (mult, g) in enumerate([(1.0, 1.0), (1.5, 0.45), (2.0, 0.3)]):
            phase[j] += 2 * math.pi * f0 * mult / RATE
            s += g * math.sin(phase[j])
        buf.append(s * env)
    mix_into(buf, bell(1760, 0.3, decay=0.08, detune=3.0), 0.28, gain=0.25)
    return buf


def drum_hit(seconds=0.55):
    """A low hand-drum thump: a falling sine plus a short burst of noise."""
    rnd = random.Random(7)
    out, phase = [], 0.0
    for i in range(int(RATE * seconds)):
        t = i / RATE
        freq = 55 + 70 * math.exp(-t / 0.05)
        phase += 2 * math.pi * freq / RATE
        body = math.sin(phase) * math.exp(-t / 0.22)
        skin = (rnd.random() * 2 - 1) * math.exp(-t / 0.012) * 0.35
        out.append(body + skin)
    return out


def judgement_drum():
    """Two drum beats, the second heavier: 'ba-DOOM'."""
    buf = silence(0.85)
    mix_into(buf, drum_hit(0.4), 0.0, gain=0.55)
    mix_into(buf, drum_hit(0.6), 0.22, gain=1.0)
    fade_out(buf, 0.05)
    return buf


def timer_end():
    """A small gong: inharmonic partials that ring and fade."""
    buf = silence(0.9)
    for mult, gain, decay in [(1.0, 1.0, 0.35), (2.76, 0.5, 0.2), (5.40, 0.25, 0.1), (8.93, 0.1, 0.06)]:
        mix_into(buf, bell(220 * mult, 0.9, decay=decay, attack=0.002, detune=1.5), 0.0, gain)
    fade_out(buf, 0.08)
    return buf


if __name__ == "__main__":
    write("pass_chime", pass_chime())
    write("reveal_glow", reveal_glow(), peak=0.6)
    write("judgement_drum", judgement_drum())
    write("timer_end", timer_end())
