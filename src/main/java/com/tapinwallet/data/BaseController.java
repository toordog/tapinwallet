package com.tapinwallet.data;

/**
 *
 * @author michael
 */
public class BaseController {
    protected AppContext ctx;

    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;
        onAppContextAvailable();
    }

    // subclasses can override to react immediately
    protected void onAppContextAvailable() { }
}
