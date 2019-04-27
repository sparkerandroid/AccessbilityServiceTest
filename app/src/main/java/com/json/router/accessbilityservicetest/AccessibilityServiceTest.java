package com.json.router.accessbilityservicetest;

import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.Map;

public class AccessibilityServiceTest extends AccessibilityService {

    private Map<Integer, Boolean> handledMap = new HashMap<>();//防止二次处理


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                int windowId = nodeInfo.getWindowId();
                if (handledMap.get(windowId) == null) {
                    handledMap.put(windowId, true);
                    iterateAccessibilityNodeTree(nodeInfo);
                }
            }
        }
    }

    // 一个AccessibilityNodeInfo是一个Android的View的属性及操作的抽象
    // 因此，AccessibilityNodeInfo的用法和View或ViewGroup一致
    private boolean iterateAccessibilityNodeTree(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();// 子View的数量
            if ("android.widget.Button".equals(nodeInfo.getClassName())) {
                String widgetTextContent = nodeInfo.getText().toString();
                if (!TextUtils.isEmpty(widgetTextContent) && (
                        widgetTextContent.equals("确定") ||
                                widgetTextContent.equals("完成") ||
                                widgetTextContent.equals("安装")
                )) {// 找到了满足要求的按钮，执行点击事件
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            } else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

            }

            for (int i = 0; i < childCount; i++) {
                if (iterateAccessibilityNodeTree(nodeInfo.getChild(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }
}
