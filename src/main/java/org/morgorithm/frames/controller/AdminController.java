package org.morgorithm.frames.controller;

import lombok.AllArgsConstructor;
import org.morgorithm.frames.dto.AdminDto;
import org.morgorithm.frames.service.AdminServiceImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//admin
@Controller
@AllArgsConstructor
public class AdminController {
    private AdminServiceImpl adminService;

    @GetMapping("/admin/signup")
    public String showSignup(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("error", error);
        return "admin/signup";
    }

    @PostMapping("/admin/signup")
    public String execSignup(AdminDto adminDto) {
        try {
            adminService.loadUserByUsername(adminDto.getUsername());
        }
        catch (UsernameNotFoundException e) { // 중복없음
            if (!adminDto.getPassword().equals(adminDto.getPasswordRpt())) { // 비밀번호 확인 실패
                return "redirect:/admin/signup?error=비밀번호가%20일치하지%20않습니다.";
            }
            adminService.joinUser(adminDto);
            return "redirect:/admin/login";
        }
        return "redirect:/admin/signup?error=이미%20가입된%20아이디입니다.";
    }

    @RequestMapping("/admin/login")
    public String showLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("error", error);
        return "admin/login";
    }

    @RequestMapping("/admin/login/result")
    public String showLoginResult() {
        return "redirect:/";
    }

    @RequestMapping("/admin/login/denied")
    public String showLoginDenied(Model model) {
        model.addAttribute("alert", "로그인에 실패했습니다!");
        model.addAttribute("redirect", "/admin/login");
        return "redirectWithAlert";
    }

    @RequestMapping("/admin/logout/result")
    public String showLogout() {
        return "redirect:/";
    }
}
