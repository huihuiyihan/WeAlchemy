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

public class ZhaoHangAccessService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener  {
    private static final String TAG = "ZhaoHangAccessService";

    private AccessibilityNodeInfo rootNodeInfo;
    //活动页面
    private static final String ZHAOHANG_GENERAL_ACTIVITY = "com.project.foundation.cmbView.cmbwebviewv2.cmbW1IZ35";
    //当前活动页面
    private String currentActivityName = ZHAOHANG_GENERAL_ACTIVITY;
    //商品页面 -- 通过debug得到
    private static final String ZHAOHANG_ACTIVITY_GOODS = "com.project.foundation.cmbView.cmbwebviewv2.cmbW1IZ35";
    //// zhaohang
    private static final String ZHAOHANG_ACTIVITY_PAGE = "起拍喵数";
    private static final String ZHAOHANG_ACTIVITY_ADDPAGE = "加喵";
    //加喵页面   ---通过debug得到
    private static final String ZHAOHANG_ACTIVITY_JIAMIAO = "com.project.foundation.cmbView.cmbwebviewv2.cmbX7UB5E";
    //加喵按钮
    private static final String ZHAOHANG_ACTIVITY_ADDMIAO = "全部";
    //加喵页面   ---通过debug得到
    private static final String ZHAOHANG_ACTIVITY_JIAMIAO_OK = "com.project.foundation.cmbView.cmbwebviewv2.cmbW1IZ35";
    //加喵按钮 530 2107
    private static final String ZHAOHANG_ACTIVITY_ADDMIAO_OK = "确认出喵";
    private static final String ZHAOHANG_ACTIVITY_ADDMIAO_OKINFO = "出喵成功后您的小招喵将被扣减，若抢拍失败将于结果公布后1个工作日内返还，若抢拍成功则不作返还。";


    //是否加喵页面
    private boolean step1 = false;
    //加喵完成
    private boolean step2 = false;
    //完成
    private boolean step3 = false;

    private SharedPreferences sharedPreferences;
    private boolean isAddPreferences = false;//是否出喵配置
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

                Toast.makeText(this, "Window Changed", Toast.LENGTH_SHORT).show();
                setCurrentActivityName(event);
                //findAddMiaoAndClick(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

//                if(currentActivityName.contains(ZHAOHANG_ACTIVITY_GOODS)){
////                    //起拍喵
////                    if(findStartMiaoAndClick(event,ZHAOHANG_ACTIVITY_PAGE))return;
////                    //加喵
////                    //findReAddMiaoAndClick(event,ZHAOHANG_ACTIVITY_ADDPAGE);
////                }
                Toast.makeText(this, "Content Changed", Toast.LENGTH_SHORT).show();

                findWebViewFrameButton();
                //findStartMiaoAndClick(event, ZHAOHANG_ACTIVITY_PAGE,ZHAOHANG_ACTIVITY_ADDPAGE);
                //findStartMiaoAndClick(event, "第四轮");
//                if(step2){
//                    //点击全部
                //findAddMiaoAndClick(event);
//                }
                //加喵完成
                //if(!isAddOver) return;
                //确认加喵
                //findOKMiaoAndClick(event,ZHAOHANG_ACTIVITY_ADDMIAO_OK);

                break;
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
     * 步骤  1 起拍 -跳转
     * @param event
     * @param keyName
     * @return
     */
    private boolean findStartMiaoAndClick(AccessibilityEvent event,String keyName,String keyName2) {
        //if(!currentActivityName.contains(ZHAOHANG_GENERAL_ACTIVITY)) return false;
        AccessibilityNodeInfo eventSource  = getRootInActiveWindow();
        if (eventSource == null) return false;

        List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
        //深度遍历
        findOpenButton(eventSource,buttons,"",keyName2);
        //findOpenButton(eventSource,buttons,keyName,keyName2);
        //找到之后，取第一个起拍
        //debug 共16个起拍喵数 按钮
        if(buttons.size() > 0){
            for(int i = 0;i < buttons.size(); i++){
                AccessibilityNodeInfo nodeToClick1 = buttons.get(i);
                if(nodeToClick1.getText().toString().equals(ZHAOHANG_ACTIVITY_PAGE)){
                    if(nodeToClick1.getParent() != null &&
                            nodeToClick1.getParent().getParent() != null &&
                            nodeToClick1.getParent().getParent().getParent() != null &&
                            nodeToClick1.getParent().getParent().getParent() .getChildCount() > 1){
                        String text = nodeToClick1.getParent().getParent().getParent().getChild(2).getText().toString();
                        //useGestureClick(null,664,908,0);
                        if(!text.equals("华为手机666元购买权益")) {
                            continue;
                        }
                    }
                    Toast.makeText(this, "Click "+ZHAOHANG_ACTIVITY_PAGE, Toast.LENGTH_SHORT).show();
                    //起拍
                    nodeToClick1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    nodeToClick1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else if(nodeToClick1.getText().toString().equals(ZHAOHANG_ACTIVITY_ADDPAGE)){
                    Toast.makeText(this, "Click "+ZHAOHANG_ACTIVITY_ADDPAGE, Toast.LENGTH_SHORT).show();
                    nodeToClick1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                step1 = false;
                step2 = true;
                step3 = false;
            }
            //useGestureClick(null,530,2107,10);
            //useGestureClick(null,265,557,10);
            return true;
        }

        return false;

    }

    /**
     * 步骤 2 加喵 - 当前界面操作
     * @param event
     * @return
     */
    private boolean findAddMiaoAndClick(AccessibilityEvent event) {
        //if(!currentActivityName.contains(ZHAOHANG_ACTIVITY_JIAMIAO)) return false;
        AccessibilityNodeInfo eventSource  = getRootInActiveWindow();
        if (eventSource == null) return false;

        List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
        //深度遍历
        findOpenButton(eventSource,buttons,ZHAOHANG_ACTIVITY_ADDMIAO,"");
        //找到之后，取第一个起拍
        if(buttons.size() > 0){
            AccessibilityNodeInfo nodeToClick = buttons.get(0);
            //点击全部
            Toast.makeText(this, "Click "+ZHAOHANG_ACTIVITY_ADDMIAO, Toast.LENGTH_SHORT).show();

            nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            step1 = true;
            //其实nodeToClick节点的父节点，在获取第19个子节点就是【确认出喵】按钮
            //全部879，1936
            //AccessibilityNodeInfo finalNode = nodeToClick.getParent().getChild(19);
            //执行点击确认出喵
            //非原生控件，点击事件机制发生变化采用Gesture手势解决点击
            //useGestureClick(finalNode,ZhaoHangAccessService.this);
            useGestureClick(null,530,2107,0);
            Toast.makeText(this, "Click "+ZHAOHANG_ACTIVITY_ADDMIAO_OK, Toast.LENGTH_SHORT).show();

        }


//        //以下是确认出喵-同一个界面放在一个方法
//        List<AccessibilityNodeInfo> buttonsOK = new ArrayList<AccessibilityNodeInfo>();
//        //深度遍历
//        findOpenAllButton(eventSource,buttonsOK,ZHAOHANG_ACTIVITY_ADDMIAO_OK);
//
//        //点击出喵
//        if(buttonsOK.size() > 0){
//            AccessibilityNodeInfo nodeToClick = buttonsOK.get(0);
//            if(isAddPreferences){
//                if(true){//!isAddEnd
//                    nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    useGestureClick(nodeToClick,ZhaoHangAccessService.this);
//                }
//
//                isAddEnd = true;
//            }
//            return true;
//        }

        return false;
    }

//    /**
//     * 步骤 3 确认 页面跳转
//     * @param event
//     * @param keyName
//     * @return
//     */
//    private boolean findOKMiaoAndClick(AccessibilityEvent event,String keyName) {
    //此处要注意了
//        if(!currentActivityName.contains(ZHAOHANG_ACTIVITY_JIAMIAO_OK)){
//            //不是加喵页面，退出
//            return true;
//        }
//        AccessibilityNodeInfo eventSource  = getRootInActiveWindow();
//        if (eventSource == null) return false;
//
//        List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
//        //深度遍历
//        findOpenAllButton(eventSource,buttons,keyName);
//
//        //找到之后，取第一个
//        if(buttons.size() > 0){
//            AccessibilityNodeInfo nodeToClick = buttons.get(0);
//            isAddPage = true;
//            if(isAddPreferences){
//                if(!isAddEnd)
//                    nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                isAddEnd = true;
//            }
//            return true;
//        }
//        return false;
////    }

    /**
     * 步骤  1/4 起拍 -加喵
     * @param event
     * @param keyName
     * @return
     */
    private boolean findReAddMiaoAndClick(AccessibilityEvent event,String keyName) {
        AccessibilityNodeInfo eventSource  = getRootInActiveWindow();
        if (eventSource == null) return false;

        List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
        //深度遍历
        findOpenButton(eventSource,buttons,keyName,"");
        //找到之后，取第一个起拍
        //debug 共16个起拍喵数 按钮
        if(buttons.size() > 0){
            AccessibilityNodeInfo nodeToClick = buttons.get(0);
            step1 = true;
            nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            //以下代码不知何故无效
            //此处测试Gesture
            useGestureClick(nodeToClick,ZhaoHangAccessService.this);

            //点击
//            Rect rect = new Rect();
//            nodeToClick.getBoundsInScreen(rect);
//            clickMiddleInRect(rect);
            return true;
        }

        return false;

    }

    @Override
    public void onInterrupt() {
        //中断处理
    }

    /**
     * 深度遍历剪枝，懒惰贪心
     * @param node
     * @param visited
     * @param keyName
     */
    private void findOpenButton(AccessibilityNodeInfo node,List<AccessibilityNodeInfo> visited,String keyName,String keyName2) {
        if (node == null)
            return;
        if(visited.size() > 1){
            //取第一个
            return;
        }
        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.view.View".equals(node.getClassName())){
                if(node.getText() != null && !node.getText().toString().equals("") && (keyName.equals(node.getText().toString()) || keyName2.equals(node.getText().toString())))
                    visited.add(node);
                else
                    return;
            }
            else
                return;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findOpenButton(node.getChild(i),visited,keyName,keyName2);
        }
    }
    private void findWebViewFrameButton() {

        AccessibilityNodeInfo node  = getRootInActiveWindow();

        List<AccessibilityNodeInfo> webview = new ArrayList<AccessibilityNodeInfo>();
        findWebView(node,webview);

        if(webview.size() > 0) {
            if (webview.get(0).getChildCount() > 0 &&
                    webview.get(0).getChild(0).getChildCount() > 0) {
                AccessibilityNodeInfo ddWiew = webview.get(0).getChild(0).getChild(0);
                if (ddWiew.getChildCount() == 2) {
                    //商品界面
                    List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
                    findOpenButton(ddWiew.getChild(1), buttons, ZHAOHANG_ACTIVITY_PAGE, ZHAOHANG_ACTIVITY_ADDPAGE);

                    if (buttons.size() > 0) {
                        for (int i = 0; i < buttons.size(); i++) {
                            AccessibilityNodeInfo nodeToClick1 = buttons.get(i);
                            if (nodeToClick1.getText().toString().equals(ZHAOHANG_ACTIVITY_PAGE)) {
                                if (nodeToClick1.getParent() != null &&
                                        nodeToClick1.getParent().getParent() != null &&
                                        nodeToClick1.getParent().getParent().getParent() != null &&
                                        nodeToClick1.getParent().getParent().getParent().getChildCount() > 1) {
                                    String text = nodeToClick1.getParent().getParent().getParent().getChild(2).getText().toString();
                                    //useGestureClick(null,664,908,0);
                                    if (!text.equals("华为手机666元购买权益")) {
                                        continue;
                                    }
                                }
                                Toast.makeText(this, "Click " + ZHAOHANG_ACTIVITY_PAGE, Toast.LENGTH_SHORT).show();

                                nodeToClick1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                nodeToClick1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            } else if (nodeToClick1.getText().toString().equals(ZHAOHANG_ACTIVITY_ADDPAGE)) {
                                Toast.makeText(this, "Click " + ZHAOHANG_ACTIVITY_ADDPAGE, Toast.LENGTH_SHORT).show();
                                nodeToClick1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                }

                if (ddWiew.getChildCount() > 2) {
                    //加喵
                    List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
                    //深度遍历
                    findOpenButton(ddWiew.getChild(2), buttons, "+50", "");
                    //找到之后，取第一个起拍
                    if (buttons.size() > 0) {
                        AccessibilityNodeInfo nodeToClick = buttons.get(0);
                        //点击全部
                        Toast.makeText(this, "Click " + ZHAOHANG_ACTIVITY_ADDMIAO, Toast.LENGTH_SHORT).show();
                        nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        for (int i = 0; i < 5; i++) {
                            useGestureClick(null, 636, 1957, 0);
                        }

                        for (int i = 0; i < nodeToClick.getParent().getChildCount(); i++) {
                            nodeToClick.getParent().getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }

                        useGestureClick(null, 530, 2107, 0);
                        Toast.makeText(this, "Click " + ZHAOHANG_ACTIVITY_ADDMIAO_OK, Toast.LENGTH_SHORT).show();

                    }
                }

            }
        }
    }

    private void findWebView(AccessibilityNodeInfo node,List<AccessibilityNodeInfo> visited) {
        if (node == null)
            return;
        if(visited.size() > 0){
            //取第一个
            return;
        }
        //非layout元素
        if ("android.webkit.WebView".equals(node.getClassName())){
            visited.add(node);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findWebView(node.getChild(i),visited);
        }
    }

    private void findWebViewButton(AccessibilityNodeInfo node,List<AccessibilityNodeInfo> visited,String keyName,String keyName2) {
        if (node == null)
            return;
        if(visited.size() > 1){
            //取第一个
            return;
        }
        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.view.View".equals(node.getClassName())){
                if(node.getText() != null && !node.getText().toString().equals("") && (keyName.equals(node.getText().toString()) || keyName2.equals(node.getText().toString())))
                    visited.add(node);
                else
                    return;
            }
            else
                return;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findOpenButton(node.getChild(i),visited,keyName,keyName2);
        }
    }

    /**
     * 深度遍历剪枝，懒惰贪心
     * @param node
     * @param visited
     * @param keyName
     */
    private void findOpenAllButton(AccessibilityNodeInfo node,List<AccessibilityNodeInfo> visited,String keyName) {
        if (node == null)
            return;

        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.view.View".equals(node.getClassName())){
                if(node.getText() !=null && keyName.equals(node.getText().toString()))
                    visited.add(node);
                else
                    return;
            }
            else
                return;
        }

        for (int i = node.getChildCount() - 1; i < 0; i--) {
            findOpenAllButton(node.getChild(i),visited,keyName);
        }
    }


    //一下为手势代码---始终无效，郁闷

    /**
     * 手势模拟点击
     * @param info
     */
    public void useGestureClick(AccessibilityNodeInfo info,float x, float y,long startTime ) {
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
     * 手势模拟点击
     * @param info
     * @param accessibilityService
     */
    public void useGestureClick(AccessibilityNodeInfo info, AccessibilityService accessibilityService) {

        if (info == null) {
            return;
        }

        Rect rect = new Rect();
        info.getBoundsInScreen(rect);
        Path path = new Path();
        //确认出喵的位置
        path.moveTo(530,2107);
        //path.moveTo(rect.centerX(), rect.centerY());
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 100L, 800L,false))
                .build();

        boolean result = accessibilityService.dispatchGesture(gestureDescription, new GestureResultCallback() {
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        isAddPreferences = sharedPreferences.getBoolean("check_box_preference_chuMiao", false);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("check_box_preference_chuMiao")) {
            isAddPreferences = sharedPreferences.getBoolean(key, false);
        }
    }


}