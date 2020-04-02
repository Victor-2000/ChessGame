public class Pawn extends Piece {

    //Standard pawn positions where certain rules apply (in particular promotion and en-passant)
    private static final int W_START = 2;
    private static final int W_END = 8;
    private static final int W_ENPASSANT = 5;
    private static final int B_START = 7;
    private static final int B_END = 1;
    private static final int B_ENPASSANT = 4;

    //TODO: Enpassant red capture
    //TODO: solve the enpassant error :
    // Pp<-has made a 2 move
    // p_
    public Pawn(boolean isWhiteCurrent, boolean notMoved, int x, int y) {
        super(isWhiteCurrent, x, y);
        type = Type.PAWN;
        this.notMoved = notMoved;
        //We use isPromotion here because the moving mechanism consists on creating a new piece in the new position
        //every time when a piece is moved
    }

    public void isPromotion(){
        if((y==W_END && isWhite)||(y==B_END && !isWhite)){
            Game.promotionMenu(x,y);
        }
    }

    //We do an override for isMoveLegal
    @Override
    public boolean isMoveLegal(int currentX, int currentY) {
        if (!isLegalPos(currentX, currentY)) {
            return false;
        }
        if(x==currentX) {
            if (isWhite) {
                //The condition lower than this comment is for checking that nothing stays in the way of the pawn
                if (Table.getPiece(x,y+1).type == Type.NULL) {

                    //We check if the pawn is at start and if the coordinates are obeying the rules
                    if (y == W_START && currentY > y && currentY <= y + 2
                            && Table.getPiece(x,y + 2).type==Type.NULL) {
                        if(currentY == y + 2) {
                            Game.setEnPassantPawnW(this,x,currentY);
                        }
                        return true;
                    }

                    //Simple move check and return of the answer
                    return currentY == y + 1;
                }
            } else {
                if (Table.getPiece(x,y-1).type==Type.NULL) {

                    //We check if the pawn is at start and if the coordinates are obeying the rules
                    if (y == B_START && currentY < y && currentY >= y - 2
                        && Table.getPiece(x,y-2).type==Type.NULL) {
                        if(currentY == y - 2) {
                            Game.setEnPassantPawnB(this,x,currentY);
                        }
                        return true;
                    }

                    //Simple move check and return of the answer
                    return currentY == y - 1;
                }
            }
        }
        return false;
    }

    //The only override because captures don't correspond to moves
    @Override
    public boolean isCaptureLegal(int currentX, int currentY/*, boolean isMoveReal*/){
        // The if condition below checks if the position is legal, does't have a king, and can be captured by
        // black/white pawn respectively in concordance with the rules
        if (isLegalPos(currentX, currentY) && Table.getPiece(currentX,currentY).getType() != Type.KING
                && ((currentY == y + 1 && isWhite) || (currentY == y - 1 && !isWhite))
                && Math.abs(x-currentX) == 1 && Table.getPiece(currentX,currentY).isWhite != isWhite) {

            //If the piece is directly in the capture cell then you are allowed to attack it
            if(Table.getPiece(currentX,currentY).getType() != Type.NULL) {
                return true;

                //If the piece is not there then you are allowed to capture if enpassant rules apply the 2 conditions
                //do it for the 2 types of pieces (black/white)
            }else if(isWhite && Game.getEnPassantPawnB()!=null && Game.getEnPassantPawnB().x == currentX
                    && Game.getEnPassantPawnB().y - 1 == currentY){
                /*Table.erase(currentX,currentY-1);
                Game.setEnPassantPawnB(null);*/
                return true;
            }else if (!isWhite && Game.getEnPassantPawnW()!=null && Game.getEnPassantPawnW().x == currentX
                    && Game.getEnPassantPawnB().y - 1 == currentY ){
                /*Table.erase(currentX,currentY+1);
                Game.setEnPassantPawnW(null);*/
                return true;
            }
        }
        return false;
    }
}
