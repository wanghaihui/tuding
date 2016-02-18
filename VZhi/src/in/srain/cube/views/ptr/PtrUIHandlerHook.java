package in.srain.cube.views.ptr;

/**
 * Run a hook(钩子) runnable, the runnable will run only once.
 * After the runnable is done, call resume to resume(重新开始，取回).
 * Once run, call takeover will directory call the resume action
 */
public abstract class PtrUIHandlerHook implements Runnable {

    private Runnable mResumeAction;
    
    private static final byte STATUS_PREPARE = 0;
    private static final byte STATUS_IN_HOOK = 1;
    private static final byte STATUS_RESUMED = 2;
    
    private byte mStatus = STATUS_PREPARE;
    
    /**
     * 接收
     */
    public void takeOver() {
        takeOver(null);
    }

    public void takeOver(Runnable resumeAction) {
    	
        if (resumeAction != null) {
            mResumeAction = resumeAction;
        }
        
        switch (mStatus) {
            case STATUS_PREPARE:
                mStatus = STATUS_IN_HOOK;
                run();
                break;
            case STATUS_IN_HOOK:
                break;
            case STATUS_RESUMED:
                resume();
                break;
        }
    }

    public void reset() {
        mStatus = STATUS_PREPARE;
    }

    public void resume() {
        if (mResumeAction != null) {
            mResumeAction.run();
        }
        mStatus = STATUS_RESUMED;
    }

    public void setResumeAction(Runnable runnable) {
        mResumeAction = runnable;
    }
}