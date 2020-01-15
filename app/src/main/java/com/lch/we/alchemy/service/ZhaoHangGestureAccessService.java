package com.lch.we.alchemy.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.lch.we.alchemy.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class ZhaoHangGestureAccessService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener  {
    private static final String TAG = "ZhaoHangAccessService";

    //活动页面
    private static final String ZHAOHANG_GENERAL_ACTIVITY = "com.project.foundation.cmbView.cmbwebviewv2.cmbW1IZ35";
    //当前活动页面
    private String currentActivityName = ZHAOHANG_GENERAL_ACTIVITY;

    private static final String ZHAOHANG_ACTIVITY_ADDMIAO = "详情";

    private boolean step0 = false;
    private boolean step1 = false;
    private boolean step2 = false;
    private boolean step3 = false;
    private boolean step31 = false;
    private boolean step32 = false;
    private boolean step4 = false;

    /**
     * AccessibilityEvent
     *
     * @param event 事件
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        LogUtil.d( "event=" + event);
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                setCurrentActivityName(event);
                //if(currentActivityName.equals("com.cmbchina.ccd.pluto.cmbActivity/.cmbLKUW8I")){
                    step0 = false;
                    step1 = false;
                    step2 = false;
                    step3 = false;
                        step31 = false;
                        step32= false;
                    step4 = false;
                //}
                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                if(currentActivityName.equals("com.cmbchina.ccd.pluto.cmbActivity/.cmbLKUW8I")){
//                    if(!step0){
//                        useGestureClick(null,937,1748,0);
//                        step0 = true;
//                    }
                }else{
                    //第一轮
                    if(!step1){
                        useGestureClick(null,104,1909,0);
                        Toast.makeText(this, "step1", Toast.LENGTH_LONG).show();
                        step1 = true;
                        delay(2000);
                    }

                    //第二轮
                    if(!step2){
                        useGestureClick(null,314,1909,1000);
                        Toast.makeText(this, "2222step2", Toast.LENGTH_LONG).show();
                        step2 = true;
                        delay(2000);
                    }

                    //第三轮
                    if(!step3){
                        useGestureClick(null,534,1909,1000);
                        Toast.makeText(this, "22222222step3", Toast.LENGTH_LONG).show();
                        step3 = true;
                        delay(2000);
                    }
                    //第四轮
                    if(!step31){
                        useGestureClick(null,744,1909,1000);
                        Toast.makeText(this, "22222222step3", Toast.LENGTH_LONG).show();
                        step3 = true;
                        delay(2000);
                    }
                    //第四轮
                    if(!step32){
                        useGestureClick(null,989,1909,1000);
                        Toast.makeText(this, "22222222step3", Toast.LENGTH_LONG).show();
                        step3 = true;
                        delay(2000);
                    }
                }

                //if(step3)
                    //findAddMiaoAndClick(event);
                break;
        }

    }
    private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 步骤 2 加喵 - 当前界面操作
     * @param event
     * @return
     */
    private boolean findAddMiaoAndClick(AccessibilityEvent event) {
        AccessibilityNodeInfo eventSource  = getRootInActiveWindow();
        if (eventSource == null) return false;

        List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
        //深度遍历
        findOpenButton(eventSource,buttons,ZHAOHANG_ACTIVITY_ADDMIAO);
        //找到之后，取第一个起拍
        if(buttons.size() > 0){
            AccessibilityNodeInfo nodeToClick = buttons.get(0);
            //点击全部
            //nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            useGestureClick(nodeToClick,877,1425,500);
            return true;
        }
        return false;
    }


    /**
     * 深度遍历剪枝，懒惰贪心
     * @param node
     * @param visited
     * @param keyName
     */
    private void findOpenButton(AccessibilityNodeInfo node,List<AccessibilityNodeInfo> visited,String keyName) {
        if (node == null)
            return;
        if(visited.size() > 0){
            //取第一个
            return;
        }
        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.view.View".equals(node.getClassName())){
                if(node.getText() != null && keyName.equals(node.getText().toString()))
                    visited.add(node);
                else
                    return;
            }
            else
                return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            findOpenButton(node.getChild(i),visited,keyName);
        }
    }

    private void setCurrentActivityName(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        try {
            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()
            );

            getPackageManager().getActivityInfo(componentName, 0);
            currentActivityName = componentName.flattenToShortString();
        } catch (PackageManager.NameNotFoundException e) {
            currentActivityName = ZHAOHANG_GENERAL_ACTIVITY;
        }
    }


    /**
     * 手势模拟点击
     * @param info
     */
    public void useGestureClick(AccessibilityNodeInfo info,float x, float y,long startTime ) {

//        if (info == null) {
//            return;
//        }

//        Rect rect = new Rect();
//        info.getBoundsInScreen(rect);
        Path path = new Path();
        //确认出喵的位置
        path.moveTo(x,y);
        //path.moveTo(rect.centerX(), rect.centerY());
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, startTime, 100L,false))
                .build();

        boolean result = this.dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
        Log.d(TAG,"result"+result);
    }


    /**
     * 手势模拟滑动
     * @param info
     */
    public void useGestureSwape(AccessibilityNodeInfo info) {
        if (info == null) {
            return;
        }

        Rect rect = new Rect();
        info.getBoundsInScreen(rect);
        Path path = new Path();
        //确认出喵的位置
        path.moveTo(531,1902);
        //path.moveTo(rect.centerX(), rect.centerY());
        path.lineTo(500, 1202);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 100L, 1000L,false))
                .build();

        boolean result = this.dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
        Log.d(TAG,"result"+result);
    }


    public void click(Point point) {
        //只有7.0才可以用
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo((float) point.x, (float) point.y);
        path.lineTo((float) point.x, (float) point.y);
        /**
         * 参数path：笔画路径
         * 参数startTime：时间 (以毫秒为单位)，从手势开始到开始笔划的时间，非负数
         * 参数duration：笔划经过路径的持续时间(以毫秒为单位)，非负数
         */
        builder.addStroke(new GestureDescription.StrokeDescription(path, 1, 1));
        final GestureDescription build = builder.build();
        /**
         * 参数GestureDescription：翻译过来就是手势的描述，如果要实现模拟，首先要描述你的腰模拟的手势嘛
         * 参数GestureResultCallback：翻译过来就是手势的回调，手势模拟执行以后回调结果
         * 参数handler：大部分情况我们不用的话传空就可以了
         * 一般我们关注GestureDescription这个参数就够了，下边就重点介绍一下这个参数
         */
        dispatchGesture(build, new GestureResultCallback() {
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
    }


    private void clickMiddleInRect(Rect rect) {
        if (android.os.Build.VERSION.SDK_INT > 23) {
            Path path = new Path();

            //确认出喵的位置
            path.moveTo(937,907);
//            path.moveTo(rect.left + rect.width() / 2, rect.top + rect.height() / 2);
//            path.moveTo(rect.left+10 + rect.width() / 2, rect.top +10+ rect.height() / 2);
            GestureDescription.Builder builder = new GestureDescription.Builder();
            try {
                GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 450, 50)).build();
                dispatchGesture(gestureDescription, new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        super.onCompleted(gestureDescription);
                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        super.onCancelled(gestureDescription);
                    }
                }, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        this.watchFlagsFromPreference();
    }

    private void watchFlagsFromPreference() {
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    @Override
    public void onInterrupt() {
        //中断处理
    }
}