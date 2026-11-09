package com.alertavecinal.admin_service.controller;

import com.alertavecinal.admin_service.dto.GenericResponseDto;
import com.alertavecinal.admin_service.entity.Parametrica;
import com.alertavecinal.admin_service.service.ParametricaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/parametricas")
@RequiredArgsConstructor
public class ParametricaController {

    private final ParametricaService parametricaService;

    @GetMapping
    public ResponseEntity<GenericResponseDto<List<Parametrica>>> listarParametricas(@RequestParam(required = false) String tipo) {
        List<Parametrica> res = (tipo != null && !tipo.isEmpty()) ? parametricaService.obtenerPorTipo(tipo) : parametricaService.obtenerTodas();
        return ResponseEntity.ok(new GenericResponseDto<>(res));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Parametrica>> obtenerPorId(@PathVariable Long id) {
        return parametricaService.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(new GenericResponseDto<>(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<Parametrica>> crearParametrica(@RequestBody Parametrica parametrica) {
        Parametrica nuevaParametrica = parametricaService.crearParametrica(parametrica);
        return new ResponseEntity<>(new GenericResponseDto<>(nuevaParametrica), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<Parametrica>> actualizarParametrica(@PathVariable Long id, @RequestBody Parametrica parametrica) {
        return parametricaService.actualizarParametrica(id, parametrica)
                .map(p -> ResponseEntity.ok(new GenericResponseDto<>(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarParametrica(@PathVariable Long id) {
        if (parametricaService.eliminarParametrica(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
