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
package de.uos.fmt.musitech.data.score;

import java.util.Collection;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.MetricalComparator;

/**
 * This class is used to contain ScoreNotes which belong to one slur.
 * It is used inside @see NotationVoice
 * @author collin
 * @version 1.0
 * @see ScoreNote
 * 
 * @hibernate.class table = "SlurContainer"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */
public class SlurContainer extends SortedContainer<Note> {

	private static final long serialVersionUID = -2600162240501364002L;
	
	private RenderingHints renderingHints;
	
	public boolean hasHint(String hint) {
		if (renderingHints == null)
			return false;
		return renderingHints.containsKey(hint); 
	}
	
	public Object getHint(String hint) {
		if (renderingHints == null)
			return null;
		return renderingHints.getValue(hint);
	}
	
    /**
     * This constructor calls the super constructor. It makes sure that only ScoreNotes are
     * allowed inside of this container. The Comparator used is the  MetricComparator
     * @param context the work context for this container
     * @see MetricalComparator
     */
    public SlurContainer(Context context) {
		super(context, Note.class, new MetricalComparator());
	}

    /**
     * Convenience Constructor which adds all Notes from the given Container to this Slur
     * @param context
     * @param notes
     */
    public SlurContainer(Context context, Collection<Note> notes) {
    	this(context);
    	this.addAll(notes);
    }
    
    
    /**
     * This constructor calls the super constructor. It makes sure that only ScoreNotes are
     * allowed inside of this container. The Comparator used is the  MetricComparator
     * @param context the work context for this container
     * @see MetricalComparator
     */
    public SlurContainer() {
		super(new Context(), Note.class, new MetricalComparator());
	}
    
	public RenderingHints getRenderingHints() {
		return renderingHints;
	}
	public void setRenderingHints(RenderingHints renderingHints) {
		this.renderingHints = renderingHints;
	}
}
