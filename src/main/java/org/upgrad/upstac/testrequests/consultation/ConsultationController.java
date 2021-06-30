package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);
    @Autowired
    TestRequestFlowService testRequestFlowService;
    @Autowired
    private TestRequestUpdateService testRequestUpdateService;
    @Autowired
    private TestRequestQueryService testRequestQueryService;
    @Autowired
    private UserLoggedInService userLoggedInService;


    //This method fetches all the test requests which are open for doctor's consultation
    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations() {
        //Here we use the findBy method of TestRequestQueryService's object and return all the test request's which
        //status set to completed
        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);

    }

    //This method returns the list of test requests assigned to current doctor
    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor() {
        //Here we first get the current logged in user from the object of UserLoggedInService using the method getLoggedInUser
        User doctor = userLoggedInService.getLoggedInUser();

        //Here we return the list of test requests which are assigned to logged in doctor using the
        // findBy method of TestRequestQueryService's object
        return testRequestQueryService.findByDoctor(doctor);

    }


    // This method assigns a particular test request id to the current logged in doctor
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {
        try {
            //Here we first get the current logged in user from the object of UserLoggedInService using the method getLoggedInUser
            User doctor = userLoggedInService.getLoggedInUser();

            //Here we assign a test request to the logged in doctor using the assignForConsultation
            // method of TestRequestUpdateService's object and id of the test request.
            return testRequestUpdateService.assignForConsultation(id, doctor);

        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


    //This method updates the specified test request id with the doctor's comments
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {
        try {
            //Here we first get the current logged in user from the object of UserLoggedInService using the method getLoggedInUser
            User doctor = userLoggedInService.getLoggedInUser();

            //Here we assign a test request to the logged in doctor using the updateConsultation
            // method of TestRequestUpdateService's object and id of the test request.
            return testRequestUpdateService.updateConsultation(id, testResult, doctor);

        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


}
