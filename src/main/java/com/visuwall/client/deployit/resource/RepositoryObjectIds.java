package com.visuwall.client.deployit.resource;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "repository-object-ids")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryObjectIds {

    @XmlElements({@XmlElement(name = "repository-object-id")})
    private List<String> repositoryObjectIds;

    public void setRepositoryObjectIds(List<String> repositoryObjectIds) {
        this.repositoryObjectIds = repositoryObjectIds;
    }

    public List<String> getRepositoryObjectIds() {
        return repositoryObjectIds;
    }

}
