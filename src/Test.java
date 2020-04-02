import java.io.IOException;
import java.util.Scanner;

//A class which will be used for testing functionality of the program
public class Test {

    public void pieceMove(Type type, boolean isWhite, int cX, int cY) throws IOException {
        Scanner in = new Scanner(System.in);
        Game.start();
        //Table.addPiece(Type.PAWN,false,1,7);
        Table.addPiece(type,isWhite,true,cX,cY);
        System.out.println("Set moving coordinates for the piece otherwise write -1:");
        int pX=cX,pY=cY;
        int x,y;
        do {
            x = in.nextInt();
            if(x == -1) break;
            y = in.nextInt();
           // if(Table.getPiece(pX,pY).move(x,y)) {
                pX=x;
                pY=y;
            //}
        }while(true);
    }

    public void checkNMateTest() throws IOException {
        Table.addPiece(Type.PAWN,true,true,2,7);
        //Table.addPiece(Type.PAWN,false,7,7); //These are for mate check
        //Table.addPiece(Type.PAWN,false,8,7);
        Table.addPiece(Type.KING,false,true,8,8);
        Table.addPiece(Type.KING,true,true,8,1);
        Game.printTable();
        Table.getPiece(2,7).moveNTableTurn(2,8);//Legal move
        Table.getPiece(8,8).moveNTableTurn(7,8);//Illegal move
        Table.getPiece(8,8).moveNTableTurn(7,7);//Legal move
        Table.getPiece(8,8).moveNTableTurn(8,7);//I
        Table.getPiece(7,7).moveNTableTurn(8,7);//I
    }
}
