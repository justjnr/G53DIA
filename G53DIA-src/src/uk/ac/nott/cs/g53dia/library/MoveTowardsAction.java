package uk.ac.nott.cs.g53dia.library;

/**
 * Action which moves the agent towards a given {@link Point} in the
 * environment.
 * 
 * @author Neil Madden.
 */

/*
 * Copyright (c) 2005 Neil Madden. Copyright (c) 2010 University of Nottingham.
 * 
 * See the file "license.terms" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

public class MoveTowardsAction implements Action {

	private Point target;

	public MoveTowardsAction(Point t) {
		target = t;
	}

	public void execute(Environment env, LitterAgent agnt) throws IllegalActionException {

		if (agnt.getPosition().equals(target)) {
			throw new IllegalActionException("MoveTowards: already there!");
		}

		Point newPosition = (Point) agnt.position.clone();

		int dx = target.x - newPosition.x;
		int dy = target.y - newPosition.y;
		if (dx < 0) {
			newPosition.x--;
		} else if (dx > 0) {
			newPosition.x++;
		}
		if (dy < 0) {
			newPosition.y--;
		} else if (dy > 0) {
			newPosition.y++;
		}

		agnt.position = (Point) newPosition.clone();
		agnt.useCharge(1);
		
	}


	public String toString() {
		return "MoveTowards: " + target;
	}

}
