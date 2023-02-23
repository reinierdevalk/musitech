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
/**
 * File NotationChord.java
 */
package de.uos.fmt.musitech.data.score;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.lyrics.I18N;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsContainer;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * This contains a number of notes that form a chord. Notes need to have the
 * same metrical onset and duration.
 * 
 * @version $Revision: 8545 $, $Date: 2013-08-30 13:37:21 +0200 (Fri, 30 Aug 2013) $
 * @author Tillman Weyde
 * 
 * @hibernate.class table="NotationChord"
 * 
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 *  
 */
public class NotationChord extends BasicContainer<Note> implements Metrical, Containable, NotationContainer, Serializable,
        Cloneable, IMPEGSerializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7414313458251026705L;

	/**
     * This variable saves an entryChord which is sometimes used e.g. in flute
     * voices to give a hint about how to start the note (it is normally
     * rendered as a half-size note with a slur to the "real note"). The crux of
     * these objects is that they are displayed as normal notes (e.g. a 1/32),
     * but that they do not have a metrical duration of their own.
     */
    private NotationChord[] entryChord = null;

    /**
     * This variable is used to denote that this chord has a different duration
     * than the underlying note. This is at the moment used for appoggiaturas,
     * where the note has a duration of zero and the chord has the duration of
     * the to be drawn note.
     */
    private Rational trueDuration = null;

    /**
     * This constructs an empty NotationChord from a Context.
     * 
     * @param context
     */
    public NotationChord(Context context) {
        super(context, Note.class);
    }

    /**
     * This constructs an empty NotationChord from a Context.
     * 
     * @param context
     */
    public NotationChord() {
        super(null, Note.class);
    }

    /**
     * This constructs a NotationChord from Note and a Context.
     * 
     * @param context
     * @param n
     */
    public NotationChord(Context context, Note n) {
        super(context, Note.class);
        add(n);
    }


    // TODO check this
    @Override
	public Object clone() {
        NotationChord c = (NotationChord) super.clone();

        c.clear();
        for (int i = 0; i < size(); i++) {
            try {
                c.add((Note)get(i).clone(),false);
            } catch (CloneNotSupportedException e) {
                throw new Error(e.getMessage());
            }
        }

        return c;
    }

    /**
     * getDuration gets the Duration of this NotationChord i.e. the metrical
     * duration that is shared by all contained notes.
     * 
     * @return Rational
     */
    @Override
	public Rational getMetricDuration() {
        if (trueDuration != null)
            return trueDuration;
        if (size() == 0)
            return Rational.ZERO;
        assert size() >= 0;
        Rational duration = new Rational();
        ScoreNote scoreNote = get(0).getScoreNote();
        do {
            duration = duration.add(scoreNote.getMetricDuration());
            scoreNote = scoreNote.getTiedNote();
        } while (scoreNote != null);

        return duration;
    }

    /**
     * This method returns the duration of a chord without considering tied
     * notes (this is the difference to getMetricDuration()).
     * 
     * @return the metric duration of this (and only this) chord
     */
    public Rational getSingleMetricDuration() {
        if (trueDuration != null)
            return trueDuration;
        return get(0).getScoreNote().getMetricDuration();
    }

    /**
     * setDuration sets the Duration of this NotationChord i.e. the metrical
     * duration that is shared by all contained notes. TODO: what happens to
     * tied notes?
     * 
     * @return Rational
     */
    public void setMetricDuration(Rational newDuration) {
        Note n[] = toArray(new Note[] {});
        for (int i = 0; i < n.length; i++) {
            n[i].getScoreNote().setMetricDuration(newDuration);
        }
    }

    /**
     * getMetricTime gets the metric onset time of all contained notes.
     * 
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricTime()
     */
    @Override
	public Rational getMetricTime() {
        assert size() > 0;
        return get(0).getScoreNote().getMetricTime();
    }

    public void setMetricTime(Rational newOnset) {
        Note n[] = toArray(new Note[] {});
        for (int i = 0; i < n.length; i++) {
            n[i].getScoreNote().setMetricTime(newOnset);
        }
    }

    /**
     * Accepts only Notes that have the same metrical onset and duration as
     * already contained notes if there are any.
     */
    @Override 
	public boolean add(Note o) {
        return add(o, false);
    }

    /**
     * This method adds a note to the chord. This function should only be used
     * with great care as it changes the given parameter!
     * @param o 
     * 
     * @param forceDuration
     *            If this is true the MetricDuration of the to be added
     *            ScoreNote will be changed to the duration of the chord
     * @return 
     * 
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Note o, boolean forceDuration) {
        typeCheck(o);
        Note newNote = o;
        if (forceDuration && size() > 0) {
            newNote.getScoreNote().setMetricDuration(getMetricDuration());
        }
        if (trueDuration == null && newNote.getScoreNote().getMetricDuration().equals(Rational.ZERO)) {
            throw new Error("you cannot add a Note with zero duration to a NotationChord");
        }
        if (size() > 0) {
            Note oldNote = get(0);
            if (!oldNote.getScoreNote().getMetricTime().equals(newNote.getScoreNote().getMetricTime()))
                throw new IllegalArgumentException(
                                                   "Argument "
                                                                                                                                                                                                            + newNote
                                                                                                                                                                                                            + " has a different onset time from exitsing notes in this NotationChord.");
            if (!oldNote.getScoreNote().getMetricDuration().equals(newNote.getScoreNote().getMetricDuration()))
                throw new IllegalArgumentException(
                                                   "Argument "
                                                                                                                                                                                                            + newNote
                                                                                                                                                                                                            + " has a different duration from exitsing notes in this NotationChord.");
        }
        boolean result = super.add(o);
        boolean testResult = testUniformity();
        assert testResult;
        return result;
    }

    boolean testUniformity() {
        Note n[] = toArray(new Note[] {});
        Note firstNote = null;
        if (n.length > 0)
            firstNote = n[0];
        for (int i = 1; i < n.length; i++) {
            Note note = n[i];
            if (!firstNote.getScoreNote().getMetricTime().equals(note.getScoreNote().getMetricTime())) {
                System.out.println(getClass() + ".testUniformity() note " + i
                                   + " has a different onset time from exitsing notes in this NotationChord.");
                return false;
            }
            if (!firstNote.getScoreNote().getMetricDuration().equals(note.getScoreNote().getMetricDuration())) {
                System.out.println(getClass() + ".testUniformity() note " + i
                                   + " has a different duration time from existing notes in this NotationChord.");
                return false;
            }
        }
        return true;
    }

    /**
     * prepareForScore
     * 
     * @see de.uos.fmt.musitech.data.score.NotationContainer#prepareForScore()
     */
    @Override
	public void prepareForScore(int pass) {
        // Nothing to do yet.
    }

    /**
     * This method returns true if at least on ScoreNote inside this chord is
     * tied to a successor ScoreNote.
     * 
     * @return true if a member of this chord is a tied note
     */
    public boolean hasTiedNote() {
        for (Note note : this) {
            if (note.getScoreNote().getTiedNote() != null) {
                return true;
            }
        }
        return false;
    }

    void removeTiedNotes() {
        for (Note element : this) {
            element.getScoreNote().setTiedNote(null);
        }
    }

    /**
     * @return the (combined) Accents of all ScoreNotes in this Chord
     */
    public byte[] getAccents() {
        byte[] allAccents = null;
        for (Note element : this) {
            byte[] accents = element.getScoreNote().getAccents();
            if (accents != null) {
                if (allAccents == null) {
                    allAccents = accents;
                } else {
                    for (int i = 0; i < accents.length; i++) {
                        boolean allreadyIn = false;
                        for (int j = 0; j < allAccents.length; j++) {
                            if (accents[i] == allAccents[j]) {
                                allreadyIn = true;
                                break;
                            }
                        }
                        if (!allreadyIn) {
                            byte[] newArray = new byte[allAccents.length + 1];
                            for (int j = 0; j < allAccents.length; j++)
                                newArray[j] = allAccents[j];
                            newArray[newArray.length - 1] = accents[i];
                            allAccents = newArray;
                        }
                    }
                }
            }
        }

        return allAccents;
    }

    /**
     * @return whether at least one of the ScoreNotes in this Chord has accents
     */
    public boolean hasAccents() {
        return getAccents() != null && getAccents().length > 0;
    }

    /**
     * This method splits a NotationChord to obtain regular Chords. A Chord is
     * considered regular if the numerator of it's duration is one (regular
     * regular chord) or three (dotted chord). If this is the case with the
     * param an array with the untouched chord is returned. Otherwise notes of a
     * lower order (lower order = numerator to the power of two) are "cut of"
     * from the chord until the numerator is one or three. All parts are then
     * returned.
     * 
     * @return the split NotationChord
     */
    public NotationChord[] normalizeChord() {
        if (getSingleMetricDuration().equals(Rational.ZERO)) {
            throw new Error("a chord may not have a duration of zero.");
        }
        if (!MyMath.isPowerOf2(getMetricDuration().getDenom())) {
            // TODO find a proper solution here
            Error e = new Error(getMetricDuration() + " is not a negative power of 2");
            e.printStackTrace();
            // throw new Error(getDuration() + " is not a negative power of 2");
            setMetricDuration(new Rational(1, 4));
        }

        //construct a duration comparator (descending)
        Comparator<Metrical> durationComparer = new Comparator<Metrical>() {

            @Override
			public int compare(Metrical n1, Metrical n2) {
                return n2.getMetricDuration().compare(n1.getMetricDuration());
            }
        };

        SortedContainer<NotationChord> newChords = new SortedContainer<NotationChord>(getContext(), NotationChord.class, durationComparer);

        //NotationChord chord = (NotationChord) ObjectCopy.copyObject(this);
        NotationChord chord = (NotationChord) this.clone();
        while (chord.getSingleMetricDuration().getNumer() != 1 && chord.getSingleMetricDuration().getNumer() != 3) {

            Rational newDuration = new Rational(3, 2); //longest regular chord
            int nextDenom = 2;

            if (!chord.getSingleMetricDuration().isGreater(Rational.ZERO)) {
                // TODO Check if this is correct:
                throw new Error("this should not happen!!!!!!");
                //chord.setMetricDuration(new Rational(1, 4));
                //otherwise we get an infinite loop
            }

            // go through the durations: 3/2, 2/2, 3/4, 2/4, 3/8, 2/8...
            while (newDuration.isGreater(chord.getSingleMetricDuration())) {
                newDuration.setNumer(newDuration.getNumer() == 3 ? 2 : 3);
                newDuration.setDenom(nextDenom);
                nextDenom *= 2;
            }

            newDuration.reduce();

            if (newDuration.isEqual(chord.getSingleMetricDuration())) //finished
                break;

            NotationChord newChord = (NotationChord) chord.clone();
            newChord.setMetricDuration(newDuration);

            //shorten the original duration by the new duration:
            chord.setMetricDuration(chord.getSingleMetricDuration().sub(newChord.getSingleMetricDuration()));

            newChords.add(newChord);
        }

        //insert the leftover one:
        newChords.add(chord);

        //now set the attack times (this was not possible before, because we
        // don't now
        //how many notes we will get):
        Rational attack = chord.getMetricTime();
        for (int i = 0; i < newChords.size(); i++) {
            NotationChord newChord = newChords.get(i);
            newChord.setMetricTime(attack);
            attack = attack.add(newChord.getSingleMetricDuration());
        }

        //set ties for split chords:
        for (int i = 0; i < (newChords.size() - 1); i++) {
            NotationChord chordLeft = newChords.get(i);
            NotationChord chordRight = newChords.get(i + 1);
            for (int j = 0; j < chordLeft.size(); j++) {
                chordLeft.get(j).getScoreNote().setTiedNote(chordRight.get(j).getScoreNote());
            }
        }

        return newChords.toArray(new NotationChord[newChords.size()]);
    }

    public void prettyPrint() {
        int i = 0;
        for (Iterator<Note> iter = this.iterator(); iter.hasNext();) {
            Note element = iter.next();
            System.out.print(element.getMetricTime() + ":" + element.getScoreNote().getDiatonic());
            if (!(i == size() - 1))
                System.out.print("/");
            i++;
        }
    }

    /**
     * @return Returns the entryChord.
     */
    public NotationChord[] getEntryChord() {
        return entryChord;
    }

    /**
     * @param entryChord
     *            The entryChord to set.
     */
    public void setEntryChord(NotationChord[] argEntryChord) {
        this.entryChord = argEntryChord;
    }

    public void addAppoggiatura(NotationChord appoggiature, int ordinal) {
        if (entryChord == null)
            entryChord = new NotationChord[ordinal];
        if (entryChord.length < ordinal) {
            NotationChord[] newArray = new NotationChord[ordinal];
            for (int i = 0; i < entryChord.length; i++) {
                newArray[i] = entryChord[i];
            }
            entryChord = newArray;
        }
        entryChord[ordinal - 1] = appoggiature;
    }

    public Rational getTrueDuration() {
        return trueDuration;
    }

    public void setTrueDuration(Rational trueDuration) {
        //TODO comment
        this.trueDuration = trueDuration;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    @Override
	public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        //commons----------------------------------
        // create element representing this object
        NotationChord chord = (NotationChord) object;
        Element noteObject;
        if (chord.isRest()) {
            noteObject = XMLHelper.addElement(parent, "rest");
        } else {
            noteObject = XMLHelper.addElement(parent, "chord");
        }
        // check for reference
        if (instance.knowsObject(noteObject, object))
            return true;
        // metadata
        MPEG_SMR_Tools.serializeMetaData(instance, noteObject, this);
        // rendering hints
        instance.writeXML(noteObject, getRenderingHints(), null, null);
        //-----------------------------------------
        ;

        /*
         * serialize information that are the same for noteObjects/chord and
         * noteObjects/rest
         */

        Element duration;
        /*
         * serialize duration; special treatment for EntryChord/Vorhalt
         */
        if (isEntryChord()) {
            duration = MPEG_SMR_Tools.addDuration(noteObject, this, (NotationVoice) instance.getParent(this));
            //duration = XMLHelper.addElement(noteObject, "duration");
            duration.setAttribute("noDuration", "true");
            XMLHelper.getOrCreateElement(noteObject, "display").setAttribute("small", "true");
        } else {
            duration = MPEG_SMR_Tools.addDuration(noteObject, this, (NotationVoice) instance.getParent(this));
        }

        /*
         * attachables
         */
        NotationVoice parent_voice = (NotationVoice) instance.getParent(this);
        NotationStaff parent_staff = (NotationStaff) instance.getParent(parent_voice);
        ArrayList<IAttachable> allAttachables = new ArrayList<IAttachable>();
        // add attachables for this chord
        allAttachables.addAll(parent_staff.getAttachables(this));
        for (Iterator<?> iter = getContent().iterator(); iter.hasNext();) {
            // add attachables for content Note's
            List<IAttachable> attachables = parent_staff.getAttachables(iter.next());
            // TODO can many notes of the chord contain the same attachable if
            // so only remember one
            for (int i = 0; i < attachables.size(); i++) {
                allAttachables.add(attachables.get(i));
            }
        }
        // check for unhandled attachabels TODO This code is only for
        // development to find out which attachables are not serialized
        for (int i = 0; i < allAttachables.size(); i++) {
            Object att = allAttachables.get(i);
            Object symbol = null;
            if (att instanceof MetricAttachable)
                symbol = ((MetricAttachable) att).getSymbol();
            else if (att instanceof Attachable)
                symbol = ((Attachable) att).getSymbol();
            if (symbol instanceof CharSymbol) {
                char symbolChar = ((CharSymbol) symbol).getSymbol();
                if (symbolChar == (char) 117) {
                    // see you later as fermata
                } else if (symbolChar == (char) 108) {
                    // see you later as upperMordent
                } else {
                    System.out.println("[WARNING] unhandeld attachable charSymbol " + symbolChar);
                }
            } else if (symbol instanceof CustomSVGGraphic) {
                // TODO in 2.1
                System.out.println("[WARNING] unhandeld attachable (" + symbol.toString() + ")");
            } else if (symbol instanceof ChordSymbol) {
                // see you later via contextLine
            } else {
                // yet unhandled exeptions
                System.out.println("[WARNING] unhandeld attachable (" + symbol.toString() + ")");
                //assert false;
            }
        }

        /*
         * from here on serialize further information only for noteObjects/chord
         */
        if (chord.isRest())
            return true;

        // serialize beam

        if (this.getBeamType() == BEAM_FORCE) {
            Element beam = XMLHelper.addElement(noteObject, "beam");
            beam.setAttribute("group", "force");
        }

        /*
         * serialize lyrics
         */
        LyricsContainer lyrics = ((NotationVoice) instance.getParent(this)).getLyrics();
        if (!isEntryChord() && lyrics != null) {
            Element lyric = XMLHelper.addElement(noteObject, "lyric");

            // default language verses
            Locale defaultLang = lyrics.getDefaultLanguage();
            if (defaultLang != null)
                lyric.setAttribute("lang", defaultLang.toString());
            LyricsSyllableSequence[] versesDefault = lyrics.getVerses();
            for (int i = 0; i < versesDefault.length; i++) {
                LyricsSyllableSequence verseDefault = versesDefault[i];
                LyricsSyllable syl = verseDefault.getSyllableAt(this.getMetricTime());
                if (syl != null) {
                    Element sylElem = XMLHelper.addElement(lyric, "verse");
                    if (verseDefault.hasVerseNumber())
                        sylElem.setAttribute("i", "" + verseDefault.getVerseNumber());
                    if (verseDefault.getVerseNumberingText() != null)
                        sylElem.setAttribute("verseNum", "" + verseDefault.getVerseNumberingText());
                    XMLHelper.addText(sylElem, syl.getText());
                }
            }

            // translations
            Locale[] usedLangs = lyrics.getLanguages();
            // loop over languages
            for (int i = 0; i < usedLangs.length; i++) {
                Locale locale = usedLangs[i];
                if (locale != null && !locale.equals(defaultLang)) {
                    LyricsSyllableSequence[] versesTranslation = lyrics.getVerses(locale);
                    Element trans = XMLHelper.addElement(lyric, "translation");
                    trans.setAttribute("lang", locale.toString());
                    //  loop over verses in current language
                    for (int iV = 0; iV < versesTranslation.length; iV++) {
                        LyricsSyllableSequence verseTranslation = versesTranslation[iV];
                        LyricsSyllable syl = verseTranslation.getSyllableAt(this.getMetricTime());
                        if (syl != null) {
                            Element sylElem = XMLHelper.addElement(trans, "verse");
                            sylElem.setAttribute("i", "" + verseTranslation.getVerseNumber());
                            if (verseTranslation.getVerseNumberingText() != null)
                                sylElem.setAttribute("verseNum", "" + verseTranslation.getVerseNumberingText());
                            XMLHelper.addText(sylElem, syl.getText());
                        }
                    }
                }
            }
        }

        /*
         * serialize articulation
         */
        if (hasAccents()) {
            byte[] accents = getAccents();
            for (int i = 0; i < accents.length; i++) {
                byte accent = accents[i];
                if (accent == Accent.STACCATO)
                    XMLHelper.addElement(XMLHelper.addElement(noteObject, "articulation"), "staccato");
                if (accent == Accent.MARCATO)
                    XMLHelper.addElement(XMLHelper.addElement(noteObject, "articulation"), "marcato");
                if (accent == Accent.PORTATO) {
                    XMLHelper.addElement(XMLHelper.addElement(noteObject, "articulation"), "staccato");
                    XMLHelper.addElement(XMLHelper.addElement(noteObject, "articulation"), "tenuto");
                }
                // TODO serialize articulation completely
            }
        }
        // attachable articulations
        for (int i = 0; i < allAttachables.size(); i++) {
            Object symbol = null;
            if (allAttachables.get(i) instanceof MetricAttachable)
                symbol = ((MetricAttachable) allAttachables.get(i)).getSymbol();
            else if (allAttachables.get(i) instanceof Attachable)
                symbol = ((Attachable) allAttachables.get(i)).getSymbol();
            if (symbol instanceof CharSymbol) {
                char symbolChar = ((CharSymbol) symbol).getSymbol();
                if (symbolChar == (char) 117) {
                    XMLHelper.addElement(XMLHelper.addElement(noteObject, "articulation"), "fermata");
                }
            }
        }

        /*
         * serialize drawObjects
         */

        // slurs
        if (numSlurNotes > 0) {
            XMLHelper.addElement(
                                 XMLHelper.addElement(XMLHelper.getOrCreateElement(noteObject, "drawObjects"),
                                                      "drawObj"), "slur").setAttribute("noteNum", "" + numSlurNotes);
        }

        /*
         * serialize heads /notes
         */
        Element heads = XMLHelper.addElement(noteObject, "heads");
        List<?> content = getContent();
        for (Iterator<?> iter = content.iterator(); iter.hasNext();) {
            Note mtNoteObject = (Note) iter.next();
            instance.setParent(mtNoteObject, this);
            instance.writeXML(heads, mtNoteObject, null, null);
        }

        /*
         * serialize ornament
         */
        if (hasAccents()) {
            byte[] accents = getAccents();
            for (int i = 0; i < accents.length; i++) {
                byte accent = accents[i];
                if (accent == Accent.TRILL)
                    XMLHelper.addElement(XMLHelper.addElement(noteObject, "ornament"), "trill");
                // TODO serialize ornament completely
            }
        }
        // attachable ornaments
        for (int i = 0; i < allAttachables.size(); i++) {
            Object symbol = null;
            if (allAttachables.get(i) instanceof MetricAttachable)
                symbol = ((MetricAttachable) allAttachables.get(i)).getSymbol();
            else if (allAttachables.get(i) instanceof Attachable)
                symbol = ((Attachable) allAttachables.get(i)).getSymbol();
            if (symbol instanceof CharSymbol) {
                char symbolChar = ((CharSymbol) symbol).getSymbol();
                if (symbolChar == (char) 108) {
                    XMLHelper.addElement(XMLHelper.addElement(noteObject, "ornament"), "upperMordent");
                }
            }
        }

        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(org.w3c.dom.Element,
     *      java.util.Hashtable, java.lang.Object)
     */
    @Override
	public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        //commons----------------------------------
        // reference-handling
        Object reference = instance.getReferenced(node, this);
        if (reference != null)
            return reference;
        //metadata
        MPEG_SMR_Tools.deserializeMetaData(instance, node, this);
        // rendering hints
        this.setRenderingHints(MPEG_SMR_Tools.deserializeRenderingHints(instance, node));
        //-----------------------------------------

        // duration
        Element duration = (Element) node.getElementsByTagName("duration").item(0);
        if (duration.getAttribute("noDuration").equalsIgnoreCase("true")) {
            setIsEntryChord(true);
        }

        Rational mtDuration = MPEG_SMR_Tools.getDuration(duration);
        // the duration is set later in this method when chord-heads or rest is
        // deserialized

        if (node.getNodeName().equals("chord")) {
            /*
             * heads
             */
            Element heads = (Element) node.getElementsByTagName("heads").item(0);
            NodeList headList = heads.getElementsByTagName("head");
            for (int i = 0; i < headList.getLength(); i++) {
                Element head = (Element) headList.item(i);
                instance.setParent(head, this);
                Note note = (Note) instance.readXML(head, Note.class);
                note.getScoreNote().setMetricDuration(mtDuration);
                NotationVoice parent = (NotationVoice) instance.getParent(node);
                note.getScoreNote().setMetricTime(instance.getNextOnset(parent));
                this.add(note);
            }

            this.setMetricDuration(mtDuration);

            /*
             * lyrics
             */
            Element lyric = XMLHelper.getElement(node, "lyric");
            if (lyric != null) {
                // default language
                Locale defaultLocale = I18N.parseLocale(lyric.getAttribute("lang"));
                LyricsContainer lyricsC = ((NotationVoice) instance.getParent(node)).getLyrics();
                if (lyricsC == null) {
                    lyricsC = new LyricsContainer(defaultLocale);
                    ((NotationVoice) instance.getParent(node)).setLyrics(lyricsC);
                }
                Element[] defVerses = XMLHelper.getChildElements(lyric, "verse");
                for (int i = 0; i < defVerses.length; i++) {
                    Element defVerse = defVerses[i];
                    byte verseNum = 0;
                    if (defVerse.hasAttribute("i"))
                        verseNum = Byte.parseByte(defVerse.getAttribute("i"));
                    LyricsSyllableSequence mtVerse = lyricsC.getVerse(verseNum, defaultLocale);
                    if (mtVerse == null) {
                        mtVerse = new LyricsSyllableSequence(defaultLocale, verseNum);
                        lyricsC.add(mtVerse);
                    }
                    if (defVerse.hasAttribute("verseNum"))
                        mtVerse.setVerseNumberingText(defVerse.getAttribute("verseNum"));
                    mtVerse.add(new LyricsSyllable(this.getMetricTime(), XMLHelper.getText(defVerse).trim()));
                }
                // translations
                Element[] translations = XMLHelper.getChildElements(lyric, "translation");
                for (int i = 0; i < translations.length; i++) {
                    Locale transLocale = I18N.parseLocale(translations[i].getAttribute("lang"));
                    Element[] transVerses = XMLHelper.getChildElements(translations[i], "verse");
                    for (int j = 0; j < transVerses.length; j++) {
                        Element transVerse = transVerses[j];
                        byte verseNum = 0;
                        if (transVerse.hasAttribute("i"))
                            verseNum = Byte.parseByte(transVerse.getAttribute("i"));
                        LyricsSyllableSequence mtVerse = lyricsC.getVerse(verseNum, transLocale);
                        if (mtVerse == null) {
                            mtVerse = new LyricsSyllableSequence(transLocale, verseNum);
                            lyricsC.add(mtVerse);
                        }
                        if (transVerse.hasAttribute("verseNum"))
                            mtVerse.setVerseNumberingText("verseNum");
                        mtVerse.add(new LyricsSyllable(this.getMetricTime(), XMLHelper.getText(transVerse).trim()));
                    }
                }
            }

            /*
             * process accents/articulation/ornament
             */
            List<?> accents = new ArrayList<Object>();

            // articulation
            NodeList articulationList = node.getElementsByTagName("articulation");
            for (int i = 0; i < articulationList.getLength(); i++) {
                Element articulation = (Element) articulationList.item(i);
                if (articulation.getElementsByTagName("staccato").item(0) != null) {
                    this.setAccent(Accent.STACCATO);
                } else if (articulation.getElementsByTagName("strongAccent").item(0) != null) {
                } else if (articulation.getElementsByTagName("accent").item(0) != null) {
                } else if (articulation.getElementsByTagName("sforzando").item(0) != null) {
                    this.setAccent(Accent.SFORZATO);
                } else if (articulation.getElementsByTagName("tenuto").item(0) != null) {
                } else if (articulation.getElementsByTagName("upbow").item(0) != null) {
                } else if (articulation.getElementsByTagName("downbow").item(0) != null) {
                } else if (articulation.getElementsByTagName("weakBeat").item(0) != null) {
                } else if (articulation.getElementsByTagName("strongBeat").item(0) != null) {
                } else if (articulation.getElementsByTagName("fermata").item(0) != null) {
                    CharSymbol fermat = new CharSymbol((char) 117);
                    Node voiceNode = node.getParentNode().getParentNode();
                    ((NotationStaff) instance.getParent(voiceNode)).addAttachable(new MetricAttachable(this.get(0),
                                                                                                       fermat));
                }

            }

            // ornament

            NodeList ornamentList = node.getElementsByTagName("ornament");
            for (int i = 0; i < ornamentList.getLength(); i++) {
                Element ornament = (Element) ornamentList.item(i);

                if (ornament.getElementsByTagName("upperMordent").item(0) != null) {
                    CharSymbol mordentup = new CharSymbol((char) 108);
                    Node voiceNode = node.getParentNode().getParentNode();
                    ((NotationStaff) instance.getParent(voiceNode)).addAttachable(new MetricAttachable(this.get(0),
                                                                                                       mordentup));
                } else if (ornament.getElementsByTagName("upperMordent").item(0) != null) {
                } else if (ornament.getElementsByTagName("trill").item(0) != null) {
                    this.setAccent(Accent.TRILL);
                } else if (ornament.getElementsByTagName("upperMordent").item(0) != null) {
                    this.setAccent(Accent.MORDENT_UP);
                } else if (ornament.getElementsByTagName("lowerMordent").item(0) != null) {
                    this.setAccent(Accent.MORDENT_DOWN);
                } else if (ornament.getElementsByTagName("grace").item(0) != null) {
                } else if (ornament.getElementsByTagName("roll").item(0) != null) {
                } else if (ornament.getElementsByTagName("turn").item(0) != null) {
                }

            }
            if (accents.size() > 0) {
                for (Iterator<?> iter = accents.iterator(); iter.hasNext();) {
                    Byte accent = (Byte) iter.next();
                    // add the accent to the first note of the chord
                    this.getContent().get(0).getScoreNote().addAccent(accent.byteValue());
                }
            }

            // beam
            Element beam = (Element) node.getElementsByTagName("beam").item(0);
            if (beam != null) {
                String beamType = beam.getAttribute("group");
                if (beamType.equals("auto"))
                    this.setBeamType(BEAM_AUTO);
                if (beamType.equals("force"))
                    this.setBeamType(BEAM_FORCE);
                if (beamType.equals("split"))
                    this.setBeamType(BEAM_SPLIT);
                if (beamType.equals("divide"))
                    this.setBeamType(BEAM_DIVIDE);
            }

            /*
             * stem
             */
            Element stem = XMLHelper.getElement(node, "stem");
            if (stem != null) {
                if (stem.hasAttribute("invisible"))
                    if (stem.getAttribute("invisible").equalsIgnoreCase("true"))
                        this.addRenderingHint("draw stem", Boolean.FALSE);
                if (stem.hasAttribute("tremoloBars")) {
                    this.addRenderingHint("tremolo", stem.getAttribute("tremoloBars"));
                }
                if (stem.hasAttribute("dir"))
                    this.addRenderingHint("stem direction", stem.getAttribute("dir"));
            }
        } else if (node.getNodeName().equals("rest")) {

            Note rest = new Note();
            rest.setScoreNote(new ScoreNote(instance.getNextOnset(instance.getParent(node)), mtDuration, 'r', (byte) 0,
                                            (byte) 0));

            // qualifier

            if (XMLHelper.getElement(XMLHelper.getElement(node, "qualifier"), "fermata") != null) {
                CharSymbol fermat = new CharSymbol((char) 117);
                Node voiceNode = node.getParentNode().getParentNode();
                ((NotationStaff) instance.getParent(voiceNode))
                        .addAttachable(new MetricAttachable(this.get(0), fermat));
            }

            this.add(rest);
        } else {
            assert false;
        }

        return this;
    }

    /**
     * sets an accent
     * 
     * @param type
     *            (as described in class Accent)
     * @see de.uos.fmt.musitech.score.gui.Accent
     */
    private void setAccent(byte type) {
        if (this.size() > 0) {
            getContent().get(0).getScoreNote().addAccent(type);
        }
    }

    /**
     * checks whether the chord is a rest
     * 
     * @author Jens
     */
    public boolean isRest() {
        return get(0).getScoreNote().getDiatonic() == 'r';
    }

    /**
     * automaticly assign beam
     */
    public static final byte BEAM_AUTO = 0;
    /**
     * force a beam to the next chord
     */
    public static final byte BEAM_FORCE = 1;
    /**
     * TODO comment
     */
    public static final byte BEAM_SPLIT = 2;
    /**
     * TODO comment
     */
    public static final byte BEAM_DIVIDE = 3;

    // type of beam
    private byte beamType;

    /**
     * gets the beam type
     * 
     * @return type of beam as described by constants NotationChord.BEAM_*
     */
    public byte getBeamType() {
        return beamType;
    }

    /**
     * set the beam type
     * 
     * @param beamType
     *            type of beam as described by constants NotationChord.BEAM_*
     */
    public void setBeamType(byte beamType) {
        this.beamType = beamType;
    }

    /**
     * convenience method to return the uppermost note (with the highest pitch)
     */
    public Note getUppermostNote() {
    	if(size()==1)
    		return get(0);
        int highestPitch = -1;
        Note highest = null;
        for (int i = 0; i < size(); i++) {
            int pitch = get(i).getMidiPitch();
            if (pitch > highestPitch) {
                highestPitch = pitch;
                highest = get(i);
            }
        }
        return highest;
    }
    
    /**
     * convenience method to return the lowest note (with the lowest pitch)
     */
    public Note getLowestNote() {
    	if(size()==1)
    		return get(0);
        int lowestPitch = Integer.MAX_VALUE;
        Note lowest = null;
        for (int i = 0; i < size(); i++) {
            int pitch = get(i).getMidiPitch();
            if (pitch < lowestPitch) {
                lowestPitch = pitch;
                lowest = get(i);
            }
        }
        return lowest;
    }

    private boolean isEntryChord = false;

    private int numSlurNotes = 0;

    /**
     * TODO synchronize with EntryChord handling in chord
     * 
     * @return EntryChord or not
     */
    public boolean isEntryChord() {
        return this.isEntryChord;
    }

    void setIsEntryChord(boolean isEntryChord) {
        this.isEntryChord = isEntryChord;
    }

    /**
     * @param numSlurs
     */
    public void setNumSlurNotes(int numSlurs) {
        this.numSlurNotes = numSlurs;
    }
}