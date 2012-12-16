package com.visuwall.client.deployit.resource;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "archived-tasks")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArchivedTasks {

    @XmlElements({@XmlElement(name = "task")})
    private List<Task> tasks;

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

}
