# Things only for testing
=begin
require '/home/jesus/usr/eclipse-age-dev/workspace/gts.age.jruby/ruby-lib/jparsetree.rb'
$sharedContext = Object.new
def $sharedContext.getShared(x)
	return '/home/jesus/tree.example'
end
=end

# Start of the real code
require 'java'

# To avoid errors it must be imported globally, why?
# Maybe a JRuby bug
include JParseTree 
class ParseTreeLookup
	
  def parse(path)
	File.open(path) do |f|
	  processor = ParseTree.new(false)
	  @result = processor.parse_tree_for_string(f.read, path) 
	end  
  end
  
  def lookup_keyword(name)
  	recursive_lookup(@result, :fcall, fcalls = [])
	recursive_lookup(fcalls, name.intern, keywords = [])
	
	result = keywords.map do |v|
	   k = KeywordResult.new(v[1])
	   args = v[2][1..-1]
	   args.each do |arg|
	   	 k.arguments << arg[1].to_s
	   end
	   k
	end
	result
  end
  
  class KeywordResult
  	attr_reader :keyword
  	attr_reader :arguments
  	def initialize(keyword)
  		@keyword = keyword
  		@arguments = []
  	end
  end

private

  def recursive_lookup(list, symbol, result, &block)
  	list.each_with_index do |element, idx|
	  if element == symbol
        result << list # list[idx + 1..-1] 
  	  else
  	    recursive_lookup(element, symbol, result) if element.kind_of?(Array)
  	  end
  	end
  end
end

class PluginParameterDSL
  attr_reader :parameters

  def initialize
  	@parameters = []
  end

  def self.evaluate_file(filename)
  	context = self.new
  	File.open(filename) do |f|
  	  context.instance_eval(f.read, filename)
  	end
  	context
  end

  def interpret(shared)
  	@parameters.each do |p|
  	  p.interpret(shared)
  	end
  end

  def parameter(&block)
  	@parameters << Parameter.new
  	@parameters.last.instance_eval(&block)
  end

  class Parameter
  	def description(value); @description_ = value; end
  	def name(value); @name_ = value; end
  	def type(value); @type_ = value; end
  	def datatype(value); @datatype_ = value; end
  	def retrieve_elements(&block); @retrieve_elements_ = block; end
  
  	def interpret(shared)
	  factory    = shared.getShared("factory")
	  parameters = shared.getShared("plugin.parameters")

	  retrieve_context = RetrieveContext.new(shared.getShared("transformation.path"))
      java_parameter = interpret_type(factory, retrieve_context)
      java_parameter.description = @description_
      java_formater  = interpret_datatype(factory)      
      java_parameter.formater = java_formater
      
      parameters.addParameter(java_parameter)
  	end
  private
    def interpret_type(factory, retrieve_context)
      if @type_ == :off_elements
      	checklist = factory.new_checklist_type 
       	elements  = evaluate_retrieve_elements(retrieve_context)
		puts "No elements retrieved" unless elements
		raise "No elements retrieved" unless elements

        elements.each do |id, on_off|
          checklist.add_element(id, on_off)
        end
        return checklist
      end
      raise "Unknown type"
    end
    
    def interpret_datatype(factory)
      return factory.new_id_list_formater if @datatype_ == :id_list
      raise "Unknown datatype"
    end
    
    def evaluate_retrieve_elements(context)
	  context.instance_eval(&@retrieve_elements_)    
    end
  end
  
  class RetrieveContext
  	attr_reader :transformation_filename
  	
  	def initialize(transformation_filename)
  	  @transformation_filename = transformation_filename
  	end
  	
  	def parse_tree(filename)
	  lookup = ParseTreeLookup.new
  	  lookup.parse(filename)
 	  lookup
 	end
  end
end

=begin
	puts "Start"
	path = $sharedContext.getShared('transformation.path')
	require 'pp'
	lookup = ParseTreeLookup.new
	pp lookup.parse(path)
	#lookup.lookup_keyword('phase')
	
	puts "Start"
	path = $sharedContext.getShared('transformation.path')
	
	lookup = ParseTreeLookup.new
	lookup.parse(path)
	lookup.lookup_keyword('phase')
	
	exit
=end

filename = $sharedContext.getShared('uidefinition.filename')
dsl = PluginParameterDSL.evaluate_file(filename)
dsl.interpret($sharedContext)

puts "finished"

