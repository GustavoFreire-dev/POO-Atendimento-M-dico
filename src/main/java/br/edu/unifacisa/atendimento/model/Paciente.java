package br.edu.unifacisa.atendimento.model;
import java.time.LocalDateTime;
public class Paciente extends Usuario {
 private String cpf, telefone;
 public Paciente(int id,String nome,String email,String senha,LocalDateTime criadoEm,String cpf,String telefone){super(id,nome,email,senha,criadoEm);setCpf(cpf);setTelefone(telefone);}
 public String getCpf(){return cpf;}
 public void setCpf(String cpf){
  if(cpf==null||!cpf.matches("\\d{11}"))throw new IllegalArgumentException("CPF deve conter 11 dígitos");
  if(cpf.chars().distinct().count()==1)throw new IllegalArgumentException("CPF inválido");
  for(int dig=9;dig<=10;dig++){int soma=0;for(int i=0;i<dig;i++)soma+=(cpf.charAt(i)-'0')*(dig+1-i);int ver=(soma*10)%11;if(ver==10)ver=0;if(ver!=cpf.charAt(dig)-'0')throw new IllegalArgumentException("CPF inválido");}
  this.cpf=cpf;
 }
 public String getTelefone(){return telefone;}
 public void setTelefone(String telefone){this.telefone=telefone==null?null:telefone.trim();}
 @Override public Perfil getPerfil(){return Perfil.PACIENTE;}
 @Override public String toString(){return super.toString()+" telefone="+telefone;}
}
