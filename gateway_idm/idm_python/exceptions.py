class CustomError(Exception):
    def __init__(self, message):
        self.message = message
        super().__init__(message)
    #raise CustomError("Userul nu are rolul necesar pentru a accesa resursa!")
    #except CustomError as e:
    #    print(e.message)