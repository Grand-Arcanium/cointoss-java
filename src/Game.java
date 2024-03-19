import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>The Game class that holds the logic for the game.</p>
 * Date: Mar 10, 2023
 *
 * @author Grand-Arcanium
 * @version 1.5
 */
public class Game {

    // General
    private final Stage stage;
    private final Scene endScene;
    private final GraphicsContext end_gc;
    private final Pane root;
    private Timeline beatTimeline; // timeline where the scoring is made

    // Images
    private Image handImage;
    private ImageView handView;


    // Classes
    private final SongSequence mainSeq;
    // private SongSequence pracSeq;
    private final SongSFX mainSfx;

    // States
    private boolean isPractice;
    private boolean isEndOfGame;
    private boolean isNewRecord;
    private boolean isMiss;
    private boolean buttonHit;

    // Randomizer
    private boolean finishedFirstCycle; // whether the player has gone through all songs in order at least once.
    private boolean finishedRandSongs; // if the randomized group of 2 songs are completed or not

    // Scoring
    private int pracCounter; // keeps track of how many successful practice hits
    private int currentScore;
    private int currentHighScore;
    private int currentSong;

    /**
     * CONSTRUCTOR
     * <p>Creates a new game with a default set up for everything.</p>
     *
     * @param stage The stage of the game
     * @param endScene The ending scene that needs to be changed depending on results
     * @param end_gc The ending scene's Graphics Context
     * @param root The root of the game scene; also gets passed to the Sequencer and the SFX player
     * @param highScore The high score stored by the app, and not the current game session
     */
    /*
     * Originally thinking of setting up a separate practice Game vs the main Game,
     * and so the constructor would have a switch that determines whether to create a practice or a main game. Or an overload one.
     * Instead, I'm implementing one practice before everything, so there's only one Game.
     */
    public Game(Stage stage, Scene endScene, GraphicsContext end_gc, Pane root, int highScore) throws Exception {
        this.stage = stage;
        this.endScene = endScene;
        this.end_gc = end_gc;
        this.root = root;
        mainSeq = new SongSequence();
        mainSfx = new SongSFX(root);

        // initializes scoring and misc.
        pracCounter = 0;
        currentScore = 0;
        currentHighScore = highScore;
        currentSong = 0;

        // initializes states
        isPractice = true;
        isEndOfGame = false;
        isNewRecord = false;
        finishedFirstCycle = false;


    }

    // *** Game Progression
    /**
     * Starts the appropriate songs and the timeline associated with it.
     */
    public void tossCoin() throws Exception {
        // plays the appropriate song
        drawHand(2); // draws the tossing hand image

        if (!isPractice) {
            if (currentSong == 12) { // stops going in order and switches to randomizer
                // also can include/reference song randomizer, but have to keep track of whether the first 12 have been played before randomizing
                currentSong = 0;
                finishedFirstCycle = true;
                finishedRandSongs = false;
                mainSfx.randomizer();

            }

            // randomizes the songs after the first cycle
            if (finishedFirstCycle) {
                if (!finishedRandSongs) { // plays the first random song cued
                    mainSfx.playRandMainSong1();
                    finishedRandSongs = true;
                }
                else { // plays the second random song, and then refreshes randomizer
                    mainSfx.playRandMainSong2();
                    finishedRandSongs = false;
                    mainSfx.randomizer();
                }
            }
            else {
                mainSfx.playMainSong(currentSong); // play the current song
            }

            startTimeline();
            currentSong++; // moves to the next song in the list

        } else { // practice, no score is kept track, and only on hit will it progress to the main game.
            mainSfx.playPracSong();
            startTimeline();
        }

    }

    /**
     * A timeline that detects whether the player is accurate & precise on catch.
     * <p>The detection/calculation is done at the end of a given range.</p>
     */
    public void startTimeline() {
        beatTimeline = new Timeline(); // create a timeline

        // according to the RH Remix Editor, songs have a minor delay before actually playing (like <70ms), this will compensate for them
        if (isPractice) {
            beatTimeline.setDelay(Duration.millis(2500 - SongSFX.getPracStartDelay())); // Ace is at 3000, and ranges are compensated for this delay
        }
        else { //not practice
            beatTimeline.setDelay(Duration.millis(2500 - SongSFX.getSongStartDelay(currentSong)));
        }

        // as recommended by intellij, turned it into AtomicBoolean, so it can be checked in all instances
        AtomicBoolean wasButtonPressed = new AtomicBoolean(false);

        // Timeline Keyframes
        /*
        * The format of these Keyframes tend to follow this format:
        * Note: Barelys, Hits and Aces are all a success, Misses are fails. "Early x" means before the perfect Ace input at Beat 6, and "Late x" means after the perfect Ace input
        *
        *   - end the game/reset practice counter (if it misses); add to score/increment practice counter (if successful)
        *   - play sfx
        *   - reset buttonHit, so it won't trigger any other keyframes
        *   - set the button press to true: This detects if a player has not attempted at all or hit waaay too early
        *
        * */
        KeyFrame start = new KeyFrame(Duration.millis(mainSeq.getCueRange(0)), // initialize
                e -> {
                    isMiss = false;
                    wasButtonPressed.set(false);
                    buttonHit = false;

                });

        KeyFrame e_miss = new KeyFrame(Duration.millis(mainSeq.getCueRange(1)), // Early Miss
                e -> {
                    if (buttonHit) {
                        if (!isEndOfGame && !isPractice) {
                            isEndOfGame = true;
                        }
                        if (isPractice) {
                            pracCounter = 0;
                        }

                        mainSfx.playMissSfx();
                        wasButtonPressed.set(true);
                        buttonHit = false;
                        isMiss = true;
                        // System.out.println("Early Miss...");
                    }
                });

        KeyFrame e_barely = new KeyFrame(Duration.millis(mainSeq.getCueRange(2)), // Early Barely
                e -> {
                    if (buttonHit) {
                        if (!isEndOfGame && !isPractice) {
                            updateScores(1);
                        }
                        else {
                            pracCounter++;
                        }
                        mainSfx.playHitSfx();
                        wasButtonPressed.set(true);
                        buttonHit = false;

                        try {
                            drawHand(3);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        // System.out.println("Early Barely.");
                    }
                });

        KeyFrame e_hit = new KeyFrame(Duration.millis(mainSeq.getCueRange(3)), // Early Hit
                e -> {
                    if (buttonHit) {
                        if (!isEndOfGame && !isPractice) {
                            updateScores(2);
                        }
                        else {
                            pracCounter++;
                        }
                        wasButtonPressed.set(true);
                        buttonHit = false;
                        mainSfx.playHitSfx();

                        try {
                            drawHand(3);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        // System.out.println("Early Hit!");
                    }
                });

        KeyFrame ace = new KeyFrame(Duration.millis(mainSeq.getCueRange(4)), // Ace/Perfect
                e -> {
                    if(buttonHit) {
                        if (!isEndOfGame && !isPractice) {
                            updateScores(3);
                        }
                        else {
                            pracCounter++;
                        }
                        wasButtonPressed.set(true);
                        buttonHit = false;
                        mainSfx.playHitSfx();

                        try {
                            drawHand(3);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        // System.out.println("Ace!");
                    }
                });

        KeyFrame l_hit = new KeyFrame(Duration.millis(mainSeq.getCueRange(5)), // Late Hit
                e -> {
                    if(buttonHit) {
                        if (!isEndOfGame && !isPractice) {
                            updateScores(2);
                        }
                        else {
                            pracCounter++;
                        }
                        wasButtonPressed.set(true);
                        buttonHit = false;
                        mainSfx.playHitSfx();

                        try {
                            drawHand(3);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        // System.out.println("Late Hit!");
                    }

                });
        KeyFrame l_barely = new KeyFrame(Duration.millis(mainSeq.getCueRange(6)), // Late Barely
                e -> {
                    if(buttonHit) {
                        if (!isEndOfGame && !isPractice) {
                            updateScores(1);
                        }
                        else {
                            pracCounter++;
                        }
                        wasButtonPressed.set(true);
                        buttonHit = false;
                        mainSfx.playHitSfx();

                        try {
                            drawHand(3);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        // System.out.println("Late Barely.");
                    }

                });

        KeyFrame l_miss = new KeyFrame(Duration.millis(mainSeq.getCueRange(7)), // Late Miss
                e -> {
                    if (buttonHit) {
                        if (!isEndOfGame && !isPractice) {
                            isEndOfGame = true;
                        }
                        if (isPractice) {
                            pracCounter = 0;
                        }
                        wasButtonPressed.set(true);
                        buttonHit = false;
                        isMiss = true;
                        mainSfx.playMissSfx();
                        // System.out.println("Late Miss...");
                    }
                    if (!wasButtonPressed.get()) {
                        mainSfx.playMissSfx();
                        isMiss = true;
                    }
                });

        // add the Keyframes
        beatTimeline.getKeyFrames().addAll(start, e_miss, e_barely, e_hit, ace, l_hit, l_barely, l_miss);

        // Do some checks at the end of the timeline
        beatTimeline.setOnFinished(event -> {
            if(isMiss || !wasButtonPressed.get()) { // mainly for checking if the button was never pressed at all, also checking if the button was pressed way before it starts
                if (!isPractice) {
                    isEndOfGame = true;
                } else {
                    pracCounter = 0;
                    try {
                        drawHand(1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                mainSfx.playSadSfx();
            }

            if (isEndOfGame) { // prompt end
                playEnding();
            }
            else if (pracCounter == 3) { // exit practice after 3 successful hits in a row
                isPractice = false;
            }

        });

        // Play the Timeline
        beatTimeline.play();
    }

    /**
     * An action that uses this function will set off the boolean that will be checked by the timeline, on whether the timing is good or not.
     */
    public void catchCoin() {
        buttonHit = true;
    }


    // *** Scoring methods
    /**
     * Gets the current score.
     *
     * @return the current score.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Gets the current high score.
     *
     * @return the current high score.
     */
    public int getCurrentHighScore() {
        return currentHighScore;
    }

    /**
     * Gets how many successful practice hits were completed.
     *
     * @return number of successful hits
     */
    public int getPracCounter() {
        return pracCounter;
    }

    /**
     * Updates the current score as well as changing the current high score if it is reached.
     *
     * @param addedScore the score to be added (1, 2, 3)
     */
    public void updateScores(int addedScore) {
        currentScore += addedScore;

        int playerScore = getCurrentScore();
        int record = getCurrentHighScore();

        if (record < playerScore) { // new record
            currentHighScore = playerScore;
            isNewRecord = true;
        }

    }

    // Outputs and Conditions
    /**
     * Changes the end text depending on whether the player receives a high score or doesn't, and displays the result.
     */
    public void drawEndText() {
        int added_x; // an x offset since one text may be longer in width than the other
        String scoreText; // collects the results, which is then placed into the text
        end_gc.setFont(Font.font("Impact", FontWeight.BOLD, FontPosture.ITALIC, 100)); // change the property for both

        if (isNewRecord) { // new record text
            end_gc.setFill(Color.GOLD);
            end_gc.fillText("New", 125, 200);
            end_gc.fillText("Record", 200, 350);

            scoreText = "New High Score: " + currentHighScore;
            added_x = 0;

        }
        else { // game over text

            end_gc.setFill(Color.CORNFLOWERBLUE);
            end_gc.fillText("Game", 150, 200);
            end_gc.fillText("Over", 250, 350);

            scoreText = "Your Score: " + currentScore;

            added_x = 25;
        }

        // draw the score text
        end_gc.setFont(Font.font("Verdana", 25));
        end_gc.setFill(Color.WHITE);
        end_gc.fillText(scoreText, 175 + added_x, 600 - 150);

    }

    /**
     * Initializes the images.
     */
    public void initializeHand() throws Exception {
        handImage = new Image(getClass().getResource("/sprites/hand_ready.png").toURI().toString());
        handView = new ImageView(handImage);
        handView.setPreserveRatio(true); // no warping when scaled

        // position
        handView.setX(400 - handView.getLayoutBounds().getWidth());
        handView.setY(400 - handView.getLayoutBounds().getHeight());

        // shown size
        handView.setFitHeight(300);
        handView.setFitWidth(300);

        handView.setVisible(true); // initializes to true just in case
    }

    /**
     * Draws one of 3 given images.
     *
     * @param whichHand one of 3 possible hand images (1, 2, 3)
     */
    public void drawHand(int whichHand) throws Exception {
        // selects the hand
        switch (whichHand) {
            case 1: // ready position
                handView.setImage(new Image(getClass().getResource("/sprites/hand_ready.png").toURI().toString()));
                break;
            case 2: // toss position
                handView.setImage(new Image(getClass().getResource("/sprites/hand_toss.png").toURI().toString()));
                break;
            case 3: // catch position
                handView.setImage(new Image(getClass().getResource("/sprites/hand_catch.png").toURI().toString()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + whichHand);
        }

    }

    /**
     * Check if the game is in Practice mode.
     *
     * @return true if in Practice mode.
     */
    public boolean getIsPractice() {
        return isPractice;
    }

    /**
     * Get the Game's SFX handler
     *
     * @return the SongSFX class
     */
    public SongSFX getMainSfx() {
        return mainSfx;
    }
    // one for songSeq

    /**
     * Get the Game's ImageView Handler
     *
     * @return the Image Viewer
     */
    public ImageView getHandView () {
        return handView;
    }

    /**
     * Swaps over to the ending scene and plays an accompanying sfx after setting all the results.
     */
    public void playEnding() {
        handView.setVisible(false); // @fixme This makes the imageview disappear, or else it stacks over multiple restarts. A bit of a hack, but alas.

        drawEndText();
        stage.setScene(endScene);

        if (isNewRecord) {
            mainSfx.playHighScore();
        }
        else {
            mainSfx.playGameOver();
        }

    }

}

