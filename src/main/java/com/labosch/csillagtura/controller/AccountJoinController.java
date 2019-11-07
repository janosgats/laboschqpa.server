package com.labosch.csillagtura.controller;

import com.labosch.csillagtura.advice.SameClassTransactionalBoolean;
import com.labosch.csillagtura.advice.SameClassTransactionalString;
import com.labosch.csillagtura.advice.SameClassTransactionalVoid;
import com.labosch.csillagtura.config.auth.user.CustomOauth2User;
import com.labosch.csillagtura.entity.AccountJoinInitiation;
import com.labosch.csillagtura.entity.User;
import com.labosch.csillagtura.entity.UserEmailAddress;
import com.labosch.csillagtura.exceptions.DisplayAsUserAlertException;
import com.labosch.csillagtura.repo.AccountJoinInitiationRepository;
import com.labosch.csillagtura.repo.ExternalAccountDetailRepository;
import com.labosch.csillagtura.repo.UserEmailAddressRepository;
import com.labosch.csillagtura.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AccountJoinController {
    private static final Logger logger = LoggerFactory.getLogger(AccountJoinController.class);
    @Autowired
    UserEmailAddressRepository userEmailAddressRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExternalAccountDetailRepository externalAccountDetailRepository;
    @Autowired
    AccountJoinInitiationRepository accountJoinInitiationRepository;
    @Autowired
    TransactionTemplate transactionTemplate;

    @GetMapping("/account/joinOther")
    @SameClassTransactionalString
    String getJoinOther(Model model, @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        authenticationPrincipal.refreshUserEntityFromDB(userRepository);

        addExisting_JoinInitiation_ToModel(model, authenticationPrincipal.getUserEntity());
        addWaitingForApproval_JoinInitiations_ToModel(model, authenticationPrincipal.getUserEntity());

        return "account/joinOther";
    }

    @PostMapping("/account/joinOther")
    String postJoinInitiate(Model model, @RequestParam("action") String action, @RequestParam(name = "subAction", required = false) String subAction, @RequestParam(name = "targetEmailAddress", required = false) String targetEmailAddress, @RequestParam(name = "initiationId", required = false) Long initiationId, @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Boolean redirectToBaseJoinPage = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                return handleMainSwitch(model, action, subAction, targetEmailAddress, initiationId, authenticationPrincipal);
            }
        });


        if (redirectToBaseJoinPage) {
            logger.info("Redirecting to base account join page.");
            return "redirect:/account/joinOther";
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                fillModelPost(model, authenticationPrincipal);
            }
        });
        return "account/joinOther";
    }

    @SameClassTransactionalBoolean
    public Boolean fillModelPost(Model model, CustomOauth2User authenticationPrincipal) {
        authenticationPrincipal.refreshUserEntityFromDB(userRepository);
        addExisting_JoinInitiation_ToModel(model, authenticationPrincipal.getUserEntity());
        addWaitingForApproval_JoinInitiations_ToModel(model, authenticationPrincipal.getUserEntity());
        return true;
    }

    @SameClassTransactionalBoolean
    public Boolean handleMainSwitch(Model model, String action, String subAction, String targetEmailAddress, Long initiationId, CustomOauth2User authenticationPrincipal) {
        authenticationPrincipal.refreshUserEntityFromDB(userRepository);

        if (authenticationPrincipal.getUserEntity() == null)
            throw new RuntimeException("Your account seems missing.");

        switch (action) {
            case "initiateJoin":
                return handleInitiateJoin(model, targetEmailAddress, authenticationPrincipal.getUserEntity());
            case "cancelInitiatedJoin":
                return handleCancelInitiatedJoin(model, authenticationPrincipal.getUserEntity());
            case "judgeInitiation":
                return handleJudgeInitiation(model, subAction, initiationId, authenticationPrincipal.getUserEntity());
            default:
                logger.info("No action found for post request on account/joinOther.");
                return true;
        }

    }

    private boolean handleCancelInitiatedJoin(Model model, User currentUser) {
        try {
            if (currentUser.getInitiatedAccountJoinInitiation() == null)
                throw new DisplayAsUserAlertException("There aren't any join requests initiated by you!");

            accountJoinInitiationRepository.delete(currentUser.getInitiatedAccountJoinInitiation());
            currentUser.setInitiatedAccountJoinInitiation(null);

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
                throw new DisplayAsUserAlertException("The targeted account is not found!");

            if (!targetedUser.getEnabled())
                throw new DisplayAsUserAlertException("The targeted account is currently disabled!");

            if (targetedUser.getId().equals(currentUser.getId()))
                throw new DisplayAsUserAlertException("The given e-mail address belongs to this account already.");

            AccountJoinInitiation alreadyExistingInitiationByCurrentAccount = currentUser.getInitiatedAccountJoinInitiation();

            if (alreadyExistingInitiationByCurrentAccount != null)
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
                    joinUserAccounts(initiatorUser, approverUser, accountJoinInitiation);
                    break;
                case "joinIntoInitiator":
                    logger.info("Joining join request into initiator.");
                    joinUserAccounts(approverUser, initiatorUser, accountJoinInitiation);
                    break;
                case "reject":
                    logger.info("Deleting (rejecting) join request.");

                    currentUser.getAccountJoinInitiationsToApprove().forEach((init) -> {
                        if (init.getId().equals(initiationId))
                            currentUser.getAccountJoinInitiationsToApprove().remove(init);
                    });
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

    @Transactional
    void joinUserAccounts(User fromUser, User toUser, AccountJoinInitiation accountJoinInitiation) {
        if (fromUser.equalsById(toUser))
            throw new RuntimeException("Error joining User Accounts: fromUser and toUser are the same account!");

        fromUser.setJoinedInto(toUser);
        fromUser.setEnabled(false);

        toUser.getUserEmailAddresses().addAll(fromUser.getUserEmailAddresses());
        fromUser.getUserEmailAddresses().forEach(userEmailAddress -> {
            userEmailAddress.setUser(toUser);
            userEmailAddressRepository.save(userEmailAddress);
        });
        fromUser.getUserEmailAddresses().clear();

        fromUser.getExternalAccountDetails().forEach(externalAccountDetail -> {
            externalAccountDetail.setUser(toUser);
            externalAccountDetailRepository.save(externalAccountDetail);
        });

        accountJoinInitiationRepository.deleteAll(fromUser.getAccountJoinInitiationsToApprove());
        accountJoinInitiationRepository.delete(fromUser.getInitiatedAccountJoinInitiation());

        userRepository.save(fromUser);
        userRepository.save(toUser);

        accountJoinInitiationRepository.delete(accountJoinInitiation);
    }

    private void addWaitingForApproval_JoinInitiations_ToModel(Model model, User currentUser) {
        currentUser.getAccountJoinInitiationsToApprove().size();
        currentUser.getAccountJoinInitiationsToApprove().forEach((init) -> {
            init.getInitiatorUser().getUserEmailAddresses().size();//Triggering lazy fetch
        });
        model.addAttribute("waitingForApprovalInitiations", currentUser.getAccountJoinInitiationsToApprove());
    }

    public void addExisting_JoinInitiation_ToModel(Model model, User currentUser) {
        AccountJoinInitiation alreadyExistingInitiationByCurrentAccount = currentUser.getInitiatedAccountJoinInitiation();


        if (alreadyExistingInitiationByCurrentAccount != null) {
            //Triggering lazy fetch
            if (alreadyExistingInitiationByCurrentAccount.getApproverUser() != null
                    && alreadyExistingInitiationByCurrentAccount.getApproverUser() != null
                    && alreadyExistingInitiationByCurrentAccount.getApproverUser().getUserEmailAddresses() != null) {
            }
            model.addAttribute("existingInitiation", alreadyExistingInitiationByCurrentAccount);
            model.addAttribute("submitEmailAddress", false);
        } else
            model.addAttribute("submitEmailAddress", true);
    }
}
