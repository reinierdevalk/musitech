package de.uos.fmt.musitech.data.voiceSeparationAlgorithms;

import java.net.URL;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.performance.midi.MidiReader;

/**
 * Class which calculates two matrices used in the Jordanous's algorithm
 * a matrix which gives the probability of transition between two successive notes in a voice.
 * a matrix which gives the probability of belonging to a voice for a note ( given its pitch )
 * @author gauthier
 *
 */
public class TransitionMatrix {

	private double[][] matrixTrans; // matrix which gives the probability of transition between two successive notes in a voice.
	private double[][] matrixProbVoice; // matrix which gives the probability of belonging to a voice for a note ( given its pitch )


	public TransitionMatrix (double[][] transMatrix, double[][] voiceMatrix) {
		matrixTrans = transMatrix;
		matrixProbVoice = voiceMatrix;
	}
	
	public TransitionMatrix(URL url) {
		MidiReader mid = new MidiReader();
		Piece p = mid.getPiece(url);
		
		// Calculation of the transition matrix
		double [][] matTrans = new double[88][88];
		for (int i=0; i<p.getScore().getContent().size(); i++)
		{
			p.getScore().getContent().get(i); // get the Staff i for this piece
			for (int j=0; j<p.getScore().getContent().get(i).size(); j++)
			{
				p.getScore().getContent().get(i).getContent().get(j); // get the voice j for the staff i
				for (int k=0; k<p.getScore().getContent().get(i).getContent().get(j).size()-1; k++)
				{
					// get the actual note k and the following note for the voice j
					Note actualNote = (Note)p.getScore().getContent().get(i).getContent().get(j).get(k).get(0);
					Note followingNote = (Note)p.getScore().getContent().get(i).getContent().get(j).get(k+1).get(0);
					// Now that we have two successive notes for a voice, increment the matrix
					int row = actualNote.getMidiPitch();
					int column = followingNote.getMidiPitch();
					matTrans[row][column]++;

				}
			}

		}
		// Normalize the transition matrix by rows ( the element i,j represents the probability of transition from the note i to the note j)
		// So the sum of the elements of one row must be equal to 1
		for (int i=0; i<88; i++)
		{
			double sumProb = 0;
			for (int j=0; j<88; j++)
			{
				sumProb += matTrans[i][j];	
			}
			for (int j=0; j<88; j++)
			{
				if ( sumProb != 0)
				{
				matTrans[i][j] /= sumProb;
				}
			}
		}
		matrixTrans = matTrans;
		
		// Calculation of the voice-belonging probability matrix
		int nbVoice=0;
		for (int i=0; i<p.getScore().getContent().size(); i++)
		{
			nbVoice+=p.getScore().getContent().get(i).getContent().size(); // count the voice number in the staff i;
		}
		double[][] matProbVoice = new double[88][nbVoice];
		int voice=0;
		for (int i=0; i<p.getScore().getContent().size(); i++)
		{
			p.getScore().getContent().get(i); // get the Staff i for this piece
			for (int j=0; j<p.getScore().getContent().get(i).size(); j++)
			{
				p.getScore().getContent().get(i).getContent().get(j); // get the voice j for the staff i
				for (int k=0; k<p.getScore().getContent().get(i).getContent().get(j).size(); k++)
				{
					Note actualNote = (Note)p.getScore().getContent().get(i).getContent().get(j).get(k).get(0);
					int row = actualNote.getMidiPitch();
					matProbVoice[row][voice]++;
				}
				voice++;
			}
		}
		// Normalize the voice-belonging probability matrix by rows ( the element i,j represents the probability that the note ( whose pitch is) i belongs to the voice j)
		// So the sum of the elements of one row must be equal to 1
		for (int i=0; i<88; i++)
		{
			double sumProb = 0;
			for (int j=0; j<nbVoice; j++)
			{
				sumProb += matProbVoice[i][j];	
			}
			for (int j=0; j<nbVoice; j++)
			{
				if (sumProb != 0)
				{
					matProbVoice[i][j] /= sumProb;
				}
			}
		}
		matrixProbVoice = matProbVoice;

	}
	
	public double[][] getMatrixTrans() {
		return matrixTrans;
	}
	
	public void setMatrixTrans(double[][] transitionMatrix) {
		matrixTrans = transitionMatrix;
	}
	
	public double[][] getMatrixProbVoice() {
		return matrixProbVoice;
	}
	
	public void setMatrixProbVoice(double[][] probVoiceMatrix) {
		matrixProbVoice = probVoiceMatrix;
	}
}
