from flask import Blueprint, request, jsonify
from app.models import db, Usuario
from flask_jwt_extended import create_access_token
from flask_jwt_extended import jwt_required, get_jwt_identity

routes = Blueprint('auth', __name__)

print("Configurando las rutas...")

@routes.route("/auth/register", methods=["POST"])
def register():
    data = request.get_json()
    if not data or "email" not in data or "password" not in data:
        return jsonify({"msg": "Datos invalidos"}), 400

    if Usuario.query.filter_by(email=data["email"]).first():
        return jsonify({"msg": "El usuario ya existe"}), 409

    user = Usuario.create(data["email"], data["password"])
    db.session.add(user)
    db.session.commit()
    return jsonify({"msg": "Usuario creado"}), 201

@routes.route("/auth/login", methods=["POST"])
def login():
    data = request.get_json()
    user = Usuario.query.filter_by(email=data.get("email")).first()
    if not user or not user.check_password(data.get("password")):
        return jsonify({"msg": "Email o contraseña incorrectos"}), 401

    access_token = create_access_token(identity=user.email)
    return jsonify(access_token=access_token), 200

@routes.route("/auth/profile", methods=["GET"])
@jwt_required()
def profile():
    #Para consultar esto pasar el token recibido en login en el header 'Authorization' con valor 'Bearer {token}'
    identity = get_jwt_identity()
    return jsonify(email=identity), 200