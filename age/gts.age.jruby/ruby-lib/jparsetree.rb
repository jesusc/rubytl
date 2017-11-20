require 'java'
require 'jruby'

module JParseTree


def filter_nested_arrays(bl)
    if bl != nil
      while bl.is_a? Array 
        if bl.size == 1 
         if bl.first.is_a? Array
          bl = bl.first
         else 
           break
         end
        else
         break
        end
      end
    end
    bl
  end
  

class ParseTree       

VERSION = '0.9.2.199'

  ##
  # Main driver for ParseTree. Returns an array of arrays containing
  # the parse tree for +klasses+.
  #
  # Structure:
  #
  #   [[:class, classname, superclassname, [:defn :method1, ...], ...], ...]
  def self.translate(klass_or_str, method=nil)
    pt = self.new(false)
    case klass_or_str
    when String
      pt.parse_tree_for_string(klass_or_str)
    else
      unless method.nil? then
        pt.parse_tree_for_method(klass_or_str, method)
      else
        pt.parse_tree(klass_or_str).first
      end
    end
  end



  def parse_tree(*klasses)
    # TODO: implement this
    result = []
    klasses.each do |klass|
      raise "You should call parse_tree_for_method(#{klasses.first}, #{klass}) instead of parse_tree" if Symbol === klass or String === klass
      klassname = klass.name rescue '' # HACK klass.name should never be nil
                                   # Tempfile's DelegateClass(File) seems to
                                   # cause this
      klassname = "UnnamedClass_#{klass.object_id}" if klassname.empty?
      klassname = klassname.to_sym

      code = if Class === klass then
               sc = klass.superclass
               sc_name = ((sc.nil? or sc.name.empty?) ? "nil" : sc.name).intern
               [:class, klassname, sc_name]
             else
               # TODO: handle this in jparsetree!
               [:module, klassname]               
             end

      # TODO: refactor this to use some provider for the Class information
      # This is necessary to support JRuby runtime, RDT project, etc support;

      method_names = []
      method_names += klass.instance_methods false
      method_names += klass.private_instance_methods false
      # protected methods are included in instance_methods, go figure!

      method_names.sort.each do |m|
        code << parse_tree_for_method(klassname, m)
      end
      result << code
    end
    return result
  end

  def parse_tree_for_string(source, filename = "", line = nil,newlines = false)
    expr = parse_tree_for_AST(JRuby.parse(source, filename)).sexpr()
    expr
  end


  def parse_tree_for_AST(node)
   ptv = ParseTreeVisitor.new() 
#   ptv.visit(node)
    begin   
     #p "Start"
     node.accept(ptv)
    rescue Exception
    p "Exception: #{$!}" 
    end
    # NOTE: sexpr is only assigned for :defn and :class; other constructs
    # happen on the stackelements stack;
   if ptv.sexpr == nil or ptv.sexpr == []
      ptv.sexpr = ptv.stackelements.pop
   end 

   if ptv.sexpr.is_a? Array
    firstEl = ptv.sexpr.first
    while firstEl.is_a? Array  
      if ptv.sexpr.size == 1
       ptv.sexpr = firstEl
       firstEl = ptv.sexpr.first
      else 
       break
      end
    end
   end
   ptv
  end

  def parse_tree_for_method(klass, methodNameAsSymbol)
    # Handle attr_accessors writers
    str_name = methodNameAsSymbol.to_s 
    if(str_name.grep(/=$/).size > 0)
      name = str_name[0, str_name.size-1] 
      return [:defn, methodNameAsSymbol, [:attrset, "@#{name}".to_sym]]
    end
    # ensure Method has method 'mc', which returns the AST node of its body
#    load '../lib/runtimeAstProvider.rb'
#    TODO: HACK: make sure this is loaded some other way and abstract this out
#    so it's possible to have different lookup methods (Runtime, RDT, FileSystem, etc)
#    NOTE: this is loaded only when parse_tree_for_method is called, so
#    the modified classes aren't changed unless they have to be (Method,
#    Proc, etc are affected by the runtimeAstProvider lib)
    load 'lib/runtimeAstProvider.rb' 
#    m = klass.method(methodNameAsSymbol)
#    TODO: Problem, this shouldn't create a new object just to check the methods!

     if (klass.is_a? Symbol)
       name_sym = klass
     else 
       name_sym = klass.name.to_sym
     end
     m = Kernel.const_get(name_sym).new.method(methodNameAsSymbol)   
#     m = klass.instance_method(methodNameAsSymbol)
     #puts "Method Type #{m.java_class}"
     method_ast = m.mc
     if method_ast.is_a?(Array) && method_ast.first == :alias
      alias_method = true
      m = Kernel.const_get(klass.name).new.method(method_ast[1].to_sym)
      #puts "Alias Method as: #{m.inspect}"
      method_ast = m.mc
     end

     
     #p method_ast
     if method_ast == nil || method_ast.is_a?(Symbol)
      [:defn, methodNameAsSymbol, :no_ast_available]
     else
        if(method_ast.first == :bmethod)
         [:defn, methodNameAsSymbol, [:bmethod, method_ast[2],parse_tree_for_AST(method_ast[1]).sexpr]]
        else
         if alias_method
          # TODO: add a test that tests an alias that has arguments!!
          [:defn, methodNameAsSymbol, [:fbody, [:scope,[:block, [:args], parse_tree_for_AST(method_ast[1]).sexpr]]]]
         else
          [:defn, methodNameAsSymbol, parse_tree_for_AST(method_ast[1]).sexpr]
         end
        end
     end
  end
end 



include_class "org.jruby.ast.visitor.NodeVisitor"

class ParseTreeVisitor < NodeVisitor

  attr_accessor :sexpr


  def stackelements()
   @stackElements
  end

 
  def initialize()
    super()
    @stackElements = []
  end
  

  def push(top)
   @stackElements << filter_nested_arrays(top)
  end

  def visit(node)
    @stackElements ||= []
    node.accept(self.to_java_object) if node
 #   p ">>>>>>> #{node}"
    nil
  end 
  
  
  
  def visitAliasNode(iVisited)
    #puts iVisited.to_s + " #{iVisited.old_name} #{iVisited.new_name}"    
    @stackElements << [:alias,[:lit, iVisited.new_name.to_sym], [:lit, iVisited.old_name.to_sym]]
    nil
  end
  
  def visitAndNode(iVisited)
    #puts iVisited.to_s
    visit(iVisited.first_node)
    first = @stackElements.pop
    visit(iVisited.second_node)
    second = @stackElements.pop
    @stackElements << [:and, first, second]
    nil
  end
  
  def visitArgsNode(iVisited)
 #   puts "#{iVisited.to_s}, ARGS, #{iVisited.arity.value}"
    iVisited.args.child_nodes.each {|n| puts n.name} unless iVisited.args == nil
    tempArgs = [:args]
    iVisited.args.child_nodes.each {|n| tempArgs << n.name.to_sym} unless iVisited.args == nil
    # TODO finish this!
#p "REST #{iVisited.rest_arg}"

    opt = nil
    if iVisited.opt_args != nil
     # TODO: is only one opt arg possible?
     iVisited.opt_args.child_nodes.each {|n| 
       visit(n)
     } 
     opt = @stackElements.pop
     # this adds it to the [:args], the block comes last
     tempArgs << opt[1]
#     tempArgs << "*c".intern

    end
    if iVisited.rest_arg > 0
      tempArgs << iVisited.rest_arg
    end
#    p "NODES: #{iVisited.opt_args.child_nodes}"
#    iVisited.rest_arg.each {|n| visit(n)} unless iVisited.rest_arg == nil
    blockarg = nil
    if iVisited.block_arg_node != nil
      visit(iVisited.block_arg_node) 
      blockarg = @stackElements.pop
      #tempArgs << @stackElements.pop
    end
    
    if opt != nil
     #p opt
     tempArgs << [:block, opt]
    end
    
    if blockarg 
      @stackElements << [:block_arg_available, tempArgs, blockarg]
    else
     @stackElements << tempArgs
    end
    nil
  end
  
  
  def visitArgsCatNode(iVisited)
    #puts iVisited.to_s
    visit(iVisited.first_node)
    first = @stackElements.pop
    visit(iVisited.second_node)
    second = @stackElements.pop
    @stackElements << [:argscat, first, second]
    nil
  end
  
  def visitArrayNode(iVisited)
    #puts iVisited.to_s
    temp = [:array]
    iVisited.child_nodes.each {|n| 
       visit(n)
       tempTop = @stackElements.pop
      
       if tempTop == :nil
      
         temp << [:nil]
       else
        temp << tempTop
       end
      
    } 
    @stackElements <<  temp
    nil
  end
  
  def visitBackRefNode(iVisited)
#    puts iVisited.to_s + " " + iVisited.type
#   HACK: the type is a char/Fixnum ASCII value, so take it and add it to a string
    type = ""
    type << iVisited.type
    @stackElements << [:back_ref, type.to_sym]
    nil
  end


  
  def visitBeginNode(iVisited)
    #puts iVisited.to_s
    visit(iVisited.body_node)
    bl = filter_nested_arrays(@stackElements.pop)
    @stackElements << [:begin, bl]
    nil
  end
  
  def visitBignumNode(iVisited)
  # TODO: fix Bigum
    #puts iVisited.to_s + " " + iVisited.value
  end
  
  def visitBlockArgNode(iVisited)
    #p "Block"
#    puts iVisited.to_s + " " + iVisited.count
    @stackElements <<  [:block_arg, iVisited.name.to_sym]
    nil
  end
  
  def visitBlockNode(iVisited)
    # puts iVisited.to_s

    tempStack = [:block]
# tempStack = []
   # p "1"
    tempStack = @stackElements.pop unless @stackElements.size < 1
    tempStack ||= []
    iVisited.child_nodes.each {|n| 
       visit(n)
       tempStack << filter_nested_arrays(@stackElements.pop)
    }

    @stackElements << tempStack

    nil
  end
  
  def visitBlockPassNode(iVisited)
   
    # TODO: this has args and iter nodes too
    visit(iVisited.body_node)
    body = @stackElements.pop
    visit(iVisited.iter_node)
    iter = @stackElements.pop
    @stackElements << [:block_pass,  body, iter]
  end
  
  def visitBreakNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.value_node)
    temp = @stackElements.pop
    if temp == nil
      @stackElements << [:break ]
    else 
      @stackElements << [:break, temp]
    end

    nil
  end
  
  def visitConstDeclNode(iVisited)
    # puts iVisited.to_s + " #{iVisited.name}"
    visit(iVisited.value_node)
    @stackElements << [:cdecl, iVisited.name.to_sym,@stackElements.pop]
    nil
  end
  
  def visitClassVarAsgnNode(iVisited)
    # puts iVisited.to_s + " #{iVisited.name}"
    visit(iVisited.value_node)
    @stackElements << [:cvasgn, iVisited.name.to_sym, @stackElements.pop]
    nil
  end
  
  def visitClassVarDeclNode(iVisited)
    visit(iVisited.value_node)
    @stackElements << [:cvdecl, iVisited.name.to_sym,@stackElements.pop]
    nil
  end
  
  def visitClassVarNode(iVisited)
    # puts iVisited.to_s + " @@#{iVisited.name}"
    @stackElements << [:cvar, iVisited.name.to_sym]
    nil 
  end
  
  def visitCallNode(iVisited)
    # puts iVisited.to_s + " #{iVisited.name}"
    visit(iVisited.receiver_node)
    recv = filter_nested_arrays(@stackElements.pop)

    visit(iVisited.args_node)
    args = @stackElements.pop
    if args == nil
      @stackElements << [:call , recv, iVisited.name.to_sym]
    else
      @stackElements << [:call , recv, iVisited.name.to_sym, args]
    end
#    @stackElements << [:call , recv, iVisited.name.to_sym]
    nil
  end
  
  def visitCaseNode(iVisited)
    # puts iVisited.to_s
     visit(iVisited.case_node)
     node = @stackElements.pop
#    visit(iVisited.case_body)
#    @stackElements << [:case]
    @stackElements << []
    iVisited.child_nodes.each {|n| 
      if !n.nil? 
       n.accept(self.to_java_object) unless n.nil?
       temp =  @stackElements.pop 
       if  @stackElements.size > 0
        @stackElements.last << temp 
       else
        # empty body, :case was popped off, so pop it on again
        @stackElements.insert(0,temp)
       end
      end
    }
    @stackElements << [:case, node, @stackElements.pop]
    nil
  end
  
  def visitClassNode(iVisited)
    # puts iVisited.to_s
    @sexpr ||= []
    
#    @sexpr << :class
#    @sexpr << iVisited.CPath.name.to_sym
    clazzname = iVisited.CPath.name.to_sym
    visit(iVisited.super_node)

    if iVisited.super_node != nil 
      superclazz = [:const, iVisited.super_node.name.to_sym]
    else 
      superclazz = nil
    end
    visit(iVisited.body_node)
    klazz = [:class,clazzname, superclazz, [:scope, filter_nested_arrays(@stackElements.pop)]]
    if @stackElements == nil
      @sexpr << klazz
    else
      @stackElements << klazz      
    end
    nil
  end
  
  def visitColon2Node(iVisited)
    # puts iVisited.to_s + " #{iVisited.name}"
    visit(iVisited.left_node) unless iVisited.left_node.nil?
    if iVisited.left_node.nil?
     @stackElements << [:colon2, nil,  iVisited.name.to_sym]
    else 
     @stackElements << [:colon2, @stackElements.pop,  iVisited.name.to_sym]
    end
    nil
  end
  
  def visitColon3Node(iVisited)
    # puts iVisited.to_s + " ::#{iVisited.name}"
    @stackElements << [:colon3, iVisited.name.to_sym]
  end
  
  def visitConstNode(iVisited)
    # puts iVisited.to_s + " #{iVisited.name}"
    @stackElements << [:const, iVisited.name.to_sym]
    nil
  end
  
  def visitDAsgnNode(iVisited)

    visit(iVisited.value_node)
    val = filter_nested_arrays(@stackElements.pop)
    if val == nil
     @stackElements << [:dasgn_curr, iVisited.name.to_sym]
#      @stackElements << [:lasgn, iVisited.name.to_sym]
    else

#     TODO: not a bug; for some reason, putting :lasgn here causes
#     less test failures thatn putting :dasgn 
    @stackElements <<[:lasgn, iVisited.name.to_sym, filter_nested_arrays(val)]   

      
      #p "made it past"
    end
    nil
  end
  
  def visitDRegxNode(iVisited)
    temp = []
   # p "REGX"
    iVisited.child_nodes.each {|n| 
    # TODO: Bug? how to get rid of :dstr?
       if !n.nil?
        n.accept(self.to_java_object) 
        temp << @stackElements.pop
       end
    }
    temp = filter_nested_arrays(temp)
    if iVisited.once
     temp[0] = :dregx_once
    else
     temp[0] = :dregx 
    end
    
    @stackElements << temp
    nil
  end
  
  def visitDStrNode(iVisited)
    # puts iVisited.to_s
    # TODO: bug in parsetree? it returns the first string as string literal, instead of [:str, ..]
    @stackElements << [:dstr]
    
    count = 1
    iVisited.child_nodes.each {|n| 
      # p "#{n}"
       if !n.nil?
        n.accept(self.to_java_object) 
        temp =  @stackElements.pop
        if count == 1
          if temp.first == :str
           # HACK: ParseTree, for some reason, wants the first
           # element of the :dstr to be a string literal, and 
           # not a [:str, val]
           @stackElements.last << temp[1]
          else
           @stackElements.last << filter_nested_arrays(temp)
          end          
        else 
          @stackElements.last << filter_nested_arrays(temp)
        end
        count = count + 1
       end
    }
    nil
  end
  
  def visitDSymbolNode(iVisited)
    temp = []
    if iVisited.child_nodes.size > 0
      iVisited.child_nodes[0].accept(self.to_java_object) 
      temp << @stackElements.pop
    end
#    p "Dsym #{temp.inspect} "
    # HACK: repackage [[:dstr, ...]] -> [:dsym, ...]
    temp = filter_nested_arrays(temp)
    temp[0] = :dsym
    
    @stackElements << temp 
    if iVisited.child_nodes.size > 1
     iVisited.child_nodes.each {|n| 
        if !n.nil?
         #p "n " 
        n.accept(self.to_java_object) 
         temp =  @stackElements.pop
         @stackElements.last << temp
        end
     }
    end
    
    nil
  end
  
  def visitDVarNode(iVisited)
    # puts iVisited.to_s + " #{iVisited.name}"
    # TODO: PROBLEM here?
    @stackElements << [:dvar, iVisited.name.to_sym]
    nil
  end
  
  
  
  
  
  
  def visitDXStrNode(iVisited)
    # puts iVisited.to_s
    # TODO: bug in parsetree? it returns the first string as string literal, instead of [:str, ..]
    @stackElements << [:dxstr]
    
    count = 1
    iVisited.child_nodes.each {|n| 
      # p "#{n}"
       if !n.nil?
        n.accept(self.to_java_object) 
        temp =  @stackElements.pop
        if count == 1
          if temp.first == :str
           # HACK: ParseTree, for some reason, wants the first
           # element of the :dstr to be a string literal, and 
           # not a [:str, val]
           @stackElements.last << temp[1]
          else
           @stackElements.last << filter_nested_arrays(temp)
          end          
        else 
          @stackElements.last << filter_nested_arrays(temp)
        end
        count = count + 1
       end
    }
    nil
  end
  
  
#  def visitDXStrNode(iVisited)
#    # puts iVisited.to_s
##    iVisited.child_nodes.each {|n| visit(n)}
#    iVisited.child_nodes.each {|n| 
#      visit(n)
#      p "#{@stackElements.inspect}"
#    }
#  end
  
  
  
  def visitDefinedNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.expression_node)
    @stackElements << [:defined, @stackElements.pop]
    nil
  end
  
  def visitDefnNode(iVisited)
    @sexpr ||= []
    # puts iVisited.to_s + " #{iVisited.name_node.name}"

    args = nil
    blockarg = nil
    if iVisited.args_node
     iVisited.args_node.accept(self.to_java_object)    
     args = @stackElements.pop
     
     if args.first == :block_arg_available
      blockarg = args[2]
      args = args[1]
     end
    end
    args = args.map{|n|
     # p n
      if n.class == Fixnum
       "*#{iVisited.scope.variables[n]}".to_sym
      else
       n
      end
    }
    
    
    # This is necessary since the block needs all code elements as items of the list and NOT
    # as a list; ie. this checks this and splits them up later in the code
    body = nil
    to_split = false
    if iVisited.body_node
     iVisited.body_node.accept(self.to_java_object)
     body = filter_nested_arrays(@stackElements.pop)
     to_split = (:block == body.first) if body.respond_to? :first
     if to_split
      body.shift 
     end
    end 

     if body && args
      if to_split
       if blockarg != nil
        the_block = [:block, args, blockarg, *body]
       else
        the_block = [:block, args, *body]
       end
      else 
       if blockarg != nil
        the_block = [:block, args, blockarg, body]
       else
        the_block = [:block, args, body]
       end
      end
      @stackElements << [:defn, iVisited.name.to_sym,[:scope, the_block ] ]
     else
      @stackElements << [:defn, iVisited.name.to_sym,[:scope, [:block,[:args], [:nil] ] ] ]
     end
     

    nil
  end
  



  def visitDefsNode(iVisited)
    visit(iVisited.receiver_node)
    recv = @stackElements.pop
    visit(iVisited.args_node)
    args = @stackElements.pop
    
    visit(iVisited.body_node)
    body =  filter_nested_arrays(@stackElements.pop)
    # TODO: handle block args as in :defn
    @stackElements << [:defs, recv, iVisited.name.to_sym, [:scope, [:block, args, body] ]]
    nil
  end

  def visitDotNode(iVisited)
    # puts iVisited.to_s + " #{iVisited.exclusive ? '..' : '...'}"
    visit(iVisited.begin_node)
    beginNode = @stackElements.pop
    visit(iVisited.end_node)
    endNode = @stackElements.pop

    if beginNode.first == :lit
     beginValue = beginNode[1]
    end
    if endNode.first == :lit
     endValue = endNode[1]
    end
    
    symbol = iVisited.exclusive ? :dot3 : :dot2
    
    if beginNode.first == :lit && endNode.first == :lit 
     if symbol == :dot2
      @stackElements << [:lit, beginValue..endValue ]
     else 
       @stackElements << [:lit, beginValue...endValue]
     end
    else
      
#     @stackElements << [iVisited.exclusive ? :dot3 : :dot2, beginNode, @stackElements.pop ]
     
    if symbol == :dot2
     @stackElements << [:dot2, beginNode, endNode ]
     else 
      @stackElements << [:dot3, beginNode, endNode]
     end
    end
    nil
  end
  
  def visitEnsureNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.ensure_node)
    ensureNode = @stackElements.pop
    visit(iVisited.body_node)
    @stackElements << [:ensure,  @stackElements.pop, filter_nested_arrays(ensureNode)]
    nil
  end
  
  def visitEvStrNode(iVisited)
#     puts "EvStr content: #{iVisited.body}"
    visit(iVisited.body)
    nil
  end
  
  def visitFCallNode(iVisited)
    # puts iVisited.to_s + " " + iVisited.name
    visit(iVisited.args_node)
    args =  @stackElements.pop
    if args == nil 
      @stackElements << [:fcall, iVisited.name.to_sym] 
    else 
      @stackElements << [:fcall, iVisited.name.to_sym, args] 
    end
    nil
  end
  
  def visitFalseNode(iVisited)
    # puts iVisited.to_s
    @stackElements << :false
    nil
  end
  
  def visitFixnumNode(iVisited)
    @stackElements << [:lit, iVisited.value]
    
    # puts iVisited.to_s + " " + iVisited.value.to_s
    nil
  end
  
  def visitFlipNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.begin_node)
    begin_node = @stackElements.pop
    visit(iVisited.end_node)
    end_node = @stackElements.pop
    # TODO: make this more elegant without an if and without the duplication of code
    if iVisited.exclusive?
     @stackElements << [:flip3, begin_node, end_node]
    else
     @stackElements << [:flip2, begin_node, end_node]
    end
    nil
  end
  
  def visitFloatNode(iVisited)
    # puts iVisited.to_s + " " + iVisited.value.to_s
    @stackElements << [:lit, iVisited.value]
    nil
  end
  
  def visitForNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.var_node)
    var = @stackElements.pop
    visit(iVisited.iter_node)
    iter = @stackElements.pop
    visit(iVisited.body_node)
    body = filter_nested_arrays(@stackElements.pop)
    @stackElements << [:for, iter,var,body]
    nil
  end
  
  def visitGlobalAsgnNode(iVisited)
    # puts iVisited.to_s + " " + iVisited.name
    visit(iVisited.value_node)
    @stackElements << [:gasgn, iVisited.name.to_sym, @stackElements.pop]
    nil
  end
  
  def visitGlobalVarNode(iVisited)
    # puts "GlobalVar " + iVisited.to_s + " " + iVisited.name
    @stackElements << [:gvar, iVisited.name.to_sym ]
    nil
  end
  
  def visitHashNode(iVisited)
    # puts iVisited.to_s    
    result = [:hash]
    visit(iVisited.list_node)
    temp = @stackElements.pop
    if temp.first == :array
      temp.shift
      temp.each{|curr| result << curr}
    end
    @stackElements << result
    nil
  end
  
  def visitInstAsgnNode(iVisited)
#    visitGlobalAsgnNode(iVisited)
    visit(iVisited.value_node)
    @stackElements << [:iasgn, iVisited.name.to_sym, @stackElements.pop]
    nil
  end
  
  def visitInstVarNode(iVisited)
#    visitGlobalVarNode(iVisited)
#    @stackElements << [:ivar, iVisited.name.to_sym]
    @stackElements << [iVisited.name.to_sym]
    nil
  end
  
  def visitIfNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.condition)
    cond = filter_nested_arrays(@stackElements.pop)
    if cond.is_a? Symbol
     temp = [:if, [cond]]
    else
     temp = [:if, cond]
    end
    visit(iVisited.then_body)
    temp << filter_nested_arrays(@stackElements.pop)
    if iVisited.else_body != nil
      visit(iVisited.else_body)
      temp << filter_nested_arrays(@stackElements.pop)
    else
      temp << nil
    end
    @stackElements << temp
    nil
  end
  
  def visitIterNode(iVisited)
    # puts iVisited.to_s
    #p "ITER"
    visit(iVisited.var_node)
    var = @stackElements.pop
    visit(iVisited.body_node)
    body = filter_nested_arrays(@stackElements.pop)
    visit(iVisited.iter_node)
    cond = filter_nested_arrays(@stackElements.pop)
    if body != nil
     @stackElements << [:iter, cond , var, body]
    else
     @stackElements << [:iter, cond , var]
    end
    nil
  end
  
  def visitLocalAsgnNode(iVisited)
    # puts iVisited.to_s + " Local var name " + iVisited.name + " is var index " + iVisited.count.to_s
    visit(iVisited.value_node)
    temp = @stackElements.pop
    if temp == nil
      @stackElements << [:lasgn, iVisited.name.to_sym]
    else
        @stackElements << [:lasgn, iVisited.name.to_sym, filter_nested_arrays(temp)]
    end

    nil
  end
  
  def visitLocalVarNode(iVisited)

           @stackElements << [:lvar, iVisited.name.to_sym]
#     @stackElements << iVisited.name.to_sym
     nil
  end
  
  def visitMultipleAsgnNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.head_node)
    head = @stackElements.pop
#    visit(iVisited.args_node)
     visit(iVisited.value_node)
    # TODO: find out how to do this!
    # TODO: handle all elements!
#    @stackElements << [:masgn, head, @stackElements.pop]
    @stackElements << [:masgn, head,  @stackElements.pop]
    nil
  end
  
  def visitMatch2Node(iVisited)
    # puts iVisited.to_s
    visit(iVisited.receiver_node)
    recv = @stackElements.pop
    visit(iVisited.value_node)
    val = @stackElements.pop
    @stackElements << [:match2, recv, val ]
    nil
  end
  
  def visitMatch3Node(iVisited)
    visitMatch2Node(iVisited)
    @stackElements.first.shift 
    @stackElements.first.insert(0,:match3)
    nil
  end
  
  def visitMatchNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.regexp_node)
    @stackElements << [:match, @stackElements.pop]
    nil
  end
  
  def visitModuleNode(iVisited)
    # puts iVisited.to_s
    # TODO: this isn't called!
    visit(iVisited.body_node)
    body = @stackElements.pop
    #puts "#{@stackElements.inspect}"
    @stackElements << [:module, iVisited.CPath.name.to_sym, [:scope, filter_nested_arrays(body)]]


    nil
  end
  
  def visitNewlineNode(iVisited)
#     puts iVisited.to_s
 #    puts iVisited.next_node.to_s
    visit(iVisited.next_node)
    @stackElements << [@stackElements.pop] # unless @stackElements.size < 1

    nil
  end
  
  def visitNextNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.value_node)
    temp =  @stackElements.pop
    if temp == nil 
    @stackElements << [:next]
    else 
    @stackElements << [:next, temp]
    end
    nil
  end
  
  def visitNilNode(iVisited)
    # puts iVisited.to_s
    # TODO: check this
    @stackElements << :nil
    nil
  end
  
  def visitNotNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.condition_node)
    @stackElements << [:not, filter_nested_arrays([@stackElements.pop])]
    nil
  end
  
  def visitNthRefNode(iVisited)
    # puts iVisited.to_s + " match number " + iVisited.match_number.to_s
    @stackElements << [:nth_ref, iVisited.match_number]
    nil
  end
  
  def visitOpElementAsgnNode(iVisited)
    # puts iVisited.to_s + " " + iVisited.operator_name
    visit(iVisited.receiver_node)
    recv = @stackElements.pop
    visit(iVisited.args_node)
    args = @stackElements.pop
    visit(iVisited.value_node)
    val = @stackElements.pop
    @stackElements << [:op_asgn1, recv, args, iVisited.operator_name.to_sym,val]
    nil
  end
   
  
  def visitOpAsgnNode(iVisited)
     visit(iVisited.receiver_node)
     recv = @stackElements.pop
     visit(iVisited.value_node)
     val = @stackElements.pop
     
     op_symbol = :op_asgn2
     operator_name = iVisited.operator_name.to_sym 

#    @stackElements << [op_symbol, recv, iVisited.variable_name,val]
     @stackElements << [op_symbol, recv, iVisited.variable_name_asgn.to_sym, operator_name, val]
    nil
  end
  

  def handleOpAsgnNode(op_symbol, iVisited)
    visit(iVisited.first_node)
    recv = @stackElements.pop
    visit(iVisited.second_node)
    val = @stackElements.pop
    @stackElements << [op_symbol, recv, val]
  end

  def visitOpAsgnAndNode(iVisited)
    handleOpAsgnNode(:op_asgn_and, iVisited)
    nil
  end
  
  def visitOpAsgnOrNode(iVisited)
    handleOpAsgnNode(:op_asgn_or, iVisited)
    nil
  end
  
  def visitOptNNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.body_node)
  end
  
  def visitOrNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.first_node)
    first = @stackElements.pop
    visit(iVisited.second_node)
    @stackElements << [:or, first, @stackElements.pop]
    nil
  end
  
  def visitPostExeNode(iVisited)
    # puts iVisited.to_s
    @stackElements << [:postexe]
    nil
  end
  
  def visitRedoNode(iVisited)
    # puts iVisited.to_s
    @stackElements << [:redo]
    nil
  end
  
  def visitRegexpNode(iVisited)
    
      @stackElements << [:lit, Regexp.new(iVisited.value)]
    nil
  end
  
  def visitRescueBodyNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.exception_nodes)
    excNodes = @stackElements.pop
    visit(iVisited.body_node)
    body = @stackElements.pop
    visit(iVisited.opt_rescue_node)
    rescue_node = @stackElements.pop
    if body == nil && rescue_node == nil
      @stackElements << [:resbody, excNodes]
    else  if rescue_node == nil
      @stackElements << [:resbody, excNodes, filter_nested_arrays(body)]
     else
     @stackElements << [:resbody, excNodes, body, filter_nested_arrays(rescue_node)]
     end
    end
    nil
  end
  
  def visitRescueNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.body_node)
    body = @stackElements.pop
    visit(iVisited.rescue_node)
    rescueNode = @stackElements.pop
    visit(iVisited.else_node)
    else_node = @stackElements.pop
    if else_node == nil
     @stackElements << [:rescue, filter_nested_arrays(body), rescueNode]
    else 
     @stackElements << [:rescue, filter_nested_arrays(body), rescueNode, filter_nested_arrays(else_node)]
    end
    nil
  end
  
  #TODO: Refactor @stackElements.push/pops into methods which also automatically filter_nested_arrays!!
  
  
  def visitRetryNode(iVisited)
    # puts iVisited.to_s
    @stackElements << [:retry]
    nil
  end
  
  def visitReturnNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.value_node)
    temp = @stackElements.pop
    
    @stackElements << [:return, temp]
    nil
  end
  
  
  def visitSClassNode(iVisited)
    # puts iVisited.to_s  
  
    visit(iVisited.receiver_node)
    recv = @stackElements.pop
    visit(iVisited.body_node)
    body = filter_nested_arrays(@stackElements.pop)

    @stackElements << [:sclass, recv, [:scope, body] ]
    nil
  end
  
    
  def visitSelfNode(iVisited)
    # puts iVisited.to_s
    @stackElements << [:self]
    nil
  end
  
  def visitSplatNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.value)
    @stackElements << [:splat, @stackElements.pop]
    nil
  end
  
  def visitStrNode(iVisited)
    # puts iVisited.to_s + "\"" + iVisited.value + "\""
    @stackElements << [:str, iVisited.value]
    nil
  end
  
  def visitSuperNode(iVisited)
    # puts iVisited.to_s
       # p "FOO"
    visit(iVisited.args_node)
    args = @stackElements.pop
    if args != nil
     if args.size > 0
       @stackElements << [:super, args]
     else 
      @stackElements << [:zsuper]     
     end
    else
    # TODO: remove redundancy and check when zsuper happens
     @stackElements << [:zsuper]     
    end

    nil
  end
  
  def visitSValueNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.value)
    @stackElements << [:svalue, @stackElements.pop]
    nil
  end
  
  def visitSymbolNode(iVisited)
    # puts iVisited.to_s + " " + iVisited.name
    @stackElements << [:lit, iVisited.name.to_sym]
    nil
  end
  
  def visitToAryNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.value)
    @stackElements << [:to_ary, @stackElements.pop]
    nil
  end
  
  def visitTrueNode(iVisited)
    # puts iVisited.to_s
    @stackElements << :true
    nil
  end
  
  def visitUndefNode(iVisited)
    # puts iVisited.to_s + " " + iVisited.name
    @stackElements << [:undef, [:lit, iVisited.name.to_sym]]
   # p "#{@stackElements}"
    nil
  end
  
  def visitUntilNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.condition_node)
    cond = [@stackElements.pop]
    visit(iVisited.body_node)
    body = filter_nested_arrays(@stackElements.pop)
    # TODO: ask about the last element "true"
    @stackElements << [:until, cond, body, true]
    nil
  end
  
  def visitVAliasNode(iVisited)
     
    # puts iVisited.to_s + " aliasing " + iVisited.old_name + " as " + iVisited.new_name
    @stackElements << [:valias, iVisited.new_name.to_sym, iVisited.old_name.to_sym]
    nil
  end
  
    def visitRootNode(iVisited)
     visit(iVisited.body_node)
    end
  
  def visitVCallNode(iVisited)
    # puts iVisited.to_s + " " + iVisited.method_name
#    visit(iVisited.args_node)
    @stackElements << [:vcall, iVisited.name.to_sym]
    nil
  end
  
  def visitWhenNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.expression_nodes)
    expr = @stackElements.pop
    visit(iVisited.body_node)
    body = @stackElements.pop
    visit(iVisited.next_case)
    next_case =  @stackElements.pop unless @stackElements.size < 1
    @stackElements << [:when, expr, body,next_case]
    nil
  end
  
  def visitWhileNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.condition_node)
    condNode = filter_nested_arrays(@stackElements.pop)
    if condNode == :false || condNode == :true
      condNode = [condNode]
    end
    visit(iVisited.body_node)
    body = filter_nested_arrays(@stackElements.pop)
    @stackElements << [:while, condNode, body, iVisited.evaluateAtStart]
    nil
  end
  
  def visitXStrNode(iVisited)
    # puts iVisited.to_s + " \"" + iVisited.value + "\""
    @stackElements << [:xstr, iVisited.value]
    nil
  end
  
  def visitYieldNode(iVisited)
    # puts iVisited.to_s
    visit(iVisited.args_node)
    temp = @stackElements.pop
    if temp == nil
     @stackElements << [:yield]
    else 
     @stackElements << [:yield, temp]
    end    
    nil
  end
  
  def visitZArrayNode(iVisited)
    # puts iVisited.to_s
    @stackElements << [:zarray]
    nil
  end
  
  def visitZSuperNode(iVisited)
    # puts iVisited.to_s
    @stackElements << [:zsuper]
    nil
  end

   
end #end class


end #module
