require 'includedFile'

class TestKlasse

  def initialize
    @field = ATargetClass.new
  end
  
  attr_writer :var
  attr_reader :var
  
  def five_plus arg
   5 + arg
  end
 
  def calculate
    @var = five_plus 1
  end
  
  def use_var
    @var += "."
  end
  
end

t = TestKlasse.new
t.calculate
puts t.var