package com.gcpTest.gcp.database.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gcpTest.gcp.database.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
	
	
	
}
