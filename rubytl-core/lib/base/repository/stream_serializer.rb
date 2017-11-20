require 'stringio'

module RMOF::ECore
  class BaseStreamSerializer
    def initialize(model, adapter = nil)
      @model = model
      @adapter = adapter
    end

    def serialize(io_to_save_result)
      @io = StringIO.new
      @file_path = File.expand_path(io_to_save_result.path) if io_to_save_result.respond_to?(:path)
      @schema_locations = {}
      @namespaces       = {}

# TODO: DO A PASS TO COMPUTE THE SCHEMA LOCATION BEFORE HAND

      #if @model.root_elements.size > 1 || @model.root_elements.size == 0
#        print_tag('xmi:XMI', specialization) {  
          @model.root_elements.each do |object|
            serialize_object(object)
          end 
#        } 
      #else
      #  serialize_object(@model.root_elements.first, nil, specialization)
      #end
 
 
# This has to be deal with at the beginning
#      @namespaces.each_pair do |prefix, uri|
#        @result.root.add_namespace(prefix, uri)
#      end
#      specialize_root_element(@result.root)

      specialization = specialize_root_element
      print_tag(io_to_save_result, 'xmi:XMI', specialization) {  
        io_to_save_result.write(@io.string)
      }

      #@io.each_line do |l|
      #  io_to_save_result.write(l)
      #end
    ensure
      @io.close
    end

protected
    def serialize_object(object, parent_property = nil, additional_attrs = nil)
      element_tag = if parent_property.nil?
        prefix  = object.metaclass.nsPrefix
        name    = object.metaclass.non_qualified_name

        store_namespace(prefix, object)
        
        "#{prefix}:#{name}"
      else
        parent_property.name
      end

      @io << "<#{element_tag}"
      @io << " " + additional_attrs if additional_attrs

      # compute which features are serialized as attributed and which ones
      # as nested elements
      as_inlined       = []
      as_containment   = []
      as_nested_string = []
         
      object.metaclass.all_structural_features.each do |feature|
        if feature.is_attribute?
          if feature.multivalued?
             as_nested_string.push *serialize_multivalued_attribute(object, feature)
          else
             as_inlined << serialize_simple_attribute(object, feature)
          end
        elsif feature.is_reference?
          as_containment << feature if feature.containment
        else
          throw "assertion error"
        end
        #serialize_attribute(object, feature, element)
        #serialize_reference(object, feature, element) if feature.is_reference?
      end

      specific_attrs = specialize_serialization(object, parent_property)
      as_inlined << specific_attrs if specific_attrs

      @io << " #{as_inlined.join(' ')}"
      if as_nested_string.empty? && as_containment.empty?
        @io << "/>#{$/}"
      else
        @io << ">#{$/}"
      
        as_nested_string.each { |s| @io << s + $/ }

        as_containment.each do |reference|
          [*object.get(reference)].each do |value|
            if value != nil
              serialize_object(value, reference)
            end
          end
        end      
        @io << "</#{element_tag}>" + $/
      end
    end
     
    
    def serialize_reference(object, reference, element)
      return "" if reference.derived

      if reference.containment
        [*object.get(reference)].compact.each do |value|
          child_element = serialize_object(value, reference)
          element.add_element(child_element)
        end
        # raise "no"
      elsif reference.eOpposite && reference.eOpposite.containment
        # Skip
        # The reference is already serialized since there already exists
        # a parent/child in the XML
      else
        if reference.multivalued?
          intermodel_references = []
          object.get(reference).each do |value|
            href = id_for_non_containment_reference(value, reference)

            if value.owning_model != @model
              el = element.add_element(reference.name, { 'href' => value.owning_model.uri + href })
              #if self.respond_to? :specialize_serialization
              #  specialize_serialization(el, object, reference)
              #end
            else
              intermodel_references << href
            end
          end
          element.attributes[reference.name] = intermodel_references.join(' ') unless intermodel_references.empty?
        else
          value = object.get(reference)
          if value
            reference_string = id_for_non_containment_reference(value, reference)

            # Duplicate code!!! I don't like this
            # An horrible hacks...
            if value.owning_model != @model
              uri = value.owning_model.uri
              if uri =~ /^file/
                raise "Can't compute relative file paths if no file is given" unless @file_path
                uri = @adapter.compute_relative_file_path(uri, @file_path)
              end
              concrete_type = ''
              # TODO: This is Ecore specific...
              if reference.eType != value.metaclass
                concrete_type = "#{value.metaclass.nsPrefix}:#{value.metaclass.non_qualified_name} "
                store_namespace(value.metaclass.nsPrefix, value)
              end
              reference_string = concrete_type + uri + reference_string
            end
            element.attributes[reference.name] = reference_string
          end
        end
#        str = [*object.get(reference)].compact.map do |value|
#           id_for_non_containment_reference(value, reference)
#        end.join(' ')
#        element.attributes[reference.name] = str unless str.strip.empty?
        # TODO: Check if the element is in a differente resource (i.e. models)
        # than this.
      end
    end

    def serialize_simple_attribute(object, attribute)
      return "" if attribute.derived

      value = object.get(attribute)

      if value != nil
        if attribute.type.is_enumeration?
          gen_xml_attr(attribute.name, value.name)
        elsif attribute.type.is_primitive? && value != attribute.defaultValue
          gen_xml_attr(attribute.name, attribute.type.to_xmi_str(value))
        else
          raise "Attributes should be primitive or enumerations (#{attribute.name} : #{attribute.type.name})"
        end
      end
    end

    def serialize_multivalued_attribute(object, attribute)
      value = object.get(attribute) 

      if attribute.type.is_primitive?
        value.map do |v|
           "<#{attribute.name}>#{v}<#{attribute.name}>"
        end
      else
         raise "Not considered yet multivalued enumerations"
      end
    end

#    def root_element(specialization, &block)
#      if @model.root_elements.size > 1 || @model.root_elements.size == 0
#        print_tag('xmi:XMI', specialization) { yield(nil) } # Yield without the specialization
#      else
#        yield(specialization)
#      end
#    end

    def print_tag(io, name, attributes = "", &block)
      io << "<#{name} #{attributes}>" + $/
      yield
      io << "</#{name}>" + $/
    end
 
    def gen_xml_attr(name, value)
      "#{name}='#{value}'"
    end

    def compute_schema_location(package)
      if @file_path && @adapter
        ns_uri   = package.nsURI
        root_uri = package.root_package.nsURI
        fragment = ns_uri != root_uri ? package.compute_uri_fragment : ''
        if path = @adapter.compute_relative_path(root_uri, @file_path)
          @schema_locations[ns_uri] = path + fragment
        end
      end
    end

    def store_namespace(prefix, object)
      return if @namespaces.key?(prefix)
      @namespaces[prefix] = object.metaclass.nsURI
      compute_schema_location(object.metaclass.ePackage)
    end

  end

  class StreamSerializer < BaseStreamSerializer # For Ecore forme
    def specialize_root_element()
      header = [gen_xml_attr('xmi:version', '2.0'),
                gen_xml_attr('xmlns:xmi', "http://www.omg.org/XMI"),
                gen_xml_attr('xmlns:xsi', "http://www.w3.org/2001/XMLSchema-instance")]
      location = @schema_locations.map { |uri, path| "#{uri} #{path}" }.join(' ')
puts location

      @namespaces.each_pair do |prefix, uri|
        header << gen_xml_attr(prefix, uri)
      end


      header << gen_xml_attr('xsi:schemaLocation', location) if location.size > 0
      header.join(' ')
    end

    def specialize_serialization(object, parent_property = nil)
      if parent_property && parent_property.type != object.metaclass
        prefix = object.metaclass.ePackage.nsPrefix
        store_namespace(prefix, object)
        gen_xml_attr("xsi:type", "#{prefix}:#{object.metaclass.non_qualified_name}")
      end
    end


  end

end

