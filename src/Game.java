import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import sun.plugin2.message.GetAppletMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Game {
    static Scanner in = new Scanner(System.in);

    private static int plyCount = 0; //Half of move count
    private static int[] direction = {-1, 0, 1};
    private static int fiftyMoveRuleCounter = 0;

    private static HashMap<Type, Character> typeToChar = new HashMap<>();

    //This boolean is for double check to block all the pieces except the king
    private static boolean boardBlock = false;

    //We block all the pieces that can't aid the king with this boolean
    private static boolean oneCheckBlock = false;

    //Turn system main variable which goes hand in hand with isWhite personal variable
    private static boolean isWhitesTurn = true;
    private static boolean shouldStartWithWhite = true;
    private static boolean isGameOver = false;
    private static boolean isCurrentMoveIllegal = false;
    private static boolean isCapture = false;
    private static boolean isPawnMoved = false;

    private static ArrayList<Piece> sweetSpots = new ArrayList<>();
    private static ArrayList<Piece> paths = new ArrayList<>();
    private static ArrayList<Coord> pathsCoord = new ArrayList<>();
    private static Piece checkingPiece;
    private static Piece selectedPiece;

    private static Image pawnW;
    private static Image pawnB;
    private static Image queenW;
    private static Image queenB;
    private static Image rookW;
    private static Image rookB;
    private static Image knightW;
    private static Image knightB;
    private static Image bishopW;
    private static Image bishopB;
    private static Image kingW;
    private static Image kingB;
    private static Image pathOption;
    private static Image captureOption;

    private static ArrayList<String> gameHistory = new ArrayList<>();

    private static Pawn enPassantPawnB;

    private static int enPassantPawnBX;
    private static int enPassantPawnBY;

    private static Pawn enPassantPawnW;

    private static int enPassantPawnWX;
    private static int enPassantPawnWY;

    private static King wKing;
    private static King bKing;

    /*TODO: 1.Pieces capturing process + en-passant DONE
     *       2.Checkmate + CASTLING!!! + PINNING!!! DONE
     *       3.Draw  DONE
     *       4.Turn system DONE
     *       5.Notation https://en.wikipedia.org/wiki/Portable_Game_Notation
     *       6.Backtrack system https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
     *       7.The option to load everything in FEN (e.g save games, play famous variations, make practice exercises)
     *       8.Image of the table DONE
     *       9.Menu DONE
     *       10.AI (min/max algorithm + research)*/

    //Starts the game

    //TODO: Undo and recording system
    /**
     * Method which starts the game.
     * Should be called at the beginning of every game.
     */
    public static void start() throws IOException {
        Table.reset();
        initialisePieceImages();
        isWhitesTurn = shouldStartWithWhite;
        plyCount = 0;
        isGameOver = false;
        enPassantPawnW = null;
        enPassantPawnB = null;
        gameHistory = new ArrayList<>();
        isCurrentMoveIllegal = false;
        boardBlock = false;
        oneCheckBlock = false;
        isCapture = false;
        isPawnMoved = false;
        fiftyMoveRuleCounter = 0;
    }

    /**
     * This function should be called at the end of a half move.
     * It will analyse the valid moves in the beginning of the opponents move.
     */
    public static void nextPly() {
        plyCount++;

        isWhitesTurn = !isWhitesTurn;

        if (isWhitesTurn) {
            //White's turn

            //On a new turn for white all the enpassant opportunities for blacks are closed
            enPassantPawnW = null;
        } else {
            //Black's turn

            //On a new turn for white all the enpassant opportunities for whites are closed
            enPassantPawnB = null;
        }
        plyCheck(true);
    }

    /**
     * This function takes care of the drawing of the path from the global perspective.
     * It is necessary for the play method to call this method for getting to the Piece.drawPath() one.
     *
     * @param pX The X position in which the selected piece is at the moment of starting the move.
     * @param pY The Y position in which the selected piece is at the moment of starting the move.
     */
    public static void pathDraw(int pX, int pY) throws IOException {
        if (Table.getPiece(pX, pY).drawPath()) {
            isCurrentMoveIllegal = false;
            Game.printTable();
            Game.clearPaths();
        } else {
            illegalMoveMenu();
        }
    }

    /**
     * A function which clears all the paths for beginning a new move.
     */
    public static void clearPaths() {
        for (Piece piece : paths) {
            Table.addPiece(piece, piece.x, piece.y);
        }
        paths = new ArrayList<>();
    }

    /**
     * Checks if the king is in check (initially was called every ply(half a move) now more often for the assessments)
     *
     * @param isRealMove Is the move real (it is generated for valid path assessment otherwise (e.g. pins))
     * @return true if there is a check or mate and false otherwise
     */
    public static boolean plyCheck(boolean isRealMove) {
        King king;
        if (isWhitesTurn) {
            king = wKing;
        } else {
            king = bKing;
        }


        //Boolean value that checks if checkmate can be avoided by a move of a piece
        boolean canBeAvoided = false;

        plyReset();

        //Checks if the king can move out of the way
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                canBeAvoided |= king.isMoveLegal(king.x + direction[i], king.y + direction[j])
                        || king.isCaptureLegal(king.x + direction[i], king.y + direction[j]);
            }
        }

        //Checks if the king is in check and counts the number of attackers (noCheck)
        king.isCheck(king.x, king.y);

        int isStalemate = 0;
        //If there are 2 checks then only the king can save himself by escaping so we block the board for one turn
        if (king.noCheck >= 2 && canBeAvoided) {
            boardBlock = true;
            return true;
        } else if (king.noCheck == 1) {
            //We set the checking piece to be the piece attacking the king and deal with it in Piece
            // (see getSweetSpots)
            checkingPiece = king.checkingPiece;
            checkingPiece.getSweetSpots();

            if (!canBeAvoided) {
                //Specification in Table
                Table.requestPieces();
                //We use wPieces which was edited by requestPieces to go through all white pieces and check if they
                //can prevent somehow checkmate
                if (king.isWhite) {
                    for (Piece wPiece : Table.wPieces) {
                        for (Piece spot : sweetSpots) {
                            if (wPiece.isMoveLegal(spot.x, spot.y) || wPiece.isCaptureLegal(spot.x, spot.y)) {
                                canBeAvoided = true;
                                break;
                            }
                        }
                        if (canBeAvoided) break;
                    }
                } else {
                    for (Piece bPiece : Table.bPieces) {
                        for (Piece spot : sweetSpots) {
                            if (bPiece.isMoveLegal(spot.x, spot.y) || bPiece.isCaptureLegal(spot.x, spot.y)) {
                                canBeAvoided = true;
                                break;
                            }
                        }
                        if (canBeAvoided) break;
                    }
                }
            }
            oneCheckBlock = true;
            if (!isRealMove) {
                return true;
            }
        } else if (!canBeAvoided) {
            //Stalemate checking routine
            //Specification in Table
            Table.requestPieces();
            isStalemate = 2;
            //We use wPieces which was edited by requestPieces to go through all white pieces and check if they
            //can prevent somehow stalemate
            if (king.isWhite) {
                for (Piece wPiece : Table.wPieces) {
                    for (int x = 1; x < Table.SIZE; x++) {
                        for (int y = 1; y < Table.SIZE; y++) {
                            if (wPiece.isMoveLegal(x, y) || wPiece.isCaptureLegal(x, y)) {
                                isStalemate = 1;
                                break;
                            }
                        }
                    }
                    if (isStalemate == 1) break;
                }
            } else {
                for (Piece bPiece : Table.bPieces) {
                    for (int x = 1; x < Table.SIZE; x++) {
                        for (int y = 1; y < Table.SIZE; y++) {
                            if (bPiece.isMoveLegal(x, y) || bPiece.isCaptureLegal(x, y)) {
                                isStalemate = 1;
                                break;
                            }
                        }
                    }
                    if (isStalemate == 1) break;
                }
            }
        }
        if (isRealMove) {
            //if not we open a mate menu and the game is over
            if (king.noCheck != 0) {
                if (!canBeAvoided) {
                    mateMenu();
                    return true;
                } else if (oneCheckBlock) {
                    checkMenu();
                    return true;
                }
            }
            //Threefold repetition and 50 move rule
            String currentPos = Table.intoString();
            //TODO: add the enpassant and castling additional rule (check threefold repetition for reference)
            int rep = 0;
            for (int i = 0; i < gameHistory.size(); i++) {
                if (currentPos.equals(gameHistory.get(i))) {
                    rep++;
                }
                if (rep == 2) {
                    drawMenu("threefold repetition");
                    return true;
                }
            }
            gameHistory.add(currentPos);

            if (isCapture || isPawnMoved) {
                fiftyMoveRuleCounter = 0;
                isCapture = false;
                isPawnMoved = false;
            } else {
                fiftyMoveRuleCounter++;
                //99 because we use the function every ply (half of move)
                if (fiftyMoveRuleCounter >= 99) {
                    drawMenu("fifty-move rule");
                    return true;
                }
            }
            Table.requestPieces();
            if (Table.wPieces.size() <= 1 && Table.bPieces.size() <= 1 &&
                    (Table.wPieces.size() != 1 || Table.wPieces.get(0).type != Type.PAWN)
                    && (Table.bPieces.size() != 1 || Table.bPieces.get(0).type != Type.PAWN)) {
                if (Table.wPieces.size() == 1 && Table.bPieces.size() == 1 && Table.wPieces.get(0).type == Type.BISHOP
                        && Table.bPieces.get(0).type == Type.BISHOP) {
                    boolean iswBishopWhite = ((Table.wPieces.get(0).x % 2) != (Table.wPieces.get(0).y % 2));
                    boolean isbBishopWhite = ((Table.bPieces.get(0).x % 2) != (Table.bPieces.get(0).y % 2));
                    if (isbBishopWhite && iswBishopWhite) {
                        drawMenu("impossibility of checkmate");
                        return true;
                    }
                } else if (Table.wPieces.size() == 1 && Table.bPieces.size() == 0) {
                    if (Table.wPieces.get(0).type == Type.KNIGHT || Table.wPieces.get(0).type == Type.BISHOP) {
                        drawMenu("impossibility of checkmate");
                        return true;
                    }
                } else if (Table.bPieces.size() == 1 && Table.wPieces.size() == 0) {
                    if (Table.bPieces.get(0).type == Type.KNIGHT || Table.bPieces.get(0).type == Type.BISHOP) {
                        drawMenu("impossibility of checkmate");
                        return true;
                    }
                } else {
                    drawMenu("impossibility of checkmate");
                    return true;
                }
            }
            //The stalemate menu activation
            if (king.noCheck == 0 && isStalemate == 2) {
                stalemateMenu();
            }
        }
        return false;
    }

    /**
     * Resets the conditions for the opponent.
     */
    public static void plyReset() {
        //We unblock the board because the double check turn ended
        boardBlock = false;

        //We block all the pieces that can't aid the king with this boolean
        oneCheckBlock = false;

        //Renew the sweet spots because we should check for a new situation for the king
        sweetSpots = new ArrayList<>();

        isCurrentMoveIllegal = false;
    }

    /**
     * The function which prints the playing table in the console.
     * It will be soon replaced with a graphical interface.
     */
    public static void printTable() throws IOException {
        System.out.println("*******************************************************\n   a  b  c  " +
                "d  e  f  g  h\n  ________________________");
        for (int y = Table.SIZE - 1; y > 0; y--) {
            System.out.print(y + "|");
            for (int x = 1; x < Table.SIZE; x++) {
                System.out.print(" " + Table.getImTableElem(x,y) + " ");
            }
            System.out.println("|" + y);
        }
        System.out.println("  ________________________\n   a  b  c  d  e  f  g  h\n**************************" +
                "*****************************");
        GameWindow.startDrawing();
    }

    public static void reverseTable() {
        for (int y = 1; y < Table.SIZE; y++) {
            for (int x = 1; x <= Table.SIZE/2; x++) {
                Table.swapImTableElem(x,y,Table.SIZE - x,y);
            }
        }
    }

    /**
     * Prints the game end menu text.
     */
    public static void endMenu() {
        System.out.println("The game is over!\nPlease choose an option from the 2 above!");
    }

    /**
     * Prints the illegal move text.
     */
    public static void illegalMoveMenu() {
        System.out.println("Illegal move!");
        isCurrentMoveIllegal = true;
    }

    /**
     * Prints the game check menu text.
     */
    public static void checkMenu() {
        System.out.println("Check!");
    }

    /**
     * Prints the game mate menu text.
     */
    public static void mateMenu() {
        System.out.println("Checkmate!");
        String team;
        if (!isWhitesTurn) {
            team = "White";
        } else {
            team = "Black";
        }
        System.out.println(team + " wins!\nTo play a new game type n to close type x or close the window.");

        isGameOver = true;
    }

    /**
     * Prints the game draw menu text.
     */
    public static void drawMenu(String motive) {
        System.out.println("Draw by " + motive + "!\nTo play a new game type n to close type x or close the window.");
        isGameOver = true;
    }

    /**
     * Prints the draw choice menu which will be used if a player wants to draw
     *
     * @param in The scanner from which the choice to draw or not will be taken from the opponent.
     */
    public static void drawChoiceMenu(Scanner in) {
        String s;
        System.out.println("You were offered a draw!\nWrite 'yes' to accept or 'no' to reject.");
        s = in.nextLine();
        while (!s.equals("yes") && !s.equals("no")) {
            System.out.println("Wrong format!\nType your choice again.\n(Remember it should be 'yes' or 'no')");
            s = in.nextLine();
        }
    }

    /**
     * Prints the game stalemate menu text.
     */
    public static void stalemateMenu() {
        System.out.println("Stalemate!\nTo play a new game type n to close type x or close the window.");
        isGameOver = true;
    }

    //Menu for promoting the pawn

    /**
     * Prints the game promotion menu text and changes the type of piece at (x,y)
     *
     * @param x The x position of the pawn.
     * @param y The y position of the pawn.
     */
    public static void promotionMenu(int x, int y) {
        System.out.println("To what piece you want to promote your pawn?");
        System.out.println("1.Queen (type q)\n2.Rook (type r)\n3.Knight (type n)\n4.Bishop (type b)");
        Type type = Type.NULL;

        //While cycle below does error handling
        while (type == Type.NULL) {
            char c = in.next().charAt(0);
            switch (c) {
                case 'q':
                    type = Type.QUEEN;
                    break;
                case 'r':
                    type = Type.ROOK;
                    break;
                case 'n':
                    type = Type.KNIGHT;
                    break;
                case 'b':
                    type = Type.BISHOP;
                    break;
                default:
                    System.out.println("Wrong character or character format! " +
                            "\nPlease read the instructions carefully and type again.");
                    break;
            }
        }

        Table.addPiece(Table.getPiece(x, y), x, y);
    }

    public static ArrayList<Piece> getSweetSpots() {
        return sweetSpots;
    }

    public static void addToSweetSpots(Piece piece) {
        sweetSpots.add(piece);
    }

    public static boolean isWhitesTurn() {
        return isWhitesTurn;
    }

    public static boolean isBoardBlock() {
        return boardBlock;
    }

    public static void setPaths(ArrayList<Piece> paths) {
        Game.paths = paths;
    }

    public static ArrayList<Coord> getPathsCoord() {
        return pathsCoord;
    }

    public static void setPathsCoord(ArrayList<Coord> pathsCoord) {
        Game.pathsCoord = pathsCoord;
    }

    public static King getbKing() {
        return bKing;
    }

    public static void setbKing(King bKing) {
        Game.bKing = bKing;
    }

    public static King getwKing() {
        return wKing;
    }

    public static void setwKing(King wKing) {
        Game.wKing = wKing;
    }

    public static void setSelectedPiece(Piece selectedPiece) {
        Game.selectedPiece = selectedPiece;
    }

    public static boolean isGameOver() {
        return isGameOver;
    }

    public static boolean isOneCheckBlock() {
        return oneCheckBlock;
    }

    public static Piece getCheckingPiece() {
        return checkingPiece;
    }

    public static void setIsPawnMoved(boolean isPawnMoved) {
        Game.isPawnMoved = isPawnMoved;
    }

    public static void setIsCapture(boolean isCapture) {
        Game.isCapture = isCapture;
    }

    public static boolean isCurrentMoveIllegal() {
        return isCurrentMoveIllegal;
    }

    public static void setIsCurrentMoveIllegal(boolean isCurrentMoveIllegal) {
        Game.isCurrentMoveIllegal = isCurrentMoveIllegal;
    }

    public static void setEnPassantPawnB(Pawn enPassantPawnB, int x, int y) {
        Game.enPassantPawnB = enPassantPawnB;
        setEnPassantPawnBX(x);
        setEnPassantPawnBY(y);
    }

    public static Pawn getEnPassantPawnB() {
        return enPassantPawnB;
    }

    public static void setEnPassantPawnW(Pawn enPassantPawnW, int x, int y) {
        Game.enPassantPawnW = enPassantPawnW;
        setEnPassantPawnWX(x);
        setEnPassantPawnWY(y);
    }

    public static Pawn getEnPassantPawnW() {
        return enPassantPawnW;
    }

    public static int getEnPassantPawnBX() {
        return enPassantPawnBX;
    }

    public static int getEnPassantPawnBY() {
        return enPassantPawnBY;
    }

    public static void setEnPassantPawnBX(int enPassantPawnBX) {
        Game.enPassantPawnBX = enPassantPawnBX;
    }

    public static void setEnPassantPawnBY(int enPassantPawnBY) {
        Game.enPassantPawnBY = enPassantPawnBY;
    }

    public static int getEnPassantPawnWX() {
        return enPassantPawnWX;
    }

    public static int getEnPassantPawnWY() {
        return enPassantPawnWY;
    }

    public static void setEnPassantPawnWX(int enPassantPawnWX) {
        Game.enPassantPawnWX = enPassantPawnWX;
    }

    public static void setEnPassantPawnWY(int enPassantPawnWY) {
        Game.enPassantPawnWY = enPassantPawnWY;
    }

    public static void initialisePieceImages() throws IOException {
        File file = new File("pawnw.png");
        BufferedImage tableImage = ImageIO.read(file);
        pawnW = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("pawn.png");
        tableImage = ImageIO.read(file);
        pawnB = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("queenw.png");
        tableImage = ImageIO.read(file);
        queenW = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("queen.png");
        tableImage = ImageIO.read(file);
        queenB = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("rookw.png");
        tableImage = ImageIO.read(file);
        rookW = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("rook.png");
        tableImage = ImageIO.read(file);
        rookB = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("knightw.png");
        tableImage = ImageIO.read(file);
        knightW = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("knight.png");
        tableImage = ImageIO.read(file);
        knightB = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("bishopw.png");
        tableImage = ImageIO.read(file);
        bishopW = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("bishop.png");
        tableImage = ImageIO.read(file);
        bishopB = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("kingw.png");
        tableImage = ImageIO.read(file);
        kingW = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("king.png");
        tableImage = ImageIO.read(file);
        kingB = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("selectpath.png");
        tableImage = ImageIO.read(file);
        pathOption = SwingFXUtils.toFXImage(tableImage, null);
        file = new File("select1.png");
        tableImage = ImageIO.read(file);
        captureOption = SwingFXUtils.toFXImage(tableImage, null);
    }

    public static void drawSetup(GraphicsContext gc) {
        for (int y = Table.SIZE - 1; y > 0; y--) {
            for (int x = 1; x < Table.SIZE; x++) {
                int xC = x;
                int yC = y;
                if(!Game.isWhitesTurn() && GameWindow.TABLE_MODE == 0){
                    xC = Table.SIZE - x;
                    yC = Table.SIZE - y;
                }
                putPiece(gc, Table.getImTableElem(xC,yC), xC, yC);
                if (Table.getImTableElem(xC,yC) == 'x') {
                    Piece piece = Table.getPiece(x, y);
                    if(GameWindow.TABLE_MODE == 2){
                        piece = Table.getPiece(Table.SIZE - x, Table.SIZE-y);
                    }
                    switch (piece.type) {
                        case PAWN:
                            if (piece.isWhite) {
                                GameWindow.putPiece(pawnW, gc, xC, yC);
                            } else {
                                GameWindow.putPiece(pawnB, gc, x, y);
                            }
                            break;
                        case KNIGHT:
                            if (piece.isWhite) {
                                GameWindow.putPiece(knightW, gc, xC, yC);
                            } else {
                                GameWindow.putPiece(knightB, gc, x, y);
                            }
                            break;
                        case BISHOP:
                            if (piece.isWhite) {
                                GameWindow.putPiece(bishopW, gc, xC, yC);
                            } else {
                                GameWindow.putPiece(bishopB, gc, x, y);
                            }
                            break;
                        case ROOK:
                            if (piece.isWhite) {
                                GameWindow.putPiece(rookW, gc, xC, yC);
                            } else {
                                GameWindow.putBRook(rookB, gc, x, y);
                            }
                            break;
                        case QUEEN:
                            if (piece.isWhite) {
                                GameWindow.putPiece(queenW, gc, xC, yC);
                            } else {
                                GameWindow.putPiece(queenB, gc, x, y);
                            }
                            break;
                    }
                }
            }
        }

    }

    public static void putPiece(GraphicsContext gc, char c, int x, int y) {
        switch (c) {
            case 'p':
                GameWindow.putPiece(pawnB, gc, x, y);
                break;
            case 'P':
                GameWindow.putPiece(pawnW, gc, x, y);
                break;
            case 'n':
                GameWindow.putPiece(knightB, gc, x, y);
                break;
            case 'N':
                GameWindow.putPiece(knightW, gc, x, y);
                break;
            case 'b':
                GameWindow.putPiece(bishopB, gc, x, y);
                break;
            case 'B':
                GameWindow.putPiece(bishopW, gc, x, y);
                break;
            case 'r':
                GameWindow.putBRook(rookB, gc, x, y);
                break;
            case 'R':
                GameWindow.putPiece(rookW, gc, x, y);
                break;
            case 'q':
                GameWindow.putPiece(queenB, gc, x, y);
                break;
            case 'Q':
                GameWindow.putPiece(queenW, gc, x, y);
                break;
            case 'k':
                GameWindow.putPiece(kingB, gc, x, y);
                break;
            case 'K':
                GameWindow.putPiece(kingW, gc, x, y);
                break;
            case '*':
                GameWindow.putPiece(pathOption, gc, x, y);
                break;
            case 'x':
                GameWindow.putCapture(captureOption, gc, x, y, GameWindow.PIECE_SIZE * 1.5);
                break;
        }
    }

}