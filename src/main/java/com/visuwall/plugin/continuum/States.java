package com.visuwall.plugin.continuum;

import com.visuwall.api.domain.BuildState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.visuwall.api.domain.BuildState.*;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<Integer, BuildState> STATE_MAPPING = new HashMap<Integer, BuildState>();

    static {
        STATE_MAPPING.put(2, SUCCESS);
        STATE_MAPPING.put(3, FAILURE);
    }

    public static BuildState asVisuwallState(int continuumState) {
        BuildState state = STATE_MAPPING.get(continuumState);
        if (state == null) {
            state = UNKNOWN;
            LOG.warn(continuumState + " is not available in Continuum plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }

}
