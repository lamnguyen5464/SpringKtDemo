package demo.test.service.utilities;

import demo.test.model.entity.ProfileEntity;
import demo.test.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ProfileRepository profileRepository;

    public boolean sendSimpleEmail(String target, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(target);
        msg.setSubject(subject);
        msg.setText(content);
        try {
            javaMailSender.send(msg);
            return true;
        } catch (MailSendException e) {
            return false;
        }
    }

    public boolean existingEmail(String email) {
        ProfileEntity user = profileRepository.getById(email);
        if (user == null) {
            return false;
        }
        return true;
    }
}
