/* HEADER 
If you use this code don’t forget to reference us :) BibTeX: http://www.cs.technion.ac.il/~rani/el-yaniv_bib.html#BegleiterEY04 

This code is free software; you can redistribute it and/or 
modify it under the terms of the GNU General Public License 
as published by the Free Software Foundation; either version 2 
of the License, or (at your option) any later version. 

This code is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License (<a href="http://www.gnu.org/copyleft/gpl.html">GPL</a>) for more details.*/ 

package vmm.algs.oppm;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import vmm.algs.com.colloquial.arithcode.*;

/**
 * Offline PPMC implementation.
 * Using <a href="http://www.colloquial.com/carp/">Bob Carpenter</a> code.
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * @author <a href="http://www.cs.technion.ac.il/~ronbeg">Ron Begleiter</a>
 * @version 1.0
 */
public class OfflinePPMModel
extends PPMModel {

	private int[] allRes = new int[3];
	private boolean isFirstPrediction;

	public OfflinePPMModel(int maxCodeLength, int absize) {
		super(maxCodeLength, absize);
		isFirstPrediction = true;
	}

	public double predict(int symbol) {
		if (isFirstPrediction) {
			isFirstPrediction = false;
			super._buffer = new ByteBuffer(super._maxContextLength + 1);
			super._contextLength = 0;
			super._contextNode = null;
		}

		double p = 1.0;
		while (super.escaped(symbol)) {
			interval(ArithCodeModel.ESCAPE, allRes);
			p *= (allRes[1] - allRes[0]) / (double) allRes[2];
		}
		interval(symbol, allRes);
		
		return (p * ( (allRes[1] - allRes[0]) / (double) allRes[2]));
	}

	public void use(int symbol) {
		while (super.escaped(symbol)) {
			super.interval(ArithCodeModel.ESCAPE, allRes); // have already done complete walk to compute escape
		}

		super.interval(symbol, allRes);
	}

	// specified in ArithCodeModel
	public void interval(int symbol, int[] result) {
		if (symbol == ArithCodeModel.EOF) {
			_backoffModel.intervalNoIncrement(EOF, result, _excludedBytes);
		}
		else if (symbol == ArithCodeModel.ESCAPE) {
			intervalEscape(result);
		}
		else {
			calcInterval(symbol, result); //will not increment symbol
		}
	}

	/**
	 * Clears this OfflinePPMModel's context.
	 * As a result the nexts symbol context will be the empty context.
	 */
	public void clearContext() {
		super._buffer = new ByteBuffer(super._maxContextLength+1);//new context buffer
		super._contextLength = 0;//empty context length
		super._contextNode = null; // Omer: added
		super._excludedBytes.clear(); // Omer: added
	}

	/** Returns interval for byte specified as an integer in 0 to 255 range.
	 * @param i Integer specification of byte in 0 to 255 range.
	 * @param result Array specifying cumulative probability for byte i.
	 */
	private void calcInterval(int i, int[] result) {
		if (_contextNode != null) {
			_contextNode.interval(i, _excludedBytes, result);
		}
		else {
			_backoffModel.intervalNoIncrement(i, result, _excludedBytes);

		}
		_buffer.buffer(i);
		_contextLength = Math.min(_maxContextLength, _buffer.length());
		getContextNodeBinarySearch();
		_excludedBytes.clear();
	}
	
	// Omer: from to end - added functionality to print trie as dit file (without changing state of model)
	private class Edge {
		public String from;
		public String to;
		public String label = "";
	}
	private class Node {
		public String name;
		public String label = "";
	}
	private DecimalFormat df = new DecimalFormat("#.##");
	private void traversePPMNode(PPMNode c, Set<Node> nodes, Set<Edge> edges, String prefix, boolean ppm) {
		if (c != null) {
			ByteBuffer buffer = new ByteBuffer(super._maxContextLength + 1);
			int[] bytes = _buffer.bytes();
			for (int b = 0; b < _buffer.length(); ++b) {
				buffer.buffer(bytes[_buffer.offset()+b]);
			}
			PPMNode contextNode = _contextNode;
			int contextLength = _contextLength;
			ByteSet excludedBytes = new ByteSet(_contexts.length);
			excludedBytes.add(_excludedBytes);
			if (prefix.length() > 0) {
				prefix += ",";
			}
			String name = prefix+c._byte;
			Node current = new Node();
			current.name = name;
			if (! ppm) {
				current.label = "xlabel=\"c="+_backoffModel._count[c._byte]+"\"";
			} else {
				current.label = "xlabel=\"c="+c._count+"\"";
			}
			current.label += (c == currentContextNode)? ",shape=doublecircle" : "";
			nodes.add(current);
			for(PPMNode child = c._firstChild; child != null; child = child._nextSibling) {
				_contextLength = contextLength;
				_contextNode = contextNode;
				_buffer = new ByteBuffer(super._maxContextLength + 1);
				bytes = buffer.bytes();
				for (int b = 0; b < buffer.length(); ++b) {
					_buffer.buffer(bytes[buffer.offset()+b]);
				}
				_excludedBytes.clear();
				_excludedBytes.add(excludedBytes);
				Edge e = new Edge();
				e.from = name;
				e.to = name+","+child._byte;
				int[] result = new int[3];
				interval(child._byte,result);
				e.label = "label=\"p="+df.format((result[1] - result[0]) / (double) result[2])+"\"";
				edges.add(e);
				traversePPMNode(child, nodes, edges, name, true);
			}
			_contextLength = contextLength;
			_contextNode = contextNode;
			_buffer = new ByteBuffer(super._maxContextLength + 1);
			bytes = buffer.bytes();
			for (int b = 0; b < buffer.length(); ++b) {
				_buffer.buffer(bytes[buffer.offset()+b]);
			}
			_excludedBytes.clear();
			_excludedBytes.add(excludedBytes);
			Node n = new Node();
			n.name = name+",e";
			n.label = "xlabel=\"escape\"";
			nodes.add(n);
			Edge e = new Edge();
			e.from = name;
			e.to = n.name;
			int[] result = new int[3];
			intervalEscape(result);
			e.label = "label=\"p="+df.format((result[1] - result[0]) / (double) result[2])+"\"";
			edges.add(e);
			_contextLength = contextLength;
			_contextNode = contextNode;
			_buffer = new ByteBuffer(super._maxContextLength + 1);
			bytes = buffer.bytes();
			for (int b = 0; b < buffer.length(); ++b) {
				_buffer.buffer(bytes[buffer.offset()+b]);
			}
			_excludedBytes.clear();
			_excludedBytes.add(excludedBytes);
		}
	}
	private PPMNode currentContextNode = _contextNode;
	public void print2dot(String filename) {
		ByteBuffer buffer = new ByteBuffer(super._maxContextLength + 1);
		int[] bytes = _buffer.bytes();
		for (int b = 0; b < _buffer.length(); ++b) {
			buffer.buffer(bytes[_buffer.offset()+b]);
		}
		PPMNode contextNode = _contextNode;
		int contextLength = _contextLength;
		ByteSet excludedBytes = new ByteSet(_contexts.length);
		excludedBytes.add(_excludedBytes);
		Set<Node> nodes = new HashSet<Node>();
		Set<Edge> edges = new HashSet<Edge>();
		Node epsilon = new Node();
		epsilon.name = "/";
		epsilon.label = (currentContextNode == null)? "shape=doublecircle" : "";
		nodes.add(epsilon);
		for (int i = 0 ; i < _contexts.length; ++i) {
			PPMNode c = _contexts[i];
			_contextLength = 0;
			_contextNode = null;
			_buffer = new ByteBuffer(super._maxContextLength + 1);
			_excludedBytes.clear();
			if (c != null) {
				Edge e = new Edge();
				e.from = epsilon.name;
				e.to = ""+c._byte;
				int[] result = new int[3];
				interval(c._byte,result);
				e.label = "label=\"p="+df.format((result[1] - result[0]) / (double) result[2])+"\"";
				edges.add(e);
				traversePPMNode(c, nodes, edges, "", false);
			} else {
				Edge e = new Edge();
				e.from = epsilon.name;
				e.to = ""+i;
				int[] result = new int[3];
				interval(i,result);
				e.label = "label=\"p="+df.format((result[1] - result[0]) / (double) result[2])+"\"";
				edges.add(e);
				Node n = new Node();
				n.label = "xlabel=\"c="+_backoffModel._count[i]+"\"";
				n.name = ""+i;
				nodes.add(n);
			}
		}
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename+".dot"), "utf-8"));
		    writer.write("digraph {\n");
		    for (Node n : nodes) {
		    	writer.write("\""+n.name+"\"");
		    	if (n.label.length()>0) {
		    		writer.write(" ["+n.label+"]");
		    	}
		    	writer.write(";\n");
		    }
	    	for (Edge e : edges) {
	    		writer.write("\""+e.from+"\" -> \""+e.to+"\"");
		    	if (e.label.length()>0) {
		    		writer.write(" ["+e.label+"]");
		    	}
		    	writer.write(";\n");
	    	}
	    	writer.write("}");
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			String dir = System.getProperty("user.dir")+"\\";
			Runtime.getRuntime().exec("\"C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot\" -Tpdf \""+dir+filename+".dot\" -o \""+dir+filename+".pdf\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		_contextLength = contextLength;
		_contextNode = contextNode;
		_buffer = new ByteBuffer(super._maxContextLength + 1);
		bytes = buffer.bytes();
		for (int b = 0; b < buffer.length(); ++b) {
			_buffer.buffer(bytes[buffer.offset()+b]);
		}
		_excludedBytes.clear();
		_excludedBytes.add(excludedBytes);
	}
}
