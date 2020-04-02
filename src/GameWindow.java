import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.min;

public class GameWindow extends Application {

    private static final String FRAME_TITLE = "Chess Game";

    public static final double CANVAS_WIDTH = (double) Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final double CANVAS_HEIGHT = (double) Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final double TABLE_X = CANVAS_WIDTH * 0.28;
    public static final double TABLE_Y = CANVAS_HEIGHT * 0.05;
    public static final int TABLE_MODE = 1;
    public static final double TABLE_SIZE = min(CANVAS_HEIGHT,CANVAS_WIDTH) * 0.8;
    public static  final double PIECE_SIZE = TABLE_SIZE/8;
    public static final double MOVE_UNIT = PIECE_SIZE;
    public static final boolean IS_COORD_TABLE = true;
    private static GraphicsContext gc;
    private static Canvas canvas;
    private static Group root;
    private static Stage primaryStage;
    private static Image gameOverMenu;
    private Player player = new Player();

    //TODO: Game over window

    @Override
    public void start(Stage primaryStage) throws IOException {
        GameWindow.primaryStage = primaryStage;
        root = new Group();
        canvas = new Canvas(CANVAS_WIDTH,
                CANVAS_HEIGHT);
        primaryStage.setTitle(FRAME_TITLE);
        //primaryStage.setFullScreen(true);
        /*Timeline timeline = new Timeline(new KeyFrame(Duration.millis(5), ae -> {
            try {
                onTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();*/
        canvas.setOnMouseClicked(evt -> {
            if(!Game.isGameOver()) {
                if (!Player.isPieceSelected()) {
                    ArrayList<Coord> pieceCoords = Table.requestPieceCoord(Game.isWhitesTurn());
                    int pieceX = -1;
                    int pieceY = -1;
                    for (Coord coord : pieceCoords) {
                        double realX = getRealX(coord.getX());
                        double realY = getRealY(coord.getY());
                        double actionX = evt.getX();
                        double actionY = evt.getY();
                        /*if(TABLE_MODE == 2 || (!Game.isWhitesTurn() && TABLE_MODE == 0)){
                            actionY = CANVAS_HEIGHT - actionY;
                        }*/
                        if (actionX >= realX && actionX <= realX + MOVE_UNIT &&
                                actionY >= realY && actionY <= realY + MOVE_UNIT) {
                            pieceX = coord.getX();
                            pieceY = coord.getY();
                        }
                    }
                    Player.selection(pieceX, pieceY, (pieceX == -1));
                } else {
                    ArrayList<Coord> pathCoords = Game.getPathsCoord();
                    int pathX = -1;
                    int pathY = -1;
                    for (Coord coord : pathCoords) {
                        double realX = getRealX(coord.getX());
                        double realY = getRealY2(coord.getY());
                        double actionX = evt.getX();
                        double actionY = evt.getY();
                        if(TABLE_MODE == 2 || (!Game.isWhitesTurn() && TABLE_MODE == 0)){
                            actionY = CANVAS_HEIGHT - actionY;
                        }
                        if (actionX >= realX && actionX <= realX + MOVE_UNIT &&
                                actionY >= realY && actionY <= realY + MOVE_UNIT) {
                            pathX = coord.getX();
                            pathY = coord.getY();
                        }
                    }
                    try {
                        Player.action(pathX, pathY, (pathX == -1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        gc = canvas.getGraphicsContext2D();
        startDrawing();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        Thread cliThread = new Thread(player);
        cliThread.setDaemon(true);
        cliThread.start();
    }

    public static void endMenu() throws IOException {
        startDrawing();
        File file = new File("gameOverMenu.png");
        BufferedImage tableImage = ImageIO.read(file);
        gameOverMenu = SwingFXUtils.toFXImage(tableImage, null);
        gc.drawImage(gameOverMenu,TABLE_X - MOVE_UNIT,TABLE_Y + MOVE_UNIT,TABLE_SIZE * 5/4, TABLE_SIZE * 3/4);
    }

    public static double getRealX(int x){
        if(TABLE_MODE == 2 || (!Game.isWhitesTurn() && TABLE_MODE == 0)){
            x = Table.SIZE - x;
        }
        return TABLE_X + MOVE_UNIT*(x-1);
    }

    public static double getRealY(int y){
        if(TABLE_MODE == 2 || (!Game.isWhitesTurn() && TABLE_MODE == 0)){
            y = Table.SIZE - y;
        }
        return TABLE_Y + MOVE_UNIT*(Table.SIZE - y - 1);
    }

    public static double getRealY2(int y){
        if((Game.isWhitesTurn() && TABLE_MODE == 0) || TABLE_MODE == 1){
            y++;
        }
        return TABLE_Y + MOVE_UNIT*(Table.SIZE - y);
    }

    public static void onTime() throws IOException {
        startDrawing();
    }

    public static void startDrawing() throws IOException {
        gc.clearRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        Table.draw(gc,TABLE_X ,TABLE_Y ,TABLE_SIZE);
        if(TABLE_MODE == 2 || (!Game.isWhitesTurn() && TABLE_MODE == 0)){
            Game.reverseTable();
        }
        Game.drawSetup(gc);
        if(TABLE_MODE == 2 || (!Game.isWhitesTurn() && TABLE_MODE == 0)){
            Game.reverseTable();
        }
    }

    public static void putPiece(Image image, GraphicsContext gc, double x, double y){
        gc.drawImage(image,TABLE_X + MOVE_UNIT*(x-1),TABLE_Y + MOVE_UNIT*(8-y),PIECE_SIZE,PIECE_SIZE);
    }

    public static void putCapture(Image image, GraphicsContext gc, double x, double y, double size){
        gc.drawImage(image,TABLE_X*0.95 + MOVE_UNIT*(x-1),TABLE_Y*0.5 + MOVE_UNIT*(8-y),size,size);
    }

    public static void putBRook(Image image, GraphicsContext gc, double x, double y){
        double size = GameWindow.PIECE_SIZE * 0.85;
        gc.drawImage(image,TABLE_X*1.01 + MOVE_UNIT*(x-1),TABLE_Y*1.15 + MOVE_UNIT*(8-y),size,size);
    }

    public static void main(String[] args){
        launch();
    }
}
