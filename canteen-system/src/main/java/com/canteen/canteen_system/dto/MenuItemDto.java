package com.canteen.canteen_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String itemname;
    private double price;
    private String category;
    private boolean available;
}
