from flask import Flask
from app.models import db, bcrypt
from app.auth import routes
from flask_jwt_extended import JWTManager
from flask_cors import CORS
from app.config import Config

print("App Flask arrancando...")

app = Flask(__name__)
app.config.from_object(Config)

CORS(app)
db.init_app(app)
bcrypt.init_app(app)
jwt = JWTManager(app)

app.register_blueprint(routes)

with app.app_context():
    db.create_all()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)