class Employee
  def work! kind
    puts "doing #{kind.to_s}"
  end
end

@e = Employee.new
@e.work! "coding"