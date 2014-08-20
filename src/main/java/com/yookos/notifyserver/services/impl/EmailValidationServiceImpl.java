package com.yookos.notifyserver.services.impl;

import com.google.gson.Gson;
import com.mongodb.*;
import com.yookos.notifyserver.core.domain.*;
import com.yookos.notifyserver.services.EmailValidationService;
import com.yookos.notifyserver.utils.EmailValidationUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jome on 2014/05/23.
 */
@Service
public class EmailValidationServiceImpl implements EmailValidationService {
    private static final long SEVENTY_TWO_HOURS = 259200000L;
    private static final String DISPOSABLE_EMAIL_URL = "http://chat.yookos.com/api/ext/validation/validate_email.php?";
    Logger log = LoggerFactory.getLogger(this.getClass());
    RestTemplate template = new RestTemplate();

    @Autowired
    Environment env;

    @Autowired
    MongoClient client;

    @Autowired
    Datastore datastore;

    @Override
    public void addBannedDomains(List<String> bannedDomains) {
        DBCollection domains = client.getDB("yookosreco").getCollection("banneddomains");

        for (String domain : bannedDomains) {
            try {
                WriteResult result = domains.save(new BasicDBObject("domainname", domain));
                log.info("Save result: {}", result.toString());
            } catch (MongoException me) {
                log.error("Caught exception: {}", me.getMessage());
            }
        }
    }

    @Override
    public void processBadEmailAccounts() {
        DBCollection domains = client.getDB("yookosreco").getCollection("banneddomains");
        DBCollection relationships = client.getDB("yookosreco").getCollection("relationships");
        int domaincount = 0;

        DBCursor domainCursor = domains.find();

        for (Object o : domainCursor) {
            DBObject domain = (DBObject) o;
            String domainName = (String) domain.get("domainname");

            //We need to delete any disposable domains from the relationships collections
            WriteResult writeResult = relationships.remove(new BasicDBObject("email", new BasicDBObject("$regex", domainName)));
            log.info("Clean up results for {}", domainName);
            log.info(writeResult.toString());
        }
    }

    @Override
    public EmailMessageResult sendVerificationEmail(String email) {
        EmailMessageResult result = new EmailMessageResult();

        //We need to checkl if this email exists...
        Query<RegistrationEmailVerification> evq = datastore.createQuery(RegistrationEmailVerification.class);
        RegistrationEmailVerification found = evq.field("lowercaseemail")
                .equal(email.toLowerCase()).get();

        if (found != null) {
            sendValidationEmail(found);
            datastore.save(found);
            result.setSuccess(true);
            result.setMessage("Email has been sent.");
            return result;

        } else {
            result.setSuccess(true);
            result.setMessage("Email has been sent.");
        }
        result.setSuccess(false);
        result.setMessage("No record satisfies your request");
        result.setCode(4001);

        return result;
    }

    @Override
    public EmailMessageResult sendVerificationEmail(UserRegistrationObject uro) {
        EmailMessageResult result = new EmailMessageResult();
        //Has the this uro been processed before?
        //If yes, overwrite the generated token
        RegistrationEmailVerification rev;


        //Before we even start, lets check that the email is valid..
        if (!checkEmailIsValid(uro.getEmails().get(0).getValue())) {
            result.setMessage("The email address is either non-existent or the email domain is not supported");
            result.setSuccess(false);
            result.setCode(4000);
            return result;
        } else {

            Query<RegistrationEmailVerification> evq = datastore.createQuery(RegistrationEmailVerification.class);
            RegistrationEmailVerification found = evq.field("lowercaseemail")
                    .equal(uro.getEmails().get(0).getValue().toLowerCase()).get();

            RegistrationEmailVerification foundUsername = evq.field("reg.jive.username")
                    .equal(uro.getEmails().get(0).getValue()).get();

            log.info("Received: {}", uro);

            if (found != null || foundUsername != null) {

                result.setMessage("The username|email address has already been used");
                if (found != null && foundUsername == null) {
                    result.setCode(2200);
                }

                if (found == null && foundUsername != null) {
                    result.setCode(2100);
                }

                if (found != null && foundUsername != null) {
                    result.setCode(2300);
                }
                result.setSuccess(false);
                return result;

            } else {
                rev = new RegistrationEmailVerification(uro);
                log.info("New Rev: {}", rev);
            }

            boolean success = sendValidationEmail(rev);

            if (success) {
                datastore.save(rev);
                result.setSuccess(true);
                result.setMessage("Email has been sent.");
                return result;

            } else {
                result.setSuccess(false);
                result.setMessage("There might be an issue with the supplied email address. Please try again or use a different email address");
                result.setCode(4000);
                return result;
            }
        }
    }

    private boolean checkEmailIsValid(String emailToCheck) {
        String senderEmail = "support@yookos.com";
        Map<String, Object> params = new HashMap<>();
        params.put("email", emailToCheck);
        params.put("sender", senderEmail);
        log.info("Email to check: {}", emailToCheck);
        String request = DISPOSABLE_EMAIL_URL + "email=" + emailToCheck + "&sender=" + senderEmail;
        log.info("Request url: {}", request);

        try {
            ResponseEntity<String> entity = template.exchange(request, HttpMethod.GET, null, String.class, params);
            //ResponseEntity<String> entity = template.getForEntity(DISPOSABLE_EMAIL_URL, String.class, params);
            log.info("Status code: {}",entity.getStatusCode().value());
            if (entity.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error("Validation error: {}", e.getMessage());
        }
        return false;


    }


    @Override
    public EmailValidationResult validateRegistrationToken(EmailVerificationEntity vt) {
        EmailValidationResult result = new EmailValidationResult();
        Query<RegistrationEmailVerification> evq = datastore.createQuery(RegistrationEmailVerification.class);
        UserRegistrationObject registration = new UserRegistrationObject();

        RegistrationEmailVerification found = evq.field("lowercaseemail")
                .equal(vt.getEmail().toLowerCase()).field("token")
                .equal(vt.getToken()).get();

        if (found != null) {
            //Has this email address been used and validated?
            //We assume that the check for an existing email address has already been done...

            //Lets be sure the token is still valid.
            long tokenCreationdate = found.getCreationdate();
            long currentTime = new Date().getTime();
            if (currentTime - tokenCreationdate > SEVENTY_TWO_HOURS) {
                result.setValid(false);
                result.setMessage("Registration token has expired");
                result.setCode(4009);
                return result;
            }
            //The email address has been successfully validated. Set validated to true...
            found.setValidated(true);
            datastore.save(found);

            result.setValid(true);
            result.setMessage("Email was successfully validated");
            result.setRegistration(found.getReg());
            return result;

        } else {
            result.setValid(false);
            result.setMessage("The token provided is invalid");
            result.setCode(4001);
            return result;
        }
    }

    @Override
    public UserExistsResult checkUserExists(String username) {
        UserExistsResult result = new UserExistsResult();
        Query<RegistrationEmailVerification> evq = datastore.createQuery(RegistrationEmailVerification.class);
        RegistrationEmailVerification foundUser = evq.field("reg.jive.username").equal(username).get();
        if (foundUser != null) {
            result.setCode(2100);
            result.setMessage("The username already exists");
        } else {
            result.setCode(4001);
            result.setMessage("No record satisfies your request");
        }


        return result;
    }

    @Override
    public UserExistsResult checkUserEmailExists(String email) {
        UserExistsResult result = new UserExistsResult();
        Query<RegistrationEmailVerification> evq = datastore.createQuery(RegistrationEmailVerification.class);
        RegistrationEmailVerification foundUser = evq.field("lowercaseemail").equal(email.toLowerCase()).get();
        if (foundUser != null) {
            result.setCode(2200);
            result.setMessage("The email address already exists");
        } else {
            result.setCode(4001);
            result.setMessage("No record satisfies your request");
        }


        return result;
    }


    private boolean sendValidationEmail(RegistrationEmailVerification rev) {
        EmailObject emailObject = new EmailObject();
        int minimum = 100000;
        int maximum = 999999;

        //TODO:Write a service to send email via email direct....
        String emaildirecturl = env.getProperty("emaildirect.send.url", "https://rest.emaildirect.com/v1/RelaySends/5");
        String auth = env.getProperty("emaildirect.authorization.key", "");

        //Generate a random number that will be saved...
        int verificationToken = minimum + (int) (Math.random() * maximum);
        if (verificationToken > 999999) {
            int diff = verificationToken - 999999;
            verificationToken = verificationToken - (diff + (int) (Math.random() * 10000));
        }

        rev.setToken(verificationToken);
        rev.setCreationdate(new Date().getTime());

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(EmailValidationUtils.getEmailHeader()).append(EmailValidationUtils.getBodyStart());
        messageBuilder.append(verificationToken);
        messageBuilder.append(EmailValidationUtils.getBodyFooter());


        String message = messageBuilder.toString();
        emailObject.setToEmail(rev.getReg().getEmails().get(0).getValue());
        emailObject.setToName(rev.getReg().getName().getFullName());
        emailObject.setSubject("Verify your email address");
        emailObject.setText(message);
        emailObject.setHTML(message);

        Gson gson = new Gson();
        String requestObject = gson.toJson(emailObject);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", auth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity(requestObject, headers);
        //log.info("Auth: {}", auth);
        //log.info("Request: {}", requestObject);

        try {
            ResponseEntity<String> responseEntity = template.exchange(emaildirecturl, HttpMethod.POST, requestEntity, String.class);
            log.info("Response: {}", responseEntity.getBody());
            return true;
        } catch (HttpClientErrorException e) {
            log.info(e.toString());
            return false;
        }
    }
}
