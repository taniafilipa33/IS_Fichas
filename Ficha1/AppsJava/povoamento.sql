use clinica;

select * from Exame;
INSERT into Pedido (idPedido, mensagem, data, estado, relatorio, codigoExame, descExame ) values(1,"MSH|^~\&|AIDA|AIDA|PACS|PACS|201504051157||ORM^O01|A2015040511    5751000002533|P|2.5|||AL|    PID|||50626||CONCEICAO    SERRANO    SEQUEIRA^MARIA^^||19411012|F||||||||||28006303|    PV1||I|INT||||||||||||||||15002727|        ORC|NW|4727374|4727374||||||20150405111053|    OBR|01|4727374|4727374|M10405^TORAX,    UMA    INCIDENCIA|||||||||||^^^|||CR|RXE||||||30||^^^20150405115723^^    0||||||",'2021-03-04 16:35:00','Pendente 2021-03-04 16:35:00', null, "M123","Exame ao torax");
select * from Pedido;

INSERT INTO Paciente (nome, dataNascimento,morada, numProcesso, telefone) Values ("António Albertim Silva", '2020-02-01', "Rua Fernando Pessoa", 456123789, 1234567);

insert into Exame (codigo_ato,ato,id_externo,medico,Paciente_idPaciente,Pedido_idPedido) values ("M123","Exame ao Torax",1,"Joaquim Alberto",1,3);


ALTER TABLE ProfissionalSaude
MODIFY COLUMN idProfissionalSaude int auto_increment;

ALTER TABLE Equipamento
MODIFY COLUMN idEquipamento int auto_increment;

ALTER TABLE Paciente
MODIFY COLUMN idpaciente int auto_increment;

ALTER TABLE Exame
MODIFY COLUMN idExame int auto_increment;

SET FOREIGN_KEY_CHECKS = 0;
SET FOREIGN_KEY_CHECKS = 1;

select * from Paciente; 
select * from Exame;

INSERT INTO Paciente (nome, dataNascimento, numProcesso, telefone, morada) Values ("Joseph Joestar", '2020-02-01', 123, 1234567, "America");
INSERT INTO Exame (estadoE, descricao, nome, ProfissionalSaude_idProfissionalSaude, Equipamento_idEquipamento, Paciente_idPaciente, Marcacao_idMarcacao, relatorio) 
VALUES ("Aceite", "Raio-X ao joelho", "Raio-X", 1, 1, 1, 1, "N\A");

SELECT max(idConsulta) from Consulta;
select * from Pedido;

drop table Pedido;


use hospital;
select * from Paciente;
select * from Pedido where idPedido = 1;
select * from RegistoHistorico;
INSERT INTO Consulta (descricao, idPaciente) Values ("Oftalmologia", 1);
INSERT INTO Paciente (nome, dataNascimento, numProcesso, morada, telefone) Values ("Joseph Joestar", '2020-02-01', 123, "America", 1234567);
