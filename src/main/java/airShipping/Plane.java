package airShipping;

import java.util.Random;

import jadex.commons.future.Future;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

public class Plane {

	private final String id;
	private IVector2 startAirport = new Vector2Double(0.0, 0.0);
	private IVector2 currentPosition = new Vector2Double(0.0, 0.0);
	private IVector2 target = new Vector2Double(0.0, 0.0);
	private double speed;
	private boolean fullyLoaded;

	enum PlaneDirection {
		NONE, RIGHTDOWN, RIGHTUP, LEFTUP, LEFTDOWN
	}

	private PlaneDirection myDirection;
	private Future<Void> targetArrived;
	private int capacity;
	private int numberOfParcelsLoaded;
	private int occupacyRate;
	private double accumulatedProfit;
	private int numberOfParcelsUnloaded;

	public Plane(String id, IVector2 startPosition, int capacity) {
		this.id = id;
		this.startAirport = startPosition.copy();
		this.currentPosition = startPosition;
		this.speed = 0.0;
		this.myDirection = PlaneDirection.NONE;
		this.fullyLoaded = false;
		this.capacity = capacity;
		this.numberOfParcelsLoaded = 0;
		this.accumulatedProfit = 0.0;
	}

	public double getCurrentPositionX(IVector2 currentPosition) {
		double posX = this.currentPosition.getXAsDouble();
		return posX;
	}

	public double getCurrentPositionY(IVector2 currentPosition) {
		double posY = this.currentPosition.getYAsDouble();
		return posY;
	}

	public void updatePos(double deltaseconds) {
		boolean stopped = this.checkStopped(getSpeed());
		double currentPosX = this.getCurrentPosition().getXAsDouble();
		double currentPosY = this.getCurrentPosition().getYAsDouble();
		double arrivalAirportPosX = target.getXAsDouble();
		double arrivalAirportPosY = target.getYAsDouble();
		IVector2 newVector = target.copy().subtract(currentPosition);
		IVector2 newUnitVector = newVector.normalize();

		switch (myDirection) {
		case RIGHTUP:
			if ((currentPosX >= arrivalAirportPosX) && (currentPosY <= arrivalAirportPosY)) {
				if (!stopped) {
					this.stopPlane();
					break;
				}
			} else {
				this.movePlane(newUnitVector);
				break;
			}
		case RIGHTDOWN:
			if ((currentPosX >= arrivalAirportPosX) && (currentPosY >= arrivalAirportPosY)) {
				if (!stopped) {
					this.stopPlane();
					break;
				}
			} else {
				this.movePlane(newUnitVector);
				break;
			}
		case LEFTUP:
			if ((currentPosX <= arrivalAirportPosX) && (currentPosY <= arrivalAirportPosY)) {
				if (!stopped) {
					this.stopPlane();
					break;
				}
			} else {
				this.movePlane(newUnitVector);
				break;
			}
		case LEFTDOWN:
			if ((currentPosX <= arrivalAirportPosX) && (currentPosY >= arrivalAirportPosY)) {
				if (!stopped) {
					this.stopPlane();
					break;
				}
			} else {
				this.movePlane(newUnitVector);
				break;
			}
		default:
			break;
		}
	}

	public PlaneDirection getPlaneDirection() {
		IVector2 newVector = target.copy().subtract(currentPosition);
		double newVectorX = newVector.getXAsDouble();
		double newVectorY = newVector.getYAsDouble();
		PlaneDirection myDirection = PlaneDirection.NONE;
		if (newVectorX >= 0 && newVectorY <= 0) {
			myDirection = PlaneDirection.RIGHTUP;
		}
		if (newVectorX >= 0 && newVectorY >= 0) {
			myDirection = PlaneDirection.RIGHTDOWN;
		}
		if (newVectorX <= 0 && newVectorY <= 0) {
			myDirection = PlaneDirection.LEFTUP;
		}
		if (newVectorX <= 0 && newVectorY >= 0) {
			myDirection = PlaneDirection.LEFTDOWN;
		}
		return myDirection;
	}

	public void stopPlane() {
		this.speed = 0.0;
		this.setCurrentPosition(target);
		System.out.println("Arrived at position: " + this.getCurrentPosition());
		System.out.println("Speed: " + this.getSpeed());
	}

	public void movePlane(IVector2 newUnitVector) {
		this.setSpeed(0.005);
		System.out.println("Plane: " + this.getId() + " current Position: " + currentPosition);
		System.out.println("Speed: " + this.getSpeed() + "\n----");
		IVector2 positionDelta = newUnitVector.multiply(speed);
		currentPosition = currentPosition.add(positionDelta);
	}

	public boolean checkStopped(double currentSpeed) {
		if (currentSpeed == 0.0) {
			return true;
		} else {
			return false;
		}
	}

	public void loadParcel() {
		while (!(numberOfParcelsLoaded == this.capacity)) {
			numberOfParcelsLoaded += 1;
			this.setOccupacyRate(numberOfParcelsLoaded);
			System.out.println("Plane: " + id + " has loaded " + numberOfParcelsLoaded + " parcels.");
		}
		if (numberOfParcelsLoaded == this.capacity) {
			this.fullyLoaded = true;
			this.setOccupacyRate(this.capacity);
		}
		System.out.println("plane is fully loaded: " + this.fullyLoaded);
	}

	public void unloadParcel() {
		int min = 0;
		int max = this.getNumberOfParcelsLoaded();
		Random random = new Random();
		int random_int = random.nextInt(max - min) + min + 1;

		numberOfParcelsLoaded -= random_int;
		if (!(this.getNumberOfParcelsLoaded() == this.getCapacity())) {
			this.fullyLoaded = false;
			this.setOccupacyRate(numberOfParcelsLoaded);
			System.out.println("Plane: " + id + " has unloaded " + random_int + " parcels. \n plane is fully loaded: "
					+ this.fullyLoaded);
			this.setNumberOfParcelsUnloaded(random_int);
		}
	}

	public void calculateAccumulatedProfit() {
		double flightDistance = this.getStartAirport().getDistance(target).getAsDouble();
		double profitPerFlight = 0;
		if (this.capacity == 100) {
			double profitPerParcel = 7 - 3; // Assumed shipping fee (7 €) and cost(3 €) per parcel, per unit of
											// distance of express route
			profitPerFlight = flightDistance * this.getNumberOfParcelsUnloaded() * profitPerParcel;
		}
		if (this.capacity == 5000) {
			double profitPerParcel = 3 - 2; // Assumed shipping fee (3 €) and cost(2 €) per parcel, per unit of
											// distance of normal route
			profitPerFlight = flightDistance * this.getNumberOfParcelsUnloaded() * profitPerParcel;
		}
		accumulatedProfit += profitPerFlight;
		this.setAccumulatedProfit(accumulatedProfit);
	}

	public IVector2 getCurrentPosition() {
		return currentPosition;
	}

	public IVector2 setCurrentPosition(IVector2 currentPosition) {
		this.currentPosition = currentPosition;
		return currentPosition;
	}

	public PlaneDirection getMyDirection() {
		return myDirection;
	}

	public void setMyDirection(PlaneDirection myDirection) {
		this.myDirection = myDirection;
	}

	public IVector2 getTarget() {
		return target;
	}

	public void setTarget(IVector2 target) {
		this.target = target;
		this.setMyDirection(this.getPlaneDirection());
	}

	public Future<Void> getTargetArrived() {
		return targetArrived;
	}

	public void setTargetArrived(Future<Void> targetArrived) {
		this.targetArrived = targetArrived;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	};

	public double getSpeed() {
		return speed;
	};

	public boolean hasArrived() {
		return currentPosition.equals(this.target);
	}

	public boolean isFullyLoaded() {
		return fullyLoaded;
	}

	public String getId() {
		return id;
	}

	public int getNumberOfParcelsLoaded() {
		return numberOfParcelsLoaded;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getOccupacyRate() {
		return occupacyRate;
	}

	public void setOccupacyRate(int numberOfParcelsLoaded) {
		double planeOccupacy = (double) numberOfParcelsLoaded / capacity;
		int planeOccupacyRate = (int) (planeOccupacy * 100);
		this.occupacyRate = planeOccupacyRate;
	}

	public IVector2 getStartAirport() {
		return startAirport;
	}

	public IVector2 setStartAirport(IVector2 startAirport) {
		this.startAirport = startAirport;
		return this.startAirport;
	}

	public double getAccumulatedProfit() {
		return accumulatedProfit;
	}

	public void setAccumulatedProfit(double accumulatedProfit) {
		this.accumulatedProfit = accumulatedProfit;
	}

	public int getNumberOfParcelsUnloaded() {
		return numberOfParcelsUnloaded;
	}

	public void setNumberOfParcelsUnloaded(int numberOfParcelsUnloaded) {
		this.numberOfParcelsUnloaded = numberOfParcelsUnloaded;
	}

}
