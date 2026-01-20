package br.com.jorgefigueredoo.agendamento_barbearia_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CriarAgendamentoRequest {

    @NotNull
    private Long servicoId;

    @NotNull
    private Long barbeiroId;

    @NotNull
    private LocalDateTime startTime;

    @NotBlank
    @Size(max = 160)
    private String clientName;

    @NotBlank
    @Size(max = 30)
    private String clientPhone;

    @Size(max = 500)
    private String notes;

    public Long getServicoId() { return servicoId; }
    public void setServicoId(Long servicoId) { this.servicoId = servicoId; }

    public Long getBarbeiroId() { return barbeiroId; }
    public void setBarbeiroId(Long barbeiroId) { this.barbeiroId = barbeiroId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
