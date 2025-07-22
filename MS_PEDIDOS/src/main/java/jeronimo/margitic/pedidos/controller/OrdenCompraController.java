package jeronimo.margitic.pedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jeronimo.margitic.pedidos.model.OrdenCompra;
import jeronimo.margitic.pedidos.service.OrdenCompraService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class OrdenCompraController {
    
    @Autowired
    private OrdenCompraService ordenCompraService;

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<OrdenCompra>> obtenerPedidosActivosCliente(@PathVariable int id){
        List<OrdenCompra> ordenes = ordenCompraService.obtenerPedidosActivosPorCliente(id);
        return ResponseEntity.ok(ordenes);
    }
    

}
