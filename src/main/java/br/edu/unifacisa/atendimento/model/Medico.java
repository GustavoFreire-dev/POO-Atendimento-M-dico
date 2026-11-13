package br.edu.unifacisa.atendimento.model;
import java.time.LocalDateTime;
public class Medico extends Usuario {
 private String crm, especialidade;
 public Medico(int id,String nome,String email,String senha,LocalDateTime criadoEm,String crm,String especialidade){super(id,nome,email,senha,criadoEm);setCrm(crm);setEspecialidade(especialidade);}
 public String getCrm(){return crm;}
 public void setCrm(String crm){if(crm==null||crm.isBlank())throw new IllegalArgumentException("CRM obrigatório");this.crm=crm.trim();}
 public String getEspecialidade(){return especialidade;}
 public void setEspecialidade(String especialidade){if(especialidade==null||especialidade.isBlank())throw new IllegalArgumentException("Especialidade obrigatória");this.especialidade=especialidade.trim();}
 @Override public Perfil getPerfil(){return Perfil.MEDICO;}
 @Override public String toString(){return super.toString()+" CRM="+crm+", especialidade="+especialidade;}
}
