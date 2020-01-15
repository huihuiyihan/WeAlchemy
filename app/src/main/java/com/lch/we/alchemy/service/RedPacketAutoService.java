package com.lch.we.alchemy.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.lch.we.alchemy.R;
import com.lch.we.alchemy.utils.ACFind;
import com.lch.we.alchemy.utils.AppUtil;
import com.lch.we.alchemy.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * 辅助功能服务
 * Created by cyb on 2017/7/10.
 */
public class RedPacketAutoService extends AccessibilityService {

    private static final String ChatList = "com.tencent.wework.launch.WwMainActivity";
    /**
     * 消息列表页面Activity类名
     */
    private static final String MessageList = "com.tencent.wework.msg.controller.MessageListActivity";
    /**
     * 红包页面Activity类名
     */
    private static final String RedEnvelopedxv = "dxv";
    private static final String RedEnvelope = "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeCollectorActivity";
    /**
     * 红包详情页面Activity类名
     */
    private static final String RedEnvelopeDetail = "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeDetailActivity";

    /**
     *红包详情页面Activity类名
     */
    private static final String RedDetailCover =  "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeDetailWithCoverActivity";
    private String currentActivity;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.d( "RedPacketAutoService onServiceConnected 企业微信红包助手已启动");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        LogUtil.d( "event=" + event);
        switch (event.getEventType()) {
            //第一步：监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                if(getBooleanSetting("pref_watch_notification", true)){
                    onNotificationStateChanged(event);
                }
                break;

            //第二步：监听是否进入微信红包消息界面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String activityName = event.getClassName().toString();
                currentActivity = activityName;
                LogUtil.d( "activityName:" + activityName);

                /* 戳开红包，红包还没抢完，遍历节点匹配“拆红包” */
                List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
                findImageView(getRootInActiveWindow(),buttons);
                for (AccessibilityNodeInfo node2:buttons) {
                    if(buttons.size() > 3) return;
                    if (node2 != null) { //&& !mUnpackNode.equals(node2)
                        node2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                }
                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                String className = event.getClassName().toString();
                LogUtil.d( "className:" + className);

                if(ChatList.equals(currentActivity)){
                    watchList(event);
                }
                if (MessageList.equals(currentActivity)) { // 消息列表
                    if(getBooleanSetting("pref_auto_click_msg", true)) {
                        queryPacket();
                        return;
                    }
                }
                if (MessageList.equals(currentActivity)
                        || RedEnvelope.equals(currentActivity)
                        || RedEnvelopedxv.equals(currentActivity)) {
                    openPacket(event); // 开红包

                } else if ( RedDetailCover.equals(currentActivity) ||
                        RedEnvelopeDetail.equals(currentActivity)) {
                    if(getBooleanSetting("pref_auto_close", true)){
                        closeRedEnvelopeDetail(); // 关闭红包详情页面
                    }
                }


                break;
        }
    }

    /**
     * 通知状态改变时，判断是否有红包消息，有则模拟点击红包消息
     * @param accessibilityEvent
     */
    private void onNotificationStateChanged(AccessibilityEvent accessibilityEvent) {
        LogUtil.d("RedPacketAutoService TYPE_NOTIFICATION_STATE_CHANGED");
        List<CharSequence> textList = accessibilityEvent.getText();
        if (textList != null && textList.size() > 0) {
            for (CharSequence text : textList) {
                LogUtil.d("notification or toast text=" + text);
                String content = text.toString();

                String defaultKeyword = getResources().getString(R.string.notification_default_keyword); // 拼手气红包
                String keywords = sharedPreferences.getString("pref_notification_keyword", defaultKeyword);
                String[] keywordArray = keywords.split(";");
                for (String keyword : keywordArray) {
                    if (keyword != null && keyword.length() > 0) {
                        if (content.contains(keyword)) {
                            //模拟打开通知栏消息
                            Parcelable parcelableData = accessibilityEvent.getParcelableData();
                            if (parcelableData != null && parcelableData instanceof Notification) {
                                Notification notification = (Notification) parcelableData;
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * 关闭红包详情界面,实现自动返回聊天窗口
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void closeRedEnvelopeDetail() {
        LogUtil.d( "关闭红包详情 closeRedEnvelopeDetail");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            performGlobalAction(GLOBAL_ACTION_BACK); // 模拟按返回按钮
            //为了演示,直接查看了关闭按钮的id
//            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.wework:id/ce0");
//            LogUtil.d( "infos=" + infos);
//            nodeInfo.recycle();
//            for (AccessibilityNodeInfo item : infos) {
//                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
        }
    }

    /**
     * 模拟点击,拆开红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPacket(AccessibilityEvent event) {
        LogUtil.d( "拆开红包 openPacket");
        final AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            String haveBeenOpened = getResources().getString(R.string.red_packet_have_opened); // 手慢了，红包派完了
            String redPacketExpired = getResources().getString(R.string.red_packet_expired); // 红包已过期
            List<AccessibilityNodeInfo> resultList = nodeInfo.findAccessibilityNodeInfosByText(haveBeenOpened);
            List<AccessibilityNodeInfo> resultList2 = nodeInfo.findAccessibilityNodeInfosByText(redPacketExpired);
            LogUtil.d( "手慢了，红包派完了 resultList=" + resultList.size());
            LogUtil.d( "该红包已过期 resultList2=" + resultList2.size());
            // 判断红包是否已抢完，如已经抢完则自动关闭抢红包页面，如没有抢完则自动抢红包
            if (resultList.size() > 0 || resultList2.size() > 0) { // 红包已抢完
                LogUtil.d( "红包已抢完或已失效");
                if(!getBooleanSetting("pref_auto_close", true)){
                    return;
                }
                performGlobalAction(GLOBAL_ACTION_BACK); // 模拟按返回键
            } else {
                if(!getBooleanSetting("pref_auto_open", true)){
                    return;
                }
                /* 戳开红包，红包还没抢完，遍历节点匹配“拆红包” */
                List<AccessibilityNodeInfo> buttons = new ArrayList<AccessibilityNodeInfo>();
                findOpenButton(nodeInfo,buttons);
                for (AccessibilityNodeInfo node2:buttons) {
                    if(buttons.size() > 3) continue;
                    if (node2 != null) { //&& !mUnpackNode.equals(node2)
                        node2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        currentActivity = event.getClassName().toString();
                        return;
                    }
                }

            }
        }
    }

    private boolean watchList(AccessibilityEvent event) {
        AccessibilityNodeInfo eventSource = event.getSource();
        if ( eventSource == null)
            return false;
        String searchText = getResources().getString(R.string.chat_red_packet);
        List<AccessibilityNodeInfo> nodes = eventSource.findAccessibilityNodeInfosByText(searchText);
        if (!nodes.isEmpty()) {
            AccessibilityNodeInfo nodeToClick = nodes.get(0).getParent();
            nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            AccessibilityNodeInfo parent1 = null;
            int breaks = 0;
            while ((parent1 = nodeToClick.getParent()) != null) {
                LogUtil.d( "parentNode=" + parent1);
                if(breaks > 500){
                    break;
                }
                if (parent1.isClickable()) {
                    parent1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
                breaks++;
            }
            return true;
        }
        return false;
    }

    /**
     * 在消息列表查找红包
     * 模拟点击,打开抢红包界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void queryPacket() {
        LogUtil.d( "开始查找红包 queryPacket");
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        String searchText = getResources().getString(R.string.red_packet); // 领取红包
        AccessibilityNodeInfo node = getLastRedpackageNode(rootNode, searchText);
        LogUtil.d( "最新的红包=" + node);
        if (node != null) {
            boolean result = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            AccessibilityNodeInfo parent = null;
            while ((parent = node.getParent()) != null) {
                LogUtil.d( "parentNode=" + parent);
                if (parent.isClickable()) {
                    result = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
            if(result){
                 currentActivity  = RedEnvelope;
            }

        }
    }


    /**
     * 查找包含指定字符串的在屏幕最下面的一个节点
     * @param rootNode
     * @param search
     * @return
     */
    public AccessibilityNodeInfo getLastRedpackageNode(AccessibilityNodeInfo rootNode, String search) {
        AccessibilityNodeInfo resultNode = null;
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodeInfoList = rootNode.findAccessibilityNodeInfosByText(search);
//            LogUtil.d( "nodeInfoList=" + nodeInfoList);
            if (nodeInfoList != null && nodeInfoList.size() > 0) {
                int bottom = 0;
                for (AccessibilityNodeInfo node : nodeInfoList) {
                    if (node != null) {
                        final Rect rect = new Rect();
                        node.getBoundsInScreen(rect);
                        if (rect.bottom > bottom) {
                            resultNode = node;
                            bottom = rect.bottom;
                        }
                    }
                }
            }
        }
        return resultNode;
    }


    private AccessibilityNodeInfo getTheLastNode(AccessibilityNodeInfo rootNode,String... texts) {
        int bottom = 0;
        AccessibilityNodeInfo lastNode = null, tempNode;
        List<AccessibilityNodeInfo> nodes = new ArrayList<AccessibilityNodeInfo>();
        String[] keywords = new String[] {
                "的红包",
                "该红包已过期",
                "红包排行榜",
                "再领红包",
                "被抢光",
                "红包已领取"
        };
        ACFind find = new ACFind(keywords);

        for (String text : texts) {
            if (text == null) continue;

            List<AccessibilityNodeInfo> nodesAllContains = rootNode.findAccessibilityNodeInfosByText(text);
            for (AccessibilityNodeInfo targetNode:nodesAllContains) {

                int index = find.find(targetNode.getText().toString());
                //不包含
                if(index == -1){
                    nodes.add(targetNode);
                }

            }

            if (nodes != null && !nodes.isEmpty()) {
                tempNode = nodes.get(nodes.size() - 1);
                if (tempNode == null) return null;
                Rect bounds = new Rect();
                tempNode.getBoundsInScreen(bounds);
                if (bounds.bottom > bottom) {
                    bottom = bounds.bottom;
                    lastNode = tempNode;
                }
            }
        }
        return lastNode;
    }
    private void findOpenButton(AccessibilityNodeInfo node,List<AccessibilityNodeInfo> visited) {
        if (node == null)
            return;

        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.widget.ImageView".equals(node.getClassName())){
                visited.add(node);
                return;
            }
            else
                return;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findOpenButton(node.getChild(i),visited);
        }
    }

    private void findImageView(AccessibilityNodeInfo node,List<AccessibilityNodeInfo> visited) {
        if (node == null)
            return;

        if(visited.size() > 4){
            return;
        }
        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.widget.ImageView".equals(node.getClassName())){
                visited.add(node);
                return;
            }
            else
                return;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findImageView(node.getChild(i),visited);
        }
    }
    @Override
    public void onInterrupt() {
        LogUtil.d( "RedPacketAutoService onInterrupt 企业微信红包助手已停止");
    }

    private boolean getBooleanSetting(String key, boolean defaultValue){
        if(sharedPreferences != null){
            boolean value = sharedPreferences.getBoolean(key, defaultValue);
            LogUtil.d(key + "=" + value);
            return value;
        }
        return defaultValue;
    }

    private int getIntegerSetting(String key, int defaultValue){
        if(sharedPreferences != null) {
            String delayTime = sharedPreferences.getString(key, "" + defaultValue);
            LogUtil.d(key + "=" + delayTime);
            try {
                return Integer.parseInt(delayTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }
}
