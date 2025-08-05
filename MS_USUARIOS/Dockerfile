# Imagen base oficial de Python
FROM python:3.11-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos necesarios
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ ./app

# Expone el puerto 5000
EXPOSE 5000

# Comando para correr Flask
CMD ["python", "app/main.py"]