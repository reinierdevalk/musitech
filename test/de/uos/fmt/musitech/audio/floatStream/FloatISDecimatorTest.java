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
 * File FloatISDecimatorTest.java
 * Created on 18.08.2004 by Tillman Weyde.
 */

package de.uos.fmt.musitech.audio.floatStream;

import java.io.IOException;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.audio.AudioFileObject;

/**
 * TODO add class comment
 * 
 * @author Tillman Weyde
 */
public class FloatISDecimatorTest extends TestCase {

    public void testSampleNum() {
        try {
            //            sampleNumComp(1.0f);
            sampleNumComp(0.5f);
            sampleNumComp(2f);
            sampleNumComp(0.43563f);
            sampleNumComp(0.25f);
            sampleNumComp(0.125f);
            sampleNumComp(0.0625f);
            sampleNumComp(2.5f);
            sampleNumComp(3f);
            sampleNumComp(4f);
            sampleNumComp(8f);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void sampleNumComp(float factor) throws IOException {
        AudioFileObject afo;
        afo = new AudioFileObject(getClass().getResource("tabla01.wav"));
        FloatInputStream fis = afo.getFloatInputStream();
        long samples = afo.getLengthInSamples();
        //    long samples = fis.remainingSamples();
        FISMultiplexer mux = new FISMultiplexer(fis);
        FloatInputStream fis1 = mux.getMultiplexerMember();
        FloatInputStream fis2 = mux.getMultiplexerMember();
        FISSampleRateDecimator decim = new FISSampleRateDecimator(fis2);
        decim.setFrameRate(fis1.getFormat().getFrameRate() * factor);
        long read1 = 0, read2 = 0;
        long total1 = 0, total2 = 0;
        float data[][] = new float[fis1.getFormat().getChannels()][1024];
                do {
                    read1 = fis1.read(data);
                    if (read1 >= 0)
                        total1 += read1;
                    read2 = decim.read(data);
                    if (read2 >= 0)
                        total2 += read2;
                } while (read1 != -1 || read2 != -1);
                fis.reset();
                fis1.reset();
                decim.reset();
        long total3 = fis.remainingSamples();
        mux.deleteMultiplexerMember(fis1);
        mux.deleteMultiplexerMember(fis2);
        FISSampleRateDecimator decim2 = new FISSampleRateDecimator(fis);
        decim2.setFrameRate(fis.getFormat().getFrameRate() * factor);
        long total4 = decim2.remainingSamples();
        long total5 = fis.remainingSamples();
        long total7 = 0;
        do {
            read1 = fis.read(data);
            if (read1 >= 0)
                total7 += read1;
        } while (read1 != -1);
        fis.reset();
        long total6 = 0;
        do {
            try {
                read2 = decim2.read(data);
                if (read2 > 0)
                    total6 += read2;
            } catch (Exception e) {
                fail(e.getMessage());
            }
        } while (read2 != -1);
        assertEquals("factor: "+factor,total5 * factor, total6, 1 * Math.max(factor, 1 / factor));
    }

}