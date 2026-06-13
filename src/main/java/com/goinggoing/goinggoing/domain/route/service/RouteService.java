package com.goinggoing.goinggoing.domain.route.service;

import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.route.dto.RouteGenerateRequest;
import com.goinggoing.goinggoing.domain.route.dto.RoutePlaceResponse;
import com.goinggoing.goinggoing.domain.route.dto.RouteResponse;
import com.goinggoing.goinggoing.domain.route.entity.Route;
import com.goinggoing.goinggoing.domain.route.entity.RoutePlace;
import com.goinggoing.goinggoing.domain.route.repository.RouteRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RouteService {

	private final RouteRepository routeRepository;
	private final ContentRepository contentRepository;

	public RouteService(RouteRepository routeRepository, ContentRepository contentRepository) {
		this.routeRepository = routeRepository;
		this.contentRepository = contentRepository;
	}

	public RouteResponse generateRoute(RouteGenerateRequest request) {
		contentRepository.findPublishedContent(request.contentId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

		Route route = routeRepository.findFirstByContentIdAndTripDurationType(
						request.contentId(),
						request.tripDurationType()
				)
				.orElseThrow(() -> new BusinessException(ErrorCode.ROUTE_NOT_FOUND));

		return toResponse(route);
	}

	public RouteResponse getRouteDetail(Long routeId) {
		Route route = routeRepository.findPublishedRoute(routeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ROUTE_NOT_FOUND));
		return toResponse(route);
	}

	public List<RouteResponse> getRoutesByRegion(Long regionId) {
		return routeRepository.findPublishedRoutesByRegionId(regionId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private RouteResponse toResponse(Route route) {
		return new RouteResponse(
				route.getId(),
				route.getContent().getId(),
				route.getTitle(),
				route.getSummary(),
				route.getTripDurationType(),
				route.getTotalDistanceMeters(),
				route.getTotalDurationMinutes(),
				route.getMapCenterLatitude(),
				route.getMapCenterLongitude(),
				route.getPlaces().stream()
						.sorted(Comparator.comparing(RoutePlace::getDayNumber).thenComparing(RoutePlace::getVisitOrder))
						.map(this::toPlaceResponse)
						.toList()
		);
	}

	private RoutePlaceResponse toPlaceResponse(RoutePlace place) {
		return new RoutePlaceResponse(
				place.getId(),
				place.getContent() == null ? null : place.getContent().getId(),
				place.getName(),
				place.getPlaceType(),
				place.getAddress(),
				place.getLatitude(),
				place.getLongitude(),
				place.getDayNumber(),
				place.getVisitOrder(),
				place.getEstimatedStayMinutes(),
				place.getMoveMinutesFromPrevious(),
				place.getRecommendationNote()
		);
	}
}
