package com.visuwall.client.bamboo.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    private String auth;

    @XmlElements({@XmlElement(name = "build")})
    private List<Build23> builds = new ArrayList<Build23>();

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAuth() {
        return auth;
    }

    public void setBuilds(List<Build23> builds) {
        this.builds = builds;
    }

    public List<Build23> getBuilds() {
        return builds;
    }

}
