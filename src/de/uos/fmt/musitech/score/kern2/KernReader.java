package de.uos.fmt.musitech.score.kern2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class reads a Kern file into a List of notes (or a few other
 * structures).
 * 
 * @author Tillman Weyde & Aline Honingh
 */
public class KernReader {

	private static final boolean DEBUG = false;
	private static URL url;

	/**
	 * constructor
	 */
	private KernReader() {

	}

	/**
	 * Imports the **kern file and returns NotationVoice.
	 * Non-pitch-classes such as rests or bar lines, are encoded by the number
	 * -1 At the moment, if there are more than one option given for a specific
	 * note, the program encodes the last option Limitations: reads **kern files
	 * consisting of a fixed number of input streams (voices/spines)
	 * 
	 * @param url The file to read.
	 * @return the list of pitch classes
	 */
	public static NotationSystem getNotesFromKern(URL argUrl) {
		url = argUrl;
		boolean read = true;
		boolean slur_end_flag = false;
//		boolean createArray = true;
		int sp_cnt = 0; // spine number
		//int rest = -1; // non pitch classes such as rests and barlines are
						// encoded by the number -1
		int pc = 100; // default value of pc is set to 100 such that non-pc's
						// (such as barlines or rests) can be easily localized 
//		ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
//		output.add(new ArrayList<Integer>());// create new ArrayList for first spine

		// variables for Musitech objects
		List<NotationVoice> voiceList = new ArrayList<NotationVoice>();
		SlurContainer slur_cont = null;
		Rational time = Rational.ZERO;
		Rational leastDuration = Rational.MAX_VALUE;
		int l_cnt = 1; // for debugging: line position in file
		int c_cnt = 1; // for debugging: character position in line

		// Store the fileName
		// fileName = file.getName();
		// fileName = url.getFile();
		// System.out.println("The name of the kern file is: "+fileName);
		// fileContent = toString(url.getContent());

		// check whether the file is readable
//		if (file.canRead()) {
//			// System.out.println("file readable");
//		} else {
//			throw new RuntimeException("sorry, file NOT readable!");
//		}

		// read the inputfile
		Reader reader = null;
		try {
			reader = new InputStreamReader(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// convert the byte stream to a character stream
		// InputStreamReader inputReader = new InputStreamReader(inputStream);

		// read the whole file, character by character
		try {
			ScoreNote scoreNote = null;
			Rational duration = new Rational(0);
			int a_int; 
			while((a_int = reader.read()) != -1) {
				char a = (char) a_int;
				c_cnt++; // count characters in line
				if (a == '*' || a == '!') { // start reading when line does not
											// start with * or !
					StringBuffer sb = new StringBuffer();
					while (a != '\t' && a != '\n') {
						sb.append(a);
						a = (char) reader.read();
						c_cnt++;
					}
					if (sb.toString().trim().equals("**kern"))
						voiceList.add(new NotationVoice());
					if (a == '\t')
						sp_cnt++;
					read = false;
				}
				if (read) {// begin translating notes into pitch numbers
					// parse any duration information, reading digit characters
					// and '.'s
					a = parseDuration(a, reader, duration);
					// if any duration was given, it is now in the duration
					// object.
					// variable a contains now the first character after any
					// duration info.

					// create or modify a score note, if necessary
					scoreNote = parseNote(time, scoreNote, duration, a);
					// score Note is now not null if there was a note name
					// variable 'a' is unchanged

					switch (a) {
					case 'a':
					case 'A':
						pc = 9;
						if(DEBUG) System.out.println("A");
						break;
					case 'b':
					case 'B':
						pc = 11;
						if(DEBUG) System.out.println("B");
						break;
					case 'c':
					case 'C':
						pc = 0;
						if(DEBUG) System.out.println("C");
						break;
					case 'd':
					case 'D':
						pc = 2;
						if(DEBUG) System.out.println("D");
						break;
					case 'e':
					case 'E':
						pc = 4;
						if(DEBUG) System.out.println("E");
						break;
					case 'f':
					case 'F':
						pc = 5;
						if(DEBUG) System.out.println("F");
						break;
					case 'g':
					case 'G':
						pc = 7;
						if(DEBUG) System.out.println("G");
						break;
					case '#':
						pc = pc + 1;
						if(DEBUG) System.out.println("sharp");
						scoreNote.setAlteration((byte) (scoreNote
								.getAlteration() + 1));
						break;
					case '-':
						pc = pc - 1;
						if(DEBUG) System.out.println("flat");
						scoreNote.setAlteration((byte) (scoreNote
								.getAlteration() - 1));
						break;
					case '{':
						if(slur_cont != null)
							System.out.println("WARNING: kern2.KernRader: slur started between end of previous slur. \n " +
									"Nested slurs are not supported, will end old slur here." +
									"line: " + l_cnt + ", char: "+ c_cnt + "in:\n" + url );
						slur_cont = new SlurContainer();
						voiceList.get(sp_cnt).addSlurContainer(slur_cont);
						if(DEBUG) System.out.println("START Segment");
						break;
					case '}':
						slur_end_flag = true;
						break;
					// add the pc to the output list and reset the pc:
					case '\t':
						// if there is a scoreNote, finish and save it
						if (scoreNote != null) {
							// add the note to the current voice 
							Note note = new Note(scoreNote, null);
							voiceList.get(sp_cnt).add(note);
							if(slur_cont!=null) slur_cont.add(note);
							if (leastDuration.isGreater(duration)) // update
								leastDuration = duration; // leastDuration
							scoreNote = null; // reset the scoreNote
						}
						if(slur_end_flag){
							slur_cont = null;
							slur_end_flag = false;
							if(DEBUG) System.out.println("END Segment");
						}
						duration = new Rational(); // reset duration
//						if (pc >= 0 && pc <= 11) {
//							output.get(sp_cnt).add(pc);
//						} else {
//							output.get(sp_cnt).add(rest);
//						}// System.out.println("character does not represent a pitch class");};
						sp_cnt = sp_cnt + 1; // tab means: go to the next spine
						pc = 100;// reset pc
//						if (createArray) {
//							output.add(new ArrayList<Integer>());
//						}// first line: create new arrayList for next spine
						break;
					// add the pc to the output list and reset the pc:
					case '\n':
						// if there is a scoreNote, finish and save it
						if (scoreNote != null) {
							// add the note to the voice and slur objects
							Note note = new Note(scoreNote, null);
							note.setPerformanceNote(scoreNote.toPerformanceNote(Piece.getDefaultPiece().getMetricalTimeLine()));
							voiceList.get(sp_cnt).add(note);
							if(slur_cont!=null) slur_cont.add(note);
							if (leastDuration.isGreater(duration)) // update leastDuration
								leastDuration = duration;
							scoreNote = null; // reset the scoreNote
						}
						if(slur_end_flag){
							slur_cont = null;
							slur_end_flag = false;
							if(DEBUG) System.out.println("END Segment");
						}
						duration = new Rational(); // reset duration
						// close the line
						if (!leastDuration.equals(Rational.MAX_VALUE)) {
							time = time.add(leastDuration);
							leastDuration = Rational.MAX_VALUE;
						}

//						if (pc >= 0 && pc <= 11) {
//							output.get(sp_cnt).add(pc);
//						} else {
//							output.get(sp_cnt).add(rest);
//						}
						;// System.out.println("character does not represent a pitch class");};
						// System.out.println("The pitch class is " + pc );
						pc = 100; // pc=100 at barlines or rests
//						createArray = false;
						break;
					default:
						break;// not a note
					} // end switch
				} // end if(read)
				if (a == '\n') {
					read = true;
					sp_cnt = 0;// reset spine number
					l_cnt++; // new line number
					c_cnt = 1; // reset char count
				}
				// System.out.println("file (" + fileName + "), character = "+  a);
			}

		} catch (IOException e) {
			System.out.println("file (" + url
								+ "): error reading character");
			e.printStackTrace();
		}

		// showNotes(voiceList);
		NotationSystem nsys = new NotationSystem();
		for (NotationVoice voice : voiceList) {
			NotationStaff nstaff = new NotationStaff(nsys);
			nstaff.add(voice);
			voice.setParent(nstaff);
			nsys.add(nstaff);
		}
		return nsys;
	}/* TODO complete and test method */

	/**
	 * Creates or modifies a note from a character using **kern format.
	 * 
	 * @param time The onset time of the note.
	 * @param scoreNote The scoreNote, null if none exists yet.
	 * @param duration The duration of the note.
	 * @param a The character to be interpreted.
	 * @return The score note created (if scoreNote argument was null) or the
	 *         one passed as scoreNote argument.
	 */
	private static ScoreNote parseNote(Rational time, ScoreNote scoreNote,
										Rational duration, char a) {
		// if there is a pitch name character
		if (a >= 'a' && a <= 'g' || a >= 'A' && a <= 'G' || a == 'r') {
			// create or continue a pitch name
			if (scoreNote == null) {
				scoreNote = new ScoreNote(time, duration, a, (byte) 0, (byte) 0);
				if (Character.isUpperCase(a))
					scoreNote.setOctave((byte) -1);
			} else {
				// continuation of a pitch name
				if (Character.toLowerCase(a) != scoreNote.getDiatonic())
					System.out
							.println("WARNING: MusicReader: note name characters do not match");
				if (Character.isUpperCase(a))
					scoreNote.setOctave((byte) (scoreNote.getOctave() - 1));
				if (Character.isLowerCase(a))
					scoreNote.setOctave((byte) (scoreNote.getOctave() + 1));
			} // end else
		} // end if(a >= 'a'
		return scoreNote;
	}

	/**
	 * Parses **kern note duration information from a Reader.
	 * 
	 * @param a The first char to parse.
	 * @param reader The reader to read further characters from.
	 * @param duration The rational where the duration information will be
	 *            saved. Will not be changed, if a is not a digit.
	 * @return The last char read (will be the same as )
	 * @throws IOException passe through from the reader, if reading fails.
	 */
	private static char parseDuration(char a, Reader reader, Rational duration)
			throws IOException {
		int denom = 0, numer = 1;
		while (a >= '0' && a <= '9') { // parse digits as decimal number
			denom = a - '0' + denom * 10;
			a = (char) reader.read();
		}
		if (denom > 0) { // continue only if there was a non null number.
			while (a == '.') {
				denom *= 2; // for all dots double the denominator
				// the next line works for multiple dots, too: 1/4 -> 3/8, 3/8
				// -> 7/16, 7/16 -> 15/32 ...
				numer = (numer + 1) * 2 - 1;
				a = (char) reader.read();
			}
			duration.setValue(numer, denom); // set the duration
		}
		return a; // last char read
	}

	public static void showNotes(List<NotationVoice> spineLists) {
		int spine = 0;
		for (NotationVoice spineContent : spineLists) {
			System.out.println("Spine" + (++spine));
			for (Object obj : spineContent) {
				System.out.println(obj);
			}
		}
		Piece piece = new Piece();
		piece.setScore(new NotationSystem(piece.getContext()));
		for (NotationVoice spineList : spineLists) {
			// for (int i = spineLists.size()-1; i >= 0 ; i--) {
			// NotationVoice spineContent = spineLists.get(i);
			// XXX @Sascha
			NotationStaff staff = new NotationStaff();
			staff.add(spineList);
			piece.getScore().add(staff);
		}
		Display disp;
		JFrame jFrame = new JFrame("Humdrum/Kern parsing result for file: "	+ url);
		try {
			disp = EditorFactory.createDisplay(piece);
			jFrame.add(new JScrollPane(disp.asComponent()));
		} catch (EditorConstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jFrame.setSize(600, 400);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
	}

}
