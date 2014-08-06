import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	//	Optimization Weights
	//	public static final boolean SHOW_PLANE = false;
	//	public static final int DISPLAY_TIME = 100;
	//	public static final int NO_MOVE_FAIL_MARGIN = 3;
	//
	//	public static final int ROW_COUNT = 10;
	//	public static final int SEAT_COUNT = 3;
	//	public static final int BONUS_ROW_COUNT = 4;
	//
	//	public static final int PARTICLE_COUNT = 1;
	//	public static final int TRIALS = 10000;
	//	public static final int ROUNDS = 50;
	//
	//	public static final int ZONE_COUNT = 9;
	//	public static final float OUTSIDE_WEIGHT = 2;
	//	public static final float BACK_WEIGHT = 2;
	//	
	//	public static final float INERTIA_WEIGHT = 0.7f;
	//	public static final float PARTICLE_WEIGHT = 1.5f;
	//	public static final float SWARM_WEIGHT = 1.5f;
	
	//	Display Weights
	public static final String USER = "";
	public static final boolean SHOW_PLANE = true;
	public static final int DISPLAY_TIME = 10;
	public static final int NO_MOVE_FAIL_MARGIN = 3;

	public static final int ROW_COUNT = 20;
	public static final int SEAT_COUNT = 3;
	public static final int BONUS_ROW_COUNT = 4;

	public static final int PARTICLE_COUNT = 1;
	public static final int TRIALS = 1;
	public static final int ROUNDS = 1;

	public static final int ZONE_COUNT = 6;
	public static final float OUTSIDE_WEIGHT = 0f;
	public static final float BACK_WEIGHT = 1f;
	
	public static final float INERTIA_WEIGHT = 0.7f;
	public static final float PARTICLE_WEIGHT = 1.5f;
	public static final float SWARM_WEIGHT = 1.5f;

	private static ArrayList<Particle> m_particles = new ArrayList<Particle>();
	private static float m_bestZoneCount;
	private static float m_bestBackWeight;
	private static float m_bestOutsideWeight;
	private static float m_bestTime = Float.NaN;
	
	public static float getZoneCount() {
		return m_bestZoneCount;
	}
	
	public static float getBackWeight() {
		return m_bestBackWeight;
	}
	
	public static float getOutsideWeight() {
		return m_bestOutsideWeight;
	}

	public static void main(String[] args) {
		for (int i = 0; i < PARTICLE_COUNT; i++) m_particles.add(new Particle(false));
		try {
			new File("C:/Users/" + USER + "/Desktop/motionChart.csv").delete();
			BufferedWriter motionChartWriter = new BufferedWriter(new FileWriter(new File("C:/Users/" + USER + "/Desktop/motionChart.csv"), true));
			motionChartWriter.write("Particle, Time, Back Weight, Outside Weight, Suitability, Zone Count");
			motionChartWriter.newLine();
			for (int i = 0; i < ROUNDS; i++) {
				System.out.println("Round: " + i + " Best Time: " + m_bestTime);
				for (int j = 0; j < PARTICLE_COUNT; j++) {
					Particle particle = m_particles.get(j);
					float time = particle.updateParams(i);
					if (m_bestTime != m_bestTime || time < m_bestTime) {
						m_bestZoneCount = particle.getZoneCount();
						m_bestBackWeight = particle.getBackWeight();
						m_bestOutsideWeight = particle.getOutsideWeight();
						m_bestTime = time;
					}
					motionChartWriter.write("Particle " + j + ", " + (2000 - ROUNDS + i) + ", " + particle.getBackWeight() + ", " + particle.getOutsideWeight() + ", " + time + ", " + particle.getZoneCount());
					motionChartWriter.newLine();
				}
			}
			motionChartWriter.close();
		} catch (IOException e) {}
	}
}