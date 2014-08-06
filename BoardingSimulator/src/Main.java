import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class Main {
	public static final int ROW_COUNT = 10;
	public static final int SEAT_COUNT = 3;
	public static final int BONUS_ROW_COUNT = 4;
	
	private static ArrayList<Row> m_plane = new ArrayList<Row>(); //new Space[ROW_COUNT + 2 * BONUS_ROW_COUNT][2 * SEAT_COUNT + 1];
	private static Stack<Passenger> m_passengers = new Stack<Passenger>();
	private static LinkedList<Space> m_emptySpaces = new LinkedList<Space>();
	private static LinkedList<Space> m_tempSpaces = new LinkedList<Space>();
	
	public static void main(String[] args) {
		// For each row
		for (int row = 0; row < ROW_COUNT + 2 * BONUS_ROW_COUNT; row++) {
			// Add a row to the plane;
			m_plane.add(new Row());
			// And for each seat in the new row,
			for (int seat = 0; seat < 2 * SEAT_COUNT + 1; seat++) {
				// Add a space to the new row,
				m_plane.get(row).add(seat, new Space(row, seat));
				// Register the new space as an empty space,
				m_emptySpaces.add(m_plane.get(row).get(seat));
				// And make a passenger to sit in that space if it's supposed to be a seat.
				if (seat != SEAT_COUNT && row >= BONUS_ROW_COUNT && row < ROW_COUNT + BONUS_ROW_COUNT) m_passengers.push(new Passenger(m_plane, row, seat));
			}
		}
		// Scramble the passengers so they board in random order.
		// Collections.shuffle(m_passengers);
		// Every passenger should petition to enter the plane.
		for (Passenger passenger : m_passengers) passenger.petition(0, SEAT_COUNT);
		// TODO: Make this while loop short circuit when all passengers are seated.
		// While we have empty space do the following:
		while (!m_emptySpaces.isEmpty()) {
			// For each empty space, if the space has a valid next occupant, then it gets that occupant so it's no longer empty. Remove the now-filled space.
			for (Iterator<Space> iter = m_emptySpaces.iterator(); iter.hasNext();) if (iter.next().nextOccupant()) iter.remove();
			// Add all of the spaces that became empty during the last round of movements.
			m_emptySpaces.addAll(m_tempSpaces);
			m_tempSpaces.clear();
			// Update the blocking maps for each row.
			for (Row row : m_plane) row.updateBlockMap();
			// Update the blocked status for each passenger.
			for (Passenger passenger : m_passengers) passenger.updateIsBlocked();
			// For each empty space, add new petitioners for that space.
			for (Space space : m_emptySpaces) space.updatePetitioners();
			// Print out our plane and wait a bit so we can see what is printed.
			printPlane();
			try {Thread.sleep(200);} catch (InterruptedException e) {}
		}
	}
	
	public static void printPlane() {
		for (int row = 0; row < ROW_COUNT + 2 * BONUS_ROW_COUNT; row++) {
			String passengerMapString = "";
			String blockMapString = "     ";
			for (int seat = 0; seat < 2 * SEAT_COUNT + 1; seat++) {
				if (m_plane.get(row).get(seat).hasOccupant()) passengerMapString += m_plane.get(row).get(seat).getOccupant();
				else if (seat == SEAT_COUNT || row < BONUS_ROW_COUNT || row >= ROW_COUNT + BONUS_ROW_COUNT) passengerMapString += ' ';
				else passengerMapString += '-';
				passengerMapString += ' ';
				
				if (seat == SEAT_COUNT || row < BONUS_ROW_COUNT || row >= ROW_COUNT + BONUS_ROW_COUNT) blockMapString += ' ';
				else blockMapString += m_plane.get(row).getBlockMap(seat);
				blockMapString += ' ';
			}
			System.out.println(passengerMapString + blockMapString + "     " + row);
		}
		System.out.println("\n\n\n");
	}
	
	public static void addEmptySpace(Space space) {
		m_tempSpaces.add(space);
	}
}