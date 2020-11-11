package com.llw.goodmusic.bean;

public class ChangeUI {
    /**
     * 状态
     */
    private String state;

    /**
     * 是否改变播放状态 （true 同时改变UI、false 只改变UI）
     */
    private boolean isChangeState;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isChangeState() {
        return isChangeState;
    }

    public void setChangeState(boolean changeState) {
        isChangeState = changeState;
    }

    public ChangeUI(String state, boolean isChangeState) {
        this.state = state;
        this.isChangeState = isChangeState;
    }
}
