
public class Space {
	private int m_row;
	private int m_seat;
	private Passenger m_occupant;
	private int m_isBlocked = 0;
	private boolean m_hasPassedBlock = false;

	public Space(int row, int seat) {
		m_row = row;
		m_seat = seat;
	}
	
	public boolean isBlocked() {
		return m_hasPassedBlock && m_isBlocked > 0;
	}
	
	public void addBlock() {
		m_hasPassedBlock = false;
		m_isBlocked++;
	}
	
	public void removeBlock() {
		m_isBlocked--;
	}

	public int getRow() {
		return m_row;
	}

	public int getSeat() {
		return m_seat;
	}

	public Passenger getOccupant() {
		return m_occupant;
	}
	
	public void clearOccupant() {
		setOccupant(null);
	}
	
	public void setOccupant(Passenger p) {
		if (p != null && !p.isBlocker() && m_isBlocked > 0) m_hasPassedBlock = true;
		m_occupant = p;
	}
	
	public boolean hasOccupant() {
		return m_occupant != null;
	}

	public String toString() {
		return m_row + ", " + m_seat;
	}

	public boolean isAdjacent(Space space) {
		return (Math.abs(space.getRow() - m_row) == 1 && space.getSeat() == m_seat) || (Math.abs(space.getSeat() - m_seat) == 1 && space.getRow() == m_row);
	}
}
