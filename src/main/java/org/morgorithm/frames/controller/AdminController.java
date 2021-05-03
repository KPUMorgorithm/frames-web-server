package org.morgorithm.frames.controller;

import lombok.AllArgsConstructor;
import org.morgorithm.frames.dto.AdminDto;
import org.morgorithm.frames.service.AdminServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class AdminController {
    private AdminServiceImpl adminService;

    @GetMapping("/admin/signup")
    public String showSignup() {
        return "admin/signup";
    }

    @PostMapping("/admin/signup")
    public String execSignup(AdminDto adminDto) {
        if (adminService.loadUserByUsername(adminDto.getUsername()) != null
                || !adminDto.getPassword().equals(adminDto.getPasswordRpt())){
            return "redirect:/error";
        }
        adminService.joinUser(adminDto);
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/login")
    public String showLogin() {
        return "admin/login";
    }

    @GetMapping("/admin/login/result")
    public String showLoginResult() {
        return "redirect:/";
    }

    @GetMapping("/admin/logout/result")
    public String showLogout() {
        return "redirect:/";
    }
}
