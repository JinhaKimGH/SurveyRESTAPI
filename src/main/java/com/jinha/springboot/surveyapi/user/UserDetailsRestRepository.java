package com.jinha.springboot.surveyapi.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserDetailsRestRepository extends PagingAndSortingRepository<UserDetails, Long> {

    List<UserDetails> findByRole(String role);
}
