import java.util.*;
public class Board{
    int[][] data = new int[8][8];
    boolean wLeftC, wRightC, bLeftC, bRightC;
    int enPassant = -999;
    HashSet<Move> playerMoves, computerMoves;
    HashSet<Move> playerChildren, computerChildren;
    Move wLeftCastle = new Move(4, 7, 1, 7);
    Move bLeftCastle = new Move(4, 0, 1, 0);
    Move wRightCastle = new Move(4, 7, 6, 7);
    Move bRightCastle = new Move(4, 0, 6, 0);
    Board(){
        for(int x=0;x<8;x++){
            data[x][6] = Piece.PAWN;
            data[x][1] = -Piece.PAWN;
        }
        for(int y=0;y<=7;y+=7){
            int p = (y==0) ? -1 : 1;
            data[0][y] = p * Piece.ROOK;
            data[7][y] = p * Piece.ROOK;
            data[1][y] = p * Piece.KNIGHT;
            data[6][y] = p * Piece.KNIGHT;
            data[2][y] = p * Piece.BISHOP;
            data[5][y] = p * Piece.BISHOP;
            data[3][y] = p * Piece.QUEEN;
            data[4][y] = p * Piece.KING;
        }
        wLeftC = wRightC = bLeftC = bRightC = true;
    }

    Board(Board parent, Move m){
        // copy data
        for(int x=0;x<8;x++)
            for(int y=0;y<8;y++)
                data[x][y] = parent.data[x][y];
        wLeftC = parent.wLeftC; wRightC = parent.wRightC;
        bLeftC = parent.bLeftC; bRightC = parent.bRightC;
        // temp vars
        int origin = data[m.origX][m.origY];
        int destin = data[m.destX][m.destY];
        int piece = Math.abs(origin);
        // change state vars
        if(piece==Piece.KING){
            if(origin > 0)
                wLeftC = wRightC = false;
            else
                bLeftC = bRightC = false;
            if(m.equals(wLeftCastle)){
                data[2][7] = Piece.ROOK;
                data[0][7] = 0;
            } else if(m.equals(wRightCastle)){
                data[5][7] = Piece.ROOK;
                data[7][7] = 0;
            } else if(m.equals(bLeftCastle)){
                data[2][0] = -Piece.ROOK;
                data[0][0] = 0;
            } else if(m.equals(bRightCastle)){
                data[5][0] = -Piece.ROOK;
                data[7][0] = 0;
            }
        }
        if(piece==Piece.ROOK)
            if(origin > 0){
                if(m.origX == 0)
                    wLeftC = false;
                else if(m.origX == 7)
                    wRightC = false;
            } else {
                if(m.origX == 0)
                    bLeftC = false;
                else if(m.origX == 7)
                    bRightC = false;
            }
        if(piece==Piece.PAWN)
            if(Math.abs(m.origY-m.destY)==2)
                enPassant = m.origX;
            else if(destin==0 && m.origX!=m.destX)
                data[m.destX][m.origY] = 0;
            else if(m.destY==0)
                origin = Piece.QUEEN;
            else if(m.destY==7)
                origin = -Piece.QUEEN;
        // move piece
        data[m.destX][m.destY] = origin;
        data[m.origX][m.origY] = 0;
    }

    int evaluation(){
        int sum = 0;
        for(int x=0;x<8;x++)
            for(int y=0;y<8;y++)
                sum += data[x][y];
        // if a king is missing
        if(Math.abs(sum) > 500)
            return (sum > 0) ? 999 : -999;
        return sum;
    }

    HashSet<Move> getMoves(boolean player){
        if(player && playerMoves!=null)
            return playerMoves;
        else if(!player && computerMoves!=null)
            return computerMoves;
        HashSet<Move> moves = new HashSet<Move>();
        for(int x=0;x<8;x++)
            for(int y=0;y<8;y++)
                if((player && data[x][y]>0)||(!player && data[x][y]<0)){
                    int piece = Math.abs(data[x][y]);
                    if(piece==Piece.PAWN)
                        moves.addAll(Pawn.getMoves(this, x, y));
                    else if(piece==Piece.KNIGHT)
                        moves.addAll(Knight.getMoves(this, x, y));
                    else if(piece==Piece.BISHOP)
                        moves.addAll(Bishop.getMoves(this, x, y));
                    else if(piece==Piece.ROOK)
                        moves.addAll(Rook.getMoves(this, x, y));
                    else if(piece==Piece.QUEEN)
                        moves.addAll(Queen.getMoves(this, x, y));
                    else if(piece==Piece.KING)
                        moves.addAll(King.getMoves(this, x, y));
                }
        if(player)
            playerMoves = moves;
        else
            computerMoves = moves;
        return moves;
    }

    /**
     * Finds out if the opposing player will kill a king on their turn
     */
    boolean isValidMove(Move move, boolean player){
        // Note: can't castle out of check
        HashSet<Move> moves = new HashSet<Move>();
        boolean valid = true;
        Board b = new Board(this, move);
        for(Move m : b.getMoves(!player))
            if(Math.abs(b.data[m.destX][m.destY])==999)
                return false;
        return valid;
    }

    HashSet<Move> getChildren(boolean player){
        if(player && playerChildren!=null)
            return playerChildren;
        else if(!player && computerChildren!=null)
            return computerChildren;
        HashSet<Move> output = new HashSet<Move>();
        for(Move m : getMoves(player))
            if(isValidMove(m, player))
                output.add(m);
        if(player)
            playerChildren = output;
        else
            computerChildren = output;
        return output;
    }

    boolean gameOver(boolean player){
        if(player && playerChildren!=null)
            return playerChildren.size()==0;
        else if(!player && computerChildren!=null)
            return computerChildren.size()==0;
        return getChildren(player).size()==0;
    }

    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int y=0;y<8;y++){
            for(int x=0;x<8;x++){
                s.append(Piece.toChar(data[x][y]));
                s.append(" ");
            }
            if(y!=7)
                s.append("\n");
        }
        return s.toString();
    }
}
