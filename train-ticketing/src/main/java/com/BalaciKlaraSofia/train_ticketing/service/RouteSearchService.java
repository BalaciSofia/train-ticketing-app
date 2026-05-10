package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.dto.RouteSearchResult;

public interface RouteSearchService {
    RouteSearchResult search(Integer fromStationId, Integer toStationId);
}
