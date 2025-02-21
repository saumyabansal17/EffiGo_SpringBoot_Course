//package com.effigo.ems.mapper;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//import com.effigo.ems.dto.UserDetailsDto;
//import com.effigo.ems.model.Users;
//
//@Mapper(componentModel = "spring")
//public interface UserMapper {
//
////    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
//
////    @Mapping(source = "id", target = "id")
////    @Mapping(source = "phone_no", target = "phone")
////    @Mapping(source = "emailId", target = "emailId")
////    Users dtoToEntity(UserDetailsDto userdto);
//
//    // Optional: reverse mapping from Users to UserDetailsDto
//    @Mapping(source = "id", target = "id")
//    @Mapping(source = "phone_no", target = "phone")
//    @Mapping(source = "emailId", target = "emailId")
//    UserDetailsDto entityToDto(Users user);
//}
