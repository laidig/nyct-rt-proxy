/*
 * Copyright (C) 2017 Cambridge Systematics, Inc.
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

import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdateOrBuilder;
import org.onebusaway.gtfs.model.StopTime;

import java.util.ListIterator;

/**
 * Encapsulates the results of matching a RT TripUpdate.
 *
 * @author Simon Jacobs
 */
public class TripMatchResult implements Comparable<TripMatchResult> {

  /**
   * Possible statuses that a match can have.
   *
   * Ordered by goodness to make comparison easier.
   */

  private Status status;
  private ActivatedTrip result;
  private int delta; // lateness of RT trip relative to static trip
  private TripUpdateOrBuilder tripUpdate;
  private String rtTripId;

  public TripMatchResult(TripUpdateOrBuilder tripUpdate, Status status, ActivatedTrip result, int delta) {
    this.tripUpdate = tripUpdate;
    this.status = status;
    this.result = result;
    this.delta = delta;
    this.rtTripId = tripUpdate.getTrip().getTripId();
  }

  // strict match
  public TripMatchResult(TripUpdateOrBuilder tripUpdate, ActivatedTrip result) {
    this(tripUpdate, Status.STRICT_MATCH, result, 0);
  }

  // no match
  public TripMatchResult(TripUpdateOrBuilder tripUpdate, Status status) {
    this(tripUpdate, status, null, 0);
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public ActivatedTrip getResult() {
    return result;
  }

  public void setResult(ActivatedTrip result) {
    this.result = result;
  }

  public boolean hasResult() {
    return result != null;
  }

  // return negative number, 0, or positive number as this object is worse, equal or better than the other
  @Override
  public int compareTo(TripMatchResult other) {
    if (this.status.equals(Status.LOOSE_MATCH_COERCION) && other.status.equals(Status.LOOSE_MATCH_COERCION))
      return other.delta - delta; // flip because smaller is better
    else
      return status.compareTo(other.status);
  }

  // Create TripMatchResult that's a loose match. We expect that it can either be coerced or on a different service day,
  // but not both.
  public static TripMatchResult looseMatch(TripUpdateOrBuilder tripUpdate, ActivatedTrip at, int delta, boolean onServiceDay) {
    Status status = Status.LOOSE_MATCH;
    if (delta > 0)
      status = Status.LOOSE_MATCH_COERCION;
    if (!onServiceDay)
      status = Status.LOOSE_MATCH_ON_OTHER_SERVICE_DATE;
    return new TripMatchResult(tripUpdate, status, at, delta);
  }

  public TripUpdateOrBuilder getTripUpdate() {
    return tripUpdate;
  }

  public TripUpdate.Builder getTripUpdateBuilder() {
    if (tripUpdate instanceof TripUpdate.Builder)
      return (TripUpdate.Builder) tripUpdate;
    return TripUpdate.newBuilder((TripUpdate) tripUpdate);
  }

  /**
   * Check that the last stop of TU is same as last stop of static trip.
   *
   * This test could happen in LazyTripMatcher, except that we need these matches
   * in order to merge trips with mid-line relief.
   *
   * @return true if last static stop and RT stop match, false otherwise
   */
  public boolean lastStopMatches() {
    if (!hasResult())
      throw new IllegalArgumentException("Cannot call lastStopMatches on a match result without an ActivatedTrip");
    String staticStop = getStaticLastStop();
    String rtStop = getRtLastStop();
    return staticStop.equals(rtStop);
  }

  /**
   * Check that the stops form the first STU in the TU to the static trip match.
   */
  public boolean stopsMatchToEnd() {
    if (!hasResult())
      throw new IllegalArgumentException("Cannot call lastStopMatches on a match result without an ActivatedTrip");
    // Step through in reverse
    ListIterator<StopTime> stopTimes = result.getStopTimes().listIterator(result.getStopTimes().size());
    ListIterator<TripUpdate.StopTimeUpdate> stopUpdates = tripUpdate.getStopTimeUpdateList()
            .listIterator(tripUpdate.getStopTimeUpdateCount());
    while(stopUpdates.hasPrevious()) {
      if (!stopTimes.hasPrevious()) {
        return false;
      }
      String rtStop = stopUpdates.previous().getStopId();
      String staticStop = stopTimes.previous().getStop().getId().getId();
      if (!rtStop.equals(staticStop)) {
        return false;
      }
    }
    return true;
  }

  public String getTripId() {
    return hasResult() ? result.getTrip().getId().getId() : tripUpdate.getTrip().getTripId();
  }

  public String getStaticLastStop() {
    return result.getStopTimes().get(result.getStopTimes().size() - 1).getStop().getId().getId();
  }

  public String getRtLastStop() {
    return tripUpdate.getStopTimeUpdate(tripUpdate.getStopTimeUpdateCount() - 1).getStopId();
  }

  public String getRtTripId() {
    return rtTripId;
  }
}
