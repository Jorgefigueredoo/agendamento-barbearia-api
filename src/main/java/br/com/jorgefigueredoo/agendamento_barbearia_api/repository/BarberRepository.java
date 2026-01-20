package br.com.jorgefigueredoo.agendamento_barbearia_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Barber;

import java.util.List;

public interface BarberRepository extends JpaRepository<Barber, Long> {
    List<Barber> findByActiveTrueOrderByNameAsc();
}
