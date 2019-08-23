/**********************************************

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see
<http://www.gnu.org/licenses/>.
In addition to the rights granted to the GNU General Public License,
you opt to use this program as specified in the following:

MUSITECH LINKING EXCEPTION

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of the
GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute the
resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based
on this library.

For the MUSITECH library, this exceptional permission described in the paragraph
above is subject to the following three conditions:
- If you modify this library, you must extend the GNU General Public License and
       this exception including these conditions to your version of the MUSITECH library.
- If you distribute a combined work with this library, you have to mention the
       MUSITECH project and link to its web site www.musitech.org in a location
       easily accessible to the users of the combined work (typically in the "About"
       section of the "Help" menu) and in any advertising material for the combined
       software.
- If you distribute a combined work with the MUSITECH library, you allow the MUSITECH
               project to use mention your combined work for promoting the MUSITECH project.
       For the purpose of this licence, 'distribution' includes the provision of software
       services (e.g. over the World Wide Web).

**********************************************/
package de.uos.fmt.musitech.audio.proc.filter.models;

/**
 * FilterDesigner.java
 * @author Tillman Weyde
 */
public class FilterDesigner {
	
	float aCoeff[]; 
	float bCoeff[];

// Base class for all IIR filters by Craig Lindley 

	public FilterDesigner(int frequency, 
							   int sampleRate,
							   double parameter) {
		// Save incoming
		this.frequency = frequency;
		this.sampleRate = sampleRate;
		
		// Damping factor for highpass and lowpass, q for bandpass
		this.parameter = parameter;
	}

	// parameter is Q
	public void bandpassFilterDesign() {

		// thetaZero = 2 * Pi * Freq * T or (2 * Pi * Freq) / sampleRate
		// where Freq is center frequency of bandpass filter
		double thetaZero = getThetaZero();

		double theTan = Math.tan(thetaZero / (2.0 * parameter));

        // Beta relates gain to bandwidth (and therefore q) at -3 db points
		beta = 0.5 * ((1.0 - theTan) / (1.0 + theTan));
		
		// For unity gain at center frequency
		alpha = (0.5 - beta) / 2.0;

		// Final filter coefficient
		gamma = (0.5 + beta) * Math.cos(thetaZero);
		
		// adaption for standard filters:
		
	}

	// parameter is dampingFactor
	public void highpassFilterDesign() {

		// Get radians per sample at cutoff frequency
		double thetaZero = getThetaZero();

		double theSin = parameter / (2.0 * Math.sin(thetaZero));

        // Beta relates gain to cutoff freq
		beta = 0.5 * ((1.0 - theSin) / (1.0 + theSin));
		
		// Final filter coefficient
		gamma = (0.5 + beta) * Math.cos(thetaZero);

		// For unity gain 
		alpha = (0.5 + beta + gamma) / 4.0;
	}


	// parameter = damping factor
	public void lowpassFilterDesign() {

		// Get radians per sample at cutoff frequency
		double thetaZero = getThetaZero();

		double theSin = parameter / (2.0 * Math.sin(thetaZero));

        // Beta relates gain to cutoff freq
		beta = 0.5 * ((1.0 - theSin) / (1.0 + theSin));
		
		// Final filter coefficient
		gamma = (0.5 + beta) * Math.cos(thetaZero);

		// For unity gain 
		alpha = (0.5 + beta - gamma) / 4.0;
	}


	// Given a frequency of interest, calculate radians/sample
	protected double calcRadiansPerSample(double freq) {

		return (2.0 * Math.PI * freq) / sampleRate;
	}

	// Return the radians per sample at the frequency of interest
	protected double getThetaZero() {
		
		return calcRadiansPerSample(frequency);
	}



	
	// Print all three IIR coefficients
	public void printCoefficients() {

		System.out.println("Filter Specifications:");
		System.out.println("\tSample Rate: " + sampleRate +
						   ", Frequency: " + frequency + 
						   ", d/q: " + parameter);

		System.out.println("\tAlpha: " + alpha);
		System.out.println("\tBeta: " + beta);
		System.out.println("\tGamma: " + gamma);
	}

	// Return alpha coefficient
	public double getAlpha() {

		return alpha;
	}

	// Return beta coefficient
	public double getBeta() {

		return beta;
	}

	// Return gamma coefficient
	public double getGamma() {

		return gamma;
	}
	
	// Private class data
	protected int frequency;
	protected int sampleRate;
	protected double parameter;
	protected double alpha;
	protected double beta;
	protected double gamma;




}
