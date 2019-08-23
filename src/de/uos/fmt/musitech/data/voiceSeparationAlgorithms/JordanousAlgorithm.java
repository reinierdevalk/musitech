package de.uos.fmt.musitech.data.voiceSeparationAlgorithms;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.utility.math.Rational;


/**
 * This class perform the Jordanous's algorithm.
 * This is a voice separation algorithm consisting in sequencing the piece in small fragments, each one being given a marker.
 * A marker is a peculiar chord in the partition where all voices are present and distributed far apart to allow an easy assignment.
 * The assignment spreads then form the marker to each edge of the fragment, given a statistical basis of transition between voices.
 * @author gauthier
 *
 */
public class JordanousAlgorithm {

	private TransitionMatrix transMat;

	private int maxNumberOfVoice;

	public JordanousAlgorithm( URL url, int maximalNumberOfVoice) {
		maxNumberOfVoice = maximalNumberOfVoice;
		transMat = new TransitionMatrix(url);
	}


	public JordanousAlgorithm() {
		;
	}


	/**
	 * 
	 * @param numberOfMarker : The Number of Marker to use for the algorithm
	 * @param extract : List of Notes on which the algorithm is performed.
	 * @param totalDur : the total duration of the extract
	 */
	public void algorithmJorda( int numberOfMarker, List<Note> extract, Rational totalDur, Rational beat) {

		List<Integer> allocatedVoice = new ArrayList<Integer>(extract.size());
		for ( int i=0; i< extract.size(); i++)
		{
			allocatedVoice.add(-1);
		}

		List<Rational> marker = new ArrayList<Rational>();

		// First step : find the marker points
		int gap = 3;

		List<Note> presentNote = new ArrayList<Note>();
		Rational time=new Rational(0,1);
		
		while ( marker.size()<numberOfMarker)
		{
			time = time.sub(time);
			marker.clear();
			//time.sub(time); // reinitialize the time
			while(time.isLess(totalDur)==true)
			{
				// determination of the present voices at this time
				presentNote.clear();
				for (int noteNumber=0; noteNumber<extract.size(); noteNumber++ )
				{
					if ( (extract.get(noteNumber).getMetricTime().isLessOrEqual(time)) && (time.isLess(extract.get(noteNumber).getScoreNote().getMetricEndTime())) )
					{
						presentNote.add(extract.get(noteNumber));
					}
				}

				// see if we can define a maker at this time
				// To have a marker, we need at least the correct gap between each of the note, 
				// and we need to have the same number of present notes than the number of voices in the piece
				boolean gapOk = true;
				for (int j=0; j<presentNote.size()-1; j++)
				{
					for ( int k=j+1; k<presentNote.size(); k++)
					{
						// check the gap
						if ( Math.abs( (presentNote.get(j).getMidiPitch()-presentNote.get(k).getMidiPitch()) )<=gap)
						{
							gapOk=false;
						}
					}
				}
				// check the number of voices
				if (presentNote.size()!=maxNumberOfVoice)
				{
					gapOk=false;
				}
				if ( gapOk == true)
				{
					marker.add(time);
				}

				time = time.add(beat);

			}
			gap--;

		}

		// If there is too many markers, remove randomly amongst the elements
		while ( marker.size()>numberOfMarker )
		{
			marker.remove((int)Math.floor(Math.random()*(marker.size())));
		}
		System.out.println(" marker = " + marker);


		// Second step : Defines windows around each marker constituted by a postWindow after the marker and a preWindow before the marker
		// and allocates the voices in each window : from the marker to each border using the transition matrices

		List<Note> postWindowNote = new ArrayList<Note>(); // Notes in the current PostWindow
		List<Note> preWindowNote = new ArrayList<Note>() ; // Notes in the current PreWindow
		List<Note> currentVoices = new ArrayList<Note>(); // Notes currently assigned to each voice ( used for postWindow )
		List<Note> futureVoices = new ArrayList<Note> (); // Notes currently assigned to each voice ( used for preWindow )
		for (int markerNumber=0; markerNumber<numberOfMarker; markerNumber++)
		{
				presentNote.clear();
				currentVoices.clear();
				futureVoices.clear();

				// determination of the present voices at this time ( time of the marker )
				for (int noteNumber=0; noteNumber<extract.size(); noteNumber++ )
				{
					if ( (extract.get(noteNumber).getMetricTime().isLessOrEqual(marker.get(markerNumber))) && (marker.get(markerNumber).isLess(extract.get(noteNumber).getScoreNote().getMetricEndTime())) )
					{
						presentNote.add(extract.get(noteNumber));
					}
				}

				// orders the notes from the lowest to the highest pitch
				for (int i=0; i<presentNote.size(); i++)
				{
					for (int j=0; j<presentNote.size()-1; j++)
					{
						if ( presentNote.get(j).getMidiPitch() > presentNote.get(j+1).getMidiPitch() )
						{
							Note transNote = presentNote.get(j);
							presentNote.set(j, presentNote.get(j+1));
							presentNote.set(j+1, transNote);
						}
					}
				}

				// assigns each of the note of the marker to a voice
				// Within a marker, the notes are far apart so we can assign the lowest note to the lowest voice and so on.
				for (int i=0; i<presentNote.size(); i++)
				{
					allocatedVoice.set(extract.indexOf(presentNote.get(i)), i);
					currentVoices.add(presentNote.get(i));
					futureVoices.add(presentNote.get(i));
				}

				// To each marker is assigned a window, zone where assignment depends on the central marker
				// Each window is divided in two parts : before and after the marker
				postWindowNote.clear();
				preWindowNote.clear();
				// Creates the window after the marker ( postWindowNote ) and before the marker ( preWindowNote )
				for ( int noteNumber=0; noteNumber<extract.size(); noteNumber++)
				{
					// construction of the postWindow
					// the postWindow begins at the marker time, and ends when we have made half the way to the following marker.
					// if it is the last marker, the postWindow ends at the end of the piece.
					if ( markerNumber == numberOfMarker-1)
					{
						if ( ( extract.get(noteNumber).getMetricTime().isGreater(marker.get(markerNumber))))
						{
							postWindowNote.add(extract.get(noteNumber));
						}
					}
					if ( markerNumber != numberOfMarker-1)
					{
						if ( ( extract.get(noteNumber).getMetricTime().isGreater(marker.get(markerNumber))) && (extract.get(noteNumber).getMetricTime().isLessOrEqual(marker.get(markerNumber).add(marker.get(markerNumber+1)).div(2))) )
						{
							postWindowNote.add(extract.get(noteNumber));
						}
					}
					
					// construction of the preWindow
					// the preWindow begins half the way to the previous marker, and ends at the marker.
					// if it is the first marker, the window begins at the beginning of the piece.
					if ( markerNumber == 0)
					{
						if (  extract.get(noteNumber).getScoreNote().getMetricEndTime().isLessOrEqual(marker.get(markerNumber)))
						{
							preWindowNote.add(extract.get(noteNumber));
						}
					}
					if ( markerNumber != 0)
					{
						if ( ( extract.get(noteNumber).getMetricTime().isGreater(marker.get(markerNumber).add(marker.get(markerNumber-1)).div(2)) ) 
								&& ( extract.get(noteNumber).getScoreNote().getMetricEndTime().isLessOrEqual(marker.get(markerNumber))) )
						{
							preWindowNote.add(extract.get(noteNumber));
						}
					}
				}
				
				// orders the notes of the postWindow by metric time
				for (int i=0; i<postWindowNote.size(); i++)
				{
					for (int j=0; j<postWindowNote.size()-1; j++)
					{
						if ( postWindowNote.get(j).getMetricTime().isGreater(postWindowNote.get(j+1).getMetricTime()) )
						{
							Note transNote = postWindowNote.get(j);
							postWindowNote.set(j, postWindowNote.get(j+1));
							postWindowNote.set(j+1, transNote);
						}
					}
				}
				
				// orders the notes of the preWindow by reverse metric end time
				for (int i=0; i<preWindowNote.size(); i++)
				{
					for (int j=0; j<preWindowNote.size()-1; j++)
					{
						if ( preWindowNote.get(j).getScoreNote().getMetricEndTime().isLess(preWindowNote.get(j+1).getScoreNote().getMetricEndTime()) )
						{
							Note transNote = preWindowNote.get(j);
							preWindowNote.set(j, preWindowNote.get(j+1));
							preWindowNote.set(j+1, transNote);
						}
					}
				}
				
				// assign the voices for the notes in the postWindow
				while ( postWindowNote.size() != 0)
				{
					Rational actualTime = postWindowNote.get(0).getMetricTime();
					List<Note> actualChord = new ArrayList<Note>(); 
					
					// the voice assignment is chord by chord : we consider all the chord beginning at the same time.
					// that allows to assign different voices to each of these notes

					// gets the actual chord and removes it in the window-list of the notes to assign
					while ( postWindowNote.get(0).getMetricTime().isEqual(actualTime)==true)
					{
						actualChord.add(postWindowNote.get(0));
						postWindowNote.remove(0);
						if ( postWindowNote.size()==0)
						{
							break;
						}
					}

					List<Integer> availableVoices = new ArrayList<Integer>();

					// determines the voices available to assign the notes of this chord.
					// A voice is not available when the note it sings has a metric end time greater than the metric time of the chord.
					for ( int numberVoice=0; numberVoice<currentVoices.size(); numberVoice++)
					{				
						if ( currentVoices.get(numberVoice).getScoreNote().getMetricEndTime().isLessOrEqual(actualTime))
						{
							availableVoices.add(numberVoice);
						}
					}

					List<List<Double>> listPostCostFunction = new ArrayList<List<Double>>();

					// calculate the probability of assignment given the transition matrices for each note of the chord to each available voice.
					for ( int i=0; i<actualChord.size(); i++)
					{
						List<Double> costFunction = new ArrayList<Double>();
						for ( int numberVoice=0; numberVoice<availableVoices.size(); numberVoice++)
						{
							// probability of transition between two notes
							double result = transMat.getMatrixTrans()[currentVoices.get(availableVoices.get(numberVoice)).getMidiPitch()][actualChord.get(i).getMidiPitch()];
							// probability of belonging to a voice given the note's pitch
							result+= 0.5*transMat.getMatrixProbVoice()[actualChord.get(i).getMidiPitch()][availableVoices.get(numberVoice)];
							costFunction.add(result);
						}
						
						listPostCostFunction.add(costFunction);
					}

					// assign the notes of the actual chord
					while ( actualChord.size() != 0 && availableVoices.size() != 0)
					{
						List<Double> costMax = new ArrayList<Double>();
						// for each note, find the best cost
						for (int i=0; i<actualChord.size(); i++)
						{
							double max = listPostCostFunction.get(i).get(0);
							for ( int j=0; j<listPostCostFunction.get(i).size(); j++)
							{
								if ( listPostCostFunction.get(i).get(j)>max)
								{
									max=listPostCostFunction.get(i).get(j);
								}
							}
							costMax.add(max);

						}

						// find the best of all costs
						double globMax = costMax.get(0);
						for (int i=0; i<costMax.size(); i++)
						{
							if ( costMax.get(i)>globMax)
							{
								globMax=costMax.get(i);
							}
						}

						// assigns the the note with the best of all costs and then removes it
						Note noteToAssign = actualChord.get(costMax.indexOf(globMax));
						int voiceToAssign = availableVoices.get(listPostCostFunction.get(costMax.indexOf(globMax)).indexOf(globMax));
						allocatedVoice.set(extract.indexOf(noteToAssign), voiceToAssign);

						// removes the voice and the note assigned in the listCostFunction
						for ( int i=0; i<listPostCostFunction.size(); i++)
						{
							listPostCostFunction.get(i).remove(availableVoices.indexOf(voiceToAssign));
						}
						listPostCostFunction.remove(actualChord.indexOf(noteToAssign));

						actualChord.remove(noteToAssign);
						availableVoices.remove(availableVoices.indexOf(voiceToAssign));

						currentVoices.set(voiceToAssign, noteToAssign);
					}

				}

				// assign the voices for the notes in the preWindow
				while ( preWindowNote.size() != 0 )
				{
					Rational actualReverseTime = preWindowNote.get(0).getScoreNote().getMetricEndTime();
					List<Note> actualChord = new ArrayList<Note>();

					// gets the actual chord and removes it in the window-list of the notes to assign
					while ( preWindowNote.get(0).getScoreNote().getMetricEndTime().isEqual(actualReverseTime)==true)
					{
						actualChord.add(preWindowNote.get(0));
						preWindowNote.remove(0);
						if ( preWindowNote.size()==0)
						{
							break;
						}
					}

					List<Integer> availableVoices = new ArrayList<Integer>();

					// determines the voices available to assign the notes of this chord.
					for ( int numberVoice=0; numberVoice<futureVoices.size(); numberVoice++)
					{
						if ( futureVoices.get(numberVoice).getMetricTime().isGreaterOrEqual(actualReverseTime))
						{
							availableVoices.add(numberVoice);
						}
					}

					List<List<Double>> listPreCostFunction = new ArrayList<List<Double>>();

					// calculate the probability of assignment given the transition matrices for each note of the chord.
					for ( int i=0; i<actualChord.size(); i++)
					{
						List<Double> costFunction = new ArrayList<Double>();
						for ( int numberVoice=0; numberVoice<availableVoices.size(); numberVoice++)
						{
							double result = transMat.getMatrixTrans()[actualChord.get(i).getMidiPitch()][futureVoices.get(availableVoices.get(numberVoice)).getMidiPitch()];
							result+= 0.5*transMat.getMatrixProbVoice()[actualChord.get(i).getMidiPitch()][availableVoices.get(numberVoice)];
							costFunction.add(result);
						}
						listPreCostFunction.add(costFunction);
					}
					

					// assign the notes of the chord
					while ( actualChord.size() != 0 && availableVoices.size() != 0)
					{
						List<Double> costMax = new ArrayList<Double>();
						// for each note, find the best cost
						for (int i=0; i<actualChord.size(); i++)
						{
							double max = listPreCostFunction.get(i).get(0);
							for ( int j=0; j<listPreCostFunction.get(i).size(); j++)
							{
								if ( listPreCostFunction.get(i).get(j)>max)
								{
									max=listPreCostFunction.get(i).get(j);
								}
							}
							costMax.add(max);

						}

						// find the best of all costs
						double globMax = costMax.get(0);
						for (int i=0; i<costMax.size(); i++)
						{
							if ( costMax.get(i)>globMax)
							{
								globMax=costMax.get(i);
							}
						}

						// assigns the the note with the best of all cost and then removes it
						Note noteToAssign = actualChord.get(costMax.indexOf(globMax));
						int voiceToAssign = availableVoices.get(listPreCostFunction.get(costMax.indexOf(globMax)).indexOf(globMax));
						allocatedVoice.set(extract.indexOf(noteToAssign), voiceToAssign);

						// removes the voice and the note assigned in the listCostFunction
						for ( int i=0; i<listPreCostFunction.size(); i++)
						{
							listPreCostFunction.get(i).remove(availableVoices.indexOf(voiceToAssign));
						}
						listPreCostFunction.remove(actualChord.indexOf(noteToAssign));

						actualChord.remove(noteToAssign);
						availableVoices.remove(availableVoices.indexOf(voiceToAssign));

						futureVoices.set(voiceToAssign, noteToAssign);
					}

				}

		}
		System.out.println(" allocatedVoice = " + allocatedVoice.size() + allocatedVoice);
	}
	
	public TransitionMatrix getTransMat() {
		return transMat;
	}
	
	public void setTransitionMatrix( TransitionMatrix transitionMatrix) {
		transMat = transitionMatrix;
	}

	public int getMaxNumberOfVoice() {
		return maxNumberOfVoice;
	}
	
	public void setMaxNumberOfVoice(int numberVoice) {
		maxNumberOfVoice = numberVoice;
	}
}