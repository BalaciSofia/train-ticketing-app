package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.dto.RouteSearchResult;
import com.BalaciKlaraSofia.train_ticketing.service.RouteSearchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class RouteSearchController {

    private final RouteSearchService routeSearchService;

    public RouteSearchController(RouteSearchService routeSearchService) {
        this.routeSearchService = routeSearchService;
    }

    @GetMapping
    public RouteSearchResult search(@RequestParam Integer fromStationId,
                                    @RequestParam Integer toStationId) {
        return routeSearchService.search(fromStationId, toStationId);
    }
}
