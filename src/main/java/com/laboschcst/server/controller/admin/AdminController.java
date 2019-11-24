package com.laboschcst.server.controller.admin;

import com.laboschcst.server.config.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @GetMapping(AppConstants.adminBaseUrl + "**")
    private String getAdminDeeperTest() {
        return "admin test page: any deep";
    }
}
