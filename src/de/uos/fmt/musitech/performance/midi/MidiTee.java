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
package de.uos.fmt.musitech.performance.midi;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

/**
 * A midi duplexer.
 *
 * A receiver that gets MidiEvents and
 * sends them to two other receivers. based on MidiThruExample by
 * <A HREF="mailto:G.Gehnen@atrie.de">Gerrit Gehnen</A>
 * and <A HREF="mailto:niels@bonneville.nl">Niels Gorisse</A>.
 * @author TW
 * @version 0.109
 */
public class MidiTee implements Receiver {
	Receiver out1;
	Receiver out2;
	boolean on1= true ;
	boolean on2= true ;

	@Override
	public void close() {
	}

	@Override
	public void send(MidiMessage event, long time) {
		if (on1)
			out1.send(event, time);
		if (on2)
			out2.send(event, time);
	}

	/**
	 * MidiTee constructor.
	 * @param out1 Receiver
	 * @param out2 Receiver
	 */
	public MidiTee(Receiver out1, Receiver out2) {
	    if(out1 == null)
	        on1 = false;
		this.out1 = out1;
	    if(out2 == null)
	        on2 = false;
		this.out2 = out2;
	}
}