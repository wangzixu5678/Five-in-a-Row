package com.vhly.games.wuziqi.game;

/**
 * Created by IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 13-10-22
 * Email: vhly@163.com
 */

/**
 * 游戏状态回调
 */
public interface GameStateListener {
    /**
     * 游戏结束，有人赢了
     *
     * @param winner 赢了游戏的一方 可选值 Constants.WINNER_BLACK Constants.WINNER_WHITE
     * @see Constants#WINNER_NONE
     * @see Constants#WINNER_BLACK
     * @see Constants#WINNER_WHITE
     */
    void gameWin(int winner);
}
