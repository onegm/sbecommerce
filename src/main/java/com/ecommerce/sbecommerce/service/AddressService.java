package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.exceptions.APIException;
import com.ecommerce.sbecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecommerce.model.Address;
import com.ecommerce.sbecommerce.model.User;
import com.ecommerce.sbecommerce.payload.AddressDTO;
import com.ecommerce.sbecommerce.payload.AddressResponse;
import com.ecommerce.sbecommerce.repository.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AddressService {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserService userService;

    ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
            .withIgnorePaths("id", "users")
            .withMatcher("buildingNumber", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("street", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("city", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("country", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase());

    public boolean exists(AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);
        return addressRepository.exists(Example.of(address, exampleMatcher));
    }

    public AddressDTO create(AddressDTO addressDTO, User user) {
        if(exists(addressDTO))
            throw new APIException("This address already exists.");
        Address address = modelMapper.map(addressDTO, Address.class);
        address.addUser(user);
        return modelMapper.map(addressRepository.save(address), AddressDTO.class);
    }

    public AddressResponse get(Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending) {
        Sort sortByAndOrder = sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Address> addressPage = addressRepository.findAll(pageDetails);
        List<Address> addresses = addressPage.getContent();
        if(addresses.isEmpty())
            throw new APIException("No addresses have been created.");

        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        AddressResponse addressResponse = new AddressResponse(addressDTOS);
        addressResponse.setPageNumber(addressPage.getNumber());
        addressResponse.setPageSize(addressPage.getSize());
        addressResponse.setTotalElements(addressPage.getTotalElements());
        addressResponse.setTotalPages(addressPage.getTotalPages());
        addressResponse.setLastPage(addressPage.isLast());
        return addressResponse;
    }

    public AddressDTO getById(Long addressId) {
        return modelMapper.map(getEntityById(addressId), AddressDTO.class);
    }

    public Address getEntityById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", addressId));
    }

    public List<AddressDTO> getByUserId(User user) {
        Set<Address> addresses = user.getAddresses();
        if(addresses.isEmpty())
            throw new APIException("No addresses found for this user.");
        return addresses.stream().map((address) -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    public AddressDTO update(AddressDTO addressDTO) {
        if(addressDTO.getId() == null)
            throw new APIException("Address ID (id) must be provided");
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        Address originalAddress = addressRepository.findById(addressDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", addressDTO.getId()));
        modelMapper.map(addressDTO, originalAddress);
        Address updatedAddress = addressRepository.save(originalAddress);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    public AddressDTO delete(Long addressId) {
        Address toDelete = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", addressId));
        toDelete.removeUsers();
        addressRepository.deleteById(addressId);
        return modelMapper.map(toDelete, AddressDTO.class);
    }
}
