package com.example.vaccine.dao;

import com.example.vaccine.dto.RegistrationRequestDTO;
import com.example.vaccine.dto.UserDTO;
import com.example.vaccine.enums.District;
import com.example.vaccine.enums.Vaccine;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RegistrationRepositoryService implements IRegistrationRepositoryService {

    private static final Map<Integer, Set<UserDTO>> districtRegistration = new ConcurrentHashMap<>();

    private static final Map<Integer, Set<UserDTO>> pincodeRegistration = new ConcurrentHashMap<>();

    @Override
    public List<UserDTO> getUserForDistrict(Integer id, Vaccine vaccine) {
        List<UserDTO> userDTOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(districtRegistration.get(id))) {
            userDTOList = districtRegistration.get(id).stream()
                    .filter(userDTO -> vaccine.equals(userDTO.getVaccine()))
                    .collect(Collectors.toList());
        }
        return userDTOList;
    }


    @Override
    public List<String> getEmailForDistrict(Integer id, Vaccine vaccine) {
        List<String> emailList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(districtRegistration.get(id))) {
            emailList = districtRegistration.get(id).stream()
                    .filter(userDTO -> vaccine.equals(userDTO.getVaccine()))
                    .map(UserDTO::getEmail)
                    .collect(Collectors.toList());
        }
        return emailList;
    }

    @Override
    public List<String> getPhoneNumberForDistrict(Integer id, Vaccine vaccine) {
        List<String> phoneList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(districtRegistration.get(id))) {
            phoneList = districtRegistration.get(id).stream()
                    .filter(userDTO -> vaccine.equals(userDTO.getVaccine()) && StringUtils.isNotEmpty(userDTO.getPhone()))
                    .map(UserDTO::getPhone)
                    .collect(Collectors.toList());
        }
        return phoneList;
    }

    @Override
    public List<String> getEmailForPincode(Integer id, Vaccine vaccine) {
        List<String> emailList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(pincodeRegistration.get(id))) {
            emailList = pincodeRegistration.get(id).stream()
                    .filter(userDTO -> vaccine.equals(userDTO.getVaccine()))
                    .map(UserDTO::getEmail)
                    .collect(Collectors.toList());
        }
        return emailList;
    }

    @Override
    public void registerEmail(RegistrationRequestDTO requestDTO) {
        Integer districtId = requestDTO.getDistrict().getId();

        if(Objects.nonNull(districtId)) {
            Set<UserDTO> registeredUsers = districtRegistration.getOrDefault(districtId, new HashSet<>());
            registeredUsers.addAll(requestDTO.getUsers());
            districtRegistration.put(districtId, registeredUsers);
        }

        printRegistrations();
    }

    private static void printRegistrations () {
        if(MapUtils.isNotEmpty(districtRegistration)) {
            System.out.println("DistrictRegistrations");
            for(Map.Entry<Integer, Set<UserDTO>> entry : districtRegistration.entrySet()) {
                District district = District.fromId(entry.getKey());
                System.out.println("\t"+district +":");
                entry.getValue().forEach(value -> System.out.println("\t\t"+value));
            }
        }
    }

    @Override
    public boolean deRegisterEmail(RegistrationRequestDTO requestDTO) {
        boolean emptyRegistration = false;
        Integer districtId = requestDTO.getDistrict().getId();
        Set<UserDTO> registeredUsers;
        if(Objects.nonNull(districtId) && districtRegistration.containsKey(districtId)) {
            registeredUsers = districtRegistration.get(districtId);
            registeredUsers.removeAll(requestDTO.getUsers());
            if(!CollectionUtils.isEmpty(registeredUsers)){
                districtRegistration.put(districtId, registeredUsers);
            } else {
                emptyRegistration = true;
                districtRegistration.remove(districtId);
            }
        }

        printRegistrations();

        return emptyRegistration;
    }
}
