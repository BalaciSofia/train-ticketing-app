package com.BalaciKlaraSofia.train_ticketing.dto;

import java.util.List;

public class RouteSearchResult {

    private List<DirectRoute> directRoutes;
    private List<ChangeoverRoute> changeoverRoutes;

    public RouteSearchResult(List<DirectRoute> directRoutes, List<ChangeoverRoute> changeoverRoutes) {
        this.directRoutes = directRoutes;
        this.changeoverRoutes = changeoverRoutes;
    }

    public List<DirectRoute> getDirectRoutes() { return directRoutes; }
    public List<ChangeoverRoute> getChangeoverRoutes() { return changeoverRoutes; }
}
