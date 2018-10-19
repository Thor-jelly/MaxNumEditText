# MaxNumEditText
> 暂时只能在xml添加`android:inputType="numberDecimal"`属性使其输入数字，因为我在其构造方法中添加`setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);`没有效果!有知道怎么改的可以发我QQ邮箱：745661590@qq.com

[![GitHub release](https://img.shields.io/badge/release-v1.0.7-green.svg)](https://github.com/Thor-jelly/MaxNumEditText/releases)

```
     /**
     * 设置保留位数，并返回数值
     *
     * @param module 0,1,2,3(保留0--3位小数，数值小于一万)
     * @param iCall  回调输出值
     */
    public void setModule(int module, ICall iCall)
    
    /**
     * 设置最大数值，并返回数值,如果最大值为整数则不可以输小数
     *
     * @param maxNum 输入最大值
     * @param iCall  回调输出值
     */
    public void setModule(double maxNum, ICall iCall)
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
	        compile 'com.github.Thor-jelly:MaxNumEditText:v最新版本'
	}
    ```
