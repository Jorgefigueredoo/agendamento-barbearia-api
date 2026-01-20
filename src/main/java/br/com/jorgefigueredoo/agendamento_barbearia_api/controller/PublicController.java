package br.com.jorgefigueredoo.agendamento_barbearia_api.controller;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.AgendamentoResponse;
import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.CriarAgendamentoRequest;
import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.HorarioDisponivelResponse;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Barber;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.ServiceEntity;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.BarberRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.ServiceRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.service.AgendamentoService;
import br.com.jorgefigueredoo.agendamento_barbearia_api.service.DisponibilidadeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
public class PublicController {

    private final ServiceRepository serviceRepository;
    private final BarberRepository barberRepository;
    private final AgendamentoService agendamentoService;
    private final DisponibilidadeService disponibilidadeService;

    public PublicController(ServiceRepository serviceRepository,
            BarberRepository barberRepository,
            AgendamentoService agendamentoService,
            DisponibilidadeService disponibilidadeService) {
        this.serviceRepository = serviceRepository;
        this.barberRepository = barberRepository;
        this.agendamentoService = agendamentoService;
        this.disponibilidadeService = disponibilidadeService;
    }

    // --------- SERVIÇOS ----------
    @GetMapping("/services")
    public List<ServiceEntity> listarServicos() {
        return serviceRepository.findByActiveTrueOrderByNameAsc();
    }

    // --------- BARBEIROS ----------
    @GetMapping("/barbers")
    public List<Barber> listarBarbeiros() {
        return barberRepository.findByActiveTrueOrderByNameAsc();
    }

    // --------- DISPONIBILIDADE ----------
    // Ex: /availability?date=2026-01-20&serviceId=1&barberId=any
    @GetMapping("/availability")
    public List<HorarioDisponivelResponse> disponibilidade(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long serviceId,
            @RequestParam(defaultValue = "any") String barberId) {
        return disponibilidadeService.listar(date, serviceId, barberId);
    }

    // --------- AGENDAMENTO ----------
    @PostMapping("/agendamentos")
    public AgendamentoResponse criarAgendamento(@RequestBody @Valid CriarAgendamentoRequest req) {
        return agendamentoService.criar(req);
    }

    @GetMapping("/agendamentos/por-telefone")
    public List<AgendamentoResponse> listarPorTelefone(@RequestParam String phone) {
        return agendamentoService.listarPorTelefone(phone);
    }

    @PatchMapping("/agendamentos/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id) {
        return agendamentoService.cancelar(id);
    }

    @PatchMapping("/agendamentos/{id}/confirmar")
    public AgendamentoResponse confirmar(@PathVariable Long id) {
        return agendamentoService.confirmar(id);
    }

}
