package com.goinggoing.goinggoing.domain.sync.client;

public interface ExternalDataSyncClient {

	ExternalSyncResult syncRelatedPlaces();

	ExternalSyncResult syncRegionalDemand();
}
