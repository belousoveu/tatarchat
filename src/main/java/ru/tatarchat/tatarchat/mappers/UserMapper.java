package ru.tatarchat.tatarchat.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tatarchat.tatarchat.dto.admin.UserInfoResponse;
import ru.tatarchat.tatarchat.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "enabled", target = "enabled")
    UserInfoResponse toUserResponse(User user);
}