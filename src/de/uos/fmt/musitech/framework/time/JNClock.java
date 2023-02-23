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
package de.uos.fmt.musitech.framework.time;


/**
   @version 1.00 2001-06-05
   @author <strong> Lonce Wyse <\strong>
*/


public class JNClock implements MTimer
{
    public native long StartTimer(int i_BufferTimeMS);
    public native void StopTimer();
    public native long TimeGetTime();
    
  

    public JNClock() {
		
		System.loadLibrary("MMTime");
		StartTimer(1);
		TimeGetTime();
    }

    /**
       This method is "called back" by the native timer callback function
       @param ntime The period of the callback in ms
    */
    private long lasttime = 0;
    private void JTimerCallback(long ntime){
		// Do callback processing here ...
		long delta = ntime-lasttime;
//		System.out.println("delta time since last call = " + delta);
		lasttime=ntime;
    }

    /********************************************************************/
    /**
       This main() is just for testing this class ...
    */
    public static void main(String[] args)
    {
		int timer_period = 5;
		int rundur = 100*timer_period;		

		JNClock ntimer = new JNClock();	

		System.out.println("MS Time upon loading is " + ntimer.getTimeMicros());

		for(int i=0; i<10; i++){
			System.out.println("next MS Time is " + ntimer.getTimeMicros());
			ntimer.doze(1);    
		}

		System.out.println("first trail finished " + ntimer.getTimeMicros());

		ntimer.doze(100); 
	
		for(int i=0; i<10; i++){
			System.out.println("next MS Time is " + ntimer.getTimeMicros());
			ntimer.doze(0);    
		}

		System.out.println("second trail finished " + ntimer.getTimeMicros());


		ntimer.StopTimer();

		ntimer.StartTimer(timer_period);
		ntimer.doze(rundur);    //while timer runs
		ntimer.StopTimer();

		System.out.println("done");

		System.exit(0);
    }


    /**
	* Helper function to make main more readable.
    */
    private void doze(long ms)
    {
		try{
		    Thread.sleep(ms);
		}
		catch(InterruptedException e){}
    }

	/** 
	 * @see de.uos.fmt.musitech.framework.time.MTimer#getTimeMicros()
	 */
	@Override
	public long getTimeMicros() {
		
		return TimeGetTime()*1000;
	}

};

