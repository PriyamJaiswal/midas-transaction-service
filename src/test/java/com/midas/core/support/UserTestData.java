package com.midas.core.support;

import com.midas.core.model.User;
import com.midas.core.repository.UserRepository;

import java.util.List;

public final class UserTestData {
    private UserTestData() {
    }

    public static void seedStandardUsers(UserRepository userRepository) {
        userRepository.deleteAll();
        List<User> users = List.of(
                new User(1L, "bernie", 1200.23),
                new User(2L, "grommit", 2215.37),
                new User(3L, "maria", 2774.14),
                new User(4L, "mario", 12.34),
                new User(5L, "waldorf", 444.55),
                new User(6L, "whosit", 888.90),
                new User(7L, "whatsit", 777.60),
                new User(8L, "howsit", 68.70),
                new User(9L, "wilbur", 3476.21),
                new User(10L, "antonio", 2121.54),
                new User(11L, "calypso", 779421.33)
        );
        userRepository.saveAll(users);
    }
}

