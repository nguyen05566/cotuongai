package com.zaro.cotuong;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends Activity {
    private static final String TAG="MainActivity";
    private BoardView boardView;
    private TextView turnText,statusText;
    private Button btnNew,btnUndo,btnHint,btnFlip,btnRed,btnBlack;
    private Board board;
    private PikafishEngine engine;
    private List<HistoryEntry> history=new ArrayList<>();
    private Handler handler=new Handler(Looper.getMainLooper());
    private boolean aiThinking=false,engineReady=false;
    private boolean aiRed=false,aiBlack=true;
    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        boardView=findViewById(R.id.boardView); turnText=findViewById(R.id.turnText); statusText=findViewById(R.id.statusText);
        btnNew=findViewById(R.id.btnNew); btnUndo=findViewById(R.id.btnUndo); btnHint=findViewById(R.id.btnHint);
        btnFlip=findViewById(R.id.btnFlip); btnRed=findViewById(R.id.btnSelectRed); btnBlack=findViewById(R.id.btnSelectBlack);
        board=new Board(); boardView.setBoard(board);
        statusText.setText("Đang khởi động Pikafish...");
        new Thread(()->{
            engine=new PikafishEngine(MainActivity.this);
            if(engine.init()){ engineReady=true; handler.post(()->{ statusText.setText("Pikafish sẵn sàng"); updateTurn(); }); }
            else handler.post(()->statusText.setText("Lỗi khởi động Pikafish engine"));
        }).start();
        boardView.setOnMoveListener((fr,fc,tr,tc)->{ if(!aiThinking) tryUserMove(fr,fc,tr,tc); });
        btnNew.setOnClickListener(v->newGame()); btnUndo.setOnClickListener(v->undo()); btnHint.setOnClickListener(v->hint());
        btnFlip.setOnClickListener(v->boardView.toggleFlip());
        btnRed.setOnClickListener(v->{ aiRed=false;aiBlack=true; Toast.makeText(this,"Bạn đi Đỏ (trước)",Toast.LENGTH_SHORT).show(); newGame(); });
        btnBlack.setOnClickListener(v->{ aiRed=true;aiBlack=false; Toast.makeText(this,"Bạn đi Đen (sau)",Toast.LENGTH_SHORT).show(); newGame(); });
    }
    private void tryUserMove(int fr,int fc,int tr,int tc){
        boolean isRed=board.isRed(board.cells[fr][fc]);
        if(isRed&&aiRed) return; if(!isRed&&aiBlack) return;
        if(!MoveValidator.isValidMove(board,fr,fc,tr,tc)){ boardView.clearSelection(); return; }
        doMove(fr,fc,tr,tc);
        if(!MoveValidator.hasLegalMoves(board)){ statusText.setText("Bạn thắng!"); return; }
        if((board.isRedTurn()&&aiRed)||(board.isBlackTurn()&&aiBlack)) triggerAI();
    }
    private void doMove(int fr,int fc,int tr,int tc){
        int cap=board.cells[tr][tc]; history.add(new HistoryEntry(fr,fc,tr,tc,cap,board.isRedTurn()));
        board.makeMove(fr,fc,tr,tc); boardView.setLastMove(fr,fc,tr,tc); boardView.clearSelection(); updateTurn();
    }
    private void triggerAI(){
        if(!engineReady) return; aiThinking=true; statusText.setText("Pikafish đang tính...");
        new Thread(()->{
            String best=engine.getBestMove(board.toFen(),2000);
            handler.post(()->{ aiThinking=false;
                if(best!=null){ int[] m=MoveValidator.parseUci(best);
                    if(m!=null){ doMove(m[0],m[1],m[2],m[3]); statusText.setText(MoveValidator.hasLegalMoves(board)?"Đến lượt bạn":"AI thắng!"); }
                }else statusText.setText("AI hết nước - Bạn thắng!");
            });
        }).start();
    }
    private void hint(){
        if(!engineReady||aiThinking) return; statusText.setText("Đang tìm gợi ý...");
        new Thread(()->{
            String best=engine.getBestMove(board.toFen(),1500);
            handler.post(()->{ if(best!=null){ int[] m=MoveValidator.parseUci(best); if(m!=null){ boardView.setHint(m[0],m[1],m[2],m[3]); statusText.setText("Gợi ý nước đi!"); } } });
        }).start();
    }
    private void undo(){
        if(aiThinking||history.size()<2) return;
        for(int i=0;i<2;i++) if(!history.isEmpty()){ HistoryEntry e=history.remove(history.size()-1); board.cells[e.fr][e.fc]=board.cells[e.tr][e.tc]; board.cells[e.tr][e.tc]=e.captured; }
        board.reset(); for(HistoryEntry e:history) board.makeMove(e.fr,e.fc,e.tr,e.tc);
        boardView.clearSelection(); boardView.setHint(-1,-1,-1,-1); updateTurn(); statusText.setText("Đã đi lại");
    }
    private void newGame(){
        board.reset(); boardView.setBoard(board); boardView.clearSelection(); boardView.setHint(-1,-1,-1,-1); history.clear(); aiThinking=false; updateTurn(); statusText.setText("Ván mới");
        if(aiRed) triggerAI();
    }
    private void updateTurn(){ turnText.setText(board.isRedTurn()?(aiRed?"Lượt: Đỏ (AI)":"Lượt: Đỏ (Bạn)"):(aiBlack?"Lượt: Đen (AI)":"Lượt: Đen (Bạn)")); }
    @Override protected void onDestroy(){ super.onDestroy(); if(engine!=null) engine.destroy(); }
    private static class HistoryEntry{ int fr,fc,tr,tc,captured; boolean wasRed; HistoryEntry(int fr,int fc,int tr,int tc,int c,boolean r){ this.fr=fr;this.fc=fc;this.tr=tr;this.tc=tc;captured=c;wasRed=r; } }
}
