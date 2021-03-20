package com.ms.oracle.repository;

import com.ms.oracle.domain.Hello;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloRepository extends JpaRepository<Hello, String> {
}
