package com.dongdongwu.maxnumet;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 类描述： 必须调用这个方法setModule，自定义保留位数edittext<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2017/8/31 17:38 <br/>
 */
public class MaxEditTextView extends android.support.v7.widget.AppCompatEditText {
    private double MAX_NUM0 = 9999D;
    private double MAX_NUM1 = 9999.9D;
    private double MAX_NUM2 = 9999.99D;
    private double MAX_NUM3 = 9999.999D;

    /**
     * 最大数值
     */
    private double mMaxNum = MAX_NUM3;

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
     * @param module 0,1,2,3(保留0--3位小数，数值小于一万)
     * @param iCall  回调输出值
     */
    public void setModule(int module, ICall iCall) {
//        CommonUtil.debug("123===", "最大值--保留位数->> "+ module);
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
        }
//        CommonUtil.debug("123===", "最大值--111->> "+ mMaxNum);
        mICall = iCall;
    }

    /**
     * 设置最大数值，并返回数值,如果最大值为整数则不可以输小数
     *
     * @param maxNum 输入最大值
     * @param iCall  回调输出值
     */
    public void setModule(double maxNum, ICall iCall) {
        mMaxNum = maxNum;
        mICall = iCall;
//        CommonUtil.debug("123===", "最大值--->> "+ mMaxNum);
    }

    @Override
    protected void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {
        if (mICall != null) {
            boolean isDot = (mMaxNum - (int) mMaxNum) != 0;//是否是小数
            //CommonUtil.debug("123===", "0---> " + isDot);
            if (!isDot) {
                if (s != null && s.toString().contains(".")) {
                    int diLength = s.toString().indexOf(".");
                    setText(s.subSequence(0, diLength));
                    setSelection(diLength);
                    return;//为了输出超过保留小数位，更改到超出小数点前的数据下面不需要重新调用
                }
            } else {//小数
                if (s != null && s.toString().contains(".")) {
                    int diLength = s.toString().indexOf(".");//输入框小数点位置
//                    CommonUtil.debug("123===", "小数点位置= "+diLength);
                    if (diLength == 0) {
                        //如果直接输入. 转换为 "0." 显示样式
                        setText("0.");
                        setSelection(2);
                        return;
                    }
                    int maxDiLength = String.valueOf(mMaxNum).indexOf(".");//最大数的小数点位置
                    if (s.toString().length() - diLength > String.valueOf(mMaxNum).length() - maxDiLength) {
                        //设置删除的无用小数位，用原来的长度，减去(原来保留的小数位 减去 最大值保留的小数位)
                        //setText(s.subSequence(0, (s.toString().length() - ((s.toString().length() - diLength) - (String.valueOf(mMaxNum).length() - maxDiLength)))));
                        setText(s.subSequence(0, (diLength + String.valueOf(mMaxNum).length() - maxDiLength)));
                        //因为这次得到的s还是更改前的s值，所以设置光标位置还是用原来截取到位置
                        setSelection((diLength + String.valueOf(mMaxNum).length() - maxDiLength));
                        return;//为了输出超过保留小数位，更改到超出小数点前的数据下面不需要重新调用
                    }
                }
            }
//        if (s.toString().trim().equals("")) {
//            return;
//        }
            if (s.toString().equals("00")) {
                setText(s.subSequence(0, 1));
                setSelection(1);
                return;
            }
            try {
                double jinPrice = Double.parseDouble(s.toString());
                if (isExceedMaxEditNum(jinPrice, mMaxNum)) {//判断是否超过最大值
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            mICall.Call(s.toString());
        } else {
            Log.e("MaxEditText", "没有调用setModule方法，请调用该方法！");
        }
        //super.onTextChanged(s, start, lengthBefore, lengthAfter);
    }


    /**
     * 判断斤两菜当前值是否超过设置的最大值
     *
     * @param currentPrice 当前值
     * @param maxNum       设置的最大值
     */
    private boolean isExceedMaxEditNum(double currentPrice, double maxNum) {
        boolean isDot = (mMaxNum - (int) mMaxNum) != 0;//是否是小数
        if (currentPrice > maxNum) {
            if (isDot) {
                setText(String.valueOf(maxNum));
                setSelection(String.valueOf(maxNum).length());
            } else {
                setText(String.valueOf((int) maxNum));
                setSelection(String.valueOf((int) maxNum).length());
            }
            return true;
        }
        return false;
    }
}