OUTPUT_PATH = ARGV.first + "/"

def file_name(klass)
  file_name = OUTPUT_PATH + klass.to_s.downcase
  file_name.gsub!("::", "/")
  file_name << ".rb"
  return file_name
end

def dir_names(file_name)
  last_slash = file_name.rindex("/")
  return nil if last_slash.nil?
  file_name[0...last_slash]
end

def print_method(f, method, method_name, singleton = false)
  f << "  def "
  f << "self." if singleton
  f << method_name.to_s
  if !method.nil? and method.arity != 0
    # TODO We need to handle methods that take blocks!
    f << "("
    if method.arity < 0
      args = []
       (method.arity.abs + 1).times {|i| args << "arg#{i}" }
      args << "*rest"
      f << args.join(", ")    
    else
      args = []
      method.arity.times {|i| args << "arg#{i}" }
      f << args.join(", ")
    end
    f << ")"
  end
  f << "\n  end\n"
end

require 'FileUtils'
@klasses = Module.constants.select {|c| ["Class", "Module"].include?(eval("#{c}.class").to_s) }
@klasses = @klasses.collect {|k| eval("#{k}")}
@klasses = @klasses.uniq.sort_by {|klass| klass.to_s }
@klasses.each do |klass|
  next if klass.to_s[0].chr == "f" # TODO Skip if first char is lowercase
  file_name = file_name(klass)
  dirs = dir_names(file_name)
  FileUtils.mkdir_p(dirs) if !dirs.nil? and !File.exist?(file_name)
  open(file_name, 'w') do |f|
    f << "#{klass.class.to_s.downcase} #{klass}"
    f << " < #{klass.superclass.to_s }" if klass.respond_to?(:superclass) and !klass.superclass.nil?
    f << "\n"
    klass.included_modules.each {|mod| f << "  include #{mod.to_s}\n" unless mod.to_s == "Kernel" && klass.to_s != "Object"}
    f << "\n"
    # FIXME We aren't grabbing some important methods inside Module (like "include")
    klass.methods(false).each do |method_name|
      method = eval("#{klass}").method(method_name) rescue nil    
      print_method(f, method, method_name.to_s, true)
    end
    # TODO Fix it so we can get a hold of the module instance methods properly
    klass.instance_methods(false).each do |method_name|
      begin
        obj = if klass.class.to_s == "Module" then klass else klass.new end
        method = obj.method(method_name.to_s)
      rescue StandardError => e
        puts e
        # TODO If we can't create an instance of a class, generate dynamic subclass where we can, and then
        # grab methods from there
        begin
          # If we're a module, we may need to force the function to be more visible to grab it
          obj.module_eval do
            module_function(method_name.to_s)
          end
          method = obj.method(method_name.to_s)         
        rescue StandardError => e
          puts e
          method = nil
        end
      end
      print_method(f, method, method_name.to_s)
    end
    f << "end\n"
  end
end