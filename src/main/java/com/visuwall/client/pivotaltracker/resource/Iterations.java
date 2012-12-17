package com.visuwall.client.pivotaltracker.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "iterations")
@XmlAccessorType(XmlAccessType.FIELD)
public class Iterations {

    @XmlElements(@XmlElement(name = "iteration"))
    private List<Iteration> iterations = new ArrayList<Iteration>();

    public Iteration get(int i) {
        return iterations.get(i);
    }

}
