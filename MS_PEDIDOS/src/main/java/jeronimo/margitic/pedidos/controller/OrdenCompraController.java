package jeronimo.margitic.pedidos.controller;

import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jeronimo.margitic.pedidos.dto.OrdenCompraDTO;
import jeronimo.margitic.pedidos.model.OrdenCompra;
import jeronimo.margitic.pedidos.service.OrdenCompraService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.ipc.http.HttpSender.Response;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class OrdenCompraController {
    
    @Autowired
    private OrdenCompraService ordenCompraService;

    @GetMapping("/todos")
    public ResponseEntity<List<OrdenCompra>> obtenerTodos(){
        List<OrdenCompra> todas = ordenCompraService.obtenerTodas();
        return ResponseEntity.ok(todas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompra> obtenerPedidoPorId(@PathVariable int id){
        Optional<OrdenCompra> buscado = ordenCompraService.obtenerPedidoPorId(id);
        return ResponseEntity.of(buscado);
    }

    @GetMapping("/{numeroPedido}")
    public ResponseEntity<OrdenCompra> obtenerPedidoPorNumero(@PathVariable int numeroPedido){
        Optional<OrdenCompra> buscado = ordenCompraService.obtenerPedidoPorNumero(numeroPedido);
        return ResponseEntity.of(buscado);
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<OrdenCompra>> obtenerPedidosActivosCliente(@PathVariable int id){
        List<OrdenCompra> ordenes = ordenCompraService.obtenerPedidosActivosPorCliente(id);
        return ResponseEntity.ok(ordenes);
    }

    @PostMapping(path = "/crear", consumes = "application/json")
    public ResponseEntity<OrdenCompra> crearOrden (@RequestBody OrdenCompraDTO ordenNueva) {
        try{
            OrdenCompra ordenCreada = ordenCompraService.crearPedido(ordenNueva);
            return ResponseEntity.status(201).body(ordenCreada);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrdenCompra> actualizarOrden (@PathVariable int id, @RequestBody OrdenCompra ordenAModificar) {
        Optional<OrdenCompra> ordenBuscada = ordenCompraService.obtenerPedidoPorId(id);
        if(ordenBuscada.isPresent()){
            try{
                OrdenCompra ordenModificada = ordenCompraService.actualizarPedido(ordenAModificar);
                return ResponseEntity.ok(ordenModificada);
            }catch (Exception e){
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path="/aceptarPedido/{id}")
    public ResponseEntity<OrdenCompra> aceptarPedido (@PathVariable int id){
        Optional<OrdenCompra> ordenBuscada = ordenCompraService.obtenerPedidoPorId(id);
        if(ordenBuscada.isPresent()){
            try{
                OrdenCompra ordenActualizada = ordenCompraService.aceptarPedidoYActualizar(ordenBuscada.get());
                return ResponseEntity.ok(ordenActualizada);
            }catch(Exception e){
                System.err.println(e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path="/prepararPedido/{id}")
    public ResponseEntity<OrdenCompra> prepararPedido(@PathVariable int id){
        Optional<OrdenCompra> ordenBuscada = ordenCompraService.obtenerPedidoPorId(id);
        if(ordenBuscada.isPresent()){
            try{
                OrdenCompra ordenActualizada = ordenCompraService.prepararPedidoYActualizar(ordenBuscada.get());
                return ResponseEntity.ok(ordenActualizada);
            }catch(Exception e){
                System.err.println(e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    
    

}
