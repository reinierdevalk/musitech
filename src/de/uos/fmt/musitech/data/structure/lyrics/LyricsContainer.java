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
 * LyricsContainer contain LyricSyllableSequences 
 * @author Jens Wissmann
 */
package de.uos.fmt.musitech.data.structure.lyrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.utility.collection.TypedCollection;

/**
 * @author Jens
 */
public class LyricsContainer extends BasicContainer<LyricsSyllableSequence> {

    public LyricsContainer(Locale defaultLanguage) {
        super(null,LyricsSyllableSequence.class);
        setDefaultLanguage(defaultLanguage);
    }

    private Locale defaultLanguage = null;

    /**
     * get the number of verses in the default language
     * 
     * @return number of verses
     */
    public byte getNumberOfVerses() {
        return getNumberOfVerses(getDefaultLanguage());
    }

    /**
     * get the number of verses in a certain language
     * 
     * @param language
     * @return number of verses
     */
    public byte getNumberOfVerses(Locale language) {
        byte num = 0;
        for (Iterator iter = getContent().iterator(); iter.hasNext();) {
            LyricsSyllableSequence seq = (LyricsSyllableSequence) iter.next();
            byte thisVerseNumber = seq.getVerseNumber();
            if (seq.isLanguage(language) && thisVerseNumber > num)
                num = thisVerseNumber;
        }
        return num;
    }

    public Locale[] getLanguages() {
        Set<Locale> languages = new LinkedHashSet<Locale>();
        if (size() > 0) {
            for (Iterator<?> iter = getContent().iterator(); iter.hasNext();) {
                LyricsSyllableSequence seq = (LyricsSyllableSequence) iter.next();
                Locale language = seq.getLanguage();
                if (!languages.contains(language)) {
                    languages.add(language);
                }
            }
        }
        Locale[] array = new Locale[languages.size()];
        int i = 0;
        for (Iterator<Locale> iter = languages.iterator(); iter.hasNext();) {
            Locale element = iter.next();
            array[i++] = element;
        }
        return array;
    }

    /**
     * Get the verse with the given number in the default language
     * 
     * @param verseNum
     * @return verse
     */
    public LyricsSyllableSequence getVerse(byte verseNum) {
        return getVerse(verseNum, getDefaultLanguage());
    }

    /**
     * Get the verse with the given number in the given language
     * 
     * @param verseNum
     *            verse-number
     * @param language
     * @return verse
     */
    public LyricsSyllableSequence getVerse(byte verseNum, Locale language) {
        for (Iterator iter = getContent().iterator(); iter.hasNext();) {
            LyricsSyllableSequence seq = (LyricsSyllableSequence) iter.next();
            if (seq.getVerseNumber() == verseNum && seq.isLanguage(language))
                return seq;
        }
        if(size()>0)
            return get(0);
        return null;
    }

    public LyricsSyllableSequence[] getVerses() {
        return getVerses(getDefaultLanguage());
    }

    public LyricsSyllableSequence[] getVerses(Locale locale) {
        if (size() > 0) {
            List<LyricsSyllableSequence> verses = new ArrayList<LyricsSyllableSequence>();
            //            Collection verses = new ArrayList();
            for (Iterator<?> iter = getContent().iterator(); iter.hasNext();) {
                LyricsSyllableSequence seq = (LyricsSyllableSequence) iter.next();
                if (seq.isLanguage(locale)) {
                    verses.add(seq);
                }
            }
            Collections.sort(verses,new Comparator(){
                public int compare(Object o1, Object o2) {
                    return ((LyricsSyllableSequence)o2).getVerseNumber() - ((LyricsSyllableSequence)o1).getVerseNumber();
                }
                });
            return verses.toArray(new LyricsSyllableSequence[verses.size()]);
            //            LyricsSyllableSequence[] lyrics = new
            // LyricsSyllableSequence[verses.si]
        }
        return null;
    }

    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Locale defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * @param lyrics
     */
    public void addLyricsSegment(LyricsContainer lyrics) {
        for (Iterator iter = lyrics.getContent().iterator(); iter.hasNext();) {
            LyricsSyllableSequence seq = (LyricsSyllableSequence) iter.next();
            // get fitting sequence according to language and verse number
            LyricsSyllableSequence fit = this.getVerse(seq.getVerseNumber(), seq.getLanguage());
            if (fit == null) {
                this.add(seq);
            } else {
                fit.addAll(seq);
            }
        }
    }
}