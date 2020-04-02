import java.io.IOException;
import java.util.Scanner;
public class Player implements Runnable {

    private static boolean isPieceSelected = false;

    private static int pX=0;
    private static int pY=0;
    private static int x;
    private static int y;
    private static String s;
    private static char c;
    private static boolean wasCanceled;
    private static Scanner in;
    //The main driver function which will be used by the AI as well as humans
    public void run() {
        TableSetups.set(TableArr.BASIC);
        try {
            Game.start();
            TableSetups.load();
        }catch(IOException e){
            e.printStackTrace();
        }
        in = new Scanner(System.in);
        System.out.println("Set moving coordinates for the piece otherwise write -1:");

        pX=0;
        pY=0;
        c=' ';
        do {
            selection();
            if(action()){
                continue;
            }else{
                break;
            }
        }while(true);
    }

    public static boolean isPieceSelected() {
        return isPieceSelected;
    }

    public static void selection(){
        wasCanceled = false;
        if (!Game.isCurrentMoveIllegal()) {
            do {
                s = in.nextLine();
                while (!s.equals("x") && (s.length() != 2 || s.charAt(1) - '0' > 8 || s.charAt(0) < 'a' || s.charAt(0) > 'h')) {
                    System.out.println("Wrong format! Type again.");
                    s = in.nextLine();
                }
                c = s.charAt(0);
                if (c == 'x') {
                    Game.setIsCurrentMoveIllegal(false);
                    Game.clearPaths();
                    try {
                        Game.printTable();
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                    wasCanceled = true;
                    continue;
                }
                pX = c - 'a' + 1;
                if (pX == -1) break;
                pY = s.charAt(1) - '0';
                try {
                    Game.pathDraw(pX, pY);
                }catch(IOException e) {
                    e.printStackTrace();
                }
            } while (Game.isCurrentMoveIllegal());
        }
        isPieceSelected = true;
    }

    public static void selection(int x, int y, boolean isWrong){
        wasCanceled = false;
        pX = x;
        pY = y;
        if (!Game.isCurrentMoveIllegal()) {
            do {
                if (isWrong) {
                    Game.setIsCurrentMoveIllegal(false);
                    Game.clearPaths();
                    try {
                        Game.printTable();
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                    wasCanceled = true;
                    continue;
                }
                try {
                    Game.pathDraw(x, y);
                }catch(IOException e) {
                    e.printStackTrace();
                }
            } while (Game.isCurrentMoveIllegal());
        }
        isPieceSelected = true;
    }

    /**
     * @return true if the program should continue and false if the program should be terminated immediately
     */
    public static boolean action(){
        if (!wasCanceled) {
            System.out.println(c + "" + pY + " piece selected. If you want to cancel the selection type x.");
            s = in.nextLine();
            while (!s.equals("x") && (s.length() != 2 || s.charAt(1) - '0' > 8 || s.charAt(0) < 'a' || s.charAt(0) > 'h')) {
                System.out.println("Wrong format! Type again.");
                s = in.nextLine();
            }
            c = s.charAt(0);
            if (c == 'x') {
                Game.setIsCurrentMoveIllegal(false);
                Game.clearPaths();
                try {
                    Game.printTable();
                }catch(IOException e) {
                    e.printStackTrace();
                }
                isPieceSelected = false;
                return true;
            }
            x = c - 'a' + 1;
            y = s.charAt(1) - '0';
            try {
                Table.getPiece(pX, pY).moveNTableTurn(x, y);
            }catch(IOException e) {
                e.printStackTrace();
            }
            if (Game.isGameOver()) {
                s = in.nextLine();
                while (!s.equals("n") && !s.equals("x")) {
                    Game.endMenu();
                    s = in.nextLine();
                }
                if (s.equals("x")) {
                    isPieceSelected = false;
                    return false;
                } else {
                    try {
                        Game.start();
                        TableSetups.load();
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Set moving coordinates for the piece otherwise write -1:");
                }
            }
        }
        isPieceSelected = false;
        return true;
    }

    /**
     * @return true if the program should continue and false if the program should be terminated immediately
     */
    public static boolean action(int x, int y, boolean isWrong) throws IOException {
        if (!wasCanceled) {
            if (isWrong) {
                Game.setIsCurrentMoveIllegal(false);
                Game.clearPaths();
                try {
                    Game.printTable();
                }catch(IOException e) {
                    e.printStackTrace();
                }
                isPieceSelected = false;
                return true;
            }
            try {
                Table.getPiece(pX, pY).moveNTableTurn(x, y);
            }catch(IOException e) {
                e.printStackTrace();
            }
            if (Game.isGameOver()) {
                GameWindow.endMenu();
                /*try {
                    Game.start();
                    TableSetups.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
        isPieceSelected = false;
        return true;
    }
}