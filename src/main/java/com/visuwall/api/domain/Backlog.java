package com.visuwall.api.domain;

public class Backlog {

    private Stories stories = new Stories();

    public Backlog(Stories stories) {
        this.stories = stories;
    }

    public Stories getStories() {
        return stories;
    }

}
