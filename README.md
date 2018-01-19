# MaxNumEditText
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
	        compile 'com.github.Thor-jelly:MaxNumEditText:v1.0'
	}
    ```