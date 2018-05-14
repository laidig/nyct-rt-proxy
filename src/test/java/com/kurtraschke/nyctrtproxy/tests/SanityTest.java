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
package com.kurtraschke.nyctrtproxy.tests;

import com.google.inject.Inject;
import com.google.transit.realtime.GtfsRealtime.*;
import com.kurtraschke.nyctrtproxy.model.MatchMetrics;
import com.kurtraschke.nyctrtproxy.services.TripUpdateProcessor;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class SanityTest extends RtTestRunner {

  private static final Logger _log = LoggerFactory.getLogger(SanityTest.class);

  @Inject
  protected TripUpdateProcessor _processor;

  @Inject
  private GtfsRelationalDao _dao;

  protected void test(int feedId, String protobuf, int nScheduledExpected, int nAddedExpected) throws Exception {
    FeedMessage msg = readFeedMessage(protobuf);
    List<TripUpdate> updates = _processor.processFeed(feedId, msg, new MatchMetrics());

    int nScheduled = 0, nAdded = 0, nRt = 0, nDuplicates = 0;

    Set<String> tripIds = new HashSet<>();

    for (TripUpdate tripUpdate : updates) {
      switch(tripUpdate.getTrip().getScheduleRelationship()) {
        case SCHEDULED:
          checkScheduledTrip(tripUpdate);
          nScheduled++;
          nRt++;
          break;
        case ADDED:
          checkAddedTrip(tripUpdate);
          nAdded++;
          nRt++;
          break;
        default:
          throw new Exception("unexpected schedule relationship");
      }
      String tripId = tripUpdate.getTrip().getTripId();
      if (tripIds.contains(tripId))
        nDuplicates++;
      tripIds.add(tripId);
    }

    _log.info("nScheduled={}, nAdded={}, nDuplicates={}", nScheduled, nAdded, nDuplicates);
    // make sure we have improved or stayed the same
    assertTrue(nScheduled >= nScheduledExpected);
    assertTrue(nAdded <= nAddedExpected);

    // if improved:
    if (nScheduled != nScheduledExpected || nAdded != nAddedExpected) {
      _log.info("Better than expected, could update test.");
    }
  }

  private void checkScheduledTrip(TripUpdate tripUpdate) {
    Trip trip = getTrip(tripUpdate);
    assertNotNull(trip);
    assertEquals(tripUpdate.getTrip().getRouteId(), trip.getRoute().getId().getId());

    List<TripUpdate.StopTimeUpdate> stus = tripUpdate.getStopTimeUpdateList();
    assertFalse(stus.isEmpty());

    Set<String> stopIds = _dao.getStopTimesForTrip(trip)
            .stream()
            .map(st -> st.getStop().getId().getId())
            .collect(Collectors.toSet());

    for (TripUpdate.StopTimeUpdate stu : stus) {
      assertTrue(stopIds.contains(stu.getStopId()));
    }

  }

  private void checkAddedTrip(TripUpdate tripUpdate) {
    Trip trip = getTrip(tripUpdate);
    assertNull(trip);
  }

}
