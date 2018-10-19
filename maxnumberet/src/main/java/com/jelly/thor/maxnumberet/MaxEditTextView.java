package com.jelly.thor.maxnumberet;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.DecimalFormat;

/**
 * 类描述： 必须调用这个方法setModule，自定义保留位数edittext<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2017/8/31 17:38 <br/>
 */
public class MaxEditTextView extends androidx.appcompat.widget.AppCompatEditText {
    private static final String TAG = "MaxEditTextView";

    private double MAX_NUM0 = 9999D;
    private double MAX_NUM1 = 9999.9D;
    private double MAX_NUM2 = 9999.99D;
    private double MAX_NUM3 = 9999.999D;

    /**
     * 最大数值
     */
    private double mMaxNum = MAX_NUM3;

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
        void Call(String s);
    }

    private ICall mICall;


    /**
     * 设置保留位数，并返回数值
     *
     * @param module (0),(1),(2),(3)(保留0--3位小数，数值小于一万)
     * @param iCall  回调输出值
     */
    public void setModule(int module, ICall iCall) {
        setModule(module, false, iCall);
    }

    /**
     * 设置保留位数，并返回数值
     *
     * @param module            (0),(1),(2),(3)(保留0--3位小数，数值小于一万)
     * @param isHasAllTextWatch 是否需要没有焦点的时候也回调textWatch，如没有焦点的时候 setText是不走TextWatch方法的
     * @param iCall             回调输出值
     */
    public void setModule(int module, boolean isHasAllTextWatch, ICall iCall) {
        switch (module) {
            case 3://保留三位小数
                mMaxNum = MAX_NUM3;
                break;
            case 2:
                mMaxNum = MAX_NUM2;
                break;
            case 1:
                mMaxNum = MAX_NUM1;
                break;
            case 0:
                mMaxNum = MAX_NUM0;
                break;
            default:
                mMaxNum = MAX_NUM3;
                break;
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "最大值--保留位数->> " + module);
        }
        mICall = iCall;
        mIsHasAllTextWatch = isHasAllTextWatch;
        initEt();
    }


    /**
     * 设置最大数值，并返回数值,如果最大值为整数则不可以输小数
     *
     * @param maxNum 输入最大值
     * @param iCall  回调输出值
     */
    public void setModule(double maxNum, ICall iCall) {
        setModule(maxNum, false, iCall);
    }

    /**
     * 设置保留位数，并返回数值
     *
     * @param maxNum            输入最大值
     * @param isHasAllTextWatch 是否需要没有焦点的时候也回调textWatch，如没有焦点的时候 setText是不走TextWatch方法的
     * @param iCall             回调输出值
     */
    public void setModule(double maxNum, boolean isHasAllTextWatch, ICall iCall) {
        mMaxNum = maxNum;
        mICall = iCall;
        mIsHasAllTextWatch = isHasAllTextWatch;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "最大值--->> " + mMaxNum);
        }
        initEt();
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
        } else {
            setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        addTextChangedListener(etTextWatch);
                    } else {
                        removeTextChangedListener(etTextWatch);
                    }
                }
            });
        }
    }

    /**
     * 判断斤两菜当前值是否超过设置的最大值
     *
     * @param currentPrice 当前值
     * @param maxNum       设置的最大值
     */
    private boolean isExceedMaxEditNum(TextWatcher textWatcher, double currentPrice, double maxNum) {
        boolean isDot = (mMaxNum - (long) mMaxNum) != 0;//是否是小数
//        Log.d(TAG, "isExceedMaxEditNum: mMaxNum=" + mMaxNum);
//        Log.d(TAG, "isExceedMaxEditNum: (long) mMaxNum=" + ((long) mMaxNum));
        if (currentPrice > maxNum) {
            if (isDot) {
                removeTextChangedListener(textWatcher);
                setText(String.valueOf(maxNum));
                addTextChangedListener(textWatcher);
                setSelection(String.valueOf(maxNum).length());
            } else {
                removeTextChangedListener(textWatcher);
                setText(String.valueOf((long) maxNum));
                addTextChangedListener(textWatcher);
                setSelection(String.valueOf((long) maxNum).length());
            }
            return true;
        }
        return false;
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
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "--->>" + s);
                }
                if (mICall == null) {
                    //没有回调就直接返回
                    return;
                }

                boolean isDot = (mMaxNum - (long) mMaxNum) != 0;//是否是小数

                //数前多0判断
                if (s.toString().matches("[-]*00.*")) {
                    removeTextChangedListener(this);
                    setText(oldNumSb.toString());
                    addTextChangedListener(this);
                    setSelection(oldNumSb.toString().length());

                    if (oldNumSb.toString().startsWith(".")) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                        }
                        mICall.Call("0" + oldNumSb.toString());
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                        }
                        mICall.Call(oldNumSb.toString());
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
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                            }
                            mICall.Call("0" + oldNumSb.toString());
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                            }
                            mICall.Call(oldNumSb.toString());
                        }
                        return;
                    }
                }

                String ss = s.toString();
                if (!isDot) {
                    /*
                        整数
                        数前0判断 | 小数点删除
                     */
                    if (ss.matches("(0.+)|(.*\\..*)")) {
                        removeTextChangedListener(this);
                        String ssFormat = getDoubleDecimalFormat(ss);

                        //如果当前整数在设置的时候就有小数点并且小数点后都为0
                        if (ssFormat.matches("[^.]+")) {
                            oldNumSb.setLength(0);
                            if (isExceedMaxEditNum(this, Double.parseDouble(ssFormat), mMaxNum)) {//判断是否超过最大值
                                oldNumSb.append(((long) mMaxNum) + "");
                            } else {
                                oldNumSb.append(ssFormat);
                            }
                        } else if (oldNumSb.toString().matches(".*\\..*")) {
                            //如果原来就含有小数直接设置为整数
                            String oldStr = oldNumSb.toString();
                            oldNumSb.setLength(0);
                            int dotIndex = oldStr.indexOf(".");
                            if (dotIndex == 0) {
                                oldNumSb.append("1");
                            } else {
                                oldNumSb.append(oldStr.substring(0, dotIndex));
                            }
                        }
                        setText(oldNumSb.toString());
                        addTextChangedListener(this);
                        setSelection(oldNumSb.toString().length());

                        if (oldNumSb.toString().startsWith(".")) {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                            }
                            mICall.Call("0" + oldNumSb.toString());
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                            }
                            mICall.Call(oldNumSb.toString());
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

                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "返回数据--->0.");
                            }
                            mICall.Call("0.");
                            return;
                        }

                        //屏蔽多个.
                        if (s.toString().matches("((.*\\.{2}.*)*)|((.*\\..*){2,})")) {
                            removeTextChangedListener(this);
                            setText(oldNumSb.toString());
                            addTextChangedListener(this);
                            setSelection(oldNumSb.toString().length());

                            if (oldNumSb.toString().startsWith(".")) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                                }
                                mICall.Call("0" + oldNumSb.toString());
                            } else {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                                }
                                mICall.Call(oldNumSb.toString());
                            }
                            return;
                        }

                        int diLength = ss.indexOf(".");//输入框小数点位置
                        int maxDiLength = String.valueOf(mMaxNum).indexOf(".");//最大数的小数点位置
                        if (s.toString().length() - diLength > String.valueOf(mMaxNum).length() - maxDiLength) {
                            removeTextChangedListener(this);
                            setText(oldNumSb.toString());
                            addTextChangedListener(this);
                            setSelection(oldNumSb.toString().length());

                            if (oldNumSb.toString().startsWith(".")) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "返回数据--->0." + oldNumSb.toString());
                                }
                                mICall.Call("0" + oldNumSb.toString());
                            } else {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "返回数据--->" + oldNumSb.toString());
                                }
                                mICall.Call(oldNumSb.toString());
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
                    e.printStackTrace();
                }
                if (isExceedMaxEditNum(this, jinPrice, mMaxNum)) {//判断是否超过最大值
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "isExceedMaxEditNum--->>超过最大值");
                    }
                    if (!isDot) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "isExceedMaxEditNum--->>" + ((long) mMaxNum));
                        }
                        mICall.Call(((long) mMaxNum) + "");
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "isExceedMaxEditNum--->>" + mMaxNum);
                        }
                        mICall.Call(mMaxNum + "");
                    }
                    return;
                }
                if (ss.startsWith(".")) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "返回数据--->0." + ss);
                    }
                    mICall.Call("0" + ss);
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "返回数据--->" + ss);
                    }
                    mICall.Call(ss);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private String getDoubleDecimalFormat(final String number) {
        if (number.equals(".")) {
            return number;
        }
        double doule = 0;
        try {
            if (!number.equals("")) {
                doule = Double.valueOf(number);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        DecimalFormat format = new DecimalFormat("#.#####");
        return format.format(doule);
    }
}