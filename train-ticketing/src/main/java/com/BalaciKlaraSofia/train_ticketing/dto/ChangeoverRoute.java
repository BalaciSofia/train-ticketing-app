package com.BalaciKlaraSofia.train_ticketing.dto;

public class ChangeoverRoute {

    private DirectRoute firstLeg;
    private DirectRoute secondLeg;
    private String changeoverCity;

    public ChangeoverRoute(DirectRoute firstLeg, DirectRoute secondLeg, String changeoverCity) {
        this.firstLeg = firstLeg;
        this.secondLeg = secondLeg;
        this.changeoverCity = changeoverCity;
    }

    public DirectRoute getFirstLeg() { return firstLeg; }
    public DirectRoute getSecondLeg() { return secondLeg; }
    public String getChangeoverCity() { return changeoverCity; }
}
