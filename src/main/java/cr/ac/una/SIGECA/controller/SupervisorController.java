/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.logic.LogicUser;
import cr.ac.una.SIGECA.domain.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author crist
 */
@Controller
@RequestMapping("/supervisors")
public class SupervisorController {
    
    @Autowired
    private LogicUser logUser;

    @GetMapping("/supervisor/list")
    public String listUser(Model model) {
        List<User> users = logUser.getUser();
        model.addAttribute("users", users);
        return "user/list_user";
    }

    @GetMapping("/supervisor/filter")
    public String filterUser(@RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String genderFilter,
            Model model) {
        List<User> users = logUser.filterUser(roleFilter, genderFilter);
        System.out.println(users.getFirst().toString());
        model.addAttribute("users", users);
        return "user/list_user";
    }
}
