public class Move{
    int origX, origY;
    int destX, destY;

    public Move(int origX, int origY, int destX, int destY){
        this.origX = origX;
        this.origY = origY;
        this.destX = destX;
        this.destY = destY;
    }

    boolean equals(Move m){
        return origX == m.origX
        && origY == m.origY
        && destX == m.destX
        && destY == m.destY;
    }

    public String toString(){
        return ""
        + origX + ","
        + origY + "->"
        + destX + ","
        + destY;
    }
}
