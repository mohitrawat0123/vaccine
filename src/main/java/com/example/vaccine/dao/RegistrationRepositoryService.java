package com.example.vaccine.dao;

import com.example.vaccine.dto.RegistrationRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RegistrationRepositoryService implements IRegistrationRepositoryService {

    private static final Map<Integer, Set<String>> districtRegistration = new ConcurrentHashMap<>();

    private static final Map<Integer, Set<String>> pincodeRegistration = new ConcurrentHashMap<>();

    @Override
    public List<String> getEmailForDistrict(Integer id) {
        List<String> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(districtRegistration.get(id)))
            list = new ArrayList<>(districtRegistration.get(id));
        return list;
    }

    @Override
    public List<String> getEmailForPincode(Integer id) {
        List<String> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(pincodeRegistration.get(id)))
            list = new ArrayList<>(pincodeRegistration.get(id));
        return list;
    }

    @Override
    public void registerEmail(RegistrationRequestDTO registrationRequestDTO) {
        Integer districtId = registrationRequestDTO.getDistrict().getId();
        if(Objects.nonNull(districtId)) {
            Set<String> emailList = districtRegistration.getOrDefault(districtId, new HashSet<>());
            emailList.addAll(registrationRequestDTO.getEmail());
            districtRegistration.put(districtId, emailList);
        }

        Integer pincode = registrationRequestDTO.getPincode();
        if(Objects.nonNull(pincode)) {
            Set<String> emailList = pincodeRegistration.getOrDefault(pincode, new HashSet<>());
            emailList.addAll(registrationRequestDTO.getEmail());
            districtRegistration.put(pincode, emailList);
        }

        System.out.println("District registrations: "+ districtRegistration);
        System.out.println("Pincode registrations: "+ pincodeRegistration);
    }

    @Override
    public boolean deRegisterEmail(RegistrationRequestDTO requestDTO) {
        boolean emptyRegistration = false;
        Integer districtId = requestDTO.getDistrict().getId();
        Set<String> registeredEmails;
        if(Objects.nonNull(districtId) && districtRegistration.containsKey(districtId)) {
            registeredEmails = districtRegistration.get(districtId);
            registeredEmails.removeAll(requestDTO.getEmail());
            if(!CollectionUtils.isEmpty(registeredEmails)){
                districtRegistration.put(districtId, registeredEmails);
            } else {
                emptyRegistration = true;
                districtRegistration.remove(districtId);
            }
        }

        Integer pincode = requestDTO.getPincode();
        if(Objects.nonNull(pincode) && pincodeRegistration.containsKey(pincode)) {
            registeredEmails = pincodeRegistration.get(districtId);
            registeredEmails.removeAll(requestDTO.getEmail());
            if(!CollectionUtils.isEmpty(registeredEmails)){
                pincodeRegistration.put(districtId, registeredEmails);
            } else {
                pincodeRegistration.remove(districtId);
            }
        }

        System.out.println("District registrations: "+ districtRegistration);
        System.out.println("Pincode registrations: "+ pincodeRegistration);

        return emptyRegistration;
    }
}
