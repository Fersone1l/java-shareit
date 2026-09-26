package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserMapper {
    public UserDto userToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User dtoToUser(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public List<UserDto> userListToDtoList(List<User> users) {
        ArrayList<UserDto> dtoUsers = new ArrayList<>();
        for (User user : users) {
            dtoUsers.add(userToDto(user));
        }

        return dtoUsers;
    }
}