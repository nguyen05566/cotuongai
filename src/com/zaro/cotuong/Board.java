package com.zaro.cotuong;
import java.util.ArrayList;
import java.util.List;
public class Board {
    public static final int EMPTY = 0;
    public static final int B_KING = 1, B_ADV = 2, B_BIS = 3, B_KNIGHT = 4, B_ROOK = 5, B_CANNON = 6, B_PAWN = 7;
    public static final int R_KING = 8, R_ADV = 9, R_BIS = 10, R_KNIGHT = 11, R_ROOK = 12, R_CANNON = 13, R_PAWN = 14;
    private boolean turn = true;
    public int[][] cells = new int[10][9];
    public int lastFromR = -1, lastFromC = -1, lastToR = -1, lastToC = -1;
    public Board() { reset(); }
    public void reset() {
        cells = new int[][]{
            {B_ROOK,B_KNIGHT,B_BIS,B_ADV,B_KING,B_ADV,B_BIS,B_KNIGHT,B_ROOK},
            {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
            {EMPTY,B_CANNON,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,B_CANNON,EMPTY},
            {B_PAWN,EMPTY,B_PAWN,EMPTY,B_PAWN,EMPTY,B_PAWN,EMPTY,B_PAWN},
            {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
            {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
            {R_PAWN,EMPTY,R_PAWN,EMPTY,R_PAWN,EMPTY,R_PAWN,EMPTY,R_PAWN},
            {EMPTY,R_CANNON,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,R_CANNON,EMPTY},
            {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
            {R_ROOK,R_KNIGHT,R_BIS,R_ADV,R_KING,R_ADV,R_BIS,R_KNIGHT,R_ROOK}
        };
        turn = true;
        lastFromR = lastFromC = lastToR = lastToC = -1;
    }
    public boolean isRed(int p) { return p >= 8 && p <= 14; }
    public boolean isBlack(int p) { return p >= 1 && p <= 7; }
    public boolean isRedTurn() { return turn; }
    public boolean isBlackTurn() { return !turn; }
    public int colorOf(int p) { return isRed(p) ? 1 : isBlack(p) ? -1 : 0; }
    public void makeMove(int fr, int fc, int tr, int tc) {
        cells[tr][tc] = cells[fr][fc]; cells[fr][fc] = EMPTY;
        lastFromR = fr; lastFromC = fc; lastToR = tr; lastToC = tc; turn = !turn;
    }
    public boolean isEmpty(int r, int c) { return r<0||r>9||c<0||c>8||cells[r][c]==EMPTY; }
    public boolean isLegal(int fr, int fc, int tr, int tc) {
        if(fr<0||fr>9||fc<0||fc>8||tr<0||tr>9||tc<0||tc>8) return false;
        int p = cells[fr][fc]; if(p==EMPTY) return false;
        int t = cells[tr][tc]; if(colorOf(p)==colorOf(t)&&t!=EMPTY) return false;
        switch(p){
            case B_KING: case R_KING: return checkKing(fr,fc,tr,tc,p);
            case B_ADV: case R_ADV: return checkAdvisor(fr,fc,tr,tc,p);
            case B_BIS: case R_BIS: return checkBishop(fr,fc,tr,tc,p);
            case B_KNIGHT: case R_KNIGHT: return checkKnight(fr,fc,tr,tc);
            case B_ROOK: case R_ROOK: return checkRook(fr,fc,tr,tc);
            case B_CANNON: case R_CANNON: return checkCannon(fr,fc,tr,tc);
            case B_PAWN: case R_PAWN: return checkPawn(fr,fc,tr,tc,p);
            default: return false;
        }
    }
    private boolean checkKing(int fr, int fc, int tr, int tc, int p) {
        int minR=isRed(p)?7:0, maxR=isRed(p)?9:2;
        if(tr<minR||tr>maxR||tc<3||tc>5) return false;
        return (Math.abs(fr-tr)+Math.abs(fc-tc))==1;
    }
    private boolean checkAdvisor(int fr, int fc, int tr, int tc, int p) {
        int minR=isRed(p)?7:0, maxR=isRed(p)?9:2;
        if(tr<minR||tr>maxR||tc<3||tc>5) return false;
        return Math.abs(fr-tr)==1&&Math.abs(fc-tc)==1;
    }
    private boolean checkBishop(int fr, int fc, int tr, int tc, int p) {
        if(Math.abs(fr-tr)!=2||Math.abs(fc-tc)!=2) return false;
        if(isRed(p)&&tr<5||isBlack(p)&&tr>4) return false;
        return cells[(fr+tr)/2][(fc+tc)/2]==EMPTY;
    }
    private boolean checkKnight(int fr, int fc, int tr, int tc) {
        int dr=Math.abs(fr-tr), dc=Math.abs(fc-tc);
        if(!(dr==2&&dc==1||dr==1&&dc==2)) return false;
        int lr=fr,lc=fc;
        if(dr==2) lr=fr+(tr>fr?1:-1); else lc=fc+(tc>fc?1:-1);
        return cells[lr][lc]==EMPTY;
    }
    private boolean checkRook(int fr, int fc, int tr, int tc) {
        if(fr!=tr&&fc!=tc) return false;
        int dr=Integer.signum(tr-fr), dc=Integer.signum(tc-fc);
        int r=fr+dr, c=fc+dc;
        while(r!=tr||c!=tc){ if(cells[r][c]!=EMPTY) return false; r+=dr; c+=dc; }
        return true;
    }
    private boolean checkCannon(int fr, int fc, int tr, int tc) {
        if(fr!=tr&&fc!=tc) return false;
        int dr=Integer.signum(tr-fr), dc=Integer.signum(tc-fc), cnt=0;
        int r=fr+dr, c=fc+dc;
        while(r!=tr||c!=tc){ if(cells[r][c]!=EMPTY) cnt++; r+=dr; c+=dc; }
        return cells[tr][tc]==EMPTY ? cnt==0 : cnt==1;
    }
    private boolean checkPawn(int fr, int fc, int tr, int tc, int p) {
        if(Math.abs(fr-tr)+Math.abs(fc-tc)!=1) return false;
        if(isRed(p)){ if(tr>fr||(fr<=4&&fc!=tc)) return false; }
        else{ if(tr<fr||(fr>=5&&fc!=tc)) return false; }
        return true;
    }
    public boolean inCheck() {
        int kp=turn?R_KING:B_KING, kr=-1,kc=-1;
        for(int r=0;r<10;r++) for(int c=0;c<9;c++) if(cells[r][c]==kp){kr=r;kc=c;break;}
        if(kr<0) return false;
        for(int r=0;r<10;r++) for(int c=0;c<9;c++) if(cells[r][c]!=EMPTY&&colorOf(cells[r][c])!=colorOf(kp)&&isLegal(r,c,kr,kc)) return true;
        return false;
    }
    public boolean kingsFaceEachOther() {
        int rr=-1,rc=-1,br=-1,bc=-1;
        for(int r=0;r<10;r++) for(int c=0;c<9;c++){
            if(cells[r][c]==R_KING){rr=r;rc=c;} if(cells[r][c]==B_KING){br=r;bc=c;}
        }
        if(rc!=bc) return false;
        int min=Math.min(rr,br),max=Math.max(rr,br);
        for(int r=min+1;r<max;r++) if(cells[r][rc]!=EMPTY) return false;
        return true;
    }
    public String toFen(){
        StringBuilder sb=new StringBuilder();
        for(int r=0;r<10;r++){ int e=0;
            for(int c=0;c<9;c++){ char ch=pieceToFenChar(cells[r][c]);
                if(ch=='0') e++; else{ if(e>0){sb.append(e);e=0;} sb.append(ch); }
            }
            if(e>0) sb.append(e); if(r<9) sb.append('/');
        }
        sb.append(turn?" r":" b"); return sb.toString();
    }
    public static char pieceToFenChar(int p){
        switch(p){
            case B_KING:return 'k';case B_ADV:return 'a';case B_BIS:return 'b';
            case B_KNIGHT:return 'n';case B_ROOK:return 'r';case B_CANNON:return 'c';case B_PAWN:return 'p';
            case R_KING:return 'K';case R_ADV:return 'A';case R_BIS:return 'B';
            case R_KNIGHT:return 'N';case R_ROOK:return 'R';case R_CANNON:return 'C';case R_PAWN:return 'P';
            default:return '0';
        }
    }
    public int[][] getBoard(){
        int[][] copy=new int[10][9];
        for(int r=0;r<10;r++) for(int c=0;c<9;c++) copy[r][c]=cells[r][c];
        return copy;
    }
}
