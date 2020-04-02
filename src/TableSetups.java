import java.io.IOException;

public class TableSetups {
    private static TableArr type;

    public static void set(TableArr type){
        TableSetups.type = type;
    }

    public static void load() throws IOException {
        switch (type){
            case BASIC:
                basic();
                break;
            case CHILDISH_MATE:
                childishMate();
                break;
            case CASTLING:
                castling();
                break;
            case PIN_CHECK:
                pinCheck();
                break;
            case PIECE_CHECK:
                pieceCheck();
                break;
            case SPASSKY_VS_FISCHER:
                spasskyVsFischer();
                break;
            case ENPASSANT:
                enPassant();
                break;
            case STALEMATE:
                stalemate();
                break;
            case IMPOSSIBLE_MATE:
                impossibleMate();
                break;
        }
    }

    public static void basic() throws IOException {
        for(int x=1;x<Table.SIZE;x++){
            Table.addPiece(Type.PAWN,true,true,x,2);
            Table.addPiece(Type.PAWN,false,true,x,7);
        }

        Table.addPiece(Type.KING,true,true,5,1);
        Table.addPiece(Type.KING,false,true,5,8);

        Table.addPiece(Type.QUEEN,true,true,4,1);
        Table.addPiece(Type.QUEEN,false,true,4,8);

        Table.addPiece(Type.BISHOP,true,true,3,1);
        Table.addPiece(Type.BISHOP,true,true,6,1);
        Table.addPiece(Type.BISHOP,false,true,3,8);
        Table.addPiece(Type.BISHOP,false,true,6,8);

        Table.addPiece(Type.KNIGHT,true,true,2,1);
        Table.addPiece(Type.KNIGHT,true,true,7,1);
        Table.addPiece(Type.KNIGHT,false,true,2,8);
        Table.addPiece(Type.KNIGHT,false,true,7,8);

        Table.addPiece(Type.ROOK,true,true,1,1);
        Table.addPiece(Type.ROOK,true,true,8,1);
        Table.addPiece(Type.ROOK,false,true,1,8);
        Table.addPiece(Type.ROOK,false,true,8,8);

        System.out.println("The basic setup is set!");
        Game.printTable();
    }

    public static void childishMate() throws IOException {
        for(int x=1;x<Table.SIZE;x++){
            Table.addPiece(Type.PAWN,false,true,x,7);
        }

        Table.addPiece(Type.KING,true,true,5,1);
        Table.addPiece(Type.KING,false,true,5,8);

        Table.addPiece(Type.QUEEN,true,true,6,3);
        Table.addPiece(Type.QUEEN,false,true,4,8);

        Table.addPiece(Type.BISHOP,true,true,3,4);
        Table.addPiece(Type.BISHOP,false,true,6,8);

        System.out.println("The childishMate setup is set!");
        Game.printTable();
    }

    public static void pinCheck() throws IOException {
        Table.addPiece(Type.KING,true,true,4,4);
        Table.addPiece(Type.ROOK,true,true,5,4);

        Table.addPiece(Type.KING,false,true,8,8);
        Table.addPiece(Type.QUEEN,false,true,8,4);

        System.out.println("The pinCheck setup is set!");
        Game.printTable();
    }

    public static void pieceCheck() throws IOException {
        Table.addPiece(Type.KING,true,true,8,1);
        Table.addPiece(Type.PAWN,true,true,4,2);

        Table.addPiece(Type.KING,false,true,8,8);

        System.out.println("The pinCheck setup is set!");
        Game.printTable();
    }

    public static void castling() throws IOException {
        for(int x=1;x<Table.SIZE;x++){
            Table.addPiece(Type.PAWN,true,true,x,2);
            Table.addPiece(Type.PAWN,false,true,x,7);
        }

        Table.addPiece(Type.KING,true,true,5,1);
        Table.addPiece(Type.KING,false,true,5,8);

        Table.addPiece(Type.ROOK,true,true,1,1);
        Table.addPiece(Type.ROOK,true,true,8,1);
        Table.addPiece(Type.ROOK,false,true,1,8);
        Table.addPiece(Type.ROOK,false,true,8,8);

        System.out.println("The castling setup is set!");
        Game.printTable();
    }

    public static void spasskyVsFischer() throws IOException {
        Table.addPiece(Type.KING,true,true,5,2);
        Table.addPiece(Type.KING,false,true,6,8);

        Table.addPiece(Type.BISHOP,true,true,3,1);
        Table.addPiece(Type.BISHOP,false,true,8,2);

        Table.addPiece(Type.PAWN,true,true,1,3);
        Table.addPiece(Type.PAWN,true,true,2,5);
        Table.addPiece(Type.PAWN,true,true,5,3);
        Table.addPiece(Type.PAWN,true,true,6,2);
        Table.addPiece(Type.PAWN,true,true,7,3);

        Table.addPiece(Type.PAWN,false,true,1,7);
        Table.addPiece(Type.PAWN,false,true,2,7);
        Table.addPiece(Type.PAWN,false,true,6,6);
        Table.addPiece(Type.PAWN,false,true,7,7);
        Table.addPiece(Type.PAWN,false,true,8,4);
        Table.addPiece(Type.PAWN,false,true,5,6);

        System.out.println("The Spassky vs Fischer (game 1 move 19) setup is set!");
        Game.printTable();
    }

    public static void stalemate() throws IOException {
        Table.addPiece(Type.KING,true,true,1,1);
        Table.addPiece(Type.KING,false,true,8,8);

        Table.addPiece(Type.QUEEN,true,true,6,1);

        System.out.println("Stalemate setup is set!");
        Game.printTable();
    }

    public static void impossibleMate() throws IOException {
        Table.addPiece(Type.KING,true,true,1,1);
        Table.addPiece(Type.KING,false,true,8,8);

        Table.addPiece(Type.BISHOP,true,true,6,1);
        Table.addPiece(Type.BISHOP,false,true,3,6);

        Table.addPiece(Type.PAWN,false,false,1,6);
        System.out.println("Impossible mate setup is set!");
        Game.printTable();
    }

    public static void enPassant() throws IOException {
        Table.addPiece(Type.KING,true,true,1,1);
        Table.addPiece(Type.KING,false,true,8,8);

        Table.addPiece(Type.PAWN,false,true,1,7);
        Table.addPiece(Type.PAWN,true,true,2,4);
        System.out.println("Impossible mate setup is set!");
        Game.printTable();
    }
}
