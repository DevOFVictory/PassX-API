package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserAccountController {

    private UserAccountRepository userAccountRepository;

    public UserAccountController(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("")
    public List<UserAccount> index() {
        return userAccountRepository.findAll();
    }

}
