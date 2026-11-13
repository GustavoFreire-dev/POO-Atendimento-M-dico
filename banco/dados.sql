USE atendimento_medico;
-- Dados fictícios. Nesta primeira entrega o enunciado permite senhas demonstrativas em texto.
INSERT INTO usuarios (id,nome,email,senha,perfil,crm,especialidade,cpf,telefone) VALUES
(1,'Ana Secretaria','ana.secretaria@exemplo.test','senha123','secretaria',NULL,NULL,NULL,NULL),
(2,'Bruno Medico','bruno.medico@exemplo.test','senha123','medico','CRM-PB-1001','Clínica Geral',NULL,NULL),
(3,'Carla Medica','carla.medica@exemplo.test','senha123','medico','CRM-PB-1002','Pediatria',NULL,NULL),
(4,'Daniel Paciente','daniel.paciente@exemplo.test','senha123','paciente',NULL,NULL,'52998224725','83999990001'),
(5,'Elisa Paciente','elisa.paciente@exemplo.test','senha123','paciente',NULL,NULL,'11144477735','83999990002'),
(6,'Fabio Paciente','fabio.paciente@exemplo.test','senha123','paciente',NULL,NULL,'12345678909','83999990003')
ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO consultas (id,paciente_id,medico_id,data_hora,status,observacoes) VALUES
(1,4,2,'2027-01-10 09:00:00','agendada',NULL),
(2,5,2,'2027-01-10 10:00:00','agendada',NULL),
(3,6,3,'2027-01-11 14:00:00','agendada',NULL),
(4,4,3,'2027-01-12 15:00:00','agendada',NULL)
ON DUPLICATE KEY UPDATE paciente_id=VALUES(paciente_id);
