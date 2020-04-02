public class Knight extends Piece {

    public Knight(boolean isWhiteCurrent, boolean notMoved, int x, int y) {
        super(isWhiteCurrent, x, y);
        type = Type.KNIGHT;
        this.notMoved = notMoved;
    }

    @Override
    public boolean isMoveLegal(int currentX, int currentY) {

        //The code below does the knight move legality check
        return legalByMoveType(currentX,currentY)
                && Table.getPiece(currentX,currentY).getType() == Type.NULL;
    }

    @Override
    public boolean legalByMoveType(int currentX, int currentY){
        int difX = Math.abs(x - currentX);
        int difY = Math.abs(y - currentY);
        return isLegalPos(currentX, currentY) && (difX <= 2 && difY <= 2 && difX > 0 && difY > 0 && difX != difY);
    }
}
