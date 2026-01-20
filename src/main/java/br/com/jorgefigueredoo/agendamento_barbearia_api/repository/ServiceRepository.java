package br.com.jorgefigueredoo.agendamento_barbearia_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.jorgefigueredoo.agendamento_barbearia_api.model.ServiceEntity;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByActiveTrueOrderByNameAsc();
}
