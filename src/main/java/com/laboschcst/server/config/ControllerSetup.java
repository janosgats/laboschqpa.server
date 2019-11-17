package com.laboschcst.server.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerSetup {

    @ModelAttribute
    public void initModel(HttpServletRequest request, Model model) {
        model.addAttribute("_csrf", request.getAttribute("_csrf"));
        AppConstants.preFillModel(model);
    }



}
