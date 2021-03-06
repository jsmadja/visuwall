/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.visuwall.api.domain.track;

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

    public static class Builder {

        private State state;
        private Integer estimation;

        public Builder withState(State state) {
            this.state = state;
            return this;
        }

        public Builder withEstimation(int estimation) {
            this.estimation = estimation;
            return this;
        }

        public Story build() {
            Story story = new Story();
            story.state = state;
            story.estimation = estimation;
            return story;
        }

    }

}
