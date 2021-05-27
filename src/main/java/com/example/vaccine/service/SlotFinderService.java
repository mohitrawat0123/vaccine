package com.example.vaccine.service;

import com.example.vaccine.dao.RegistrationRepositoryService;
import com.example.vaccine.dto.*;
import com.example.vaccine.enums.District;
import com.example.vaccine.enums.Vaccine;
import com.example.vaccine.util.MailSenderUtil;
import com.example.vaccine.util.TwilioUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class SlotFinderService implements ISlotFinderService {

    @Value("${cowin.base.url}")
    private String cowinBaseUrl;

    @Value("${schedule.time.period}")
    private Integer interval;

    @Value("${cowin.min.slot}")
    private Integer minSlot;

    @Autowired
    private MailSenderUtil mailSenderUtil;

    @Autowired
    private TwilioUtil twilioUtil;

    @Autowired
    private RegistrationRepositoryService registrationRepositoryService;

    private static RestTemplate restTemplate;

    private static ScheduledExecutorService executorService;

    private static final ReentrantLock lock = new ReentrantLock();

    private static final Map<String, Timer> districtTaskRegsitration = new ConcurrentHashMap<>();

    private static int itr = 0;

    private static final List<String> userAgents = Arrays.asList(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 6.0.1; Moto G (4)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; U; en-us; KFAPWI Build/JDQ39) AppleWebKit/535.19 (KHTML, like Gecko) Silk/3.13 Safari/535.19 Silk-Accelerated=true",
            "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 10 Build/MOB31T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0");

    public SlotFinderService(@Autowired RestTemplate restTemplate) {
        SlotFinderService.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        executorService = Executors.newScheduledThreadPool(10);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
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

        Date now = new Date();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(now);

        System.out.println(now + ": Scanning " + district + "...");

        try {

            URIBuilder uriBuilder = new URIBuilder(cowinBaseUrl + "/calendarByDistrict");
            uriBuilder.addParameter("district_id", String.valueOf(district.getId()));
            uriBuilder.addParameter("date", date);

            VaccineResponseDTO responseDTO = sendCurlRequest(uriBuilder.build());


            responseDTO.getCenters().forEach(center -> {
                for(SessionDTO sessionDTO : center.getSessions()){
                    if(sessionDTO.getAvailableDose1() >= minSlot && sessionDTO.getAvailable() > 0
                            && sessionDTO.getMinAge().equals(18)){

                        String message = "Site: " + center.getName()
                                +"\nAddress: "+ center.getAddress()+ ", "+ center.getPincode()
                                +"\nVaccine: "+sessionDTO.getVaccine()
                                +"\nDose1: "+sessionDTO.getAvailableDose1() +"\tDose2: "+sessionDTO.getAvailableDose2()
                                +"\n"+sessionDTO.getAvailable()+" slot(s) available on "
                                + new SimpleDateFormat("EEE, d MMM").format(sessionDTO.getDate());

                        String subject = "Vaccination Slot Available | " + center.getName() +" | "+center.getDistrictName();

                        Set<String> emailIds = new HashSet<>(registrationRepositoryService.getEmailForDistrict(district.getId(), Vaccine.ANY));

                        Set<String> phoneNumber = new HashSet<>(registrationRepositoryService.getPhoneNumberForDistrict(district.getId(), Vaccine.ANY));

                        if(sessionDTO.getVaccine().equals(Vaccine.COVAXIN)) {
                            emailIds.addAll(registrationRepositoryService.getEmailForDistrict(district.getId(), Vaccine.COVAXIN));
                            phoneNumber.addAll(registrationRepositoryService.getPhoneNumberForDistrict(district.getId(), Vaccine.COVAXIN));
                        }

                        if(!CollectionUtils.isEmpty(emailIds)) {
                            MailRequestDTO mailRequestDTO = MailRequestDTO.builder()
                                    .from("noreply@cowidbot.in")
                                    .to(new ArrayList<>(emailIds))
                                    .subject(subject)
                                    .body(message)
                                    .build();
                            mailSenderUtil.sendMail(mailRequestDTO);
                        }

                        if (!CollectionUtils.isEmpty(phoneNumber)) {
                            WhatsappRequestDTO whatsappRequestDTO = WhatsappRequestDTO.builder().body("*"+subject+"*\n"+message).build();
                            phoneNumber.forEach(number -> {
                                whatsappRequestDTO.setTo(number);
                                try {
                                    twilioUtil.sendMessage(whatsappRequestDTO);
                                } catch (Exception ex) {
                                    System.out.println("Exception occurred while sending whatsapp message to: "+ number+" ex: "+ex);
                                }

                            });
                        }

                        System.out.println(new Date()+": " + center.getName() + " | " + center.getDistrictName()
                                + " | " + sessionDTO.getVaccine() + "-" + sessionDTO.getAvailable()
                                +  " | " + new SimpleDateFormat("EEE, d MMM").format(sessionDTO.getDate()));

                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Exception occurred while checking slots - "+e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void registerForNotification(RegistrationRequestDTO requestDTO) {
        District district = requestDTO.getDistrict();
        synchronized (district) {
            registrationRepositoryService.registerEmail(requestDTO);
            if(!districtTaskRegsitration.containsKey(district.getName())) {
                System.out.println("Registering for "+ district);
                Timer timerTask = new Timer();
                timerTask.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        checkSlot(district);
                    }
                }, 0,interval * 1000);
                districtTaskRegsitration.put(district.getName(), timerTask);
            }
        }
    }

    @Override
    public void deRegisterForNotification(RegistrationRequestDTO requestDTO) {
        District district = requestDTO.getDistrict();
        synchronized (district) {
            if(registrationRepositoryService.deRegisterEmail(requestDTO)
                    && districtTaskRegsitration.containsKey(district.getName())){
                districtTaskRegsitration.get(district.getName()).cancel();
                districtTaskRegsitration.remove(district.getName());
            }
        }
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
//                            center.setSessions(center.getSessions().stream()
//                                    .filter(session -> !CollectionUtils.isEmpty(session.getSlots()))
//                                    .collect(Collectors.toList()));
                            center.getSessions().removeIf(sessionDTO -> CollectionUtils.isEmpty(sessionDTO.getSlots()));
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
//        httpHeaders.add("if-none-match", "W/\"aad6-usSnZGE366m59dXydt9RRTkoRKU\"");


        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders), String.class);

        String responseString = responseEntity.getBody();
        return new ObjectMapper().readValue(responseString, VaccineResponseDTO.class);

    }
}
