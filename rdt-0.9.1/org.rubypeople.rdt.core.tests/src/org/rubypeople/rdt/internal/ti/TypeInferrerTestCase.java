package org.rubypeople.rdt.internal.ti;

import java.util.List;

import junit.framework.TestCase;

public abstract class TypeInferrerTestCase extends TestCase {

	protected ITypeInferrer inferrer;

	public TypeInferrerTestCase() {
		super();
	}

	public void setUp() {
		inferrer = createTypeInferrer();
	}

	/**
	 * Shortcut for testing that a particular type is the only one inferred, 
	 * and is inferred with 100% confidence
	 * @param guesses
	 * @param type
	 */
	protected void assertInfersTypeWithoutDoubt(List<ITypeGuess> guesses, String type) {
		assertEquals(1, guesses.size());
		ITypeGuess guess = guesses.get(0);
		assertEquals(type, guess.getType());
		assertEquals(100, guess.getConfidence());		
	}
	
	/**
	 * Shortcut for testing that two types are inferred, each with 50% confidence
	 * @param guesses
	 * @param type
	 * @param type2
	 */

	protected void assertInfersTypeFiftyFifty( List<ITypeGuess> guesses, String type1, String type2 ) {
		assertEquals(2, guesses.size());
		assertEquals( guesses.get(0).getType(), type1 );
		assertEquals( guesses.get(1).getType(), type2 );
		assertEquals( guesses.get(0).getConfidence(), 50 );
		assertEquals( guesses.get(1).getConfidence(), 50 );
	}

	/**
	 * Override this method in subclasses so that we can test any 
	 * implementation of ITypeInferrer the same way.
	 * @return an implementation of ITypeInferrer
	 */
	protected abstract ITypeInferrer createTypeInferrer();

}