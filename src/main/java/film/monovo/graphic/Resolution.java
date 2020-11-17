package film.monovo.graphic;

public class Resolution {
	private final static int DPI = 200;
	private final static double millimeterToInch = 1.0 / 25.4;
	private final static double inchToMillimeter = 25.4;
	private final static double adjustCoifficent = inchToMillimeter / DPI;

	public static final int toMillimeter(int pixel) {
		return (int) Math.floor(pixel * adjustCoifficent);
	}

	public static final int toPixel(int mm) {
		return (int) Math.floor(mm / adjustCoifficent);
	}
}
