# python -m pip install suds
from suds.client import Client
c = Client('http://localhost:8000/?wsdl')

#print(c.service.addition(10, 5))
#print(c.service.substraction(10, 5))
print(c.service.addStudent("Matei", "Rares",4,"1410A"))
print(c.service.addStudent("Manga", "Cartof",7,"1410A"))

print(c.service.findStudent("Andrei"))

print(c.service.getCatalog(5))