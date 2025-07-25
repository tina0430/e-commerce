package kr.hhplus.be.ecommerce.user.presentation;

import kr.hhplus.be.ecommerce.user.domain.model.PointTransaction;
import kr.hhplus.be.ecommerce.user.domain.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    PointDto.Response toPointResponseDto(User source);

    PointDto.HistoryResponse toHistoryResponseDto(PointTransaction source);

    List<PointDto.HistoryResponse> toHistoryResponseDtoList(List<PointTransaction> source);

}