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
 * Created on 19.01.2005
 *
 */
package de.uos.fmt.musitech.structure.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.structure.Note;

/**
 * @author Kerstin Neubarth
 *  
 */
public class Score2Score {

    private static NotationFilter filter;

    /**
     * Returns a replica of the specified NotationSystem filtered by the
     * <code>filter</code>.
     * 
     * @param srcScore NotationSystem to be copied with filtering
     * @param aFilter NotationFilter to use
     * @return NotationSystem copied from the specified NotationSystem with
     *         filtering
     */
    public static NotationSystem copyScore(NotationSystem srcScore, NotationFilter aFilter) {
        filter = aFilter;
        NotationSystem destScore = new NotationSystem(srcScore.getContext());
        List content = srcScore.getContent();
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i) instanceof NotationStaff) {
                convertNotationStaff((NotationStaff) content.get(i), destScore);
            }
        }
        removeEmptyStaffs(destScore);	//TODO if true, check staffConnectors
        if (destScore.isEmpty())
            return null;
        if (srcScore.getStaffConnectors() != null) {
            destScore.setStaffConnectors(srcScore.getStaffConnectors());
        }
        if (srcScore.getBarlines() != null) {
            destScore.setBarlines(srcScore.getBarlines());
        }
        if (srcScore.getLinebreaks() != null) {
            destScore.setLinebreaks(srcScore.getLinebreaks());
        }
        if (srcScore.getPagebreaks() != null) {
            destScore.setPagebreaks(srcScore.getPagebreaks());
        }
        if (srcScore.getRenderingHints() != null) {
            destScore.setRenderingHints(srcScore.getRenderingHints());
        }
        destScore.insertBarlines();
        return destScore;
    }

    private static NotationStaff convertNotationStaff(NotationStaff srcStaff, NotationSystem destSystem) {
        NotationStaff destStaff = new NotationStaff(destSystem);
        List content = srcStaff.getContent();
        boolean filled = false;
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i) instanceof NotationVoice) {
                convertNotationVoice((NotationVoice) content.get(i), destStaff);
            }
        }
        if (srcStaff.getClefTrack() != null) {
            destStaff.setClefTrack(srcStaff.getClefTrack());
        }
        if (srcStaff.getAttachables() != null) {
            destStaff.setAttachables(srcStaff.getAttachables());
        }
        if (srcStaff.getRenderingHints() != null) {
            destStaff.setRenderingHints(srcStaff.getRenderingHints());
        }
        return destStaff;
    }

    private static NotationVoice convertNotationVoice(NotationVoice srcVoice, NotationStaff destStaff) {
        NotationVoice destVoice = new NotationVoice(destStaff);
        List content = srcVoice.getContent();
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i) instanceof NotationChord) {
            	NotationChord chord = (NotationChord) content.get(i);
                NotationChord newChord = new NotationChord(chord.getContext());
                List<Note> chordContent = chord.getContent();
                for (int j = 0; j < chordContent.size(); j++) {
                    if (filter != null && filter.includeElement(chordContent.get(j))) {
                        newChord.add(chordContent.get(j));
                    }
                }
                if (!newChord.isEmpty()) {
                    destVoice.add(newChord);
                }
            } //else if (content.get(i) instanceof Note){ //TODO tritt das auf?
            //if (filter!=null && filter.includeElement(content.get(i))){
            //  destVoice.add(content.get(i));
            //}
            //}
        }
//        destVoice.fillGaps();
        if (srcVoice.getBeamContainers() != null) {
//            destVoice.setBeamContainers(srcVoice.getBeamContainers());
            List beamContainers = new ArrayList();
            for (Iterator iter = srcVoice.getBeamContainers().iterator(); iter.hasNext();) {
                BeamContainer element = (BeamContainer) iter.next();
                boolean contained = true;
                for (int i = 0; i < element.size(); i++) {
                    if (!srcVoice.contains(element.get(i))){
                        contained = false;
                    }
                }
                if (contained)
                    beamContainers.add(element);
            }
            destVoice.setBeamContainers(beamContainers);
        }
        if (srcVoice.getSlurContainers() != null) {
//            destVoice.setSlurContainers(srcVoice.getSlurContainers());
            List slurContainers = new ArrayList();
            for (Iterator iter = srcVoice.getSlurContainers().iterator(); iter.hasNext();) {
                SlurContainer element = (SlurContainer) iter.next();
                boolean contained = true;
                for (int i = 0; i < element.size(); i++) {
                    if (!srcVoice.contains(element.get(i))){
                        contained = false;
                    }
                }
                if (contained)
                    slurContainers.add(element);
            }
            destVoice.setSlurContainers(slurContainers);
        }
        if (srcVoice.getTupletContainers() != null) {
            destVoice.setTupletContainers(srcVoice.getTupletContainers());
        }
        if (srcVoice.getLyrics() != null) {
            destVoice.setLyrics(srcVoice.getLyrics());
        }
        if (srcVoice.getRenderingHints() != null) {
            destVoice.setRenderingHints(srcVoice.getRenderingHints());
        }
        return destVoice;
    }

    private static boolean removeEmptyStaffs(NotationSystem notationSystem) {
        boolean removed = false;
        if (notationSystem != null) {
            List content = notationSystem.getContent();
            Collection toRemove = new ArrayList();
            for (int i = 0; i < content.size(); i++) {
                if (content.get(i) instanceof NotationStaff) {
                    if (removeEmptyVoices((NotationStaff)content.get(i))){
                        if (((NotationStaff)content.get(i)).isEmpty())
//                            notationSystem.remove(content.get(i));
                            toRemove.add(content.get(i));
                        removed = true;
                    }
                }
            }
            content.removeAll(toRemove);
        }
        return removed;
    }

    private static boolean removeEmptyVoices(NotationStaff notationStaff) {
        boolean removed = false;
        if (notationStaff != null) {
            List content = notationStaff.getContent();
            Collection toRemove = new ArrayList();
            for (int i = 0; i < content.size(); i++) {
                if (content.get(i) instanceof NotationVoice){
                    if (((NotationVoice)content.get(i)).isEmpty()){
//                        notationStaff.remove(content.get(i));
                        toRemove.add(content.get(i));
                        removed = true;
                    }
                }
            }
            content.removeAll(toRemove);
        }
       return removed;
    }

}