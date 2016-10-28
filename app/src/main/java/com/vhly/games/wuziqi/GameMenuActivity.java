package com.vhly.games.wuziqi;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.vhly.games.wuziqi.game.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 13-11-5
 * Email: vhly@163.com
 */
public class GameMenuActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.gamemenu);

        TextView txtVer = (TextView) findViewById(R.id.about_ver);
        if (txtVer != null) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) {
                    String versionName = packageInfo.versionName;
                    txtVer.setText("ver " + versionName);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 菜单界面 Play 按钮点击事件
     *
     * @param v
     */
    public void btnPlayOnClick(View v) {
        Intent it = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Constants.INTENT_KEY_GAME_MODE, Constants.GAME_MODE_TWO_PLAYER);
        it.putExtras(extras);
        startActivity(it);
    }

    /**
     * 菜单界面 Play 按钮点击事件
     *
     * @param v
     */
    public void btnPlaySingleOnClick(View v) {
        Intent it = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Constants.INTENT_KEY_GAME_MODE, Constants.GAME_MODE_ONE_PLAYER_WITH_CPU);
        it.putExtras(extras);
        startActivity(it);
    }

    /**
     * 菜单界面 Help 按钮点击事件
     *
     * @param v
     */
    public void btnHelpOnClick(View v) {
        Intent it = new Intent(this, HelpActivity.class);
        startActivity(it);
    }

    public void btnChessManualOnClick(View v) {
        Intent it = new Intent(this, AutoShowActivity.class);
        Bundle extras = new Bundle();
        extras.putString("stepName", "steps/step_01.xml");
        it.putExtras(extras);
        startActivity(it);
    }
}