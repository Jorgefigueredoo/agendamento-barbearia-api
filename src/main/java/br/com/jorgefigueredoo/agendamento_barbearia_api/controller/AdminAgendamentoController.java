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

    @PatchMapping("/{id}/confirmar")
    public AgendamentoResponse confirmar(@PathVariable Long id) {
        return agendamentoService.confirmar(id);
    }

    @PatchMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id) {
        return agendamentoService.cancelar(id);
    }

    @GetMapping
    public List<AgendamentoResponse> listar(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long barberId) {
        // prioridade: se vier range, usa range
        if (range != null && !range.isBlank()) {
            return agendamentoService.listarPorRange(range, barberId);
        }

        // se não vier range, mas vier date, usa o dia específico
        if (date != null) {
            return agendamentoService.listarAgendaDoDia(date, barberId);
        }

        // default: hoje
        return agendamentoService.listarPorRange("today", barberId);
    }

}
