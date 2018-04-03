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

import com.google.transit.realtime.GtfsRealtimeNYCT.TripReplacementPeriod;
import com.google.transit.realtime.GtfsRealtime.TimeRange;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Trip active on a service day.
 *
 * @author kurt
 */
public class ActivatedTrip {

  private static Logger _log = LoggerFactory.getLogger(ActivatedTrip.class);

  private final ServiceDate sd;
  private final Trip theTrip;
  private final NyctTripId parsedTripId;
  private long start;
  private long end;
  private List<StopTime> stopTimes;

  public ActivatedTrip(ServiceDate sd, Trip theTrip, List<StopTime> stopTimes) {
    this.sd = sd;
    this.theTrip = theTrip;
    this.parsedTripId = stopTimes.isEmpty() ? null : NyctTripId.buildFromGtfs(theTrip, stopTimes);
    int startSec = stopTimes.isEmpty() ? -1 : stopTimes.get(0).getDepartureTime();
    int endSec = stopTimes.isEmpty() ? -1 : stopTimes.get(stopTimes.size() - 1).getArrivalTime();
    this.start = sd.getAsDate().getTime()/1000 + startSec;
    this.end = sd.getAsDate().getTime()/1000 + endSec;
    this.stopTimes = stopTimes;
  }

  public ServiceDate getServiceDate() {
    return sd;
  }

  public Trip getTrip() {
    return theTrip;
  }

  public NyctTripId getParsedTripId() {
    return parsedTripId;
  }

  public long getEnd() {
    return end;
  }

  public long getStart() {
    return start;
  }

  public List<StopTime> getStopTimes() {
    return stopTimes;
  }

  public boolean activeFor(TripReplacementPeriod trp, long timestamp) {
    TimeRange tr = trp.getReplacementPeriod();
    long trStart = tr.hasStart() ? tr.getStart() : timestamp;
    long trEnd = tr.hasEnd() ? tr.getEnd() : timestamp;
    return getStart() < trEnd && getEnd() > trStart;
  }

  @Override
  public String toString() {
    return "ActivatedTrip{" + "sd=" + sd + ", theTrip=" + theTrip + '}';
  }

}
