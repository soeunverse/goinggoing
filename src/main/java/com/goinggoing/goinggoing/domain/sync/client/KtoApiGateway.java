package com.goinggoing.goinggoing.domain.sync.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface KtoApiGateway {

	List<JsonNode> fetchItems(KtoEndpoint endpoint, Map<String, String> queryParameters);
}
