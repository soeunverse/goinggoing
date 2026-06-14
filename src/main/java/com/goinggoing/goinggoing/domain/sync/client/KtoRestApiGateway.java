package com.goinggoing.goinggoing.domain.sync.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "kto.sync.enabled", havingValue = "true")
public class KtoRestApiGateway implements KtoApiGateway {

	private final RestClient restClient;
	private final ObjectMapper objectMapper;
	private final String serviceKey;
	private final String regionalDemandBaseUrl;
	private final String relatedPlaceBaseUrl;

	public KtoRestApiGateway(
			RestClient.Builder restClientBuilder,
			ObjectMapper objectMapper,
			@Value("${kto.service-key}") String serviceKey,
			@Value("${kto.regional-demand.base-url:https://apis.data.go.kr/B551011/AreaTarResDemService}") String regionalDemandBaseUrl,
			@Value("${kto.related-place.base-url:https://apis.data.go.kr/B551011/TarRlteTarService1}") String relatedPlaceBaseUrl
	) {
		this.restClient = restClientBuilder.build();
		this.objectMapper = objectMapper;
		this.serviceKey = serviceKey;
		this.regionalDemandBaseUrl = regionalDemandBaseUrl;
		this.relatedPlaceBaseUrl = relatedPlaceBaseUrl;
	}

	@Override
	public List<JsonNode> fetchItems(KtoEndpoint endpoint, Map<String, String> queryParameters) {
		String responseBody = restClient.get()
				.uri(uriBuilder -> buildUri(uriBuilder, endpoint, queryParameters))
				.retrieve()
				.body(String.class);
		return extractItems(responseBody);
	}

	private URI buildUri(UriBuilder uriBuilder, KtoEndpoint endpoint, Map<String, String> queryParameters) {
		UriBuilder builder = uriBuilder
				.scheme("https")
				.host(resolveHost(endpoint))
				.path(resolvePathPrefix(endpoint))
				.path(endpoint.getPath())
				.queryParam("serviceKey", serviceKey)
				.queryParam("numOfRows", queryParameters.getOrDefault("numOfRows", "100"))
				.queryParam("pageNo", queryParameters.getOrDefault("pageNo", "1"))
				.queryParam("_type", "json");

		queryParameters.forEach((key, value) -> {
			if (!"numOfRows".equals(key) && !"pageNo".equals(key) && value != null && !value.isBlank()) {
				builder.queryParam(key, value);
			}
		});
		return builder.build();
	}

	private String resolveHost(KtoEndpoint endpoint) {
		String baseUrl = resolveBaseUrl(endpoint).replace("https://", "").replace("http://", "");
		int slashIndex = baseUrl.indexOf('/');
		if (slashIndex < 0) {
			return baseUrl;
		}
		return baseUrl.substring(0, slashIndex);
	}

	private String resolvePathPrefix(KtoEndpoint endpoint) {
		String baseUrl = resolveBaseUrl(endpoint).replace("https://", "").replace("http://", "");
		int slashIndex = baseUrl.indexOf('/');
		if (slashIndex < 0) {
			return "";
		}
		return "/" + baseUrl.substring(slashIndex + 1);
	}

	private String resolveBaseUrl(KtoEndpoint endpoint) {
		if (endpoint == KtoEndpoint.RELATED_PLACE_KEYWORD) {
			return relatedPlaceBaseUrl;
		}
		return regionalDemandBaseUrl;
	}

	private List<JsonNode> extractItems(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode itemNode = root.path("response").path("body").path("items").path("item");
			if (itemNode.isMissingNode()) {
				itemNode = root.path("body").path("items").path("item");
			}
			if (itemNode.isArray()) {
				List<JsonNode> items = new ArrayList<>();
				Iterator<JsonNode> iterator = itemNode.elements();
				while (iterator.hasNext()) {
					items.add(iterator.next());
				}
				return items;
			}
			if (itemNode.isObject()) {
				return List.of(itemNode);
			}
			return List.of();
		} catch (Exception exception) {
			throw new IllegalStateException("한국관광공사 API 응답 파싱에 실패했습니다.", exception);
		}
	}
}
