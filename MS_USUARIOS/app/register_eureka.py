import requests
import socket
import threading
import time

def register_with_eureka():

    print("Registrandose en Eureka...")

    eureka_url = "http://eureka-server:8761/eureka/apps/usuarios-service"
    instance_id = f"{socket.gethostbyname(socket.gethostname())}:usuarios-service:5000"

    payload = f"""
    <instance>
        <instanceId>{instance_id}</instanceId>
        <hostName>{socket.gethostname()}</hostName>
        <app>usuarios-service</app>
        <ipAddr>{socket.gethostbyname(socket.gethostname())}</ipAddr>
        <status>UP</status>
        <port enabled="true">5000</port>
        <dataCenterInfo class="com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo">
            <name>MyOwn</name>
        </dataCenterInfo>
    </instance>
    """

    headers = {"Content-Type": "application/xml"}

    try:
        response = requests.post(eureka_url, data=payload, headers=headers)
        print("Registered in Eureka:", response.status_code)
    except Exception as e:
        print("Failed to register in Eureka:", e)

    # Start heartbeat
    def heartbeat():
        while True:
            try:
                requests.put(f"{eureka_url}/{instance_id}", headers=headers)
                print("Sent heartbeat to Eureka")
            except Exception as e:
                print("Heartbeat error:", e)
            time.sleep(30)

    threading.Thread(target=heartbeat, daemon=True).start()
