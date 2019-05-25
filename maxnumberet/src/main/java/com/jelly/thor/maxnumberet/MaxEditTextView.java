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

                //数前多0判断
                if (s.toString().matches("[-]*00.*")) {
                    removeTextChangedListener(this);
                    setText(oldNumSb.toString());
                    addTextChangedListener(this);
                    setSelection(oldNumSb.toString().length());

                    if (oldNumSb.toString().startsWith(".")) {
                        //如果原来是.xxx这种数字返回数据变为0.xxx这种形式
                        if (mIsDebug) {
                            Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                        }
                        mICall.call("0" + oldNumSb.toString());
                    } else {
                        if (mIsDebug) {
                            Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                        }
                        mICall.call(oldNumSb.toString());
                    }
                    return;
                }

                //多个-判断或数字后添加-
                if (s.toString().contains("-")) {
                    if (s.toString().matches("(.+-.*)+")) {
                        removeTextChangedListener(this);
                        setText(oldNumSb.toString());
                        addTextChangedListener(this);
                        setSelection(oldNumSb.toString().length());

                        if (oldNumSb.toString().startsWith(".")) {
                            if (mIsDebug) {
                                Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                            }
                            mICall.call("0" + oldNumSb.toString());
                        } else {
                            if (mIsDebug) {
                                Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                            }
                            mICall.call(oldNumSb.toString());
                        }
                        return;
                    }
                }

                String ss = s.toString();
                boolean isDot = mDot != 0;//是否是小数
                if (!isDot) {
                    /*
                        整数
                        数后.000判断 | 旧数小数点删除
                     */
                    if (ss.matches("(.*\\..*)")) {
                        removeTextChangedListener(this);
                        String ssFormat = getDoubleDecimalFormat(ss);

                        if (ssFormat.matches("(\\d\\.0*$)*")) {
                            //如果当前整数在设置的时候就有小数点并且小数点后都为0
                            ssFormat = ssFormat + "0";
                            oldNumSb.setLength(0);
                            if (isExceedMaxEditNum(this, Double.parseDouble(ssFormat), mMaxNum)) {//判断是否超过最大值
                                oldNumSb.append((long) mMaxNum);
                            } else {
                                oldNumSb.append(Long.parseLong(ssFormat));
                            }
                        } else if (oldNumSb.toString().matches(".*\\..*")) {
                            //如果原来就含有小数直接设置为整数
                            String oldStr = oldNumSb.toString();
                            oldNumSb.setLength(0);
                            int dotIndex = oldStr.indexOf(DOT_STR);
                            if (dotIndex <= 0) {
                                oldNumSb.append("");
                            } else {
                                oldNumSb.append(oldStr.substring(0, dotIndex));
                            }
                        }
                        setText(oldNumSb.toString());
                        addTextChangedListener(this);
                        setSelection(oldNumSb.toString().length());

                        if (oldNumSb.toString().startsWith(DOT_STR)) {
                            if (mIsDebug) {
                                Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                            }
                            mICall.call("0" + oldNumSb.toString());
                        } else {
                            if (mIsDebug) {
                                Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                            }
                            mICall.call(oldNumSb.toString());
                        }
                        return;
                    }
                } else {
                    /*
                        如果是小数
                     */
                    if (ss.contains(".")) {
                        //如果直接输入.变成0.样式
                        if (s.toString().matches("\\.")) {
                            removeTextChangedListener(this);
                            setText("0.");
                            addTextChangedListener(this);
                            setSelection(2);

                            if (mIsDebug) {
                                Log.d(TAG, "返回数据--->0.");
                            }
                            mICall.call("0.");
                            return;
                        }

                        //屏蔽多个.
                        if (s.toString().matches("((\\.\\d+\\.)+)|((\\.){2,})")) {
                            removeTextChangedListener(this);
                            setText(oldNumSb.toString());
                            addTextChangedListener(this);
                            setSelection(oldNumSb.toString().length());

                            if (oldNumSb.toString().startsWith(DOT_STR)) {
                                if (mIsDebug) {
                                    Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                                }
                                mICall.call("0" + oldNumSb.toString());
                            } else {
                                if (mIsDebug) {
                                    Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                                }
                                mICall.call(oldNumSb.toString());
                            }
                            return;
                        }

                        int diLength = ss.indexOf(DOT_STR);//输入框小数点位置
                        int maxDiLength = mDot + 1;//最大数的小数点位置
                        if (s.toString().length() - diLength > maxDiLength) {
                            removeTextChangedListener(this);
                            setText(oldNumSb.toString());
                            addTextChangedListener(this);
                            setSelection(oldNumSb.toString().length());

                            if (oldNumSb.toString().startsWith(".")) {
                                if (mIsDebug) {
                                    Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                                }
                                mICall.call("0" + oldNumSb.toString());
                            } else {
                                if (mIsDebug) {
                                    Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                                }
                                mICall.call(oldNumSb.toString());
                            }
                            return;
                        }
                    }
                }
                double jinPrice = 0D;
                try {
                    if (!ss.equals("")) {
                        jinPrice = Double.parseDouble(ss);
                    }
                } catch (NumberFormatException e) {
                    if (mIsDebug) {
                        Log.w(TAG, e.getMessage());
                    }
                }
                if (isExceedMaxEditNum(this, jinPrice, mMaxNum)) {//判断是否超过最大值
                    if (mIsDebug) {
                        Log.d(TAG, "isExceedMaxEditNum--->>超过最大值");
                    }
                    if (mIsDebug) {
                        Log.d(TAG, "isExceedMaxEditNum--->>" + ((long) mMaxNum));
                    }
                    String nowMaxStr = formatDouble(mMaxNum);
                    removeTextChangedListener(this);
                    setText(nowMaxStr);
                    addTextChangedListener(this);
                    setSelection(nowMaxStr.length());

                    mICall.call(nowMaxStr);
                    return;
                }

                //数据都没问题走
                if (ss.startsWith(".")) {
                    if (mIsDebug) {
                        Log.d(TAG, "返回数据--->0." + ss);
                    }
                    mICall.call("0" + ss);
                } else {
                    if (mIsDebug) {
                        Log.d(TAG, "返回数据--->" + ss);
                    }
                    mICall.call(ss);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    /**
     * 判断斤两菜当前值是否超过设置的最大值
     *
     * @param currentPrice 当前值
     * @param maxNum       设置的最大值
     */
    private boolean isExceedMaxEditNum(TextWatcher textWatcher, double currentPrice, double maxNum) {
//        boolean isDot = mDot != 0;//是否是小数
//        Log.d(TAG, "isExceedMaxEditNum: mMaxNum=" + mMaxNum);
//        Log.d(TAG, "isExceedMaxEditNum: (long) mMaxNum=" + ((long) mMaxNum));
        if (currentPrice > maxNum) {
//            removeTextChangedListener(textWatcher);
//            String maxStr = formatDouble(maxNum);
//            setText(maxStr);
//            addTextChangedListener(textWatcher);
//            setSelection(maxStr.length());
            return true;
        }
        return false;
    }

    /**
     * 获取格式化的小数
     */
    private String getDoubleDecimalFormat(final String numberStr) {
        if (numberStr.equals(".")) {
            return numberStr;
        }
        double number = 0;
        try {
            if (!numberStr.isEmpty()) {
                number = Double.valueOf(numberStr);
            }
        } catch (NumberFormatException e) {
            if (mIsDebug) {
                Log.w(TAG, e.getMessage());
            }
        }
        return formatDouble(number);
    }

    /**
     * 格式化小数
     */
    private String formatDouble(double number) {
        String pattern = "#.#####";
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(number);
    }
}