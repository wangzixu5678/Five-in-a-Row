package com.vhly.games.wuziqi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import com.vhly.games.wuziqi.chessmap.ChessParser;
import com.vhly.games.wuziqi.chessmap.ChessStepHandler;
import com.vhly.games.wuziqi.game.PanView;
import com.vhly.games.wuziqi.game.StepData;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 13-11-1
 * Email: vhly@163.com
 */
public class AutoShowActivity extends FragmentActivity {

    private PanView gameView;

    private Stack<StepData> myShowSteps;
    private Stack<StepData> steps;
    private HashMap<String, StepData> points;

    private int index;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.autoshow);

        gameView = (PanView) findViewById(R.id.gameView);

        gameView.setLocked(true);

        steps = new Stack<StepData>();
        myShowSteps = new Stack<StepData>();
        points = new HashMap<String, StepData>();

        gameView.setSteps(steps);
        gameView.setPoints(points);

        myShowSteps = new Stack<StepData>();

        index = 0;

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String stepName = extras.getString("stepName");
                if (stepName != null) {
                    try {
                        InputStream in = getAssets().open(stepName);
                        if (in != null) {
                            ChessStepHandler handler = new ChessStepHandler();
                            ChessParser.parseSteps(in, handler);
                            Stack<StepData> stepData = handler.getStepData();
                            if (stepData != null) {
                                if (!stepData.isEmpty()) {
                                    myShowSteps.addAll(stepData);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void showNextStep(View v) {
        int size = myShowSteps.size();
        if (index < size) {
            StepData step = myShowSteps.get(index);
            index++;
            steps.push(step);
            gameView.postInvalidate();
        }
    }
}