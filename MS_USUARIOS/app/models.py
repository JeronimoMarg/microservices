from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt

print("Definiendo Usuarios...")

db = SQLAlchemy()
bcrypt = Bcrypt()

class Usuario(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password = db.Column(db.String(128), nullable=False)

    def check_password(self, password_plana):
        return bcrypt.check_password_hash(self.password, password_plana)
    
    @classmethod
    def create(cls, email, password_plana):
        hasheado = bcrypt.generate_password_hash(password_plana).decode('utf-8')
        return cls(email=email, password=hasheado)