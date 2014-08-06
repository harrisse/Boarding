import java.util.ArrayList;

public class Row extends ArrayList<Space> {
	private static final long serialVersionUID = 1L;
	
	public boolean isBlocked(Space space) {
		boolean isBlocked = false;
		if (space.getSeat() < Main.SEAT_COUNT) {
			for (int i = space.getSeat() + 1; i < Main.SEAT_COUNT; i++) if (get(i).hasOccupant()) isBlocked = true;
		} else {
			for (int i = Main.SEAT_COUNT + 1; i < space.getSeat(); i++) if (get(i).hasOccupant()) isBlocked = true;
		}
		return isBlocked;
	}
	
	public boolean isPermanentlyBlocked(Space space) {
		boolean isBlocked = false;
		if (space.getSeat() < Main.SEAT_COUNT) {
			for (int i = space.getSeat() + 1; i < Main.SEAT_COUNT; i++) if (get(i).hasOccupant() && get(i).getOccupant().isSeated()) isBlocked = true;
		} else {
			for (int i = Main.SEAT_COUNT + 1; i < space.getSeat(); i++) if (get(i).hasOccupant() && get(i).getOccupant().isSeated()) isBlocked = true;
		}
		return isBlocked;
	}
	
	public void unblockSpace(Space space) {
		int row = space.getRow();
		int seat = space.getSeat();
		if (seat < Main.SEAT_COUNT) {
			int count = 0;
			for (int i = seat + 1; i < Main.SEAT_COUNT; i++) if (get(i).hasOccupant()) {
				count++;
				get(i).getOccupant().setTarget(row + count, Main.SEAT_COUNT);
			}
		} else {
			int count = 0;
			for (int i = seat - 1; i > Main.SEAT_COUNT; i--) if (get(i).hasOccupant()) {
				count++;
				get(i).getOccupant().setTarget(row + count, Main.SEAT_COUNT);
			}
		}
	}
}
