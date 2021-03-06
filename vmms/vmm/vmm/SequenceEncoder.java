package vmm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SequenceEncoder {

	private final ConcurrentMap<Object,Integer> dict;
	private final ConcurrentMap<Integer,Object> revdict;
	
	public SequenceEncoder () {
		this.dict = new ConcurrentHashMap<Object, Integer>();
		this.revdict = new ConcurrentHashMap<Integer,Object>();
	}
	
	public Integer getEncodedSymbol(Object s) {
		if (!dict.containsKey(s)) {
			synchronized (dict) {
				if (!dict.containsKey(s)) {
					Integer i = dict.size()+1;
					this.dict.put(s, i);
					this.revdict.put(i, s);
				}
			}
		}
		return dict.get(s);
	}
	
	public Object getDecodedSymbol(Integer i) {
		if (i >= dict.size()) {
			throw new IndexOutOfBoundsException("Decoded int doesn't match a recorded string");
		}
		return revdict.get(i);
	}
	
	public IntSequence encode(Object[] seq) {
		Integer[] res = new Integer[seq.length+1];
		res[0] = 0;
		for (int i = 0; i < seq.length; i++) {
			res[i+1] = getEncodedSymbol(seq[i]);
		}
		return new IntSequence(res);
	}
	
	public Object[] decode(IntSequence iseq) {
		Object[] res = new String[iseq.length()-1];
		for (int i = 1; i < iseq.length(); i++) {
			res[i-1] = getDecodedSymbol(iseq.intAt(i));
		}
		return res;
	}
}
