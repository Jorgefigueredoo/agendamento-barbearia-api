package br.com.jorgefigueredoo.agendamento_barbearia_api.dto;

import java.time.LocalDateTime;

public class HorarioDisponivelResponse {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long barbeiroId;

    public HorarioDisponivelResponse() {}

    public HorarioDisponivelResponse(LocalDateTime startTime, LocalDateTime endTime, Long barbeiroId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.barbeiroId = barbeiroId;
    }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Long getBarbeiroId() { return barbeiroId; }
    public void setBarbeiroId(Long barbeiroId) { this.barbeiroId = barbeiroId; }
}
