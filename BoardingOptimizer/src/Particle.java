import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class Particle {
	private float m_zoneCount;
	private float m_backWeight;
	private float m_outsideWeight;
	
	private float m_zoneCountVel = 0;
	private float m_backWeightVel = 0;
	private float m_outsideWeightVel = 0;
	
	private float m_bestZoneCount;
	private float m_bestBackWeight;
	private float m_bestOutsideWeight;
	private float m_bestTime = Float.NaN;
	
	private ArrayList<Row> m_plane = new ArrayList<Row>();
	private LinkedList<Passenger> m_passengers = new LinkedList<Passenger>();
	private Stack<Passenger> m_unboardedPassengers = new Stack<Passenger>();
	private int m_timeCount = 0;
	
	public Particle(boolean rand) {
		if (rand) {
			m_zoneCount = (float) (Math.random() * Main.ZONE_COUNT) + 1;
			m_backWeight = (float) ((2 * Math.random() - 1) * Main.BACK_WEIGHT);
			m_outsideWeight = (float) ((2 * Math.random() - 1) * Main.OUTSIDE_WEIGHT);
		} else {
			m_zoneCount = Main.ZONE_COUNT;
			m_backWeight = Main.BACK_WEIGHT;
			m_outsideWeight = Main.OUTSIDE_WEIGHT;
		}
	}
	
	public float updateParams(float round) {
		float particleWeight = (float) (Math.random() * Main.PARTICLE_WEIGHT);
		float swarmWeight = (float) (Math.random() * Main.SWARM_WEIGHT);
		if (m_bestTime == m_bestTime) {
			m_zoneCountVel = (float) (Main.INERTIA_WEIGHT * m_zoneCountVel + particleWeight * (m_bestZoneCount - m_zoneCount) + swarmWeight * (Main.getZoneCount() - m_zoneCount));
			m_backWeightVel = (float) (Main.INERTIA_WEIGHT * m_backWeightVel + particleWeight * (m_bestBackWeight - m_backWeight) + swarmWeight * (Main.getBackWeight() - m_backWeight));
			m_outsideWeightVel = (float) (Main.INERTIA_WEIGHT * m_outsideWeightVel + particleWeight * (m_bestOutsideWeight - m_outsideWeight) + swarmWeight * (Main.getOutsideWeight() - m_outsideWeight));
			
			if (m_zoneCount + m_zoneCountVel >= 1 && m_zoneCount + m_zoneCountVel <= Main.ZONE_COUNT) m_zoneCount += m_zoneCountVel;
			else if (m_zoneCountVel < 0) m_zoneCount = 1;
			else m_zoneCount = Main.ZONE_COUNT;
			if (Math.abs(m_backWeight + m_backWeightVel) <= 100) m_backWeight += m_backWeightVel;
			else if (m_backWeightVel < 0) m_backWeight = -100;
			else m_backWeight = 100;
			if (Math.abs(m_outsideWeight + m_outsideWeightVel) <= 100) m_outsideWeight += m_outsideWeightVel;
			else if (m_outsideWeightVel < 0) m_outsideWeight = -100;
			else m_outsideWeight = 100;
		} else if (round > 0) {
			m_zoneCountVel = (float) (Main.INERTIA_WEIGHT * m_zoneCountVel + swarmWeight * (Main.getZoneCount() - m_zoneCount));
			m_backWeightVel = (float) (Main.INERTIA_WEIGHT * m_backWeightVel + swarmWeight * (Main.getBackWeight() - m_backWeight));
			m_outsideWeightVel = (float) (Main.INERTIA_WEIGHT * m_outsideWeightVel + swarmWeight * (Main.getOutsideWeight() - m_outsideWeight));
			
			if (m_zoneCount + m_zoneCountVel >= 1 && m_zoneCount + m_zoneCountVel <= Main.ZONE_COUNT) m_zoneCount += m_zoneCountVel;
			else if (m_zoneCountVel < 0) m_zoneCount = 1;
			else m_zoneCount = Main.ZONE_COUNT;
			if (Math.abs(m_backWeight + m_backWeightVel) <= 100) m_backWeight += m_backWeightVel;
			else if (m_backWeightVel < 0) m_backWeight = -100;
			else m_backWeight = 100;
			if (Math.abs(m_outsideWeight + m_outsideWeightVel) <= 100) m_outsideWeight += m_outsideWeightVel;
			else if (m_outsideWeightVel < 0) m_outsideWeight = -100;
			else m_outsideWeight = 100;
		}
		return runSimulation();
	}
	
	public float getZoneCount() {
		return m_zoneCount;
	}
	
	public float getOutsideWeight() {
		return m_outsideWeight;
	}
	
	public float getBackWeight() {
		return m_backWeight;
	}
	
	public float runSimulation() {
		m_timeCount = 0;
		int count = 0;
		for (int i = 0; i < Main.TRIALS; i++) {
			if (Main.SHOW_PLANE) {
				new File("C:/Users/" + Main.USER + "/Desktop/simulationData.csv").delete();
				try {
					BufferedWriter simulationWriter = new BufferedWriter(new FileWriter(new File("C:/Users/" + Main.USER + "/Desktop/simulationData.csv"), true));
					simulationWriter.write("Space, Time, Row, Seat, Zone");
					simulationWriter.newLine();
					m_plane = new ArrayList<Row>();
					m_passengers = new LinkedList<Passenger>();
					m_unboardedPassengers = new Stack<Passenger>();
					if (simulate(simulationWriter)) count++;
					simulationWriter.close();
				} catch (IOException e) {}
			} else {
				m_plane = new ArrayList<Row>();
				m_passengers = new LinkedList<Passenger>();
				m_unboardedPassengers = new Stack<Passenger>();
				if (simulate(null)) count++;
			}
		}
//		System.out.println("Simulation Success Rate: " + Float.valueOf((float) count / (float) Main.TRIALS));
//		System.out.println("Average Time: " + Float.valueOf((float) m_timeCount / (float) count));
		float time = Float.valueOf((float) m_timeCount / (float) count);
		if (m_bestTime != m_bestTime || time < m_bestTime) {
			m_bestBackWeight = m_backWeight;
			m_bestOutsideWeight = m_outsideWeight;
			m_bestZoneCount = m_zoneCount;
			m_bestTime = time;
		}
		return time;
	}
	
	public boolean simulate(BufferedWriter writer) {
		// For each row
		for (int row = 0; row < Main.ROW_COUNT + 2 * Main.BONUS_ROW_COUNT; row++) {
			// Add a row to the plane;
			m_plane.add(new Row());
			// And for each seat in the new row,
			for (int seat = 0; seat < 2 * Main.SEAT_COUNT + 1; seat++) {
				// Add a space to the new row,
				m_plane.get(row).add(seat, new Space(row, seat));
				// And make a passenger to sit in that space if it's supposed to be a seat.
				if (seat != Main.SEAT_COUNT && row >= Main.BONUS_ROW_COUNT && row < Main.ROW_COUNT + Main.BONUS_ROW_COUNT) {
					Passenger newPassenger = new Passenger(m_plane, row, seat, this);
					m_passengers.add(newPassenger);
					m_unboardedPassengers.push(newPassenger);
				}
			}
		}
		// Sort the passengers.
		Collections.shuffle(m_unboardedPassengers);
		Collections.sort(m_unboardedPassengers);
		// Separate passengers into zones.
		ArrayList<LinkedList<Passenger>> zones = new ArrayList<LinkedList<Passenger>>();
		for (int i = 0; i < m_zoneCount; i++) zones.add(new LinkedList<Passenger>());
		for (int count = 0; count < 2 * Main.SEAT_COUNT * Main.ROW_COUNT; count++) {
			int zone = (int) m_zoneCount * count / (2 * Main.SEAT_COUNT * Main.ROW_COUNT);
			Passenger next = m_unboardedPassengers.pop();
			next.setZone(zone);
			zones.get(zone).add(next);
		}
		for (LinkedList<Passenger> zone : zones) {
			Collections.shuffle(zone);
			m_unboardedPassengers.addAll(zone);
		}
		Collections.reverse(m_unboardedPassengers);
		// Do the actual simulation
		int failCount = Main.NO_MOVE_FAIL_MARGIN;
		int timeCount = 0;
		while (true) {
			timeCount++;
			Space firstSpace = m_plane.get(0).get(Main.SEAT_COUNT);
			if (!firstSpace.hasOccupant() && !m_unboardedPassengers.isEmpty()) {
				Passenger newPassenger = m_unboardedPassengers.pop();
				newPassenger.setSpace(firstSpace);
				firstSpace.setOccupant(newPassenger);
			}
			boolean hasMoved = false;
			for (Passenger p : m_passengers) hasMoved = p.move() || hasMoved;
			if (!hasMoved) failCount--;
			else failCount = Main.NO_MOVE_FAIL_MARGIN;
			if (failCount == 0) return false;
			if (Main.SHOW_PLANE) {
				printPlane(writer, timeCount);
				try {Thread.sleep(Main.DISPLAY_TIME);} catch (InterruptedException e) {e.printStackTrace();}
			}
			boolean allSeated = true;
			for (Passenger p : m_passengers) allSeated = allSeated && p.isSeated();
			if (allSeated) {
				m_timeCount += timeCount;
				return true;
			}
		}
	}
	
	public void printPlane(BufferedWriter writer, int time) {
		for (int row = 0; row < Main.ROW_COUNT + 2 * Main.BONUS_ROW_COUNT; row++) {
			String passengerMapString = "";
			for (int seat = 0; seat < 2 * Main.SEAT_COUNT + 1; seat++) {
				if (m_plane.get(row).get(seat).hasOccupant()) passengerMapString += m_plane.get(row).get(seat).getOccupant();
				else if (seat == Main.SEAT_COUNT || row < Main.BONUS_ROW_COUNT || row >= Main.ROW_COUNT + Main.BONUS_ROW_COUNT) passengerMapString += ' ';
				else passengerMapString += '-';
				passengerMapString += ' ';
			}
			System.out.println(passengerMapString);
		}
		System.out.println("\n\n\n");
		try {
			for (Passenger passenger : m_passengers) if (passenger.hasSpace()) {
				writer.write(passenger.getIdentifier() + ", " + Integer.toString(1000 + time) + ", " + passenger.getSpace().getRow() + ", " + passenger.getSpace().getSeat() + ", " + passenger.getZone());
				writer.newLine();
			}
		} catch (IOException e) {}
	}
}
