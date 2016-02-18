package in.srain.cube.views.ptr;


/**
 * A single linked list(单链表) to wrap(包装) PtrUIHandler
 */
// 单例模式
class PtrUIHandlerHolder implements PtrUIHandler {

    private PtrUIHandler mHandler;
    
    private PtrUIHandlerHolder mNext;

    private boolean contains(PtrUIHandler handler) {
        return mHandler != null && mHandler == handler;
    }
    
    private PtrUIHandlerHolder() {

    }

    public boolean hasHandler() {
        return mHandler != null;
    }

    private PtrUIHandler getHandler() {
        return mHandler;
    }

    /**
     * 链表中添加一个节点
     * @param head
     * @param handler
     */
    public static void addHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {

        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }

        PtrUIHandlerHolder current = head;
        
        // 遍历
        for (; ; current = current.mNext) {
            // duplicated
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        PtrUIHandlerHolder newHolder = new PtrUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static PtrUIHandlerHolder create() {
        return new PtrUIHandlerHolder();
    }

    /**
     * 链表中删除一个节点
     * @param head
     * @param handler
     * @return
     */
    public static PtrUIHandlerHolder removeHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
    	
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }

        PtrUIHandlerHolder current = head;
        PtrUIHandlerHolder pre = null;
        
        do {
            // delete current: link pre to next, unlink next from current;
            // pre will no change, current move to next element;
            if (current.contains(handler)) {
                // current is head
                if (pre == null) {
                	
                    head = current.mNext;
                    current.mNext = null;

                    current = head;
                } else {
                	
                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }

        } while (current != null);
        
        if (head == null) {
            head = new PtrUIHandlerHolder();
        }
        
        return head;
    }

    /**
     * UI 复位
     */
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    /**
     * UI 刷新准备
     */
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshPrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    /**
     * UI 刷新开始
     */
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    /**
     * UI 刷新完成
     */
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshComplete(frame);
            }
        } while ((current = current.mNext) != null);
    }

    /**
     * UI 位置改变
     */
    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, int oldPosition, int currentPosition, float oldPercent, float currentPercent) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIPositionChange(frame, isUnderTouch, status, oldPosition, currentPosition, oldPercent, currentPercent);
            }
        } while ((current = current.mNext) != null);
    }
}
