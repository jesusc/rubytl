package org.rubypeople.rdt.internal.ti;


/**
 * @author Jason
 *
 */
public class TypeInferrerTest extends TypeInferrerTestCase {
	
	protected ITypeInferrer createTypeInferrer() {
		return new DefaultTypeInferrer();
	}
	
	public void testFixnum() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("5", 0), "Fixnum");
	}
	
	public void testString() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("'string'", 3), "String");
	}
	
	public void testFixnumAssignment() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("var=5", 1), "Fixnum");
	}
	
	public void testLocalVariableAfterAssignment() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5;x", 4), "Fixnum");
	}

	public void testLocalVariableAfterAssignmentInsideScope() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("module M;x=5;x;end", 13), "Fixnum");
	}

	public void testLocalVariableAfterAssignmentWithOverwrite() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5;x='foo';x", 12), "String");
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5;y='foo';x", 12), "Fixnum");
	}
	
	public void testLocalVariableAssignmentToLocalVariable() throws Exception {
		String script = "x=5;y=x;x;y";	
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 8), "Fixnum");  // "x"
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 10), "Fixnum");  // "y"
	}
	
	public void testLocalVariableAssignmentToLocalVariableTwice() throws Exception {
		String script = "x=5;y=x;z=y;z;y;x";
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 12), "Fixnum");  // "z"
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 14), "Fixnum");  // "y"
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 16), "Fixnum");  // "x"
	}
	public void testLocalVariableAssignmentToWellKnownMethodCall() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5.to_s;x", 9), "String");
	}

	public void testLocalVariableAssignmentToClassInstantiation() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=Regexp.new;x", 13), "Regexp");
	}
	
	public void testInfiniteLoop() throws Exception {
		inferrer.infer("@inst = 1;@inst = @inst.blah", 15);
		assertTrue(true);
	}

}
