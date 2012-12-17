package com.visuwall.client.pivotaltracker.resource;

import javax.xml.bind.annotation.*;
import java.util.Iterator;
import java.util.List;

@XmlRootElement(name = "projects")
@XmlAccessorType(XmlAccessType.FIELD)
public class Projects implements Iterable<Project> {

    @XmlElements({@XmlElement(name = "project")})
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }

    @Override
    public Iterator<Project> iterator() {
        return projects.iterator();
    }
}
