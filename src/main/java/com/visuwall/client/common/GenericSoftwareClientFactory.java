package com.visuwall.client.common;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class GenericSoftwareClientFactory {

    public GenericSoftwareClient createClient(Map<String, String> properties) {
        if (properties != null && StringUtils.isNotBlank(properties.get("login")) && StringUtils.isNotBlank(properties.get("password"))) {
            String login = properties.get("login");
            String password = properties.get("password");
            return new GenericSoftwareClient(login, password);
        }
        return new GenericSoftwareClient();
    }

}
