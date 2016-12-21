package fr.ldu.android.floodit.image.model;

public class Level implements Comparable<Level> {
	private static final String STAR = " * ";
	private int nbRows;
	private int nbCols;
	public Level (int r, int c) {
		nbRows = r; nbCols = c;
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Level)) return false;
		Level l = (Level)o;
		return nbRows == l.nbRows && nbCols == l.nbCols;
	}
	@Override
	public int hashCode() {
		return nbRows * nbCols;
	}
	@Override
	public int compareTo(Level another) {
		int anotherProd = another.nbCols * another.nbRows;
		int prod = nbCols * nbRows;
		
		if (prod>anotherProd) return 1;
		if (prod == anotherProd) return 0;
		return -1;
	}
	public int getNbRows() {
		return nbRows;
	}
	public void setNbRows(int nbRows) {
		this.nbRows = nbRows;
	}
	public int getNbCols() {
		return nbCols;
	}
	public void setNbCols(int nbCols) {
		this.nbCols = nbCols;
	}
	@Override
	public String toString() {
		return nbRows + STAR + nbCols;
	}
}
