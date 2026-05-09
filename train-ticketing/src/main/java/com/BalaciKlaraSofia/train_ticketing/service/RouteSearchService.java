package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.ScheduleStop;
import com.BalaciKlaraSofia.train_ticketing.dto.ChangeoverRoute;
import com.BalaciKlaraSofia.train_ticketing.dto.DirectRoute;
import com.BalaciKlaraSofia.train_ticketing.dto.RouteSearchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RouteSearchService {

    private final ScheduleStopService scheduleStopService;

    public RouteSearchService(ScheduleStopService scheduleStopService) {
        this.scheduleStopService = scheduleStopService;
    }

    public RouteSearchResult search(Integer fromStationId, Integer toStationId) {
        List<ScheduleStop> fromStops = scheduleStopService.findByStationId(fromStationId);
        List<ScheduleStop> toStops = scheduleStopService.findByStationId(toStationId);

        Map<Integer, ScheduleStop> fromBySchedule = fromStops.stream()
                .collect(Collectors.toMap(ss -> ss.getSchedule().getId(), ss -> ss));
        Map<Integer, ScheduleStop> toBySchedule = toStops.stream()
                .collect(Collectors.toMap(ss -> ss.getSchedule().getId(), ss -> ss));

        List<DirectRoute> directRoutes = new ArrayList<>();
        List<ChangeoverRoute> changeoverRoutes = new ArrayList<>();

        for (Map.Entry<Integer, ScheduleStop> entry : fromBySchedule.entrySet()) {
            ScheduleStop dep = entry.getValue();
            ScheduleStop arr = toBySchedule.get(entry.getKey());
            if (arr != null && dep.getRouteStop().getStopNumber() < arr.getRouteStop().getStopNumber()) {
                directRoutes.add(buildDirect(dep, arr));
            }
        }

        for (Map.Entry<Integer, ScheduleStop> entry : fromBySchedule.entrySet()) {
            Integer s1Id = entry.getKey();
            ScheduleStop depStop = entry.getValue();

            if (toBySchedule.containsKey(s1Id)) continue;

            List<ScheduleStop> laterOnS1 = scheduleStopService
                    .findLaterStopsOnSchedule(s1Id, depStop.getRouteStop().getStopNumber());

            for (ScheduleStop changeoverArrival : laterOnS1) {
                Integer changeoverStationId = changeoverArrival.getRouteStop().getStation().getId();
                if (changeoverStationId.equals(toStationId)) continue;

                List<ScheduleStop> stopsAtChangeover = scheduleStopService.findByStationId(changeoverStationId);

                for (ScheduleStop changeoverDep : stopsAtChangeover) {
                    Integer s2Id = changeoverDep.getSchedule().getId();
                    if (s2Id.equals(s1Id)) continue;

                    ScheduleStop finalArr = toBySchedule.get(s2Id);
                    if (finalArr == null) continue;

                    if (changeoverDep.getRouteStop().getStopNumber() >= finalArr.getRouteStop().getStopNumber()) continue;
                    if (!changeoverDep.getDepartureTime().isAfter(changeoverArrival.getArrivalTime())) continue;

                    String changeoverCity = changeoverArrival.getRouteStop().getStation().getCity();
                    changeoverRoutes.add(new ChangeoverRoute(
                            buildDirect(depStop, changeoverArrival),
                            buildDirect(changeoverDep, finalArr),
                            changeoverCity
                    ));
                }
            }
        }

        return new RouteSearchResult(directRoutes, changeoverRoutes);
    }

    private DirectRoute buildDirect(ScheduleStop dep, ScheduleStop arr) {
        return new DirectRoute(
                dep.getSchedule().getTrain().getTrainNumber(),
                dep.getRouteStop().getStation().getCity(),
                dep.getDepartureTime(),
                arr.getRouteStop().getStation().getCity(),
                arr.getArrivalTime(),
                dep.getId(),
                arr.getId()
        );
    }
}
