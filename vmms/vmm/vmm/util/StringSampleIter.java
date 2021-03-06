/* HEADER 
If you use this code don�t forget to reference us :) BibTeX: http://www.cs.technion.ac.il/~rani/el-yaniv_bib.html#BegleiterEY04 

This code is free software; you can redistribute it and/or 
modify it under the terms of the GNU General Public License 
as published by the Free Software Foundation; either version 2 
of the License, or (at your option) any later version. 

This code is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License (<a href="http://www.gnu.org/copyleft/gpl.html">GPL</a>) for more details.*/

package vmm.util;

import vmm.IntSequence;
import vmm.util.SampleIterator;

//import util.Samples;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class StringSampleIter implements SampleIterator {
	private static final String NULL_NAME = "*NONAME*";
	private String data;
	private int dataInd;
	private boolean stringORseq;
	private IntSequence dataSeq;

	private String name;

	public StringSampleIter(IntSequence data) {
		this.dataSeq = data;
		dataInd = 0;
		name = NULL_NAME;
		stringORseq = false;
	}

	public StringSampleIter(String data) {
		this.data = data;
		dataInd = 0;
		name = NULL_NAME;
		stringORseq = true;
	}

	public boolean hasNext() {
		if (stringORseq) {
			return dataInd < data.length();
		} else {
			return dataInd < dataSeq.length();
		}
	}

	public int next() {
		if (hasNext()) {
			if (stringORseq) {
				return (int) data.charAt(dataInd++);
			} else {
				return (int) dataSeq.intAt(dataInd++);
			}
		} else
			throw new java.util.NoSuchElementException();
	}

	public void restart() {
		dataInd = 0;
	}

	public long size() {
		if (stringORseq) {
			return data.length();
		} else {
			return dataSeq.length();
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
