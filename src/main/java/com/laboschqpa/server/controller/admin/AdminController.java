package com.laboschqpa.server.controller.admin;

import com.laboschqpa.server.config.helper.AppConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @GetMapping(AppConstants.adminBaseUrl + "**")
    public String getAdminDeeperTest() {
        return "admin test page: any deep";
    }
}
