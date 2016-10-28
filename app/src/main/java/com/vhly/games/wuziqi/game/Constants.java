package com.vhly.games.wuziqi.game;

/**
 * Created by IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 13-10-22
 * Email: vhly@163.com
 */
public class Constants {
    public static final int TURN_NONE = -1;
    public static final int TURN_BLACK = 0;
    public static final int TURN_WHITE = 1;

    public static final int WINNER_NONE = -1;
    public static final int WINNER_BLACK = 0;
    public static final int WINNER_WHITE = 1;
    public static final String FLAG_X = "X:";
    public static final String FLAG_Y = "Y:";

    /**
     * 两个玩家队战
     */
    public static final int GAME_MODE_TWO_PLAYER = 0;
    /**
     * 一个玩家和一个电脑队战
     */
    public static final int GAME_MODE_ONE_PLAYER_WITH_CPU = 1;

    public static final String INTENT_KEY_GAME_MODE = "gameMode";
}
