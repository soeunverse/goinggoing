package com.goinggoing.goinggoing.domain.sync.client;

public interface ExternalDataSyncClient {

	ExternalSyncResult syncContents();

	ExternalSyncResult syncRelatedPlaces();

	ExternalSyncResult syncRegionalDemand();
}
