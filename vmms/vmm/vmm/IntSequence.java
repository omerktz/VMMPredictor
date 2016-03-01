package vmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class IntSequence implements Comparable<IntSequence> {

	private final List<Integer> seq;
	
	public IntSequence() {
		this.seq = new ArrayList<Integer>();
	}
	
	public IntSequence(CharSequence cseq) {
		this.seq = new ArrayList<Integer>();
		for (int i = 0; i < cseq.length(); i++) {
			this.seq.add((int)cseq.charAt(i));
		}
	}
	
	public IntSequence(IntSequence other) {
		this.seq = other.seq;
	}
	
	public IntSequence(List<Integer> seq) {
		this.seq = seq;
	}
	
	public IntSequence(Collection<Integer> seq) {
		this.seq = new ArrayList<Integer>(seq);
	}
	
	public IntSequence(Integer[] seq) {
		this.seq = Arrays.asList(seq);
	}
	
	public IntSequence(Integer i) {
		this.seq = new ArrayList<Integer>();
		this.seq.add(i);
	}
	
    public IntSequence(byte[] barr) {
		this.seq = new ArrayList<Integer>();
		for( byte b: barr) {
			this.seq.add((int)b);
		}
	}

	/**
     * Returns the length of this character sequence.  The length is the number
     * of 16-bit <code>char</code>s in the sequence.</p>
     *
     * @return  the number of <code>char</code>s in this sequence
     */
    public int length() {
    	return this.seq.size();
    }

    /**
     * Returns the <code>char</code> value at the specified index.  An index ranges from zero
     * to <tt>length() - 1</tt>.  The first <code>char</code> value of the sequence is at
     * index zero, the next at index one, and so on, as for array
     * indexing. </p>
     *
     * <p>If the <code>char</code> value specified by the index is a
     * <a href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param   index   the index of the <code>char</code> value to be returned
     *
     * @return  the specified <code>char</code> value
     *
     * @throws  IndexOutOfBoundsException
     *          if the <tt>index</tt> argument is negative or not less than
     *          <tt>length()</tt>
     */
    public int intAt(int index) {
    	return this.seq.get(index);
    }

    /**
     * Returns a new <code>CharSequence</code> that is a subsequence of this sequence.
     * The subsequence starts with the <code>char</code> value at the specified index and
     * ends with the <code>char</code> value at index <tt>end - 1</tt>.  The length
     * (in <code>char</code>s) of the
     * returned sequence is <tt>end - start</tt>, so if <tt>start == end</tt>
     * then an empty sequence is returned. </p>
     *
     * @param   start   the start index, inclusive
     * @param   end     the end index, exclusive
     *
     * @return  the specified subsequence
     *
     * @throws  IndexOutOfBoundsException
     *          if <tt>start</tt> or <tt>end</tt> are negative,
     *          if <tt>end</tt> is greater than <tt>length()</tt>,
     *          or if <tt>start</tt> is greater than <tt>end</tt>
     */
    public IntSequence subSequence(int start, int end) {
    	return new IntSequence(this.seq.subList(start, end));
    }

    /**
     * Returns a string containing the characters in this sequence in the same
     * order as this sequence.  The length of the string will be the length of
     * this sequence. </p>
     *
     * @return  a string consisting of exactly this sequence of characters
     */
    public String toString() {
    	StringBuilder str = new StringBuilder();
    	for (Integer i : this.seq) {
    		str.append(i.toString()+',');
		}
    	str.deleteCharAt(str.length()-1);
    	return str.toString();
    }
    
    public IntSequence append(IntSequence other) {
    	this.seq.addAll(other.seq);
    	return this;
    }

    public IntSequence append(Integer i) {
    	this.seq.add(i);
    	return this;
    }
    
    public Integer[] toArray() {
    	return (Integer[]) this.seq.toArray();
    }

	@Override
	public int compareTo(IntSequence other) {
		int i = 0;
		while ((i < this.seq.size()) && (i < other.seq.size())) {
			if (this.seq.get(i) != other.seq.get(i)) {
				return this.seq.get(i) - other.seq.get(i);
			}
			i++;
		}
		return this.seq.size() - other.seq.size();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof IntSequence) {
			IntSequence otherSeq = (IntSequence)other;
			if (this.seq.size() == otherSeq.seq.size()) {
				for (int i=0; i<this.seq.size(); i++) {
					if (this.seq.get(i) != otherSeq.seq.get(i)) {
						return false;
					}
				}
				return true;
			}
		}
	    return false;
	}
}

