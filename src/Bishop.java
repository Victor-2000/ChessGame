public class Bishop extends Piece {

    public Bishop(boolean isWhiteCurrent, boolean notMoved, int x, int y) {
        super(isWhiteCurrent, x, y);
        type = Type.BISHOP;
        this.notMoved = notMoved;
    }

    @Override
    public boolean isMoveLegal(int currentX, int currentY) {
        return isMoveLegalQRB(currentX,currentY);
    }

    @Override
    public boolean legalByMoveType(int currentX,int currentY){
        return isLegalPos(currentX, currentY) && x!=currentX && y!=currentY &&
                ((Math.abs(x - currentX) == Math.abs(y - currentY)
                || x + y == currentX + currentY)) && isClearPath(currentX,currentY);
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
}
