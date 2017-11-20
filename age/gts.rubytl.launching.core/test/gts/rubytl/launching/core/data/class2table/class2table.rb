transformation 'class2table'
input  'ClassM' => 'http://gts.inf.um.es/examples/class'
output 'TableM' => 'http://gts.inf.um.es/examples/relational'

top_rule 'klass2table' do 
  from  ClassM::Class
  to    TableM::Table
  
  mapping do |klass, table|
    table.name = klass.name
    table.cols = klass.attrs
  end
end
  
rule 'property2column' do 
  from    ClassM::Attribute
  to      TableM::Column
  filter  do |attr|
    attr.type.kind_of? ClassM::PrimitiveType
  end
  
  mapping do |attr, column|
    column.name = attr.name
    column.type = attr.type.name
    column.owner.pkeys << column if attr.is_primary
  end
end
  
rule 'reference2column' do 
  from    ClassM::Attribute
  to      many(TableM::Column)
  filter  do |attr|
    attr.type.kind_of? ClassM::Class
  end
  
  mapping do |attr, set|      
    set.values = attr.type.attrs.select { |a| a.is_primary }.map do |primary_attr|
      TableM::Column.new(:name => attr.type.name + "_" + primary_attr.name + '_id',
                         :type => primary_attr.type.name)
    end
  end
end
  
top_rule 'tofkey' do
  from  ClassM::Attribute
  to    TableM::FKey
  filter do |attr|
    attr.type.kind_of? ClassM::Class
  end
  mapping do |attr, fkey|
    fkey.cols = reference2column(attr)
    fkey.references = attr.type
    fkey.owner = attr.owner
  end  
end  

