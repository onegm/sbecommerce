package com.ecommerce.sbecommerce.payload;

import lombok.Data;

import java.util.List;

@Data
public class AddressResponse {
    private List<AddressDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean isLastPage;

    public AddressResponse(List<AddressDTO> content) {
        this.content = content;
    }
}
