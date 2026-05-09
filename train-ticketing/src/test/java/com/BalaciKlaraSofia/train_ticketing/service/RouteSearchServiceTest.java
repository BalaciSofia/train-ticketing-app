package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.*;
import com.BalaciKlaraSofia.train_ticketing.dto.RouteSearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RouteSearchServiceTest {

    @Mock private ScheduleStopService scheduleStopService;

    @InjectMocks private RouteSearchService routeSearchService;

    private ScheduleStop mockStop(int id, int scheduleId, String trainNumber,
                                  int stationId, String city, int stopNumber,
                                  LocalDateTime arrivalTime, LocalDateTime departureTime) {
        Station station = mock(Station.class);
        when(station.getId()).thenReturn(stationId);
        when(station.getCity()).thenReturn(city);

        RouteStop routeStop = mock(RouteStop.class);
        when(routeStop.getStopNumber()).thenReturn(stopNumber);
        when(routeStop.getStation()).thenReturn(station);

        Train train = mock(Train.class);
        when(train.getTrainNumber()).thenReturn(trainNumber);

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleId);
        when(schedule.getTrain()).thenReturn(train);

        ScheduleStop stop = mock(ScheduleStop.class);
        when(stop.getId()).thenReturn(id);
        when(stop.getSchedule()).thenReturn(schedule);
        when(stop.getRouteStop()).thenReturn(routeStop);
        when(stop.getArrivalTime()).thenReturn(arrivalTime);
        when(stop.getDepartureTime()).thenReturn(departureTime);
        return stop;
    }

    @Test
    void search_directRouteExists_returnsDirectRoute() {
        LocalDateTime dep = LocalDateTime.of(2026, 5, 15, 8, 0);
        LocalDateTime arr = LocalDateTime.of(2026, 5, 15, 11, 30);

        ScheduleStop fromStop = mockStop(1, 1, "IR 1581", 1, "Bucuresti Nord", 1, dep, dep);
        ScheduleStop toStop   = mockStop(4, 1, "IR 1581", 4, "Brasov",         4, arr, arr);

        when(scheduleStopService.findByStationId(1)).thenReturn(List.of(fromStop));
        when(scheduleStopService.findByStationId(4)).thenReturn(List.of(toStop));

        RouteSearchResult result = routeSearchService.search(1, 4);

        assertEquals(1, result.getDirectRoutes().size());
        assertEquals(0, result.getChangeoverRoutes().size());
        assertEquals("Bucuresti Nord", result.getDirectRoutes().get(0).getFromCity());
        assertEquals("Brasov",         result.getDirectRoutes().get(0).getToCity());
    }

    @Test
    void search_stopsOnSameScheduleButWrongDirection_noDirectRoute() {
        LocalDateTime t = LocalDateTime.of(2026, 5, 15, 8, 0);

        ScheduleStop fromStop = mockStop(4, 1, "IR 1581", 4, "Brasov",         4, t, t);
        ScheduleStop toStop   = mockStop(1, 1, "IR 1581", 1, "Bucuresti Nord", 1, t, t);

        when(scheduleStopService.findByStationId(4)).thenReturn(List.of(fromStop));
        when(scheduleStopService.findByStationId(1)).thenReturn(List.of(toStop));
        when(scheduleStopService.findLaterStopsOnSchedule(anyInt(), anyInt())).thenReturn(List.of());

        RouteSearchResult result = routeSearchService.search(4, 1);

        assertEquals(0, result.getDirectRoutes().size());
        assertEquals(0, result.getChangeoverRoutes().size());
    }

    @Test
    void search_noSharedSchedule_emptyResult() {
        LocalDateTime t = LocalDateTime.of(2026, 5, 15, 8, 0);

        ScheduleStop fromStop = mockStop(1, 1, "IR 1581", 1, "Bucuresti Nord", 1, t, t);
        ScheduleStop toStop   = mockStop(9, 3, "IC 521",  5, "Sibiu",          2, t, t);

        when(scheduleStopService.findByStationId(1)).thenReturn(List.of(fromStop));
        when(scheduleStopService.findByStationId(5)).thenReturn(List.of(toStop));
        when(scheduleStopService.findLaterStopsOnSchedule(1, 1)).thenReturn(List.of());

        RouteSearchResult result = routeSearchService.search(1, 5);

        assertEquals(0, result.getDirectRoutes().size());
        assertEquals(0, result.getChangeoverRoutes().size());
    }

    @Test
    void search_changeoverRouteExists_returnsChangeoverRoute() {
        LocalDateTime depA  = LocalDateTime.of(2026, 5, 15, 8,  0);
        LocalDateTime arrB1 = LocalDateTime.of(2026, 5, 15, 9,  0);
        LocalDateTime depB2 = LocalDateTime.of(2026, 5, 15, 12, 0);
        LocalDateTime arrC  = LocalDateTime.of(2026, 5, 15, 17, 0);

        ScheduleStop stopA_s1  = mockStop(1, 1, "IR 1581", 1, "Bucuresti Nord", 1, depA,  depA);
        ScheduleStop stopB_s1  = mockStop(2, 1, "IR 1581", 2, "Brasov",         4, arrB1, arrB1);
        ScheduleStop stopB_s2  = mockStop(5, 2, "IC 521",  2, "Brasov",         1, depB2, depB2);
        ScheduleStop stopC_s2  = mockStop(7, 2, "IC 521",  3, "Cluj-Napoca",    3, arrC,  arrC);

        when(scheduleStopService.findByStationId(1)).thenReturn(List.of(stopA_s1));
        when(scheduleStopService.findByStationId(3)).thenReturn(List.of(stopC_s2));
        when(scheduleStopService.findLaterStopsOnSchedule(1, 1)).thenReturn(List.of(stopB_s1));
        when(scheduleStopService.findByStationId(2)).thenReturn(List.of(stopB_s2));

        RouteSearchResult result = routeSearchService.search(1, 3);

        assertEquals(0, result.getDirectRoutes().size());
        assertEquals(1, result.getChangeoverRoutes().size());
        assertEquals("Brasov",      result.getChangeoverRoutes().get(0).getChangeoverCity());
        assertEquals("IR 1581",     result.getChangeoverRoutes().get(0).getFirstLeg().getTrainNumber());
        assertEquals("IC 521",      result.getChangeoverRoutes().get(0).getSecondLeg().getTrainNumber());
        assertEquals("Cluj-Napoca", result.getChangeoverRoutes().get(0).getSecondLeg().getToCity());
    }

    @Test
    void search_changeoverDepartureNotAfterArrival_changeoverRejected() {
        LocalDateTime depA  = LocalDateTime.of(2026, 5, 15, 8,  0);
        LocalDateTime arrB1 = LocalDateTime.of(2026, 5, 15, 12, 0);
        LocalDateTime depB2 = LocalDateTime.of(2026, 5, 15, 11, 0);
        LocalDateTime arrC  = LocalDateTime.of(2026, 5, 15, 17, 0);

        ScheduleStop stopA_s1 = mockStop(1, 1, "IR 1581", 1, "Bucuresti Nord", 1, depA,  depA);
        ScheduleStop stopB_s1 = mockStop(2, 1, "IR 1581", 2, "Brasov",         4, arrB1, arrB1);
        ScheduleStop stopB_s2 = mockStop(5, 2, "IC 521",  2, "Brasov",         1, depB2, depB2);
        ScheduleStop stopC_s2 = mockStop(7, 2, "IC 521",  3, "Cluj-Napoca",    3, arrC,  arrC);

        when(scheduleStopService.findByStationId(1)).thenReturn(List.of(stopA_s1));
        when(scheduleStopService.findByStationId(3)).thenReturn(List.of(stopC_s2));
        when(scheduleStopService.findLaterStopsOnSchedule(1, 1)).thenReturn(List.of(stopB_s1));
        when(scheduleStopService.findByStationId(2)).thenReturn(List.of(stopB_s2));

        RouteSearchResult result = routeSearchService.search(1, 3);

        assertEquals(0, result.getDirectRoutes().size());
        assertEquals(0, result.getChangeoverRoutes().size());
    }
}
