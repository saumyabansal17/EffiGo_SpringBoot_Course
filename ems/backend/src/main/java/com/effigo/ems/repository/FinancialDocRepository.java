package com.effigo.ems.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.effigo.ems.model.FinancialDocument;
import com.effigo.ems.model.Users;

@Repository
public interface FinancialDocRepository extends JpaRepository<FinancialDocument, UUID> {

	List<FinancialDocument> findByUser(Users user);
	
	@Query(value = "SELECT doc.doc_id FROM financial_document doc where doc.user_id=:id", nativeQuery = true)
	UUID findDocIDById(UUID id);
	
	@Query(value="SELECT * from financial_document d WHERE d.user_id = :userId",nativeQuery=true)
	List<FinancialDocument> findDocumentsByUserId(@Param("userId") UUID userId);

}
