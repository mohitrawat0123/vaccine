package com.example.vaccine.util;


import com.example.vaccine.dto.WhatsappRequestDTO;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwilioUtil {

    public static final String ACCOUNT_SID = "AC890274234fbbb734a085f0824ec1afa2";
    public static final String AUTH_TOKEN = "3ea0e856cb16d19876ba471e6525e273";

    public void sendMessage(WhatsappRequestDTO whatsappRequestDTO) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber("whatsapp:"+whatsappRequestDTO.getTo()),
                new PhoneNumber("whatsapp:+14155238886"),
                whatsappRequestDTO.getBody())
                .create();
    }

}
