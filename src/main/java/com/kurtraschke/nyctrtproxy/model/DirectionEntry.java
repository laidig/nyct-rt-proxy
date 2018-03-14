/*
 * Copyright (C) 2015 Kurt Raschke <kurt@kurtraschke.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kurtraschke.nyctrtproxy.model;

import org.onebusaway.csv_entities.schema.annotations.CsvField;
import org.onebusaway.csv_entities.schema.annotations.CsvFields;

@CsvFields(filename = "directions.csv")
public class DirectionEntry {

    @CsvField(name="Station ID")
    private String stationId;

    @CsvField(name="GTFS Stop ID")
    private String gtfsStopId;

    @CsvField(name="Line")
    private String line;

    @CsvField(name="Stop Name")
    private String stopName;

    @CsvField(name="Daytime Routes")
    private String daytimeRoutes;

    @CsvField(name="Railroad north descriptor", optional = true)
    private String railroadNorthDescriptor;

    @CsvField(name="Railroad south descriptor", optional = true)
    private String railroadSouthDescriptor;

    @CsvField(name="Notes", ignore=true)
    private String notes;

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getGtfsStopId() {
        return gtfsStopId;
    }

    public void setGtfsStopId(String gtfsStopId) {
        this.gtfsStopId = gtfsStopId;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getDaytimeRoutes() {
        return daytimeRoutes;
    }

    public void setDaytimeRoutes(String daytimeRoutes) {
        this.daytimeRoutes = daytimeRoutes;
    }

    public String getRailroadNorthDescriptor() {
        return railroadNorthDescriptor;
    }

    public void setRailroadNorthDescriptor(String railroadNorthDescriptor) {
        this.railroadNorthDescriptor = railroadNorthDescriptor;
    }

    public String getRailroadSouthDescriptor() {
        return railroadSouthDescriptor;
    }

    public void setRailroadSouthDescriptor(String railroadSouthDescriptor) {
        this.railroadSouthDescriptor = railroadSouthDescriptor;
    }
}
