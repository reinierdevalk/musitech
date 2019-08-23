package de.uos.fmt.musitech.data.voiceSeparationAlgorithms;

import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class perform the Chew and Wu's algorithm.
 * This is a voice separation algorithm consisting in sequencing the piece in fragments, witch are called contigs. The contigs are delimited
 * by boundaries, created when the voice number is changing or when the voice status is changing. Fragments are created in each contig,
 * each fragment gathering the notes of a same voice, considering that there is none voice-crossing within a fragment.
 * The algorithm then defines maximal voice contigs, which are contigs in which all voices are present. Then the voice assignment is realized within this contigs.
 * The assignment is then propagated from each maximal voice contig to each neighbor contig, minimizing a cost function.
 * @author gauthier
 *
 */
public class ChewAndWuAlgorithm {

	public ChewAndWuAlgorithm() {
	;
	}
	
	/**
	 * 
	 * @param extract : list of notes on which the algorithm is performed
	 * @param maxNumberVoice : maximal number of voices in the extract
	 * @param totalDur : the total duration of the extract
	 * @param beat : the minimal beat of the extract.
	 */
	public void algorithmCW( List<Note> extract, int maxNumberVoice, Rational totalDur, Rational beat) {


		// The list with the voice assigned. The element i of this list is the voice assigned to the note i.
		// The -1 assignment means that the note hasn't been assigned to a voice.
		List<Integer> allocatedVoice = new ArrayList<Integer>(extract.size());
		for ( int i=0; i< extract.size(); i++)
		{
			allocatedVoice.add(-1);
		}

		// 1st Step : Find the boundaries and create the contigs.
		// Find the segmentation boundaries
		Rational time=new Rational(0,1);
		List<Rational> boundaries = new ArrayList<Rational>();
		int voiceNumber=0;
		int voiceNumberPre=0;
		// add boundaries when the number of voices changes
		while(time.isLess(totalDur)==true)
		{
			for ( int noteNumber=0; noteNumber<extract.size(); noteNumber++)
			{
				// determination of the number of present voices at this time
				if ( (extract.get(noteNumber).getMetricTime().isLessOrEqual(time)) && (time.isLess(extract.get(noteNumber).getScoreNote().getMetricEndTime())) )
				{
					voiceNumber++;
				}
			}

			// if the number of voices change, then we have a boundaries
			if ( voiceNumber != voiceNumberPre)
			{
				boundaries.add(time);
			}
			voiceNumberPre=voiceNumber;
			voiceNumber=0;
			time = time.add(beat);
		}
		// add boundaries when the voice status changes
		int prevSize = boundaries.size()+1;
		while ( prevSize != boundaries.size())
		{
			for ( int i=0; i< boundaries.size(); i++)
			{
				prevSize = boundaries.size();
				for ( int noteNumber=0; noteNumber<extract.size(); noteNumber++)
				{
					if ( extract.get(noteNumber).getMetricTime().isLess(boundaries.get(i)) && extract.get(noteNumber).getScoreNote().getMetricEndTime().isGreater(boundaries.get(i)))
					{
						if ( boundaries.contains(extract.get(noteNumber).getMetricTime()) == false)
						{
							boundaries.add(extract.get(noteNumber).getMetricTime());
						}
						if ( boundaries.contains(extract.get(noteNumber).getScoreNote().getMetricEndTime()) == false)
						{
							boundaries.add(extract.get(noteNumber).getScoreNote().getMetricEndTime());
						}
					}
				}
			}
		}
		// removes the useless boundaries ( boundaries when the voice number is maximal )
		List<Rational> copyBoundaries = boundaries;
		for ( int i=0; i< boundaries.size(); i++)
		{
			int voiceNumberBefore=0;
			int voiceNumberAfter=0;
			for ( int noteNumber=0; noteNumber<extract.size(); noteNumber++)
			{
				// determination of the number of present voices at this time
				Rational timeBefore=boundaries.get(boundaries.size()-i-1);
				timeBefore = timeBefore.sub(beat);
				Rational timeAfter=boundaries.get(boundaries.size()-i-1);
				if ( (extract.get(noteNumber).getMetricTime().isLessOrEqual(timeBefore)) && (timeBefore.isLess(extract.get(noteNumber).getScoreNote().getMetricEndTime())) )
				{
					voiceNumberBefore++;
				}
				if ( (extract.get(noteNumber).getMetricTime().isLessOrEqual(timeAfter)) && (timeAfter.isLess(extract.get(noteNumber).getScoreNote().getMetricEndTime())) )
				{
					voiceNumberAfter++;
				}
			}
			if ( voiceNumberBefore == voiceNumberAfter && voiceNumberBefore == maxNumberVoice)
			{
				copyBoundaries.remove(boundaries.size()-i-1);
			}
		}
		boundaries=copyBoundaries;

		// the end of the extract is the end of the last contig, so it is a boundaries
		boundaries.add(totalDur);

		// Hereafter, we need to order the boundaries by crescent time
		for (int i=0; i<boundaries.size(); i++)
		{
			for (int j=0; j<boundaries.size()-1; j++)
			{
				if ( boundaries.get(j).isGreater(boundaries.get(j+1)))
				{
					Rational transBoundaries = boundaries.get(j);
					boundaries.set(j, boundaries.get(j+1));
					boundaries.set(j+1, transBoundaries);
				}
			}
		}
		System.out.println( "boundaries = " + boundaries);


		List<List<Note>> contig = new ArrayList<List<Note>>(); // list of contigs
		List<List<Integer>> contigVoice = new ArrayList<List<Integer>>(); // list with the voice assigned in each contigs
		List<Boolean> contigDone = new ArrayList<Boolean>(); // list to know if the assignment has been made for each contig.

		// Once the boundaries are ordered : Create the contigs and add the notes to the corresponding contig;
		for ( int i=0; i<boundaries.size()-1; i++)
		{
			contig.add( new ArrayList<Note>());
			contigVoice.add( new ArrayList<Integer>());
			contigDone.add(false);
		}

		for ( int noteNumber=0; noteNumber<extract.size(); noteNumber++)
		{
			for ( int i=0; i< boundaries.size()-1; i++ )
			{
				if ( extract.get(noteNumber).getMetricTime().isLess(boundaries.get(i+1)) && extract.get(noteNumber).getScoreNote().getMetricEndTime().isGreater(boundaries.get(i)))
				{
					contig.get(i).add(extract.get(noteNumber));
				}
			}
		}

		// 2nd Step : Create the fragments within each contig.
		// The fragments are created by considering that within a contig, the voices remains the same, without voice crossings.
		List<List<List<Note>>> fragments = new ArrayList<List<List<Note>>>();
		for ( int i=0; i<contig.size(); i++)
		{
			fragments.add( new ArrayList<List<Note>>());
		}

		for ( int i=0; i<contig.size(); i++)
		{
			Rational timeContig=boundaries.get(i);
			List<Note> notesToAssign = contig.get(i);
			while ( timeContig.isLess(boundaries.get(i+1)))
			{
				// determines the present voices at this time
				List<Note> presentNote = new ArrayList<Note>();
				for (int noteNumber=0; noteNumber<notesToAssign.size(); noteNumber++)
				{
					if ( notesToAssign.get(noteNumber).getMetricTime().isLessOrEqual(timeContig) && notesToAssign.get(noteNumber).getScoreNote().getMetricEndTime().isGreater(timeContig))
					{
						presentNote.add(notesToAssign.get(noteNumber));
					}
				}

				Rational endTime = new Rational(totalDur);
				// orders the present voices from the lowest to the highest
				for ( int k=0; k<presentNote.size(); k++)
				{
					for ( int l=0; l<presentNote.size()-1; l++)
					{
						if ( presentNote.get(l).getMidiPitch() > presentNote.get(l+1).getMidiPitch())
						{
							Note transNote = presentNote.get(l);
							presentNote.set(l, presentNote.get(l+1));
							presentNote.set(l+1, transNote);
						}
					}
				}
				// assign the fragments
				// the lowest fragments corresponds to the lowest voice
				for ( int k=0; k< presentNote.size(); k++)
				{
					// if this is the first note to assign, the fragment need to be initialized
					if ( timeContig.isEqual(boundaries.get(i)))
					{
						fragments.get(i).add( new ArrayList<Note>());
					}
					if ( fragments.get(i).get(k).contains(presentNote.get(k)) == false)
					{
						fragments.get(i).get(k).add(presentNote.get(k));
					}
					if ( presentNote.get(k).getScoreNote().getMetricEndTime().isLess(endTime))
					{
						endTime = presentNote.get(k).getScoreNote().getMetricEndTime();
					}
				}
				
				timeContig = endTime;
			}

			// orders the notes within each fragment by metric time
			for ( int numberFragment=0; numberFragment<fragments.get(i).size(); numberFragment++)
			{
				for ( int k=0; k<fragments.get(i).get(numberFragment).size(); k++)
				{
					for ( int l=0; l<fragments.get(i).get(numberFragment).size()-1; l++)
					{
						if ( fragments.get(i).get(numberFragment).get(l).getMetricTime().isGreater(fragments.get(i).get(numberFragment).get(l+1).getMetricTime()))
						{
							Note transNote = fragments.get(i).get(numberFragment).get(l);
							fragments.get(i).get(numberFragment).set(l, fragments.get(i).get(numberFragment).get(l+1));
							fragments.get(i).get(numberFragment).set(l+1, transNote);
						}
					}
				}
			}
		}
		System.out.println( " fragments = " + fragments);


		// 3rd Step : Assign the notes in the maximal voice contigs.

		List<List<Note>> maxContig = new ArrayList<List<Note>>();
		for ( int i=0; i<contig.size(); i++)
		{
			// If all the voices are present, then it is a maximal voice contig
			// When we have a maximal voice contig, the assignment is easy : the lowest voice to the lowest note and so on.
			// Here we just have to assign the lowest voice to the lowest fragment and so on
			if ( fragments.get(i).size() == maxNumberVoice)
			{
				for ( int k=0; k<fragments.get(i).size(); k++)
				{
					for ( int l=0; l<fragments.get(i).get(k).size(); l++)
					{
						allocatedVoice.set(extract.indexOf(fragments.get(i).get(k).get(l)), k);
					}
					contigVoice.get(i).add(k);
				}
				maxContig.add(contig.get(i));
				contigDone.set(i, true);
			}
		}
		
		// 4th Step : Spread the assignment and assign the notes within the other contigs.
		// We first assigned the voices in the contigs neighbors to the maximal voice contigs, then to the following neighbors, and so on.
		for ( int k=0; k<contig.size() ; k++)
		{
			// k=contig.size() is the worst case for the loop, that would mean that we only have one maximal voice contig at the very end or the very beginning of the extract
			// So we need to break this loop before if the voices are already assigned within each contig.
			boolean progress = false; // boolean which indicates if the contigs are assigned or not.
			for ( int i=0; i<maxContig.size(); i++)
			{
				// A - forward propagation

				if ( (contig.indexOf(maxContig.get(i))+k+1) <= contig.size()-1 )
				{
					if ( contigDone.get(contig.indexOf(maxContig.get(i))+k+1) == false )
					{
						// enumeration of all the possibilities of assignment
						int fragmentFBef = fragments.get(contig.indexOf(maxContig.get(i))+k).size();
						int fragmentFAft = fragments.get(contig.indexOf(maxContig.get(i))+k+1).size();
						int fragmentFMax = fragmentFAft;
						int fragmentFMin = fragmentFBef;
						if ( fragmentFBef >= fragmentFAft )
						{
							fragmentFMax = fragmentFBef;
							fragmentFMin = fragmentFAft;
						}

						int fragmentFMinFict = fragmentFMin+1;
						int numberOfPossF = (int) java.lang.Math.pow( fragmentFMinFict, fragmentFMax);

						// initialization of the list of possibilities
						List<List<Integer>> listOfPossF = new ArrayList< List<Integer>>(numberOfPossF);
						for ( int j=0; j<numberOfPossF; j++) {
							List<Integer> possi = new ArrayList<Integer>();
							for ( int l=0; l<fragmentFMax; l++)	{
								possi.add(0);
							}
							listOfPossF.add(possi);
						}

						// 1rst : Creation of all the mathematical assignments, even the impossible ones
						for ( int j = 0; j < fragmentFMinFict; j++) { 
							for ( int l = 0; l < fragmentFMax; l++) { 
								int poss = j*(int)(java.lang.Math.pow(fragmentFMinFict, (fragmentFMax - 1 - l)));
								while (poss<numberOfPossF) {
									for (int count = 0; count < java.lang.Math.pow(fragmentFMinFict, (fragmentFMax - 1 - l)); count++)	{
										if (j == fragmentFMinFict - 1)	{
											listOfPossF.get(poss).set(l, -1); 
										}
										if (j != fragmentFMinFict - 1) {
											listOfPossF.get(poss).set(l, j); 
										}
										if (count != (java.lang.Math.pow(fragmentFMinFict, (fragmentFMax - 1 - l)) - 1))	{
											poss++;
											//  }
										}
									}
									poss += 1 + java.lang.Math.pow(fragmentFMinFict, (maxNumberVoice - l)) - 
											java.lang.Math.pow(fragmentFMinFict, (maxNumberVoice - l - 1));
								}
							}
						}
						// 2nd : suppression of the impossible assignments
						// A fragment can be linked only to one other fragment
						List<Integer> suprF = new ArrayList<Integer>(); // List which contains all the elements to filter
						// fills the list supr with the position of the impossible assignments in listOfPoss
						for (int poss = 0; poss < numberOfPossF; poss++) {
							for (int j = 0; j < fragmentFMin; j++) {
								int used = 0;
								for (int nbV= 0; nbV < maxNumberVoice; nbV++) {
									if (listOfPossF.get(poss).get(nbV) == j) {
										used++;
									}
								}
								if (used == 0 || used > 1 ) {
									suprF.add(poss); 
									break;
								}
							}
						}
						// now removes the elements in the listOfPoss which are in the list supr
						for (int j = 0; j < suprF.size(); j++) {
							listOfPossF.remove((int)suprF.get(suprF.size() - j - 1)); // removes the elements from the end to avoid conflicts
						}

						// Now that we have the possibilities, we evaluate the cost for each possibility
						List<Integer> costsF = new ArrayList<Integer>();

						for ( int j=0; j<listOfPossF.size(); j++)
						{
							int result =0;
							for ( int l=0; l<listOfPossF.get(j).size(); l++)
							{
								// first note and last note of the two fragments that we want to connect.
								Note note0 = new Note();
								Note note1 = new Note();
								if ( fragmentFBef >= fragmentFAft )
								{
									note0 = fragments.get(contig.indexOf(maxContig.get(i))+k).get(l).get(fragments.get(contig.indexOf(maxContig.get(i))+k).get(l).size()-1);
									if ( listOfPossF.get(j).get(l) != -1 )
									{
										note1 = fragments.get(contig.indexOf(maxContig.get(i))+k+1).get(listOfPossF.get(j).get(l)).get(0);
									}
									if ( listOfPossF.get(j).get(l) == -1 )
									{
										note1=null;
									}

								}
								if ( fragmentFBef < fragmentFAft )
								{
									note0 = fragments.get(contig.indexOf(maxContig.get(i))+k+1).get(listOfPossF.get(j).get(l)).get(0);
									if ( listOfPossF.get(j).get(l) != -1 )
									{
										note1 = fragments.get(contig.indexOf(maxContig.get(i))+k).get(l).get(fragments.get(contig.indexOf(maxContig.get(i))+k).get(l).size()-1);	
									}
									if ( listOfPossF.get(j).get(l) == -1 )
									{
										note1 = null;
									}
								}
								// assigns the costs : if the two notes are segments of the same longer note : -1000
								// if one of the two notes is null : +1000
								// else the absolute difference between the pitches of the two notes
								if ( note0 == note1 ) {
									result+= -1000;
								}else if ( note0 == null || note1 == null ) {
									result+= 1000;
								}else { 
									result+= Math.abs(note0.getMidiPitch()-note1.getMidiPitch()); }
							}
							costsF.add(result);
						}

						// Find the best cost
						int minCostF = costsF.get(0);
						for ( int j=1; j<costsF.size(); j++)
						{
							if ( costsF.get(j) < minCostF)
							{
								minCostF = costsF.get(j);
							}
						}

						// Attribute the fragments considering the best cost
						if ( fragmentFBef >= fragmentFAft )
						{
							for ( int j=0; j<fragments.get(i+k).size(); j++)
							{
								if ( listOfPossF.get(costsF.indexOf(minCostF)).get(j) != -1)
								{
									for ( int l=0; l<fragments.get(contig.indexOf(maxContig.get(i))+k+1).get(listOfPossF.get(costsF.indexOf(minCostF)).get(j)).size(); l++)
									{
										Note noteToAssign = fragments.get(contig.indexOf(maxContig.get(i))+k+1).get(listOfPossF.get(costsF.indexOf(minCostF)).get(j)).get(l);
										Integer voiceToAssign = contigVoice.get(contig.indexOf(maxContig.get(i))+k).get(j);
										allocatedVoice.set(extract.indexOf(noteToAssign), voiceToAssign);
									}
								}
								contigVoice.get(contig.indexOf(maxContig.get(i))+k+1).add(contigVoice.get(contig.indexOf(maxContig.get(i))+k).get(j));
							}
						}

						if ( fragmentFBef < fragmentFAft )
						{
							for ( int j=0; j<fragments.get(contig.indexOf(maxContig.get(i))+k+1).size(); j++)
							{
								if ( listOfPossF.get(costsF.indexOf(minCostF)).get(j) != -1)
								{
									for ( int l=0; l<fragments.get(contig.indexOf(maxContig.get(i))+k+1).get(j).size(); l++)
									{
										Note noteToAssign = fragments.get(contig.indexOf(maxContig.get(i))+k+1).get(j).get(l);
										Integer voiceToAssign = contigVoice.get(contig.indexOf(maxContig.get(i))+k).get(listOfPossF.get(costsF.indexOf(minCostF)).get(j));
										allocatedVoice.set(extract.indexOf(noteToAssign), voiceToAssign);
									}
								}
								contigVoice.get(contig.indexOf(maxContig.get(i))+k+1).add(contigVoice.get(contig.indexOf(maxContig.get(i))+k).get(listOfPossF.get(costsF.indexOf(minCostF)).get(j)));
							}
						}
						contigDone.set(contig.indexOf(maxContig.get(i))+k+1, true);
						progress = true;
					}
				}

				// B - backward propagation

				if ( (contig.indexOf(maxContig.get(i))-k-1) >= 0 )
				{
					if ( contigDone.get(contig.indexOf(maxContig.get(i))-k-1) == false )
					{
						// enumeration of all the possibilities of assignment
						int fragmentBBef = fragments.get(contig.indexOf(maxContig.get(i))-k-1).size();
						int fragmentBAft = fragments.get(contig.indexOf(maxContig.get(i))-k).size();
						int fragmentBMax = fragmentBAft;
						int fragmentBMin = fragmentBBef;
						if ( fragmentBBef >= fragmentBAft )
						{
							fragmentBMax = fragmentBBef;
							fragmentBMin = fragmentBAft;
						}

						int fragmentBMinFict = fragmentBMin+1;
						int numberOfPossB = (int) java.lang.Math.pow( fragmentBMinFict, fragmentBMax);

						// initialization of the list of possibilities
						List<List<Integer>> listOfPossB = new ArrayList< List<Integer>>(numberOfPossB);
						for ( int j=0; j<numberOfPossB; j++) {
							List<Integer> possi = new ArrayList<Integer>();
							for ( int l=0; l<fragmentBMax; l++)	{
								possi.add(0);
							}
							listOfPossB.add(possi);
						}

						// 1rst : Creation of all the mathematical assignments, even the impossible ones
						for ( int j = 0; j < fragmentBMinFict; j++) { 
							for ( int l = 0; l < fragmentBMax; l++) { 
								int poss = j*(int)(java.lang.Math.pow(fragmentBMinFict, (fragmentBMax - 1 - l)));
								while (poss<numberOfPossB) {
									for (int count = 0; count < java.lang.Math.pow(fragmentBMinFict, (fragmentBMax - 1 - l)); count++)	{
										if (j == fragmentBMinFict - 1)	{
											listOfPossB.get(poss).set(l, -1); 
										}
										if (j != fragmentBMinFict - 1) {
											listOfPossB.get(poss).set(l, j); 
										}
										if (count != (java.lang.Math.pow(fragmentBMinFict, (fragmentBMax - 1 - l)) - 1))	{
											poss++;
											//  }
										}
									}
									poss += 1 + java.lang.Math.pow(fragmentBMinFict, (maxNumberVoice - l)) - 
											java.lang.Math.pow(fragmentBMinFict, (maxNumberVoice - l - 1));
								}
							}
						}
						// 2nd : suppression of the impossible assignments
						// A fragment can be linked only to one other fragment
						List<Integer> suprB = new ArrayList<Integer>(); // List which contains all the elements to filter
						// fills the list supr with the position of the impossible assignments in listOfPoss
						for (int poss = 0; poss < numberOfPossB; poss++) {
							for (int j = 0; j < fragmentBMin; j++) {
								int used = 0;
								for (int nbV= 0; nbV < maxNumberVoice; nbV++) {
									if (listOfPossB.get(poss).get(nbV) == j) {
										used++;
									}
								}
								if (used == 0 || used > 1 ) {
									suprB.add(poss); 
									break;
								}
							}
						}
						// now removes the elements in the listOfPoss which are in the list supr
						for (int j = 0; j < suprB.size(); j++) {
							listOfPossB.remove((int)suprB.get(suprB.size() - j - 1)); // removes the elements from the end to avoid conflicts
						}

						// Now that we have the possibilities, we evaluate the cost for each possibility
						List<Integer> costsB = new ArrayList<Integer>();

						for ( int j=0; j<listOfPossB.size(); j++)
						{
							int result =0;
							for ( int l=0; l<listOfPossB.get(j).size(); l++)
							{
								Note note0 = new Note();
								Note note1 = new Note();
								if ( fragmentBBef >= fragmentBAft )
								{
									note1 = fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(l).get(fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(l).size()-1);
									if ( listOfPossB.get(j).get(l) != -1 )
									{
										note0 = fragments.get(contig.indexOf(maxContig.get(i))-k).get(listOfPossB.get(j).get(l)).get(0);
									}
									if ( listOfPossB.get(j).get(l) == -1 )
									{
										note0=null;
									}

								}

								if ( fragmentBBef < fragmentBAft )
								{

									note1 = fragments.get(contig.indexOf(maxContig.get(i))-k).get(l).get(0);
									if ( listOfPossB.get(j).get(l) != -1 )
									{
										 note0 = fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(listOfPossB.get(j).get(l)).get(fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(listOfPossB.get(j).get(l)).size()-1);
									}
									if ( listOfPossB.get(j).get(l) == -1 )
									{
										note0 = null;
									}
								}
								// assigns the costs : if the two notes are segments of the same longer note : -1000
								// if one of the two notes is null : +1000
								// else the absolute difference between the pitches of the two notes
								if ( note0 == note1 ) {
									result+= -1000;
								}else if ( note0 == null || note1 == null ) {
									result+= 1000;
								}else { 
									result+= Math.abs(note0.getMidiPitch()-note1.getMidiPitch()); }
							}
							costsB.add(result);
						}

						// Find the best cost
						int minCostB = costsB.get(0);
						for ( int j=1; j<costsB.size(); j++)
						{
							if ( costsB.get(j) < minCostB)
							{
								minCostB = costsB.get(j);
							}
						}

						// Attribute the fragments considering the best cost
						if ( fragmentBBef >= fragmentBAft )
						{
							for ( int j=0; j<fragments.get(contig.indexOf(maxContig.get(i))-k-1).size(); j++)
							{
								if ( listOfPossB.get(costsB.indexOf(minCostB)).get(j) != -1)
								{
									for ( int l=0; l<fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(j).size(); l++)
									{
										Note noteToAssign = fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(j).get(l);
										Integer voiceToAssign = contigVoice.get(contig.indexOf(maxContig.get(i))-k).get(listOfPossB.get(costsB.indexOf(minCostB)).get(j));
										allocatedVoice.set(extract.indexOf(noteToAssign), voiceToAssign);

									}
								}
								contigVoice.get(contig.indexOf(maxContig.get(i))+k+1).add(contigVoice.get(contig.indexOf(maxContig.get(i))-k).get(listOfPossB.get(costsB.indexOf(minCostB)).get(j)));
							}
						}

						if ( fragmentBBef < fragmentBAft )
						{
							for ( int j=0; j<fragments.get(contig.indexOf(maxContig.get(i))-k).size(); j++)
							{
								if ( listOfPossB.get(costsB.indexOf(minCostB)).get(j) != -1)
								{
									for ( int l=0; l<fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(listOfPossB.get(costsB.indexOf(minCostB)).get(j)).size(); l++)
									{
										Note noteToAssign = fragments.get(contig.indexOf(maxContig.get(i))-k-1).get(listOfPossB.get(costsB.indexOf(minCostB)).get(j)).get(l);
										Integer voiceToAssign = contigVoice.get(contig.indexOf(maxContig.get(i))-k).get(j);
										allocatedVoice.set(extract.indexOf(noteToAssign), voiceToAssign);
									}
								}
								contigVoice.get(contig.indexOf(maxContig.get(i))-k-1).add(contigVoice.get(contig.indexOf(maxContig.get(i))-k).get(j));
							}
						}
						progress = true;
						contigDone.set(contig.indexOf(maxContig.get(i))-k-1, true);
					}
				}
			}
			if (progress == false)
			{
				// if the voices are assigned in all the contigs, we don't need to continune the assignment, so the algorithm ends.
				break;
			}
		}

		System.out.println(" allocatedVoice = " + allocatedVoice);
	}

}
