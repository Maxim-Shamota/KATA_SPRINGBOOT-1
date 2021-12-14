package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "admin")
    public String showAdminPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    @GetMapping(value = "admin/add")
    public String addNewUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "add";
    }

    @PostMapping(value = "admin/add")
    public String addNewUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "checkBoxRoles") String[] checkBoxRoles) {
        Set<Role> roleSet = new HashSet<>();
        for (String role : checkBoxRoles) {
            roleSet.add(roleService.getRole(role));
        }
        user.setRoles(roleSet);
//        setUserRole(user);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "admin/update/{id}")
    public String updateUser(Model model, @PathVariable("id") long id) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        return "update";
    }

    @PostMapping(value = "admin/update")
    public String updateUser(@ModelAttribute("user") User user) {
        setUserRole(user);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @PostMapping(value = "admin/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    private void setUserRole(User user) {
        user.setRoles(user.getRoles().stream()
                .map(r -> roleService.getRole(r.getUserRole()))
                .collect(Collectors.toSet()));
    }
}
