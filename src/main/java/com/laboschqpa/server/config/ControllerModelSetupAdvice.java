package com.laboschqpa.server.config;

import com.laboschqpa.server.config.helper.AppConstants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerModelSetupAdvice {
    @ModelAttribute
    public void initModel(HttpServletRequest request, Model model) {
        model.addAttribute("_csrf", request.getAttribute("_csrf"));
        AppConstants.preFillModel(model);
    }
}
