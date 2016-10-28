package com.vhly.games.wuziqi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.vhly.games.wuziqi.game.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MainActivity extends FragmentActivity implements View.OnTouchListener, GameStateListener, DialogInterface.OnClickListener {

    private PanView gameView;

    private AlertDialog winDialog;

    private Handler gameStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) { // Game Win
                int arg1 = msg.arg1;
                Resources res = getResources();
                String who = arg1 == Constants.WINNER_BLACK
                        ? res.getString(R.string.player_black)
                        : res.getString(R.string.player_white);

                gameView.setLocked(true);
                if (winDialog == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setPositiveButton(R.string.play_again, MainActivity.this);
                    builder.setNegativeButton(R.string.cancel, MainActivity.this);
                    winDialog = builder.create();
                }


                String winState = res.getString(R.string.victory);
                winDialog.setTitle(R.string.win);

                if (gameMode == Constants.GAME_MODE_ONE_PLAYER_WITH_CPU) {
                    who = res.getString(R.string.you);
                    if (arg1 == Constants.WINNER_BLACK) {
                        winDialog.setTitle(R.string.win);
                        winState = res.getString(R.string.win_state_win);
                    } else {
                        winDialog.setTitle(R.string.lost);
                        winState = res.getString(R.string.win_state_lost);
                    }
                }

                String s = who + " " + winState;

                winDialog.setMessage(s);
                winDialog.show();
            }
        }
    };

    /////////////////////////

    // 信息部分
    private TextView txtBlackInfo;
    private TextView txtWhiInfo;
    /////////////////////////

    // 游戏数据

    private Stack<StepData> steps;

    private HashMap<String, StepData> points;

    private int gameMode = Constants.GAME_MODE_TWO_PLAYER;

    /////////////////////////

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.INTENT_KEY_GAME_MODE)) {
                gameMode = extras.getInt(Constants.INTENT_KEY_GAME_MODE);
            }
        }
        setContentView(R.layout.main);

        View viewById = findViewById(R.id.topControllerBar);

        if (gameMode == Constants.GAME_MODE_ONE_PLAYER_WITH_CPU) {
            if (viewById != null) {
                viewById.setVisibility(View.INVISIBLE);
            }
        }

        gameView = (PanView) findViewById(R.id.gameView);

        gameView.setOnTouchListener(this);
        gameView.setStateListener(this);

        steps = new Stack<StepData>();
        points = new HashMap<String, StepData>();

        gameView.setSteps(steps);
        gameView.setPoints(points);

        if (savedInstanceState != null) {
            Serializable ss = savedInstanceState.getSerializable("steps");
            if (ss != null) {
                if (ss instanceof List) {
                    List list = (List) ss;
                    for (Object o : list) {
                        if (o instanceof StepData) {
                            StepData step = (StepData) o;
                            steps.push(step);
                        }
                    }
                }
            }

            ss = savedInstanceState.getSerializable("points");
            if (ss != null) {
                if (ss instanceof Map) {
                    Map map = (Map) ss;
                    points.putAll(map);
                }
            }
            boolean locked = savedInstanceState.getBoolean("GAME_LOCKED");
            gameView.setLocked(locked);

            int currentTurn = savedInstanceState.getInt("currentTurn");
            gameView.setCurrentTurn(currentTurn);

            gameMode = savedInstanceState.getInt(Constants.INTENT_KEY_GAME_MODE);
        }
    }

    /**
     * 屏幕旋转，触发新的 Activity，就会触发这个方法来保存数据
     *
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        boolean locked = gameView.isLocked();
        outState.putBoolean("GAME_LOCKED", locked);
        if (steps != null) {
            outState.putSerializable("steps", steps);
        }
        if (points != null) {
            outState.putSerializable("points", points);
        }
        int currentTurn = gameView.getCurrentTurn();
        outState.putInt("currentTurn", currentTurn);

        outState.putInt(Constants.INTENT_KEY_GAME_MODE, gameMode);
    }

    // 游戏结束对话框

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (dialogInterface == winDialog) {
            dialogInterface.dismiss();
            if (i == DialogInterface.BUTTON_POSITIVE) {
                // OK
                gameView.restart();
                gameView.postInvalidate();
            } else if (i == DialogInterface.BUTTON_NEGATIVE) {
                // Cancel
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean processed = false;
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            float ex = motionEvent.getX();
            float ey = motionEvent.getY();
            if (gameMode == Constants.GAME_MODE_TWO_PLAYER) {
                gameView.recordTouch(ex, ey);
            } else {
                boolean work = gameView.recordTouch(ex, ey);
                if (work && !gameView.isLocked()) {
                    gameView.setLocked(true);
                    // TODO 添加 第一个棋子的位置
                    int currentTurn = gameView.getCurrentTurn();
//                    int[][] mydata = AI.calcScore(points, currentTurn);
//                    int[][] mydata = AI.calcRoundScore(points, currentTurn);
                    int[][] mydata = AI.calcChaScore(points, currentTurn);
//                    gameView.setDebugQ(mydata);
                    int[] mylocation = new int[2];
                    int mmax = AI.maxScore(mydata, mylocation);
                    if (currentTurn == Constants.TURN_BLACK) {
                        currentTurn = Constants.TURN_WHITE;
                    } else {
                        currentTurn = Constants.TURN_BLACK;
                    }
//                    int[][] tdata = AI.calcScore(points, currentTurn);
//                    int[][] tdata = AI.calcRoundScore(points, currentTurn);
                    int[][] tdata = AI.calcChaScore(points, currentTurn);
                    int[] tlocation = new int[2];
                    int tmax = AI.maxScore(tdata, tlocation);
                    if (mmax > tmax) {
                        // TODO 走自己的位置
                        gameView.recordStep(mylocation[1], mylocation[0]);
                    } else {
                        // TODO 走敌方的位置
                        gameView.recordStep(tlocation[1], tlocation[0]);
                    }
                    gameView.setLocked(false);
                }
            }
            processed = true;
        }
        return processed;
    }

    /**
     * 游戏结束，有人赢了
     *
     * @param winner 赢了游戏的一方 可选值 Constants.WINNER_BLACK Constants.WINNER_WHITE
     * @see com.vhly.games.wuziqi.game.Constants#WINNER_NONE
     * @see com.vhly.games.wuziqi.game.Constants#WINNER_BLACK
     * @see com.vhly.games.wuziqi.game.Constants#WINNER_WHITE
     */
    @Override
    public void gameWin(int winner) {
        Message message = gameStateHandler.obtainMessage(1);
        message.arg1 = winner;
        gameStateHandler.sendMessage(message);
    }

    /**
     * 悔棋按钮事件支持
     *
     * @param v
     */
    public void btnRevertStep(View v) {
        int id = v.getId();
        int turn = gameView.getCurrentTurn();
        if (id == R.id.topRevertStep) {
            if (turn == Constants.TURN_BLACK) {
                // 当前是黑方的时候，也就是说，上一次是白方，需要悔棋
                gameView.revertPrevStep();
            }
        } else if (id == R.id.bottomRevertStep) {
            if (gameMode == Constants.GAME_MODE_ONE_PLAYER_WITH_CPU) {
                gameView.revertPrevStep();
                gameView.revertPrevStep();
            } else if (gameMode == Constants.GAME_MODE_TWO_PLAYER) {
                if (turn == Constants.TURN_WHITE) {
                    // 当前是白方的时候，也就是说，上一次是黑方，需要悔棋
                    gameView.revertPrevStep();
                }
            }
        }
    }

    /**
     * 记录当前棋盘棋子<br/>
     * 保存在存储卡目录 /sdcard/Android/data/wuziqi/maps/ 中
     *
     * @param v View
     */
    public void btnRecordOnClick(View v) {
        File storageDirectory = Environment.getExternalStorageDirectory();
        File targetDir = new File(storageDirectory, "Android/data/wuziqi/maps/");
        boolean bok = false;
        if (!targetDir.exists()) {
            bok = targetDir.mkdirs();
        }
        if (bok) {
            long ct = System.currentTimeMillis();
            File targetFile = new File(targetDir, "map" + ct + ".txt");
            try {
                if (!targetFile.exists()) {
                    bok = targetFile.createNewFile();
                }
                if (bok) {
                    FileWriter fw = null;
                    PrintWriter pw = null;
                    fw = new FileWriter(targetFile);
                    pw = new PrintWriter(fw);
                    pw.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    pw.print("<steps title=\"\" desc=\"\">\n");
                    StringBuilder sb = new StringBuilder();
                    for (StepData step : steps) {
                        sb.append("<step panx=\"")
                                .append(step.panX).append("\" pany=\"")
                                .append(step.panY).append("\" turn=\"")
                                .append(step.turn).append("\" desc=\"\"/>");
                        pw.print(sb.toString());
                        sb.setLength(0);
                    }
                    pw.print("</steps>");
                    pw.close();
                    fw.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
