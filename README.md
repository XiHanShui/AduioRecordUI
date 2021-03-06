# 录音效果图：

![record.gif](https://user-gold-cdn.xitu.io/2017/12/18/1606785131add263?w=364&h=648&f=gif&s=2411320)

自定义属性列表：

```
<!--录音-->
    <declare-styleable name="AudioRecord">
        <!--录音时长 单位分钟-->
        <attr name="recordTimeInMinutes" format="integer" />
        <!--录音采样频率 每秒钟采样个数-->
        <attr name="recordSamplingFrequency" format="integer" />
        <!--是否显示刻度尺上的文字-->
        <attr name="showRuleText" format="boolean" />
        <!--一格大刻度多少格小刻度-->
        <attr name="intervalCount" format="integer" />
        <!--刻度间隔-->
        <attr name="scaleIntervalLength" format="dimension" />
        <!--尺子 小刻度 高-->
        <attr name="smallScaleStrokeLength" format="dimension" />
        <!--尺子 小刻度 宽-->
        <attr name="smallScaleStrokeWidth" format="dimension" />
        <!--尺子 大刻度 高-->
        <attr name="bigScaleStrokeLength" format="dimension" />
        <!--尺子 大刻度 宽-->
        <attr name="bigScaleStrokeWidth" format="dimension" />
        <!--尺子 底部横线 颜色-->
        <attr name="ruleHorizontalLineColor" format="color" />
        <!--尺子 底部横线 线宽-->
        <attr name="ruleHorizontalLineStrokeWidth" format="dimension" />
        <!--尺子 底部横线 线高度-->
        <attr name="ruleHorizontalLineHeight" format="dimension" />
        <!--尺子 竖线颜色-->
        <attr name="ruleVerticalLineColor" format="color" />
        <!--尺子上文字颜色-->
        <attr name="ruleTextColor" format="color" />
        <!--尺子上文字大小-->
        <attr name="ruleTextSize" format="dimension" />
        <!--水平线 颜色-->
        <attr name="middleHorizontalLineColor" format="color" />
        <!--水平线 stroke width-->
        <attr name="middleHorizontalLineStrokeWidth" format="dimension" />
        <!--垂直线 颜色-->
        <attr name="middleVerticalLineColor" format="color" />
        <!--垂直线 stroke width-->
        <attr name="middleVerticalLineStrokeWidth" format="dimension" />
        <!--垂直线 两个圆的圆心 半径-->
        <attr name="middleCircleRadius" format="dimension" />
        <!--声波 矩形 颜色-->
        <attr name="rectColor" format="color" />
        <!--声波 矩形 颜色-->
        <attr name="rectInvertColor" format="color" />
        <!--声波矩形 间距-->
        <attr name="rectGap" format="dimension" />
        <!--声波矩形 距离顶部垂直间距-->
        <attr name="rectMarginTop" format="dimension" />
        <!--底部文字颜色-->
        <attr name="bottomTextColor" format="color" />
        <!--底部文字大小-->
        <attr name="bottomTextSize" format="dimension" />
        <!--底部 文字区域 背景颜色-->
        <attr name="bottomRectColor" format="color" />

    </declare-styleable>
 ```
  
# 播放录音效果图：

![](https://user-gold-cdn.xitu.io/2017/12/18/160681be9e9fcfb6?w=364&h=639&f=gif&s=831378)

自定义属性列表：

```
<!--播放音频-->
    <declare-styleable name="PlayAudio">
        <!--声音数据采集的频率 每秒钟采集个数-->
        <attr name="p_audioSourceFrequency" format="integer" />
        <!--中心垂直线的宽-->
        <attr name="p_circleMarginTop" format="dimension" />
        <!--波形 距离圆点间距-->
        <attr name="p_rectMarginTop" format="dimension" />
        <!--圆形半径-->
        <attr name="p_circleRadius" format="dimension" />
        <!--中心垂直线的宽-->
        <attr name="p_centerLineWidth" format="dimension" />
        <!--没有扫描的矩形颜色-->
        <attr name="p_unSwipeColor" format="color" />
        <!--扫描过的矩形颜色-->
        <attr name="p_swipedColor" format="color" />
        <!--波形 宽-->
        <attr name="p_rectWidth" format="dimension" />
        <!--波形 间距-->
        <attr name="p_rectGap" format="dimension" />

    </declare-styleable>
```
