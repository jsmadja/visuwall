package com.visuwall.api.domain;

public class Story {

    private State state;
    private Integer estimation;

    public boolean hasState(State state) {
        return this.state == state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setEstimation(Integer estimation) {
        this.estimation = estimation;
    }

    public boolean isNotEstimated() {
        return estimation == null;
    }

    public boolean isEstimated() {
        return estimation != null;
    }

    public Integer getEstimation() {
        return estimation;
    }

    public enum State {
        ACCEPTED, READY, STARTED, UNKNOWN, UNSTARTED, DELIVERED, REJECTED;
    }

}
