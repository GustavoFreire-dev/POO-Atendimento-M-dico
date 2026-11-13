package br.edu.unifacisa.atendimento.db;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public final class ConexaoBD {
 private ConexaoBD(){}
 public static Connection getConnection() throws SQLException {
  Properties props=new Properties();
  try(InputStream stream=ConexaoBD.class.getClassLoader().getResourceAsStream("db.properties")){
   if(stream==null)throw new SQLException("Arquivo db.properties não encontrado. Copie db.properties.example.");
   props.load(stream);
  }catch(IOException e){throw new SQLException("Erro ao ler configurações do banco",e);}
  return DriverManager.getConnection(props.getProperty("db.url"),props.getProperty("db.usuario"),props.getProperty("db.senha"));
 }
}
