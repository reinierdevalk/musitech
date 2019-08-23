package de.uos.fmt.musitech.utility.enm;

import java.util.Random;

/**
 * Provides a method for drawing <code>enum</code> elements at random in a single method call. 
 * @author Tillman Weyde
 */
public class EnumTool {

	static final Random RND = new Random();
	
	/**
	 * Returns a random element of the Enum class provided. 
	 * @param enm An enumeration class.
	 * @return A random value of the <code>enm</code> enumeration.
	 */
	public static <E extends Enum<?>> E rand(Class<E> enm){
		return enm.getEnumConstants()[RND.nextInt(enm.getEnumConstants().length)];
	}
	

	/**
	 * Returns a random element of the Array provided. 
	 * @param array An array.
	 * @return A random element out of that array. 
	 */
	public static <E> E rand(E array[]){
		return array[RND.nextInt(array.length)];
	}

	// Just for testing.
	enum TestEnum{
		ONE, TWO, THREE;
	}
	
	/*
	 * Just for testing.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		System.out.println(rand(TestEnum.class));
	}

}
