import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Table {
    static final int SIZE = 9;
    public final static int INC = 32;
    private static char[][] imTable = new char[SIZE][SIZE];
    private static Piece[][] table = new Piece[SIZE][SIZE];
    public static ArrayList<Piece> wPieces = new ArrayList<>();
    public static ArrayList<Piece> bPieces = new ArrayList<>();
    public static BufferedImage tableImage;

    //Gets a piece from the table
    public static Piece getPiece(int currentX, int currentY) {
        return table[currentX][currentY];
    }

    //Sets a cell in the table equal to the piece we want to set
    private static void setPiece(int currentX, int currentY, char sign, Piece piece) {
        //imTable is for illustration purposes
        imTable[currentX][currentY] = sign;
        table[currentX][currentY] = piece;
    }

    public static char getImTableElem(int x, int y) {
        if((!Game.isWhitesTurn() && GameWindow.TABLE_MODE==0) || GameWindow.TABLE_MODE == 2){
            y = Table.SIZE  - y;
        }
        return imTable[x][y];
    }

    public static void setImTableElem(int x, int y, char c) {
        Table.imTable[x][y] = c;
    }

    public static void swapImTableElem(int x, int y, int x2,int y2) {
        char aux = Table.imTable[x][y];
        Table.imTable[x][y] = Table.imTable[x2][y2];
        Table.imTable[x2][y2] = aux;
    }

    //A method that renews the list of all the black/white pieces on board except kings
    public static void requestPieces(){
        wPieces = new ArrayList<>();
        bPieces = new ArrayList<>();
        for(int i=1;i<SIZE;i++){
            for(int j=1;j<SIZE;j++){
                if(getPiece(i,j).type != Type.NULL && getPiece(i,j).type != Type.KING){
                    if(getPiece(i,j).isWhite){
                        wPieces.add(getPiece(i,j));
                    }else{
                        bPieces.add(getPiece(i,j));
                    }
                }
            }
        }
    }

    public static ArrayList<Coord> requestPieceCoord(boolean isWhite){
        ArrayList<Coord> piecesCoord = new ArrayList<>();
        for(int i=1;i<SIZE;i++){
            for(int j=1;j<SIZE;j++){
                if(getPiece(i,j).type != Type.NULL && getPiece(i,j).isWhite == isWhite){
                        piecesCoord.add(new Coord(i,j));
                }
            }
        }
        return piecesCoord;
    }

    //Erases a piece (basically changes it into an empty cell)
    public static void erase(int currentX, int currentY){
        Table.addPiece(Type.NULL,false,false,currentX,currentY);
    }

    //Cleans the whole table (makes it fully empty)
    public static void reset(){
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                Table.addPiece(Type.NULL,false,false,i,j);
            }
        }
    }

    //All in one function for avoiding the repeating of different functions that are in Table class
    public static void addPiece(Type type, boolean isWhite, boolean notMoved, int x,int y){
        switch(type){
            case PAWN:
                Table.addPawn(isWhite,notMoved,x,y);
                break;
            case BISHOP:
                Table.addBishop(isWhite,notMoved,x,y);
                break;
            case KNIGHT:
                Table.addKnight(isWhite,notMoved,x,y);
                break;
            case ROOK:
                Table.addRook(isWhite,notMoved,x,y);
                break;
            case QUEEN:
                Table.addQueen(isWhite,notMoved,x,y);
                break;
            case KING:
                Table.addKing(isWhite,notMoved,x,y);
                break;
            case NULL:
                Table.addNull(isWhite,x,y);
        }
    }

    public static void addPiece(Piece piece, int x,int y){
        addPiece(piece.type,piece.isWhite,piece.notMoved,x,y);
    }

    public static void draw(GraphicsContext gc, double x, double y, double dim) throws IOException {
        File file = new File("table2back.png");
        tableImage = ImageIO.read(file);
        Image image = SwingFXUtils.toFXImage(tableImage, null );
        double xMulti = 1;
        double yMulti = 1;
        double sizeMulti = 1;
        if(GameWindow.IS_COORD_TABLE){
            xMulti = 0.94;
            yMulti = 0.42;
            sizeMulti = 1.07;
            gc.drawImage(image,x*xMulti,y*yMulti,dim*sizeMulti,dim*sizeMulti);
        }


        file = new File("table2.png");
        tableImage = ImageIO.read(file);
        image = SwingFXUtils.toFXImage(tableImage, null );
        gc.drawImage(image,x,y,dim,dim);
    }

    private static void addPawn(boolean isWhite, boolean notMoved, int x, int y) {
        Pawn pawn = new Pawn(isWhite,notMoved, x, y);

        //The below piece of code is for illustration purposes
        char piece = 'p';
        if (isWhite) {
            piece -= INC;
        }

       setPiece(x, y, piece, pawn);
        pawn.isPromotion();
    }

    //Adds empty cell
    private static void addNull(boolean isWhite, int x, int y){
        Piece piece = new Piece(isWhite,x,y);

        //The below piece of code is for illustration purposes
        char pieceC = '_';

        setPiece(x,y,pieceC,piece);
    }

    private static void addBishop(boolean isWhite, boolean notMoved, int x, int y) {
        Bishop bishop = new Bishop(isWhite,notMoved,x, y);

        //The below piece of code is for illustration purposes
        char piece = 'b';
        if (isWhite) {
            piece -= INC;
        }

        setPiece(x, y, piece, bishop);
    }

    private static void addKnight(boolean isWhite, boolean notMoved, int x, int y) {
        Knight knight = new Knight(isWhite,notMoved, x, y);

        //The below piece of code is for illustration purposes
        char piece = 'n';
        if (isWhite) {
            piece -= INC;
        }

        setPiece(x, y, piece, knight);
    }

    private static void addRook(boolean isWhite, boolean notMoved, int x, int y) {
        Rook rook = new Rook(isWhite,notMoved, x, y);

        //The below piece of code is for illustration purposes
        char piece = 'r';
        if (isWhite) {
            piece -= INC;
        }

        setPiece(x, y, piece, rook);
    }

    private static void addQueen(boolean isWhite, boolean notMoved, int x, int y) {
        Queen queen = new Queen(isWhite,notMoved, x, y);

        //The below piece of code is for illustration purposes
        char piece = 'q';
        if (isWhite) {
            piece -= INC;
        }

        setPiece(x, y, piece, queen);
    }

    private static void addKing(boolean isWhite, boolean notMoved, int x, int y) {
        King king = new King(isWhite, notMoved, x, y);

        //The below piece of code is for illustration purposes
        char piece = 'k';
        if (isWhite) {
            piece -= INC;
        }

        setPiece(x, y, piece, king);
    }

    public static String intoString(){
        String s=new String();
        for(int i=1;i<SIZE;i++){
            for(int j=1;j<SIZE;j++){
                s+=imTable[i][j];
            }
        }
        return s;
    }
}