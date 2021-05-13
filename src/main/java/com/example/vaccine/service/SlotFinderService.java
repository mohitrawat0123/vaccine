package com.example.vaccine.service;

import com.example.vaccine.dto.CenterResponseDTO;
import com.example.vaccine.dto.SessionDTO;
import com.example.vaccine.dto.VaccineResponseDTO;
import com.example.vaccine.enums.District;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class SlotFinderService implements ISlotFinderService {

    @Autowired
    private static RestTemplate restTemplate;

    @Value("${cowin.base.url}")
    private String cowinBaseUrl;

    private static final ReentrantLock lock = new ReentrantLock();

    private static int itr = 0;

    private static final List<String> userAgents = Arrays.asList(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 6.0.1; Moto G (4)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0");

    public SlotFinderService(@Autowired RestTemplate restTemplate) {
        SlotFinderService.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 30000)
    public void checkSlotEastDelhi() {
        this.checkSlot(District.EAST_DELHI);
    }

    @Scheduled(fixedRate = 30000)
    public void checkSlotShahdara() {
        this.checkSlot(District.SHAHDARA);

    }

    private static int getItr(){
        int next;
        lock.lock();
        itr = (itr + 1) % userAgents.size();
        next = itr;
        lock.unlock();
        return next;
    }

    private void checkSlot(District district) {
//        System.out.println("Checking slot for: "+district);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        try {

            URIBuilder uriBuilder = new URIBuilder(cowinBaseUrl + "/calendarByDistrict");
            uriBuilder.addParameter("district_id", String.valueOf(district.getId()));
            uriBuilder.addParameter("date", date);

            VaccineResponseDTO responseDTO = sendCurlRequest(uriBuilder.build());

            responseDTO.getCenters().forEach(center -> {
                for(SessionDTO sessionDTO : center.getSessions()){
                    if(sessionDTO.getAvailable() > 0 && sessionDTO.getMinAge().equals(18)){
                        if(!CollectionUtils.isEmpty(sessionDTO.getSlots())){
                            System.out.println(new Date() + ": Found "+sessionDTO.getAvailable()+" slot(S) in ["+center.getName() +", "+ center.getPincode()+"]");
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void registerForNotification(District district, Integer age, String email) {
        //TODO: submit a task, if doesn't exist
    }

    @SneakyThrows
    @Override
    public VaccineResponseDTO getAvailableSlots(District district, Integer age) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        URIBuilder uriBuilder = new URIBuilder(cowinBaseUrl + "/calendarByDistrict");
        uriBuilder.addParameter("district_id", String.valueOf(district.getId()));
        uriBuilder.addParameter("date", date);

        VaccineResponseDTO vaccineResponseDTO = sendCurlRequest(uriBuilder.build());

        if(Objects.isNull(age))
            return VaccineResponseDTO.builder().centers(filterForAvailability(vaccineResponseDTO.getCenters())).build();

        return VaccineResponseDTO.builder().centers(filterForAvailabilityAndAge(vaccineResponseDTO.getCenters(), age)).build();

    }

    @SneakyThrows
    @Override
    public VaccineResponseDTO getAvailableSlots(Integer pincode, Integer age) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        URIBuilder uriBuilder = new URIBuilder(cowinBaseUrl + "/calendarByPin");
        uriBuilder.addParameter("pincode", String.valueOf(pincode));
        uriBuilder.addParameter("date", date);

        VaccineResponseDTO vaccineResponseDTO = sendCurlRequest(uriBuilder.build());

        if(Objects.isNull(age))
            return VaccineResponseDTO.builder().centers(filterForAvailability(vaccineResponseDTO.getCenters())).build();

        return VaccineResponseDTO.builder().centers(filterForAvailabilityAndAge(vaccineResponseDTO.getCenters(), age)).build();

    }

    private static List<CenterResponseDTO> filterForAvailability(List<CenterResponseDTO> centerResponseDTOS) {

        return centerResponseDTOS.stream()
                .filter(center -> {
                            center.setSessions(center.getSessions().stream()
                                    .filter(session -> !CollectionUtils.isEmpty(session.getSlots()))
                                    .collect(Collectors.toList()));
                            return !center.getSessions().isEmpty();
                        }
                ).collect(Collectors.toList()).stream()
                .filter(center ->
                        center.getSessions().stream().anyMatch(session -> session.getAvailable() > 0))
                .collect(Collectors.toList());

    }

    private static List<CenterResponseDTO> filterForAvailabilityAndAge(List<CenterResponseDTO> centerResponseDTOS, Integer age) {
        return centerResponseDTOS.stream()
                .filter(center -> {
                            center.setSessions(center.getSessions().stream()
                                    .filter(session -> !CollectionUtils.isEmpty(session.getSlots()))
                                    .collect(Collectors.toList()));
                            return !center.getSessions().isEmpty();
                        }
                ).collect(Collectors.toList()).stream()
                .filter(center ->
                        center.getSessions().stream().anyMatch(session -> session.getAvailable() > 0 && session.getMinAge().equals(age)))
                .collect(Collectors.toList());
    }

    private VaccineResponseDTO sendCurlRequest(URI uri) throws JsonProcessingException {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setOrigin("https://www.cowin.gov.in");
        httpHeaders.add("user-agent", userAgents.get(getItr()));
        httpHeaders.add("host", "cdn-api.co-vin.in");
        httpHeaders.add("referrer", "https://www.cowin.gov.in/");

        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders), String.class);

        String responseString = responseEntity.getBody();
        return new ObjectMapper().readValue(responseString, VaccineResponseDTO.class);

    }
}
