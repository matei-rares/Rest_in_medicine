from spyne import Application, rpc, ServiceBase, Integer, String, ComplexModel
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication

class Student(ComplexModel):
    __namespace__ = 'Student'
    nume = String
    prenume = String
    an = Integer
    grupa = String

class CatalogService(ServiceBase):
    def __init__(self):
        self.studenti = []

    @rpc(Student, _returns=Student)
    def addStudent(self, student):
        self.studenti.append(student)
        return student

application = Application([CatalogService], 'http://example.com/catalog', in_protocol=Soap11(validator='lxml'), out_protocol=Soap11())

if __name__ == '__main__':
    from wsgiref.simple_server import make_server

    print("Listening to http://127.0.0.1:8000")
    print("WSDL is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, WsgiApplication(application))
    server.serve_forever()
