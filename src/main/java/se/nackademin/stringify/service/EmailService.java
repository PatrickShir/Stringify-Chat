package se.nackademin.stringify.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

/**
 * A service class to handle transactional emails using SendGrid API.
 */
@Slf4j
@Service
@EnableAsync
public class EmailService {

    private SendGrid sg = new SendGrid(System.getenv("SG_API_KEY"));

    void sendMail(Mail mail) {

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            log.warn(ex.getMessage());
        }
    }

    /**
     * Send an invitational email
     *
     * @param sendTo    Recipient email
     * @param invitedBy Sender profile name
     * @param chatId    The id of an active ChatSession.
     */
    public void sendInvitationEmail(String sendTo, String invitedBy, UUID chatId) {
        Mail mail = new Mail();
        mail.setFrom(new Email("noreply@stringify.com"));
        mail.setTemplateId("d-ee2942f110ba453b9f208384338c0824");

        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("INVITED_BY", invitedBy);
        personalization.addDynamicTemplateData("MEETING_URL", "https://stringify-chat.netlify.app/connect?chat-id=" + chatId);
        personalization.addTo(new Email(sendTo));
        mail.addPersonalization(personalization);
        sendMail(mail);
    }
}
