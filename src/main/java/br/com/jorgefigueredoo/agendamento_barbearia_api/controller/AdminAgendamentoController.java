package br.com.jorgefigueredoo.agendamento_barbearia_api.controller;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.AgendamentoResponse;
import br.com.jorgefigueredoo.agendamento_barbearia_api.service.AgendamentoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/agendamentos")
public class AdminAgendamentoController {

    private final AgendamentoService agendamentoService;

    public AdminAgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    // Ex: /admin/agendamentos?date=2026-01-21
    // ou /admin/agendamentos?date=2026-01-21&barberId=1
    @GetMapping
    public List<AgendamentoResponse> agendaDoDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long barberId
    ) {
        return agendamentoService.listarAgendaDoDia(date, barberId);
    }

    @PatchMapping("/{id}/confirmar")
    public AgendamentoResponse confirmar(@PathVariable Long id) {
        return agendamentoService.confirmar(id);
    }

    @PatchMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id) {
        return agendamentoService.cancelar(id);
    }
}
