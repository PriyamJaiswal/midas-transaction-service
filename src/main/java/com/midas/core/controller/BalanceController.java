package com.midas.core.controller;

import com.midas.core.dto.Balance;
import com.midas.core.model.User;
import com.midas.core.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(path = "/balance")
    public Balance getBalance(@RequestParam(name = "userId") Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new Balance(0.0);
        }
        return new Balance(user.getBalance());
    }
}

