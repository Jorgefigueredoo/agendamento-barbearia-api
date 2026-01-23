package br.com.jorgefigueredoo.agendamento_barbearia_api.controller;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.*;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.ServiceEntity;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.ServiceRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/servicos")
public class AdminServicoController {

    private final ServiceRepository serviceRepository;

    public AdminServicoController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<ServiceEntity> listarTodos() {
        return serviceRepository.findAll();
    }

    @PostMapping
    public ServiceEntity criar(@RequestBody @Valid CriarServicoRequest req) {
        ServiceEntity s = new ServiceEntity();
        s.setName(req.getName());
        s.setDurationMin(req.getDurationMin());
        s.setPriceCents(req.getPriceCents());
        s.setActive(req.getActive() != null ? req.getActive() : true);
        return serviceRepository.save(s);
    }

    @PutMapping("/{id}")
    public ServiceEntity atualizar(@PathVariable Long id, @RequestBody @Valid AtualizarServicoRequest req) {
        ServiceEntity s = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        s.setName(req.getName());
        s.setDurationMin(req.getDurationMin());
        s.setPriceCents(req.getPriceCents());
        if (req.getActive() != null) s.setActive(req.getActive());

        return serviceRepository.save(s);
    }

    @PatchMapping("/{id}/toggle-active")
    public ServiceEntity toggle(@PathVariable Long id) {
        ServiceEntity s = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        s.setActive(!Boolean.TRUE.equals(s.getActive()));
        return serviceRepository.save(s);
    }
}
