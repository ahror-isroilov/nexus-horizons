package utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * author: ahror
 * <p>
 * since: 8/30/24
 */
public final class AudioUtils {
    private static final ExecutorService audioThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ConcurrentHashMap<String, SoundPool> soundPools = new ConcurrentHashMap<>();
    private static final int DEFAULT_POOL_SIZE = 15;
    private static final long COOLDOWN_TIME = 100;

    public static void preloadSound(String source, int poolSize) {
        soundPools.computeIfAbsent(source, k -> new SoundPool(source, poolSize));
    }

    public static void play(String source) {
        audioThreadPool.submit(() -> {
            SoundPool pool = soundPools.computeIfAbsent(source, k -> new SoundPool(source, DEFAULT_POOL_SIZE));
            pool.play();
        });
    }

    public static void stopAll() {
        soundPools.values().forEach(SoundPool::stopAll);
    }

    public static void shutdown() {
        stopAll();
        soundPools.values().forEach(SoundPool::close);
        soundPools.clear();
        audioThreadPool.shutdown();
    }

    private static class SoundPool {
        private final Clip[] clips;
        private final AtomicInteger nextIndex = new AtomicInteger(0);
        private final AtomicLong lastPlayTime = new AtomicLong(0);

        public SoundPool(String source, int size) {
            clips = new Clip[size];
            for (int i = 0; i < size; i++) {
                clips[i] = loadSound(source);
            }
        }

        public void play() {
            long currentTime = System.currentTimeMillis();
            long lastTime = lastPlayTime.get();
            if (currentTime - lastTime < COOLDOWN_TIME) {
                return; // Skip this play request if it's too soon after the last one
            }

            if (lastPlayTime.compareAndSet(lastTime, currentTime)) {
                int index = nextIndex.getAndIncrement() % clips.length;
                Clip clip = clips[index];
                if (clip != null) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.setFramePosition(0);
                    clip.start();
                }
            }
        }

        public void stopAll() {
            for (Clip clip : clips) {
                if (clip != null) {
                    clip.stop();
                }
            }
        }

        public void close() {
            for (Clip clip : clips) {
                if (clip != null) {
                    clip.close();
                }
            }
        }
    }

    private static Clip loadSound(String source) {
        URL soundURL = AudioUtils.class.getResource("/sounds/" + source);
        if (soundURL == null) {
            System.err.println("Sound file not found: " + source);
            return null;
        }

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL)) {
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound " + source + ": " + e.getMessage());
            return null;
        }
    }
}
