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

package vmm.algs;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vmm.IntSequence;
import vmm.pred.VMMPredictor;
import vmm.pred.VMMNotTrainedException;

import vmm.algs.oppm.*;

//import java.io.*;


/**
 * <p><b>PPMC Predictor</b></p>
 * <p>
 * Usage example:
 *
 * PPMCPredictor ppmc = new PPMCPredictor();
 * ppmc.init(256, 5);
 * ppmc.learn("abracadabra");
 * System.out.println("logeval : " + ppmc.logEval("cadabra"));
 * System.out.println("P(c|abra) : " + ppmc.predict('c', "abra"));
 * </p>
 *
 * Using <a href="http://www.colloquial.com/carp/">Bob Carpenter</a> code.
 * <p>Copyright: Copyright (c) 2004</p>
 * @author <a href="http://www.cs.technion.ac.il/~ronbeg">Ron Begleiter</a>
 * @version 1.0
 */

public final class PPMCPredictor
implements VMMPredictor {

	private static final double NEGTIVE_INVERSE_LOG_2 = - (1 / Math.log(2.0));

	private OfflinePPMModel ppmc;

	private int count;
	
	public PPMCPredictor() {
		ppmc = null;
		count = 0;
	}

	/**
	 * initializes this PPMPredictor
	 * @param abSize alphabet size
	 * @param vmmOrder VMM order
	 */
	public void init(int abSize, int vmmOrder) {
		ppmc = new OfflinePPMModel(vmmOrder, abSize);
	}

	public void learn(IntSequence trainingSequence) {
		ppmc.clearContext(); // Omer: added
		for (int symIndex = 0; symIndex < trainingSequence.length(); ++symIndex) {
			ppmc.use(trainingSequence.intAt(symIndex));
		}
	}

	public double predict(int symbol, IntSequence context) {
		try {
			ppmc.clearContext();
			for (int i = 0; i < context.length(); ++i) {
				ppmc.predict(context.intAt(i)); //updates the ppmc context
			}
			return ppmc.predict(symbol);
		}
		catch (NullPointerException npe) {
			if (ppmc == null) {
				throw new VMMNotTrainedException();
			}
			else {
				throw npe;
			}
		}
	}

	public double logEval(IntSequence testSequence) {
		try {
			ppmc.clearContext();

			double value = 0.0;

			for (int i = 0; i < testSequence.length(); ++i) {
				value += Math.log(ppmc.predict(testSequence.intAt(i)));
			}
			return value * NEGTIVE_INVERSE_LOG_2; // the Math.log is in natural base
		}
		catch (NullPointerException npe) {
			if (ppmc == null) {
				throw new VMMNotTrainedException();
			}
			else {
				throw npe;
			}
		}

	}

	public double logEval(IntSequence testSequence, IntSequence initialContext) {
		//for (int symIndex = 0; symIndex < initialContext.length(); ++symIndex) {
		//	ppmc.use(initialContext.intAt(symIndex));
		//}
		//return logEval(testSequence);
		throw new NotImplementedException();
	}
	
	public void print2dot(String filename) {
		ppmc.print2dot(filename);
	}

}
