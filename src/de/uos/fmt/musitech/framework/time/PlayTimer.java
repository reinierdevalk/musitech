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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.MetricTimeable;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This is the central timer for playback in MUSITECH. It supports players in
 * two ways. Players can query the current time (pull mode), or they can be
 * notified by the PlayTimer (push mode) if they implement Timable. Querying
 * (pull) can be used for higher precision, e.g. for MIDI playback. Notification
 * (push mode) does not need an extra thread, but it happens only about 10 times
 * per second (good enough for many graphics).
 * 
 * @author Tillman Weyde
 */
public class PlayTimer {
    
    //TODO test if endTime < current time leads to errors 

    private boolean playing = false;
    private boolean recording = false;

    private MTimer timer = MTimerFactory.getTimer();
    private long offset = 0;
    private long timePlayed = 0; // playTime in microSec
    private List<Player> playersRegistered = new ArrayList<Player>();
    /** <code>playersRegisteredForPush</code> is default visible to improve performance. */
    List<Timeable> playersRegisteredForPush = new ArrayList<Timeable>();
    /** <code>metricPlayersRegisteredForPush</code> is default visible to improve performance. */
    List<MetricTimeable> metricPlayersRegisteredForPush = new ArrayList<MetricTimeable>();
    /** Comment for <code>endOfAllPlayers</code>  is default visible to improve performance. */
    long endOfAllPlayers = 0;
    long stopTime = Long.MAX_VALUE;

    private Container<Locator> locators;
    
    private PushThread pushTimeThread = new PushThread();
    private MetronomePlayTimer metroPlayTimer;
    private MetronomePanel metroPanel;
    private Context context = Context.getDefaultContext();
    
    private Runnable runOnStop;

    /**
     * Singleton-pattern: constructor only called once and not visible to others.
     */
    private PlayTimer() {
        pushTimeThread.start();
        initMetronomTimer();
    }

    //	static Map timerMap = new HashMap();
    //
    //	/**
    //	 * There is one PlayTimer per Context, so that there can be
    //	 * different play positions.
    //	 * @param context
    //	 * @return
    //	 */
    //	public static synchronized PlayTimer getTimer(Context context) {
    //		PlayTimer timer = (PlayTimer) timerMap.get(context);
    //		if(timer == null){
    //			timer = new PlayTimer();
    //			timerMap.put(context,timer);
    //		}
    //		return instance;
    //	}

    /**
     * This class is used to avoid endless calls between MetronomePlayTimer and
     * PlayTimer This class is overridden in MetronomePlayTimer
     *  
     */
    void initMetronomTimer() {
        metroPanel = new MetronomePanel();
        metroPlayTimer = new MetronomePlayTimer(this);
    }

    private static PlayTimer instance = new PlayTimer();

    private boolean preCount;
    // used in getPlayTimeMicros
    //private boolean whilePreCount = false;
    // used to remember playTime while Precount
    private long preCountOffset;
    private long startTime;

    /**
     * Get the instance of the play-timer. This is a singleton, i.e. there is
     * only one instance in one JVM. 
     * TODO: Design solution for multiple instances. One PlayTimer per context ?
     * 
     * @return The timer instance.
     */
    public static PlayTimer getInstance() {
        return instance;
    }

    /**
     * Get the 'playing' state of the player.
     * 
     * @return <code>true</code> if the player is playing, <code>false</code>
     *         if stopped.
     */
    public boolean isPlaying() {
        return playing;
    }

    static int count = 0;
    /** true if the end of a piece has been reached. */
    public boolean reachedEnd;

    /**
     * The thread that notifies all players registered for push mode.
     */
    public class PushThread extends Thread {

        /**
         * TODO comment
         * 
         */
        public PushThread() {
            super("PlayTimer Push" + count++);
        }

        // TODO improve synchronization.
        private boolean stopped = true;

        /**
         * The run method calls the players in push mode in a permanent loop.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            if (DebugState.DEBUG)
                System.out.println("*** Push Thread: start running");
            while (true) {
                while (stopped) {
                    synchronized (this) {
                        if (DebugState.DEBUG)
                            System.out.println("*** Push Thread: waiting to run");
                        // wait to be notified
                        try {
                            if (stopped)
                                this.wait();
                        } catch (InterruptedException e) {
                            if (DebugState.DEBUG)
                                System.out.println("*** Push Thread: resuming");
                            // this exception is thrown, when the
                            // PlayTimeer is started, therefore nothing
                            // needs to be done
                        } // end try/catch
                    } // end synchronized
                } // end while (stopped)
                if(DebugState.DEBUG)
                	System.out.println("*** Push Thread resuming");

                while (true) {
                    long playTime = getPlayTimeMicros();
                    // set time to all timeables
                    for (Iterator<Timeable> iter = playersRegisteredForPush.iterator(); iter.hasNext();) {
                        Timeable element = iter.next();
                        element.setTimePosition(playTime);
                    } // for
                    // set time to all metricTimeables 
                    if ( metricPlayersRegisteredForPush.size() > 0 ) {
                        Rational metricTime = getContext().getPiece().getMetricalTimeLine().getMetricTime(playTime);
                        for (Iterator<MetricTimeable> iter = metricPlayersRegisteredForPush.iterator(); iter.hasNext();) {
                            MetricTimeable element = iter.next();
                            element.setMetricTime(metricTime);
                        }
                    }

                    // stop when all players have finished or stopTime is reached.
                    if (playTime > endOfAllPlayers || playTime > stopTime) {
                    	if(playTime >= endOfAllPlayers)
                    		PlayTimer.this.reachedEnd = true;
                    	if(autoReset){ // Jens: new control parameter 
                    		// this is the old code:
                    		PlayTimer.this.reset();
                    		// stopTime = Long.MAX_VALUE;
                    	}else{ // Jens: quick (and dirty???) solution to prevent Player from jumping back to the beginning
                    		PlayTimer.this.stop();
                    	}
                    }
                    try {
                        Thread.sleep(95);
                    } catch (InterruptedException e) {
                        if (DebugState.DEBUG)
                            System.out.println("Push Thread: interrupted");
                        // this exception is caught if the PlayTimer
                        // is stopped while this thread is sleeping.
                        // Therefore nothing needs to be done.
                    }
                    if (stopped)
                        break;
                }
            }
        }

        /**
         * Stop the thread using the notify mechanism.
         */
        public void stopThread() {
            stopped = true;
            synchronized (this) {
                this.notifyAll();
            }
        }

        /**
         * Start the thread using the notify mechanism.
         */
        public void startThread() {
            stopped = false;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
	
	private boolean autoReset = true;
	public void setAutoReset(boolean autoReset) {
		this.autoReset = autoReset;
	}

    /**
     * Start all players form the current position.
     */
    public synchronized void start(Runnable argOnStop) {
    	runOnStop = argOnStop;
    	start();
    }

	
    /**
     * Start all players form the current position.
     */
    public synchronized void start() {

        if (!playing) {
            if (preCount) {

                preCountOffset = getContext().getPiece().getMetricalTimeLine().getPrecountTime(getPlayTimeMicros());
                if(DebugState.DEBUG)
                	System.out.println("preCountOffset: " + preCountOffset);
                // -1000 to avoid playing first note
                startTime = timePlayed - 1000;
                //	timePlayed = timePlayed - preCountOffset;
            }
            for (Iterator<Player> iterator = playersRegistered.iterator(); iterator.hasNext();) {
                Player player = iterator.next();
                try {
                    player.setTimePosition(getPlayTimeMicros());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                metroPlayTimer.start();
                offset = timer.getTimeMicros() - timePlayed;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (preCount) {
                offset += preCountOffset;
            }
            playing = true;
            for (Iterator<Player> iterator = playersRegistered.iterator(); iterator.hasNext();) {
                Player player = iterator.next();
                // it is possible that stop() is called while
                // this loop is executed
                if (playing) {
                    try {
                        player.start();
//                        System.out.println("Start Player " + player);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            if (playing) {
                pushTimeThread.startThread();
            }

        }
    }

    /**
     * Stops the players and the timer. It keeps the playTime, the next start
     * will continue from the position where the timer stopped.
     */
    public synchronized void stop() {
        if (playing)
            timePlayed = timer.getTimeMicros() - offset;
        playing = false;
        recording = false;
        for (Iterator<Player> iterator = playersRegistered.iterator(); iterator.hasNext();) {
            Player player = iterator.next();
            try {
                player.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (player instanceof Recorder) {
                ((Recorder) player).setRecord(false);
            }
        }
        pushTimeThread.stopThread();
        if(runOnStop != null) {        
        	SwingUtilities.invokeLater(runOnStop);
        	runOnStop = null;
        }

        //	TODO: Sleep avoids MIDI hangers when reset and start is pressed very fast
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Forces the playTimer to stop at stopTimeMillis
     * 
     * 
     * 
     * @param stopTimeMillis: the Time to stop
     */
    public synchronized void stopAt(long stopTimeMillis){
    		this.stopTime = stopTimeMillis;
    }
    
    /**
     * Resets the players, i.e. stops them, and sets the playTime to 0.
     */
    public synchronized void reset() {

        stop();
        setPlayTimeMicros(0);

        for (Iterator<Player> iterator = playersRegistered.iterator(); iterator.hasNext();) {
            Player player = iterator.next();
            try {
                player.reset();
//                System.out.println("Reset Player " + player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * TODO add comment
     *  
     */
    public void record() {
        recording = true;
        for (Iterator<Player> iterator = playersRegistered.iterator(); iterator.hasNext();) {
            Player player = iterator.next();
            try {
                if (!playing)
                    player.start();

                if (player instanceof Recorder) {
                    ((Recorder) player).setRecord(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        offset = timer.getTimeMicros() - timePlayed;
        playing = true;
        pushTimeThread.startThread();

    }

    /**
     * Get the playTime in microseconds.
     * 
     * @return The play time.
     */
    public long getPlayTimeMicros() {
        //		System.out.println("startTime: " + startTime);
        //		System.out.println("micros - offset: " + (timer.getTimeMicros() -
        // offset));
        if (startTime > timer.getTimeMicros() - offset) {
            if(DebugState.DEBUG)
            	System.out.println("getPlayTimeMicros: " + (timer.getTimeMicros() - offset));
            return startTime;
        } else {
            if (playing) {
                return timer.getTimeMicros() - offset;
            } else
                return timePlayed;
        }

    }

    /**
     * Set the playTime in microseconds.
     * 
     * @param time The play time.
     */
    public synchronized void setPlayTimeMicros(long time) {

        reachedEnd = time <= endOfAllPlayers;

        if (playing) {

            offset = timer.getTimeMicros() - time;

            //			timePlayed = time;
        } else {
            timePlayed = time;
        }

        for (Iterator<Player> iterator = playersRegistered.iterator(); iterator.hasNext();) {
            Player player = iterator.next();
            try {
                player.setTimePosition(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Iterator<Timeable> iter = playersRegisteredForPush.iterator(); iter.hasNext();) {
            try {
                Timeable element = iter.next();
                element.setTimePosition(time);
            } catch (ConcurrentModificationException e) {
                iter = playersRegisteredForPush.iterator();
            }
        }
        
        if ( metricPlayersRegisteredForPush.size() > 0 ) {
            Rational metricTime = getContext().getPiece().getMetricalTimeLine().getMetricTime(time);
            for (Iterator<MetricTimeable> iter = metricPlayersRegisteredForPush.iterator(); iter.hasNext();) {
                MetricTimeable element;
                try {
                    element = iter.next();
                } catch (Exception e) {
                    iter = metricPlayersRegisteredForPush.iterator();
                    if (DebugState.DEBUG)
                        System.out.println(e.getStackTrace());
                    continue;
                }
                element.setMetricTime(metricTime);
            }
        }

    }

    /**
     * Register a Timeable for the push mode. It will be notified approximately
     * every 100 milliseconds.
     * 
     * @param timeable The Timeable to register.
     */
    public void registerForPush(Timeable timeable) {
        boolean changed = false;
        if (!playersRegisteredForPush.contains(timeable)){
            changed = playersRegisteredForPush.add(timeable);
        }
        if (changed)
            computeEnd();
    }

    /**
     * Register a Timeable for the push mode. It will be notified approximately
     * every 100 milliseconds.
     * 
     * @param metricTimeable The MetricalTimeable to register.
     */
    public void registerMetricForPush(MetricTimeable metricTimeable) {
        if (!metricPlayersRegisteredForPush.contains(metricTimeable))
            metricPlayersRegisteredForPush.add(metricTimeable);
    }

    /**
     * Removes a player from the list of push-players
     * 
     * @param timeable The Timeable to unregister.
     * @return true if the Timeable was registered.
     */
    public boolean unRegisterForPush(Timeable timeable) {
        boolean changed = playersRegisteredForPush.remove(timeable);
        if (changed)
            computeEnd();
        return changed;
    }

    /**
     * Removes a player from the list of push-players
     * 
     * @param timeable The MetricTimeable to unregister.
     * @return true if the Timeable was registered.
     */
    public boolean unRegisterMetricForPush(MetricTimeable timeable) {
        boolean changed = metricPlayersRegisteredForPush.remove(timeable);
        if (changed)
            computeEnd();
        return changed;
    }

    /**
     * Get the offset between the timer and the PlayTime. It's calculated as
     * timer.getTimeMillis - timePlayed.
     * 
     * @return The offset.
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Register a player, to receive 'start', 'stop', 'reset' and
     * 'setTimePosition'.
     * 
     * @param player The player to register.
     */
    public void registerPlayer(Player player) {
        if (!playersRegistered.contains(player))
            playersRegistered.add(player);
        player.setPlayTimer(this);
        computeEnd();
    }

    /**
     * Register a player, to receive 'start', 'stop', and 'reset'.
     * 
     * @param player The player to register.
     */
    public void unRegisterPlayer(Player player) {
        playersRegistered.remove(player);
        computeEnd();
    }

    /**
     * Updates the end time of the player. This is called whenever a player is
     * registered (normal or push).
     */
    public void computeEnd() {
        long tmpend = 0;
        for (Iterator<Player> iter = playersRegistered.iterator(); iter.hasNext();) {
            Player player = iter.next();
            try {
                long endTime = player.getEndTime();
                if (endTime > tmpend)
                    tmpend = endTime;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Iterator<Timeable> iter = playersRegisteredForPush.iterator(); iter.hasNext();) {
            Timeable timeable = iter.next();
            try {
                long endTime2 = timeable.getEndTime();
                if (endTime2 > tmpend)
                    tmpend = endTime2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Ergänzung 30/11/04
//        for (Iterator iter = metricPlayersRegisteredForPush.iterator(); iter.hasNext();) {
//            MetricTimeable metricTimeable = (MetricTimeable) iter.next();
//            try {
////                long endTime3;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        //Ende Ergänzung
        
        endOfAllPlayers = tmpend;
        
    }

    /**
     * Get the last position to play. This is used for determining the end of
     * playback (e.g. for the length of scales or sliders).
     * 
     * @return the last end time of players in microseconds.
     */
    public long getEndOfAllPlayer() {
        return endOfAllPlayers;
    }

    /**
     * Get the recording state.
     * 
     * @return true if recording.
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * Switch the pre-counter on or off.
     * 
     * @param preCount <code>true</code> to switch on, <code>false</code> to switch off. 
     */
    public void setPreCount(boolean preCount) {
        this.preCount = preCount;
        metroPlayTimer.setPreCount(preCount);
    }

    /**
     * Check if the playTimer has reached to end of the current obejcts to be played.
     * 
     * @return true if the player has reached the end of the 
     */
    public boolean hasReachedEnd() {
        return reachedEnd;
    }

    /**
     * Set the number of Beats to be counted before playing (if preCount is on).
     * 
     * @param num The number of beats for pre-counting.
     */
    public void setPreCountNum(int num) {
        // TODO Auto-generated method stub
    }

    /**
     * Switch the metronome click during playback and recording (true == on).
     * 
     * @param metronomeOn Set to <code>true</code> to switch on, <code>false</code> to switch off.
     */
    public void setMetronome(boolean metronomeOn) {
        metroPlayTimer.setMetronome(metronomeOn);
    }

    /**
     * Get the state of the pre-counter (true == on).
     * 
     * @return <code>true</code> if the pre-count is on, <code>false</code> otherwise.
     */
    public boolean isPreCount() {

        return preCount;
    }

    /**
     * Get the state of the metronome.
     * 
     * @return <code>true</code> if the metronome is on, <code>false</code> otherwise.
     */
    public boolean isMetronome() {

        return false;
    }

    private class MetronomePlayTimer extends PlayTimer {

        Metronome metronom;
//        Metronome.MetronomTimeable metronomTimeable;

//        PlayTimer playTimer;
        MidiPlayer midiPlayer = MidiPlayer.getInstance();

        //		MetronomePlayTimer metroPlayTimer = new MetronomePlayTimer();

        //		private MetronomePlayTimer getInstance(){
        //			return metroPlayTimer;
        //		}

        private MetronomePlayTimer() {

        }

        private MetronomePlayTimer(PlayTimer playTimer) {
//            this.playTimer = playTimer;
            metronom = new Metronome(super.getContext());
            //	metronomTimeable = new metronom.MetronomTimeable();
            playTimer.registerPlayer(metronom);
            playTimer.registerPlayer(midiPlayer);
            playTimer.registerPlayer(PlayTimer.this.getMetronomPanel());
            PlayTimer.this.getMetronomPanel().setPlayTimer(this);
            //	playTimer.registerForPush(metronomTimeable);
            metronom.setPlayTimer(this);
            midiPlayer.setPlayTimer(this);
            //			metroPanel.init();

        }

        /**
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#getPlayTimeMicros()
         */
        public long getPlayTimeMicros() {
            if (playing) {
                //				System.out.println(
                //					"MetronomTimer playing: "
                //						+ (timer.getTimeMicros() - offset)
                //						+ " offset: "
                //						+ offset);
                return timer.getTimeMicros() - offset;

            } else {
                //				System.out.println(
                //					"MetronomTimer not playing: "
                //						+ (timer.getTimeMicros() - offset)
                //						+ " offset: "
                //						+ offset);
                return timePlayed;
            }

        }

        /**
         * Nothing done to avoid endless calls from PlayTimer
         * 
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#initMetronomTimer()
         */
        void initMetronomTimer() {
            // Nothing done to avoid endless calls from PlayTimer
        }

        /**
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#setContext(de.uos.fmt.musitech.data.structure.Context)
         */
        public void setContext(Context context) {
            metronom.setContext(context);
        }

        /**
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#setPlayTimeMicros(long)
         */
        public synchronized void setPlayTimeMicros(long time) {
            metronom.setTimePosition(time);
        }

        /**
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#setMetronome(boolean)
         */
        public void setMetronome(boolean metronomeOn) {

            metronom.setPlayMetronom(metronomeOn);
        }

        /**
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#setPreCount(boolean)
         */
        public void setPreCount(boolean preCount) {

            metronom.setPreCount(preCount);
        }

        /**
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#setPreCountNum(int)
         */
        public void setPreCountNum(int num) {
            super.setPreCountNum(num);
        }

        /**
         * @see de.uos.fmt.musitech.framework.time.PlayTimer#start()
         */
        public void start() {
            //			super.start();
            if (preCount && !playing) {
                metronom.setTimePosition(timePlayed - preCountOffset);
            }
            if (!playing) {
                //				metronom.setTimePosition(timePlayed);
                //				metronom.start();
            }
        }
    }

    /**
     * @return
     */
    public Context getContext() {
        if (context == null) {
            context = Context.getDefaultContext();
        }
        return context;
    }

    /**
     * @param context
     */
    public void setContext(Context context) {
        if (context == null){
            context = Context.getDefaultContext();
        }
        this.context = context;
        metroPlayTimer.setContext(context);
        getMetronomPanel().setContext(context);
        locators = new SortedContainer<Locator>(context, Locator.class, new LocatorComparator());
    }

    /**
     * @return
     */
    public MetronomePanel getMetronomPanel() {
        return metroPanel;
    }
    
    //*****************************
    // Added Locator
    
    /**
     * You must have a context to add Locator
     * @param loc
     * @return
     */
    public boolean addLocator(Locator loc){
        if (context == null || loc == null){
            throw new IllegalArgumentException("Context oder Locator = null");
        }
        return locators.add(loc);
    }
    
    public Container<Locator> getLocators(){
        return locators;
    }
    
    public boolean removeLocator(Locator loc){
        return locators.remove(loc);
    }
    
    /**
     * skips to next locator
     * @return true, if locator exists, false otherwise 
     */
    public boolean nextLocator(){
        return nextLocator(getPlayTimeMicros());
    }
    
    /**
     * skips to next Locator after time
     * @param time
     * @return true, if locator exists, false otherwise
     */
    public boolean nextLocator(long time){
//        if (locators!=null)
        for (Iterator<Locator> iter = locators.iterator(); iter.hasNext();) {
            Locator loc = iter.next();
            if (loc.getTimeInMicros() > time){
                setPlayTimeMicros(loc.getTimeInMicros());
                return true;
            }
        }
        return false;
    }
    
    /**
     *skips to previous Locator before time
     * @param time
     * @return
     */
    public boolean prevLocator(long time){
        Locator oldLoc = null;
//        if (locators!=null)
        for (Iterator<Locator> iter = locators.iterator(); iter.hasNext();) {
            Locator loc = iter.next();
            
            if (loc.getTimeInMicros() >= time){
                if (oldLoc != null){
                setPlayTimeMicros(oldLoc.getTimeInMicros());
                return true;
                }
                else return false;
            }
            oldLoc = loc;
        }
        if (oldLoc!=null)
        assert oldLoc.getTimeInMicros() < time: "loc is last before time";
        if (oldLoc != null){
            setPlayTimeMicros(oldLoc.getTimeInMicros());
            return true;
        }
        return false;
    }
    
    /**
     * skips to previous Locator
     * @return
     */
    public boolean prevLocator(){
        return prevLocator(getPlayTimeMicros());
    }
    
    /**
     * Skips to Locator with name of 'name'
     * 
     * @param name
     * @return
     */
    public boolean toLocator(String name){
        for (Iterator<Locator> iter = locators.iterator(); iter.hasNext();) {
            Locator loc = iter.next();
            if (loc.getName().compareTo(name) == 0){
                setPlayTimeMicros(loc.getTimeInMicros());
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the metricPlayersRegisteredForPush.
     */
    public List<MetricTimeable> getMetricPlayersRegisteredForPush() {
        return metricPlayersRegisteredForPush;
    }
    
    /**
     * @return Returns the playersRegistered.
     */
    public List<Player> getPlayersRegistered() {
        return playersRegistered;
    }
    
    /**
     * @return Returns the playersRegisteredForPush.
     */
    public List<Timeable> getPlayersRegisteredForPush() {
        return playersRegisteredForPush;
    }
    
    
    /**
     * Registers a runnable to be run on the next time the sequencer is stopped (in the Swing thread).
     * @param run The Runnable to run. 
     */
    public void registerForStop(Runnable run){
    	runOnStop = run;
    }
}