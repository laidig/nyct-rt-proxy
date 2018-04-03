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
package com.kurtraschke.nyctrtproxy.services;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.kurtraschke.nyctrtproxy.model.ActivatedTrip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.inject.Inject;
import javax.inject.Named;

/**
 * Find currently-active trips for a given time. (Only needed for ActivatedTripMatcher)
 *
 * @author kurt
 */
public class TripActivator {

  private CalendarServiceData _csd;

  private GtfsRelationalDao _dao;

  private String _agencyId = "MTA NYCT";

  private static final Logger _log = LoggerFactory.getLogger(TripActivator.class);

  @Inject
  public void setCalendarServiceData(CalendarServiceData csd) {
    _csd = csd;
  }
  
  @Inject(optional = true)
  public void setAgencyMatchId(@Named("NYCT.gtfsAgency") String agencyid) {
	  _agencyId = agencyid;
	  _log.info("Using AgencyId "+_agencyId);
  }

  @Inject
  public void setGtfsRelationalDao(GtfsRelationalDao dao) {
    _dao = dao;
  }

  public Stream<ActivatedTrip> getTripsForRangeAndRoutes(Date start, Date end, Set<String> routeIds) {
    List<ActivatedTrip> trips = new ArrayList<>();
    ServiceDate startDate = new ServiceDate(start);
    for (ServiceDate sd : Arrays.asList(startDate.previous(), startDate, startDate.next())) {
        Set<AgencyAndId> serviceIdsForDate = _csd.getServiceIdsForDate(sd);

        int sdOrigin = (int) (sd.getAsCalendar(_csd.getTimeZoneForAgencyId(_agencyId)).getTimeInMillis() / 1000);

        int startTime = (int) ((start.getTime() / 1000) - sdOrigin);
        int endTime = (int) ((end.getTime() / 1000) - sdOrigin);

        for (Trip trip : _dao.getAllTrips()) {
            if (routeIds.contains(trip.getRoute().getId().getId())
                && serviceIdsForDate.contains(trip.getServiceId())) {
                List<StopTime> stopTimes = _dao.getStopTimesForTrip(trip);
                if (stopTimes.isEmpty())
                    continue;
                int tripStart = stopTimes.get(0).getDepartureTime();
                int tripEnd = stopTimes.get(stopTimes.size() - 1).getArrivalTime();
                if (tripEnd >= startTime && tripStart <= endTime) {
                    trips.add(new ActivatedTrip(sd, trip, stopTimes));
                }
            }
        }
    }
    return trips.stream();
  }

  public Stream<ActivatedTrip> getTripsForRangeAndRoute(Date start, Date end, String routeId) {
    return getTripsForRangeAndRoutes(start, end, ImmutableSet.of(routeId));
  }

  public boolean isStopInStaticData(String stop) {
      return _dao.getStopForId(new AgencyAndId(_agencyId, stop)) != null;
  }

  public String getStopNameForId(String stop) {
      return _dao.getStopForId(new AgencyAndId(_agencyId, stop)).getName();
  }

}
