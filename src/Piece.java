import java.io.IOException;
import java.util.ArrayList;

public class Piece {

    protected boolean isWhite;
    protected int x;
    protected int y;
    protected Type type = Type.NULL;
    protected boolean notMoved;

    public Piece(boolean isWhiteCurrent, int x, int y) {
        isWhite = isWhiteCurrent;
        this.x = x;
        this.y = y;
        type = Type.NULL;
    }

    public boolean isPieceWhite() {
        return isWhite;
    }

    //Checks if position is within the board range and that the position should not be itself
    public boolean isLegalPos(int currentX, int currentY) {
        return (currentX > 0 && currentX < Table.SIZE) && (currentY > 0 && currentY < Table.SIZE)
                && !(x == currentX && y == currentY);
    }

    //A (usually) ovewritten function that checks if a move is legal related to the type of piece we use
    public boolean isMoveLegal(int currentX, int currentY) {
        return isLegalPos(currentX, currentY) && Table.getPiece(currentX,currentY).getType() != Type.NULL;
    }

    public boolean isMoveLegalQRB(int currentX,int currentY){
        //The below if statement does the rook and bishop move legality check
        if(legalByMoveType(currentX,currentY)) {

            //The code below compares the 2 elements and determines the direction in which they are going
            int dY = Integer.compare(currentY, this.y);
            int dX = Integer.compare(currentX, this.x);
            int x = this.x;
            int y = this.y;

            //The code below checks every cell up till the last one if it is empty
            do {
                y += dY;
                x += dX;
            } while (isLegalPos(x, y) && Table.getPiece(x, y).type == Type.NULL && (y != currentY || x != currentX));

            //If the last position remaining is the chosen position and it is empty then it is allowed to move
            return y == currentY && x == currentX && Table.getPiece(x, y).type == Type.NULL;
        }
        return false;
    }

    //A function (overwritten just at pawn and king) that checks if a capture is legal related to the type of
    // piece we use
    public boolean isCaptureLegal(int currentX,int currentY){
        //TODO: Add in notation the capture syntax here
        return isLegalPos(currentX, currentY) && legalByMoveType(currentX,currentY)
                && Table.getPiece(currentX,currentY).getType() != Type.KING
                && Table.getPiece(currentX,currentY).getType() != Type.NULL
                && isWhite != Table.getPiece(currentX,currentY).isWhite;
    }

    public boolean legalByMoveType(int currentX, int currentY){
        return true;
    }

    public void getSweetSpots(){
        if(this.type == Type.BISHOP || this.type == Type.ROOK || this.type == Type.QUEEN) {
            getSweetSpotsRBQ();
        }else{
            Game.addToSweetSpots(this);
        }
    }

    public boolean drawPath(){
        if((Game.isWhitesTurn() != isWhite) || this.type == Type.NULL
                || (Game.isBoardBlock() && this.type != Type.KING)){
            return false;
        }
        ArrayList <Piece> paths = new ArrayList<>();
        ArrayList <Coord> pathsCoord = new ArrayList<>();
        for(int cX=1;cX<Table.SIZE;cX++){
            for(int cY=1;cY<Table.SIZE;cY++){
                if(move(cX,cY, true)){
                    Piece piece = Table.getPiece(cX,cY);
                    paths.add(piece);
                    pathsCoord.add(new Coord(cX,cY));
                    //Table.erase(cX,cY);
                    if(piece.type != Type.NULL) {
                        Table.setImTableElem(cX,cY,'x');
                    }else{
                        Table.setImTableElem(cX,cY,'*');
                    }
                }
            }
        }
        Game.setPaths(paths);
        Game.setPathsCoord(pathsCoord);
        return true;
    }
    public void getSweetSpotsRBQ() {
        King cK;
        if (isWhite) {
            cK = Game.getbKing();
        } else {
            cK = Game.getwKing();
        }

        int dY = Integer.compare(cK.y,this.y);
        int dX = Integer.compare(cK.x, this.x);
        int x = this.x;
        int y = this.y;

        do {
            Game.addToSweetSpots(Table.getPiece(x, y));
            y += dY;
            x += dX;
        } while (isLegalPos(x,y) && (y != cK.y || x != cK.x));
    }

    //A function that is responsible for moving the pieces around the board after it calls for legality checks
    public boolean move(int currentX, int currentY, boolean isDrawing) {
        boolean oneCheckLegalMove=false;

        Game.setSelectedPiece(this);
        if(Game.isGameOver()){
            Game.endMenu();
            return false;
        }

        if((Game.isWhitesTurn() != isWhite) || this.type == Type.NULL
                || (Game.isBoardBlock() && this.type != Type.KING)){
            if(!isDrawing) {
                Game.illegalMoveMenu();
            }
            return false;
        }

        if(Game.isOneCheckBlock() && this.type != Type.KING){
            Game.getCheckingPiece().getSweetSpots();
            for(Piece value: Game.getSweetSpots()){
                if(currentX==value.x && currentY==value.y){
                    oneCheckLegalMove=true;
                }
            }
            if (!oneCheckLegalMove){
                if(!isDrawing) {
                    Game.illegalMoveMenu();
                }
                return false;
            }
        }

        //Move and capture mechanisms basically erase the old piece in the old position and add a new piece in the
        //new one
        if (isMoveLegal(currentX, currentY)) {
            //We check if a move is legal
            Table.erase(x,y);
            if(!isDrawing) {
                Table.addPiece(type,isWhite,false,currentX,currentY);
            }else{
                Table.addPiece(type,isWhite,notMoved,currentX,currentY);
            }

            if(Game.plyCheck(false)){
                Table.erase(currentX,currentY);
                Table.addPiece(type,isWhite,notMoved,x,y);
                //Game.printTable();
                if(!isDrawing) {
                    Game.illegalMoveMenu();
                }
                Game.plyReset();
                return false;
            }
            if(isDrawing){
                Table.erase(currentX,currentY);
                Table.addPiece(type,isWhite,notMoved,x,y);
            }
            //System.out.println(x + " " + y + " -> " + currentX + " " + currentY);
            if(Table.getPiece(currentX,currentY).type==Type.KING && notMoved)
            {
                if(currentX-5==2){
                    Table.addPiece(Table.getPiece(8,y),x+1,y);
                    Table.erase(8,y);
                }else if(currentX-5==-2){
                    Table.addPiece(Table.getPiece(1,y),x-1,y);
                    Table.erase(1,y);
                }
            }
            if(!isDrawing) {
                notMoved = false;
                if(type==Type.PAWN){
                    Game.setIsPawnMoved(true);
                }
            }
            return true;
        } else if(isCaptureLegal(currentX,currentY)){
            //If the move is not legal we check for captures
            Piece piece = Table.getPiece(currentX,currentY);

            Table.erase(x,y);
            if(!isDrawing) {
                Table.addPiece(type,isWhite,false,currentX,currentY);
            }else{
                Table.addPiece(type,isWhite,notMoved,currentX,currentY);
            }
            if(Game.plyCheck(false)){
                Table.erase(currentX,currentY);
                Table.addPiece(piece,currentX,currentY);
                Table.addPiece(type,isWhite,notMoved,x,y);
                //Game.printTable();
                if(!isDrawing) {
                    Game.illegalMoveMenu();
                }
                Game.plyReset();
                return false;
            }
            if(isDrawing){
                Table.erase(currentX,currentY);
                Table.addPiece(piece,currentX,currentY);
                Table.addPiece(type,isWhite,notMoved,x,y);
            }
            //System.out.println(x + " " + y + " -x> " + currentX + " " + currentY);
            if(!isDrawing) {
                notMoved = false;
                if(this.type == Type.PAWN){
                    if(isWhite && Game.getEnPassantPawnB()!=null && currentX == Game.getEnPassantPawnBX()
                            && currentY-1 == Game.getEnPassantPawnBY()) {
                        Table.erase(currentX,currentY-1);
                        Game.setEnPassantPawnB(null,0,0);
                    }else if(!isWhite && Game.getEnPassantPawnW()!=null && currentX == Game.getEnPassantPawnWX()
                            && currentY+1 == Game.getEnPassantPawnWY()){
                        Table.erase(currentX,currentY+1);
                        Game.setEnPassantPawnW(null,0,0);
                    }
                }
                Game.setIsCapture(true);
            }

            return true;
        } else {
            if(!isDrawing) {
                Game.illegalMoveMenu();
            }
            return false;
        }
    }

    public void moveNTableTurn(int cX, int cY) throws IOException {
        if(move(cX,cY,false)){
            Game.nextPly();
            Game.printTable();
        }
    }

    public Type getType(){
        return type;
    }

}
