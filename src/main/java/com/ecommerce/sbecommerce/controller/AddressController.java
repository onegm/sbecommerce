package com.ecommerce.sbecommerce.controller;

import com.ecommerce.sbecommerce.config.AppConstants;
import com.ecommerce.sbecommerce.exceptions.APIException;
import com.ecommerce.sbecommerce.model.Address;
import com.ecommerce.sbecommerce.payload.AddressDTO;
import com.ecommerce.sbecommerce.payload.AddressResponse;
import com.ecommerce.sbecommerce.security.AuthUtil;
import com.ecommerce.sbecommerce.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AddressService addressService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthUtil authUtil;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> create(@RequestBody AddressDTO addressDTO){
        return new ResponseEntity<>(addressService.create(addressDTO, authUtil.loggedInUser()), HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<AddressResponse> get(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortAscending", defaultValue = AppConstants.SORT_ASCENDING) boolean sortAscending){
        return ResponseEntity.ok(addressService.get(pageNumber, pageSize, sortBy, sortAscending));
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> get(@PathVariable Long addressId){
        return ResponseEntity.ok(addressService.getById(addressId));
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getLoggedInUserAddress(){
        return ResponseEntity.ok(addressService.getByUserId(authUtil.loggedInUser()));
    }

    @PutMapping("/address")
    public ResponseEntity<AddressDTO> update(@RequestBody AddressDTO addressDTO){
        return ResponseEntity.ok(addressService.update(addressDTO));
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> delete(@PathVariable Long addressId){
        return ResponseEntity.ok(addressService.delete(addressId));
    }

}
