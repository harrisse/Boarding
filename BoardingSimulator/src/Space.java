import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Space {
	private int m_row;
	private int m_seat;
	private Passenger m_occupant;
	private LinkedList<Passenger> m_petitioners = new LinkedList<Passenger>();
	private LinkedList<Passenger> m_tempPetitioners = new LinkedList<Passenger>();
	
	public Space(int row, int seat) {
		m_row = row;
		m_seat = seat;
	}
	
	public void addPetitioner(Passenger passenger) {
		if (!m_petitioners.contains(passenger) && !m_tempPetitioners.contains(passenger)) m_tempPetitioners.add(passenger);
	}
	
	public boolean nextOccupant() {
		if (m_row != 0) Collections.sort(m_petitioners);
		if (m_petitioners.isEmpty()) return false;
		for (Iterator<Passenger> iter = m_petitioners.iterator(); iter.hasNext();) {
			Passenger next = iter.next();
			if (!next.isBlocked() && !(next.isMoved() && next.getSpace().getSeat() == Main.SEAT_COUNT && m_seat != Main.SEAT_COUNT) && (next.getSpace() == null || isAdjacent(next.getSpace()))) {
				m_occupant = next;
				m_occupant.moveTo(this);
				iter.remove();
				m_tempPetitioners.remove(m_occupant);
				break;
			}
		}
		return m_occupant != null;
	}
	
	public int getRow() {
		return m_row;
	}
	
	public int getSeat() {
		return m_seat;
	}
	
	public void clearOccupant() {
		Main.addEmptySpace(this);
		m_occupant = null;
	}
	
	public boolean hasOccupant() {
		return m_occupant != null;
	}
	
	public Passenger getOccupant() {
		return m_occupant;
	}
	
	public String toString() {
		return Integer.toString(m_row * 10 + m_seat);
	}
	
	public void updatePetitioners() {
		m_petitioners.addAll(m_tempPetitioners);
		m_tempPetitioners.clear();
	}
	
	public boolean isAdjacent(Space space) {
		return (Math.abs(space.getRow() - m_row) == 1 && space.getSeat() == m_seat) || (Math.abs(space.getSeat() - m_seat) == 1 && space.getRow() == m_row);
	}
}
