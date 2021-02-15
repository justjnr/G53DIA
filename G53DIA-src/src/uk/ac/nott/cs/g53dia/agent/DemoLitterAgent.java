package uk.ac.nott.cs.g53dia.agent;

import uk.ac.nott.cs.g53dia.library.*;
import java.util.Random;

/**
 * A simple example LitterAgent
 * 
 * @author Julian Zappala
 */
/*
 * Copyright (c) 2011 Julian Zappala
 * 
 * See the file "license.terms" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class DemoLitterAgent extends LitterAgent {

	public DemoLitterAgent() {
		this(new Random());
	}

	/**
	 * The tanker implementation makes random moves. For reproducibility, it
	 * can share the same random number generator as the environment.
	 * 
	 * @param r
	 *            The random number generator.
	 */
	public DemoLitterAgent(Random r) {
		this.r = r;
	}

	/*
	 * The following is a simple demonstration of how to write a tanker. The
	 * code below is very stupid and simply moves the tanker randomly until the
	 * charge agt is half full, at which point it returns to a charge pump.
	 */

	int nearestDistance = 100, lastDistance = 0;
	Point nearestPoint, lastChargePoint = new Point(0,0), nearestChargePoint = new Point(1,1), nearestStation, nearestBin, lastStation, lastBin;

	/**
	 * For available view 61x61 grid, scan for bin, or station and choose the nearest one,
	 * only if its closer than the nearest one found so far
	 * @param view - current available view
	 * @param type - type of bin or storage wanted
	 * @return - returns coordinates for the wanted type of item
	 */
	public Point scanFor(Cell[][] view, String type){
		if (type == "LitterBin") {
			for (int i = 0; i < view[0].length; i++) {
				for (int j = 0; j < view[1].length; j++) {
					if (view[i][j] instanceof LitterBin) {
						LitterBin tempBin = (LitterBin)view[i][j];
						if (tempBin.getTask() != null) {
							lastDistance = getDistance(getCurrentCell(view).getPoint(), view[i][j].getPoint());
							if (lastDistance < nearestDistance) {
								nearestDistance = lastDistance;
								nearestBin = view[i][j].getPoint();
							}
							//System.out.println(nearestDistance);
						}
					}
				}
			}
		} else if (type == "RecyclingBin"){
			for (int i = 0; i < view[0].length; i++){
				for (int j = 0; j < view[1].length; j++){
					if (view[i][j] instanceof RecyclingBin){
						RecyclingBin tempBin = (RecyclingBin)view[i][j];
						if (tempBin.getTask() != null) {
							lastDistance = getDistance(getCurrentCell(view).getPoint(), view[i][j].getPoint());
							if (lastDistance < nearestDistance){
								nearestDistance = lastDistance;
								nearestBin = view[i][j].getPoint();
							}
							//System.out.println(nearestDistance);
						}
					}
				}
			}
		} else if (type == "RecyclingStation"){
			for (int i = 0; i < view[0].length; i++){
				for (int j = 0; j < view[1].length; j++){
					if (view[i][j] instanceof RecyclingStation){
						lastDistance = getDistance(getCurrentCell(view).getPoint(), view[i][j].getPoint());
						if (lastDistance < nearestDistance){
							nearestDistance = lastDistance;
							nearestStation = view[i][j].getPoint();
						}
						//System.out.println(nearestDistance);
					}
				}
			}
		} else if (type == "WasteBin"){
			for (int i = 0; i < view[0].length; i++){
				for (int j = 0; j < view[1].length; j++){
					if (view[i][j] instanceof WasteBin){
						WasteBin tempBin = (WasteBin)view[i][j];
						if (tempBin.getTask() != null) {
							//System.out.println((tempTask.getAmount() + this.getLitterLevel()) + " LITTER LEVEL");
							lastDistance = getDistance(getCurrentCell(view).getPoint(), view[i][j].getPoint());
							if (lastDistance < nearestDistance){
								nearestDistance = lastDistance;
								nearestBin = view[i][j].getPoint();
							}
							//System.out.println(nearestDistance);
						}
					}
				}
			}
		} else if (type == "WasteStation"){
			for (int i = 0; i < view[0].length; i++){
				for (int j = 0; j < view[1].length; j++){
					if (view[i][j] instanceof WasteStation){
						lastDistance = getDistance(getCurrentCell(view).getPoint(), view[i][j].getPoint());
						if (lastDistance < nearestDistance){
							nearestDistance = lastDistance;
							nearestStation = view[i][j].getPoint();
						}
						//System.out.println(nearestDistance);
					}
				}
			}
		}
		if (type == "LitterBin" || type == "WasteBin" || type == "RecyclingBin") {
			if (nearestBin == null) {
				return null;
			}
			return nearestBin;
		} else if (type == "RecyclingStation" || type == "WasteStation"){
			if (nearestStation == null) {
				return null;
			}
			return nearestStation;
		}
		return null;
	}

	/**
	 * Search for recharge points and choose the nearest one,
	 * only if its closer than the nearest one found so far
	 * @param view - current available view
	 */
	public void scanRechargePoints(Cell[][] view){
		for (int i = 0; i < view[0].length; i++){
			for (int j = 0; j < view[1].length; j++){
				if (view[i][j] instanceof RechargePoint){
					lastChargePoint = view[i][j].getPoint();
					if (getDistance(getCurrentCell(view).getPoint(), lastChargePoint) < getDistance(getCurrentCell(view).getPoint(), nearestChargePoint)){
						nearestChargePoint = lastChargePoint;
					}
					//System.out.println(nearestDistance);
				}
			}
		}
	}

	/**
	 * Returns distance to item based on current position
	 * @param currentPos - current position of agent
	 * @param itemPos - item position
	 * @return - integer of distance
	 */
	public int getDistance(Point currentPos, Point itemPos){
		return currentPos.distanceTo(itemPos);
	}

	/**
	 * Traverse environment in this direction if nothing in sight
	 * @return - MoveAction
	 */
	public Action searchEnvironment(){
		//System.out.println("debug: SEARCH");
		return new MoveAction(5);
	}

	Point nearestInterruptW = new Point(0,0), nearestInterruptR = new Point(0,0), lastInterruptW = new Point(1,1), lastInterruptR = new Point(1,1);

	/**
	 * Scans nearest Recycling or Waste Station
	 * @param view - current view
	 * @param type - type of station wanted
	 */
	public void scanStations(Cell[][] view, String type){
		if (type == "Waste") {
			for (int i = 0; i < view[0].length; i++) {
				for (int j = 0; j < view[1].length; j++) {
					if (view[i][j] instanceof WasteStation) {
						//System.out.println("FOUND STATION");
						lastInterruptW = view[i][j].getPoint();
						if (getDistance(getCurrentCell(view).getPoint(), lastInterruptW) < getDistance(getCurrentCell(view).getPoint(), nearestInterruptW)) {
							nearestInterruptW = lastInterruptW;
						}
					}
				}
			}
		}
		if (type == "Recycling") {
			for (int i = 0; i < view[0].length; i++) {
				for (int j = 0; j < view[1].length; j++) {
					if (view[i][j] instanceof RecyclingStation) {
						//System.out.println("FOUND STATION");
						lastInterruptR = view[i][j].getPoint();
						if (getDistance(getCurrentCell(view).getPoint(), lastInterruptR) < getDistance(getCurrentCell(view).getPoint(), nearestInterruptR)) {
							nearestInterruptR = lastInterruptR;
						}
					}
				}
			}
		}
	}

	/**
	 * If agents position is currently close enough to a station,
	 * go and drop off the corresponding litter, otherwise keep on
	 * collecting litter
	 * @param view - current view
	 * @param type - type of bin wanted
	 * @return
	 */
	public Action decideBinOrStation(Cell[][] view, String type){
		if (type == "WasteBin"){
			scanStations(view, "Waste");
			if (getLitterLevel() != 0 && getCurrentCell(view) instanceof WasteStation){
				//System.out.println("Waste Station Found");
				//nearestInterruptW = getCurrentCell(view).getPoint();
				return new DisposeAction();
			}
			if (getLitterLevel() != 0 && (getDistance(getCurrentCell(view).getPoint(), nearestInterruptW) < 2 && (getDistance(getCurrentCell(view).getPoint(), nearestInterruptW) != 0))) {
				//System.out.println(getLitterLevel() + " LITTER LEVEL");
				//System.out.println(getDistance(getCurrentCell(view).getPoint(), nearestInterruptW));
				return new MoveTowardsAction(nearestInterruptW);
			}

			Point goTo = scanFor(view, "WasteBin");
			if (goTo == null){
				return searchEnvironment();
			} else {
				if (getCurrentCell(view) instanceof WasteBin){
					WasteBin tempBin = (WasteBin)getCurrentCell(view);
					if (tempBin.getTask() != null) {
						//System.out.println("Waste Bin Found");
						nearestDistance = 100;
						lastDistance = 0;
						nearestBin = null;
						return new LoadAction(tempBin.getTask());
					}
				}
				//System.out.println("debug: WASTEB");
				return new MoveTowardsAction(goTo);
			}
		}
		if (type == "RecyclingBin"){
			scanStations(view, "Recycling");
			if (getCurrentCell(view) instanceof RecyclingStation){
				return new DisposeAction();
			}
			if (getLitterLevel() != 0 && (getDistance(getCurrentCell(view).getPoint(), nearestInterruptR) < 2 && (getDistance(getCurrentCell(view).getPoint(), nearestInterruptR) != 0))){
				return new MoveTowardsAction(nearestInterruptR);
			}

			Point goTo = scanFor(view, type);
			if (goTo == null){
				return searchEnvironment();
			} else {
				if (getCurrentCell(view) instanceof RecyclingBin){
					RecyclingBin tempBin = (RecyclingBin)getCurrentCell(view);
					if (tempBin.getTask() != null) {
						//System.out.println("Recycling Bin Found");
						nearestDistance = 100;
						lastDistance = 0;
						nearestBin = null;
						return new LoadAction(tempBin.getTask());
					}
				}
				//System.out.println("debug: RECYL");
				return new MoveTowardsAction(goTo);
			}
		}
		return null;
	}

	/**
	 *
	 * @param view
	 *            The cells the agent can currently see.
	 * 	 * @param timestep
	 *            The current timestep.
	 * @param timestep
	 * @return - action to take
	 */
	public Action senseAndAct(Cell[][] view, long timestep) {
		scanRechargePoints(view);

		if (getCurrentCell(view) instanceof RechargePoint && getChargeLevel() < MAX_CHARGE){
			return new RechargeAction();
		}
		int rechargeThreshold = getDistance(getCurrentCell(view).getPoint(), nearestChargePoint) + 2;

		/**
		 * If agent doesn't have enough charge, move towards nearestChargePoint, else
		 * check if carrying waste or recycling and proceed
		 */
		if (getChargeLevel() <= rechargeThreshold){
			//System.out.println("debug: RECH");
			return new MoveTowardsAction(nearestChargePoint);
		} else {
			if (getLitterLevel() == 0){ //if not carrying anything & has enough charge, go to nearest LitterBin
				Point goTo = scanFor(view, "LitterBin");
				if (goTo == null){
					return searchEnvironment();
				} else {
					if (getCurrentCell(view) instanceof WasteBin){
						WasteBin tempBin = (WasteBin)getCurrentCell(view);
						if (tempBin.getTask() != null) {
							//System.out.println("Waste Bin Found");
							nearestDistance = 100;
							lastDistance = 0;
							nearestBin = null;
							return new LoadAction(tempBin.getTask());
						}
					}
					if (getCurrentCell(view) instanceof RecyclingBin){
						RecyclingBin tempBin = (RecyclingBin)getCurrentCell(view);
						if (tempBin.getTask() != null) {
							//System.out.println("Recycling Bin Found");
							nearestDistance = 100;
							lastDistance = 0;
							nearestBin = null;
							return new LoadAction(tempBin.getTask());
						}
					}
					//System.out.println("debug: LITTERB");
					return new MoveTowardsAction(goTo);
				}
			}

			if ((getRecyclingLevel() == 0 && getWasteLevel() != 0)){ //if carrying waste & has enough charge,
				if (getLitterLevel() > (MAX_LITTER - 50)){ //if not enough capacity, go to WasteStation
					if (getCurrentCell(view) instanceof WasteStation){
						//System.out.println("Waste Station Found");
						nearestDistance = 100;
						lastDistance = 0;
						nearestStation = null;
						return new DisposeAction();
					}
					Point goTo = scanFor(view, "WasteStation");
					if (goTo == null){
						return searchEnvironment();
					} else {
						//System.out.println("debug: WASTES");
						return new MoveTowardsAction(goTo);
					}
				} else { //decide to go to bin or station if a station is close enough
					return decideBinOrStation(view, "WasteBin");
				}
			}
			if ((getWasteLevel() == 0 && getRecyclingLevel() != 0)){ //if carrying recycling & has enough charge,
				if (getLitterLevel() > (MAX_LITTER - 50)) { //if not enough capacity, go to nearest RecyclingStation
					if (getCurrentCell(view) instanceof RecyclingStation){
						//System.out.println("Recycling Station Found");
						nearestDistance = 100;
						lastDistance = 0;
						nearestStation = null;
						return new DisposeAction();
					}
					Point goTo = scanFor(view, "RecyclingStation");
					if (goTo == null) {
						return searchEnvironment();
					} else {
						//System.out.println("debug: RECYLSTAT");
						return new MoveTowardsAction(goTo);
					}
				} else { //decide to go to bin or station if a station is close enough
					return decideBinOrStation(view, "RecyclingBin");
				}
			}
			return searchEnvironment();
		}
	}
}
