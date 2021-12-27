package airShipping2;

import java.util.ArrayList;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.search.ServiceQuery;
import jadex.micro.annotation.Agent;

@Agent
public class PlaneAgent {
	@Agent
	private IInternalAccess agent;

	private IMapService mapService;

	private Airports airports = new Airports();

	private ArrayList<IVector2> route = new ArrayList<>();

	@OnInit
	public IFuture<Void> agentInit() {
		route.add(airports.getAirportC());
		route.add(airports.getAirportA());
		route.add(airports.getAirportB());

		ServiceQuery<IMapService> query = new ServiceQuery<IMapService>(IMapService.class);
		query.setScope(ServiceScope.PLATFORM);

		mapService = agent.getLocalService(query);

		IFuture<Void> done = mapService.createPlane("Plane-01");

		System.out.println("Plane Agent found: " + mapService);

		return done;
	}

	@OnStart
	public IFuture<Void> agentStart() {

		IResultListener<Void> rl = new IResultListener<Void>() {
			int i = 0;

			public void resultAvailable(Void done) {
				i = (i + 1) % route.size();
				IFuture<Void> arrived = mapService.setPlaneTarget(route.get(i));
				arrived.addResultListener(this);
				System.out.println("Plane has arrived at " + route.get(i - 1));
			}

			public void exceptionOccurred(Exception e) {
			}
		};

		IFuture<Void> arrived = mapService.setPlaneTarget(route.get(0));
		arrived.addResultListener(rl);

		return new Future<Void>();
	}
}
