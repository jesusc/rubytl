
context TableM::Table do
  inv('must-have-pkeys') do
    self.pkeys.size > 0
  end
end