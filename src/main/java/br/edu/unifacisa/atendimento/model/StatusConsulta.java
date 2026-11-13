package br.edu.unifacisa.atendimento.model;
public enum StatusConsulta {
 AGENDADA("agendada"), REALIZADA("realizada"), CANCELADA("cancelada");
 private final String valor;
 StatusConsulta(String valor) { this.valor=valor; }
 public String getValor() { return valor; }
 public static StatusConsulta fromDb(String valor) {
  for (StatusConsulta s : values()) if (s.valor.equalsIgnoreCase(valor)) return s;
  throw new IllegalArgumentException("Status inválido: " + valor);
 }
}
