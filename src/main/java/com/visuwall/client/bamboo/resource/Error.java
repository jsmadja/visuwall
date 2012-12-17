package com.visuwall.client.bamboo.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "errors")
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {

    @XmlElements({@XmlElement(name = "error")})
    private List<String> errors = new ArrayList<String>();

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

}
