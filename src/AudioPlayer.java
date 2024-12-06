import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip backgroundMusic;

    // Metode untuk memutar musik latar
    public void playBackgroundMusic(String filePath) {
        new Thread(() -> {
            try {
                File file = new File(filePath);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusic.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Metode untuk menghentikan musik latar
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    // Metode untuk memutar efek suara
    public void playSoundEffect(String filePath) {
        new Thread(() -> {
            try {
                File file = new File(filePath);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start(); // Mainkan efek suara
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
