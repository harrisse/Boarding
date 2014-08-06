import java.util.ArrayList;

public class Passenger implements Comparable<Passenger> {
	private int m_targetRow;
	private int m_targetSeat;
	private int m_returnRow;
	private int m_returnSeat;
	private int m_zone;
	private boolean m_isSeated = false;
	private boolean m_isWaiting = false;
	private boolean m_isTargetting = false;
	private boolean m_isBlocker = false;
	private Space m_space;
	private ArrayList<Row> m_plane;
	private Particle m_particle;

	public Passenger(ArrayList<Row> plane, int row, int seat, Particle particle) {
		m_plane = plane;
		m_targetRow = row;
		m_targetSeat = seat;
		m_particle = particle;
	}
	
	public boolean move() {
		boolean ret = false;
		Row targetRow = m_plane.get(m_targetRow);
		if (m_space != null && m_targetRow == m_space.getRow() + 1 && targetRow.isBlocked(targetRow.get(m_targetSeat))) {
			m_isWaiting = true;
			if (targetRow.isPermanentlyBlocked(targetRow.get(m_targetSeat))) targetRow.unblockSpace(targetRow.get(m_targetSeat));
		} else m_isWaiting = false;
		if (!m_isSeated && !m_isWaiting && m_space != null) {
			Space nextSpace = null;
			int currentRow = m_space.getRow();
			int currentSeat = m_space.getSeat();
			
			if (m_space.getRow() == m_targetRow && m_space.getSeat() == m_targetSeat) {
				if (m_isTargetting) {
					m_isTargetting = false;
					m_targetRow = m_returnRow;
					m_targetSeat = m_returnSeat;
				} else m_isSeated = true;
			} else if (currentRow < m_targetRow && currentSeat == Main.SEAT_COUNT) {
				if (!m_plane.get(currentRow + 1).get(Main.SEAT_COUNT).isBlocked()) nextSpace = m_plane.get(currentRow + 1).get(Main.SEAT_COUNT);
			} else if (currentRow > m_targetRow && currentSeat == Main.SEAT_COUNT) nextSpace = m_plane.get(currentRow - 1).get(Main.SEAT_COUNT);
			else if (currentSeat < m_targetSeat) {
				if (!m_plane.get(currentRow).get(currentSeat + 1).isBlocked()) nextSpace = m_plane.get(currentRow).get(currentSeat + 1);
			} else if (currentSeat > m_targetSeat) {
				if (!m_plane.get(currentRow).get(currentSeat - 1).isBlocked()) nextSpace = m_plane.get(currentRow).get(currentSeat - 1);
			}
			
			if (nextSpace != null && !nextSpace.hasOccupant()) {
				// System.out.println("(" + m_space.getRow() + ", " + m_space.getSeat() + ") -> (" + nextSpace.getRow() + ", " + nextSpace.getSeat() + ")");
				m_space.clearOccupant();
				m_space = nextSpace;
				nextSpace.setOccupant(this);
				ret = true;
			}
			if (m_isBlocker && !m_isTargetting && m_targetRow == m_space.getRow()) {
				m_plane.get(m_targetRow).get(Main.SEAT_COUNT).removeBlock();
				m_isBlocker = false;
			}
		}
		return ret;
	}
	
	public void setSpace(Space space) {
		m_space = space;
	}
	
	public boolean isBlocker() {
		return m_isBlocker;
	}
	
	public void setTarget(int targetRow, int targetSeat) {
		m_plane.get(m_targetRow).get(Main.SEAT_COUNT).addBlock();
		m_returnRow = m_targetRow;
		m_returnSeat = m_targetSeat;
		m_targetRow = targetRow;
		m_targetSeat = targetSeat;
		m_isSeated = false;
		m_isTargetting = true;
		m_isBlocker = true;
	}
	
	public boolean isSeated() {
		return m_isSeated;
	}
	
	public boolean isTargetting() {
		return m_isTargetting;
	}

	public String getInfo() {
		return "Current: " + m_space.getRow() + ", " + m_space.getSeat() + " Destination: " + m_targetRow + ", " + m_targetSeat;
	}

	public String toString() {
		return m_isTargetting ? "?" : m_isSeated || m_isWaiting ? "x" : "*";
	}

	public Space getSpace() {
		return m_space;
	}
	
	public boolean hasSpace() {
		return m_space != null;
	}

	public int getTargetRow() {
		return m_targetRow;
	}

	public int getTargetSeat() {
		return m_targetSeat;
	}
	
	public void setZone(int zone) {
		m_zone = zone;
	}
	
	public int getZone() {
		return m_zone;
	}
	
	public String getIdentifier() {
		int row = m_isTargetting ? m_returnRow : m_targetRow;
		int seat = m_isTargetting ? m_returnSeat : m_targetSeat;
		return "Passenger " + row + " " + seat;
	}

	public boolean equals(Passenger passenger) {
		return m_targetRow == passenger.getTargetRow() && m_targetSeat == passenger.getTargetSeat();
	}

	public int compareTo(Passenger p) {
		// The 1000000 here serves to increase precision because integer rounding was causing trouble with contract violations on Collections.sort();
		return (int) (1000000 * (m_particle.getOutsideWeight() * Main.ROW_COUNT * outsideFirstInsideLast(p) + m_particle.getBackWeight() * Main.SEAT_COUNT * backToFront(p)));
	}
	
	public float outsideFirstInsideLast(Passenger p) {
		return Math.abs(m_targetSeat - Main.SEAT_COUNT) - Math.abs(p.getTargetSeat() - Main.SEAT_COUNT);
	}
	
	public float backToFront(Passenger p) {
		return m_targetRow - p.getTargetRow();
	}
}