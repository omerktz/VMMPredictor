
package vmm;

import java.util.Collection;

import vmm.algs.BinaryCTWPredictor;
import vmm.algs.DCTWPredictor;
import vmm.algs.PPMCPredictor;
import vmm.pred.VMMPredictor;

public class API {

	public static enum VMMType {
		BinaryCTW,
		DCTW,
		//LZ78,
		//LZms,
		PPMC,
		//PST,
	}
	
	private static final SequenceEncoder enc = new SequenceEncoder();
	
	/**
	 * Create and initialize and new VMMPredictor instance
	 * @param type type of model: PPM-C, DCTW, BinaryCTW
	 * @param alphabetSize number of letters in the used alphabet
	 * @param maxSequenceLength length of longest sequence
	 * @return the newly created model
	 */
	public static VMMPredictor getNewPredictor(VMMType type, int alphabetSize, int maxSequenceLength) {
		VMMPredictor p = null;
		switch(type) {
		case PPMC: p = new PPMCPredictor(); ((PPMCPredictor)p).init(alphabetSize, maxSequenceLength-1); break;
		case DCTW: p = new DCTWPredictor(); ((DCTWPredictor)p).init(alphabetSize, maxSequenceLength-1); break;
		case BinaryCTW: p = new BinaryCTWPredictor(); ((BinaryCTWPredictor)p).init(alphabetSize, maxSequenceLength); break;
		}
		return p;
	}
	
	/**
	 * Train a model on a single sequence
	 * @param p model to train
	 * @param seq sequence used for training
	 */
	public static void trainModel(VMMPredictor p, Object[] seq) {
		p.learn(enc.encode(seq));
	}
	
	/**
	 * Train a model on a collection of sequences
	 * @param p model to train
	 * @param seqs sequences used for training
	 */
	public static void trainModel(VMMPredictor p, Collection<Object[]> seqs) {
		for (Object[] seq : seqs) {
			p.learn(enc.encode(seq));
		}
	}
	
	/**
	 * Compute probability of a symbol appearing after a context
	 * @param p
	 * @param symbol
	 * @param context
	 * @return probability
	 */
	public static double predict(VMMPredictor p, Object symbol, Object[] context) {
		return p.predict(enc.getEncodedSymbol(symbol), enc.encode(context));
	}
	
	/**
	 * Compute probability of a symbol appearing after an empty context
	 * @param p
	 * @param symbol
	 * @return probability
	 */
	public static double predict(VMMPredictor p, Object symbol) {
		return p.predict(enc.getEncodedSymbol(symbol), new IntSequence());
	}
	
	/**
	 * Compute probability of a sequence appearing after a context
	 * @param p
	 * @param symbol
	 * @param context
	 * @return -log_2(probability)
	 */
	public static double logEval(VMMPredictor p, Object[] seq, Object[] context) {
		return p.logEval(enc.encode(seq), enc.encode(context));
	}

	/**
	 * Compute probability of a sequence appearing after an empty context
	 * @param p
	 * @param symbol
	 * @return -log_2(probability)
	 */
	public static double logEval(VMMPredictor p, Object[] seq) {
		return p.logEval(enc.encode(seq));
	}
}