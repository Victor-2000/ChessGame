public class Rook extends Piece {

    public Rook(boolean isWhiteCurrent, boolean notMoved, int x, int y) {
        super(isWhiteCurrent, x, y);
        type = Type.ROOK;
        this.notMoved = notMoved;
    }

    @Override
    public boolean isMoveLegal(int currentX, int currentY) {
        return isMoveLegalQRB(currentX,currentY);
    }

    public boolean isClearPath(int currentX,int currentY){
        int cx = Integer.compare(this.x,currentX);
        int cy = Integer.compare(this.y,currentY);
        while((currentX!=x || currentY!=y) && isLegalPos(currentX,currentY)){
            currentX+=cx;
            currentY+=cy;
            if(isLegalPos(currentX,currentY)
                    && (currentX!=x || currentY!=y)
                    && Table.getPiece(currentX,currentY).type != Type.NULL){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean legalByMoveType(int currentX,int currentY){
        if(currentX==4 && currentY==1){
            currentX=4;
        }
        return isLegalPos(currentX, currentY) && (x == currentX) ^ (y == currentY) && isClearPath(currentX,currentY);
    }
}