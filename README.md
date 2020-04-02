# RubyTL

RubyTL is a model transformation tool embedded into Ruby. It thas the following components:

  * A model transformation language, RubyTL (yes, RubyTL is included in RubyTL)
  * Code generation through a DSL plus code templates
  * An OCL-like validation language
  * EMOF and ECore import and export (using the RMOF project)
  * Chaining of transformation tasks through Rake.
  * An Eclipse-based environment (called AGE) providing editors with syntax highlighting, facilities to launch transformations, etc.

If you have any comment or suggestion, send an e-mail to jesusc[at]um.es. We really appreciate any report about experiences (including bad ones) using AGE. 

## Installation

To install AGE use the update site:

  * http://sanchezcuadrado.es/projects/rubytl/updatesite 

Tested with:

  * Ruby 2.1
  * Eclipse Mars

### Configuration

 Once installed the interpreter, AGE should be configured with the path where the Ruby interpreter has been installed. Click on Windows -> Preferences and select Ruby -> Installed Interpreters preferences. In this dialog, you can add the installed interpreter, usually the path c:\bin\ruby.exe or /usr/bin/ruby is used.

You can also watch the [Introductory Screencast](http://gts.inf.um.es/trac/age/wiki/Screencasts) Introductory Screencast to see the steps.

## Tutorial

A tutorial of the original version of RubyTL, which is still mostly valid. [Here](http://gts.inf.um.es/trac/age/chrome/site/tutorial.pdf)

## Examples

Here are a few examples to try RubyTL.

### Class2Java

[Download link](http://gts.inf.um.es/trac/age/attachment/wiki/TransformationExamples/class2java.tar.gz) 

This example provides three transformations between a simplified UML-like class model and a Java model. The transformations are organized in an increasing level of complexity:

 *  class2java-simple.rb This transformation only deals with transforming classes into Java classes, and attributes into a private field plus a get and set method. In addition, it provides methods to convert an arbitrary naming style to the Java convention. It is an example of the use of Ruby code when doing certain tranformation tasks. 

 *  class2java-operations.rb This transformation, in addition, transforms class operations into Java methods, and it handles the situation of name clashes. Also, it creates a package to place the generated Java classes. 

* class2java-inherit-simple.rb To deal with multiple inheritance, an interface is generated for every class. Then, when transforming a class with more than one parent class, one of them will be selected to be the superclass and the rest will be interfaces. The criterion is to select the base class with more methods. 

* There is also a model-to-code transformation that shows how to use transformation rules to map model elements to code, instead of using templates.

### Class2Table


[Download link](http://gts.inf.um.es/trac/age/attachment/wiki/TransformationExamples/class2table-tutorial.tar.gz) 

This example, which is used in the tutorial, includes model transformation, code generation and model validation tasks.

### Examination assistant


[Download link](http://gts.inf.um.es/trac/age/attachment/wiki/TransformationExamples/examination-assistant.tar.gz) 

This example shows how the phasing mechanism provided by RubyTL can be used to improve modularity of a transformation definition. 


## Some history

RubyTL was created as part of my PhD thesis. I ceased its maintenance in 2010 and it became out-of-date with respect to newer Ruby and Eclipse versions (it only worked for Ruby 1.8.7 and Eclipse Kepler). For some unknown reason (probably due to nostalgia) I have recently decided to update the code base to make it work with more recent versions. I hope that this is useful for somebody.

## Related papers

<ol class="publication-list-by-category">
		<li class="publication"><strong class="publication-title">RubyTL: A Practical, Extensible Transformation Language</strong>. Jesús Sánchez Cuadrado, Jesús García Molina and Marcos Menarguez Tortosa. 2nd European Conference on Model Driven Architecture (ECMDA'06), 2006 <a class="pdflink" href="http://sanchezcuadrado.es/papers/ecmda2006.rubytl.jsanchez.pdf">[PDF]</a>. </li>  
  <li class="publication"><strong class="publication-title">Modularization of model transformations through a phasing mechanism</strong>. Jesús Sánchez Cuadrado and Jesús García Molina. Software and Systems Modeling, 2009 <a class="pdflink" href="http://sanchezcuadrado.es/papers/sosym2008.phasing-modularization.jsanchez.pdf"> [PDF]</a>. <a class="publishedlink" href="http://www.springerlink.com/content/d24x280842823006/">Publisher site.</a> </li>   
		<li class="publication"><strong class="publication-title">Building domain-specific languages for model-driven development</strong>. Jesús Sánchez Cuadrado and Jesús García Molina. IEEE Software, 2007 <a class="pdflink" href="http://sanchezcuadrado.es/papers/ieeesoftware2007.embedded-dsls.jsanchez.pdf"> [PDF]</a>. <a class="publishedlink" href="http://www.computer.org/portal/web/csdl/doi/10.1109/MS.2007.135">Publisher site.</a> </li>   
		<li class="publication"><strong class="publication-title">A Model-Based Approach to Families of Embedded Domain Specific Languages</strong>. Jesús Sánchez Cuadrado and Jesús García Molina. IEEE Transactions on Software Engineering, 2009 <a class="pdflink" href="http://sanchezcuadrado.es/papers/tse2009.families-dsls.jsanchez.pdf"> [PDF]</a>.  </li>   
	 <li class="publication"><strong class="publication-title">Approaches for Model Transformation Reuse: Factorization and Composition</strong>. Jesús Sánchez Cuadrado and Jesús García Molina. 1st International Conference on Model Transformations (ICMT'08), 2008 <a class="pdflink" href="http://sanchezcuadrado.es/papers/icmt2008.transformation-reuse.jsanchez.pdf">[PDF]</a>. </li>  
	 <li class="publication"><strong class="publication-title">A phasing mechanism for model transformation languages</strong>. Jesús Sánchez Cuadrado and Jesús García Molina. The 22nd Annual ACM Symposium on Applied Computing (SAC'07), 2007 <a class="pdflink" href="http://sanchezcuadrado.es/papers/sac2007.phasing.jsanchez.pdf">[PDF]</a>. </li>  
  <li class="publication"><strong class="publication-title">A Plugin-Based Language to Experiment with Model Transformation</strong>. Jesús Sánchez Cuadrado and Jesús García Molina. 9th International Conference on Model Driven Engineering Languages and Systems, MoDELS 2006, 2006 <a class="pdflink" href="http://sanchezcuadrado.es/papers/models2006.rubytl-experimenting.jsanchez.pdf">[PDF]</a>. </li>    
	</ol>
