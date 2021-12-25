package airShipping2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.time.Instant;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.micro.annotation.Agent;

@Agent(autoprovide = Boolean3.TRUE)
@Service
public class MapAgent implements IMapService {

	@Agent
	private IInternalAccess agent;

	private JFrame frame;

	private Plane plane;

	private IComponentStep<Void> simulationstep;

	private final IVector2 airportA = new Vector2Double(1.7, 7.9);
	private final IVector2 airportB = new Vector2Double(13.8, 2.5);
	private final IVector2 airportC = new Vector2Double(8.2, 11.7);

	@OnInit
	public IFuture<Void> agentInit() {
		Future<Void> done = new Future<Void>();

		SwingUtilities.invokeLater(() -> {
			frame = new JFrame("Environment Simulation v1.0");
			frame.setSize(600, 600);
			frame.setVisible(true);

			agent.getExternalAccess().scheduleStep(new IComponentStep<Void>() {
				public IFuture<Void> execute(IInternalAccess ia) {
					done.setResult(null);
					return IFuture.DONE;
				}
			});
		});

		return done;
	}

	@OnStart
	public IFuture<Void> agentRun() {

		System.out.println("Map Agent is running.");

		Timer swingtimer = new Timer(1, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (MapAgent.this) {
//					System.out.println("Plane: " + plane);
					Container pane = frame.getContentPane();

					// uncomment 2 next line, then comment the 3rd next line, then go to line 165
					BufferedImage img = new BufferedImage(pane.getWidth(), pane.getHeight(),
							BufferedImage.TYPE_4BYTE_ABGR_PRE);
					Graphics2D g = (Graphics2D) img.getGraphics();
					// Graphics2D g = (Graphics2D) frame.getContentPane().getGraphics();

					// grid
					g.setColor(Color.pink);
					g.fillRect(0, 0, pane.getWidth(), pane.getHeight());

					// departAirport (Airport A)
					double w1 = 75;
					double h1 = 50;
					double x1 = 1.7 * 30;
					x1 = x1 - (w1 / 2);
					double y1 = 7.9 * 30;
					y1 = y1 - (h1 / 2);
					Rectangle2D dAirportRec = new Rectangle2D.Double(x1, y1, w1, h1);
					g.setColor(Color.darkGray);
					g.fill(dAirportRec);
					// Depart airport text
					Font f1 = new Font("Arial", Font.PLAIN, 12);
					g.setColor(Color.black);
					g.setFont(f1);
					String s = "Airport A";
					g.drawString(s, (int) x1, (int) (y1 - 10));
					// capacity text
					g.setColor(Color.black);
					g.setFont(f1);
					String cap1 = "This is: " + (s);
					g.drawString(cap1, (int) x1, (int) (y1 + 70));

					// arrivalAirport (Airport B)
					double w2 = 75;
					double h2 = 50;
					double x2 = airportB.getXAsDouble() * 30;
					x2 = x2 - (w2 / 2);
					double y2 = airportB.getYAsDouble() * 30;
					y2 = y2 - (h2 / 2);
					Rectangle2D planeRecc = new Rectangle2D.Double(x2, y2, w2, h2);
					g.setColor(Color.yellow);
					g.fill(planeRecc);
					// Arrival airport text
					g.setColor(Color.black);
					g.setFont(f1);
					String s2 = "Airport B";
					g.drawString(s2, (int) x2, (int) (y2 - 10));
					// capacity text
					g.setColor(Color.black);
					g.setFont(f1);
					String cap2 = "This is: " + (s2);
					g.drawString(cap2, (int) x2, (int) (y2 + 70));

					// arrivalAirport (Airport C)
					double w3 = 75;
					double h3 = 50;
					double x3 = airportC.getXAsDouble() * 30;
					x3 = x3 - (w3 / 2);
					double y3 = airportC.getYAsDouble() * 30;
					y2 = y3 - (h2 / 2);
					Rectangle2D airportCRecc = new Rectangle2D.Double(x3, y3, w3, h3);
					g.setColor(Color.blue);
					g.fill(airportCRecc);
					// Arrival airport text
					g.setColor(Color.black);
					g.setFont(f1);
					String s3 = "Airport C";
					g.drawString(s3, (int) x3, (int) (y3 - 10));
					// capacity text
					g.setColor(Color.black);
					g.setFont(f1);
					String cap3 = "This is: " + (s3);
					g.drawString(cap3, (int) x3, (int) (y3 + 70));

					// Plane
					if (plane != null) {
						double w = 15;
						double h = 15;
						double x = plane.getCurrentPosition().getXAsDouble() * 30;
						x = x - (w / 2);
						double y = plane.getCurrentPosition().getYAsDouble() * 30;
						y = y - (h / 2);
						// double x = 13.8 -(w/2);
						// double y = 2.5 - (h/2);
						Ellipse2D planeRec = new Ellipse2D.Double(x, y, w, h);
						g.setColor(Color.red);
						g.fill(planeRec);
						// capacity text
						g.setColor(Color.black);
						g.setFont(f1);
						String capPlane = "Plane's is fully loaded: " + (plane.isFullyLoaded());
						g.drawString(capPlane, (int) x, (int) (y + 70));
					}
					// uncomment 2 next line
					Graphics2D gScreen = (Graphics2D) pane.getGraphics();
					gScreen.drawImage(img, 0, 0, null);
				}
			}
		});
		swingtimer.start();

		simulationstep = new IComponentStep<Void>() {
			public IFuture<Void> execute(IInternalAccess ia) {
				synchronized (MapAgent.this) {
					if (plane != null) {
						if (!plane.hasArrived()) {
							plane.updatePos(1);
						} else {
							if (plane.getTargetArrived() != null && !plane.getTargetArrived().isDone()) {
								plane.getTargetArrived().setResult(null);
								plane.setTargetArrived(null);
							}
						}
					}
					ia.scheduleStep(simulationstep);
				}
				return IFuture.DONE;
			}
		};
		agent.scheduleStep(simulationstep);

		return new Future<Void>();

	}

	public IFuture<Void> createPlane(String id) {

		synchronized (this) {
			plane = new Plane(id);
			System.out.println("A plane is created.");
		}

		return IFuture.DONE;
	};

	public IFuture<Void> setPlaneTarget(/* String id, */ IVector2 target) {
		Future<Void> ret = new Future<>();
		synchronized (this) {
//			Car car = cars.get(name);
			plane.setTarget(target);
			plane.setTargetArrived(ret);
		}

		return ret;
	}
}