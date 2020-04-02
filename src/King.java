public class King extends Piece {
    public int noCheck=0;
    public Piece checkingPiece;
    boolean isKingCheckedForCheck = false;

    public King(boolean isWhiteCurrent, boolean notMoved, int x, int y) {
        super(isWhiteCurrent, x, y);
        type = Type.KING;
        this.notMoved = notMoved;
        if(isWhite) {
            Game.setwKing(this);
        }else{
            Game.setbKing(this);
        }
    }

    @Override
    public boolean isMoveLegal(int currentX, int currentY) {
        return isLegalPos(currentX, currentY)
                && Table.getPiece(currentX,currentY).type==Type.NULL
                &&((Math.abs(x - currentX) <= 1 && Math.abs(y - currentY) <= 1) || isCastlingLegal(currentX,currentY))
                && !isCheck(currentX, currentY);
    }

    public boolean isCastlingLegal(int currentX, int currentY){

        if(notMoved && currentY==y && Table.getPiece(currentX,y).type==Type.NULL
                && Math.abs(currentX - x) == 2 && !isCheck(x,y)
                && isLegalPos(currentX, y) && !isCheck(currentX, y)){
            if(currentX > x && Table.getPiece(8,y).type == Type.ROOK
                    && Table.getPiece(8,y).notMoved
                    && !isCheck(x+1, y)){
                return Table.getPiece(x+1,y).type == Type.NULL;
            }else if(currentX < x && Table.getPiece(1,y).type == Type.ROOK
                    && Table.getPiece(1,y).notMoved
                    && !isCheck(x-1, y)){
                return Table.getPiece(x-1,y).type == Type.NULL;
            }
        }

        return false;

    }

    @Override
    public boolean isCaptureLegal(int currentX, int currentY){
        return isLegalPos(currentX,currentY) && !isCheck(currentX,currentY) && super.isCaptureLegal(currentX,currentY)
                && (Math.abs(x - currentX) <= 1 && Math.abs(y - currentY) <= 1);
    }

    //Check control routines for determining legal positions for the king to move
    public boolean isCheck(int currentX, int currentY) {
        //Resets the number of attackers in the current situation
        noCheck = 0;
        isKingCheckedForCheck = (Table.getPiece(currentX,currentY).type == Type.KING);
        return isKingAtk(currentX, currentY) || isKnightAtk(currentX, currentY) || isPawnAtk(currentX, currentY)
                || isQBRAtk(currentX, currentY);
    }

    //Checks if the king is attacked by a foreign pawn
    private boolean isPawnAtk(int currentX, int currentY) {
        int x = currentX;
        int y = currentY;
        //If the king is white the pawn should be searched higher than his y, if black - lower
        if(isPieceWhite()){
            y++;
        }else {
            y--;
        }
        if(pawnCheck(x+1,y)){
            noCheck++;
        }
        if(pawnCheck(x-1,y)){
            noCheck++;
        }
        if(noCheck!=0){
            return true;
        }
        return false;
    }

    //Is this an enemy pawn on a legal position?
    private boolean pawnCheck(int x,int y){
        if(isLegalPos(x,y) && Table.getPiece(x,y).isPieceWhite() != isPieceWhite()  &&  Table.getPiece(x,y).type == Type.PAWN){
            checkingPiece = Table.getPiece(x,y);
            return true;
        }
        return false;
    }

    private boolean isKingAtk(int currentX, int currentY) {
        int[] direction = {-1,0,1};
        //We use the direction int array for shortening code and not repeating ourselves. It's a trick learned from lee.
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                int x=currentX+direction[i];
                int y=currentY+direction[j];
                if(isLegalPos(x,y) && Table.getPiece(x,y).isPieceWhite() != isPieceWhite() && Table.getPiece(x,y).type == Type.KING){
                    checkingPiece = Table.getPiece(x,y);
                    return true;
                }
            }
        }
        return false;
    }

    //Is this an enemy king on a legal position?
    private boolean isKnightAtk(int currentX, int currentY){
        int[] direction = {1,-1};
        for(int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                //A slightly twitched trick for getting all 8 legal positions
                if(knightCheck(currentX+2*direction[i],currentY+direction[j])){
                    noCheck++;
                }
                if(knightCheck(currentX+direction[i],currentY+2*direction[j])){
                    noCheck++;
                }
            }
        }
        if(noCheck!=0){
            return true;
        }
        return false;
    }

    @Override
    public boolean isLegalPos(int currentX, int currentY) {
        return (currentX > 0 && currentX < Table.SIZE) && (currentY > 0 && currentY < Table.SIZE);
    }

    //Is this an enemy knight on a legal position?
    private boolean knightCheck(int x,int y){
        if( isLegalPos(x,y) && Table.getPiece(x,y).isPieceWhite() != isPieceWhite()
                && Table.getPiece(x,y).type == Type.KNIGHT){
            checkingPiece = Table.getPiece(x,y);
            return true;
        }
        return false;
    }

    private boolean isQBRAtk(int currentX, int currentY){
        Type[] qR = {Type.QUEEN,Type.ROOK};
        boolean isPassed = false;
        int[] direction = {1,-1};
        for(int i=0;i<2;i++) {
            int x = currentX; //Check right-left rook flank we go until we find a piece
            do {
                x+=direction[i];
                //System.out.println("x:"+x);
            }while(isLegalPos(x,currentY) && (Table.getPiece(x,currentY).type==Type.NULL
                    || Table.getPiece(x,currentY) == this));
            if(qRBCheck(x,currentY,qR)){
                noCheck++;
            }
        }

        for(int i=0;i<2;i++) {
            int y=currentY;//Check up-down rook flank we go until we find a piece
            do{
                y+=direction[i];
                //System.out.println("y:"+y);
            }while(isLegalPos(currentX,y) && (Table.getPiece(currentX,y).type==Type.NULL
                    || Table.getPiece(currentX,y) == this));
            if(qRBCheck(currentX,y,qR)){
                noCheck++;
            }
        }
        //Check the bishop flanks using the direction trick simulating diagonal movement this time
        Type[] qB = {Type.QUEEN, Type.BISHOP};
        for(int i=0;i<2;i++) {
            for(int j=0;j<2;j++) {
                int y = currentY;
                int x = currentX;
                do {
                    //We go until we find a piece
                    y+=direction[i];
                    x+=direction[j];
                    //System.out.println("x: "+x+" y: "+y);
                }while (isLegalPos(x, y) && (Table.getPiece(x, y).type == Type.NULL
                        || Table.getPiece(x, y) == this));
                if (qRBCheck(x, y, qB)) {
                    noCheck++;
                }
            }
        }
        if(noCheck!=0){
            return true;
        }
        return false;
    }
    //Is this an enemy queen/rook/bishop on a legal position?
    private boolean qRBCheck(int cX, int cY, Type[] types){
        //Types determine which mode is selected (queen+bishop or queen+rook) for checkup
        if(isLegalPos(cX,cY) && Table.getPiece(cX,cY).isPieceWhite() != isPieceWhite()) {
            for (Type value : types) {
                if (Table.getPiece(cX, cY).type == value) {
                    checkingPiece = Table.getPiece(cX,cY);
                    return true;
                }
            }
        }
        return false;
    }
}