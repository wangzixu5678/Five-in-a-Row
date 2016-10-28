package com.vhly.games.wuziqi.game;

/**
 * Created by IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 13-10-22
 * Email: vhly@163.com
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.vhly.games.wuziqi.R;

import java.util.HashMap;
import java.util.Stack;

/**
 * 棋盘 视图 主要用于游戏的绘制，<br/>
 * 游戏的业务逻辑应该移动到 Activity 遵守 MVC 设计模式
 */
public class PanView extends View {

    /**
     * 棋盘的行数，包含两个边框的数目
     */
    private static final int NUM_ROWS = 16;
    /**
     * 棋盘的列数，包含两个边框的数目
     */
    private static final int NUM_COLS = 16;

    /**
     * 固定的一格宽度
     */
    public int CELL_WIDTH = 48;
    /**
     * 固定的一格高度
     */
    public int CELL_HEIGHT = 48;

    /**
     * 当前视图的宽度
     */
    private int currentWidth;
    /**
     * 当前视图的高度
     */
    private int currentHeight;

    /**
     * 绘制棋盘线参数
     */
    private Paint linePaint;
    /**
     * 绘制黑子的参数
     */
    private Paint blackPaint;
    /**
     * 绘制白子的参数
     */
    private Paint whitePaint;

    private int circleR;

    /**
     * 当前是哪一方
     */
    private int currentTurn;

    /**
     * 按照顺序的棋子信息，这个可以作为记录棋子顺序的仓库
     */
    private Stack<StepData> steps;

    /**
     * 坐标标识，用于快速查找棋子的位置
     */
    private HashMap<String, StepData> points;

    /**
     * 当前棋盘是否锁定，这个是赢了的时候用于禁止再点击棋盘的功能
     */
    private boolean locked = false;

    /**
     * 游戏状态监听器
     */
    private GameStateListener stateListener;

    private int[][] debugQ;
    private static final boolean DEBUG = false;

    public PanView(Context context) {
        this(context, null);
    }

    public PanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化信息
     *
     * @param ct    Context
     * @param attrs AttributeSet
     */
    private void init(Context ct, AttributeSet attrs) {
        if (ct != null && attrs != null) {
            TypedArray typedArray = ct.getTheme().obtainStyledAttributes(attrs, R.styleable.WuZiQiPanView, 0, 0);
            if (typedArray != null) {
                float ch = typedArray.getDimension(R.styleable.WuZiQiPanView_cellHeight, 48);
                float cw = typedArray.getDimension(R.styleable.WuZiQiPanView_cellWidth, 48);
                CELL_HEIGHT = (int) ch;
                CELL_WIDTH = (int) cw;
            }
        }
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(1);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setAntiAlias(true);

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blackPaint.setStrokeWidth(1);
        blackPaint.setAntiAlias(true);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        whitePaint.setStrokeWidth(1);
        whitePaint.setAntiAlias(true);
    }

    public void setDebugQ(int[][] debugQ) {
        this.debugQ = debugQ;
    }

    /**
     * 设置存储区，用于坐标标识
     *
     * @param points HashMap&lt;String, StepData&gt;
     */
    public void setPoints(HashMap<String, StepData> points) {
        this.points = points;
    }

    /**
     * 设置下棋存储区
     *
     * @param steps Stack&lt;StepData&gt;
     */
    public void setSteps(Stack<StepData> steps) {
        this.steps = steps;
    }

    /**
     * 设置游戏监听器，当游戏检查出有人赢了，则进行通知
     *
     * @param stateListener GameStateListener
     */
    public void setStateListener(GameStateListener stateListener) {
        this.stateListener = stateListener;
    }

    /**
     * 获取是否锁定
     *
     * @return boolean
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 设置是否锁定棋盘
     *
     * @param locked boolean
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * 重新一盘游戏
     */
    public void restart() {
        if (steps == null) {
            steps = new Stack<StepData>();
        }
        steps.clear();

        if (points == null) {
            points = new HashMap<String, StepData>();
        }
        points.clear();

        locked = false;

        currentTurn = Constants.TURN_BLACK;
    }

    /**
     * 测量棋盘视图的尺寸，同时也是最终设置尺寸最后的流程
     *
     * @param widthMeasureSpec  MeasureSpec
     * @param heightMeasureSpec MeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int ws = MeasureSpec.getSize(widthMeasureSpec);
        int hs = MeasureSpec.getSize(heightMeasureSpec);

        ws = ws / NUM_COLS;
        ws = ws * NUM_COLS;
        hs = hs / NUM_ROWS;
        hs = hs * NUM_ROWS;

//        ws = ws / CELL_WIDTH;
//        ws *= CELL_WIDTH;
//        hs /= CELL_HEIGHT;
//        hs *= CELL_HEIGHT;
        setMeasuredDimension(ws, hs);
    }

    /**
     * 尺寸变化
     *
     * @param w    新尺寸
     * @param h    新尺寸
     * @param oldw 旧尺寸
     * @param oldh 旧尺寸
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        currentWidth = w;
        currentHeight = h;
//        NUM_ROWS = currentHeight / CELL_HEIGHT;
//        NUM_COLS = currentWidth / CELL_WIDTH;
        CELL_HEIGHT = currentHeight / NUM_ROWS;
        CELL_WIDTH = currentWidth / NUM_COLS;
        int cc = CELL_WIDTH >= CELL_HEIGHT ? CELL_HEIGHT : CELL_WIDTH;
        if (cc > 20) {
            circleR = 10;
        } else {
            circleR = 6;
        }
    }

    /**
     * 核心绘制
     *
     * @param canvas Canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentWidth == 0 || currentHeight == 0) {
            return;
        }

        float currentX = CELL_WIDTH;
//        float currentY = 0;
        float currentY = CELL_HEIGHT;
        for (int i = 0; i < NUM_COLS; i++) {
            canvas.drawLine(currentX, currentY, currentX, currentHeight - CELL_HEIGHT, linePaint);
            currentX += CELL_WIDTH;
        }

//        currentX = 0;
        currentX = CELL_WIDTH;
        currentY = CELL_HEIGHT;
        for (int i = 0; i < NUM_ROWS; i++) {
            canvas.drawLine(currentX, currentY, currentWidth - CELL_WIDTH, currentY, linePaint);
            currentY += CELL_HEIGHT;
        }
        if (DEBUG) {
            if (debugQ != null) {
                currentY = CELL_HEIGHT;
                for (int i = 0; i < NUM_ROWS; i++) {
                    currentX = CELL_WIDTH;
                    for (int j = 0; j < NUM_COLS; j++) {
                        int dq = debugQ[i][j];
                        canvas.drawText(Integer.toString(dq), currentX, currentY, linePaint);
                        currentX += CELL_WIDTH;
                    }
                    currentY += CELL_HEIGHT;
                }
            }
        }

        if (steps != null && !steps.isEmpty()) {
            Paint currentPaint = null;
            for (StepData step : steps) {
                if (step.turn == Constants.TURN_BLACK) {
                    currentPaint = blackPaint;
                } else {
                    currentPaint = whitePaint;
                }
                canvas.drawCircle(step.panX * CELL_WIDTH, step.panY * CELL_HEIGHT, circleR, currentPaint);
            }
        }
    }

    /**
     * 记录当前一方的点击屏幕坐标与棋盘坐标的映射<br/>
     * 如果返回 true 那么下一步操作就由自动下棋或者是交换下一轮。
     *
     * @param ex 点击事件的x坐标
     * @param ey 点击事件的y坐标
     * @return boolean 是否记录 此处返回主要用于是否继续下一步操作用户
     * @see PanView#locked locked
     * @see PanView#currentTurn currentTurn
     * @see Constants#TURN_BLACK
     * @see Constants#TURN_WHITE
     */
    public boolean recordTouch(float ex, float ey) {
        boolean bret = false;
        if (!locked) {
            float col = ex / CELL_WIDTH;
            float row = ey / CELL_HEIGHT;
            int icol = Math.round(col);
            int irow = Math.round(row);
//            Log.d("Pan", "icol = " + icol + " irow = " + irow);
            // because pan lines scale small, and point must check from 1 to 15
            if (irow < NUM_ROWS && irow > 0 && icol < NUM_COLS && icol > 0) {
                bret = recordStep(icol, irow);
            }
        }
        return bret;
    }

    /**
     * 记录一个子的位置
     *
     * @param icol 所在坐标
     * @param irow 所在坐标
     * @return 是否记录了
     */
    public boolean recordStep(int icol, int irow) {
        boolean bret = false;
        String flag = Constants.FLAG_X + icol + Constants.FLAG_Y + irow;
        if (!points.containsKey(flag)) {
            StepData data = new StepData();
            data.panX = icol;
            data.panY = irow;
            data.turn = currentTurn;
            steps.push(data);
            points.put(flag, data);
            if (currentTurn == Constants.TURN_BLACK) {
                currentTurn = Constants.TURN_WHITE;
            } else if (currentTurn == Constants.TURN_WHITE) {
                currentTurn = Constants.TURN_BLACK;
            }
            postInvalidate();
            bret = true;
            int winner = calcWinner();
            if (winner != Constants.WINNER_NONE) {
                bret = false;
                if (stateListener != null) {
                    stateListener.gameWin(winner);
                }
            }
        }
        return bret;
    }

    /**
     * 计算当前棋盘中是否包含5个一条线上的相同颜色棋子（米字形）
     *
     * @return turn 当前赢的一方
     * @see Constants#TURN_BLACK Constants.TURN_BLACK 黑方
     * @see Constants#TURN_WHITE Constants.TURN_WHITE 白方
     */
    private int calcWinner() {
        int ret = Constants.WINNER_NONE;
        if (!steps.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < NUM_COLS; col++) {
                for (int row = 0; row < NUM_ROWS; row++) {
                    sb.append(Constants.FLAG_X).append(col).append(Constants.FLAG_Y).append(row);
                    String flag = sb.toString();
                    sb.setLength(0);
                    if (points.containsKey(flag)) {
                        StepData data = points.get(flag);
                        if (hasMyFive(data)) {
                            ret = data.turn;
                        }
                    }

                }
            }
        }
        return ret;
    }

    /**
     * 计算一个点周围是否有5个相同颜色的子<br/>
     * 由于采用从左到右的网格检查，因此使用这种算法<br/>
     * 优化方式<br/>
     * <ol>
     * <li>遍历所有下过子的坐标来进行处理 避免 row * col 每次重复这么多次</li>
     * </ol><br/>
     * 核心游戏检测流程
     *
     * @param data StepData 当前要检查的子
     * @return boolean true: 找到相同颜色的子 够5个; false: 指定颜色和位置的子没有构成5个
     */
    private boolean hasMyFive(StepData data) {
        boolean bret = false;
        int px = data.panX;
        int py = data.panY;
        int pt = data.turn;
        int count = 1;
        // 斜上检查、正中检查、斜下检查 进行 3 次处理
        int cx = px, cy = py;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {  // 变量为4因为 上来就检查 cx++, cy-- 也就是 看看当前左边以外的4个数 计算右上斜线
            cx++;
            cy--;
            if (cy >= 0 && cx <= NUM_COLS) {
                sb.append(Constants.FLAG_X).append(cx).append(Constants.FLAG_Y).append(cy);
                String flag = sb.toString();
                sb.setLength(0);
                if (points.containsKey(flag)) {
                    StepData dd = points.get(flag);
                    if (pt == dd.turn) {
                        count++;
                    } else {
                        count = 1;
                        break;
                    }
                } else {
                    count = 1;
                    break;
                }
            } else {
                count = 1;
                break;
            }
        }
        if (count >= 5) {
            bret = true;
        } else {
            cx = px;
            cy = py;
            for (int i = 0; i < 4; i++) {  // 计算右边横线
                cx++;
//                cy--;
                if (cy >= 0 && cx <= NUM_COLS) {
                    sb.append(Constants.FLAG_X).append(cx).append(Constants.FLAG_Y).append(cy);
                    String flag = sb.toString();
                    sb.setLength(0);
                    if (points.containsKey(flag)) {
                        StepData dd = points.get(flag);
                        if (pt == dd.turn) {
                            count++;
                        } else {
                            count = 1;
                            break;
                        }
                    } else {
                        count = 1;
                        break;
                    }
                } else {
                    count = 1;
                    break;
                }
            }
            if (count >= 5) {
                bret = true;
            } else {
                cx = px;
                cy = py;
                for (int i = 0; i < 4; i++) { // 计算右下方斜线
                    cx++;
                    cy++;
                    if (cy <= NUM_ROWS && cx <= NUM_COLS) {
                        sb.append(Constants.FLAG_X).append(cx).append(Constants.FLAG_Y).append(cy);
                        String flag = sb.toString();
                        sb.setLength(0);
                        if (points.containsKey(flag)) {
                            StepData dd = points.get(flag);
                            if (pt == dd.turn) {
                                count++;
                            } else {
                                count = 1;
                                break;
                            }
                        } else {
                            count = 1;
                            break;
                        }
                    } else {
                        count = 1;
                        break;
                    }
                }
                if (count >= 4) {
                    bret = true;
                } else {
                    cx = px;
                    cy = py;
                    for (int i = 0; i < 4; i++) { // 计算下方直线
                        cy++;
                        if (cy <= NUM_ROWS && cx <= NUM_COLS) {
                            sb.append(Constants.FLAG_X).append(cx).append(Constants.FLAG_Y).append(cy);
                            String flag = sb.toString();
                            sb.setLength(0);
                            if (points.containsKey(flag)) {
                                StepData dd = points.get(flag);
                                if (pt == dd.turn) {
                                    count++;
                                } else {
                                    count = 1;
                                    break;
                                }
                            } else {
                                count = 1;
                                break;
                            }
                        } else {
                            count = 1;
                            break;
                        }
                    }
                    if (count >= 4) {
                        bret = true;
                    }
                }
            }
        }
        return bret;
    }

    /**
     * 活期当前是哪一方需要下
     *
     * @return int Turn
     * @see Constants#TURN_BLACK Constants.TURN_BLACK 黑方
     * @see Constants#TURN_WHITE Constants.TURN_WHITE 白方
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * 设置当前该哪一方下了
     *
     * @param turn int
     * @see Constants#TURN_WHITE
     * @see Constants#TURN_BLACK
     */
    public void setCurrentTurn(int turn) {
        if (turn == Constants.TURN_BLACK) {
            currentTurn = turn;
        } else if (turn == Constants.TURN_WHITE) {
            currentTurn = turn;
        } else {
            throw new IllegalArgumentException("Turn must be black or white");
        }
    }

    /**
     * 悔棋<br/>
     * 删除上一步，并且上一方继续下棋
     */
    public void revertPrevStep() {
        if (!steps.isEmpty()) {
            StepData pop = steps.pop();
            StringBuilder sb = new StringBuilder();
            String key = sb.append(Constants.FLAG_X).append(pop.panX).append(Constants.FLAG_Y).append(pop.panY).toString();
            points.remove(key);
            currentTurn = pop.turn;
            invalidate();
        }
    }
}
