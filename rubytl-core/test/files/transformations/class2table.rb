
top_rule 'klass2table' do 
  from  ClassM::Class
  to    TableM::Table
  
  mapping do |klass, table|
    table.name = klass.name
    # Without phases, there are self-referencing problems    
    table.cols = klass.attrs.select { |a| a.type.kind_of?(ClassM::PrimitiveType) } +
                 klass.attrs.select { |a| a.type.kind_of?(ClassM::Class) }
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
    table = klass2table(attr.type) 
    #puts table.name + ' - ' + table.pkeys.map { |c| c.name }.join(',')
    set.values = table.pkeys.map do |col| 
      TableM::Column.new(:name => attr.name + '_' + table.name + "_" + col.name,
                         :type => col.type)
                         #:owner => klass2table(attr.owner))
    end
    table.fkeys = TableM::FKey.new(:cols => set)
  end
end
