from suds.client import Client

# Crearea unui client pentru serviciu
c = Client('http://localhost:8000/?wsdl')

# Importă tipul 'Student' din serviciu
from server2 import Student

# Creează un obiect 'Student' și trimite-l către serviciu
student = Student(nume="Matei", prenume="Rares", an=4, grupa="1410A")

response = c.service.addStudent(student)
print(f"Student adaugat: {response.nume} {response.prenume}, An {response.an}, Grupa {response.grupa}")
