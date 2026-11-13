package br.edu.unifacisa.atendimento.model;
import java.time.LocalDateTime;
import java.util.Locale;
public abstract class Usuario {
 private int id;
 private String nome, email, senha;
 private LocalDateTime criadoEm;
 protected Usuario(int id, String nome, String email, String senha, LocalDateTime criadoEm) {
  this.id=id; setNome(nome); setEmail(email); setSenha(senha); this.criadoEm=criadoEm;
 }
 public int getId(){return id;}
 public void setId(int id){if(id<0)throw new IllegalArgumentException("ID inválido");this.id=id;}
 public String getNome(){return nome;}
 public void setNome(String nome){if(nome==null||nome.isBlank())throw new IllegalArgumentException("Nome obrigatório");this.nome=nome.trim();}
 public String getEmail(){return email;}
 public void setEmail(String email){if(email==null||!email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))throw new IllegalArgumentException("E-mail inválido");this.email=email.trim().toLowerCase(Locale.ROOT);}
 public String getSenha(){return senha;}
 public void setSenha(String senha){if(senha==null||senha.length()<6)throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");this.senha=senha;}
 public LocalDateTime getCriadoEm(){return criadoEm;}
 public void setCriadoEm(LocalDateTime criadoEm){this.criadoEm=criadoEm;}
 public abstract Perfil getPerfil();
 @Override public String toString(){return getClass().getSimpleName()+"{id="+id+", nome='"+nome+"', email='"+email+"', perfil="+getPerfil().getValor()+"}";}
}
