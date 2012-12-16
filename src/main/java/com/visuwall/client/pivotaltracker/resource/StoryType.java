package com.visuwall.client.pivotaltracker.resource;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "story_type")
public enum StoryType {
    chore, feature, bug, release;
}
