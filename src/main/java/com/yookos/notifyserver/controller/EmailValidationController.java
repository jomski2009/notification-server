package com.yookos.notifyserver.controller;

import com.yookos.notifyserver.core.domain.*;
import com.yookos.notifyserver.services.EmailValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by jome on 2014/05/23.
 */

@RestController
public class EmailValidationController {
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    EmailValidationService emailValidationService;

    @RequestMapping(value = "email/adddomains", method = RequestMethod.POST)
    public HttpEntity addBannedDomains(@RequestBody List<String> domains) {
        emailValidationService.addBannedDomains(domains);
        return new ResponseEntity("Service completed", HttpStatus.CREATED);
    }

    @RequestMapping(value = "email/cleandomains", method = RequestMethod.GET)
    public HttpEntity cleanBannedDomains() {
        emailValidationService.processBadEmailAccounts();
        return new ResponseEntity("Service completed", HttpStatus.OK);
    }

    @RequestMapping(value = "email/verifyemail", method = RequestMethod.POST)
    public HttpEntity verifyEmailOwner(@RequestBody UserRegistrationObject uro) {
        EmailMessageResult messageResult = emailValidationService.sendVerificationEmail(uro);
        //Ideally we should be able to send a status of the delivery of the email or provide a location header to check f
        //for the status of the delivery.

        if(messageResult.getCode() == 2100 || messageResult.getCode() == 2200 || messageResult.getCode() ==2300 || messageResult.getCode() == 4000){
            return new ResponseEntity(messageResult, HttpStatus.EXPECTATION_FAILED);
        }

        return new ResponseEntity(messageResult, HttpStatus.CREATED);
    }

    @RequestMapping(value = "registration/validationtoken/resend", method = RequestMethod.POST)
    public HttpEntity resendToken(@RequestBody String  email) {
        EmailMessageResult messageResult = emailValidationService.sendVerificationEmail(email);
        //Ideally we should be able to send a status of the delivery of the email or provide a location header to check f
        //for the status of the delivery.

        if (messageResult.isSuccess()){
            return new ResponseEntity(messageResult, HttpStatus.CREATED);
        }else{
            return new ResponseEntity(messageResult, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "registration/validatetoken", method=RequestMethod.POST)
    public HttpEntity validateToken(@RequestBody EmailVerificationEntity verificationToken){
        EmailValidationResult result = emailValidationService.validateRegistrationToken(verificationToken);

        if(result.isValid()){
            return new ResponseEntity(result, HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity(result, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "registration/checkusername/{username}", method=RequestMethod.GET)
    public HttpEntity checkUserNameExists(@PathVariable String username){
        UserExistsResult result = emailValidationService.checkUserExists(username);

        if(result.getCode() == 4001){
            return new ResponseEntity(result, HttpStatus.EXPECTATION_FAILED);
        }

        return new ResponseEntity(result, HttpStatus.OK);

    }

    @RequestMapping(value = "registration/checkuseremail", method=RequestMethod.POST)
    public HttpEntity checkUserEmailExists(@RequestBody String email){
        log.info(email);
        UserExistsResult result = emailValidationService.checkUserEmailExists(email);

        if(result.getCode() == 4001){
            return new ResponseEntity(result, HttpStatus.EXPECTATION_FAILED);
        }

        return new ResponseEntity(result, HttpStatus.OK);

    }

}
