package com.vhly.games.wuziqi.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: xhzhang
 * Date: 13-11-14
 */

/**
 * AI 逻辑
 */
public final class AI {
    /**
     * 权重
     */
    public static final int[] SCS = new int[]{0, 100, 400, 2000, 10000, 20000};
    public static final int MAX_CHECK_CHESS_COUNT = 4;

    /**
     * 获取指定坐标下面的棋子，检查是否存在
     *
     * @param steps 棋子数据 HashMap&lt;String, StepData&gt;
     * @param x     坐标
     * @param y     坐标
     * @return int 棋子颜色
     */
    private static int getLocationStep(HashMap<String, StepData> steps, int x, int y) {
        int ret = Constants.TURN_NONE;
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.FLAG_X).append(x).append(Constants.FLAG_Y).append(y);
        String flag = sb.toString();
        if (steps.containsKey(flag)) {
            StepData data = steps.get(flag);
            ret = data.turn;
        }
        return ret;
    }

    /**
     * Native 版本的计算权重最大值的方法
     * @param scores 棋盘中的权重表
     * @param location 找到的最大的权重数值的位置
     * @return 权重数值
     */
   // private static native int getMaxScore(int[][] scores, int[] location);

    /**
     * 计算分值中最大的分值，并且将(x,y)的坐标放到 location中<br/>
     * location 必须是 2个元素的数组
     *
     * @param scores   int[][]
     * @param location int[]
     * @return int 最大分值
     */
    public static int maxScore(int[][] scores, int[] location) {
        int ret = -1;
        if (scores != null && location != null) {
            int rows = scores.length;
            for (int r = 0; r < rows; r++) {
                int[] ss = scores[r];
                int cols = ss.length;
                for (int c = 0; c < cols; c++) {
                    int d = ss[c];
                    if (d >= ret) {
                        ret = d;
                        location[0] = r;
                        location[1] = c;
                    }
                }
            }
        }
        return ret;
    }

    public static int[][] calcChaScore(HashMap<String, StepData> steps, int turn) {
        int[][] ret = new int[16][16];
        int len = ret.length;
        for (int i = len - 1; i >= 0; i--) {
            ret[i] = new int[16];
        }
        // 遍历每一个棋子
        Collection<StepData> values = steps.values();

        if (!values.isEmpty()) {
            for (StepData next : values) {
                if (turn == next.turn) {
                    int col = next.panX;
                    int row = next.panY;

                    // 正上方
                    int dCol = col;
                    int dRow = row - 1;
                    checkEmptyQ(steps, ret, dCol, dRow);
                    // 正下方
                    dCol = col;
                    dRow = row + 1;
                    checkEmptyQ(steps, ret, dCol, dRow);
                    // 正左方
                    dCol = col - 1;
                    dRow = row;
                    checkEmptyQ(steps, ret, dCol, dRow);
                    // 正右方
                    dCol = col + 1;
                    dRow = row;
                    checkEmptyQ(steps, ret, dCol, dRow);
                    // 左上方
                    dCol = col - 1;
                    dRow = row - 1;
                    checkEmptyQ(steps, ret, dCol, dRow);
                    // 左下方
                    dCol = col - 1;
                    dRow = row + 1;
                    checkEmptyQ(steps, ret, dCol, dRow);
                    // 右上方
                    dCol = col + 1;
                    dRow = row - 1;
                    checkEmptyQ(steps, ret, dCol, dRow);
                    // 右下方
                    dCol = col + 1;
                    dRow = row + 1;
                    checkEmptyQ(steps, ret, dCol, dRow);

                    ///////////////////////////////////
                    // 检查指定点与当前点是否构成连线，连线则 将当前点另一个反方向上空白点加权
                    ///////////////////////////////////

                    // 正上方
                    dCol = col;
                    dRow = row - 1;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col, row + 1, turn);
                    // 正下方
                    dCol = col;
                    dRow = row + 1;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col, row - 1, turn);
                    // 正左方
                    dCol = col - 1;
                    dRow = row;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col + 1, row, turn);
                    // 正右方
                    dCol = col + 1;
                    dRow = row;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col - 1, row, turn);
                    // 左上方
                    dCol = col - 1;
                    dRow = row - 1;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col + 1, row + 1, turn);
                    // 左下方
                    dCol = col - 1;
                    dRow = row + 1;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col + 1, row - 1, turn);
                    // 右上方
                    dCol = col + 1;
                    dRow = row - 1;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col - 1, row + 1, turn);
                    // 右下方
                    dCol = col + 1;
                    dRow = row + 1;
                    checkNextEmptyQ(steps, ret, dCol, dRow, col - 1, row - 1, turn);
                }
            }
        }
        return ret;
    }

    /**
     * 检查指定坐标是否为空，并且没有超出范围
     *
     * @param steps 棋子列表
     * @param ret   权值数组
     * @param dcol  列
     * @param drow  行
     */
    private static void checkNextEmptyQ(HashMap<String, StepData> steps, int[][] ret, int dcol, int drow, int ncol, int nrow, int turn) {
        if (dcol >= 1 && dcol <= 15 && drow >= 1 && drow <= 15) {
            int dd = getLocationStep(steps, dcol, drow);
            if (dd == turn) {
                if (ncol >= 1 && ncol <= 15 && nrow >= 1 && nrow <= 15) {
                    dd = getLocationStep(steps, ncol, nrow);
                    if (dd == Constants.TURN_NONE) {
                        int cc = dcol - ncol;
                        int cr = drow - nrow;
                        if (cc > 0) {
                            cc = 1;
                        } else if (cc < 0) {
                            cc = -1;
                        }
                        if (cr > 0) {
                            cr = 1;
                        } else if (cr < 0) {
                            cr = -1;
                        }
                        int ccol = ncol, crow = nrow;
                        for (int i = 0; i < 5; i++) {
                            ccol += cc;
                            crow += cr;
                            dd = getLocationStep(steps, ccol, crow);
                            if (dd == turn) {
                                ret[nrow][ncol] = (i * 5);
                            } else {
                                break;
                            }
                        }
//                        ret[nrow][ncol]++;
                    }
                }
            }
        }
    }

    /**
     * 检查指定坐标是否为空，并且没有超出范围
     *
     * @param steps 棋子列表
     * @param ret   权值数组
     * @param dcol  列
     * @param drow  行
     */
    private static void checkEmptyQ(HashMap<String, StepData> steps, int[][] ret, int dcol, int drow) {
        if (dcol >= 1 && dcol <= 15 && drow >= 1 && drow <= 15) {
            int dd = getLocationStep(steps, dcol, drow);
            if (dd == Constants.TURN_NONE) {
                int old = ret[drow][dcol];
                if (old > 1) {
                    ret[drow][dcol]++;
                } else {
                    ret[drow][dcol] = 1;
                }
            }
        }
    }

    /**
     * 新的权重计算方法<br/>
     * 计算每一个子周围的空白节点的权重，减少计算量，同时空白节点权重 采取 能够连成几个子的方式
     *
     * @param steps
     * @param turn
     * @return
     */
    public static int[][] calcRoundScore(HashMap<String, StepData> steps, int turn) {
        int[][] ret = new int[16][16];
        int len = ret.length;
        for (int i = len - 1; i >= 0; i--) {
            ret[i] = new int[16];
        }

        // 遍历每一个棋子
        Collection<StepData> values = steps.values();

        if (values.size() == 1) { // only one
            Iterator<StepData> iterator = values.iterator();
            StepData next = iterator.next();
            if (turn == next.turn) {
                int col = next.panX;
                int row = next.panY;
                if (row - 1 >= 1) {
                    ret[row - 1][col] = 1;
                    if (col - 1 >= 1) {
                        ret[row - 1][col - 1] = 1;
                    }
                    if (col + 1 <= 15) {
                        ret[row - 1][col + 1] = 1;
                    }
                }
                if (row + 1 <= 15) {
                    ret[row + 1][col] = 1;
                    if (row - 1 >= 1) {
                        ret[row + 1][col - 1] = 1;
                    }
                    if (row + 1 <= 15) {
                        ret[row + 1][col + 1] = 1;
                    }
                }
                if (col - 1 >= 1) {
                    ret[row][col - 1] = 1;
                }
                if (col + 1 <= 15) {
                    ret[row][col + 1] = 1;
                }
            }
        } else {
            for (StepData step : values) {
                if (step.turn == turn) { // 待检验棋子
                    int px = step.panX;
                    int py = step.panY;

                    int count = 0;
                    int myLen = 0;
                    boolean hasEmpty = false;

                    //////////////  从左到右  ///////////////

                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // 水平 计算 1 - 5 之间 有几个子 从左到右
                        int cx = px + c;
                        int cy = py;
                        if (cx <= 15) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) { // 水平 计算 1 - 5 之间 有几个子 从左到右
                            int cx = px + c;
                            int cy = py;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }
                            }
                        }
                    }

                    //////////////  从右到左  ///////////////
                    count = 0;
                    myLen = 0;
                    hasEmpty = false;
                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // 水平 从右到左
                        int cx = px - c;
                        int cy = py;
                        if (cx >= 1) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) {
                            int cx = px - c;
                            int cy = py;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }
                            }
                        }
                    }

                    //////////////  从上到下  ///////////////

                    count = 0;
                    myLen = 0;
                    hasEmpty = false;
                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // 垂直 计算 1-5 之间有几个子 从上到下
                        int cx = px;
                        int cy = py + c;
                        if (cy <= 15) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) { // 水平 计算 1 - 5 之间 有几个子
                            int cx = px;
                            int cy = py + c;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }

                            }
                        }
                    }

                    //////////////  从下到上  ///////////////

                    count = 0;
                    myLen = 0;
                    hasEmpty = false;
                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // 垂直 计算 1-5 之间有几个子 从上到下
                        int cx = px;
                        int cy = py - c;
                        if (cy >= 1) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) { // 水平 计算 1 - 5 之间 有几个子
                            int cx = px;
                            int cy = py - c;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }

                            }
                        }
                    }

                    //////////////  从左上到右下  ///////////////

                    count = 0;
                    myLen = 0;
                    hasEmpty = false;
                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // \ 方向 计算 1-5 之间有几个子 从上到下
                        int cx = px + c;
                        int cy = py + c;
                        if (cy <= 15 && cx <= 15) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) { // 水平 计算 1 - 5 之间 有几个子
                            int cx = px + c;
                            int cy = py + c;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }

                            }
                        }
                    }

                    //////////////  从右下到左上  ///////////////

                    count = 0;
                    myLen = 0;
                    hasEmpty = false;
                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // \ 方向 计算 1-5 之间有几个子 从上到下
                        int cx = px - c;
                        int cy = py - c;
                        if (cy >= 1 && cx >= 1) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) { // 水平 计算 1 - 5 之间 有几个子
                            int cx = px - c;
                            int cy = py - c;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }
                            }
                        }
                    }

                    //////////////  从左下到右上  ///////////////
                    count = 0;
                    myLen = 0;
                    hasEmpty = false;
                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // / 方向 计算 1-5 之间有几个子 从下到上
                        int cx = px + c;
                        int cy = py - c;
                        if (cy >= 1 && cx <= 15) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) { // 水平 计算 1 - 5 之间 有几个子
                            int cx = px + c;
                            int cy = py - c;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }

                            }
                        }
                    }

                    //////////////  从右上到左下  ///////////////
                    count = 0;
                    myLen = 0;
                    hasEmpty = false;
                    for (int c = 0; c < MAX_CHECK_CHESS_COUNT; c++) { // / 方向 计算 1-5 之间有几个子 从下到上
                        int cx = px - c;
                        int cy = py + c;
                        if (cx >= 1 && cy <= 15) {
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == turn) {
                                count++;
                                myLen++;
                            } else if (ss == Constants.TURN_NONE) {
                                hasEmpty = true;
                                myLen++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasEmpty) { // 计算处理
                        for (int c = 0; c < myLen; c++) { // 水平 计算 1 - 5 之间 有几个子
                            int cx = px - c;
                            int cy = py + c;
                            int ss = getLocationStep(steps, cx, cy);
                            if (ss == Constants.TURN_NONE) {
                                int old = ret[cy][cx];
                                if (old < count + 1) {
                                    ret[cy][cx] = (count + 1);
                                }

                            }
                        }
                    }


                }
            }
        }

        return ret;
    }

    /**
     * 计算当前棋盘的分值<br/>
     * 核心算法，主要是计算每一个空白节点的权重
     *
     * @param steps
     * @param turn
     * @return
     */
    public static int[][] calcScore(HashMap<String, StepData> steps, int turn) {
        int[][] ret = new int[16][16];
        int len = ret.length;
        for (int i = len - 1; i >= 0; i--) {
            ret[i] = new int[16];
        }

        byte[] seeds = new byte[4];
        for (int a = 0; a < 16; a++) {    // 行
            for (int b = 0; b < 16; b++) {  // 列
                int cc = getLocationStep(steps, a, b);
                if (cc == Constants.TURN_NONE) {
                    // 计算空白区域的分值
                    byte d = 0;
                    // 计算当前空白子中，左边的 最大5个格
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a - c, b) != turn || a - c == 0)   //左边
                            break;
                        else
                            d++;
                    }
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a + c, b) != turn || a - c == 14)  //右边
                            break;
                        else
                            d++;
                    }
                    // 这个 d 是权重
                    seeds[0] = d;

                    d = 0;
                    // 计算当前空白子中，左边的 最大5个格
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a, b - c) != turn || b - c == 0)   //上边
                            break;
                        else
                            d++;
                    }
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a, b + c) != turn || b + c == 14)  //下边
                            break;
                        else
                            d++;
                    }
                    // 这个 d 是权重
                    seeds[1] = d;

                    d = 0;
                    // 计算当前空白子中，左边的 最大5个格
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a - c, b - c) != turn || b - c == 0 || a - c == 0)   //左上
                            break;
                        else
                            d++;
                    }
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a + c, b + c) != turn || b + c == 14 || a + c == 14)  //右下
                            break;
                        else
                            d++;
                    }
                    // 这个 d 是权重
                    seeds[2] = d;

                    d = 0;
                    // 计算当前空白子中，左边的 最大5个格
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a - c, b + c) != turn || a - c == 0 || b + c == 14)   //左上
                            break;
                        else
                            d++;
                    }
                    for (int c = 1; c < MAX_CHECK_CHESS_COUNT; c++) {
                        if (getLocationStep(steps, a + c, b - c) != turn || a + c == 14 || b - c == 0)  //右上
                            break;
                        else
                            d++;
                    }
                    // 这个 d 是权重
                    seeds[3] = d;
                    int sum = 0;
                    for (byte seed : seeds) {
                        if (seed > SCS.length) {
                            sum += 20000;
                            break;
                        }
                        sum += SCS[seed];
                    }
                    ret[a][b] = sum;
                }
            }
        }

        return ret;
    }
}
