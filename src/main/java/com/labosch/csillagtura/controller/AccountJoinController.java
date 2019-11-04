package com.labosch.csillagtura.controller;

import com.labosch.csillagtura.config.auth.user.CustomOauth2User;
import com.labosch.csillagtura.entity.AccountJoinInitiation;
import com.labosch.csillagtura.entity.User;
import com.labosch.csillagtura.entity.UserEmailAddress;
import com.labosch.csillagtura.exceptions.DisplayAsUserAlertException;
import com.labosch.csillagtura.exceptions.NotImplementedException;
import com.labosch.csillagtura.repo.AccountJoinInitiationRepository;
import com.labosch.csillagtura.repo.UserEmailAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class AccountJoinController {
    private static final Logger logger = LoggerFactory.getLogger(AccountJoinController.class);
    @Autowired
    UserEmailAddressRepository userEmailAddressRepository;
    @Autowired
    AccountJoinInitiationRepository accountJoinInitiationRepository;

    @GetMapping("/account/joinOther")
    String getJoinOther(Model model, @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        addExisting_JoinInitiation_ToModel(model, authenticationPrincipal.getUserEntity());
        addWaitingForApproval_JoinInitiations_ToModel(model, authenticationPrincipal.getUserEntity());
        //((CustomOauth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).
        return "account/joinOther";
    }

    @PostMapping("/account/joinOther")
    String postJoinInitiate(Model model, @RequestParam("action") String action, @RequestParam(name = "targetEmailAddress", required = false) String targetEmailAddress, @RequestParam(name = "initiationId", required = false) Integer initiationId, @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        switch (action) {
            case "initiateJoin":
                handleInitiateJoin(model, targetEmailAddress, authenticationPrincipal.getUserEntity());
                break;
            case "judgeInitiation":
                handleJudgeInitiation(model, initiationId, authenticationPrincipal.getUserEntity());
                break;
            default:
                logger.warn("No action found for post request on account/joinOther.");
                return "redirect:/account/joinOther";
        }


        addExisting_JoinInitiation_ToModel(model, authenticationPrincipal.getUserEntity());
        addWaitingForApproval_JoinInitiations_ToModel(model, authenticationPrincipal.getUserEntity());
        return "account/joinOther";
    }

    private void handleInitiateJoin(Model model, String targetEmailAddress, User currentUser) {
        try {
            if (targetEmailAddress == null || targetEmailAddress.isBlank())
                throw new DisplayAsUserAlertException("Your given e-mail address is blank!");

            Optional<UserEmailAddress> targetedUserEmailAddress = userEmailAddressRepository.findByEmail(targetEmailAddress);
            if (targetedUserEmailAddress.isEmpty())
                throw new DisplayAsUserAlertException("E-mail address is not found!");

            User targetedUser = targetedUserEmailAddress.get().getUser();
            if (targetedUser == null)
                throw new DisplayAsUserAlertException("Account is not found!");

            if (targetedUser.getId().equals(currentUser.getId()))
                throw new DisplayAsUserAlertException("The given e-mail address belongs to this account already.");

            Optional<AccountJoinInitiation> alreadyExistingInitiationByCurrentAccount = accountJoinInitiationRepository.findByInitiatorUser(currentUser);

            if (alreadyExistingInitiationByCurrentAccount.isPresent())
                throw new DisplayAsUserAlertException("You can have only one pending initiation by user. Approve the current one from the other account or cancel it from this one!");

            AccountJoinInitiation accountJoinInitiation = new AccountJoinInitiation();
            accountJoinInitiation.setInitiatorUser(currentUser);
            accountJoinInitiation.setApproverUser(targetedUser);
            accountJoinInitiationRepository.save(accountJoinInitiation);

            currentUser.setInitiatedAccountJoinInitiation(accountJoinInitiation);

            model.addAttribute("initiationStatus", "success");
        } catch (DisplayAsUserAlertException e) {
            model.addAttribute("initiationStatus", "error");
            model.addAttribute("statusMessage", e.getMessage());
        }
    }
    private void handleJudgeInitiation(Model model, Integer initiationId, User currentUser) {
        //TODO: Checking if this initiation's approver is really the currentUser

        throw new NotImplementedException();
    }
    private void addWaitingForApproval_JoinInitiations_ToModel(Model model, User currentUser) {
        List<AccountJoinInitiation> waitingForApprovalInitiations = accountJoinInitiationRepository.findByApproverUser(currentUser);
        model.addAttribute("waitingForApprovalInitiations", waitingForApprovalInitiations);
    }

    private void addExisting_JoinInitiation_ToModel(Model model, User currentUser) {
        Optional<AccountJoinInitiation> alreadyExistingInitiationByCurrentAccount = accountJoinInitiationRepository.findByInitiatorUser(currentUser);

        if (alreadyExistingInitiationByCurrentAccount.isPresent()) {
            model.addAttribute("existingInitiation", alreadyExistingInitiationByCurrentAccount.get());
            model.addAttribute("submitEmailAddress", false);
        } else
            model.addAttribute("submitEmailAddress", true);
    }
}
