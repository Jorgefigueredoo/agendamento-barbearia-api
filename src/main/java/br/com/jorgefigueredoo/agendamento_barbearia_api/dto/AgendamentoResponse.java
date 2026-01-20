package br.com.jorgefigueredoo.agendamento_barbearia_api.dto;

import br.com.jorgefigueredoo.agendamento_barbearia_api.enums.StatusAgendamento;

import java.time.LocalDateTime;

public class AgendamentoResponse {

    private Long id;
    private StatusAgendamento status;

    private String clientName;
    private String clientPhone;
    private String notes;

    private Long servicoId;
    private String servicoNome;
    private Integer servicoDuracaoMin;

    private Long barbeiroId;
    private String barbeiroNome;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getServicoId() { return servicoId; }
    public void setServicoId(Long servicoId) { this.servicoId = servicoId; }

    public String getServicoNome() { return servicoNome; }
    public void setServicoNome(String servicoNome) { this.servicoNome = servicoNome; }

    public Integer getServicoDuracaoMin() { return servicoDuracaoMin; }
    public void setServicoDuracaoMin(Integer servicoDuracaoMin) { this.servicoDuracaoMin = servicoDuracaoMin; }

    public Long getBarbeiroId() { return barbeiroId; }
    public void setBarbeiroId(Long barbeiroId) { this.barbeiroId = barbeiroId; }

    public String getBarbeiroNome() { return barbeiroNome; }
    public void setBarbeiroNome(String barbeiroNome) { this.barbeiroNome = barbeiroNome; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
