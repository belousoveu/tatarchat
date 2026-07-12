package ru.tatarchat.tatarchat.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tatarchat.tatarchat.dto.admin.InviteCodeResponse;
import ru.tatarchat.tatarchat.entities.InviteCode;

@Mapper(componentModel = "spring")
public interface InviteCodeMapper {

    @Mapping(source = "createdByUserId", target = "createdByUserId")
    @Mapping(source = "createdAt", target = "createdAt")
    InviteCodeResponse toInviteCodeResponse(InviteCode inviteCode);
}