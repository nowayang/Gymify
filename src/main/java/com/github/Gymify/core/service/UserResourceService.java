package com.github.Gymify.core.service;

import com.github.Gymify.persistence.entity.UserResource;
import com.github.Gymify.persistence.repository.UserResourceRepository;
import com.github.Gymify.persistence.specification.UserResourceSpecificationFactory;
import com.github.Gymify.security.service.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Getter(value = AccessLevel.PROTECTED)
public abstract class UserResourceService<T extends UserResource> implements CrudService<T, Long> {
    private final UserResourceRepository<T> userResourceRepository;
    private final UserService userService;
    private final UserResourceSpecificationFactory<T> specificationFactory;


    @Override
    public Optional<T> find(Long id) {
        return this.userResourceRepository
            .findOne(this.specificationFactory.idEquals(id).and(this.currentUserSpecification()));
    }

    @Override
    public T add(T obj) {
        return this.userResourceRepository.save(obj);
    }

    @Override
    public T update(T obj) {
        return this.userResourceRepository.save(obj);
    }

    @Override
    public void delete(Long id) {
        this.userResourceRepository
            .findOne(this.specificationFactory.idEquals(id).and(this.currentUserSpecification()))
            .ifPresent(this.userResourceRepository::delete);
    }

    public List<T> getAllByCurrentUserId() {
        return this.userResourceRepository.findAllByUserId(this.userService.getCurrentUser().getId());
    }

    public Page<T> getAllByCurrentUserId(Pageable pageable) {
        return this.userResourceRepository.findAll(this.currentUserSpecification(), pageable);
    }

    public Page<T> getAllByCurrentUserId(Pageable pageable, Specification<T> specification) {
        return this.userResourceRepository.findAll(specification.and(currentUserSpecification()), pageable);
    }

    protected Specification<T> currentUserSpecification() {
        return this.specificationFactory.userIdEquals(userService.getCurrentUser().getId());
    }
}
