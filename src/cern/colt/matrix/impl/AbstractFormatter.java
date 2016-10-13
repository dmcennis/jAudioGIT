/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix.impl;

/** 
Abstract base class for flexible, well human readable matrix print formatting.
Value type independent.
A single cell is formatted via a format string.
Columns can be aligned left, centered, right and by decimal point. 
<p>A column can be broader than specified by the parameter <tt>minColumnWidth</tt> 
  (because a cell may not fit into that width) but a column is never smaller than 
  <tt>minColumnWidth</tt>. Normally one does not need to specify <tt>minColumnWidth</tt>.
Cells in a row are separated by a separator string, similar separators can be set for rows and slices.
For more info, see the concrete subclasses.
 
@author wolfgang.hoschek@cern.ch
@version 1.0, 09/24/99
*/
public abstract class AbstractFormatter extends cern.colt.PersistentObject {
 	/**
 	 * The alignment string aligning the cells of a column to the left.
 	 */
 	public static final String LEFT = "left";
	
 	/**
 	 * The alignment string aligning the cells of a column to its center.
 	 */
 	public static final String CENTER = "center";
	
 	/**
 	 * The alignment string aligning the cells of a column to the right.
 	 */
 	public static final String RIGHT = "right";
	
 	/**
 	 * The alignment string aligning the cells of a column to the decimal point.
 	 */
 	public static final String DECIMAL = "decimal";

 	/**
 	 * The default minimum number of characters a column may have; currently <tt>1</tt>.
 	 */
	public static final int DEFAULT_MIN_COLUMN_WIDTH = 1;
	
 	/**
 	 * The default string separating any two columns from another; currently <tt>" "</tt>.
 	 */
	public static final String DEFAULT_COLUMN_SEPARATOR = " ";

 	/**
 	 * The default string separating any two rows from another; currently <tt>"\n"</tt>.
 	 */
	public static final String DEFAULT_ROW_SEPARATOR = "\n";

	/**
 	 * The default string separating any two slices from another; currently <tt>"\n\n"</tt>.
 	 */
	public static final String DEFAULT_SLICE_SEPARATOR = "\n\n";

	
 	/**
 	 * The default format string for formatting a single cell value; currently <tt>"%G"</tt>.
 	 */
 	protected String alignment = LEFT;
	
 	/**
 	 * The default format string for formatting a single cell value; currently <tt>"%G"</tt>.
 	 */
 	protected String format = "%G";
	
 	/**
 	 * The default minimum number of characters a column may have; currently <tt>1</tt>.
 	 */
	protected int minColumnWidth = DEFAULT_MIN_COLUMN_WIDTH;
	
 	/**
 	 * The default string separating any two columns from another; currently <tt>" "</tt>.
 	 */
	protected String columnSeparator= DEFAULT_COLUMN_SEPARATOR;

 	/**
 	 * The default string separating any two rows from another; currently <tt>"\n"</tt>.
 	 */
	protected String rowSeparator = DEFAULT_ROW_SEPARATOR;

	/**
 	 * The default string separating any two slices from another; currently <tt>"\n\n"</tt>.
 	 */
	protected String sliceSeparator = DEFAULT_SLICE_SEPARATOR;

	/**
 	 * Tells whether String representations are to be preceded with summary of the shape; currently <tt>true</tt>.
 	 */
	protected boolean printShape = true;
	

	private static String[] blanksCache; // for efficient String manipulations

	protected static final FormerFactory factory = new FormerFactory();

	static {
		setupBlanksCache();
	}
/**
 * Makes this class non instantiable, but still let's others inherit from it.
 */
protected AbstractFormatter() {}
/**
 * Modifies the strings in a column of the string matrix to be aligned (left,centered,right,decimal).
 */
protected void align(String[][] strings) {
	int rows = strings.length;
	int columns = 0;
	if (rows>0) columns = strings[0].length;

	int[] maxColWidth = new int[columns];
	int[] maxColLead = null;
	boolean isDecimal = alignment.equals(DECIMAL);
	if (isDecimal) maxColLead = new int[columns];
	//int[] maxColTrail = new int[columns];

	// for each column, determine alignment parameters
	for (int column=0; column<columns; column++) {
		int maxWidth = minColumnWidth;
		int maxLead  = Integer.MIN_VALUE;
		//int maxTrail = Integer.MIN_VALUE;
		for (int row=0; row<rows; row++) {
			String s = strings[row][column];
			maxWidth = Math.max(maxWidth, s.length());
			if (isDecimal) maxLead = Math.max(maxLead, lead(s));
			//maxTrail = Math.max(maxTrail, trail(s));
		}
		maxColWidth[column] = maxWidth;
		if (isDecimal) maxColLead[column] = maxLead;
		//maxColTrail[column] = maxTrail;
	}

	// format each row according to alignment parameters
	//StringBuffer total = new StringBuffer();
	for (int row=0; row<rows; row++) {
		alignRow(strings[row], maxColWidth, maxColLead);
	}

}
/**
 * Converts a row into a string.
 */
protected int alignmentCode(String alignment) {
	//{-1,0,1,2} = {left,centered,right,decimal point}
	if (alignment.equals(LEFT)) return -1;
	else if (alignment.equals(CENTER)) return 0;
	else if (alignment.equals(RIGHT)) return 1;
	else if (alignment.equals(DECIMAL)) return 2;
	else throw new IllegalArgumentException("unknown alignment: "+alignment);
}
/**
 * Modifies the strings the string matrix to be aligned (left,centered,right,decimal).
 */
protected void alignRow(String[] row, int[] maxColWidth, int[] maxColLead) {
	int align = alignmentCode(alignment); //{-1,0,1,2} = {left,centered,right,decimal point}
	StringBuffer s = new StringBuffer();

	int columns = row.length;
	for (int column=0; column<columns; column++) {
		s.setLength(0);
		String c = row[column];
		//if (alignment==1) {
		if (alignment.equals(RIGHT)) {
			s.append(blanks(maxColWidth[column] - s.length()));
			s.append(c);
		}
		//else if (alignment==2) {
		else if (alignment.equals(DECIMAL)) {
			s.append(blanks(maxColLead[column] - lead(c)));
			s.append(c);
			s.append(blanks(maxColWidth[column] - s.length()));
		}
		//else if (align==0) {
		else if (alignment.equals(CENTER)) {
			s.append(blanks((maxColWidth[column] - c.length()) / 2));
			s.append(c);
			s.append(blanks(maxColWidth[column] - s.length()));

		}
		//else if (align<0) {
		else if (alignment.equals(LEFT)) {
			s.append(c);
			s.append(blanks(maxColWidth[column] - s.length()));
		}
		else throw new InternalError();
		
		row[column] = s.toString();
	}
}
/**
 * Returns a String with <tt>length</tt> blanks.
 */
protected String blanks(int length) {
	if (length < 0) length = 0;
	if (length < blanksCache.length) return blanksCache[length];
	
	StringBuffer buf = new StringBuffer(length);
	for (int k = 0; k < length; k++) {
		buf.append(' ');
	}
	return buf.toString();
}
/**
 * Demonstrates how to use this class.
 */
public static void demo1() {
/*
// parameters
Object[][] values = {
	{3,     0,        -3.4, 0},
	{5.1   ,0,        +3.0123456789, 0},
	{16.37, 0.0,       2.5, 0},
	{-16.3, 0,        -3.012345678E-4, -1},
	{1236.3456789, 0,  7, -1.2}
};
String[] formats =         {"%G", "%1.10G", "%f", "%1.2f", "%0.2e", null};


// now the processing
int size = formats.length;
ObjectMatrix2D matrix = cern.colt.matrix.ObjectFactory2D.dense.make(values);
String[] strings = new String[size];
String[] sourceCodes = new String[size];
String[] htmlStrings = new String[size];
String[] htmlSourceCodes = new String[size];

for (int i=0; i<size; i++) {
	String format = formats[i];
	strings[i] = toString(matrix,format);
	sourceCodes[i] = toSourceCode(matrix,format);

	// may not compile because of packages not included in the distribution
	//htmlStrings[i] = cern.colt.matrixpattern.Converting.toHTML(strings[i]);
	//htmlSourceCodes[i] = cern.colt.matrixpattern.Converting.toHTML(sourceCodes[i]);
}

System.out.println("original:\n"+toString(matrix));

// may not compile because of packages not included in the distribution
for (int i=0; i<size; i++) {
	//System.out.println("\nhtmlString("+formats[i]+"):\n"+htmlStrings[i]);
	//System.out.println("\nhtmlSourceCode("+formats[i]+"):\n"+htmlSourceCodes[i]);
}

for (int i=0; i<size; i++) {
	System.out.println("\nstring("+formats[i]+"):\n"+strings[i]);
	System.out.println("\nsourceCode("+formats[i]+"):\n"+sourceCodes[i]);
}
*/
}
/**
 * Demonstrates how to use this class.
 */
public static void demo2() {
/*
// parameters
Object[] values = {
	//5, 0.0, -0.0, -Object.NaN, Object.NaN, 0.0/0.0, Object.NEGATIVE_INFINITY, Object.POSITIVE_INFINITY, Object.MIN_VALUE, Object.MAX_VALUE
	5, 0.0, -0.0, -Object.NaN, Object.NaN, 0.0/0.0, Object.MIN_VALUE, Object.MAX_VALUE , Object.NEGATIVE_INFINITY, Object.POSITIVE_INFINITY
	//Object.MIN_VALUE, Object.MAX_VALUE //, Object.NEGATIVE_INFINITY, Object.POSITIVE_INFINITY
};
//String[] formats =         {"%G", "%1.10G", "%f", "%1.2f", "%0.2e"};
String[] formats =         {"%G", "%1.19G"};


// now the processing
int size = formats.length;
ObjectMatrix1D matrix = new DenseObjectMatrix1D(values);

String[] strings = new String[size];
//String[] javaStrings = new String[size];

for (int i=0; i<size; i++) {
	String format = formats[i];
	strings[i] = toString(matrix,format);
	for (int j=0; j<matrix.size(); j++) {
		System.out.println(String.valueOf(matrix.get(j)));
	}
}

System.out.println("original:\n"+toString(matrix));

for (int i=0; i<size; i++) {
	System.out.println("\nstring("+formats[i]+"):\n"+strings[i]);
}
*/
}
/**
 * Demonstrates how to use this class.
 */
public static void demo3(int size, Object value) {
	/*
	cern.colt.Timer timer = new cern.colt.Timer();
	String s;
	StringBuffer buf;
	ObjectMatrix2D matrix = cern.colt.matrix.ObjectFactory2D.dense.make(size,size, value);

	timer.reset().start();
	buf = new StringBuffer();
	for (int i=size; --i >= 0; ) {
		for (int j=size; --j >= 0; ) {
			buf.append(matrix.getQuick(i,j));
		}
	}
	buf = null;
	timer.stop().display();

	timer.reset().start();
	corejava.Format format = new corejava.Format("%G");
	buf = new StringBuffer();
	for (int i=size; --i >= 0; ) {
		for (int j=size; --j >= 0; ) {
			buf.append(format.form(matrix.getQuick(i,j)));
		}
	}
	buf = null;
	timer.stop().display();

	timer.reset().start();
	s = Formatting.toString(matrix, null);
	//System.out.println(s);
	s = null;
	timer.stop().display();

	timer.reset().start();
	s = Formatting.toString(matrix, "%G");
	//System.out.println(s);
	s = null;
	timer.stop().display();
	*/
}
/**
 * Converts a given cell to a String; no alignment considered.
 */
protected abstract String form(AbstractMatrix1D matrix, int index, Former formatter);
/**
 * Returns a string representations of all cells; no alignment considered.
 */
protected abstract String[][] format(AbstractMatrix2D matrix);
/**
 * Returns a string representations of all cells; no alignment considered.
 */
protected String[] formatRow(AbstractMatrix1D vector) {
	Former formatter = null;
	formatter = factory.create(format);
	int s = vector.size();
	String[] strings = new String[s];
	for (int i=0; i<s; i++) {
		strings[i] = form(vector,i,formatter);
	}
	return strings;
}
/**
 * Returns the number of characters or the number of characters before the decimal point.
 */
protected int lead(String s) {
	return s.length();
}
/**
 * Returns a String with the given character repeated <tt>length</tt> times.
 */
protected String repeat(char character, int length) {
	if (character==' ') return blanks(length);
	if (length < 0) length = 0;
	StringBuffer buf = new StringBuffer(length);
	for (int k = 0; k < length; k++) {
		buf.append(character);
	}
	return buf.toString();
}
/**
 * Sets the column alignment (left,center,right,decimal).
 * @param alignment the new alignment to be used; must be one of <tt>{LEFT,CENTER,RIGHT,DECIMAL}</tt>.
 */
public void setAlignment(String alignment) {
	this.alignment = alignment;
}
/**
 * Sets the string separating any two columns from another.
 * @param columnSeparator the new columnSeparator to be used.
 */
public void setColumnSeparator(String columnSeparator) {
	this.columnSeparator = columnSeparator;
}
/**
 * Sets the way a <i>single</i> cell value is to be formatted.
 * @param format the new format to be used.
 */
public void setFormat(String format) {
	this.format = format;
}
/**
 * Sets the minimum number of characters a column may have.
 * @param minColumnWidth the new minColumnWidth to be used.
 */
public void setMinColumnWidth(int minColumnWidth) {
	if (minColumnWidth<0) throw new IllegalArgumentException();
	this.minColumnWidth = minColumnWidth;
}
/**
 * Specifies whether a string representation of a matrix is to be preceded with a summary of its shape.
 * @param printShape <tt>true</tt> shape summary is printed, otherwise not printed.
 */
public void setPrintShape(boolean printShape) {
	this.printShape = printShape;
}
/**
 * Sets the string separating any two rows from another.
 * @param rowSeparator the new rowSeparator to be used.
 */
public void setRowSeparator(String rowSeparator) {
	this.rowSeparator = rowSeparator;
}
/**
 * Sets the string separating any two slices from another.
 * @param sliceSeparator the new sliceSeparator to be used.
 */
public void setSliceSeparator(String sliceSeparator) {
	this.sliceSeparator = sliceSeparator;
}
/**
 * Cache for faster string processing.
 */
protected static void setupBlanksCache() {
	// Pre-fabricate 40 static strings with 0,1,2,..,39 blanks, for usage within method blanks(length).
	// Now, we don't need to construct and fill them on demand, and garbage collect them again.
	// All 40 strings share the identical char[] array, only with different offset and length --> somewhat smaller static memory footprint
	int size = 40;
	blanksCache = new String[size];
	StringBuffer buf = new StringBuffer(size);
	for (int i=size; --i >= 0; ) buf.append(' ');
	String str = buf.toString();
	for (int i=size; --i >= 0; ) {
		blanksCache[i] = str.substring(0,i);
		//System.out.println(i+"-"+blanksCache[i]+"-");
	}
}
/**
 * Returns a short string representation describing the shape of the matrix.
 */
public static String shape(AbstractMatrix1D matrix) {
	//return "Matrix1D of size="+matrix.size();
	//return matrix.size()+" element matrix";
	//return "matrix("+matrix.size()+")";
	return matrix.size()+" matrix";
}
/**
 * Returns a short string representation describing the shape of the matrix.
 */
public static String shape(AbstractMatrix2D matrix) {
	return matrix.rows()+" x "+matrix.columns()+" matrix";
}
/**
 * Returns a short string representation describing the shape of the matrix.
 */
public static String shape(AbstractMatrix3D matrix) {
	return matrix.slices()+" x "+matrix.rows()+" x "+matrix.columns()+" matrix";
}
/**
 * Returns a single string representation of the given string matrix.
 * @param strings the matrix to be converted to a single string.
 */
protected String toString(String[][] strings) {
	int rows = strings.length;
	int columns = strings.length<=0 ? 0: strings[0].length;

	StringBuffer total = new StringBuffer();
	StringBuffer s = new StringBuffer();
	for (int row=0; row<rows; row++) {
		s.setLength(0);
		for (int column=0; column<columns; column++) {
			s.append(strings[row][column]);
			if (column<columns-1) s.append(columnSeparator);
		}
		total.append(s);
		if (row<rows-1) total.append(rowSeparator);
	}

	return total.toString();
}
/**
 * Returns a string representation of the given matrix.
 * @param matrix the matrix to convert.
 */
protected String toString(AbstractMatrix2D matrix) {
	String[][] strings = this.format(matrix);
	align(strings);
	StringBuffer total = new StringBuffer(toString(strings));
	if (printShape) total.insert(0, shape(matrix) + "\n");
	return total.toString();
}
}
