package ru.bmstu.iu7.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.iu7.impl.model.Information;

@Repository
public interface SpringInformationRepository extends JpaRepository<Information, Long>{
}
