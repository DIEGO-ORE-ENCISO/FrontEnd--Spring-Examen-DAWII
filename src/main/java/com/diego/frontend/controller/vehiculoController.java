package com.diego.frontend.controller;

import com.diego.frontend.config.RestTemplateConfig;
import com.diego.frontend.dto.VehiculoRequestDTO;
import com.diego.frontend.dto.VehiculoResponseDTO;
import com.diego.frontend.models.VehiculoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class vehiculoController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/vehiculo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vehiculo", new VehiculoForm(""));
        return "buscar";
    }

    @PostMapping("/buscar")
    public String buscarVehiculo(@ModelAttribute("vehiculo") VehiculoForm vehiculoForm,
                                 BindingResult bindingResult, Model model) {

        String url = "http://localhost:8080/api/vehiculos/buscar";

        // Validaciones del formulario
        if (!isValidPlaca(vehiculoForm.placa(), model)) {
            return "buscar";
        }

        // Hacer la solicitud a la API
        VehiculoRequestDTO vehiculoRequest = new VehiculoRequestDTO(vehiculoForm.placa());
        ResponseEntity<VehiculoResponseDTO> response; // Cambiar a ResponseEntity

        try {
            response = restTemplate.postForEntity(url, vehiculoRequest, VehiculoResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                VehiculoResponseDTO resultado = response.getBody();
                model.addAttribute("resultado", resultado);
                return "detalleVehiculo";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage",  e.getMessage().toString());
            return "detalleVehiculo";
        }

        model.addAttribute("errorMessage", "Se produjo un error inesperado.");
        return "buscar";
    }

    // Método de validación de la placa
    private boolean isValidPlaca(String placa, Model model) {
        if (placa == null || placa.trim().isEmpty()) {
            model.addAttribute("errorMessage", "El campo es requerido.");
            return false;
        } else if (placa.length() > 8) {
            model.addAttribute("errorMessage", "El campo debe tener una longitud de 8 caracteres.");
            return false;
        } else if (!placa.matches("^[A-Z]{3}-\\d{3}$")) {
            model.addAttribute("errorMessage", "La placa debe tener el formato correcto (Ej: ASR-125).");
            return false;
        }
        return true;
    }
}
