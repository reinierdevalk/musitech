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
/*
 * Created on 20-Dec-2004
 *
 */
package de.uos.fmt.musitech.data.score;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * 
 * Information for notes to be displayed in tablatures for string instruments. A
 * TablaturNote can have either a fret and (instrumental) string or a pull-up.
 * 
 * @author collin
 *  
 */
public class TablatureNote implements IMPEGSerializable {

    private int instrString = -1;
    private int fret = -1;
    private Rational pullUp;
    private Note pullUpTarget;
    private boolean paranthised;
    private Note pullDownTarget;
    private boolean longPullDown = false;
    private int pullDownTargetInt = -1;
    
    private float pullDownShift;

    /**
     * Empty note.
     */
    public TablatureNote() {
        instrString = 0;
        fret = 0;
        pullUp = Rational.ZERO;
    }

    /**
     * Get the fret number. 0 means open string, 1 ... means the fret, -1 is
     * undefined.
     * 
     * @return The fret number.
     */
    public int getFret() {
        return fret;
    }

    /**
     * Set the fret number. 0 means open string, 1 ... means the fret, -1 is
     * undefined.
     * 
     * @param fretNum
     *            The fret on which to play this note. -1 means undefined.
     */
    public void setFret(int fretNum) {
        this.fret = fretNum;
    }

    /**
     * The string on which to play this note. String numbers start from 1, -1
     * means undefined.
     * 
     * @return The string number.
     */
    public int getInstrString() {
        return instrString;
    }

    /**
     * The string on which to play this note. String numbers start from 1, -1
     * means undefined.
     * 
     * @param stringNum
     *            The string number.
     */
    public void setInstrString(int stringNum) {
        this.instrString = stringNum;
    }

    /**
     * If not null, indicates to raise the note by pulling the string; the value
     * indicating the number of whole tones.
     * 
     * @return The number of whole notes or null (no pull-up).
     */
    public Rational getPullUp() {
        return pullUp;
    }

    /**
     * If not null, indicates to raise the note by pulling the string; the value
     * indicating the number of whole tones.
     * 
     * @param newPullUp
     *            The number of whole notes or null (no pull-up).
     */
    public void setPullUp(Rational newPullUp) {
        this.pullUp = newPullUp;
    }

    public Note getPullUpTarget() {
        return pullUpTarget;
    }

    public void setPullUpTarget(Note pullUpTarget) {
        this.pullUpTarget = pullUpTarget;
    }

    public boolean isParanthised() {
        return paranthised;
    }

    /**
     * set if the TablatureNote should be put in parantheses
     * 
     * @param paranthised
     */
    public void setParanthised(boolean paranthised) {
        this.paranthised = paranthised;
    }

    /**
     * set the pullDown target for this tablature note
     * 
     * @return
     */
    public Note getPullDownTarget() {
        return pullDownTarget;
    }

    public void setPullDownTarget(Note pullDownTarget) {
        this.pullDownTarget = pullDownTarget;
    }

    public boolean isLongPullDown() {
        return longPullDown;
    }

    /**
     * if the pull down is to be split in to graphical curves connected by a
     * dotted line
     * 
     * @param longPullDown
     */
    public void setLongPullDown(boolean longPullDown) {
        this.longPullDown = longPullDown;
    }

    public int getPullDownTargetInt() {
        return pullDownTargetInt;
    }

    public void setPullDownTargetInt(int pullDownTargetInt) {
        this.pullDownTargetInt = pullDownTargetInt;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    public boolean toMPEG(MusiteXMLSerializer instance, Node head, Object object, String fieldname) {
        Element tab = XMLHelper.addElement(head, "tablature");
        tab.setAttribute("instrString", "" + getInstrString());
        tab.setAttribute("fret", "" + getFret());
        tab.setAttribute("paranthised", "" + isParanthised());

        if (getPullUp() != null) {
            Element pullUp = XMLHelper.addElement(tab, "pullUp");
            pullUp.setAttribute("numerator", "" + getPullUp().getNumer());
            pullUp.setAttribute("denominator", "" + getPullUp().getDenom());
            MPEG_SMR_Tools.setPitch(pullUp, getPullUpTarget().getScoreNote());
            //TODO pullUp.setAttribute("noteHead",""+getPullUp().getDenom());
            pullUp.setAttribute("pullDownTarget", "" + getPullDownTargetInt());
            pullUp.setAttribute("longPullDown", "" + getPullUp().getDenom());
        }
        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    public Object fromMPEG(MusiteXMLSerializer instance, Element tablature) {
        if (tablature.getAttribute("instrString") != null)
            this.setInstrString(Integer.parseInt(tablature.getAttribute("instrString")));
        if (tablature.getAttribute("fret") != null)
            this.setFret(Integer.parseInt(tablature.getAttribute("fret")));
        if (tablature.getAttribute("paranthised") != null)
            this.setParanthised(tablature.getAttribute("paranthised").equalsIgnoreCase("true"));

        Element pullUp = XMLHelper.getElement(tablature, "pullUp");
        if (pullUp != null) {
            this.setPullUp(new Rational(Integer.parseInt(pullUp.getAttribute("numerator")), Integer.parseInt(pullUp
                    .getAttribute("denominator"))));
            Note pullUpTarget = new Note();
            pullUpTarget.setScoreNote(MPEG_SMR_Tools.getScoreNote(pullUp));
            assert pullUpTarget!=null;
            this.setPullUpTarget(pullUpTarget);
            //TODO pullUp.getAttribute("noteHead");
            if (pullUp.hasAttribute("pullDownTarget"))
                this.setPullDownTargetInt(Integer.parseInt(pullUp.getAttribute("pullDownTarget")));
            if (pullUp.hasAttribute("longPullDown"))
                this.setLongPullDown(pullUp.getAttribute("longPullDown").equalsIgnoreCase("true"));
        }
        return this;
    }
	public float getPullDownShift() {
		return pullDownShift;
	}
	public void setPullDownShift(float pullDownShift) {
		this.pullDownShift = pullDownShift;
	}
}