from flask import Flask
from app.models import db, bcrypt
from app.auth import routes
from flask_jwt_extended import JWTManager
from flask_cors import CORS
from app.config import Config
from app.register_eureka import register_with_eureka

print("App Flask arrancando...")

app = Flask(__name__)
app.config.from_object(Config)

CORS(app)
db.init_app(app)
bcrypt.init_app(app)
jwt = JWTManager(app)

app.register_blueprint(routes)

#Se registra con eureka para poder usarse en el gateway.
register_with_eureka()

with app.app_context():
    db.create_all()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)