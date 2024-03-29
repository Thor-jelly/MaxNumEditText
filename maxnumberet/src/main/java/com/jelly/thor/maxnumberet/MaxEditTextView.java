package com.jelly.thor.maxnumberet;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 类描述： 必须调用这个方法setModule，自定义保留位数edittext<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2017/8/31 17:38 <br/>
 */
public class MaxEditTextView extends AppCompatEditText {
    private static final String TAG = "MaxEditTextView";
    private static final String DOT_STR = ".";
    /**
     * 是否是debug模式
     */
    private boolean mIsDebug = false;
    /**
     * 小数时如果后面为0是否显示
     */
    private boolean mIsShowEnd0 = false;
    /**
     * 最大数值
     */
    private double mMaxNum = 10_000D;
    /**
     * 保留小数位数
     */
    private int mDot = 0;

    /**
     * 是否需要没有焦点的时候也回调textWatch，如没有焦点的时候 setText是不走TextWatch方法的
     */
    private boolean mIsHasAllTextWatch = false;

    public MaxEditTextView(Context context) {
        super(context);
    }

    public MaxEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public interface ICall {
        void call(String s);
    }

    public interface IExceedMaxEditNumCall extends ICall {
        void isExceedMaxEditNum();
    }

    private ICall mICall;

    public interface FocusChangeListenerCallback {
        void onFocusChange(View v, boolean hasFocus);
    }

    private FocusChangeListenerCallback mFocusChangeListenerCallback;

    /**
     * 设置是否显示日志
     */
    public void setDebug(boolean isDebug) {
        this.mIsDebug = isDebug;
    }

    /**
     * 设置是否显示小数后的0
     */
    public void setShowEnd0(boolean showEnd0) {
        mIsShowEnd0 = showEnd0;
    }

    /**
     * 设置最大数值，并返回数值,如果最大值为整数则不可以输小数
     *
     * @param dot    保留小数位数
     * @param maxNum 输入最大值
     * @param iCall  回调输出值
     */
    public void setModule(@IntRange(from = 0) int dot, double maxNum, ICall iCall) {
        setModule(dot, maxNum, false, iCall);
    }

    /**
     * 设置保留位数，并返回数值
     *
     * @param dot               保留小数位数
     * @param maxNum            输入最大值
     * @param isHasAllTextWatch 是否需要没有焦点的时候也回调textWatch，false:如没有焦点的时候 setText是不走TextWatch方法的
     * @param iCall             回调输出值
     */
    public void setModule(@IntRange(from = 0) int dot, double maxNum, boolean isHasAllTextWatch, ICall iCall) {
        mDot = dot;
        mMaxNum = maxNum;
        mICall = iCall;
        mIsHasAllTextWatch = isHasAllTextWatch;
        if (mIsDebug) {
            Log.d(TAG, "最大值--->> " + mMaxNum + " 保留小数：" + mDot);
        }
        initEt();
    }

    /**
     * 更改最大值和保留位数
     *
     * @param dot    保留小数位数
     * @param maxNum 输入最大值
     */
    public void changMaxNumber(@IntRange(from = 0) int dot, double maxNum) {
        mDot = dot;
        mMaxNum = maxNum;
        if (mIsDebug) {
            Log.d(TAG, "改变后的最大值--->> " + mMaxNum + " 保留小数：" + mDot);
        }
        Editable text = getText();
        if (text == null) {
            return;
        }
        String nowTextStr = text.toString();
        double nowD;
        try {
            nowD = Double.parseDouble(nowTextStr);
        } catch (NumberFormatException e) {
            nowD = 0;
        }
        if (mIsHasAllTextWatch) {
            setText(nowTextStr);
        } else {
            if (nowD > mMaxNum) {
                String nowMaxStr = getDoubleDecimalFormat(mMaxNum + "", mDot, mIsShowEnd0);
                setText(nowMaxStr);
                if (isFocused()) {
                    setSelection(getText().toString().length());
                }
                mICall.call(nowMaxStr);
            }
        }
    }

    /**
     * 添加焦点监听，外面调用系统的不起作用
     */
    public void setOnFocusChangeListener(FocusChangeListenerCallback focusChangeListenerCallback) {
        mFocusChangeListenerCallback = focusChangeListenerCallback;
    }

    private void initEt() {
        /*
          改变前数据
         */
        StringBuilder mOldSb = new StringBuilder();
        final TextWatcher etTextWatch = getEtTextWatch(mOldSb);

        if (mIsHasAllTextWatch) {
            removeTextChangedListener(etTextWatch);
            addTextChangedListener(etTextWatch);
            setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (mFocusChangeListenerCallback != null) {
                        mFocusChangeListenerCallback.onFocusChange(v, hasFocus);
                    }
                }
            });
        } else {
            setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (mFocusChangeListenerCallback != null) {
                        mFocusChangeListenerCallback.onFocusChange(v, hasFocus);
                    }
                    if (hasFocus) {
                        addTextChangedListener(etTextWatch);
                    } else {
                        removeTextChangedListener(etTextWatch);
                    }
                }
            });
        }
    }

    @NonNull
    private TextWatcher getEtTextWatch(final StringBuilder oldNumSb) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldNumSb.setLength(0);
                oldNumSb.append(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsDebug) {
                    Log.d(TAG, "--->>" + s);
                }
                if (mICall == null) {
                    //没有回调就直接返回
                    return;
                }

                String ss = s.toString();

                //格式化数前0
                if (ss.matches("^[-]*((0+\\d+)|(0\\d\\.\\d*))$")) {
                    removeTextChangedListener(this);
                    String newSS = getDoubleDecimalFormat(ss, mDot, false);
                    //判断输入的newSS是否超过最大值如果超过设置为最大值
                    if (isExceedMaxEditNum(Double.parseDouble(newSS), mMaxNum)) {//判断是否超过最大值
                        newSS = getDoubleDecimalFormat(mMaxNum + "", mDot, mIsShowEnd0);
                    }
                    oldNumSb.setLength(0);
                    oldNumSb.append(newSS);
                    setText(newSS);
                    setSelection(newSS.length());
                    addTextChangedListener(this);
                    setCallbackData(newSS);
                    return;
                }

                //多个-判断或数字后添加-
                if (ss.matches("(.+-.*)+")) {
                    removeTextChangedListener(this);
                    String toString = oldNumSb.toString();
                    setText(toString);
                    addTextChangedListener(this);
                    setSelection(toString.length());

                    //设置回调数据
                    setCallbackData(toString);
                    return;
                }

                boolean isDot = mDot != 0;//是否是小数
                if (!isDot) {
                    //整数有小数点
                    if (ss.matches(".*\\..*")) {
                        removeTextChangedListener(this);
                        //有多个小数点返回原数值
                        if (!ss.matches(".*\\..*\\..*")) {
                            //如果只有一个点截取点前数据
                            String ssFormat = getDoubleDecimalFormat(ss, 0, mIsShowEnd0);
                            oldNumSb.setLength(0);
                            if (isExceedMaxEditNum(Double.parseDouble(ssFormat), mMaxNum)) {//判断是否超过最大值
                                oldNumSb.append((long) mMaxNum);
                            } else {
                                oldNumSb.append(Long.parseLong(ssFormat));
                            }
                        }
                        String toString = oldNumSb.toString();
                        setText(toString);
                        addTextChangedListener(this);
                        setSelection(toString.length());

                        //设置回调数据
                        setCallbackData(toString);
                        return;
                    }
                } else {
                    /*
                        如果是小数
                     */
                    if (ss.contains(".")) {
                        //如果直接输入.变成0.样式
                        if (ss.matches("\\.")) {
                            removeTextChangedListener(this);
                            setText("0.");
                            addTextChangedListener(this);
                            setSelection(2);

                            //设置回调数据
                            setCallbackData("0.0");
                            return;
                        }

                        //屏蔽多个.
                        if (ss.matches(".*\\..*\\..*")) {
                            removeTextChangedListener(this);
                            String toString = oldNumSb.toString();
                            setText(toString);
                            addTextChangedListener(this);
                            setSelection(toString.length());

                            //设置回调数据
                            setCallbackData(toString);
                            return;
                        }

                        //判断是否超过小数点位数
                        int diLength = ss.indexOf(DOT_STR);//输入框小数点位置
                        int maxDiLength = mDot + 1;//最大数的小数点位置
                        if (ss.length() - diLength > maxDiLength) {
                            removeTextChangedListener(this);
                            String toString = oldNumSb.toString();
                            setText(toString);
                            addTextChangedListener(this);
                            setSelection(toString.length());

                            //设置回调数据
                            setCallbackData(toString);
                            return;
                        }
                    }
                }

                String newS = getDoubleDecimalFormat(ss, mDot, false);
                if (isExceedMaxEditNum(Double.parseDouble(newS), mMaxNum)) {//判断是否超过最大值
                    if (mIsDebug) {
                        Log.d(TAG, "isExceedMaxEditNum--->>超过最大值");
                    }
                    if (mIsDebug) {
                        Log.d(TAG, "isExceedMaxEditNum--->>" + ((long) mMaxNum));
                    }
                    String nowMaxStr = getDoubleDecimalFormat(mMaxNum + "", mDot, mIsShowEnd0);
                    removeTextChangedListener(this);
                    setText(nowMaxStr);
                    addTextChangedListener(this);
                    setSelection(nowMaxStr.length());

                    mICall.call(nowMaxStr);
                    return;
                }

                //数据都没问题走
                //设置回调数据
                setCallbackData(ss);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    /**
     * 设置回调数据
     */
    private void setCallbackData(String ss) {
        String newSS = getDoubleDecimalFormat(ss, mDot, mIsShowEnd0);
        if (mIsDebug) {
            Log.d(TAG, "返回数据--->" + newSS);
        }
        mICall.call(newSS);
    }

    /**
     * 判断斤两菜当前值是否超过设置的最大值
     *
     * @param currentPrice 当前值
     * @param maxNum       设置的最大值
     */
    private boolean isExceedMaxEditNum(double currentPrice, double maxNum) {
//        boolean isDot = mDot != 0;//是否是小数
//        Log.d(TAG, "isExceedMaxEditNum: mMaxNum=" + mMaxNum);
//        Log.d(TAG, "isExceedMaxEditNum: (long) mMaxNum=" + ((long) mMaxNum));
        boolean isExceedMaxEditNum = currentPrice > maxNum;
        if (isExceedMaxEditNum && mICall != null && mICall instanceof IExceedMaxEditNumCall) {
            ((IExceedMaxEditNumCall) mICall).isExceedMaxEditNum();
        }
        return isExceedMaxEditNum;
    }

    /**
     * 获取格式化的小数
     */
    private String getDoubleDecimalFormat(String numberStr, int dot, boolean isShowEnd0) {
        if (numberStr.equals(".")) {
            numberStr = "0.0";
        }
        double number = 0;
        try {
            if (!numberStr.isEmpty()) {
                number = Double.parseDouble(numberStr);
            }
        } catch (NumberFormatException e) {
            if (mIsDebug) {
                Log.w(TAG, e.getMessage());
            }
        }

        return formatDouble(number, dot, isShowEnd0);
    }

    /**
     * 格式化小数
     */
    private String formatDouble(double number, int dot, boolean isShowEnd0) {
        StringBuilder patternSb = new StringBuilder("0");
        for (int i = 0; i < dot; i++) {
            if (i == 0) {
                patternSb.append(".");
            }
            if (isShowEnd0) {
                patternSb.append("0");
            } else {
                patternSb.append("#");
            }
        }
        DecimalFormat format = new DecimalFormat(patternSb.toString());
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(number);
    }
}