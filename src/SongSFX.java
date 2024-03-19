import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * CLASS
 * <p>Determines, sets and handles all audio.</p>
 * <p>Note: A lot of the parameters are UNUSED or have values set manually. A later update will allow them to be set up dynamically. Hopefully.</p>
 * Date: Mar 10, 2023
 *
 * @author Grand-Arcanium
 * @version 1.5
 */
// Realistically, there would be multiple arrays of sfx as lot as most games have 1 or more cues, and 1 or more sfx for determining "barely" hits, misses and other added sfx.
public class SongSFX {
    // Song List
    private int numOfSongs;
    private String[] mainSongs;
    private String practiceSong;

    // Delay before the file plays each song, based from the RH Editor
    private static double[] songStartDelay = new double[]{53, 33, 38, 48, 49, 34, 35, 38, 37, 35, 34, 41};
    private static double pracStartDelay = 57;

    // Song Components
    private Pane root;
    private Media media;
    private MediaPlayer player;
    private MediaView mediaView;

    // Random Song Holders
    private AudioClip song1;
    private AudioClip song2;

    // SFX List
    private String[] gameSfx; // SFX exclusive to the game: a catch or a miss
    private String[] conditionSfx; // SFX for everywhere else: end of the game, or high score

    // SFX Components
    private AudioClip catchSound;
    private AudioClip missSound;
    private AudioClip sadSound;
    private AudioClip gameOverSound;
    private AudioClip highScoreSound;

    // Volume of the song(s) and sfx (all songs of a given type (song or sfx) have the same initial volume, so these would adjust all of them)
    private double song_vol;
    private double sfx_vol;


    /**
     * CONSTRUCTOR
     * <p>Creates the sequences of the song sfx.</p>
     *
     * @param root the root where the Media Player is applied to.
     * */
    public SongSFX(Pane root) throws Exception {
        // General Setup
        this.root = root;
        sfx_vol = 0.40;
        song_vol = 0.75;

        // SFX Setup
        gameSfx = new String[]{"/audio/sfx_catch.mp3", "/audio/sfx_miss.mp3", "/audio/sfx_disappoint.mp3"};
        conditionSfx = new String[]{"/audio/sfx_gameover.mp3", "/audio/sfx_highscore.mp3"};

        // Song Setup
        numOfSongs = 12;
        mainSongs = new String[numOfSongs]; // 12 audio files are available
        for (int i = 0; i < numOfSongs; i++) {
            mainSongs[i] = "/audio/toss_main_" + (i + 1) + ".mp3";
        }
        practiceSong = "/audio/toss_practice.mp3";

        // SFX setup
        catchSound = new AudioClip(getClass().getResource(gameSfx[0]).toURI().toString());
        missSound = new AudioClip(getClass().getResource(gameSfx[1]).toURI().toString());
        sadSound = new AudioClip(getClass().getResource(gameSfx[2]).toURI().toString());
        gameOverSound = new AudioClip(getClass().getResource(conditionSfx[0]).toURI().toString());
        highScoreSound = new AudioClip(getClass().getResource(conditionSfx[1]).toURI().toString());

        // setting the volume
        catchSound.setVolume(sfx_vol);
        missSound.setVolume(sfx_vol);
        sadSound.setVolume(sfx_vol);
        gameOverSound.setVolume(sfx_vol);
        highScoreSound.setVolume(sfx_vol);

    }

    // *** Delays
    /**
     * Gets the initial delay of the main songs
     *
     * @param index index of the song (this and the song array's indexes correspond to each other)
     */
    public static double getSongStartDelay(int index){
        return songStartDelay[index];
    }

    /**
     * Gets the initial delay of the practice song
     */
    public static double getPracStartDelay(){
        return pracStartDelay;
    }

    // *** SFX Methods
    /**
     * Plays a main song, whose file names are stored in an array.
     *
     * @param currentSong the index of the song to be played
     */
    public void playMainSong(int currentSong) throws Exception {
        AudioClip song = new AudioClip(getClass().getResource(mainSongs[currentSong]).toURI().toString());
        song.setVolume(song_vol);

        song.play();
    }

    /**
     * Plays the first of a randomized group of two songs.
     */
    public void playRandMainSong1() throws Exception {
        song1.setVolume(song_vol);
        song1.play();
    }

    /**
     * Plays the second of a randomized group of two songs.
     */
    public void playRandMainSong2() throws Exception {
        song2.setVolume(song_vol);
        song2.play();
    }

    /**
     * Plays the practice song.
     */
    public void playPracSong() throws Exception {
        AudioClip song = new AudioClip(getClass().getResource(practiceSong).toURI().toString());
        song.setVolume(song_vol);

        song.play();
    }

    /**
     * Plays the hit SFX.
     */
    public void playHitSfx() {
        catchSound.play();
    }

    /**
     * Plays the hit SFX.
     */
    public void playMissSfx() {
        missSound.play();
    }

    /**
     * Plays the hit SFX.
     */
    public void playSadSfx() {
        sadSound.play();
    }

    /**
     * Plays the hit SFX.
     */
    public void playGameOver() {
        gameOverSound.play();

    }

    /**
     * Plays the hit SFX.
     */
    public void playHighScore() {
        highScoreSound.play();

    }

    // Misc. Methods
    /**
     * UNUSED - Stops all audio from playing.
     *
     */
    public void stopSong() throws Exception {
        highScoreSound.stop();
        gameOverSound.stop();
        catchSound.stop();
        missSound.stop();
        sadSound.stop();
    }

    /**
     * Creates a randomized index based on the number of songs / 2 (because songs are played in sets of 2), then sets up the songs with said index, and index + 1
     */
    public void randomizer() throws Exception {
        int rand = (int)(Math.random() * (numOfSongs / 2)); // generates a rand int from 0 to 5
        int randIndex = rand * 2; // 2 songs per group

        song1 = new AudioClip(getClass().getResource(mainSongs[randIndex]).toURI().toString());
        song2 = new AudioClip(getClass().getResource(mainSongs[randIndex + 1]).toURI().toString()); // +1 is the index after

    }


}
