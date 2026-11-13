package br.edu.unifacisa.atendimento.model;
import java.time.LocalDateTime;
public class Consulta {
 private int id;
 private Paciente paciente;
 private Medico medico;
 private LocalDateTime dataHora;
 private StatusConsulta status;
 private String observacoes;
 public Consulta(int id,Paciente paciente,Medico medico,LocalDateTime dataHora,StatusConsulta status,String observacoes){
  this.id=id;setPaciente(paciente);setMedico(medico);setDataHora(dataHora);setStatus(status);setObservacoes(observacoes);
 }
 public int getId(){return id;}
 public void setId(int id){if(id<0)throw new IllegalArgumentException("ID inválido");this.id=id;}
 public Paciente getPaciente(){return paciente;}
 public void setPaciente(Paciente paciente){if(paciente==null)throw new IllegalArgumentException("Paciente obrigatório");this.paciente=paciente;}
 public Medico getMedico(){return medico;}
 public void setMedico(Medico medico){if(medico==null)throw new IllegalArgumentException("Médico obrigatório");this.medico=medico;}
 public LocalDateTime getDataHora(){return dataHora;}
 public void setDataHora(LocalDateTime dataHora){if(dataHora==null)throw new IllegalArgumentException("Data e hora obrigatórias");this.dataHora=dataHora;}
 public StatusConsulta getStatus(){return status;}
 public void setStatus(StatusConsulta status){if(status==null)throw new IllegalArgumentException("Status obrigatório");this.status=status;}
 public String getObservacoes(){return observacoes;}
 public void setObservacoes(String observacoes){this.observacoes=observacoes;}
 @Override public String toString(){return "Consulta{id="+id+", paciente="+paciente.getNome()+", medico="+medico.getNome()+", dataHora="+dataHora+", status="+status.getValor()+", observacoes="+observacoes+"}";}
}
