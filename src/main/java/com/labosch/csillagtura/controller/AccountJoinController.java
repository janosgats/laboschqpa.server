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
    String postJoinInitiate(Model model, @RequestParam("action") String action, @RequestParam(name = "subAction", required = false) String subAction, @RequestParam(name = "targetEmailAddress", required = false) String targetEmailAddress, @RequestParam(name = "initiationId", required = false) Long initiationId, @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        boolean redirectToBaseJoinPage;
        switch (action) {
            case "initiateJoin":
                redirectToBaseJoinPage = handleInitiateJoin(model, targetEmailAddress, authenticationPrincipal.getUserEntity());
                break;
            case "cancelInitiatedJoin":
                redirectToBaseJoinPage = handleCancelInitiatedJoin(model, authenticationPrincipal.getUserEntity());
                break;
            case "judgeInitiation":
                redirectToBaseJoinPage = handleJudgeInitiation(model, subAction, initiationId, authenticationPrincipal.getUserEntity());
                break;
            default:
                logger.info("No action found for post request on account/joinOther.");
                redirectToBaseJoinPage = true;
        }

        if (redirectToBaseJoinPage) {
            logger.info("Redirecting to base account join page.");
            return "redirect:/account/joinOther";
        }

        addExisting_JoinInitiation_ToModel(model, authenticationPrincipal.getUserEntity());
        addWaitingForApproval_JoinInitiations_ToModel(model, authenticationPrincipal.getUserEntity());
        return "account/joinOther";
    }

    private boolean handleCancelInitiatedJoin(Model model, User currentUser) {
        try {
            Optional<AccountJoinInitiation> accountJoinInitiationOptional = accountJoinInitiationRepository.findByInitiatorUser(currentUser);
            if (accountJoinInitiationOptional.isEmpty())
                throw new DisplayAsUserAlertException("There aren't any join requests initiated bey you!");

            AccountJoinInitiation accountJoinInitiation = accountJoinInitiationOptional.get();

            accountJoinInitiationRepository.delete(accountJoinInitiation);

        } catch (DisplayAsUserAlertException e) {
            model.addAttribute("initiationStatus", "error");
            model.addAttribute("statusMessage", e.getMessage());
        }

        return false;
    }

    private boolean handleInitiateJoin(Model model, String targetEmailAddress, User currentUser) {
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

        return false;
    }

    private boolean handleJudgeInitiation(Model model, String subAction, Long initiationId, User currentUser) {
        try {
            if (subAction == null || initiationId == null)
                return true;

            Optional<AccountJoinInitiation> accountJoinInitiationOptional = accountJoinInitiationRepository.findById(initiationId);
            if (accountJoinInitiationOptional.isEmpty())
                throw new DisplayAsUserAlertException("Join request is not found.");

            AccountJoinInitiation accountJoinInitiation = accountJoinInitiationOptional.get();
            if (!accountJoinInitiation.getApproverUser().equalsById(currentUser))
                throw new DisplayAsUserAlertException("You cannot decide on this join request. You are not the approver.");

            User initiatorUser = accountJoinInitiation.getInitiatorUser();
            User approverUser = accountJoinInitiation.getApproverUser();

            switch (subAction) {
                case "joinIntoApprover":
                    logger.info("Joining join request into approver.");
                    joinUserAccounts(initiatorUser, approverUser);
                    break;
                case "joinIntoInitiator":
                    logger.info("Joining join request into initiator.");
                    joinUserAccounts(approverUser, initiatorUser);
                    break;
                case "reject":
                    logger.info("Deleting (rejecting) join request.");
                    accountJoinInitiationRepository.delete(accountJoinInitiation);
                    break;
                default:
                    logger.info("No subAction found for judgeInitiation post request on account/joinOther.");
                    return true;
            }
        } catch (DisplayAsUserAlertException e) {
            model.addAttribute("initiationStatus", "error");
            model.addAttribute("statusMessage", e.getMessage());
        }

        return false;
    }

    private void joinUserAccounts(User fromUser, User toUser) {
        if (fromUser.equalsById(toUser))
            throw new RuntimeException("Error joining User Accounts: fromUser and toUser are the same account!");

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
