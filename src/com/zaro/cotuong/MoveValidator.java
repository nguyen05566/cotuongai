package com.zaro.cotuong;
import java.util.ArrayList;
import java.util.List;
public class MoveValidator {
    public static boolean isValidMove(Board board, int fr, int fc, int tr, int tc) {
        if(!board.isLegal(fr,fc,tr,tc)) return false;
        int cap=board.cells[tr][tc];
        board.cells[tr][tc]=board.cells[fr][fc]; board.cells[fr][fc]=Board.EMPTY;
        boolean ck=board.inCheck(), fk=board.kingsFaceEachOther();
        board.cells[fr][fc]=board.cells[tr][tc]; board.cells[tr][tc]=cap;
        return !ck&&!fk;
    }
    public static List<int[]> getLegalMoves(Board board, int r, int c) {
        List<int[]> moves=new ArrayList<>();
        if(board.cells[r][c]==Board.EMPTY) return moves;
        for(int tr=0;tr<10;tr++) for(int tc=0;tc<9;tc++) if(isValidMove(board,r,c,tr,tc)) moves.add(new int[]{tr,tc});
        return moves;
    }
    public static boolean hasLegalMoves(Board board){
        boolean rd=board.isRedTurn();
        for(int r=0;r<10;r++) for(int c=0;c<9;c++){
            int p=board.cells[r][c];
            if((rd&&board.isRed(p))||(!rd&&board.isBlack(p))) if(!getLegalMoves(board,r,c).isEmpty()) return true;
        }
        return false;
    }
    public static int[] parseUci(String uci){
        if(uci==null||uci.length()<4) return null;
        return new int[]{9-(uci.charAt(1)-'0'),uci.charAt(0)-'a',9-(uci.charAt(3)-'0'),uci.charAt(2)-'a'};
    }
    public static String toUci(int fr,int fc,int tr,int tc){
        return ""+(char)('a'+fc)+(char)('0'+(9-fr))+(char)('a'+tc)+(char)('0'+(9-tr));
    }
}
