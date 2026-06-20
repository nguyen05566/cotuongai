package com.zaro.cotuong;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.*;
public class PikafishEngine {
    private static final String TAG="PikafishEngine";
    private static final String ENGINE_BIN="pikafish-armv8";
    private static final String NNUE_FILE="pikafish.nnue";
    private final Context ctx;
    private Process proc;
    private BufferedReader reader;
    private BufferedWriter writer;
    private File binaryFile;
    private File nnueFile;
    private volatile boolean engineBusy;
    private volatile boolean ready;
    public PikafishEngine(Context ctx){ this.ctx=ctx; }
    public boolean init(){
        try{
            File dir=ctx.getFilesDir();
            binaryFile=new File(dir,ENGINE_BIN);
            nnueFile=new File(dir,NNUE_FILE);
            if(!binaryFile.exists()||binaryFile.length()<100000){
                Log.d(TAG,"Extracting pikafish binary...");
                if(!copyAsset(ENGINE_BIN,binaryFile)){ Log.e(TAG,"Init failed: cannot extract binary"); return false; }
            }
            if(!nnueFile.exists()||nnueFile.length()<100000){
                Log.d(TAG,"Extracting pikafish.nnue...");
                if(!copyAsset(NNUE_FILE,nnueFile)){ Log.e(TAG,"Init failed: cannot extract NNUE"); return false; }
            }
            if(!binaryFile.setExecutable(true)){ Log.e(TAG,"Init failed: setExecutable failed"); return false; }
            ProcessBuilder pb=new ProcessBuilder(binaryFile.getAbsolutePath());
            pb.directory(dir); pb.redirectErrorStream(false);
            proc=pb.start();
            reader=new BufferedReader(new InputStreamReader(proc.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            final BufferedReader err=new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            new Thread(()->{ try{ String l; while((l=err.readLine())!=null) Log.e(TAG,"stderr: "+l); }catch(IOException ignored){} }).start();
            send("uci"); String line;
            while((line=reader.readLine())!=null) if(line.equals("uciok")) break;
            send("setoption name EvalFile value "+nnueFile.getAbsolutePath());
            send("setoption name Hash value 16");
            send("setoption name Threads value 1");
            send("isready");
            while((line=reader.readLine())!=null) if(line.equals("readyok")) break;
            send("ucinewgame");
            ready=true; Log.d(TAG,"Engine ready"); return true;
        }catch(Exception e){ Log.e(TAG,"Init failed",e); return false; }
    }
    private boolean copyAsset(String name,File dest){
        try(InputStream in=ctx.getAssets().open(name); OutputStream out=new FileOutputStream(dest)){
            byte[] buf=new byte[65536]; int n; while((n=in.read(buf))>0) out.write(buf,0,n); return true;
        }catch(IOException e){ Log.e(TAG,"copyAsset: "+name,e); return false; }
    }
    private void send(String cmd) throws IOException { writer.write(cmd+"\n"); writer.flush(); }
    public String getBestMove(String fen,int movetime){
        if(!ready) return null;
        engineBusy=true;
        try{
            send("position fen "+fen); send("go movetime "+movetime);
            long timeout=Math.max(movetime+5000,15000), start=System.currentTimeMillis();
            String line,best=null;
            while((line=reader.readLine())!=null){
                if(line.startsWith("bestmove ")){ String[] p=line.split(" "); if(p.length>=2&&!p[1].equals("(none)")) best=p[1]; break; }
                if(System.currentTimeMillis()-start>timeout) break;
            }
            engineBusy=false; return best;
        }catch(Exception e){ Log.e(TAG,"getBestMove failed",e); engineBusy=false; return null; }
    }
    public void sendUciNewGame(){ try{send("ucinewgame");}catch(Exception ignored){} }
    public boolean isReady(){ return ready; }
    public boolean isBusy(){ return engineBusy; }
    public void destroy(){
        ready=false;
        try{if(writer!=null){send("quit");writer.close();}}catch(Exception ignored){}
        try{if(reader!=null)reader.close();}catch(Exception ignored){}
        if(proc!=null)proc.destroy();
    }
}
