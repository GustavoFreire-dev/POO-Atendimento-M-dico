package br.edu.unifacisa.atendimento.model;
public enum Perfil {
 MEDICO("medico"), SECRETARIA("secretaria"), PACIENTE("paciente");
 private final String valor;
 Perfil(String valor) { this.valor=valor; }
 public String getValor() { return valor; }
 public static Perfil fromDb(String valor) {
  for (Perfil p : values()) if (p.valor.equalsIgnoreCase(valor)) return p;
  throw new IllegalArgumentException("Perfil inválido: " + valor);
 }
}
