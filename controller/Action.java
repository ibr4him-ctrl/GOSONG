package controller;

import model.chef.Chef;
import model.station.Station;

public interface Action {
    boolean execute(Chef chef, Station station);
}