package com.vhly.games.wuziqi.game;

/**
 * Created by IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 13-10-22
 * Email: vhly@163.com
 */

import java.io.Serializable;

/**
 * 下棋的数据记录
 */
public class StepData implements Serializable, Comparable<StepData> {

    private static final long serialVersionUID = 192893;

    /**
     * 当前下棋的是黑棋还是白棋
     */
    public int turn;
    /**
     * 整个棋盘的位置 这个是列
     */
    public int panX;
    /**
     * 整个棋盘的位置 这个是行
     */
    public int panY;

    /**
     * 描述，这个如果用在棋谱部分，就可以用来进行自动下棋播放。<br/>
     * 这个不需要进行序列化
     */
    public String desc;

    @Override
    public int compareTo(StepData stepData) {
        int ret = 1;
        if (stepData != null) {
            int tpx = stepData.panX;
            int tpy = stepData.panY;
            int px = panX;
            int py = panY;
            if (px > tpx) {
                if (py >= tpy) {
                    ret = 1;
                } else {
                    ret = -1;
                }
            } else if (px == tpx) {
                if (py < tpy) {
                    ret = -1;
                } else if (py > tpy) {
                    ret = 1;
                } else {
                    ret = 0;
                }
            } else { // px < tpx
                if (py <= tpy) {
                    ret = -1;
                } else {
                    ret = 1;
                }
            }
        }
        return ret;
    }
}
