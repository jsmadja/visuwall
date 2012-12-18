package com.visuwall.api.domain;

import org.joda.time.DateMidnight;

public class Iteration {

    private DateMidnight end;

    private Stories stories = new Stories();

    public Iteration(Stories stories, DateMidnight end) {
        this.stories = stories;
        this.end = end;
    }

    public DateMidnight getEnd() {
        return end;
    }

    public Stories getStories() {
        return stories;
    }
}
