package kr.hhplus.be.ecommerce.user.domain;

import kr.hhplus.be.ecommerce.user.domain.model.PointTransaction;
import kr.hhplus.be.ecommerce.user.domain.model.PointTransactionEntity;
import kr.hhplus.be.ecommerce.user.domain.model.User;
import kr.hhplus.be.ecommerce.user.domain.model.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User toUser(UserEntity userEntity);

    PointTransaction toPointTransaction(PointTransactionEntity source);

    List<PointTransaction> toPointTransactionList(List<PointTransactionEntity> source);

    /**
     * 도메인 객체의 변경된 상태를 기존 엔티티에 적용합니다.
     * <p>
     * 이 메서드는 새로운 엔티티를 생성하지 않으며,
     * 전달받은 엔티티 객체의 상태만 변경합니다.
     *
     * @param domain 도메인 객체
     * @param entity 기존 엔티티 (상태가 변경됨)
     */
    default void applyToEntity(User domain, UserEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        
        entity.setBalance(domain.getBalance());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

} 