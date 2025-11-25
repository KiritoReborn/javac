package com.canteen.canteen_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuItemDto {
    private Long id;
    
    @NotBlank(message = "Item name is required")
    private String itemname;
    
    @Min(value = 0, message = "Price must be positive")
    private double price;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private boolean available;
}
