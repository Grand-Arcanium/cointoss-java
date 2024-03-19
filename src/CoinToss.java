import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * <p>This program is a rhythm game that demonstrates JavaFX event handlers and animations alongside various Java classes, arrays, functions, etc.</p>
 * Date: Mar 10, 2023
 *
 * @author Grand-Arcanium
 * @version 1.6
 */

public class CoinToss extends Application {

    // *** Instance Variables for View Components and Model

    // General Constants
    private final double WINDOW_W = 600;
    private final double WINDOW_H = 800;
    private final double CANVAS_S = 600;

    // Roots
    Pane introRoot = new Pane();
    // Pane pracRoot = new Pane();
    Pane gameRoot = new Pane();
    Pane endRoot = new Pane();

    // Scenes
    Scene introScene = new Scene(introRoot, WINDOW_W, WINDOW_H);
    // Scene pracScene = new Scene(pracRoot, WINDOW_W, WINDOW_H);
    Scene gameScene = new Scene(gameRoot, WINDOW_W, WINDOW_H);
    Scene endScene = new Scene(endRoot, WINDOW_W, WINDOW_H);

    // Model
    private Game mainGame;

    // Canvases and their equivalent Graphics Context
    private Canvas c_intro;
    // private Canvas c_practice;
    private Canvas c_game;
    private Canvas c_end;
    private GraphicsContext gc_intro;
    // private GraphicsContext gc_practice;
    private GraphicsContext gc_game;
    private GraphicsContext gc_end;

    // Buttons
    private Button btn_introStart, btn_introExit, btn_gameExit, btn_restart; // and btn_skip for the practice
    private Button btn_toss, btn_catch;

    // Label
    private Label lb_instructions1, lb_instructions2; // small popup that tells the user how to play

    // Text
    private Text txt_practice; // popup that tells the user is in practice, replaced by the scores in the real game
    private Text txt_currentScore, txt_highScore; // displays game scores
    private Text txt_title, txt_credits; // displays text in the main menu


    private int highScore = 0; // the high score of the current session (NOT the individual game)

    /**
     * Draws the logo.
     *
     * @param gc the Graphics Context used to draw.
     */
    public void drawLogo(GraphicsContext gc) {
        // Coin Logo, drawn on Canvas
        gc.setLineWidth(5);
        gc.setFill(Color.DARKGOLDENROD);
        gc.fillOval(100, 100, 300, 300); // dark outline
        gc.setFill(Color.GOLDENROD);
        gc.fillOval(110, 110, 280, 280); // body

        gc.setFill(Color.DARKGOLDENROD);
        gc.strokeOval(100, 100, 300, 300); // border
        gc.fillOval(175, 200, 30, 30); // left eye
        gc.fillOval(300, 200, 30, 30); // right eye
        gc.fillArc(162.5, 175, 175, 175, 180, 180, ArcType.CHORD); // smile
    }

    /**
     * Draws the background.
     *
     * @param gc the Graphics Context used to draw.
     */
    public void drawBackground(GraphicsContext gc) {
        // Background Properties
        gc.setFill(Color.GOLD);
        gc.fillRect(0,0, CANVAS_S, CANVAS_S);

    }

    /**
     * Draws the background and the buttons of the end scene.
     */
    public void drawEndScene() {
        gc_end.setFill(Color.BLACK);
        gc_end.fillRect(0,0, CANVAS_S, CANVAS_S);

        // Buttons
        btn_restart.setMaxSize(100, 50);
        btn_restart.setPrefSize(100, 50);
        btn_restart.setStyle("-fx-font-size:20");


        btn_gameExit.setMaxSize(100, 50);
        btn_gameExit.setPrefSize(100, 50);
        btn_gameExit.setStyle("-fx-font-size:20");

    }

    /**
     * Contains the Main stage, components, scenes, and adding them to the root to be displayed.
     *
     * @param stage The main stage
     */
    @Override
    public void start(Stage stage) throws Exception {
        // *** Initial
        stage.setTitle("Coin Toss Lite");
        stage.setScene(introScene);

        c_intro = new Canvas(CANVAS_S, CANVAS_S);
        c_game = new Canvas(CANVAS_S, CANVAS_S);
        c_end = new Canvas(CANVAS_S, CANVAS_S);

        gc_intro = c_intro.getGraphicsContext2D();
        gc_game = c_game.getGraphicsContext2D();
        gc_end = c_end.getGraphicsContext2D();


        // *** The Model
        try {
            mainGame = new Game(stage, endScene, gc_end, gameRoot, highScore);
        } catch (Exception ex) {
            System.out.println("Game could not be loaded. Check the Game Class.");
            throw new RuntimeException(ex);
        }

        // *** Creating the GUI components
        // Intro Music
        Media media = new Media(getClass().getResource("/audio/opening.mp3").toURI().toString());
        MediaPlayer introPlayer = new MediaPlayer(media);
        introPlayer.setVolume(0.2);
        MediaView mediaView = new MediaView(introPlayer);

        // Buttons
        btn_introStart = new Button("Start");
        btn_introExit = new Button("Exit");
        // btn_skip = new Button("Skip Practice");
        btn_restart = new Button("Restart");
        btn_gameExit = new Button("Exit");

        btn_toss = new Button("Toss!");
        btn_catch = new Button("Catch!");

        // Text
        txt_title = new Text("Coin Toss");
        txt_credits = new Text("A Rhythm Game by: Grand-Arcanium, 2023, COMP10062");
        txt_practice = new Text("PRACTICE: 0/3");
        txt_currentScore = new Text("");
        txt_highScore = new Text("");

        // Label
        lb_instructions1 = new Label("Toss the coin to start a song!");
        lb_instructions2 = new Label("Catch it on the 7th beat!");

        // *** Adding Components to Root
        // Intro Scene
        introRoot.getChildren().add(c_intro);
        introRoot.getChildren().addAll(btn_introStart, btn_introExit,
                                       txt_title, txt_credits);
        introRoot.getChildren().add(mediaView);

        // Practice Scene
        // pracRoot.getChildren().addAll(btn_toss, btn_catch, btn_skip, lb_instructions1, lb_instructions2);

        // Game Scene
        gameRoot.getChildren().add(c_game);
        gameRoot.getChildren().addAll(btn_toss, btn_catch, lb_instructions1, lb_instructions2, txt_practice, txt_highScore, txt_currentScore);

        // End Scene
        endRoot.getChildren().add(c_end);
        endRoot.getChildren().addAll(btn_gameExit, btn_restart);

        // *** Configure Components
        drawBackground(gc_intro);
        drawBackground(gc_game);

        // INTRO SCENE
        drawLogo(gc_intro);

        // Button Properties
        btn_introStart.relocate( 100 - (btn_introStart.getLayoutBounds().getWidth() / 2), 650);
        btn_introStart.setMaxSize(100, 50);
        btn_introStart.setPrefSize(100, 50);
        btn_introStart.setStyle("-fx-font-size:20");

        btn_introExit.relocate(400 - (btn_introExit.getLayoutBounds().getWidth() / 2), 650);
        btn_introExit.setMaxSize(100, 50);
        btn_introExit.setPrefSize(100, 50);
        btn_introExit.setStyle("-fx-font-size:20");

        // Title Properties
        gc_intro.setLineWidth(5);
        txt_title.setFont(Font.font("Impact", 75));
        txt_title.setFill(Color.GOLDENROD);
        txt_title.setStroke(Color.BLACK);
        txt_title.relocate(250, 450);

        // Credits Properties
        txt_credits.setFont(Font.font("Verdana", 10));
        txt_credits.setFill(Color.BLACK);
        txt_credits.relocate((WINDOW_W / 2) - (txt_credits.getLayoutBounds().getWidth() / 2), 750);

        // GAME SCENE
        // Buttons
        btn_toss.relocate( 100 - (btn_toss.getLayoutBounds().getWidth() / 2), 650);
        btn_toss.setMaxSize(100, 50);
        btn_toss.setPrefSize(100, 50);
        btn_toss.setStyle("-fx-font-size:20");

        btn_catch.relocate(400 - (btn_catch.getLayoutBounds().getWidth() / 2), 650);
        btn_catch.setMaxSize(100, 50);
        btn_catch.setPrefSize(100, 50);
        btn_catch.setStyle("-fx-font-size:20");

        // Labels
        lb_instructions1.setFont(Font.font("Verdana", 15));
        lb_instructions1.relocate((190) - (lb_instructions1.getLayoutBounds().getWidth() / 2), 725);
        lb_instructions2.setFont(Font.font("Verdana", 15));
        lb_instructions2.relocate((210) - (lb_instructions2.getLayoutBounds().getWidth() / 2), 750);

        txt_practice.setFont(Font.font("Verdana", 20));
        txt_practice.relocate((300) - (txt_practice.getLayoutBounds().getWidth() / 2), CANVAS_S - 30);

        txt_highScore.setFont(Font.font("Verdana", 20));
        txt_highScore.relocate(20, CANVAS_S - 30);

        txt_currentScore.setFont(Font.font("Verdana", 20));
        txt_currentScore.relocate(WINDOW_W - 200 + 50, CANVAS_S - 30);

        // Images
        mainGame.initializeHand();
        gameRoot.getChildren().add(mainGame.getHandView());

        // END SCENE
        btn_gameExit.relocate(400 - (btn_gameExit.getLayoutBounds().getWidth() / 2), 650);
        btn_restart.relocate( 100 - (btn_restart.getLayoutBounds().getWidth() / 2), 650);
        drawEndScene();


        // *** Adding Event Handlers and Final Setup
        btn_introStart.setOnMousePressed(e -> {
            stage.setScene(gameScene); // move to game scene

            // initializes button states
            btn_toss.setDisable(false);
            btn_catch.setDisable(true);
            btn_toss.requestFocus();

            introPlayer.stop(); // clear currently playing sound

        });

        btn_introExit.setOnMousePressed(e -> Platform.exit()); // exit app

        btn_gameExit.setOnMousePressed(e -> Platform.exit()); // exit app

        btn_restart.setOnMousePressed(e -> {
            highScore = mainGame.getCurrentHighScore(); // update the session's high score

            // stop song and recreate a fresh Game
            try {
                mainGame.getMainSfx().stopSong();
                mainGame = new Game(stage, endScene, gc_end, gameRoot, highScore);
                mainGame.initializeHand(); // reinitialize the hand

                gameRoot.getChildren().add(mainGame.getHandView());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // Play intro song again and set the intro scene

            introPlayer.play();
            stage.setScene(introScene);

            // resets the changed buttons on restart
            txt_practice.setText("PRACTICE: 0/3");
            txt_currentScore.setText("");
            txt_highScore.setText("");

            // resets the end scene
            endRoot.getChildren().clear();
            endRoot.getChildren().add(c_end);
            endRoot.getChildren().addAll(btn_gameExit, btn_restart);
            drawEndScene();

        });

        btn_toss.setOnMousePressed(e -> {
            // change button states
            btn_toss.setDisable(true);
            btn_catch.setDisable(false);
            btn_catch.requestFocus();

            // changes the text depending on the scores
            if (!mainGame.getIsPractice()) { // real game
                // remove the practice txt
                txt_practice.setText("");

                // add the score txt
                txt_currentScore.setText("Your Score: " + mainGame.getCurrentScore());
                txt_highScore.setText("High Score: " + mainGame.getCurrentHighScore());

                txt_currentScore.relocate(WINDOW_W - txt_currentScore.getLayoutBounds().getWidth() - 20, CANVAS_S - 30);
                txt_highScore.relocate(20, CANVAS_S - 30);

            }
            else { // practice
                //update the practice counter
                txt_practice.setText("PRACTICE: " + (mainGame.getPracCounter() + 1) + "/3");
            }

            // attempt a toss, starting the cue
            try {
                mainGame.tossCoin();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });

        btn_catch.setOnMousePressed(e -> {
            // change button states
            btn_toss.setDisable(false);
            btn_catch.setDisable(true);
            btn_toss.requestFocus();

            // attempt a catch, giving a signal to the game that will be checked
            try {
                mainGame.catchCoin();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // thing
            if (!mainGame.getIsPractice()) { // real game
                // update the score
                txt_currentScore.setText("Your Score: " + mainGame.getCurrentScore());
                txt_highScore.setText("High Score: " + mainGame.getCurrentHighScore());

                txt_currentScore.relocate(WINDOW_W - txt_currentScore.getLayoutBounds().getWidth() - 20, CANVAS_S - 30);
                txt_highScore.relocate(20, CANVAS_S - 30);
            }

        });

        /*
        // for the separate practice mode
        btn_skip.setOnAction(e -> {
            stage.setScene(gameScene);
        });
        */

        introPlayer.play(); // plays the intro song

        // *** Show Stage
        stage.show();
    }

    /**
     * Launches the application.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }
}
