import java.util.ArrayList;

public class Passenger implements Comparable {
	private int m_row;
	private int m_seat;
	private Space m_space;
	private ArrayList<Row> m_plane;
	private boolean m_isSeated = false;
	private boolean m_isBlocked = false;
	private boolean m_isMoved = false;
	private Space m_preferredSpace;

	public Passenger(ArrayList<Row> plane, int row, int seat) {
		m_plane = plane;
		m_row = row;
		m_seat = seat;
	}

	public void moveTo(Space space) {
		if (m_space != null) m_space.clearOccupant();
		m_space = space;
		if (m_isMoved) {
			Space blockedSpace = m_plane.get(m_row - 1).get(Main.SEAT_COUNT);
			if (!blockedSpace.hasOccupant() || (!blockedSpace.getOccupant().isBlocked() && !blockedSpace.getOccupant().isMoved())) {
				m_isMoved = false;
				petition(m_space.getRow() - 1, Main.SEAT_COUNT);
			} else if (m_space.getSeat() < Main.SEAT_COUNT) {
				petition(m_row, m_space.getSeat() + 1);
			} else if (m_space.getSeat() > Main.SEAT_COUNT) {
				petition(m_row, m_space.getSeat() - 1);
			} else if (blockedSpace.hasOccupant()
					&& (blockedSpace.getOccupant().isBlocked() || blockedSpace.getOccupant().isMoved())) {
				petition(m_space.getRow() + 1, Main.SEAT_COUNT);
			}
		} else {
			if (m_space.getRow() < m_row) petition(m_space.getRow() + 1, Main.SEAT_COUNT);
			else if (m_space.getRow() > m_row) petition(m_space.getRow() - 1, Main.SEAT_COUNT);
			else if (m_space.getSeat() < m_seat) petition(m_row, m_space.getSeat() + 1);
			else if (m_space.getSeat() > m_seat) petition(m_row, m_space.getSeat() - 1);
			else m_isSeated = true;
		}
	}

	public void petition(int row, int seat) {
		m_plane.get(row).get(seat).addPetitioner(this);
		m_preferredSpace = m_plane.get(row).get(seat);
	}

	public boolean isSeated() {
		return m_isSeated;
	}

	public boolean isBlocked() {
		return m_isBlocked;
	}

	public boolean isMoved() {
		return m_isMoved;
	}

	public void updateIsBlocked() {
		if (m_space != null && m_row == m_space.getRow() + 1 && m_plane.get(m_row).getBlockMap(m_seat) > 0) {
			m_isBlocked = true;
			m_plane.get(m_row).clearRow(m_seat);
		} else m_isBlocked = false;
	}

	public String getInfo() {
		return "Current: " + m_space.getRow() + ", " + m_space.getSeat() + " Destination: " + m_row + ", " + m_seat;
	}

	public String toString() {
		return m_isMoved ? "?" : m_isSeated || m_isBlocked ? "x" : "*";
	}

	public Space getSpace() {
		return m_space;
	}

	public void setMoved() {
		m_isMoved = true;
		if (m_space.getSeat() < Main.SEAT_COUNT) petition(m_row, m_seat + 1);
		else petition(m_row, m_seat - 1);
	}

	public int getDestRow() {
		return m_row;
	}

	public int getDestSeat() {
		return m_seat;
	}

	public boolean equals(Passenger passenger) {
		return m_row == passenger.getDestRow() && m_seat == passenger.getDestSeat();
	}

	public int compareTo(Object o) {
		Passenger p = (Passenger) o;
		if (m_row == p.getDestRow() && (p.isBlocked() || isBlocked())) {
			if (Math.abs(m_seat - Main.SEAT_COUNT) > Math.abs(p.getDestSeat() - Main.SEAT_COUNT)) return -1;
			else return 1;
		} else if (p.getSpace() != null && m_space != null) return p.getSpace().getRow() - m_space.getRow();
		else return p.getDestRow() - m_row;
	}
}