# MaxNumEditText
> 暂时只能在xml添加`android:inputType="numberDecimal"`属性使其输入数字，因为我在其构造方法中添加`setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);`没有效果!有知道怎么改的可以发我QQ邮箱：745661590@qq.com

[![GitHub release](https://img.shields.io/badge/release-1.1.4-green.svg)](https://github.com/Thor-jelly/MaxNumEditText/releases)

```
     /**
      * 设置是否显示日志
      */
     public void setDebug(boolean isDebug) 
     
     /**
      * 设置最大数值，并返回数值,如果最大值为整数则不可以输小数
      *
      * @param dot    保留小数位数
      * @param maxNum 输入最大值
      * @param iCall  回调输出值
      */
     public void setModule(@IntRange(from = 0) int dot, double maxNum, ICall iCall) 
     
     /**
      * 设置保留位数，并返回数值
      *
      * @param dot               保留小数位数
      * @param maxNum            输入最大值
      * @param isHasAllTextWatch 是否需要没有焦点的时候也回调textWatch，false:如没有焦点的时候 setText是不走TextWatch方法的
      * @param iCall             回调输出值
      */
     public void setModule(@IntRange(from = 0) int dot, double maxNum, boolean isHasAllTextWatch, ICall iCall) 
     
     /**
      * 更改最大值和保留位数
      *
      * @param dot    保留小数位数
      * @param maxNum 输入最大值
      */
     public void changMaxNumber(@IntRange(from = 0) int dot, double maxNum)
     
     /**
      * 添加焦点监听，外面调用系统的不起作用
      */
     public void setOnFocusChangeListener(FocusChangeListenerCallback focusChangeListenerCallback)
```

#To get a Git project into your build
1. Add it in your root build.gradle at the end of repositories:  

    ```
        allprojects {
        		repositories {
        			...
        			maven { url 'https://jitpack.io' }
        		}
        	}
    ```

2. Add the dependency

    ```
        dependencies {
	        compile 'com.github.Thor-jelly:MaxNumEditText:最新版本'
	}
    ```
