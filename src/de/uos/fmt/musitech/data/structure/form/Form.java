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
package de.uos.fmt.musitech.data.structure.form;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;

/**
 * @author Nicolai Strauch
 * @hibernate.class table="Form"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 */
public class Form extends BasicContainer {

	public static final Class types = FormType.class;
//		{
//		 	Note.class, 
//		 	Motif.class, 
//		 	Phrase.class,
//		 	Section.class, 
//		 	Movement.class,
//			Form.class, 
//			Voice.class
//		};

	public Form() {
		super(null, types);
	}

	public Form(Context context) {
		super(context, types);
	}

	/**
	 * @author tweyde
	 * @hibernate.class table="FormPrototype"
	 * @hibernate.joined-subclass
	 * @hibernate.joined-subclass-key column = "uid"
	 */
	static public class FormPrototype implements MObject, Named {

		private String name = "";
		private String comment = "";

		/**
		 * Default Constructor.
		 */
		public FormPrototype() {
		}

		/**
		 * Constructor with name.
		 */
		public FormPrototype(String name) {
			setName(name);
		}

		/**
		 * getName
		 * 
		 * @see de.uos.fmt.musitech.data.Named#getName()
		 * @hibernate.property
		 */
		public String getName() {
			return name;
		}

		/**
		 * setName
		 * 
		 * @see de.uos.fmt.musitech.data.Named#setName(java.lang.String)
		 */
		public void setName(String name) {
			this.name = name;
		}

		static final public FormPrototype SONATA = new FormPrototype("Sonata");
		static final public FormPrototype RONDO = new FormPrototype("Rondo");
		static final public FormPrototype SONG_ABA = new FormPrototype(
			"Song ABA");
		static final public FormPrototype SONG_AABA = new FormPrototype(
			"Song ABA");

		/**
		 * getComment
		 * 
		 * @return
		 * @hibernate.property
		 */
		public String getComment() {
			return comment;
		}

		/**
		 * setComment
		 * 
		 * @param string
		 */
		public void setComment(String string) {
			comment = string;
		}

		// Unique ID for this object.
		private Long uid;

		/**
		 * getUid
		 * 
		 * @see de.uos.fmt.musitech.data.MObject#getUid()
		 * @hibernate.id generator-class="native"
		 */
		public Long getUid() {
			return uid;
		}

		/**
		 * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
		 *      java.lang.Object)
		 */
		public boolean isValidValue(String propertyName, Object value) {
			// TODO Auto-generated method stub
			return true; // default
		}

		/**
		 * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
		 */
		public void setUid(Long uid) {
			this.uid = uid;
		}
	}
}
