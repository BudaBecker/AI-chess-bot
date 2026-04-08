package UI;

import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Singleton that loads and plays chess sound effects from res/sounds/.
 * Missing files are silently ignored — the game works fine without audio.
 */
public class SoundManager {

    public enum SoundType { MOVE, CAPTURE, CHECK, GAME_OVER }

    private static SoundManager instance;

    private final Clip moveClip;
    private final Clip captureClip;
    private final Clip checkClip;
    private final Clip gameOverClip;

    private SoundManager() {
        moveClip    = loadClip("res/sounds/move.wav");
        captureClip = loadClip("res/sounds/capture.wav");
        checkClip   = loadClip("res/sounds/check.wav");
        gameOverClip = loadClip("res/sounds/gameover.wav");
    }

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    public void play(SoundType type) {
        Clip clip = switch (type) {
            case MOVE      -> moveClip;
            case CAPTURE   -> captureClip;
            case CHECK     -> checkClip;
            case GAME_OVER -> gameOverClip;
        };
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    private static Clip loadClip(String path) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(path)));
            return clip;
        } catch (UnsupportedAudioFileException | LineUnavailableException | java.io.IOException e) {
            return null; // sound file missing or unsupported — silent fallback
        }
    }
}
