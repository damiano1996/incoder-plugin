package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ChatState {

    private ChatStateEnum currentState;
    private volatile boolean stopRequested;

    public ChatState() {
        this.currentState = ChatStateEnum.IDLE;
        this.stopRequested = false;
    }

    public synchronized void setState(ChatStateEnum newState) {
        log.debug("State transition: {} -> {}", currentState, newState);
        this.currentState = newState;

        // Reset stop request when entering IDLE state
        if (newState == ChatStateEnum.IDLE) {
            this.stopRequested = false;
        }
    }

    public synchronized void requestStop() {
        if (currentState.canStop()) {
            log.debug("Stop requested for state: {}", currentState);
            this.stopRequested = true;
            setState(ChatStateEnum.STOPPING);
        } else {
            log.warn("Cannot stop in current state: {}", currentState);
        }
    }

    public boolean isGenerating() {
        return currentState.isGenerating();
    }

    public boolean canAcceptInput() {
        return currentState.canAcceptInput();
    }

    public boolean canStop() {
        return currentState.canStop();
    }

    public void checkStopRequest() {
        if (stopRequested) {
            throw new StopStreamException();
        }
    }

    public synchronized void handleError() {
        setState(ChatStateEnum.ERROR);
    }

    public synchronized void reset() {
        setState(ChatStateEnum.IDLE);
    }
}
