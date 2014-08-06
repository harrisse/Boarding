import java.util.ArrayList;

public class Row extends ArrayList<Space> {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Integer> m_blockMap = new ArrayList<Integer>();
	
	public void add(int seat, Space space) {
		super.add(seat, space);
		m_blockMap.add(0);
	}
	
	public int getBlockMap(int seat) {
		return m_blockMap.get(seat);
	}
	
	public void updateBlockMap() {
		int count = 0;
		for (int i = Main.SEAT_COUNT; i >= 0; i--) {
			if (get(i).hasOccupant() && get(i).getOccupant().isSeated()) count++;
			m_blockMap.set(i, count);
		}
		count = 0;
		for (int i = Main.SEAT_COUNT; i <= Main.SEAT_COUNT * 2; i++) {
			if (get(i).hasOccupant() && (get(i).getOccupant().isSeated() || i != Main.SEAT_COUNT)) count++;
			m_blockMap.set(i, count);
		}
	}
	
	public void clearRow(int seat) {
		if (seat < Main.SEAT_COUNT) for (int i = seat + 1; i < Main.SEAT_COUNT; i++) {
			if (get(i).hasOccupant()) get(i).getOccupant().setMoved();
		} else for (int j = Main.SEAT_COUNT + 1; j < seat; j++) {
			if (get(j).hasOccupant()) get(j).getOccupant().setMoved();
		}
	}
}
