# python -m pip install lxml spyne
from spyne import Application, rpc, ServiceBase, Integer, ComplexModelBase, Unicode
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication

class CalculatorService(ServiceBase):
    @rpc(Integer, Integer, _returns=Integer)
    def addition(self, a, b):
        return a + b
    @rpc(Integer, Integer, _returns=Integer)
    def substraction(self, a, b):
        return a - b
    @rpc(Integer, Integer, _returns=Integer)
    def multiply(self, a, b):
        return a * b
    @rpc(Integer, Integer, _returns=Integer)
    def division(self, a, b):
        return a / b
class Student(ComplexModelBase):
    _type_info = {
        'nume': Unicode,
        'prenume': Unicode,
        'an': Integer,
        'grupa': Unicode
    }
    def __init__(self, nume, prenume, an, grupa):
        self.nume=nume
        self.prenume=prenume
        self.an=an
        self.grupa=grupa

    def toString(self):
        return "{nume = "+self.nume+" prenume= "+self.prenume+" an= " + str(self.an) +" grupa= "+self.grupa+"}"

studenti = []

class CatalogService(ServiceBase):
    @rpc(Unicode, Unicode, Integer, Unicode, _returns=Student)
    def addStudent(self, a, b,c,d):
        student = Student(a, b, c, d)
        studenti.append(Student(a,b,c,d))
        return student

    @rpc(Unicode, _returns=Unicode)
    def findStudent(self, a):

        for student in studenti:
            if student.nume  == a:
                print("ok")
                return student.toString()
        return "Not found"

    @rpc(Integer, _returns=Unicode)
    def getCatalog(self, nr):
        print(len(studenti))
        string=""
        if nr > len(studenti):
            for student in studenti:
                string= string + student.toString()
            return string

        for index in range(0,nr):
            string = string + studenti[index].toString()
        return string


application = Application([CatalogService], 'services.calculator.soap', #aici se pot mai multe servicii : [serv1,serv2]
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)

if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()