/*
 * Created on Feb 1, 2005
 *
 */
package org.rubypeople.rdt.internal.ui.text;


/**
 * @author Chris
 *
 */
public interface IRubyPartitions {

	/**
	 * The name of the Ruby partitioning.
	 * @since 0.7.0
	 */
	public final static String RUBY_PARTITIONING= "___ruby_partitioning";  //$NON-NLS-1$
	
	/**
     * The identifier default ruby code partition content type.
     */
    String RUBY_DEFAULT= "__ruby_default"; //$NON-NLS-1$	
   
    /**
     * The identifier of the single-line end comment partition content type.
     */
    String RUBY_SINGLE_LINE_COMMENT= "__ruby_singleline_comment"; //$NON-NLS-1$

    /**
     * The identifier multi-line comment partition content type.
     */
    String RUBY_MULTI_LINE_COMMENT= "__ruby_multiline_comment"; //$NON-NLS-1$	
    
    /**
     * The identifier regular expression partition content type.
     */
    String RUBY_REGULAR_EXPRESSION= "__ruby_regular_expression"; //$NON-NLS-1$	
    
    /**
     * The identifier string partition content type.
     */
    String RUBY_STRING= "__ruby_string"; //$NON-NLS-1$	
    
    /**
     * The identifier command partition content type.
     */
    String RUBY_COMMAND= "__ruby_command"; //$NON-NLS-1$	
}
