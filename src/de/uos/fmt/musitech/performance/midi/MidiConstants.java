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
 * File MidiConstants.java Created on 22.05.2004
 */

package de.uos.fmt.musitech.performance.midi;

/**
 * TODO add class coment
 * @author Tillman Weyde
 */
public interface MidiConstants {

	/**
	 * General Midi Instruments
	 */

	// Pianos
	final int GM_ACOUSTIC_GRAND = 0;
	final int GM_BRIGHT_GRAND = 1;
	final int GM_ELECTRIC_GRAND = 2;
	final int GM_HONKY_TONK = 3;
	final int GM_ELECTRIC_PIANO_1 = 4;
	final int GM_ELECTRIC_PIANO_2 = 5;
	final int GM_HARPSICHORD = 6;
	final int GM_CLAVICHORD = 7;

	// Tuned Idiophones
	final int GM_CELESTA = 8;
	final int GM_GLOCKENSPIEL = 9;
	final int GM_MUSIC_BOX = 10;
	final int GM_VIBRAPHONE = 11;
	final int GM_MARIMBA = 12;
	final int GM_XYLOPHONE = 13;
	final int GM_TUBULAR_BELLS = 14;
	final int GM_DULCIMER = 15;

	// Organs
	final int GM_DRAWBAR_ORGAN = 16;
	final int GM_PERCUSSIVE_ORGAN = 17;
	final int GM_ROCK_ORGAN = 18;
	final int GM_CHURCH_ORGAN = 19;
	final int GM_REED_ORGAN = 20;
	final int GM_ACCORDION = 21;
	final int GM_HARMONICA = 22;
	final int GM_TANGO_ACCORDION = 23;

	// Guitars
	final int GM_ACOUSTIC_GUITAR_NYLON = 24;
	final int GM_ACOUSTIC_GUITAR_STEEL = 25;
	final int GM_ELECTRIC_GUITAR_JAZZ = 26;
	final int GM_ELECTRIC_GUITAR_CLEAN = 27;
	final int GM_ELECTRIC_GUITAR_MUTED = 28;
	final int GM_OVERDRIVEN_GUITAR = 29;
	final int GM_DISTORTION_GUITAR = 30;
	final int GM_GUITAR_HARMONICS = 31;

	// Basses
	final int GM_ACOUSTIC_BASS = 32;
	final int GM_ELECTRIC_BASS_FINGER = 33;
	final int GM_ELECTRIC_BASS_PICK = 34;
	final int GM_FRETLESS_BASS = 35;
	final int GM_SLAP_BASS_1 = 36;
	final int GM_SLAP_BASS_2 = 37;
	final int GM_SYNTH_BASS_1 = 38;
	final int GM_SYNTH_BASS_2 = 39;

	// Strings and Timpani
	final int GM_VIOLIN = 40;
	final int GM_VIOLA = 41;
	final int GM_CELLO = 42;
	final int GM_CONTRABASS = 43;
	final int GM_TREMOLO_STRINGS = 44;
	final int GM_PIZZICATO_STRINGS = 45;
	final int GM_ORCHESTRAL_STRINGS = 46;
	final int GM_TIMPANI = 47;

	// Ensemble Strings and Voices
	final int GM_STRING_ENSEMBLE_1 = 48;
	final int GM_STRING_ENSEMBLE_2 = 49;
	final int GM_SYNTH_STRINGS_1 = 50;
	final int GM_SYNTH_STRINGS_2 = 51;
	final int GM_VOICE_AAH = 52;
	final int GM_VOICE_OOH = 53;
	final int GM_SYNTH_VOICE = 54;
	final int GM_ORCHESTRA_HIT = 55;

	// Brass
	final int GM_TRUMPET = 56;
	final int GM_TROMBONE = 57;
	final int GM_TUBA = 58;
	final int GM_MUTED_TRUMPET = 59;
	final int GM_FRENCH_HORN = 60;
	final int GM_BRASS_SECTION = 61;
	final int GM_SYNTH_BRASS_1 = 62;
	final int GM_SYNTH_BRASS_2 = 63;

	// Reeds
	final int GM_SOPRANO_SAX = 64;
	final int GM_ALTO_SAX = 65;
	final int GM_TENOR_SAX = 66;
	final int GM_BARITONE_SAX = 67;
	final int GM_OBOE = 68;
	final int GM_ENGLISH_HORN = 69;
	final int GM_BASSOON = 70;
	final int GM_CLARINET = 71;

	// Pipes
	final int GM_PICCOLO = 72;
	final int GM_FLUTE = 73;
	final int GM_RECORDER = 74;
	final int GM_PAN_FLUTE = 75;
	final int GM_BLOWN_BOTTLE = 76;
	final int GM_SHAKUHACHI = 77;
	final int GM_WHISTLE = 78;
	final int GM_OCARINA = 79;

	// Synth Leads (Synonyms)
	final int GM_LEAD_1_SQUARE_WAVE = 80;
	final int GM_LEAD_2_SAWTOOTH_WAVE = 81;
	final int GM_LEAD_3_CALLIOPE = 82;
	final int GM_LEAD_4_CHIFF = 83;
	final int GM_LEAD_5_CHARANG = 84;
	final int GM_LEAD_6_VOICE = 85;
	final int GM_LEAD_7_FIFTHS = 86;
	final int GM_LEAD_8_BASS_LEAD = 87;

	// Synth Pads (Synonyms)
	final int GM_PAD_1_NEW_AGE = 88;
	final int GM_PAD_2_WARM = 89;
	final int GM_PAD_3_POLYSYNTH = 90;
	final int GM_PAD_4_CHOIR = 91;
	final int GM_PAD_5_BOWED = 92;
	final int GM_PAD_6_METALLIC = 93;
	final int GM_PAD_7_HALO = 94;
	final int GM_PAD_8_SWEEP = 95;
	
	// Musical Effects
	final int GM_FX_1 = 96;
	final int GM_FX_2 = 97;
	final int GM_FX_3 = 98;
	final int GM_FX_4 = 99;
	final int GM_FX_5 = 100;
	final int GM_FX_6 = 101;
	final int GM_FX_7 = 102;
	final int GM_FX_8 = 103;

	// Ethnic
	final int GM_SITAR = 104;
	final int GM_BANJO = 105;
	final int GM_SHAMISEN = 106;
	final int GM_KOTO = 107;
	final int GM_KALIMBA = 108;
	final int GM_BAGPIPE = 109;
	final int GM_FIDDLE = 110;
	final int GM_SHANAI = 111;

	// Percussion
	final int GM_TINKLE_BELL = 112;
	final int GM_AGOGO = 113;
	final int GM_STEEL_DRUMS = 114;
	final int GM_WOODBLOCK = 115;
	final int GM_TAIKO_DRUMS = 116;
	final int GM_MELODIC_TOM = 117;
	final int GM_SYNTH_DRUM = 118;
	final int GM_REVERSE_CYMBAL = 119;

	// Sound Effects
	final int GM_FRET_NOISE = 120;
	final int GM_BREATH_NOISE = 121;
	final int GM_SEASHORE = 122;
	final int GM_BIRD_TWEET = 123;
	final int GM_TELEPHONE = 124;
	final int GM_HELICOPTER = 125;
	final int GM_APPLAUSE = 126;
	final int GM_GUNSHOT = 127;

}