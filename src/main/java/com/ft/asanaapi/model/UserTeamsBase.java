package com.ft.asanaapi.model;

import com.asana.Client;
import com.asana.models.Team;
import com.asana.requests.CollectionRequest;
import com.asana.resources.Resource;

public class UserTeamsBase extends Resource {
    public UserTeamsBase(Client client) {
        super(client);
    }

    public CollectionRequest<Team> findByUser(String userId) {
        String path = String.format("/users/%s/teams", userId);
        return new CollectionRequest<>(this, Team.class, path, "GET");
    }
}
