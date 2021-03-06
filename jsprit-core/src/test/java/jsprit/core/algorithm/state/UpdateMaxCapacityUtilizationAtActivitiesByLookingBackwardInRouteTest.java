package jsprit.core.algorithm.state;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import jsprit.core.problem.Capacity;
import jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.job.Delivery;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.job.Pickup;
import jsprit.core.problem.solution.route.ReverseRouteActivityVisitor;
import jsprit.core.problem.solution.route.RouteActivityVisitor;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.state.StateFactory;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleType;

import org.junit.Before;
import org.junit.Test;

public class UpdateMaxCapacityUtilizationAtActivitiesByLookingBackwardInRouteTest {
	
	private Vehicle vehicle;
	
	@Before
	public void doBefore(){
		vehicle = mock(Vehicle.class);
		VehicleType type = mock(VehicleType.class);
		when(type.getCapacityDimensions()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 10000)
				.addDimension(1, 10000)
				.addDimension(2, 10000)
				.addDimension(3, 10000)
				.addDimension(4, 10000)
				.addDimension(5, 10000).build());
		when(vehicle.getType()).thenReturn(type);
	}
	
	
	@Test
	public void whenVehicleRouteHasPickupAndDelivery_maxLoadAtEachActivityShouldBeCalculatedCorrectly(){
		StateManager stateManager = new StateManager(mock(VehicleRoutingTransportCosts.class));
		UpdateLoads updateLoad = new UpdateLoads(stateManager);
		UpdateMaxCapacityUtilisationAtActivitiesByLookingBackwardInRoute updateMaxLoad = new UpdateMaxCapacityUtilisationAtActivitiesByLookingBackwardInRoute(stateManager);
		
		RouteActivityVisitor routeActivityVisitor = new RouteActivityVisitor();
		routeActivityVisitor.addActivityVisitor(updateLoad);
		routeActivityVisitor.addActivityVisitor(updateMaxLoad);
		
		Pickup pickup = mock(Pickup.class);
		when(pickup.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 1).build());
		
		Delivery delivery = mock(Delivery.class);
		when(delivery.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 10).build());
		
		VehicleRoute route = VehicleRoute.Builder.newInstance(vehicle, mock(Driver.class))
				.addService(pickup).addService(delivery).build();
		
		updateLoad.informInsertionStarts(Arrays.asList(route), Collections.<Job>emptyList());
		routeActivityVisitor.visit(route);
		
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(0), StateFactory.PAST_MAXLOAD, Capacity.class).get(0));
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(1), StateFactory.PAST_MAXLOAD, Capacity.class).get(0));
	}
	
	@Test
	public void whenVehicleRouteHasPickupAndDeliveryWithMultipleCapDims_futureMaxLoadAtEachActivityShouldBeCalculatedCorrectly(){
		StateManager stateManager = new StateManager(mock(VehicleRoutingTransportCosts.class));
		UpdateLoads updateLoad = new UpdateLoads(stateManager);
		UpdateMaxCapacityUtilisationAtActivitiesByLookingBackwardInRoute updateMaxLoad = new UpdateMaxCapacityUtilisationAtActivitiesByLookingBackwardInRoute(stateManager);
		
		RouteActivityVisitor routeActivityVisitor = new RouteActivityVisitor();
		routeActivityVisitor.addActivityVisitor(updateLoad);
		routeActivityVisitor.addActivityVisitor(updateMaxLoad);
		
		Pickup pickup = mock(Pickup.class);
		when(pickup.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 1)
				.addDimension(1, 5).build());
		
		Delivery delivery = mock(Delivery.class);
		when(delivery.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 10)
				.addDimension(1, 3).build());
		
		VehicleRoute route = VehicleRoute.Builder.newInstance(vehicle, mock(Driver.class))
				.addService(pickup).addService(delivery).build();
		
		updateLoad.informInsertionStarts(Arrays.asList(route), Collections.<Job>emptyList());
		routeActivityVisitor.visit(route);
		
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(0), StateFactory.PAST_MAXLOAD, Capacity.class).get(0),0.1);
		assertEquals(8,stateManager.getActivityState(route.getActivities().get(0), StateFactory.PAST_MAXLOAD, Capacity.class).get(1),0.1);
		
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(1), StateFactory.PAST_MAXLOAD, Capacity.class).get(0),0.1);
		assertEquals(8,stateManager.getActivityState(route.getActivities().get(1), StateFactory.PAST_MAXLOAD, Capacity.class).get(1),0.1);
	}
	
	@Test
	public void whenVehicleRouteHasPickupAndDeliveryAndPickupWithMultipleCapDims_futureMaxLoadAtEachActivityShouldBeCalculatedCorrectly(){
		StateManager stateManager = new StateManager(mock(VehicleRoutingTransportCosts.class));
		UpdateLoads updateLoad = new UpdateLoads(stateManager);
		UpdateMaxCapacityUtilisationAtActivitiesByLookingBackwardInRoute updateMaxLoad = new UpdateMaxCapacityUtilisationAtActivitiesByLookingBackwardInRoute(stateManager);
		
		RouteActivityVisitor routeActivityVisitor = new RouteActivityVisitor();
		routeActivityVisitor.addActivityVisitor(updateLoad);
		routeActivityVisitor.addActivityVisitor(updateMaxLoad);
		
		Pickup pickup = mock(Pickup.class);
		when(pickup.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 1)
				.addDimension(1, 5).build());
		
		Delivery delivery = mock(Delivery.class);
		when(delivery.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 10)
				.addDimension(1, 3).build());
		
		Pickup pickup2 = mock(Pickup.class);
		when(pickup2.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 3)
				.addDimension(1, 8).build());
		
		VehicleRoute route = VehicleRoute.Builder.newInstance(vehicle, mock(Driver.class))
				.addService(pickup).addService(delivery).addService(pickup2).build();
		
		updateLoad.informInsertionStarts(Arrays.asList(route), Collections.<Job>emptyList());
		routeActivityVisitor.visit(route);
		
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(0), StateFactory.PAST_MAXLOAD, Capacity.class).get(0));
		assertEquals(8,stateManager.getActivityState(route.getActivities().get(0), StateFactory.PAST_MAXLOAD, Capacity.class).get(1));
		
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(1), StateFactory.PAST_MAXLOAD, Capacity.class).get(0));
		assertEquals(8,stateManager.getActivityState(route.getActivities().get(1), StateFactory.PAST_MAXLOAD, Capacity.class).get(1));
		
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(2), StateFactory.PAST_MAXLOAD, Capacity.class).get(0));
		assertEquals(13,stateManager.getActivityState(route.getActivities().get(2), StateFactory.PAST_MAXLOAD, Capacity.class).get(1));
		
	}
	
	@Test
	public void whenVehicleRouteHasPickupAndDeliveryAndPickupWithMultipleCapDims_futureMaxLoadAtEachActivityShouldBeCalculatedCorrectly_v2(){
		StateManager stateManager = new StateManager(mock(VehicleRoutingTransportCosts.class));
		UpdateLoads updateLoad = new UpdateLoads(stateManager);
		UpdateMaxCapacityUtilisationAtActivitiesByLookingForwardInRoute updateMaxLoad = new UpdateMaxCapacityUtilisationAtActivitiesByLookingForwardInRoute(stateManager);
		
		RouteActivityVisitor routeActivityVisitor = new RouteActivityVisitor();
		routeActivityVisitor.addActivityVisitor(updateLoad);
		
		ReverseRouteActivityVisitor revRouteActivityVisitor = new ReverseRouteActivityVisitor();
		revRouteActivityVisitor.addActivityVisitor(updateMaxLoad);
		
		Pickup pickup = mock(Pickup.class);
		when(pickup.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 1)
				.addDimension(1, 5).build());
		
		Delivery delivery = mock(Delivery.class);
		when(delivery.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 10)
				.addDimension(1, 3).build());
		
		Pickup pickup2 = mock(Pickup.class);
		when(pickup2.getSize()).thenReturn(Capacity.Builder.newInstance().addDimension(0, 3)
				.addDimension(1, 8).addDimension(4, 29).build());
		
		VehicleRoute route = VehicleRoute.Builder.newInstance(vehicle, mock(Driver.class))
				.addService(pickup).addService(delivery).addService(pickup2).build();
		
		updateLoad.informInsertionStarts(Arrays.asList(route), Collections.<Job>emptyList());
		routeActivityVisitor.visit(route);
		revRouteActivityVisitor.visit(route);
		
		assertEquals(11,stateManager.getActivityState(route.getActivities().get(0), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(0));
		assertEquals(13,stateManager.getActivityState(route.getActivities().get(0), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(1));
		assertEquals(29,stateManager.getActivityState(route.getActivities().get(0), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(4));
		
		assertEquals(4,stateManager.getActivityState(route.getActivities().get(1), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(0));
		assertEquals(13,stateManager.getActivityState(route.getActivities().get(1), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(1));
		assertEquals(29,stateManager.getActivityState(route.getActivities().get(1), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(4));
		
		assertEquals(4,stateManager.getActivityState(route.getActivities().get(2), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(0));
		assertEquals(13,stateManager.getActivityState(route.getActivities().get(2), StateFactory.FUTURE_MAXLOAD, Capacity.class).get(1));
		
		
	}
}
