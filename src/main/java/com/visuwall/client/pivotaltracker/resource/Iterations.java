package com.visuwall.client.pivotaltracker.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "iterations")
@XmlAccessorType(XmlAccessType.FIELD)
public class Iterations {

    @XmlElements(@XmlElement(name = "iteration"))
    private List<Iteration> iterations = new ArrayList<Iteration>();

    public Iteration get(int i) {
        return iterations.get(i);
    }

}
