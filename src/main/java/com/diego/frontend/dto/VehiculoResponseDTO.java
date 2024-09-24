package com.diego.frontend.dto;

import lombok.Data;

@Data
public class VehiculoResponseDTO {
    private String marca;
    private String modelo;
    private int nroAsientos;
    private double precio;
    private String color;
}
